package com.siliddor.developper.lucenepourandroid;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Study {
    private static final String TITLE  = "title";
    private static final String AUTHOR  = "authors";
    private static final String PUBLISH_YEAR  = "year";
    private static final String VOLUME  = "volume";
    private static final String PUBLISHER  = "publisher";
    private static final String PAGES  = "pages";
    private static final String TYPE  = "type";
    private static final String ACCESS  = "access";
    private static final String ARTICLE_LINK  = "url";
    private static final String SOURCE  = "ee";
    private static final String INFO_UNAVAILABLE = "(Information non disponible)";


    public static void main(String args[]) throws Exception {
        if (args.length < 3) {
            showHelpAndExit();
            return;
        }

        if (args[0].equalsIgnoreCase("index")) {
            index(args[1], args[2]);
        } else if (args[0].equalsIgnoreCase("search")) {
            search(args[1], args[2]);
        } else if (args[0].equalsIgnoreCase("suggest")) {
            suggest(args[1], args[2]);
        } else if (args[0].equalsIgnoreCase("add")) {

            if (args.length < 8) {
                showHelpAndExit();
            }
            add(args[0], args[1], args[2], args[3], args[4], args[5], args[6],
                    args[7], args[8], args[9], args[10], args[11], args[12], args[13]);

        } else if (args[0].equalsIgnoreCase("delete")) {
            delete(args[1], args[2]);
        } else {
            showHelpAndExit();
        }
    }

    private static void showHelpAndExit() {
        System.err.println("Usage: Study [index|search|suggest] arguments...");
        System.err.println("    index <source JSON> <index path>");
        System.err.println("    search <index path> <query>");
        System.err.println("    suggest <index path> <keyword(s)>");
        System.err.println("    add <index path> <title> <year> <rating> <positive> <review> <source>");
        System.err.println("    delete <index path> <query>");
        System.exit(1);
    }



    private static void index(String sourcePath, String indexPath) {
        File dataFile = new File(sourcePath);
        if (!dataFile.exists()) {
            System.err.println("JSON source not found: " + sourcePath);
            System.exit(1);
        }

        if (dataFile.length() > Integer.MAX_VALUE) {
            System.exit(1);
        }

        try (FileInputStream stream = new FileInputStream(sourcePath)) {
            ImporterDonneesJSON(stream, indexPath, true);
        } catch (Exception e) {
            // Should not happen
            e.printStackTrace();
            System.exit(1);
        }
    }



     public static int ImporterDonneesJSON(InputStream stream, String indexPath, boolean withSuggestion) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final int bufSize = 4096;
        byte[] buf = new byte[bufSize];
        int read;
        while ((read = stream.read(buf)) > 0) {
            baos.write(buf, 0, read);
        }
        String dataStr = new String(baos.toByteArray(), "UTF-8");

        List<Documents> docs = new ArrayList<>();


         JSONObject docParent = new JSONObject(dataStr);
         JSONObject docEnfant = docParent.getJSONObject("result");
         JSONObject docResultats = docEnfant.getJSONObject("hits");
         JSONArray tabelauResultats = docResultats.getJSONArray("hit");

         for (int i = 0; i < tabelauResultats.length(); i++) {

             JSONObject tempsObject = docEnfant.getJSONObject("time");

             String nombreTotalResultat = docResultats.getString("@total");
             String nombreAffiche = docResultats.getString("@sent");

             JSONObject entrees = tabelauResultats.getJSONObject(i);
             JSONObject infoObject = entrees.getJSONObject("info");


             String titre = "";
             if (infoObject.has(TITLE)) {
                 titre = infoObject.getString(TITLE);
             }else{
                 titre = INFO_UNAVAILABLE;
             }


             String auteur = "";
             if (infoObject.has(AUTHOR)) {
                 auteur  = RecupererAuteurs(infoObject);
             }else{
                 auteur = INFO_UNAVAILABLE;
             }


             String anneePub = "";
             if (infoObject.has(PUBLISH_YEAR)) {
                 anneePub = infoObject.getString(PUBLISH_YEAR);
             }else{
                 anneePub = INFO_UNAVAILABLE;
             }


             String publicateur = "";
             if (infoObject.has(PUBLISHER)) {
                 publicateur = infoObject.getString(PUBLISHER);
             }else{
                 publicateur = INFO_UNAVAILABLE;
             }


             String type = "";
             if (infoObject.has(TYPE)) {
                 type = infoObject.getString(TYPE);
             }else{
                 type = INFO_UNAVAILABLE;
             }

             String volume = "";
             if (infoObject.has(VOLUME)) {
                 volume = infoObject.getString(VOLUME);
             }else{
                 volume = INFO_UNAVAILABLE;
             }

             String pages = "";
             if (infoObject.has(PAGES)) {
                 pages = infoObject.getString(PAGES);
             }else{
                 pages = INFO_UNAVAILABLE;
             }


             String acces = "";
             if (infoObject.has(ACCESS)){
                 acces = infoObject.getString(ACCESS);
             }else {
                 acces = INFO_UNAVAILABLE;
             }


             String source = "";
             if (infoObject.has(SOURCE)) {
                 source = infoObject.getString(SOURCE);
             }else{
                 source = INFO_UNAVAILABLE;
             }


             String lienVersPub = "";
             if (infoObject.has(ARTICLE_LINK)) {
                 lienVersPub = infoObject.getString(ARTICLE_LINK);
             }else{
                 lienVersPub = INFO_UNAVAILABLE;
             }

             String tempsRecherche = tempsObject.getString("text") +
                     " " +
                     tempsObject.getString("@unit");

             Documents doc = new Documents(tempsRecherche, nombreTotalResultat, nombreAffiche, titre,
                     auteur, anneePub, publicateur, type, volume, pages, acces, source, lienVersPub);
             docs.add(doc);
         }

        Indexer indexer = new Indexer(indexPath, false);
        indexer.AjouterDocumentsJSON(docs);
        indexer.close();

        if (withSuggestion) {
            Suggester.rebuild(indexPath);
        }

        return docs.size();
    }




    private static String RecupererAuteurs(JSONObject object) throws JSONException {

        JSONObject auteursObject = object.getJSONObject(AUTHOR);


        if (auteursObject.has("author")) {

            StringBuilder auteurBuilder = new StringBuilder();

            Object auteurObject = auteursObject.get("author");


            if (auteurObject instanceof JSONArray) {
                JSONArray tableauAuteurs = (JSONArray) auteurObject;

                for (int j = 0; j < tableauAuteurs.length(); j++) {

                    JSONObject objetRecupere = tableauAuteurs.getJSONObject(j);
                    auteurBuilder.append(objetRecupere.getString("text"));

                    if (j < tableauAuteurs.length() - 1) {
                        auteurBuilder.append(", ");
                    }else{
                        auteurBuilder.append(".");
                    }


                }
            }else if (auteurObject instanceof JSONObject) {
                auteurBuilder.append(((JSONObject) auteurObject).getString("text"));
            }

            return auteurBuilder.toString();
        }else{
            return INFO_UNAVAILABLE;
        }

    }



    private static void search(String indexPath, String query) throws Exception {
        Searcher searcher = new Searcher(indexPath);
        SearchResult result = searcher.search(query, null, 10);


        for (Documents doc : result.documents) {
            System.out.println("title   : " + result.getHighlightedTitle(doc));
            System.out.println("year    : " + doc.anneeDePub);
            System.out.println("rating  : " + doc.anneeDePub);
            System.out.println("positive: " + doc.auteur);
            System.out.println("review  : " + result.getHighlightedReview(doc));
            System.out.println();
        }

        searcher.close();
    }




    private static void delete(String indexPath, String query) throws Exception {
        Indexer indexer = new Indexer(indexPath, true);
        indexer.deleteDocumentsByQuery(query);
        indexer.close();
        Suggester.rebuild(indexPath);
    }




    private static void suggest(String indexPath, String query) throws Exception {
        Suggester suggester = new Suggester(indexPath);
        List<String> suggestions = suggester.suggest(query);
        for (String text : suggestions) {
            System.out.println("Suggestion: " + text);
        }
        suggester.close();
    }



    private static void add(String indexPath, String tempsDeRecherche, String nombreTotalResultat, String nombreAffiche,
                    String titre, String auteur, String anneePub, String publicateur, String type,
                    String volume, String pages, String acces, String source, String lienVersPub) throws Exception {

        Documents doc = new Documents(tempsDeRecherche, nombreTotalResultat, nombreAffiche, titre,
        auteur, anneePub, publicateur, type, volume, acces, pages, source, lienVersPub);

        Indexer indexer = new Indexer(indexPath, true);
        indexer.AjouterDocumentsJSON(Collections.singletonList(doc));
        indexer.close();

        Suggester.rebuild(indexPath);
    }


}

package com.nftec.developper.detplagi;

import com.siliddor.developper.lucenepourandroid.Documents;
import com.siliddor.developper.lucenepourandroid.SearchResult;

import java.util.ArrayList;
import java.util.List;

public class Resultats {
    public final SearchResult searchResult;
    public final Documents doc;
    public final String tempsDeRecherche;
    public final String nombreTotalResultat;
    public final String nombreAffiche;
    public final String titre;
    public final String auteur;
    public final String anneeDePub;
    public final String publicateur;
    public final String typeDarticle;
    public final String volume;
    public final String pages;
    public final String acces;
    public final String source;
    public final String lienVersArticle;

    Resultats(SearchResult searchResult, Documents doc) {
        this.searchResult = searchResult;
        this.doc = doc;
        tempsDeRecherche = doc.tempsDeRecherche;
        nombreTotalResultat = doc.nombreTotalResultat;
        nombreAffiche = doc.nombreAffiche;
        titre = searchResult.getHighlightedTitle(doc);
        auteur = String.format(doc.auteur);
        anneeDePub = doc.anneeDePub;
        publicateur = doc.publicateur;
        typeDarticle = doc.typeDarticle;
        volume = doc.volume;
        pages = doc.pages;
        acces = doc.acces;
        source = String.format("<a href=\"%s\">Source</a>", doc.source);
        lienVersArticle = String.format("<a href=\"%s\">Voir l'article</a>", doc.lienVersArticle);
    }


    public static List<Resultats> ResultatDesRecherches(SearchResult searchResult) {
        final ArrayList<Resultats> resultats = new ArrayList<>();
        for (Documents doc : searchResult.documents) {
            resultats.add(new Resultats(searchResult, doc));
        }
        return resultats;
    }
}

package com.siliddor.developper.lucenepourandroid;


import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.SortedDocValuesField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

import java.io.IOException;
import org.lukhnos.portmobile.file.Path;
import org.lukhnos.portmobile.file.Paths;
import java.util.List;

/**
 * Indexer for the document index. This class also defines the "schema" of the document index: It
 * defines field names and decides the Lucene field type to use when indexing. It also provides
 * an internal converter from Lucene documents to our Document objects.
 */
public class Indexer implements AutoCloseable {
    static final String TITLE_FIELD_NAME = "title";
    static final String YEAR_FIELD_NAME = "year";
    static final String RATING_FIELD_NAME = "rating";
    static final String REVIEW_FIELD_NAME = "review";

    public static final String TEMPS_DE_RECHERCHE = "text";
    public static final String NOMBRE_TOTAL_RESULTATS = "@total";
    public static final String NOMBRE_AFFICHE = "@sent";
    public static final String TITRE  = "title";
    public static final String AUTEUR  = "authors";
    public static final String ANNEE_PUB  = "year";
    public static final String VOLUME  = "volume";
    public static final String PUBLICATEUR  = "publisher";
    public static final String PAGES  = "pages";
    public static final String TYPE  = "type";
    public static final String ACCES  = "access";
    public static final String LIEN_VERS_ARTICLE  = "ee";
    public static final String SOURCE  = "url";

    static final String INDEX_NAME = "main";

    final IndexWriter indexWriter;

    /**
     * Create a new document index.
     *
     * @param indexRoot The parent directory inside which the index lives.
     * @throws IOException
     */
    public Indexer(String indexRoot) throws IOException {
        this(indexRoot, false);
    }

    /**
     * Create or open a document index
     *
     * @param indexRoot The parent directory inside which the index lives.
     * @param appendIfExists If true, the index will be opened for appending new documents.
     * @throws IOException
     */
    public Indexer(String indexRoot, boolean appendIfExists) throws IOException {
        Path indexRootPath = Paths.get(indexRoot);
        Analyzer analyzer = getAnalyzer();

        Directory mainIndexDir = FSDirectory.open(getMainIndexPath(indexRootPath));
        IndexWriterConfig mainIndexWriterConfig = new IndexWriterConfig(analyzer);

        if (appendIfExists) {
            mainIndexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        } else {
            mainIndexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        }

        indexWriter = new IndexWriter(mainIndexDir, mainIndexWriterConfig);
    }

    public static Analyzer getAnalyzer() {
        return new EnglishAnalyzer();
    }

    public static Query parseQuery(Analyzer analyzer, String queryStr) throws ParseException {
        String[] fields = { Indexer.TITLE_FIELD_NAME, Indexer.REVIEW_FIELD_NAME };
        QueryParser parser = new MultiFieldQueryParser(fields, analyzer);
        return parser.parse(queryStr);
    }

    static Integer getInteger(org.apache.lucene.document.Document luceneDoc, String fieldName) {
        IndexableField field = luceneDoc.getField(fieldName);
        if (field != null) {
            Number number = field.numericValue();
            if (number != null) {
                return number.intValue();
            }
        }
        return null;
    }


    static Documents fromLuceneDocument(org.apache.lucene.document.Document luceneDoc) {

        String tempsDeRecherche = luceneDoc.get(TEMPS_DE_RECHERCHE);
        String nombreTotalResultat = luceneDoc.get(NOMBRE_TOTAL_RESULTATS);
        String nombreAffiche = luceneDoc.get(NOMBRE_AFFICHE);
        String titre = luceneDoc.get(TITRE);
        String auteur = luceneDoc.get(AUTEUR);
        String anneeDePub = luceneDoc.get(ANNEE_PUB);
        String publicateur = luceneDoc.get(PUBLICATEUR);
        String type = luceneDoc.get(TYPE);
        String volume = luceneDoc.get(VOLUME);
        String pages = luceneDoc.get(PAGES);
        String acces = luceneDoc.get(ACCES);
        String source = luceneDoc.get(SOURCE);
        String lienVersPub = luceneDoc.get(LIEN_VERS_ARTICLE);



        return new Documents(tempsDeRecherche, nombreTotalResultat, nombreAffiche, titre, auteur, anneeDePub, publicateur, type, volume,
                pages, acces, lienVersPub, source);
    }

    static Path getMainIndexPath(Path indexRoot) {
        return indexRoot.resolve(INDEX_NAME);
    }



    @Override
    public void close() throws Exception {
        indexWriter.close();
    }



    public void AjouterDocumentsJSON(List<Documents> docs) throws IOException {

        Field champTempsR = new TextField(TEMPS_DE_RECHERCHE, "", Field.Store.YES);
        Field valeurTempsR = new SortedDocValuesField(TEMPS_DE_RECHERCHE, new BytesRef(0));

        Field chamNombreTR = new TextField(NOMBRE_TOTAL_RESULTATS, "", Field.Store.YES);
        Field valeurChampNombreTR = new SortedDocValuesField(NOMBRE_TOTAL_RESULTATS, new BytesRef(0));

        Field champNombreAffiche = new TextField(NOMBRE_AFFICHE, "", Field.Store.YES);
        Field valeurChampNombreAffiche = new SortedDocValuesField(NOMBRE_AFFICHE, new BytesRef(0));

        Field champTitre = new TextField(TITRE, "", Field.Store.YES);
        Field valeurChampTitre = new SortedDocValuesField(TITRE, new BytesRef(0));

        Field champAuteur = new TextField(AUTEUR, "", Field.Store.YES);
        Field valeurChampAuteur = new SortedDocValuesField(AUTEUR, new BytesRef(0));

        Field champAnneePub = new TextField(ANNEE_PUB, "", Field.Store.YES);
        Field valeurChampAnneePub = new SortedDocValuesField(ANNEE_PUB, new BytesRef(0));

        Field champPublicateur = new TextField(PUBLICATEUR, "", Field.Store.YES);
        Field valeurPublicateur = new SortedDocValuesField(PUBLICATEUR, new BytesRef(0));

        Field champType = new TextField(TYPE, "", Field.Store.YES);
        Field valeurChampType = new SortedDocValuesField(TYPE, new BytesRef(0));

        Field champVolume = new TextField(VOLUME, "", Field.Store.YES);
        Field valeurChampVolume = new SortedDocValuesField(VOLUME, new BytesRef(0));

        Field champPages = new TextField(PAGES, "", Field.Store.YES);
        Field valeurChampPages = new SortedDocValuesField(PAGES, new BytesRef(0));

        Field champAcces = new TextField(ACCES, "", Field.Store.YES);
        Field valeurChampAcces = new SortedDocValuesField(ACCES, new BytesRef(0));

        Field champSource = new TextField(SOURCE, "", Field.Store.YES);
        Field valeurChampSource = new SortedDocValuesField(SOURCE, new BytesRef(0));

        Field champLienVArt = new TextField(LIEN_VERS_ARTICLE, "", Field.Store.YES);
        Field valeurLienVArt = new SortedDocValuesField(LIEN_VERS_ARTICLE, new BytesRef(0));




        for (Documents doc : docs) {
            org.apache.lucene.document.Document luceneDoc = new org.apache.lucene.document.Document();

            if (doc.tempsDeRecherche != null && !doc.tempsDeRecherche.isEmpty()) {
                champTempsR.setStringValue(doc.tempsDeRecherche);
                luceneDoc.add(champTempsR);

                valeurTempsR.setBytesValue(new BytesRef(doc.tempsDeRecherche));
                luceneDoc.add(valeurTempsR);
            }

            if (doc.nombreTotalResultat != null && !doc.nombreTotalResultat.isEmpty()) {
                chamNombreTR.setStringValue(doc.nombreTotalResultat);
                luceneDoc.add(chamNombreTR);

                valeurChampNombreTR.setBytesValue(new BytesRef(doc.nombreTotalResultat));
                luceneDoc.add(valeurChampNombreTR);
            }

            if (doc.nombreAffiche != null && !doc.nombreAffiche.isEmpty()) {
                champNombreAffiche.setStringValue(doc.nombreAffiche);
                luceneDoc.add(champNombreAffiche);

                valeurChampNombreAffiche.setBytesValue(new BytesRef(doc.nombreAffiche));
                luceneDoc.add(valeurChampNombreAffiche);
            }

            if (doc.titre != null && !doc.titre.isEmpty()) {
                champTitre.setStringValue(doc.titre);
                luceneDoc.add(champTitre);

                valeurChampTitre.setBytesValue(new BytesRef(doc.titre));
                luceneDoc.add(valeurChampTitre);
            }

            if (doc.auteur != null && !doc.auteur.isEmpty()) {
                champAuteur.setStringValue(doc.auteur);
                luceneDoc.add(champAuteur);

                valeurChampAuteur.setBytesValue(new BytesRef(doc.auteur));
                luceneDoc.add(valeurChampAuteur);
            }

            if (doc.anneeDePub != null && !doc.anneeDePub.isEmpty()) {
                champAnneePub.setStringValue(doc.anneeDePub);
                luceneDoc.add(champAnneePub);

                valeurChampAnneePub.setBytesValue(new BytesRef(doc.anneeDePub));
                luceneDoc.add(valeurChampAnneePub);
            }

            if (doc.publicateur != null && !doc.publicateur.isEmpty()) {
                champPublicateur.setStringValue(doc.publicateur);
                luceneDoc.add(champPublicateur);

                valeurPublicateur.setBytesValue(new BytesRef(doc.publicateur));
                luceneDoc.add(valeurPublicateur);
            }

            if (doc.typeDarticle != null && !doc.typeDarticle.isEmpty()) {
                champType.setStringValue(doc.typeDarticle);
                luceneDoc.add(champType);

                valeurChampType.setBytesValue(new BytesRef(doc.typeDarticle));
                luceneDoc.add(valeurChampType);
            }

            if (doc.volume != null && !doc.volume.isEmpty()) {
                champVolume.setStringValue(doc.volume);
                luceneDoc.add(champVolume);

                valeurChampVolume.setBytesValue(new BytesRef(doc.volume));
                luceneDoc.add(valeurChampVolume);
            }

            if (doc.pages != null && !doc.pages.isEmpty()) {
                champPages.setStringValue(doc.pages);
                luceneDoc.add(champPages);

                valeurChampPages.setBytesValue(new BytesRef(doc.pages));
                luceneDoc.add(valeurChampPages);
            }

            if (doc.acces != null && !doc.acces.isEmpty()) {
                champAcces.setStringValue(doc.acces);
                luceneDoc.add(champAcces);

                valeurChampAcces.setBytesValue(new BytesRef(doc.acces));
                luceneDoc.add(valeurChampAcces);
            }

            if (doc.source != null && !doc.source.isEmpty()) {
                champSource.setStringValue(doc.source);
                luceneDoc.add(champSource);

                valeurChampSource.setBytesValue(new BytesRef(doc.source));
                luceneDoc.add(valeurChampSource);
            }

            if (doc.lienVersArticle != null && !doc.lienVersArticle.isEmpty()) {
                champLienVArt.setStringValue(doc.lienVersArticle);
                luceneDoc.add(champLienVArt);

                valeurLienVArt.setBytesValue(new BytesRef(doc.titre));
                luceneDoc.add(valeurLienVArt);
            }

            indexWriter.addDocument(luceneDoc);
        }

        indexWriter.commit();
    }


    public void deleteDocumentsByQuery(String queryStr) throws ParseException, IOException {
        Query query = parseQuery(indexWriter.getAnalyzer(), queryStr);
        indexWriter.deleteDocuments(query);
        indexWriter.commit();
    }

}
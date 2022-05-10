package com.siliddor.developper.lucenepourandroid;


public class Documents {
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

    public Documents(String tempsDeRecherche, String nombreTotalResultat, String nombreAffiche, String titre, String auteur,
                     String anneeDePub, String publicateur, String typeDarticle,
                     String volume, String pages, String acces, String source, String lienVersArticle) {
        this.tempsDeRecherche = tempsDeRecherche;
        this.nombreTotalResultat = nombreTotalResultat;
        this.nombreAffiche = nombreAffiche;
        this.titre = titre;
        this.auteur = auteur;
        this.anneeDePub = anneeDePub;
        this.publicateur = publicateur;
        this.typeDarticle = typeDarticle;
        this.volume = volume;
        this.pages = pages;
        this.acces = acces;
        this.source = source;
        this.lienVersArticle = lienVersArticle;
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("(")
                .append(tempsDeRecherche)
                .append(", ")
                .append(nombreTotalResultat)
                .append(", ")
                .append(nombreAffiche)
                .append(", ")
                .append(titre)
                .append(", ")
                .append(auteur)
                .append(", ")
                .append(anneeDePub)
                .append(", ")
                .append(publicateur)
                .append(", ")
                .append(typeDarticle)
                .append(", ")
                .append(volume)
                .append(", ")
                .append(pages)
                .append(", ")
                .append(acces)
                .append(", ")
                .append(source)
                .append(", ")
                .append(lienVersArticle)
                .append(")");
        return builder.toString();
    }
}
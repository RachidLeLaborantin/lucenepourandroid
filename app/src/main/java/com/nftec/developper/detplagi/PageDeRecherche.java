package com.nftec.developper.detplagi;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import com.siliddor.developper.lucenepourandroid.SearchResult;
import com.siliddor.developper.lucenepourandroid.Searcher;
import com.siliddor.developper.lucenepourandroid.Study;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.lucene.queryparser.classic.ParseException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

public class PageDeRecherche extends AppCompatActivity {
    private static final String TAG = PageDeRecherche.class.getSimpleName();
    private final static String SOURCE_DES_DONNEES = "resultats.json";
    private static final String NOM_DIRECTION_INDEX = "index";
    private ArrayAdapter<Resultats> adaptateurResultats;
    private ListView listeResultats;
    private View vuesRestantes;
    private TextView etatDutexte;
    private SearchView champDeRecherche;

    private Executor executor;
    private LancementRecherche lancementRecherche;
    private static final String LIEN_INITIAL = "https://dblp.uni-trier.de/search/publ/api?q=";
    private File fichier;
    private ProgressDialog progression;
    private ProgressDialog recuperationEncours;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_de_recherche);

        etatDutexte = findViewById(R.id.etatDuTexte);
        vuesRestantes = findViewById(R.id.vuesRestantes);

        ///lancementRecherche = new LancementRecherche(this);

        adaptateurResultats = new AdaptateurResultats(this, new ArrayList<Resultats>());
        listeResultats = findViewById(R.id.listeDeResultats);
        Button boutonReconstruire = findViewById(R.id.boutonReconstruireIndex);
        listeResultats.setAdapter(adaptateurResultats);

        DefinirEtatTexte(getString(R.string.texteBienvenue));
        champDeRecherche = findViewById(R.id.champDeRecherche);
        champDeRecherche.setQueryHint(getString(R.string.entrezMotCle));

        champDeRecherche.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String texteEntre) {
                //RecreerIndex();
                lancementRecherche = new LancementRecherche(PageDeRecherche.this);

                final StringBuilder stringBuilder = new StringBuilder();

                stringBuilder.append(LIEN_INITIAL)
                        .append(texteEntre)
                        .append("&h=1000&format=json");

                        try {

                            //LancementRecheche lancementRecheche = new LancementRecheche();

                            //lancementRecheche.execute(new String[]{"http://www.google.com"});
                            recuperationEncours = new ProgressDialog(PageDeRecherche.this);
                            recuperationEncours.setTitle("Veuillez patienter...");
                            recuperationEncours.setMessage("Récupération des résultats");
                            recuperationEncours.setCancelable(false);
                            recuperationEncours.show();
                            lancementRecherche.execute(new String[]{stringBuilder.toString()});



                            Searcher rechercheur = new Searcher(RecupererCheminIndex().getAbsolutePath());
                            SearchResult resultatDeRecherche = rechercheur.search(texteEntre, 100);
                            List<Resultats> resultats = Resultats.ResultatDesRecherches(resultatDeRecherche);
                            rechercheur.close();

                            TextView tempsResultat = findViewById(R.id.cTempsDR);
                            tempsResultat.setText(resultats.get(0).tempsDeRecherche);

                            TextView nombreTotal = findViewById(R.id.cnombreTR);
                            nombreTotal.setText(resultats.get(0).nombreTotalResultat);

                            adaptateurResultats.clear();
                            adaptateurResultats.addAll(resultats);
                            adaptateurResultats.notifyDataSetChanged();

                            if (resultats.size() == 0) {
                                DefinirEtatTexte(getString(R.string.aucunResultat));
                            } else {
                                DefinirEtatTexte(null);
                            }
                            champDeRecherche.clearFocus();
                        } catch (ParseException e) {
                            DefinirEtatTexte(getString(R.string.aucunResultat));
                            Toast.makeText(PageDeRecherche.this, R.string.erreurAdaptationRequete, Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            DefinirEtatTexte(getString(R.string.aucunResultat));
                            //Toast.makeText(PageDeRecherche.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        recuperationEncours.dismiss();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        boutonReconstruire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecreerIndex();
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        RecreerIndexSiNonExistant();
    }


    private void DefinirEtatTexte(String texte) {
        if (texte == null) {
            vuesRestantes.setVisibility(View.INVISIBLE);
            etatDutexte.setText("");
            listeResultats.setVisibility(View.VISIBLE);
        } else {
            vuesRestantes.setVisibility(View.VISIBLE);
            etatDutexte.setText(texte);
            listeResultats.setVisibility(View.INVISIBLE);
        }
    }




    private static class AfficheurDeVues {
        private TextView tempsResultat;
        private TextView nombreTotalResultat;
        private TextView titre;
        private TextView auteur;
        private TextView anneeDePub;
        private TextView volume;
        private TextView publicateur;
        private TextView pages;
        private TextView type;
        private TextView acces;
        private TextView source;
        private TextView lienVersPub;
    }

    private File RecupererCheminIndex() {
        return new File(getCacheDir(), NOM_DIRECTION_INDEX);
    }

    private void RecreerIndex() {
        progression = new ProgressDialog(this);
        final StringBuilder builder = new StringBuilder();
        progression.setTitle(R.string.titreCreationIndex);
        progression.setMessage(getString(R.string.corpsCreationIndex));
        progression.setCancelable(false);
        progression.show();
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {

                try {
                    File fichier = new File(getCacheDir(), "resultats.json");
                    InputStream is = new FileInputStream(fichier);

                    Study.ImporterDonneesJSON(is, RecupererCheminIndex().getAbsolutePath(), false);
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean result) {
                progression.dismiss();
                if (result) {
                    DefinirEtatTexte(getString(R.string.texteBienvenue));
                } else {
                    Toast.makeText(PageDeRecherche.this, R.string.erreurCreationIndex, Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    private void RecreerIndexSiNonExistant() {
        if (!RecupererCheminIndex().exists()) {
            RecreerIndex();
        }
    }


    private class AdaptateurResultats extends ArrayAdapter<Resultats> {

        public AdaptateurResultats(Context context, List<Resultats> resultats) {
            super(context, R.layout.vue_de_resultat, resultats);
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final Resultats resultats = getItem(position);
            AfficheurDeVues afficheurDeVues;

            if (convertView == null) {
                afficheurDeVues = new AfficheurDeVues();
                convertView = getLayoutInflater().inflate(R.layout.vue_de_resultat, parent, false);
                afficheurDeVues.tempsResultat = convertView.findViewById(R.id.cTempsDR);
                afficheurDeVues.nombreTotalResultat = convertView.findViewById(R.id.cnombreTR);
                afficheurDeVues.titre = convertView.findViewById(R.id.cTitre);
                afficheurDeVues.auteur = convertView.findViewById(R.id.cAuteur);
                afficheurDeVues.anneeDePub = convertView.findViewById(R.id.cAnnePub);
                afficheurDeVues.publicateur = convertView.findViewById(R.id.cPublicateur);
                afficheurDeVues.type = convertView.findViewById(R.id.cTypeArticle);
                afficheurDeVues.volume = convertView.findViewById(R.id.cVolume);
                afficheurDeVues.pages = convertView.findViewById(R.id.cPages);
                afficheurDeVues.acces = convertView.findViewById(R.id.cAcces);
                afficheurDeVues.source = convertView.findViewById(R.id.source);
                afficheurDeVues.lienVersPub = convertView.findViewById(R.id.lienVersArt);

                //On rend les sources cliquables
                afficheurDeVues.source.setMovementMethod(LinkMovementMethod.getInstance());

                convertView.setTag(afficheurDeVues);
            } else {
                afficheurDeVues = (AfficheurDeVues) convertView.getTag();
            }

            assert resultats != null;
            //afficheurDeVues.tempsResultat.setText(resultats.tempsDeRecherche);
            //afficheurDeVues.nombreTotalResultat.setText(resultats.nombreTotalResultat);
            afficheurDeVues.titre.setText(Html.fromHtml(resultats.titre));
            afficheurDeVues.auteur.setText(resultats.auteur); // info is not in HTML
            afficheurDeVues.anneeDePub.setText(resultats.anneeDePub);
            afficheurDeVues.publicateur.setText(resultats.publicateur);
            afficheurDeVues.type.setText(resultats.typeDarticle);
            afficheurDeVues.volume.setText(resultats.volume);
            afficheurDeVues.pages.setText(resultats.pages);
            afficheurDeVues.acces.setText(resultats.acces);
            afficheurDeVues.lienVersPub.setText(Html.fromHtml(resultats.lienVersArticle));
            afficheurDeVues.source.setText(Html.fromHtml(resultats.source));

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(PageDeRecherche.this, DetailsDesResultats.class);
                    intent.putExtra(DetailsDesResultats.EXTRA_TITRE, resultats.titre);
                    intent.putExtra(DetailsDesResultats.EXTRA_INFO, resultats.auteur);
                    intent.putExtra(DetailsDesResultats.EXTRA_SOURCE, resultats.source);
                    startActivity(intent);
                }
            });

            return convertView;
        }
    }

    private class LancementRecheche extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {
            StringBuilder reponseRequete = new StringBuilder();
            for (String liens : strings) {
                DefaultHttpClient client = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(liens);

                try {
                    HttpResponse executer = client.execute(httpGet);
                    InputStream contenu = executer.getEntity().getContent();

                    BufferedReader tampon = new BufferedReader(new InputStreamReader(contenu));
                    String s = "";
                    while ((s = tampon.readLine()) != null){
                        reponseRequete.append(s);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

            return reponseRequete.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            DefinirEtatTexte(s);
        }
    }
}

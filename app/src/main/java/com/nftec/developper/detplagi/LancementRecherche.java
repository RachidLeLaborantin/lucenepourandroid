package com.nftec.developper.detplagi;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.siliddor.developper.lucenepourandroid.Study;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class LancementRecherche extends AsyncTask<String, Void, String> {
    private Connection connection;
    private Document document;
    private final Context context;
    private File dossier, fichier;
    private String donneesObtenues;
    private FileWriter ecriveur;
    private ProgressDialog progression;

    public LancementRecherche(Context context){
        this.context = context;
    }

    public String Connexion() throws IOException {
        //connection = Jsoup.connect(lien);
        //document = connection.get();

        //Creation d'un fichier temporaire pour sauvegarder le resultat de la requete
        //dossier = context.getCacheDir();
        //fichier = File.createTempFile("resultat", ".xml", dossier);

        //donneesObtenues = document.body().text();

        //ecriveur = new FileWriter(fichier);
        //ecriveur.write(donneesObtenues);

        return donneesObtenues;
    }

    private File RecupererCheminIndex() {
        return new File(context.getCacheDir(), "index");
    }

    @Override
    protected void onPreExecute() {
        progression = new ProgressDialog(context);
        progression.setTitle("Veuillez patienter...");
        progression.setMessage("Recherche en cours");
        progression.setCancelable(false);
        //progression.show();
    }

    @Override
    protected String doInBackground(String... strings) {


        StringBuilder reponseRequete = new StringBuilder();
        for (String liens : strings) {

            try {

                /*
                TrustStrategy trustStrategy = new TrustStrategy() {
                    @Override
                    public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                        return true;
                    }
                };

                SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, trustStrategy).build();
                SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslContext,
                                                            NoopHostnameVerifier.INSTANCE);
                Registry<ConnectionSocketFactory> socketFactoryRegistry =
                                                    RegistryBuilder.<ConnectionSocketFactory>create()
                                                            .register("https", socketFactory)
                                                            .register("http", new PlainConnectionSocketFactory())
                                                            .build();
                BasicHttpClientConnectionManager connectionManager =
                        new BasicHttpClientConnectionManager(socketFactoryRegistry);

                CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(socketFactory)
                        .setConnectionManager(connectionManager).build();


                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(liens);
                HttpResponse executer = httpClient.execute(httpGet);

                 */
                //Creation d'un fichier temporaire pour sauvegarder le resultat de la requete


                URL url = new URL(liens);
                URLConnection connection = url.openConnection();

                InputStream contenu = connection.getInputStream();

                //Study.ImporterDonneesJSON(contenu, RecupererCheminIndex().getAbsolutePath(), false);
                BufferedReader tampon = new BufferedReader(new InputStreamReader(contenu));
                String s = "";
                while ((s = tampon.readLine()) != null){
                    reponseRequete.append(s);
                }

            } catch (Exception e) {
                //Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            }

        }
        try {
            fichier = new File(context.getCacheDir(), "resultats.json");
            FileOutputStream outputStream = new FileOutputStream(fichier.getAbsolutePath());
            OutputStreamWriter writer = new OutputStreamWriter(outputStream);
            writer.write(reponseRequete.toString());
            writer.close();
            Toast.makeText(context, "Création terminée", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {

        }


        return reponseRequete.toString();
    }


    @Override
    protected void onPostExecute(String resultat) {
        //Creation d'un fichier temporaire pour sauvegarder le resultat de la requete
        //dossier = context.getCacheDir();
        //progression.dismiss();
        //Toast.makeText(context, resultat, Toast.LENGTH_SHORT).show();
    }




}

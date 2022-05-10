package com.nftec.developper.detplagi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

public class DetailsDesResultats extends AppCompatActivity {
    public static final String EXTRA_TITRE = DetailsDesResultats.class.getCanonicalName() + ".titre";
    public static final String EXTRA_INFO = DetailsDesResultats.class.getCanonicalName() + ".info";
    public static final String EXTRA_SOURCE = DetailsDesResultats.class.getCanonicalName() + ".source";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_des_resultats);

        Intent intent = getIntent();
        TextView detailsAffiche = findViewById(R.id.detailsAffiche);
        String titre = intent.getStringExtra(EXTRA_TITRE);
        String info = intent.getStringExtra(EXTRA_INFO);
        String source = intent.getStringExtra(EXTRA_SOURCE);

        StringBuilder builder = new StringBuilder();
        builder.append("<p><big><big>")
                .append(titre)
                .append("</big></big></p>");

        builder.append("<p>")
                .append(info)
                .append(" ")
                .append(source)
                .append("</p>");

        detailsAffiche.setText(Html.fromHtml(builder.toString()));
        detailsAffiche.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
package com.example.nation_currency;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;


public class CountryDetail extends AppCompatActivity {
    private TextView name, code, capital, area, population, currency, languages, continent;
    private ImageView flagView, mapView;
    private Double d = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail);

        name = (TextView) findViewById(R.id.countryName);
        code = (TextView) findViewById(R.id.codeText);
        capital = (TextView) findViewById(R.id.capitalText);
        area = (TextView) findViewById(R.id.areaText);
        population = (TextView) findViewById(R.id.populationText);
        currency = (TextView) findViewById(R.id.currencyText);
        languages = (TextView) findViewById(R.id.languagesText);
        continent = (TextView) findViewById(R.id.continentText);
        flagView = (ImageView) findViewById(R.id.flagView);
        mapView = (ImageView) findViewById(R.id.mapView);

        Intent i = getIntent();

        name.setText(i.getStringExtra("name"));
        code.setText(i.getStringExtra("code"));
        capital.setText(i.getStringExtra("capital"));
        area.setText(i.getStringExtra("area")+" Km²");
        population.setText(i.getStringExtra("population")+" Person");

        currency.setText(i.getStringExtra("currency"));
        languages.setText(i.getStringExtra("languages"));
        continent.setText(i.getStringExtra("continent"));

        //Picasso.get().load(i.getStringExtra("flag")).into(flagView);
        //Picasso.get().load(i.getStringExtra("map")).into(mapView);

        Glide.with(getApplicationContext()).load(i.getStringExtra("flag")).into(flagView);
        Glide.with(getApplicationContext()).load(i.getStringExtra("map")).into(mapView);



    }
}

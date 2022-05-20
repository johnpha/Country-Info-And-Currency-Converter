package com.example.nation_currency;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private static final String TAG = "NetworkingURLActivity";
    private final static String MTEXTVIEW_TEXT_KEY = "MTEXTVIEW_TEXT_KEY";

    private ListView countryListView;
    private String currency, languages;

    private List<Country> result;
    private NumberFormat formater = new DecimalFormat("#,###");


    private static final String URL = "http://api.geonames.org/countryInfoJSON?username=aporter";
    private static final String URLCurrency = "https://openexchangerates.org/api/currencies.json";
    private static final String URLLanguages = "https://gist.githubusercontent.com/piraveen/fafd0d984b2236e809d03a0e306c8a4d/raw/4258894f85de7752b78537a4aa66e027090c27ad/languages.json";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        new Thread(new Runnable() {
            @Override
            public void run() {

                result = loadFromNetwork();

                CountryViewAdapter countriesArrayAdapter = new CountryViewAdapter(getApplicationContext(), result);
                countryListView = findViewById(R.id.listView);
                countryListView.setOnItemClickListener(MainActivity.this::onItemClick);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        countryListView.setAdapter(countriesArrayAdapter);

                    }

                });

            }
        }).start();


    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id){
        Intent intent = new Intent(getApplicationContext(), CountryDetail.class);

        Country c = result.get(position);

        List<String> languageList = Arrays.asList(c.getLanguages().split(","));

        for(int i = 0; i < languageList.size(); i++){

            String s = languageList.get(i);

            if(s.contains("-")){
                languageList.set(i, s.substring(0, s.indexOf("-")));
            }
        }


        new Thread(new Runnable() {
            @Override
            public void run() {
                //parse json currency code -> currency name
                currency = loadFromNetworkCurency(URLCurrency, c.getCurrency());

                //parse json language code -> language name
                languages = "";
                for(String s : languageList){

                    String str = loadFromNetworkLaguage(URLLanguages, s) + ", ";

                    languages += str;

                }
                languages = languages.substring(0, languages.length()-2);

                String mArea = formater.format(c.getArea());
                String mPopulation = formater.format(c.getPopulation());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        //pass value item selected -> Country detail class

                        intent.putExtra("code", c.getCode());
                        intent.putExtra("name", c.getName());
                        intent.putExtra("flag", c.getUrlFlagImage());
                        intent.putExtra("capital", c.getCapital());
                        intent.putExtra("area", mArea);
                        intent.putExtra("population", mPopulation);
                        intent.putExtra("currency", currency);

                        intent.putExtra("languages", languages);
                        intent.putExtra("continent", c.getContinentName());
                        intent.putExtra("map", c.getUrlMapImage());

                        startActivity(intent);

                    }

                });

            }
        }).start();



    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if(id == R.id.exchange){
            Intent i = new Intent(getApplicationContext(), CurrencyConverter.class);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public List<Country> parseJsonString(String data) {
        List<Country> result = new ArrayList<>();

        try {

            JSONObject responseObject = (JSONObject) new JSONTokener(data).nextValue();

            JSONArray countries = responseObject.getJSONArray("geonames");

            for (int idx = 0; idx < countries.length(); idx++) {

                JSONObject country = (JSONObject) countries.get(idx);

                result.add(new Country(country.getString("countryName"), country.getString("countryCode"),
                        country.getString("capital"), country.getDouble("areaInSqKm"),
                        country.getInt("population"), country.getString("currencyCode"),
                        country.getString("languages"), country.getString("continentName")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    private String parseJsonStringLanguage(String data, String key) {
        String name = "";
        try {
            JSONObject responseObject = (JSONObject) new JSONTokener(data).nextValue();

            JSONObject languageObject = responseObject.getJSONObject(key);

            name = languageObject.getString("name");


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return name;
    }

    private String parseJsonStringCurrency(String data, String key) {

        String name = "";
        try {

            JSONObject responseObject = (JSONObject) new JSONTokener(data).nextValue();

            name = responseObject.getString(key);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return name;
    }


    public String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuilder data = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
                data.append(line);
            }
        } catch (IOException e) {
            Log.e(TAG, "IOException");
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(TAG, "IOException");
                }
            }
        }
        return data.toString();
    }

    public List<Country> loadFromNetwork() {
        String data = null;
        List<Country> result = null;
        HttpURLConnection httpUrlConnection = null;

        try {

            httpUrlConnection = (HttpURLConnection) new URL(URL)
                    .openConnection();

            InputStream in = new BufferedInputStream(
                    httpUrlConnection.getInputStream());

            data = readStream(in);

            result = parseJsonString(data);

        } catch (MalformedURLException exception) {
            Log.e(TAG, "MalformedURLException");

        } catch (IOException exception) {
            Log.e(TAG, "IOException");

        } finally {
            if (null != httpUrlConnection) {

                httpUrlConnection.disconnect();
            }
        }

        return result;
    }
    private String loadFromNetworkLaguage(String url,String key) {
        String data = null;
        String result = null;
        HttpURLConnection httpUrlConnection = null;

        try {

            httpUrlConnection = (HttpURLConnection) new URL(url)
                    .openConnection();

            InputStream in = new BufferedInputStream(
                    httpUrlConnection.getInputStream());

            data = readStream(in);

            result = parseJsonStringLanguage(data, key);

        } catch (MalformedURLException exception) {
            Log.e(TAG, "MalformedURLException");

        } catch (IOException exception) {
            Log.e(TAG, "IOException");

        } finally {
            if (null != httpUrlConnection) {

                httpUrlConnection.disconnect();
            }
        }

        return result;
    }

    private String loadFromNetworkCurency(String url,String key) {
        String data = null;
        String result = null;
        HttpURLConnection httpUrlConnection = null;

        try {

            httpUrlConnection = (HttpURLConnection) new URL(url)
                    .openConnection();

            InputStream in = new BufferedInputStream(
                    httpUrlConnection.getInputStream());

            data = readStream(in);

            result = parseJsonStringCurrency(data, key);

        } catch (MalformedURLException exception) {
            Log.e(TAG, "MalformedURLException");

        } catch (IOException exception) {
            Log.e(TAG, "IOException");

        } finally {
            if (null != httpUrlConnection) {

                httpUrlConnection.disconnect();
            }
        }

        return result;
    }

    /*
    private List<Country> loadFromNetworkXML() {
        String data = null;
        List<Country> result = null;
        HttpURLConnection httpUrlConnection = null;

        try {
            // 1. Get connection. 2. Prepare request (URI)
            httpUrlConnection = (HttpURLConnection) new URL(URLxml)
                    .openConnection();

            // 3. This app does not use a request body
            // 4. Read the response

            InputStream in = new BufferedInputStream(
                    httpUrlConnection.getInputStream());

//            data = readStream(in);


//            result = parseJsonString(data);

            result = parsePullXML(in);

        } catch (MalformedURLException exception) {
            Log.e(TAG, "MalformedURLException");
        } catch (IOException exception) {
            Log.e(TAG, "IOException");
        } finally {
            if (null != httpUrlConnection) {
                // 5. Disconnect
                httpUrlConnection.disconnect();
            }
        }

        //return String data;

        return result;
    }

     */

    /*
    List<Country> parsePullXML(InputStream in) {
        List<Country> result = new ArrayList<>();

        try {
            XmlPullParserFactory parserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = parserFactory.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES,false);
            parser.setInput(in,null);

            String tag = "" , text = "";

            int event = parser.getEventType();

            Country c = null;

            while (event!= XmlPullParser.END_DOCUMENT){
                tag = parser.getName();
                switch (event){
                    case XmlPullParser.START_TAG:
                        if(tag.equals("country"))
                            c = new Country();
                        break;
                    case XmlPullParser.TEXT:
                        text=parser.getText();
                        break;
                    case XmlPullParser.END_TAG:
                        switch (tag){
                            case "countryCode": c.setCode(text);
                                break;
                            case "countryName": c.name = text;
                                break;
                            case "country":
                                if(c!=null)
                                    result.add(c);
                                break;
                        }
                        break;
                }
                event = parser.next();
            }

        }
        catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        return result;
    }

     */

}
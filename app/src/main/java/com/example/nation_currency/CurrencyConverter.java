package com.example.nation_currency;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

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

public class CurrencyConverter extends AppCompatActivity{

    private static final String TAG = "NetworkingURLActivity";
    private final static String MTEXTVIEW_TEXT_KEY = "MTEXTVIEW_TEXT_KEY";

    private Spinner sFrom, sTo;

    private Double currencyFrom = 1.0, currencyTo = 1.0;

    private ArrayList<Currency> result;

    private ArrayList<String> currencyName = new ArrayList<String>();


    private static final String urlCurrency = "https://raw.githubusercontent.com/leequixxx/currencies.json/master/currencies.json";

    private NumberFormat formater = new DecimalFormat("#,###");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exchange);

        Intent i = getIntent();

        sFrom = (Spinner) findViewById(R.id.ExchangeFrom);

        sTo = (Spinner) findViewById(R.id.ExchangeTo);


        new Thread(new Runnable() {
            @Override
            public void run() {
                result = loadFromNetwork(urlCurrency);

                for(Currency cur : result){
                    currencyName.add(cur.getCode()+" - "+cur.getName());
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, currencyName);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        sFrom.setAdapter(adapter);

                        sTo.setAdapter(adapter);

                    }
                });
            }
        }).start();

        Button btnConfrim = (Button) findViewById(R.id.btnConfỉrm);
        btnConfrim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int posFrom = sFrom.getSelectedItemPosition();

                        int posTo = sTo.getSelectedItemPosition();

                        Currency curObjectFrom = result.get(posFrom);

                        Currency curObjectTo = result.get(posTo);

                        if(!curObjectFrom.getCode().equalsIgnoreCase("USD")){
                            String curResultFrom = loadFromNetworkXML(curObjectFrom.getUrl());
                            currencyFrom = Double.parseDouble(curResultFrom.substring(curResultFrom.indexOf("=") + 1, curResultFrom.indexOf(curObjectFrom.getCode())));
                        }

                        if(!curObjectTo.getCode().equalsIgnoreCase("USD")) {

                            String curResultTo = loadFromNetworkXML(curObjectTo.getUrl());
                            currencyTo = Double.parseDouble(curResultTo.substring(curResultTo.indexOf("=") + 1, curResultTo.indexOf(curObjectTo.getCode())));
                        }

                        EditText editFrom = (EditText) findViewById(R.id.numFrom);
                        Double amountFrom = Double.parseDouble(editFrom.getText().toString());

                        Double usd = amountFrom / currencyFrom;

                        Double amountTo = usd * currencyTo;
                        String result = formater.format(amountTo);

                        EditText editTo = (EditText) findViewById(R.id.numTo);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                editTo.setText(result);
                            }
                        });
                    }
                }).start();



            }
        });




    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


/*
    public void displayCurrency(int positionFrom, int positionTo){
        Currency curObjectFrom = result.get(positionFrom);
        String curResultFrom = loadFromNetworkXML(curObjectFrom.getUrl());
        Double currencyFrom = Double.parseDouble(curResultFrom.substring(curResultFrom.indexOf("=") + 1, curResultFrom.indexOf(curObjectFrom.getCode())));

        Currency curObjectTo = result.get(positionFrom);
        String curResultTo = loadFromNetworkXML(curObjectTo.getUrl());
        Double currencyTo = Double.parseDouble(curResultTo.substring(curResultTo.indexOf("=") + 1, curResultTo.indexOf(curObjectTo.getCode())));


    }

    public Double calculateCurrency(Double amountFrom, Double currencyFrom, Double currencyTo){
        Double temp = amountFrom * currencyFrom;
        if(currencyTo > 0 )
            return temp / currencyTo;
        return 0.0;
    }

 */

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

    public ArrayList<Currency> loadFromNetwork(String url) {
        String data = null;
        ArrayList<Currency> result = null;
        HttpURLConnection httpUrlConnection = null;

        try {

            httpUrlConnection = (HttpURLConnection) new URL(url)
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

    public ArrayList<Currency> parseJsonString(String data) {

        ArrayList<Currency> result = new ArrayList<>();

        try {

            JSONArray curArr = new JSONArray(data);

            for (int idx = 0; idx < curArr.length(); idx++) {

                JSONObject cur = (JSONObject) curArr.get(idx);

                result.add(new Currency(cur.getString("code"), cur.getString("name")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public String loadFromNetworkXML(String url) {
        String data = null;
        String result = "";
        HttpURLConnection httpUrlConnection = null;

        try {

            httpUrlConnection = (HttpURLConnection) new URL(url)
                    .openConnection();


            InputStream in = new BufferedInputStream(
                    httpUrlConnection.getInputStream());

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

        return result;
    }



    public String parsePullXML(InputStream in) {
        String result = null;

        try {
            XmlPullParserFactory parserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = parserFactory.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES,false);
            parser.setInput(in,null);

            String tag = "" , text = "";

            int event = parser.getEventType();


            while (event!= XmlPullParser.END_DOCUMENT){
                tag = parser.getName();
                switch (event){
                    case XmlPullParser.START_TAG:
                        if(tag.equals("description"))
                            result = "";
                        break;
                    case XmlPullParser.TEXT:
                        text = parser.getText();
                        break;
                    case XmlPullParser.END_TAG:
                        if(tag.equals("description"))
                            result = text;
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
}
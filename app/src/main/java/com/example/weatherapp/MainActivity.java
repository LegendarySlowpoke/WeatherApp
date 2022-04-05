package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.icu.text.Edits;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {

    //Fields for requesting data
    private final String urlPart1 = "https://api.openweathermap.org/data/2.5/weather?q=";
    private final String urlPart2 = "&appid=";
    private final String urlPart3 = "&units=metric&lang=en"; // Setting language & metrics
    private final String key1 = "beb4708b7544eb975926a69d9b06eaf2";
    private final String key2 = "b5b4a2e494497f7c3b17c4812b0aa29f";

    //UI fields
    private TextView resultLabel;
    private EditText userInputField;
    private Button searchButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resultLabel = findViewById(R.id.resultInfo);
        userInputField = findViewById(R.id.userField);
        searchButton = findViewById(R.id.searchButtonMain);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View view) {
                if (userInputField.getText().toString().trim().equals("")) {
                    Toast.makeText(MainActivity.this, R.string.inputErrorZeroLength,
                            Toast.LENGTH_LONG).show();
                } else {
                    String requestedCity = userInputField.getText().toString();
                    String url = urlPart1 + requestedCity + urlPart2 + key1 + urlPart3;

                    new GetUrlData().execute(url);
                }
            }
        });
    }

    private class GetUrlData extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            resultLabel.setText("Loading info...");
        }

        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection connection = null;
            BufferedReader bufReader = null;

            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream inputStream = connection.getInputStream();
                bufReader = new BufferedReader(new InputStreamReader(inputStream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = bufReader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }

                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                assert connection != null;
                connection.disconnect();

                try {
                    if (bufReader != null)
                        bufReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            return null;
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String result) {

            if (result == null) {
                resultLabel.setText("Error: city not found =(");
            } else {
                super.onPostExecute(result);

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String sky = "";
                    JSONArray weatherTime = (JSONArray) jsonObject.get("weather");
                    System.out.println(weatherTime);

                    for (int i = 0; i < weatherTime.length(); i++) {
                        JSONObject weatherInfo = weatherTime.getJSONObject(i);
                        sky = (String) weatherInfo.get("description");
                    }


                    resultLabel.setText("City: " + jsonObject.getString("name") +
                            " in " + jsonObject.getJSONObject("sys").getString("country") +
                            "\nTemp: " +
                            jsonObject.getJSONObject("main").getDouble("temp")
                            + "°С\nSky: " + sky
                    );


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
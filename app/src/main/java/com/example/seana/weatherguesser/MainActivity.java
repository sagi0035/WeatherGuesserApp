package com.example.seana.weatherguesser;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    EditText editText;
    TextView resultTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editText);
        resultTextView = findViewById(R.id.resultTextView);
    }

    public void getWeather(View view) {

        try {
            // so when getWeather is clicked we will call Downloader
            Downloader task = new Downloader();

            String encodeCityName = URLEncoder.encode(editText.getText().toString(), "UTF-8");

            // and here is were we pick the api from
            // note that the edit text here will be specifying which city's weather we will be obtaining
            task.execute("https://openweathermap.org/data/2.5/weather?q=" + editText.getText().toString() + "&appid=439d4b804bc8187953eb36d2a8c26a02");

            // so this will in turn be to hide the keyboard
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"Invalid City",Toast.LENGTH_SHORT).show();
        }
    }

    public class Downloader extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            // so here we are obraining the url of the api
            URL url;
            HttpURLConnection connection = null;

            // so if there actually is a url (as per the api) we will initialise
            try {
                url = new URL(urls[0]);
                // here iswere we open the connection
                connection = (HttpURLConnection) url.openConnection();

                // so now we get and read the inputstream
                InputStream in = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();

                // so if data is in fact available
                while (data!=-1) {
                    // we will set a new variable that holds all of the data info
                    char current = (char) data;
                    // then we set the results to it
                    result+=current;
                    // we read the data
                    data = reader.read();
                }

                // so then we return the result
                return result;

            } catch (Exception e) {
                e.printStackTrace();

                // if we cannot get any url info we just return null
                return null;
            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);



            try {
                // so here is the object with all the json info
                JSONObject jsonObject = new JSONObject(s);
                // and we will get the weather info from said object
                String weatherInfo = jsonObject.getString("weather");
                // and we will create an array made up of all the weather info
                JSONArray arr = new JSONArray(weatherInfo);

                String message = "";

                // and now we will iterate through the contents of the above created array
                for (int i = 0; i < arr.length(); i++) {
                    // we get the part from the whole
                    JSONObject jsonPart = arr.getJSONObject(i);

                    // so we get the strings for the main and the description
                    String main = jsonPart.getString("main");
                    String description = jsonPart.getString("description");

                    // so if both are not empty we convey them within the message string
                    if (!main.equals("") && !description.equals("")) {
                        message+=main + ": " + description;
                    }

                }

                // if a valid city was entered the message will reflect that and we will set our textview as such
                if (!message.equals("")) {
                    resultTextView.setText(message);
                } else {
                    Toast.makeText(getApplicationContext(),"Invalid City",Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                Toast.makeText(getApplicationContext(),"Invalid City",Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }


        }
    }


}

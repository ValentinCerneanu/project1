package com.example.valentin.conectare;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class ComandaActivity extends AppCompatActivity {

    // CONNECTION_TIMEOUT and READ_TIMEOUT are in milliseconds

    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;
    private EditText pIdRestaurant;
    private EditText pComanda;
    private EditText pNotaDePlata;
    private String Client=null;
    public static String formattedDate=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comanda);

        // Get Reference to variables
        pIdRestaurant = (EditText) findViewById(R.id.IdRestaurant);
        pComanda = (EditText) findViewById(R.id.Comanda);
        pNotaDePlata = (EditText) findViewById(R.id.NotaDePlata);

        // client
        SecurePreferences preferences = new SecurePreferences(getApplicationContext(), "user-info",
                "YourSecurityKey", true);
        //get
        Client = preferences.getString("username");

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        formattedDate = df.format(c.getTime());

        Log.d("ComandaActivity", formattedDate);
    }

    // Triggers when LOGIN Button clicked
    public void trimiteComanda(View arg0) {
        Log.d("ComandaActivity", "BUTTON");
        final String IdRestaurant = pIdRestaurant.getText().toString();
        final String Comanda = pComanda.getText().toString();
        final String NotaDePlata = pNotaDePlata.getText().toString();
        final String data = formattedDate.toString();
        Log.d("ComandaActivity", data);

        // Initialize  AsyncLogin() class with email and password
        new AsyncLogin().execute(IdRestaurant, Comanda, NotaDePlata, Client, data);

    }


    private class AsyncLogin extends AsyncTask<String, String, String>
    {
        ProgressDialog pdLoading = new ProgressDialog(ComandaActivity.this);
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            pdLoading.setMessage("\tLoading...");
            pdLoading.setCancelable(false);
            pdLoading.show();

        }
        @Override
        protected String doInBackground(String... params) {
            try {

                // Enter URL address where your php file resides
                url = new URL("http://food.netne.net/aplications_scripts/insertComanda.inc.php");

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return "exception";
            }
            try {
                // Setup HttpURLConnection class to send and receive data from php and mysql
                conn = (HttpURLConnection)url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("POST");

                // setDoInput and setDoOutput method depict handling of both send and receive
                conn.setDoInput(true);
                conn.setDoOutput(true);

                // Append parameters to URL
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("idrestaurant", params[0])
                        .appendQueryParameter("comanda", params[1])
                        .appendQueryParameter("notadeplata", params[2])
                        .appendQueryParameter("client", params[3])
                        .appendQueryParameter("oraintroducere", params[4]);
                String query = builder.build().getEncodedQuery();

                // Open connection for sending data
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                conn.connect();

            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return "exception";
            }

            try {

                int response_code = conn.getResponseCode();

                // Check if successful connection made
                if (response_code == HttpURLConnection.HTTP_OK) {

                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    // Pass data to onPostExecute method
                    Log.d("result", result.toString());
                    return(result.toString());

                }else{

                    return("unsuccessful");
                }

            } catch (IOException e) {
                e.printStackTrace();
                return "exception";
            } finally {
                conn.disconnect();
            }
        }

        @Override
        protected void onPostExecute(String result) {

            //this method will be running on UI thread

            pdLoading.dismiss();
            Log.d("ComandaActivity", result);
            if(result.equalsIgnoreCase("true"))
            {
                /* Here launching another activity when login successful. If you persist login state
                use sharedPreferences of Android. and logout button to clear sharedPreferences.
                 */
                Log.d("ComandaActivity", "TRUE");
                Intent intent = new Intent(ComandaActivity.this,SuccessComandaActivity.class);
                startActivity(intent);
                ComandaActivity.this.finish();
            }
            else if (result.equalsIgnoreCase("false")){
                Log.d("ComandaActivity", "FALSE");
                Intent intent = new Intent(ComandaActivity.this,FailActivity.class);
                startActivity(intent);
                ComandaActivity.this.finish();

                // If username and password does not match display a error message
                // Toast.makeText(MainActivity.this, "Invalid email or password", Toast.LENGTH_LONG);
            }
            else if (result.equalsIgnoreCase("exception") || result.equalsIgnoreCase("unsuccessful")) {
                Log.d("ComandaActivity", "Exception");
                Intent intent = new Intent(ComandaActivity.this,ErrorConectionActivity.class);
                startActivity(intent);
                ComandaActivity.this.finish();
                //Toast.makeText(MainActivity.this, "OOPs! Something went wrong. Connection Problem.", Toast.LENGTH_LONG);

            }
        }
    }
}
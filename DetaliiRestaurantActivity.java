package com.example.valentin.conectare;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.ArrayList;
import java.util.List;


public class DetaliiRestaurantActivity extends AppCompatActivity {

    // CONNECTION_TIMEOUT and READ_TIMEOUT are in milliseconds

    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;
    public String passedID = null;
    public String passedNume = null;
    public String[] header = new String[60];
    public int cnt = 0;
    public int contor = 0;
    public int suma = 0;
    public int nrProduse = 0;
    public static StringBuilder comanda = new StringBuilder();
    ArrayList<SearchResults> results = new ArrayList<SearchResults>();
    public ProgressDialog pdLoading;
    int tip = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalii_restaurant);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        header[0] = "Salate";
        header[1] = "Pizza";
        header[2] = "Paste";
        header[3] = "Preparate din carne";

        passedID = getIntent().getExtras().getString("ID");
        passedNume = getIntent().getExtras().getString("Nume");

        TextView Nume = (TextView) findViewById(R.id.textViewTitluRestaurant);
        Nume.setText(passedNume);
        pdLoading = new ProgressDialog(DetaliiRestaurantActivity.this);
        lista();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.action_bar, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_cart: {
                Log.d("DetaliiComanda", "CARTTTTT");
                return true;
            }
            case android.R.id.home: {
                this.finish();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void lista() {
        cnt = 0;
        Log.d("DetaliiRestaurant", String.valueOf(tip));
        new AsyncLogin().execute(passedID, String.valueOf(tip));
    }

    public void afisare() {
        pdLoading.cancel();
        final ListView lv = (ListView) findViewById(R.id.products_listview);
        lv.setAdapter(new MyCustomBaseAdapter(this, results));
    }

    public void vizualizeazaComanda(View view) {

        Intent intent = new Intent(DetaliiRestaurantActivity.this, CartActivity.class);
        intent.putExtra("ID", passedID);
        startActivity(intent);
    }

    private class AsyncLogin extends AsyncTask<String, String, String> {
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("DetaliiRestaurant", "12");

            //this method will be running on UI thread

            pdLoading.setMessage("\tLoading...");
            pdLoading.setCancelable(false);
            pdLoading.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {

                // Enter URL address where your php file resides
                url = new URL("http://food.netne.net/aplications_scripts/meniu.inc.php");

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return "exception";
            }
            try {
                // Setup HttpURLConnection class to send and receive data from php and mysql
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("POST");

                // setDoInput and setDoOutput method depict handling of both send and receive
                conn.setDoInput(true);
                conn.setDoOutput(true);

                // Append parameters to URL
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("id", params[0])
                        .appendQueryParameter("tip", params[1]);
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
                    Log.d("DetaliiRestaurant", "13");

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    // Pass data to onPostExecute method
                    return (result.toString());

                } else {

                    return ("unsuccessful");
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

            Log.d("DetaliiRestaurant", "3");
            Log.d("DetaliiRestaurant", result);

            String[] a = new String[60];
            a = result.split("ENDOFONE");

            String[] tipuri = new String[60];
            if (a[0].equalsIgnoreCase("true")) {

                tipuri = a[1].split("ENDOFORDER");
                Log.d("DetaliiRestauranttipuri", tipuri[0]);
                int x = tipuri.length;
                int i = 0;
                List<String> head = new ArrayList<String>();

                String[] detalii = new String[60];
                while (i < x) {
                    contor++;
                    detalii = tipuri[i].split("END");
                    SearchResults sr = new SearchResults();

                    sr.setName(detalii[0]);
                    sr.setIngrediente(detalii[1]);
                    sr.setPret(detalii[2]);
                    results.add(sr);
                    i++;
                }
                Log.d("DetaliiRestaurant", "4");
                Log.d("DetaliiRestaurantCONTOR", String.valueOf(contor));
                Log.d("CNT", String.valueOf(cnt));
                cnt++;

                tip++;
                if(tip<4)
                    lista();
                else
                    afisare();

            }
        }
    }
}
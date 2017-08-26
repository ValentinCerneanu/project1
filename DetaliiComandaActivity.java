package com.example.valentin.conectare;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

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
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.Toast;

import static com.example.valentin.conectare.MainActivity.getDataAccount;


public class DetaliiComandaActivity extends AppCompatActivity {

    // CONNECTION_TIMEOUT and READ_TIMEOUT are in milliseconds

    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;
    public String passedArg=null;
    public static String dataAccount = new String();
    public static String dataComanda = new String();
    public String[] s = new String[10];

    private RatingBar ratingBar1;
    private RatingBar ratingBar2;
    private RatingBar ratingBar3;

    private Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalii_comanda);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
       actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);


        addListenerOnButton();

        passedArg = getIntent().getExtras().getString("arg");
        Log.d("DetaliiComanda", passedArg);

        // Get Reference to variables

        new AsyncLogin().execute(passedArg);
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
            case android.R.id.home:
            {
                this.finish();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onClick() {
        onBackPressed();
    }

    private class AsyncLogin extends AsyncTask<String, String, String>
    {
        ProgressDialog pdLoading = new ProgressDialog(DetaliiComandaActivity.this);
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
                url = new URL("http://food.netne.net/aplications_scripts/detaliiComanda.inc.php");

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
                        .appendQueryParameter("id", params[0]);
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
            pdLoading.cancel();
            //titlu
            String titlu = new String();
            titlu = "Detalii comanda #";
            StringBuilder Titlu = new StringBuilder();
            Titlu.append(titlu);
            Titlu.append(passedArg);
            TextView idcomanda = (TextView)findViewById(R.id.textViewTitlu);
            idcomanda.setText(Titlu);

            Log.d("detalii", result);

            // !!!!
            dataComanda = result;
            String[] detaliiout = new String[7];
            detaliiout = result.split("ENDOFLINE ");

            TextView line1 = (TextView)findViewById(R.id.line1);
            line1.setText(detaliiout[0]);

            TextView line2 = (TextView)findViewById(R.id.line2);
            line2.setText(detaliiout[1]);


            //comandaaa!!!

            String[] caca ;
            caca=detaliiout[2].split("; ");

            ArrayAdapter adapter = new ArrayAdapter<String>(DetaliiComandaActivity.this, R.layout.activity_listview, caca);

            ListView listView = (ListView) findViewById(R.id.produse);
            listView.setAdapter(adapter);

            TextView line4 = (TextView)findViewById(R.id.line4);
            line4.setText(detaliiout[3]);

            TextView line5 = (TextView)findViewById(R.id.line5);
            line5.setText(detaliiout[4]);

            TextView line6 = (TextView)findViewById(R.id.line6);
            line6.setText(detaliiout[5]);

        }
    }

    private class AsyncRating extends AsyncTask<String, String, String>
    {
        ProgressDialog pdLoading = new ProgressDialog(DetaliiComandaActivity.this);
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
                url = new URL("http://food.netne.net/aplications_scripts/rating.inc.php");

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
                        .appendQueryParameter("ID_Restaurant", params[0])
                        .appendQueryParameter("ID_Client", params[1])
                        .appendQueryParameter("ID_Comanda", params[2])
                        .appendQueryParameter("Mancare", params[3])
                        .appendQueryParameter("Servire", params[4])
                        .appendQueryParameter("Restaurant", params[5])
                        .appendQueryParameter("Recomanzi", params[6]);
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
            pdLoading.cancel();
            Log.d("DetaliiSuccessComanda", result);
        }
    }

    public void addListenerOnButton() {

        ratingBar1 = (RatingBar) findViewById(R.id.ratingBar1);
        ratingBar2 = (RatingBar) findViewById(R.id.ratingBar2);
        ratingBar3 = (RatingBar) findViewById(R.id.ratingBar3);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);

        //if click on me, then display the current rating value.
        btnSubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //id este dateAccount[2]
                dataAccount=getDataAccount().toString();

                String[] dateAccount = new String[60];
                dateAccount = dataAccount.split(" ");

                String[] dateComanda = new String[60];
                dateComanda = dataComanda.split(" ");

                Log.d("IDres", dateComanda[4]);
                Log.d("IDclient", dateAccount[2]);
                Log.d("IDcomanda",dateComanda[2]);
                float rat1=ratingBar1.getRating();
                String r1=String.valueOf(rat1);
                float rat2=ratingBar2.getRating();
                String r2=String.valueOf(rat2);
                float rat3=ratingBar3.getRating();
                String r3=String.valueOf(rat3);
                boolean rat4=true;
                String r4=String.valueOf(rat4);
                Log.d("bool",r4);

                new AsyncRating().execute(dateComanda[4], dateAccount[2], dateComanda[2], r1, r2, r3, r4);

            }

        });
    }
}
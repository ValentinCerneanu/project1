package com.example.valentin.conectare;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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


public class MainActivity extends AppCompatActivity {

    // CONNECTION_TIMEOUT and READ_TIMEOUT are in milliseconds

    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;
    String email = null;
    String password =null;
    public static String emailfinal = null;
    public static EditText etEmail;
    private EditText etPassword;
    public static StringBuilder result = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get Reference to variables
        etEmail = (EditText) findViewById(R.id.email);
        etPassword = (EditText) findViewById(R.id.password);


        SecurePreferences preferences = new SecurePreferences(getApplicationContext(), "user-info",
                "YourSecurityKey", true);
        //get
        email = preferences.getString("username");
        password = preferences.getString("password");



        if(email!= null && password != null ){
            Log.d("Mainrez:", "NOT NULL");
            //utilizatorul NU trebuie sa introduca datele
            new AsyncLogin().execute(email,password);
            Log.d("Mainuser:", email);
            Log.d("Mainpass:", password);
        }
        else {
            //utilizatorul trebuie sa introduca datele
            Log.d("Mainrez:", "NULL");
        }
    }

    public void register (View view){
        Intent intent = new Intent(MainActivity.this,RegisterActivity.class);
        startActivity(intent);
        MainActivity.this.finish();
    }

    // Triggers when LOGIN Button clicked
    public void checkLogin(View arg0) {
        Log.d("Main:", "BUTTON");
        // Get text from email and passord field
        email = etEmail.getText().toString();
        password = etPassword.getText().toString();
        emailfinal = email;
        // Initialize  AsyncLogin() class with email and password

        new AsyncLogin().execute(email,password);

    }

    private class AsyncLogin extends AsyncTask<String, String, String>
    {
        ProgressDialog pdLoading = new ProgressDialog(MainActivity.this);
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
                Log.d("Mainrez:", "1");
                // Enter URL address where your php file resides
                url = new URL("http://food.netne.net/aplications_scripts/login.inc.php");

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return "exception";
            }
            try {
                Log.d("Mainrez:", "2");
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
                        .appendQueryParameter("email", params[0])
                        .appendQueryParameter("password", params[1]);
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

                    result.setLength(0);
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    // Pass data to onPostExecute method
                    Log.d("Mainrez:", "3");
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
            Log.d("Mainrez:", "4");
            Log.d("Mainrez:", result);
            String[] date = new String[60];
            date = result.split(" ");
            if(date[0].equalsIgnoreCase("true"))
            {
                /* Here launching another activity when login successful. If you persist login state
                use sharedPreferences of Android. and logout button to clear sharedPreferences.
                 */

                Log.d("Mainrez:", "5");

                Log.d("Mainuser1:", email);
                Log.d("Mainpass1:", password);

                SecurePreferences preferences = new SecurePreferences(getApplicationContext(), "user-info",
                        "YourSecurityKey", true);
                // Put (all puts are automatically committed)
                preferences.put("username", email);
                preferences.put("password", password);

                Log.d("Main:", "TRUE");
                Intent intent = new Intent(MainActivity.this,SuccessActivity.class);
                intent.putExtra("From", "0");
                startActivity(intent);
                MainActivity.this.finish();


            }else if (result.equalsIgnoreCase("false")){
               Log.d("Main", "FALSE");
                //Intent intent = new Intent(MainActivity.this,FailActivity.class);
                //startActivity(intent);
                //MainActivity.this.finish();

                // If username and password does not match display a error message
                Toast.makeText(MainActivity.this, "Invalid email or password", Toast.LENGTH_LONG).show();

            } else if (result.equalsIgnoreCase("exception") || result.equalsIgnoreCase("unsuccessful")) {
                Log.d("Main", "Exception");
                //Intent intent = new Intent(MainActivity.this,ErrorConectionActivity.class);
                //startActivity(intent);
                //MainActivity.this.finish();

                Toast.makeText(MainActivity.this, "Connection Problem!", Toast.LENGTH_LONG).show();
            }
        }
    }

    public static StringBuilder getDataAccount() {
        return result;
    }

}
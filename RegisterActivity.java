package com.example.valentin.conectare;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
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


public class RegisterActivity extends AppCompatActivity {

    // CONNECTION_TIMEOUT and READ_TIMEOUT are in milliseconds

    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;
    public static EditText etEmail;
    private EditText etPassword;
    private EditText etUsername;
    private EditText etTel;
    public static String emailfinal = null;
    public static StringBuilder result = new StringBuilder();
    String emailPref=null;
    String passwordPref=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Get Reference to variables
        etEmail = (EditText) findViewById(R.id.email);
        etPassword = (EditText) findViewById(R.id.password);
        etUsername = (EditText) findViewById(R.id.username);
        etTel = (EditText) findViewById(R.id.tel);

    }

    // Triggers when LOGIN Button clicked
    public void register(View arg0) {
        Log.d("Register:", "BUTTON");
        // Get text from email and passord field
        final String email = etEmail.getText().toString();
        final String password = etPassword.getText().toString();
        final String username = etUsername.getText().toString();
        final String tel = etTel.getText().toString();

        emailPref=email;
        passwordPref=password;

        emailfinal = email;

        // Initialize  AsyncLogin() class with email and password
        new AsyncLogin().execute(email, username, password, tel);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
            {
                Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
                startActivity(intent);
                RegisterActivity.this.finish();
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static String getEmail() {
        return emailfinal;
    }

    public void onBackPressed() {
        Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
        startActivity(intent);
        RegisterActivity.this.finish();
    }

    private class AsyncLogin extends AsyncTask<String, String, String>
    {
        ProgressDialog pdLoading = new ProgressDialog(RegisterActivity.this);
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
                url = new URL("http://food.netne.net/aplications_scripts/registerNewAccount.inc.php");

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
                        .appendQueryParameter("email", params[0])
                        .appendQueryParameter("username", params[1])
                        .appendQueryParameter("password", params[2])
                        .appendQueryParameter("tel", params[3]);
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

            pdLoading.dismiss();
            Log.d("Register result:", result);

            String[] date = new String[60];
            date = result.split(" ");
            Log.d("Register date:", date[0]);
            if(date[0].equalsIgnoreCase("true"))
            {
                /* Here launching another activity when login successful. If you persist login state
                use sharedPreferences of Android. and logout button to clear sharedPreferences.
                 */
                SecurePreferences preferences = new SecurePreferences(getApplicationContext(), "user-info",
                        "YourSecurityKey", true);
                // Put (all puts are automatically committed)
                preferences.put("username", emailPref);
                preferences.put("password", passwordPref);

                Log.d("Register", "TRUE");
                Intent intent = new Intent(RegisterActivity.this,SuccessActivity.class);
                intent.putExtra("From", "1");
                startActivity(intent);
                RegisterActivity.this.finish();


            }else if (result.equalsIgnoreCase("false")){
                Log.d("Register", "FALSE");
                //Intent intent = new Intent(RegisterActivity.this,FailActivity.class);
                //startActivity(intent);
                //RegisterActivity.this.finish();

                Toast.makeText(RegisterActivity.this, "All fields are required", Toast.LENGTH_LONG).show();

            } else if (result.equalsIgnoreCase("exception") || result.equalsIgnoreCase("unsuccessful")) {
                Log.d("Register", "Exception");
                //Intent intent = new Intent(RegisterActivity.this,ErrorConectionActivity.class);
                //startActivity(intent);
                //RegisterActivity.this.finish();

                Toast.makeText(RegisterActivity.this, "Connection Problem!", Toast.LENGTH_LONG).show();
            }
        }

    }
    public static StringBuilder getDataAccount() {
        return result;
    }
}
package com.example.valentin.conectare;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.util.Scanner;


public class SuccessActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private String Client=null;
    public String passedArg=null;
    private static StringBuilder DataAccount= new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();

        passedArg = getIntent().getExtras().getString("From");

        if(passedArg.equalsIgnoreCase("0")) {
            DataAccount = MainActivity.getDataAccount();
            Log.d("SuccessActivityARG", passedArg);
            Log.d("SuccessActivity0", String.valueOf(DataAccount));
        }
        else {
            DataAccount = RegisterActivity.getDataAccount();
            Log.d("SuccessActivity1", String.valueOf(DataAccount));
        }

        String[] DateAccount= new String[60];
        DateAccount=DataAccount.toString().split(" ");
        Log.d("SuccessActivity", DateAccount[1]); //nume client
        Log.d("SuccessActivity", DateAccount[2]); //id client

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

     //  TextView NumeUtilizator = (TextView)findViewById(R.id.textViewNumeUtilizator1);
      // NumeUtilizator.setText("8941");

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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void goToRestaurante (View view){
        Intent intent = new Intent(SuccessActivity.this,RestauranteActivity.class);
        startActivity(intent);
    }

    private static final int TIME_INTERVAL = 2000; // # milliseconds, desired time passed between two back presses.
    private long mBackPressed;
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            Log.d("SuccessActivity", "backmenu");
        } else {
            Log.d("SuccessActivity", "back");
            if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis())
            {
                super.onBackPressed();
                return;
            }
            else { Toast.makeText(getBaseContext(), "Double tap to exit", Toast.LENGTH_SHORT).show(); }

            mBackPressed = System.currentTimeMillis();

        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.ist_com)
        {
            Intent intent = new Intent(SuccessActivity.this,ComenziActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.com_desfasurare)
        {
            Intent intent = new Intent(SuccessActivity.this,ComenziInDesfasurareActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.gaseste_restaurante)
        {
            Intent intent = new Intent(SuccessActivity.this,RestauranteActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.logout)
        {
            SecurePreferences preferences = new SecurePreferences(getApplicationContext(), "user-info", "YourSecurityKey", true);
            preferences.put("username", null);
            preferences.put("password", null);

            Intent intent = new Intent(SuccessActivity.this,MainActivity.class);
            startActivity(intent);
            SuccessActivity.this.finish();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

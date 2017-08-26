package com.example.valentin.conectare;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class SuccessComandaActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private String Client=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_successcomanda);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();

        SecurePreferences preferences = new SecurePreferences(getApplicationContext(), "user-info",
                "YourSecurityKey", true);
        //get
        Client = preferences.getString("username");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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

    public void goToComenziInDesfasurare (View view){
        Intent intent = new Intent(SuccessComandaActivity.this,ComenziInDesfasurareActivity.class);
        startActivity(intent);
    }

    private static final int TIME_INTERVAL = 2000; // # milliseconds, desired time passed between two back presses.
    private long mBackPressed;
    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            Log.d("SuccessComanda", "backmenu");
        } else {
            Log.d("SuccessComanda", "back");
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
            // Handle the camera action
            Intent intent = new Intent(SuccessComandaActivity.this,ComenziActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.com_desfasurare)
        {
            Intent intent = new Intent(SuccessComandaActivity.this,ComenziInDesfasurareActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.gaseste_restaurante)
        {
            Intent intent = new Intent(SuccessComandaActivity.this,RestauranteActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.logout)
        {
            SecurePreferences preferences = new SecurePreferences(getApplicationContext(), "user-info", "YourSecurityKey", true);
            preferences.put("username", null);
            preferences.put("password", null);

            Intent intent = new Intent(SuccessComandaActivity.this,MainActivity.class);
            startActivity(intent);
            SuccessComandaActivity.this.finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

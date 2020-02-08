package com.yilativ.punnettsquare;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import com.google.android.material.navigation.NavigationView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static String FIRST_PARENT = "FIRST_PARENT";
    public static String SECOND_PARENT = "SECOND_PARENT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_about) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_send) {
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("text/plain");
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"vitalytyrenko7@gmail.com"});
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "About \"Punnett Square\" app");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "");

            try {
                startActivity(Intent.createChooser(emailIntent, getString(R.string.send_email)));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(this, getString(R.string.no_email_client),
                        Toast.LENGTH_SHORT).show();
            }
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void ButtonGenerateClicked(View view) {
        LinearLayout linearLayout = findViewById(R.id.main_layout);
        linearLayout.requestFocus();
        //InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        //inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

        TextView textView1 = findViewById(R.id.first_parent_gene);
        TextView textView2 = findViewById(R.id.second_parent_gene);
        String s1 = textView1.getText().toString();
        String s2 = textView2.getText().toString();
        String t1 = s1.toUpperCase();
        String t2 = s2.toUpperCase();
        boolean isCorrectInput = true;

        if (t1.length() % 2 == 0 && t2.length() % 2 == 0 && t1.length() <= 16 && t2.length() <= 16
                && !t1.isEmpty() && !t2.isEmpty() && t1.length() == t2.length()) {

            for (short i = 0; i < t1.length(); i++) {
                if (t1.charAt(i) != t1.charAt(++i)) {
                    isCorrectInput = false;
                    break;
                }
            }

            if (isCorrectInput) {
                for (short i = 0; i < t2.length(); i++) {
                    if (t2.charAt(i) != t2.charAt(++i)) {
                        isCorrectInput = false;
                        break;
                    }
                }
            }

        } else {
            isCorrectInput = false;
        }

        if (isCorrectInput) {
            Intent intent = new Intent(this, TableActivity.class);
            intent.putExtra(FIRST_PARENT, s1);
            intent.putExtra(SECOND_PARENT, s2);
            startActivity(intent);
        } else {
            Toast.makeText(this, getString(R.string.fields_not_correct),
                    Toast.LENGTH_LONG).show();
        }

    }

}

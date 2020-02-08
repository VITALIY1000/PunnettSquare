package com.yilativ.punnettsquare;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.os.Environment;
import android.util.Log;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

public class TableActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private StringBuffer webData;
    private StringBuffer textData;
    private Context context;
    private Activity activity;
    public static int PERMISSION_REQUEST_CODE = 1;
    public static int pq = -1;
    private final static String FILE_NAME_TXT = "punnett.txt";
    private final static String FILE_NAME_HTML = "punnett.html";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        context = this;
        activity = this;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.save_as));
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.select_dialog_singlechoice);
        arrayAdapter.add("HTML");
        arrayAdapter.add("TXT");

        builder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        Intent intent = getIntent();
        String s1 = intent.getStringExtra(MainActivity.FIRST_PARENT);
        String s2 = intent.getStringExtra(MainActivity.SECOND_PARENT);
        if (s1 == null || s2 == null) return;

        String[] g1 = parentGenes(s1);
        String[] g2 = parentGenes(s2);

        StringBuffer ph = new StringBuffer();
        webData = new StringBuffer();
        textData = new StringBuffer();
        webData.append("<table border=\"1px\">");

        for (short i = 0; i < s1.length() / 2; i++) {
            ph.append(" ");
        }

        for (short i = -1; i < g2.length; i++) {
            webData.append("<tr>");

            for (short j = -1; j < g1.length; j++) {
                webData.append("<td>");

                if (i == -1) {
                    if (j == -1) {
                        textData.append(ph);
                        textData.append(ph);
                    } else {
                        webData.append(g1[j]);
                        textData.append(g1[j]);
                        textData.append(ph);
                    }
                } else {
                    if (j == -1) {
                        webData.append(g2[i]);
                        textData.append(g2[i]);
                        textData.append(ph);
                    } else {
                        webData.append(child(g1[i], g2[j]));
                        textData.append(child(g1[i], g2[j]));
                    }
                }

                textData.append("  ");
                webData.append("</td>");
            }

            textData.append("\r\n");
            webData.append("</tr>");
        }

        webData.append("</table>");

        WebView webView = findViewById(R.id.webview);
        webView.loadData(webData.toString(), "text/html", "utf-8");

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

        if (id == R.id.nav_home) {
            finish();
        } else if (id == R.id.nav_about) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
            finish();
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                saveText(pq);
            } else {
                Toast.makeText(context, getString(R.string.no_permission),
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    public void FABClicked(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Save as:");
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.select_dialog_item);
        arrayAdapter.add("HTML");
        arrayAdapter.add("TXT");

        builder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    saveText(i);
                } else {
                    ActivityCompat.requestPermissions(activity,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            PERMISSION_REQUEST_CODE);
                    pq = i;
                }
            }
        });

        builder.show();
    }

    private void saveText(int i) {
        // проверяем доступность SD
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, getString(R.string.storage_is_not_available),
                    Toast.LENGTH_LONG).show();
            return;
        }
        // получаем путь к SD
        File sdPath = Environment.getExternalStorageDirectory();
        // формируем объект File, который содержит путь к файлу
        File sdFile = new File(sdPath, i == 0 ? FILE_NAME_HTML : FILE_NAME_TXT);
        try {
            // открываем поток для записи
            BufferedWriter bw = new BufferedWriter(new FileWriter(sdFile));
            // пишем данные
            bw.write((i == 0 ? webData : textData).toString());
            // закрываем поток
            bw.close();
            Toast.makeText(this, getString(R.string.file_saved_to)
                    .concat(sdFile.getAbsolutePath()), Toast.LENGTH_LONG).show();

        } catch (IOException e) {
            Toast.makeText(this, getString(R.string.error_while_saving), Toast.LENGTH_LONG)
                    .show();
        }
    }

    private String[] parentGenes(String s) {

        int l = (int) Math.pow(2, s.length() / 2f);
        String[] g = new String[l];
        for (short i = 0; i < l; i++) {
            g[i] = "";
        }

        int lett = 0;
        while (lett < s.length()) {
            int i = 0;
            l /= 2;
            while (i < (Math.pow(2, s.length() / 2f))) {
                for (int j = 0; j < l; j++) {
                    g[i] += s.substring(lett, lett + 1);
                    i++;
                }
                for (int j = 0; j < l; j++) {
                    g[i] += s.substring(lett + 1, lett + 2);
                    i++;
                }
            }
            lett += 2;
        }

        return g;
    }

    private StringBuffer child(String g1, String g2) {
        StringBuffer s = new StringBuffer();

        for (int i = 0; i < g1.length(); i++) {
            s.append(g1.charAt(i) > g2.charAt(i) ? g2.substring(i, i + 1) + g1.substring(i, i + 1)
                    : g1.substring(i, i + 1) + g2.substring(i, i + 1));
        }

        return s;
    }

}

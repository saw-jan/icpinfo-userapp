package com.jan.saw.icp;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class Main extends AppCompatActivity implements Home.OnFragmentInteractionListener,Notice.OnFragmentInteractionListener,Post.OnFragmentInteractionListener,NavigationView.OnNavigationItemSelectedListener{

    private DrawerLayout sidebar;
    private ActionBarDrawerToggle toggle;
    private NavigationView navView;
    public static final String PREFS = "prefs";
    public Toolbar toolbar;
    //Url instance
    private UrlFetch path;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        path = new UrlFetch();
        FirebaseMessaging.getInstance().subscribeToTopic("Test");
        FirebaseInstanceId.getInstance().getToken();

        navView = findViewById(R.id.nav);
        final TabLayout tabs = (TabLayout) findViewById(R.id.tabs);
        tabs.addTab(tabs.newTab().setText("Home"));
        tabs.addTab(tabs.newTab().setText("Notices"));
        tabs.addTab(tabs.newTab().setText("Posts"));
        tabs.setTabGravity(TabLayout.GRAVITY_FILL);

        //session
        SharedPreferences prefs = getSharedPreferences(PREFS,0);

        View header = navView.inflateHeaderView(R.layout.header);

        TextView std_name = header.findViewById(R.id.student_name);
        TextView std_id = header.findViewById(R.id.student_id);
        TextView std_fac = header.findViewById(R.id.faculty);
        TextView std_mail = header.findViewById(R.id.gen);
        std_name.setText(prefs.getString("name",""));
        std_id.setText(prefs.getString("id",""));
        std_fac.setText(prefs.getString("faculty",""));
        std_mail.setText(prefs.getString("mail",""));
        char gender = prefs.getString("gender","").charAt(0);
        if(gender == 'F'){
            ImageView std_pp = header.findViewById(R.id.pro_img);
            std_pp.setBackground(getResources().getDrawable(R.drawable.profem));
            std_pp.invalidate();
        }
        else if(gender == 'M'){
            ImageView std_pp = header.findViewById(R.id.pro_img);
            std_pp.setBackground(getResources().getDrawable(R.drawable.pro));
            std_pp.invalidate();
        }

        //slide tab view
        toolbar = findViewById(R.id.toolbar);
        final ViewPager viewPager = findViewById(R.id.tab_content);
        final PagerAdapter adp = new PagerAdapter(getSupportFragmentManager(), tabs.getTabCount());
        viewPager.setAdapter(adp);
        viewPager.setOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabs));
        tabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                tabs.setNextFocusRightId(tab.getPosition());
                switch(tab.getPosition()){
                    case 0:
                        toolbar.setTitle("Informatics College");
                        break;
                    case 1:
                        toolbar.setTitle("Notice Board");
                        break;
                    case 2:
                        toolbar.setTitle("Public Post");
                        break;
                }
            }

            public void onTabUnselected(TabLayout.Tab tab) {

            }

            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        //drawer menu toggle
        Toolbar tbar = findViewById(R.id.toolbar);
        setSupportActionBar(tbar);

        sidebar = findViewById(R.id.sidebar);
        toggle = new ActionBarDrawerToggle(this,sidebar,R.string.open,R.string.close);
        sidebar.setDrawerListener(toggle);
        sidebar.addDrawerListener(toggle);
        toggle.syncState();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        navView.setNavigationItemSelectedListener(this);
        //navView.setCheckedItem(R.id.home);
        navView.getMenu().getItem(0).setChecked(true);
    }

    boolean exit = false;
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.sidebar);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else{
            if (exit == true) {
                System.exit(1);
            }
            exit = true;
            //double back pressed exit
            Toast.makeText(Main.this, "Press again to exit", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 2000);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        //getMenuInflater().inflate(R.menu.drawermenu,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(toggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.home:
                        item.setChecked(true);
                        sidebar.closeDrawers();
                        break;
                    case R.id.profile:
                        Intent call = new Intent(this,Profile.class);
                        startActivity(call);
                        finish();
                        item.setChecked(true);
                        sidebar.closeDrawers();
                        break;
                    case R.id.about:
                        Intent call1 = new Intent(this,About.class);
                        startActivity(call1);
                        finish();
                        item.setChecked(true);
                        sidebar.closeDrawers();
                        break;
                    case R.id.logout:
                        SharedPreferences prefs =getSharedPreferences(PREFS,this.MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.clear();
                        editor.apply();
                        finish();
                        Intent calLogin = new Intent(this,Login.class);
                        startActivity(calLogin);
                        finish();
                        break;
                    case R.id.exit:
                        System.exit(1);
                        break;
                }
                sidebar.closeDrawer(GravityCompat.START);
                return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    //pdf downloader
    public void download(View v, String file)
    {
        //String path="http://192.168.1.12/icp/files/";
        new DownloadFile().execute(path.path, file);
    }
    //pdf viewer
    public void view(View v,String file)
    {
        File pdfFile = new File(Environment.getExternalStorageDirectory() + "/icp/" + file);
        Uri path = Uri.fromFile(pdfFile);
        Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
        if (pdfFile.toString().contains(".doc") || pdfFile.toString().contains(".docx")) {
            // Word document
            pdfIntent.setDataAndType(path, "application/msword");
            pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        } else if (pdfFile.toString().contains(".pdf")) {
            // PDF file
            pdfIntent.setDataAndType(path, "application/pdf");
            pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        } else if (pdfFile.toString().contains(".ppt") || pdfFile.toString().contains(".pptx")) {
            // Powerpoint file
            pdfIntent.setDataAndType(path, "application/vnd.ms-powerpoint");
            pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        } else if (pdfFile.toString().contains(".xls") || pdfFile.toString().contains(".xlsx")) {
            // Excel file
            pdfIntent.setDataAndType(path, "application/vnd.ms-excel");
            pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }else if (pdfFile.toString().contains(".rtf")) {
            // RTF file
            pdfIntent.setDataAndType(path, "application/rtf");
            pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        } else if (pdfFile.toString().contains(".txt")) {
            // Text file
            pdfIntent.setDataAndType(path, "text/plain");
            pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        try{
            startActivity(pdfIntent);
        }catch(ActivityNotFoundException e){
            Toast.makeText(Main.this, "Install PDF reader.", Toast.LENGTH_SHORT).show();
        }
    }
    private class DownloadFile extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            String fileUrl = strings[0];   // path+filename
            String fileName = strings[1];  // filename
            String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
            File folder = new File(extStorageDirectory, "icp");
            folder.mkdir();

            File pdfFile = new File(folder, fileName);

            try{
                pdfFile.createNewFile();
            }catch (IOException e){
                e.printStackTrace();
            }
            Downloader.downloadFile(fileUrl, pdfFile);
            return null;
        }
    }

}
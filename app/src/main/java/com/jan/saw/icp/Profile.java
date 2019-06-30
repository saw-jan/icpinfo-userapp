package com.jan.saw.icp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Profile extends AppCompatActivity{

    private Toolbar toolbar;
    public static final String PREFS = "prefs";
    RequestQueue requestQueue;
    private EditText old_pass, new_pass;
    ProgressDialog progressDialog;
    private UrlFetch path = new UrlFetch();
    String std_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        old_pass = findViewById(R.id.o_pass);
        new_pass = findViewById(R.id.n_pass);

        SharedPreferences prefs = getSharedPreferences(PREFS,0);
        TextView f_letter = findViewById(R.id.f_letter);
        TextView name = findViewById(R.id.name);
        TextView id = findViewById(R.id.sid);
        TextView mail = findViewById(R.id.smail);
        TextView fac = findViewById(R.id.sfac);

        char fL = prefs.getString("name","").charAt(0);
        f_letter.setText(String.valueOf(fL));
        name.setText(prefs.getString("name",""));
        id.setText(prefs.getString("id",""));
        mail.setText(prefs.getString("mail",""));
        fac.setText(prefs.getString("faculty",""));
        std_id = prefs.getString("id","");

        toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Profile Setting");

        progressDialog = new ProgressDialog(Profile.this);
        requestQueue = Volley.newRequestQueue(getApplicationContext());
    }
    public void onBackPressed() {
            Intent back = new Intent(this,Main.class);
            startActivity(back);
            finish();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menutoolbar, menu);
        return true;
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_menu_done:
                //check nulls
                if (old_pass.getText().toString().trim().length()<1 || new_pass.getText().toString().trim().length()<1) {
                    Toast.makeText(Profile.this, "Empty Field(s)", Toast.LENGTH_LONG).show();
                } else {
                        if(new_pass.getText().toString().trim().length()>=8) {
                        progressDialog.setMessage("Updating...");
                        progressDialog.show();
                        StringRequest request = new StringRequest(Request.Method.POST, path.update,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        if (response == "Updated") {
                                            progressDialog.dismiss();
                                            Toast.makeText(Profile.this, "Updated", Toast.LENGTH_LONG).show();
                                            SharedPreferences prefs = getSharedPreferences(PREFS, Profile.this.MODE_PRIVATE);
                                            SharedPreferences.Editor editor = prefs.edit();
                                            editor.clear();
                                            editor.commit();
                                            finish();
                                            Intent calLogin = new Intent(Profile.this, Login.class);
                                            startActivity(calLogin);
                                            finish();
                                        } else {
                                            progressDialog.dismiss();
                                            Toast.makeText(Profile.this, response, Toast.LENGTH_LONG).show();
                                        }
                                    }

                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                progressDialog.dismiss();
                                Toast.makeText(Profile.this, error.toString(), Toast.LENGTH_LONG).show();

                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() {
                                Map<String, String> params = new HashMap<String, String>();
                                params.put("Student_ID", std_id.toString());
                                params.put("Old_Password", old_pass.getText().toString());
                                params.put("New_Password", new_pass.getText().toString());
                                return params;
                            }
                        };
                        RequestQueue requestQueue = Volley.newRequestQueue(Profile.this);
                        requestQueue.add(request);
                        old_pass.setText("");
                        new_pass.setText("");
                    }else{
                        Toast.makeText(this,"Password must be atleast 8 characters.",Toast.LENGTH_LONG).show();
                    }
                }
                break;
            }
    return super.onOptionsItemSelected(item);
}
}
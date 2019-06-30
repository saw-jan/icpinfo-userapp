package com.jan.saw.icp;

import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class AddPost extends AppCompatActivity {

    public static final String PREFS = "prefs";
    private UrlFetch path = new UrlFetch();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Post");

        final EditText ttl = findViewById(R.id.ttl);
        final EditText cont = findViewById(R.id.content);
        Button btn = findViewById(R.id.post);
        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (ttl.getText().toString().trim().length() > 0 || cont.getText().toString().trim().length() > 0) {
                StringRequest request = new StringRequest(Request.Method.POST, path.addpost,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Toast.makeText(getBaseContext(), "Done", Toast.LENGTH_SHORT).show();
                                Intent back = new Intent(getApplication(), Main.class);
                                startActivity(back);
                                finish();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(AddPost.this, "Connection Error", Toast.LENGTH_LONG).show();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("Title", ttl.getText().toString());
                        params.put("Content", cont.getText().toString());
                        SharedPreferences prefs = getSharedPreferences(PREFS, 0);
                        params.put("Post_by", prefs.getString("name", ""));
                        return params;
                    }
                };
                RequestQueue requestQueue2 = Volley.newRequestQueue(AddPost.this);
                requestQueue2.add(request);
            }else{
                    Toast.makeText(getBaseContext(), "Empty Fields.", Toast.LENGTH_SHORT).show();
            }
            }
        });
    }

    public void onBackPressed() {
        FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
        } else {
            Intent back = new Intent(this,Main.class);
            startActivity(back);
            finish();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.menutoolbar, menu);
        return true;
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}

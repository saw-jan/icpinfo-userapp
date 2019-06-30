package com.jan.saw.icp;

import android.app.ProgressDialog;
import android.app.VoiceInteractor;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity{

    EditText studentid, pass;
    Button login;
    RequestQueue requestQueue;
    ProgressDialog progressDialog;
    String EmailHolder, PasswordHolder;
    Boolean CheckEditText;
    public static final String PREFS = "prefs";
    TextView text;
    //Url class instance
    UrlFetch path = new UrlFetch();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        text = findViewById(R.id.txt);
        SharedPreferences prefs = getSharedPreferences(PREFS,0);
        text.setText(prefs.getString("name",""));
        if(prefs.getString("name","")!=""){
            Intent mainIntent = new Intent(Login.this, Main.class);
            startActivity(mainIntent);
            finish();
        }
        else {
            studentid = findViewById(R.id.txtName);
            pass = findViewById(R.id.txtPass);
            login = findViewById(R.id.btn_login);
            progressDialog = new ProgressDialog(Login.this);
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }

    }
    public void logIn(android.view.View v){
        CheckEditTextIsEmptyOrNot();
        if (CheckEditText) {

        progressDialog.setMessage("Checking...");
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, path.fetchData,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(!response.equals("")) {
                        try {
                            JSONObject jObj = new JSONObject(response);
                            JSONArray students = jObj.getJSONArray("student");
                            for (int a = 0; a < students.length(); a++) {
                                JSONObject student = students.getJSONObject(a);
                                String id = student.getString("Student_ID");
                                String name = student.getString("Name");
                                String password = student.getString("Password");
                                String mail= student.getString("Email");
                                String gender = student.getString("Gender");
                                String faculty = student.getString("Faculty");

                                //sessions
                                SharedPreferences prefs = getSharedPreferences(PREFS,0);
                                SharedPreferences.Editor edit = prefs.edit();
                                edit.putString("name",name);
                                edit.putString("id",id);
                                edit.putString("gender",gender);
                                edit.putString("faculty",faculty);
                                edit.putString("mail",mail);
                                edit.commit();

                                progressDialog.dismiss();
                                //call dashboard main.
                                Intent call = new Intent(Login.this,Main.class);
                                startActivity(call);
                                finish();
                            }
                        }
                        catch (JSONException e){
                            e.printStackTrace();
                        }
                        }
                        else{
                            progressDialog.dismiss();
                            Toast.makeText(Login.this, "Invalid Username or Password. Please Try Again !", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(Login.this, "No Internet Connection", Toast.LENGTH_LONG).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<String, String>();
                params.put("Student_ID",studentid.getText().toString());
                params.put("Password",pass.getText().toString());
                return params;
            }
        };
            RequestQueue requestQueue = Volley.newRequestQueue(Login.this);
            requestQueue.add(request);
        } else {
            Toast.makeText(Login.this, "Some fields are empty.", Toast.LENGTH_LONG).show();
        }
    }

    public void CheckEditTextIsEmptyOrNot() {

        EmailHolder = studentid.getText().toString().trim();
        PasswordHolder = pass.getText().toString().trim();

        if (TextUtils.isEmpty(EmailHolder) || TextUtils.isEmpty(PasswordHolder)) {
            CheckEditText = false;
        } else {
            CheckEditText = true;
        }
    }
}

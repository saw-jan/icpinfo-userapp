package com.jan.saw.icp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Objects;

public class Notice extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public Notice() {
        // Required empty public constructor
    }

    public static Notice newInstance(String param1, String param2) {
        Notice fragment = new Notice();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    //RequestQueue requestQueue;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    public static final String PREFS = "prefs";
    //Notice click event here.
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ProgressDialog progressDialog;
        UrlFetch path= new UrlFetch();
        final SharedPreferences prefs = Objects.requireNonNull(getActivity()).getSharedPreferences(PREFS,0);
        //String notice="http://192.168.1.12/icp/fetchers/fetchNotice.php";

        ScrollView scroll = new ScrollView(getContext());
        scroll.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
        //dynamic layout creation

            final LinearLayout wrap = new LinearLayout(getContext());
            wrap.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            wrap.setOrientation(LinearLayout.VERTICAL);
            wrap.setId(R.id.contain);

            scroll.addView(wrap);

            //fetching data from server
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Checking...");
        progressDialog.show();

        if(isNetAccess()){
            progressDialog.dismiss();
            final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                    path.notice, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    if (response != null) {
                        try {
                            JSONArray notices = response.getJSONArray("notices");
                            for (int a = 0; a < notices.length(); a++) {
                                JSONObject noti = notices.getJSONObject(a);
                                String title = noti.getString("Title");
                                String bdy = noti.getString("Body");
                                String file = noti.getString("File");
                                String date = noti.getString("PDate");

                                //storing array in shared prefs
                                SharedPreferences.Editor edit = prefs.edit();
                                edit.remove("n_title_"+a);
                                edit.remove("n_body_"+a);
                                edit.remove("n_file_"+a);
                                edit.putString("n_title_"+a,title);
                                edit.putString("n_body_"+a,bdy);
                                edit.putString("n_file_"+a,file);
                                edit.putString("n_date_"+a,date);
                                edit.apply();

                                final LinearLayout listCont = new LinearLayout(getContext());
                                LinearLayout.LayoutParams p0 =  new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,200);
                                p0.setMargins(10, 10, 10, 0);
                                listCont.setLayoutParams(p0);
                                listCont.setPadding(30, 30, 30, 30);
                                listCont.setOrientation(LinearLayout.VERTICAL);
                                listCont.setBackgroundResource(R.drawable.click_action);
                                listCont.setClickable(true);
                                listCont.setFocusable(true);
                                //listCont.setId(R.id.l1);
                                final int[] pass = {0};
                                listCont.setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        if(pass[0] == 0){
                                            //Toast.makeText(getActivity(), "Clicked", Toast.LENGTH_SHORT).show();
                                            LinearLayout.LayoutParams p0 =  new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                            p0.setMargins(10, 10, 10, 0);
                                            listCont.setLayoutParams(p0);
                                            listCont.setPadding(30, 30, 30, 30);
                                            listCont.setOrientation(LinearLayout.VERTICAL);
                                            pass[0]++;
                                        }
                                        else{
                                            LinearLayout.LayoutParams p0 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 200);
                                            p0.setMargins(10, 10, 10, 0);
                                            listCont.setLayoutParams(p0);
                                            listCont.setPadding(30, 30, 30, 30);
                                            listCont.setOrientation(LinearLayout.VERTICAL);
                                            pass[0]--;
                                        }
                                    }
                                });
                                wrap.addView(listCont);

                                TextView headTxt = new TextView(getContext());
                                headTxt.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                                headTxt.setText(prefs.getString("n_title_"+a,""));
                                headTxt.setTextSize(20);
                                headTxt.setTypeface(Typeface.DEFAULT_BOLD);
                                TextView dt = new TextView(getContext());
                                dt.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                                dt.setText(prefs.getString("n_date_"+a,""));
                                dt.setTextSize(10);
                                TextView body = new TextView(getContext());
                                body.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                                body.setText(prefs.getString("n_body_"+a,""));
                                body.setTextSize(14);

                                TextView txt = new TextView(getContext());
                                LinearLayout.LayoutParams p3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                txt.getLayoutParams();
                                p3.setMargins(0,20,0,0);
                                txt.setLayoutParams(p3);
                                txt.setTextSize(12);
                                //listCont.setPadding(0, 0, 0, 0);
                                txt.setText(prefs.getString("n_file_"+a,""));
                                txt.setTextColor(Color.BLUE);
                                final String filename = prefs.getString("n_file_"+a,"");
                                txt.setOnClickListener(new View.OnClickListener(){
                                    @Override
                                    public void onClick(View v) {
                                        ((Main)getActivity()).download(getView(),filename);
                                        ((Main)getActivity()).view(getView(),filename);
                                    }
                                });

                                listCont.addView(headTxt);
                                listCont.addView(dt);
                                listCont.addView(body);
                                listCont.addView(txt);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    else{
                        Toast.makeText(getActivity(),"No detail",Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            });
            RequestQueue requestQueue = Volley.newRequestQueue(Objects.requireNonNull(getActivity()));
            requestQueue.add(jsonObjectRequest);
        }
        else{

            //SharedPreferences prefM = PreferenceManager.getDefaultSharedPreferences(getContext());
            progressDialog.dismiss();
            int size = 0;
            if(prefs.getString("n_body_0","")!=""){
                size++;
                if(prefs.getString("n_body_1","")!=""){
                    size++;
                    if(prefs.getString("n_body_2","")!=""){
                        size++;
                        if(prefs.getString("n_body_3","")!=""){
                            size++;
                            if(prefs.getString("n_body_4","")!=""){
                                size++;
                                if(prefs.getString("n_body_5","")!=""){
                                    size++;
                                    if(prefs.getString("n_body_6","")!=""){
                                        size++;
                                        if(prefs.getString("n_body_7","")!=""){
                                            size++;
                                            if(prefs.getString("n_body_8","")!=""){
                                                size++;
                                                if(prefs.getString("n_body_9","")!=""){
                                                    size++;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            //Toast.makeText(getActivity(),String.valueOf(size),Toast.LENGTH_LONG).show();
            for(int a=0;a<size;a++){
                final LinearLayout listCont = new LinearLayout(getContext());
                LinearLayout.LayoutParams p0 =  new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 200);
                p0.setMargins(10, 10, 10, 0);
                listCont.setLayoutParams(p0);
                listCont.setPadding(30, 30, 30, 30);
                listCont.setOrientation(LinearLayout.VERTICAL);
                listCont.setBackgroundResource(R.drawable.click_action);
                listCont.setClickable(true);
                listCont.setFocusable(true);
                //listCont.setId(R.id.l1);
                final int[] pass = {0};
                listCont.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if(pass[0] == 0){
                            //Toast.makeText(getActivity(), "Clicked", Toast.LENGTH_SHORT).show();
                            LinearLayout.LayoutParams p0 =  new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            p0.setMargins(10, 10, 10, 0);
                            listCont.setLayoutParams(p0);
                            listCont.setPadding(30, 30, 30, 30);
                            listCont.setOrientation(LinearLayout.VERTICAL);
                            pass[0]++;
                        }
                        else{
                            LinearLayout.LayoutParams p0 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 200);
                            p0.setMargins(10, 10, 10, 0);
                            listCont.setLayoutParams(p0);
                            listCont.setPadding(30, 30, 30, 30);
                            listCont.setOrientation(LinearLayout.VERTICAL);
                            pass[0]--;
                        }
                    }
                });
                wrap.addView(listCont);

                TextView headTxt = new TextView(getContext());
                headTxt.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                headTxt.setText(prefs.getString("n_title_"+a,""));
                headTxt.setTextSize(20);
                headTxt.setTypeface(Typeface.DEFAULT_BOLD);
                TextView dt = new TextView(getContext());
                dt.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                dt.setText(prefs.getString("n_date_"+a,""));
                dt.setTextSize(10);
                TextView body = new TextView(getContext());
                body.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                body.setText(prefs.getString("n_body_"+a,""));
                body.setTextSize(14);

                TextView txt = new TextView(getContext());
                LinearLayout.LayoutParams p3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                txt.getLayoutParams();
                p3.setMargins(0,20,0,0);
                txt.setLayoutParams(p3);
                txt.setTextSize(12);
                txt.setPadding(0, 0, 0, 0);
                txt.setText(prefs.getString("n_file_"+a,""));
                txt.setTextColor(Color.BLUE);
                final String filename = prefs.getString("n_file_"+a,"");
                txt.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        ((Main)getActivity()).download(getView(),filename);
                        ((Main)getActivity()).view(getView(),filename);
                    }
                });

                listCont.addView(headTxt);
                listCont.addView(dt);
                listCont.addView(body);
                listCont.addView(txt);
            }
        }
        return scroll;
    }
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
    //check internet connection
    public boolean isNetAccess() {
        try {
            ConnectivityManager cm = (ConnectivityManager) Objects.requireNonNull(getActivity()).getSystemService(Context.CONNECTIVITY_SERVICE);
            return Objects.requireNonNull(cm).getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();

        } catch (Exception e) {
            Toast.makeText(getActivity(),"No Internet Connection.",Toast.LENGTH_LONG).show();
            return false;
        }
    }

}

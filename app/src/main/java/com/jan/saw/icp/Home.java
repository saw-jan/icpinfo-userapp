package com.jan.saw.icp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
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
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Home.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Home#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Home extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public Home() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Home.
     */
    // TODO: Rename and change types and number of parameters
    public static Home newInstance(String param1, String param2) {
        Home fragment = new Home();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    public static final String PREFS = "prefs";
    private Bitmap bitmap;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ProgressDialog progressDialog;

        final UrlFetch path = new UrlFetch();
        final SharedPreferences prefs = getActivity().getSharedPreferences(PREFS,0);
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Checking...");
        progressDialog.show();

        //scroll view
        ScrollView scroll = new ScrollView(getContext());
        scroll.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
        scroll.setVerticalScrollBarEnabled(true);
        //Linear 1
        final LinearLayout T_contain = new LinearLayout(getContext());
        LinearLayout.LayoutParams p0 =  new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        T_contain.setLayoutParams(p0);
        T_contain.setOrientation(LinearLayout.VERTICAL);
        scroll.addView(T_contain);
        //Large Image layout
        final ImageView l_img = new ImageView(getContext());
        LinearLayout.LayoutParams p1 =  new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,400);
        l_img.setLayoutParams(p1);
        l_img.setImageResource(R.drawable.bann);
        T_contain.addView(l_img);
        //Linear sub1
        final LinearLayout sub1 = new LinearLayout(getContext());
        LinearLayout.LayoutParams p2 =  new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        p2.setMargins(0,10,0,10);
        p2.setMarginStart(10);
        p2.setMarginEnd(10);
        sub1.setPadding(10,10,10,10);
        sub1.setLayoutParams(p2);
        sub1.setOrientation(LinearLayout.VERTICAL);
        sub1.setBackgroundResource(R.drawable.round_border_small);
        T_contain.addView(sub1);
        //Text title
        TextView txtHead1 = new TextView(getContext());
        LinearLayout.LayoutParams p3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        p3.setMargins(0,0,0,10);
        txtHead1.setLayoutParams(p3);
        txtHead1.setPadding(10,10,10,0);
        txtHead1.setTextSize(16);
        txtHead1.setText("Lecturers");
        txtHead1.setTypeface(Typeface.DEFAULT_BOLD);
        //HR scroll
        HorizontalScrollView hr1 = new HorizontalScrollView(getContext());
        LinearLayout.LayoutParams p4 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        hr1.setLayoutParams(p4);
        hr1.setHorizontalScrollBarEnabled(false);
        sub1.addView(txtHead1);
        sub1.addView(hr1);
        //Linear hr
        final LinearLayout hr_cont= new LinearLayout(getContext());
        LinearLayout.LayoutParams p5 =  new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        hr_cont.setLayoutParams(p4);
        hr_cont.setOrientation(LinearLayout.HORIZONTAL);
        hr1.addView(hr_cont);

        //Dynamic from here.
        if(isNetAccess()) {
            progressDialog.dismiss();
            final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                    path.lecs, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    if (response != null) {
                        try {
                            JSONArray notices = response.getJSONArray("lecturers");
                            for (int a = 0; a < notices.length(); a++) {
                                JSONObject noti = notices.getJSONObject(a);
                                String pro_p = noti.getString("Profile");
                                String name = noti.getString("Name");
                                String module = noti.getString("Module");
                                String conc = noti.getString("Contact");
                                String mail = noti.getString("Email");
                                //linear detail
                                final LinearLayout listCont = new LinearLayout(getContext());
                                LinearLayout.LayoutParams p6 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                p6.setMargins(0,0,20,0);
                                listCont.setLayoutParams(p6);
                                listCont.setOrientation(LinearLayout.VERTICAL);
                                listCont.setPadding(10, 10, 10, 10);
                                hr_cont.addView(listCont);
                                //profile picture
                                final ImageView pp = new ImageView(getContext());
                                LinearLayout.LayoutParams p7 = new LinearLayout.LayoutParams(100, 100);
                                p7.gravity= Gravity.CENTER;
                                pp.setLayoutParams(p7);
                                if(pro_p.length()>0){
                                    //if(getImage(pro_p) != null) {
                                    String url =path.imgpath+pro_p;
                                    Picasso.get().load(url).resize(100,100).into(pp);

                                }else {
                                    pp.setImageResource(R.drawable.ic_logo);
                                }

                                //text view  name
                                final TextView nam = new TextView(getContext());
                                LinearLayout.LayoutParams p8 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                p8.gravity= Gravity.CENTER;
                                nam.setLayoutParams(p8);
                                nam.setText(name);
                                nam.setTextSize(14);
                                nam.setTypeface(Typeface.DEFAULT_BOLD);
                                //Other texts
                                final TextView detail = new TextView(getContext());
                                LinearLayout.LayoutParams p9 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                p9.gravity= Gravity.CENTER;
                                detail.setLayoutParams(p9);
                                detail.setText(module);
                                detail.setTextSize(12);
                                final TextView detail2 = new TextView(getContext());
                                LinearLayout.LayoutParams px1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                px1.gravity= Gravity.CENTER;
                                detail2.setLayoutParams(px1);
                                detail2.setText(conc);
                                detail2.setTextSize(12);
                                final TextView detail3 = new TextView(getContext());
                                LinearLayout.LayoutParams px2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                px2.gravity= Gravity.CENTER;
                                detail3.setLayoutParams(px2);
                                detail3.setText(mail);
                                detail3.setTextSize(12);
                                //divider
                                listCont.addView(pp);
                                listCont.addView(nam);
                                listCont.addView(detail);
                                listCont.addView(detail2);
                                listCont.addView(detail3);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(getActivity(), "No detail", Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            });
            RequestQueue requestQueue = Volley.newRequestQueue(Objects.requireNonNull(getActivity()));
            requestQueue.add(jsonObjectRequest);
        }else{
            progressDialog.dismiss();
            TextView msg = new TextView(getContext());
            LinearLayout.LayoutParams pxy = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            pxy.gravity= Gravity.CENTER;
            msg.setLayoutParams(pxy);
            msg.setText("No Connection...");
            msg.setTextSize(18);
            msg.setPadding(20,20,20,20);
            msg.setTypeface(Typeface.DEFAULT_BOLD);
            hr_cont.addView(msg);
        }

        //Linear for staff, sub2
        final LinearLayout sub2 = new LinearLayout(getContext());
        LinearLayout.LayoutParams p10 =  new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        p10.setMargins(0,10,0,10);
        p10.setMarginStart(10);
        p10.setMarginEnd(10);
        sub2.setLayoutParams(p10);
        sub2.setPadding(10,10,10,10);
        sub2.setOrientation(LinearLayout.VERTICAL);
        sub2.setBackgroundResource(R.drawable.round_border_small);
        T_contain.addView(sub2);
        //Text title
        TextView txtHead2 = new TextView(getContext());
        LinearLayout.LayoutParams p11 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        p11.setMargins(0,0,0,10);
        txtHead2.setLayoutParams(p11);
        txtHead2.setPadding(10,10,10,0);
        txtHead2.setTextSize(16);
        txtHead2.setText("Important Personnel");
        txtHead2.setTypeface(Typeface.DEFAULT_BOLD);
        //HR scroll
        HorizontalScrollView hr2 = new HorizontalScrollView(getContext());
        LinearLayout.LayoutParams p12 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        hr2.setLayoutParams(p12);
        hr2.setHorizontalScrollBarEnabled(false);
        sub2.addView(txtHead2);
        sub2.addView(hr2);
        //Linear hr
        final LinearLayout hr_cont2= new LinearLayout(getContext());
        LinearLayout.LayoutParams p13 =  new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        hr_cont2.setLayoutParams(p13);
        hr_cont2.setOrientation(LinearLayout.HORIZONTAL);
        hr2.addView(hr_cont2);

        //Dynamic from here
        if(isNetAccess()) {
            progressDialog.dismiss();
        final JsonObjectRequest jsonObjectRequest2 = new JsonObjectRequest(Request.Method.GET,
                path.staffs, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                if (response != null) {
                    try {
                        JSONArray notices = response.getJSONArray("staffs");
                        for (int a = 0; a < notices.length(); a++) {
                            JSONObject noti = notices.getJSONObject(a);
                            String pro_p = noti.getString("Profile");
                            String name = noti.getString("Name");
                            String conc = noti.getString("Contact");
                            String mail = noti.getString("Email");
        //linear detail
        final LinearLayout listCont2 = new LinearLayout(getContext());
        LinearLayout.LayoutParams p14 =  new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        p14.setMargins(10,0,20,0);
        listCont2.setLayoutParams(p14);
        listCont2.setOrientation(LinearLayout.VERTICAL);
        listCont2.setPadding(10,10,10,10);
        hr_cont2.addView(listCont2);
        //profile picture
        final ImageView pp2 = new ImageView(getContext());
        LinearLayout.LayoutParams p15 =  new LinearLayout.LayoutParams(100,100);
        p15.gravity= Gravity.CENTER;
        pp2.setLayoutParams(p15);
                            if(pro_p.length()>0){
                                String url =path.imgpath+pro_p;
                                Picasso.get().load(url).resize(100,100).into(pp2);

                            }else {
                                pp2.setBackgroundResource(R.drawable.icp_logo);
                            }
        //text view  name
        final TextView nam2 = new TextView(getContext());
        LinearLayout.LayoutParams p16 =  new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        p16.gravity= Gravity.CENTER;
        nam2.setLayoutParams(p16);
        nam2.setText(name);
        nam2.setTextSize(14);
        nam2.setTypeface(Typeface.DEFAULT_BOLD);
        //Other texts
        final TextView detail2 = new TextView(getContext());
        LinearLayout.LayoutParams p17 =  new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        p17.gravity= Gravity.CENTER;
        detail2.setLayoutParams(p17);
        detail2.setText(conc);
        detail2.setTextSize(12);
                            final TextView detail2x = new TextView(getContext());
                            LinearLayout.LayoutParams p17x =  new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                            p17x.gravity= Gravity.CENTER;
                            detail2x.setLayoutParams(p17x);
                            detail2x.setText(mail);
                            detail2x.setTextSize(12);
        listCont2.addView(pp2);
        listCont2.addView(nam2);
        listCont2.addView(detail2);
        listCont2.addView(detail2x);
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
        RequestQueue requestQueue2 = Volley.newRequestQueue(Objects.requireNonNull(getActivity()));
        requestQueue2.add(jsonObjectRequest2);
        }else{
            progressDialog.dismiss();
            TextView msg = new TextView(getContext());
            LinearLayout.LayoutParams pxy = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            pxy.gravity= Gravity.CENTER;
            msg.setLayoutParams(pxy);
            msg.setText("No Connection...");
            msg.setTextSize(18);
            msg.setPadding(20,20,20,20);
            msg.setTypeface(Typeface.DEFAULT_BOLD);
            hr_cont2.addView(msg);
            //Toast.makeText(getActivity(),"No internet connection",Toast.LENGTH_LONG).show();
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
    public Bitmap getImage(String lnk){
        try{
            URL url = new URL(lnk);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap pro = BitmapFactory.decodeStream(input);
            return pro;
        }
        catch (Exception e) {
            e.printStackTrace();
            return  null;
        }
    }
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
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

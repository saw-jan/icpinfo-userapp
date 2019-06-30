package com.jan.saw.icp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Post extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public Post() {
        // Required empty public constructor
    }
    public static Post newInstance(String param1, String param2) {
        Post fragment = new Post();
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
    UrlFetch path = new UrlFetch();
    private String lc = "";
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_post, container, false);
        RelativeLayout rl = v.findViewById(R.id.parent);
        ScrollView scroll = new ScrollView(getContext());
        scroll.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        //dynamic layout creation
        rl.addView(scroll);

        final LinearLayout wrap = new LinearLayout(getContext());
        wrap.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        wrap.setOrientation(LinearLayout.VERTICAL);
        wrap.setId(R.id.contain);

        scroll.addView(wrap);

        if (isNetAccess()) {
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                path.post, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (response != null) {
                    try {
                        JSONArray notices = response.getJSONArray("posts");
                        for (int a = 0; a < notices.length(); a++) {
                            JSONObject noti = notices.getJSONObject(a);
                            final String id = noti.getString("ID");
                            String title = noti.getString("Title");
                            String bdy = noti.getString("Content");
                            String by = noti.getString("PostBy");
                            String date = noti.getString("PDate");

                            final LinearLayout[] listCont = {new LinearLayout(getContext())};
                            LinearLayout.LayoutParams paras = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 500);
                            paras.setMargins(20, 20, 20, 0);
                            listCont[0].setLayoutParams(paras);
                            listCont[0].setOrientation(LinearLayout.VERTICAL);
                            listCont[0].setBackgroundResource(R.drawable.click_action);
                            listCont[0].setPadding(30, 30, 30, 30);
                            listCont[0].setClickable(true);
                            listCont[0].setFocusable(true);
                            //listCont.setId(R.id.l1);
                            wrap.addView(listCont[0]);

                            final TextView headTxt = new TextView(getContext());
                            headTxt.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 50));
                            headTxt.setText(title);
                            headTxt.setTextSize(20);
                            headTxt.setTypeface(Typeface.DEFAULT_BOLD);
                            TextView dt = new TextView(getContext());
                            dt.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                            dt.setText(date);
                            dt.setTextSize(10);
                            TextView pby = new TextView(getContext());
                            pby.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                            String byName = "by - "+by;
                            pby.setText(byName);
                            pby.setTextSize(10);

                            final TextView body = new TextView(getContext());
                            body.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 100));
                            body.setText(bdy);
                            body.setTextSize(14);
                            //Interaction
                            final LinearLayout foot = new LinearLayout(getContext());
                            LinearLayout.LayoutParams px = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 100);
                            px.setMargins(20, 50, 20, 0);
                            foot.setLayoutParams(px);
                            foot.setOrientation(LinearLayout.HORIZONTAL);
                            //Like
                            final ImageView like = new ImageView(getContext());
                            LinearLayout.LayoutParams pb = new LinearLayout.LayoutParams(40, 50);
                            pb.setMargins(0, 0, 10, 0);
                            like.setLayoutParams(pb);
                            //like count
                            final TextView count = new TextView(getContext());
                            LinearLayout.LayoutParams pc = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            pc.setMargins(0, 0, 20, 0);
                            like.setLayoutParams(pc);
                            count.setTextSize(14);
                            count.setTextColor(Color.BLUE);
                            //get likes count
                            StringRequest request = new StringRequest(Request.Method.POST, path.likeCount,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            if(!response.equals("0")){
                                                //Toast.makeText(getContext(), "counted", Toast.LENGTH_SHORT).show();
                                                count.setText(response);
                                            }else{
                                                count.setText("0");
                                            }
                                        }
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();

                                }
                            }) {
                                @Override
                                protected Map<String, String> getParams() {
                                    Map<String, String> params = new HashMap<String, String>();
                                    params.put("Post_ID", id);
                                    return params;
                                }
                            };
                            RequestQueue requestQueue12 = Volley.newRequestQueue(Objects.requireNonNull(getContext()));
                            requestQueue12.add(request);
                            //if liked
                            final int[] pass = {0};
                            StringRequest req = new StringRequest(Request.Method.POST, path.islike,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String resp) {
                                            SharedPreferences prefs = Objects.requireNonNull(getActivity()).getSharedPreferences(PREFS, 0);
                                            if(resp.equals("1")){
                                                String s=id+prefs.getString("id", "");
                                                //Toast.makeText(getActivity(), resp, Toast.LENGTH_SHORT).show();
                                                //Toast.makeText(getContext(), "counted", Toast.LENGTH_SHORT).show();
                                                pass[0]=1;
                                                like.setImageResource(R.drawable.ic_like);
                                                lc="1";
                                            }else{
                                                pass[0]=0;
                                                like.setImageResource(R.drawable.ic_dislike);
                                                lc="0";
                                            }

                                        }
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
                                }
                            }) {
                                @Override
                                protected Map<String, String> getParams() {
                                    Map<String, String> params = new HashMap<String, String>();
                                    params.put("Post_ID", id);
                                    SharedPreferences prefs = Objects.requireNonNull(getActivity()).getSharedPreferences(PREFS, 0);
                                    params.put("Student", prefs.getString("id", ""));
                                    return params;
                                }
                            };
                            RequestQueue requestQ = Volley.newRequestQueue(getContext());
                            requestQ.add(req);

                            //add-delete like
                            like.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (pass[0] == 0) {
                                        like.setImageResource(R.drawable.ic_like);
                                        StringRequest request = new StringRequest(Request.Method.POST, path.likes,
                                                new Response.Listener<String>() {
                                                    @Override
                                                    public void onResponse(String response) {
                                                        count.setText(response);
                                                    }
                                                }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_LONG).show();

                                            }
                                        }) {
                                            @Override
                                            protected Map<String, String> getParams() {
                                                Map<String, String> params = new HashMap<String, String>();
                                                params.put("Post_ID", id);
                                                SharedPreferences prefs = Objects.requireNonNull(getActivity()).getSharedPreferences(PREFS, 0);
                                                params.put("Student", prefs.getString("id", ""));
                                                return params;
                                            }
                                        };
                                        RequestQueue requestQueue2 = Volley.newRequestQueue(getContext());
                                        requestQueue2.add(request);
                                        pass[0]++;
                                    } else {
                                        like.setImageResource(R.drawable.ic_dislike);
                                        StringRequest request = new StringRequest(Request.Method.POST, path.dislike,
                                                new Response.Listener<String>() {
                                                    @Override
                                                    public void onResponse(String response) {
                                                        count.setText(response);
                                                    }
                                                }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_LONG).show();

                                            }
                                        }) {
                                            @Override
                                            protected Map<String, String> getParams() {
                                                Map<String, String> params = new HashMap<String, String>();
                                                params.put("Post_ID", id);
                                                SharedPreferences prefs = Objects.requireNonNull(getActivity()).getSharedPreferences(PREFS, 0);
                                                params.put("Student", prefs.getString("id", ""));
                                                return params;
                                            }
                                        };
                                        RequestQueue requestQueue3 = Volley.newRequestQueue(getContext());
                                        requestQueue3.add(request);
                                        pass[0]--;
                                    }
                                }
                            });
                            LinearLayout cm = new LinearLayout(getContext());
                            LinearLayout.LayoutParams p00 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT );
                            cm.setLayoutParams(p00);
                            cm.setOrientation(LinearLayout.HORIZONTAL);
                            //comment
                            final EditText cmt = new EditText(getContext());
                            LinearLayout.LayoutParams pm = new LinearLayout.LayoutParams(500, 50);
                            pc.gravity = Gravity.RIGHT;
                            pc.setMargins(0, 0, 0, 0);
                            cmt.setLayoutParams(pm);
                            cmt.setHint("comment");
                            cmt.setTextSize(12);
                            cmt.setBackgroundResource(R.drawable.round_border);
                            cmt.setPadding(10, 0, 10, 10);
                            final ImageView c_img= new ImageView(getContext());
                            LinearLayout.LayoutParams pcm = new LinearLayout.LayoutParams(60, 60);
                            pcm.setMargins(30, 0, 0, 30);
                            c_img.setLayoutParams(pcm);
                            c_img.setImageResource(R.drawable.ic_send);
                            c_img.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (cmt.getText().toString().trim().length() > 0) {
                                        StringRequest rqs = new StringRequest(Request.Method.POST, path.addComt,
                                                new Response.Listener<String>() {
                                                    @Override
                                                    public void onResponse(String response) {
                                                        cmt.setText(null);
                                                        Toast.makeText(getContext(), "Done", Toast.LENGTH_SHORT).show();

                                                    }
                                                }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                Toast.makeText(getContext(), error.toString(), Toast.LENGTH_LONG).show();

                                            }
                                        }) {
                                            @Override
                                            protected Map<String, String> getParams() {
                                                Map<String, String> params = new HashMap<String, String>();
                                                params.put("Post_ID", id);
                                                SharedPreferences prefs = getActivity().getSharedPreferences(PREFS, 0);
                                                params.put("Student", prefs.getString("id", ""));
                                                params.put("comment", cmt.getText().toString());
                                                return params;
                                            }
                                        };
                                        RequestQueue rq2 = Volley.newRequestQueue(getContext());
                                        rq2.add(rqs);
                                    }else{
                                        Toast.makeText(getContext(),"Empty Comment",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            foot.addView(like);
                            foot.addView(count);
                            cm.addView(cmt);
                            cm.addView(c_img);
                            //foot.addView(cmt);
                            final int[] click = {0};
                            //show comment click
                            final LinearLayout cm101 = new LinearLayout(getContext());
                            LinearLayout.LayoutParams p101 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                            p101.setMargins(5, 10, 5, 5);
                            cm101.setLayoutParams(p101);
                            cm101.setPadding(20,20,20,20);
                            cm101.setBackgroundColor(Color.LTGRAY);
                            cm101.setOrientation(LinearLayout.VERTICAL);
                            TextView cTopic = new TextView(getContext());
                            cTopic.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                            String place ="COMMENTS";
                            cTopic.setText(place);
                            cTopic.setPadding(0,0,0,20);
                            cTopic.setTextSize(14);
                            cm101.addView(cTopic);

                            StringRequest reqst = new StringRequest(Request.Method.POST, path.comment,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            if(!response.equals("")) {
                                                try {
                                                    JSONObject jObj = new JSONObject(response);
                                                    JSONArray comments = jObj.getJSONArray("comments");
                                                    for (int a = 0; a < comments.length(); a++) {
                                                JSONObject cmt = comments.getJSONObject(a);
                                                String sid = cmt.getString("Student");
                                                String dat = cmt.getString("PDate");
                                                String comnt = cmt.getString("Comment");

                                                LinearLayout cm102 = new LinearLayout(getContext());
                                                LinearLayout.LayoutParams p102 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                                p102.setMargins(0, 5, 0, 0);
                                                cm102.setLayoutParams(p102);
                                                cm102.setBackgroundResource(R.drawable.click_action);
                                                cm102.setOrientation(LinearLayout.VERTICAL);
                                                cm102.setPadding(5, 5, 5, 5);
                                                TextView std_id = new TextView(getContext());
                                                std_id.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                                                std_id.setText(sid);
                                                std_id.setTextSize(16);
                                                std_id.setTypeface(Typeface.DEFAULT_BOLD);
                                                TextView de = new TextView(getContext());
                                                de.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                                                de.setText(dat);
                                                de.setTextSize(10);
                                                TextView com = new TextView(getContext());
                                                com.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                                                com.setText(comnt);
                                                com.setTextSize(12);
                                                cm102.addView(std_id);
                                                cm102.addView(de);
                                                cm102.addView(com);
                                                cm101.addView(cm102);
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                }
                            }) {
                                @Override
                                protected Map<String, String> getParams(){
                                    Map<String, String> params = new HashMap<String, String>();
                                    params.put("Post_ID",id);
                                    return params;
                                }
                            };
                            RequestQueue rQ = Volley.newRequestQueue(Objects.requireNonNull(getActivity()));
                            rQ.add(reqst);

                            listCont[0].addView(headTxt);
                            listCont[0].addView(dt);
                            listCont[0].addView(pby);
                            listCont[0].addView(body);
                            listCont[0].addView(foot);
                            listCont[0].addView(cm);
                            listCont[0].addView(cm101);
                            listCont[0].setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //Toast.makeText(getActivity(), "comments here", Toast.LENGTH_SHORT).show();

                                    if (click[0] == 0) {
                                    click[0]++;
                                        LinearLayout.LayoutParams paras = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                        paras.setMargins(20, 20, 20, 0);
                                        listCont[0].setLayoutParams(paras);
                                        listCont[0].setOrientation(LinearLayout.VERTICAL);
                                        LinearLayout.LayoutParams pHB = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                                        headTxt.setLayoutParams(pHB);
                                        body.setLayoutParams(pHB);
                                        LinearLayout.LayoutParams px = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 100);
                                        foot.setLayoutParams(px);
                                }else{
                                        click[0]--;
                                        LinearLayout.LayoutParams paras = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 450);
                                        paras.setMargins(20, 20, 20, 0);
                                        listCont[0].setLayoutParams(paras);
                                        listCont[0].setOrientation(LinearLayout.VERTICAL);
                                        LinearLayout.LayoutParams pH = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,50);
                                        headTxt.setLayoutParams(pH);
                                        LinearLayout.LayoutParams pB = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,100);
                                        body.setLayoutParams(pB);
                                        LinearLayout.LayoutParams px = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 100);
                                        foot.setLayoutParams(px);
                                }
                                }
                            });

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
            TextView msg = new TextView(getContext());
            LinearLayout.LayoutParams pxy = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            pxy.gravity= Gravity.CENTER_HORIZONTAL;
            pxy.gravity= Gravity.CENTER_VERTICAL;
            msg.setLayoutParams(pxy);
            String noCon= "No Connection...";
            msg.setText(noCon);
            msg.setTextSize(24);
            msg.setPadding(40,40,40,40);
            msg.setTypeface(Typeface.DEFAULT_BOLD);
            wrap.addView(msg);
    }

        FloatingActionButton add = new FloatingActionButton(Objects.requireNonNull(getContext()));
        RelativeLayout.LayoutParams para = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        para.addRule(RelativeLayout.ALIGN_PARENT_END);
        para.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        para.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        para.setMargins(0,0,30,30);
        add.setLayoutParams(para);
        add.setId(R.id.l1);
        add.setImageResource(R.drawable.ic_add);
        rl.addView(add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent post = new Intent(getContext(),AddPost.class);
                startActivity(post);
                getActivity().finish();
            }
        });

        return v;
    }
    // TODO: Rename method, update argument and hook method into UI event
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

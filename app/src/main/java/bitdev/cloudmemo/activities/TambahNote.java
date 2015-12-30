package bitdev.cloudmemo.activities;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;


import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.HashMap;


import bitdev.cloudmemo.R;
import bitdev.cloudmemo.apps.AppController;
import bitdev.cloudmemo.models.Note;
import bitdev.cloudmemo.utils.Conf;
import bitdev.cloudmemo.utils.SessionManager;

public class TambahNote extends MasterActivity {


    private Button btnSubmit;
    private TextView title,message;
    private Animation slide_down,slide_up;
    private RelativeLayout form ,loading;
    private TextView loading_text;
    private Note note;
    private SessionManager cache;
    private boolean cantInterupt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getLayoutInflater().inflate(R.layout.content_tambah_note, getMasterLayout(), true);

        note = (Note) getIntent().getParcelableExtra("data");
        title = (TextView) findViewById(R.id.input_title);
        message = (TextView) findViewById(R.id.input_message);
        slide_down = AnimationUtils.loadAnimation(this,R.anim.slide_down);
        slide_up = AnimationUtils.loadAnimation(this,R.anim.slide_up);
        btnSubmit = (Button) findViewById(R.id.btn_simpan_note);
        form = (RelativeLayout) findViewById(R.id.form_tambah_note);
        loading = (RelativeLayout) findViewById(R.id.loading_screen);
        loading_text = (TextView) findViewById(R.id.text_loading);
        cache = new SessionManager(this);
        setupLayout(cache);
        if(note != null ){
            title.setText(note.getTitle());
            message.setText(note.getMessage());
        }

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("debuggin", "i fire this method simpanNote");
                simpanNote(title.getText().toString(), message.getText().toString());

            }
        });

    }

    private void simpanNote(String title,String msg) {
        final HashMap<String,String>  data  = Conf.masterData(cache.getPref().getString(Conf.api_key,""));
        data.put("title", title);
        data.put("message", msg);
        form.setAnimation(slide_up);
        loading.setAnimation(slide_down);
        form.setVisibility(View.GONE);

        slide_up.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                loading.setVisibility(View.VISIBLE);
                loading_text.setText("Menyimpan Tulisan...");
                cantInterupt = true;
                makeRequest(data);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void makeRequest(final HashMap<String, String> data) {
        String url = Conf.masterURL+"note";
        JSONObject post = new JSONObject(data);
        int method = (note != null) ? Request.Method.PATCH : Request.Method.POST;
        JsonObjectRequest strReq = new JsonObjectRequest(method, url, post, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(final JSONObject response) {
               try {
                   if(response.getBoolean("error")){
                       form.setAnimation(slide_down);
                       loading.startAnimation(slide_up);
                       slide_up.setAnimationListener(new Animation.AnimationListener() {
                           @Override
                           public void onAnimationStart(Animation animation) {

                           }

                           @Override
                           public void onAnimationEnd(Animation animation) {
                               loading.setVisibility(View.GONE);
                               form.setVisibility(View.VISIBLE);
                           }

                           @Override
                           public void onAnimationRepeat(Animation animation) {

                           }
                       });
                       slide_down.setAnimationListener(new Animation.AnimationListener() {
                           @Override
                           public void onAnimationStart(Animation animation) {

                           }

                           @Override
                           public void onAnimationEnd(Animation animation) {

                               try {
                                   Toast.makeText(TambahNote.this, "Terjadi Kesalahan: "+response.getString("message"), Toast.LENGTH_SHORT).show();
                               } catch (JSONException e) {
                                   Log.d("TambahNote","debuggin",e);
                               }


                           }

                           @Override
                           public void onAnimationRepeat(Animation animation) {

                           }
                       });

                   }else{
                       Toast.makeText(TambahNote.this, "Tulisan Berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                       new Handler().postDelayed(new Runnable() {
                           @Override
                           public void run() {
                               cantInterupt = false;
                               makeIntentListNote();

                           }
                       },100);

                   }
               }catch (JSONException e){
                    Log.d("TambahNote","debuggin",e);
               }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(final VolleyError error) {
                form.setAnimation(slide_down);
                loading.startAnimation(slide_up);
                slide_up.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        loading.setVisibility(View.GONE);
                        form.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                slide_down.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        Toast.makeText(TambahNote.this, "Terjadi Kesalahan: "+error.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d("TambahNote","debuggin",error);

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

            }
        });
        Log.d("debugging", strReq.toString());
        AppController.getInstance().addToRequestQueue(strReq);
    }
    @Override
    public void onBackPressed() {
        if(cantInterupt){
            Toast.makeText(TambahNote.this, "Silahkan menunggu hingga selesai", Toast.LENGTH_SHORT).show();
        }
    }
    protected void makeIntentListNote(){
        startActivity(new Intent(TambahNote.this, Home.class));
    }
}

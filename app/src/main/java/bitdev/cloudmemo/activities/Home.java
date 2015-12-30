package bitdev.cloudmemo.activities;


import android.content.Intent;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;

import bitdev.cloudmemo.apps.AppController;
import bitdev.cloudmemo.models.Note;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import bitdev.cloudmemo.R;
import bitdev.cloudmemo.utils.Conf;
import bitdev.cloudmemo.utils.SessionManager;
import bitdev.cloudmemo.utils.SwipeListAdapter;

public class Home extends MasterActivity implements
        SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    private String TAG = Home.class.getSimpleName();
    private SessionManager cache;
    private String URL_FECTH;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView listView;
    private SwipeListAdapter adapter;
    private List<Note> noteList;
    private HashMap<String,String> data;
    private int lastDataResponse = 0;
    private Button btn_note;
    private ArrayList<Note> dataNote;
    private static Home mInstance;

    public Home setNote(Note n,int i){
        this.getInstaceArrayNote().add(i,n);
        return this;
    }
    private ArrayList<Note> getInstaceArrayNote(){
        if(dataNote == null){
            dataNote = new ArrayList<>();
        }
        return  dataNote;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInstance = this;
        setContentView(R.layout.activity_home);
        getLayoutInflater().inflate(R.layout.content_home, getMasterLayout(), true);
        if(getIntent().getBooleanExtra("notif_delete",false)){
            Toast.makeText(Home.this, "Tulisan berhasil di hapus", Toast.LENGTH_SHORT).show();
        }
        cache = new SessionManager(this);
        setupLayout(cache);
        data = Conf.masterData(cache.getPref().getString(Conf.api_key, ""));
        URL_FECTH = Conf.masterURL+"note";
        listView = (RecyclerView) findViewById(R.id.list);
        listView.setLayoutManager(new LinearLayoutManager(this));
        listView.setItemAnimator(new DefaultItemAnimator());
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);

        noteList = new ArrayList<>();
        adapter = new SwipeListAdapter(noteList,R.layout.list_row,this);
        listView.setAdapter(adapter);
        swipeRefreshLayout.setOnRefreshListener(this);

        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);

                                        fetchNote();
                                    }
                                }
        );
    }
    public static synchronized Home getInstance() {
        return mInstance;
    }
    private void viewNote(Note data) {
            Intent i = new Intent(getApplicationContext(),ViewNote.class);
            i.putExtra("data",data);
            startActivity(i);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.master_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        fetchNote();
    }

    private void fetchNote() {

        swipeRefreshLayout.setRefreshing(true);
        String url = URL_FECTH+"?api_token="+cache.getPref().getString(Conf.api_key,"");
        Log.d("test url",url);
        JsonArrayRequest req = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        try {
                            Log.d("debuggin", String.valueOf(response.length())+ "==" + lastDataResponse);
                            if(response.length() == lastDataResponse){
                                Toast.makeText(getApplicationContext(), "Tidak ada Tulisan terbaru", Toast.LENGTH_LONG).show();
                                swipeRefreshLayout.setRefreshing(false);
                                return;
                            }

                            lastDataResponse = response.length();
                            for (int x = 0; lastDataResponse > x; x++) {

                                JSONObject items = response.getJSONObject(x);
                                String title = items.getString("title");
                                String message = items.getString("message");
                                JSONArray tags = items.getJSONArray("tag");
                                String date = items.getString("updated_at");
                                int id = items.getInt("id");
                                Note m = new Note(title, message, tags, id,date);
                                noteList.add(x,m);
                            }
                            Collections.reverse(noteList);
                            adapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            Log.i(TAG, "JSON Parsing error:", e);
                        } catch (ClassCastException e) {
                            Log.d("TAG", "Trace", e);
                        }
                        // stopping swipe refresh
                        swipeRefreshLayout.setRefreshing(false);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Server Error: " + error.getMessage());
                // stopping swipe refresh
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(req);

    }
    @Override
    protected void makeIntentCreateNote(){
        startActivity(new Intent(Home.this, TambahNote.class));
    }

    @Override
    public void onClick(View v) {

        int id = Integer.parseInt(((TextView) v.findViewById(R.id.note_id)).getText().toString());
        viewNote(dataNote.get(id));
    }
}

package bitdev.cloudmemo.activities;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;


import com.alertdialogpro.AlertDialogPro;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.Request.Method;


import org.json.JSONException;
import org.json.JSONObject;

import bitdev.cloudmemo.R;
import bitdev.cloudmemo.apps.AppController;
import bitdev.cloudmemo.models.Note;
import bitdev.cloudmemo.utils.Conf;
import bitdev.cloudmemo.utils.SessionManager;

public class ViewNote extends MasterActivity {
    private Note note;
    private TextView icon;
    private String ACTION_URL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        note = getIntent().getParcelableExtra("data");
        setContentView(R.layout.activity_home);
        ACTION_URL = Conf.masterURL+"note/"+ note.getId();

        Typeface font = Typeface.createFromAsset(getAssets(),"fonts/fontawesomes.ttf");
        getLayoutInflater().inflate(R.layout.content_view, getMasterLayout(), true);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupLayout(new SessionManager(this));
        icon = (TextView) findViewById(R.id.icon_note_view);
        icon.setTypeface(font);
        ((TextView) findViewById(R.id.note_title)).setText(note.getTitle());
        ((TextView) findViewById(R.id.note_message)).setText(note.getMessage());
        ((TextView) findViewById(R.id.note_date)).setText(note.getDate());
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//         Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail_menu, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        switch (id)
        {
            case R.id.menu_delete:
                Log.d("debuggin", ACTION_URL);
                final JsonObjectRequest Request = new JsonObjectRequest(Method.DELETE, ACTION_URL, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            Log.d("debugging",response.toString());
                            if (response.getBoolean("error")) {
                                Toast.makeText(ViewNote.this, "Terjadi kesalah pada saat menghapus tulisan", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            Intent i = new Intent(ViewNote.this,Home.class);
                            i.putExtra("notif_delete",true);
                            startActivity(i);
                        } catch (JSONException e) {
                            Log.d("debuggin", "JSONEXception", e);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("debuggin","VolleyError",error);

                    }
                });
               new AlertDialogPro.Builder(this).setTitle("Konfirmasi Hapus").
                                                setMessage("Apakah anda yakin akan menghapus tulisan ini ?").
                                                setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        AppController.getInstance().addToRequestQueue(Request);
                                                    }
                                                }).
                                                setNegativeButton("Tidak", null).
                                                show();
                break;
            case R.id.menu_edit:
                startActivity(new Intent(ViewNote.this,TambahNote.class).putExtra("data",note));
                break;
        }
        return  super.onOptionsItemSelected(item);
    }
    protected void makeIntentListNote(){
        startActivity(new Intent(ViewNote.this, Home.class));
    }

    @Override
    protected void makeIntentCreateNote() {
        startActivity(new Intent(ViewNote.this,TambahNote.class));
    }
}

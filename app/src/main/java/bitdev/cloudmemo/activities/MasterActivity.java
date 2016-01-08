package bitdev.cloudmemo.activities;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import bitdev.cloudmemo.R;
import bitdev.cloudmemo.utils.SessionManager;

public class MasterActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    protected void setupLayout(SessionManager cache){

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
//        TextView nama_user = (TextView) findViewById(R.id.app_bar_home_view).findViewById(R.id.nama_user);
//        TextView email_uer = (TextView) findViewById(R.id.app_bar_home).findViewById(R.id.nama_user);
//        if(nama_user != null){
//            nama_user.setText(cache.getPref().getString("name",""));
//        }
//        if(email_uer != null){
//            email_uer.setText(cache.getPref().getString("email",""));
//        }
    }
    protected CoordinatorLayout getMasterLayout(){
        return (CoordinatorLayout) findViewById(R.id.master_layout);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_create) {
            makeIntentCreateNote();
        } else if (id == R.id.nav_list) {
            makeIntentListNote();
        } else if (id == R.id.nav_conf) {
            makeIntentConf();
        } else if (id == R.id.nav_tags) {
            makeIntentTags();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    protected void makeIntentListNote() {
        alert();
    }

    protected void makeIntentCreateNote() {
        alert();
    }
    protected void makeIntentConf(){
        alert("Halam ini masi pengembangan");
    }
    protected void makeIntentTags(){
        alert("Halam ini masi pengembangan");
    }
    private void alert(){
        alert("Anda sudah berada pada page tersebut");
    }
    private void alert(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }
}

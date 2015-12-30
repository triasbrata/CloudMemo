package bitdev.cloudmemo.activities;

import android.content.Intent;

import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.design.widget.TextInputLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.WindowManager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import bitdev.cloudmemo.R;
import bitdev.cloudmemo.apps.AppController;
import bitdev.cloudmemo.utils.Conf;

import bitdev.cloudmemo.utils.SessionManager;

public class Login extends AppCompatActivity {
    private EditText usernameEditText,passwordEditText;
    private TextInputLayout usernameTextInputLayout, passwordTextInputLayout;
    private LinearLayout notif,form_login;
    private Button loginBtn;
    private TextView registerBtn;
    private String URL_LOGIN = Conf.masterURL+"login";
    private CardView cardView;
    private Animation fade_out, fade_in;
    private SessionManager session;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);
//        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//        StrictMode.setThreadPolicy(policy);
        session = new SessionManager(getApplicationContext());
        if(session.isLoggedIn()){
            moveHome();
        }
        /*inisilaisaasi animasi*/
        fade_out = AnimationUtils.loadAnimation(this,R.anim.fade_out);
        fade_in = AnimationUtils.loadAnimation(this,R.anim.fade_in);
        fade_out.setDuration(800);
        fade_in.setDuration(800);


        usernameEditText = (EditText) findViewById(R.id.input_username);
        passwordEditText = (EditText) findViewById(R.id.input_password);
        usernameTextInputLayout = (TextInputLayout) findViewById(R.id.input_layout_name);
        passwordTextInputLayout = (TextInputLayout) findViewById(R.id.input_layout_password);
        cardView = (CardView) findViewById(R.id.card_view);
        usernameEditText.addTextChangedListener(new MyTextWathcer(usernameEditText));
        passwordEditText.addTextChangedListener(new MyTextWathcer(passwordEditText));
        notif = (LinearLayout) findViewById(R.id.notif);
        form_login = (LinearLayout) findViewById(R.id.login_form);
        loginBtn = (Button) findViewById(R.id.btn_sing_in);
        loginBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                submitForm();
            }
        });
        registerBtn = (TextView) findViewById(R.id.btn_register);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this,Register.class));
                finish();
            }
        });

    }
    private void moveHome(){
        Intent intent = new Intent(Login.this,Home.class);
        startActivity(intent);
        finish();
    }
    private void submitForm()  {

        if(validateUsername() && validatePassword()){
//
            try {
                makeRequestLogin(usernameEditText.getText().toString(), passwordEditText.getText().toString());
            }catch (InterruptedException e){
                e.getStackTrace();
            }



        }
    }
    private void makeRequestLogin(final String username,final String password) throws InterruptedException {
        final HashMap<String,String> userCredential = new HashMap<>();
        userCredential.put("email", username);
        userCredential.put("password", password);
        form_login.startAnimation(fade_out);
        notif.setAnimation(fade_in);
        final StringRequest strReq = new StringRequest(Method.POST, URL_LOGIN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsob = new JSONObject(response);
                    Log.d("debuggin",jsob.toString());
                    boolean error = jsob.getBoolean("error");
                    if (!error) {
                        session.setLogin(true);
                        HashMap<String, String> dataSession = new HashMap<>();
                        jsob.remove("error");

                        JSONObject usercredential = jsob.getJSONObject("user_credential");
                        Iterator uci = usercredential.keys();
                        while (uci.hasNext()) {
                            String e = (String) uci.next();
                            Log.d("debuggin",e);
                            dataSession.put(e, usercredential.getString(e));
                        }
                        Log.d("debugging", new JSONObject(dataSession).toString());
                        session.setSession(dataSession);
                        moveHome();
                    }else{

                        notif.startAnimation(fade_out);
                        form_login.setAnimation(fade_in);
                        fade_out.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {

                                notif.setVisibility(View.GONE);
                                form_login.setVisibility(View.VISIBLE);
                                Toast.makeText(Login.this, R.string.login_error, Toast.LENGTH_SHORT).show();

                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                notif.startAnimation(fade_out);
                form_login.setAnimation(fade_in);
                fade_out.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                        notif.setVisibility(View.GONE);
                        form_login.setVisibility(View.VISIBLE);
                        Toast.makeText(Login.this, R.string.login_error, Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                Log.w("err",error);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return  userCredential;
            }
        };
        fade_out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                form_login.setVisibility(View.GONE);
                notif.setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        AppController.getInstance().addToRequestQueue(strReq);
                    }
                }, 900);


            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


    }

    private boolean validateUsername(){
        if (usernameEditText.getText().toString().trim().isEmpty()){
            usernameTextInputLayout.setError(getString(R.string.username_error));
            requestFocus(usernameEditText);
            return false;
        }
        else if(! usernameEditText.getText().toString().contains("@")){
            usernameTextInputLayout.setError(getString(R.string.username_error_not_email));
            requestFocus(usernameEditText);
            return false;
        }else if(! usernameEditText.getText().toString().contains(".")){
            usernameTextInputLayout.setError(getString(R.string.username_error_not_email_dot));
            requestFocus(usernameEditText);
            return false;
        }
        else{
            usernameTextInputLayout.setErrorEnabled(false);
        }
        return true;
    }
    private boolean validatePassword(){
        if(passwordEditText.getText().toString().trim().isEmpty()){
            passwordTextInputLayout.setError(getString(R.string.password_error));
            requestFocus(passwordEditText);
            return false;

        }else {
            passwordTextInputLayout.setErrorEnabled(false);
        }
        return true;
    }
    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
    private class MyTextWathcer implements TextWatcher{

        private View view;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            switch (this.view.getId())
            {
                case R.id.input_password:
                    validatePassword();
                    break;
                case R.id.input_username:
                    validateUsername();
                    break;
            }
        }

        private MyTextWathcer(View view){
            this.view = view;
        }

    }
}

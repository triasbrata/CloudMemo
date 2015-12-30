package bitdev.cloudmemo.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

import bitdev.cloudmemo.R;
import bitdev.cloudmemo.apps.AppController;
import bitdev.cloudmemo.utils.Conf;
import bitdev.cloudmemo.utils.SessionManager;

public class Register extends AppCompatActivity {
    private EditText usernameInput,passwordInput,namaInput;
    private TextInputLayout usernameInputLayout, passwordInputLayout, namaInputLayout;
    private Button daftarBtn;
    private LinearLayout form_login,loading_sceen;
    private Animation fade_in,fade_out;
    private SessionManager session;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        /*animation*/
        fade_in = AnimationUtils.loadAnimation(this,R.anim.fade_in);
        fade_out = AnimationUtils.loadAnimation(this,R.anim.fade_out);
        fade_out.setDuration(600);
        fade_in.setDuration(600);
        /*end of animation*/

        /*session setup*/
        session = new SessionManager(getApplicationContext());
        /*end session setup*/

        form_login = (LinearLayout) findViewById(R.id.login_form);
        loading_sceen = (LinearLayout) findViewById(R.id.notif);
        usernameInput = (EditText) findViewById(R.id.input_username);
        namaInput = (EditText) findViewById(R.id.input_name);
        passwordInput = (EditText) findViewById(R.id.input_password);
        usernameInputLayout = (TextInputLayout) findViewById(R.id.input_layout_username);
        passwordInputLayout = (TextInputLayout) findViewById(R.id.input_layout_password);
        namaInputLayout = (TextInputLayout) findViewById(R.id.input_layout_name);
        usernameInput.addTextChangedListener(new MyTextWathcer(usernameInput));
        namaInput.addTextChangedListener(new MyTextWathcer(namaInput));
        passwordInput.addTextChangedListener(new MyTextWathcer(passwordInput));
        daftarBtn = (Button) findViewById(R.id.btn_register_form);
        daftarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register(namaInput.getText().toString(),usernameInput.getText().toString(),passwordInput.getText().toString());
            }
        });

    }

    private void register(String nama, String username, String password) {
        if(validateName()&& validatePassword()&& validateUsername()){
            HashMap<String,String> userCredential = new HashMap<>();
            userCredential.put("name",nama);
            userCredential.put("email",username);
            userCredential.put("password",password);
            requestRegister(userCredential);
        }
    }

    private void requestRegister(HashMap<String, String> userCredential) {
        String url = Conf.masterURL+"register";
        JSONObject userPost = new JSONObject(userCredential);
        final JsonObjectRequest registerRequest = new JsonObjectRequest(url, userPost, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(! response.getBoolean("error")){
                        JSONObject user = response.getJSONObject("userCredential");
                        HashMap<String, String> dataSession = new HashMap<>();
                        Iterator uci = user.keys();
                        while (uci.hasNext()) {
                            String e = (String) uci.next();
                            dataSession.put(e, user.getString(e));
                        }
                        session.setSession(dataSession);
                        session.setLogin(true);
                        startActivity(new Intent(Register.this,Home.class));
                    }else{
                        loadingToForm();
                    }
                } catch (JSONException e) {
                    Log.d("Register","Parsing Json Error",e);
                    loadingToForm();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Register","Volley Register Error",error);
                loadingToForm();
            }
        });
        form_login.startAnimation(fade_out);
        fade_out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                form_login.setVisibility(View.GONE);
                loading_sceen.setVisibility(View.VISIBLE);
                loading_sceen.startAnimation(fade_in);
                fade_in.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                AppController.getInstance().addToRequestQueue(registerRequest);
                            }
                        }, 300);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private final void loadingToForm() {
        Log.d("debuggin","i got fired bro!");
        loading_sceen.startAnimation(fade_out);
        fade_out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                    loading_sceen.setVisibility(View.GONE);
                    form_login.setVisibility(View.VISIBLE);
                    form_login.startAnimation(fade_in);
                    fade_in.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            Toast.makeText(Register.this, getString(R.string.register_error), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private class MyTextWathcer implements TextWatcher {
        private View v;
        public MyTextWathcer(View v) {
            this.v = v;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            switch (this.v.getId()){
                case R.id.input_username:
                    validateUsername();
                    break;
                case R.id.input_password:
                    validatePassword();
                    break;
                case R.id.input_name:
                    validateName();
                    break;
            }
        }
    }

    private boolean validateUsername(){
        if (usernameInput.getText().toString().trim().isEmpty()){
            usernameInputLayout.setError(getString(R.string.username_error));
            requestFocus(usernameInput);
            return false;
        }
        else if(! usernameInput.getText().toString().contains("@")){
            usernameInputLayout.setError(getString(R.string.username_error_not_email));
            requestFocus(usernameInput);
            return false;
        }else if(! usernameInput.getText().toString().contains(".")){
            usernameInputLayout.setError(getString(R.string.username_error_not_email_dot));
            requestFocus(usernameInput);
            return false;
        }
        else{
            usernameInputLayout.setErrorEnabled(false);
        }
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private boolean validatePassword(){
        if(passwordInput.getText().toString().trim().isEmpty()){
            passwordInputLayout.setError(getString(R.string.password_error));
            requestFocus(passwordInput);
            return false;

        }else {
            passwordInputLayout.setErrorEnabled(false);
        }
        return true;
    }
    private boolean validateName(){
        if(namaInput.getText().toString().trim().isEmpty()){
            namaInputLayout.setError(getString(R.string.nama_error));
            requestFocus(namaInput);
            return false;
        }else
        {
            namaInputLayout.setErrorEnabled(false);
        }
        return true;
    }
}

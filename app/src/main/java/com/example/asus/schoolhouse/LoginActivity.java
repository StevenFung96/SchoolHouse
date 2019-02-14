package com.example.asus.schoolhouse;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.schoolhouse.ProgressionActivity.NavDrawer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private TextInputLayout mLoginEmail;
    private TextInputLayout mLoginPassword;
    private TextView mInputTextLogin;

    private Button mLogin_btn;
    private Button mResetPwd_btn;

    private ProgressDialog mLoginProgress;
    private FirebaseAuth mAuth;

    private DatabaseReference mUserDatabase;

    public static final String LAST_TEXT = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //mToolbar = (Toolbar) findViewById(R.id.login_toolbar);
        //setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mLoginProgress = new ProgressDialog(this);

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        mAuth = FirebaseAuth.getInstance();

        mLoginEmail = (TextInputLayout) findViewById(R.id.login_email);
        mLoginPassword = (TextInputLayout) findViewById(R.id.login_password);
        mLogin_btn = (Button) findViewById(R.id.login_btn);
        mResetPwd_btn = (Button)findViewById(R.id.btn_forget_password);
        mInputTextLogin = (TextView)findViewById(R.id.inputText_login);

        mLogin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mLoginEmail.getEditText().getText().toString();
                String password = mLoginPassword.getEditText().getText().toString();

                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {

                    mLoginProgress.setTitle("Loging In");
                    mLoginProgress.setMessage("Please wait while we check your credentials.");
                    mLoginProgress.setCanceledOnTouchOutside(false);
                    mLoginProgress.show();

                    login_user(email, password);

                }
            }
        });

        mResetPwd_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
            }
        });

        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
        mInputTextLogin.setText(pref.getString(LAST_TEXT, ""));
        mInputTextLogin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                pref.edit().putString(LAST_TEXT, s.toString()).commit();


            }
        });

    }

    private void login_user(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            mLoginProgress.dismiss();

                            String current_user_id = mAuth.getCurrentUser().getUid();
                            String deviceToken = FirebaseInstanceId.getInstance().getToken();


                            mUserDatabase.child(current_user_id).child("device_token").setValue(deviceToken).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    Intent mainIntent = new Intent(LoginActivity.this, NavDrawer.class);
                                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(mainIntent);
                                    finish();

                                }
                            });

                        } else {

                            mLoginProgress.hide();
                            Toast.makeText(LoginActivity.this, "Can't Sign in. Please check the form and try again.", Toast.LENGTH_LONG).show();

                        }

                    }
                });

    }
}

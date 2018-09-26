package com.example.sanjeev.skychat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private EditText LoginEmail;
    private EditText LoginPassword;
    private Button LoginButton;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mToolbar=(Toolbar)findViewById(R.id.login_toolbar);
        LoginButton=(Button)findViewById(R.id.login_button);
        LoginEmail=(EditText)findViewById(R.id.login_email);
        LoginPassword=(EditText)findViewById(R.id.login_password);
        loadingBar=new ProgressDialog(this);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("SignIn");
        mAuth=FirebaseAuth.getInstance();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=LoginEmail.getText().toString();
                String password=LoginPassword.getText().toString();
                LoginUserAccount(email,password);
            }
        });
    }

    private void LoginUserAccount(String email, String password) {
        if(TextUtils.isEmpty(email))
        {
            Toast.makeText(getApplicationContext(),"Enter Email",Toast.LENGTH_LONG).show();
        }
        if(TextUtils.isEmpty(password))
        {
            Toast.makeText(getApplicationContext(),"Enter Password",Toast.LENGTH_LONG).show();
        }
        else {

            loadingBar.setTitle("Logging In...");
            loadingBar.setMessage("Please Wait.");
            loadingBar.show();


           mAuth.signInWithEmailAndPassword(email, password)
                   .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                       @Override
                       public void onComplete(@NonNull Task<AuthResult> task) {
                           if(task.isSuccessful())
                           {
                               Intent mainIntent=new Intent(LoginActivity.this,MainActivity.class);
                               mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                               startActivity(mainIntent);
                               finish();
                           }
                           else {
                               Toast.makeText(getApplicationContext(),"Check Email And Password",Toast.LENGTH_LONG).show();
                           }
                           loadingBar.dismiss();
                       }
                   });

        }
    }
}

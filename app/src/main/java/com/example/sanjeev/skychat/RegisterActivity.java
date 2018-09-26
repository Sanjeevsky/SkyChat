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
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    Toolbar mToolbar;
    FirebaseAuth mAuth;

    DatabaseReference storeUserDefaultDataReference;

    private EditText RegisterUserEmail;
    private EditText RegisterUserName;
    private EditText RegisterUserPassword;
    private Button CreateAccountButton;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mToolbar=(Toolbar)findViewById(R.id.register_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Register");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        RegisterUserEmail=(EditText)findViewById(R.id.register_email);
        RegisterUserName=(EditText)findViewById(R.id.register_name);
        RegisterUserPassword=(EditText)findViewById(R.id.register_password);
        CreateAccountButton=(Button)findViewById(R.id.create_account_button);

        loadingBar=new ProgressDialog(this);
        mAuth=FirebaseAuth.getInstance();


        CreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name=RegisterUserName.getText().toString();
                String email=RegisterUserEmail.getText().toString();
                String password=RegisterUserPassword.getText().toString();
                RegisterAccount(name,email,password);
            }
        });

    }

    private void RegisterAccount(final String name, String email, String password) {
        if(TextUtils.isEmpty(name))
        {
            Toast.makeText(RegisterActivity.this,"Enter UserName",Toast.LENGTH_LONG).show();
        }
        if(TextUtils.isEmpty(email))
        {
            Toast.makeText(RegisterActivity.this,"Enter Email",Toast.LENGTH_LONG).show();
        }
        if(TextUtils.isEmpty(password))
        {
            Toast.makeText(RegisterActivity.this,"Enter Password",Toast.LENGTH_LONG).show();
        }
        else {
            loadingBar.setTitle("Creating Account");
            loadingBar.setMessage("Please Wait");
            loadingBar.show();


            mAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {String current_user_id=mAuth.getCurrentUser().getUid();
                               storeUserDefaultDataReference=FirebaseDatabase.getInstance().getReference().child("Users").child(current_user_id);
                               storeUserDefaultDataReference.child("user_name").setValue(name);
                               storeUserDefaultDataReference.child("user_status").setValue("Hey There I am USing SkyChat..!!");
                               storeUserDefaultDataReference.child("user_image").setValue("default_profile");
                               storeUserDefaultDataReference.child("user_thumb_image").setValue("default_image")
                                       .addOnCompleteListener(new OnCompleteListener<Void>() {
                                           @Override
                                           public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful())
                                                {
                                                    Intent mainIntent=new Intent(RegisterActivity.this,MainActivity.class);
                                                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(mainIntent);
                                                    finish();
                                                }
                                           }
                                       });

                            }
                            else
                            {
                                Toast.makeText(RegisterActivity.this,"Error Occured Try Again..!!",Toast.LENGTH_LONG).show();
                            }
                            loadingBar.dismiss();
                        }
                    });
        }
    }
}

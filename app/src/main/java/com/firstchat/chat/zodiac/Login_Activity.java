package com.firstchat.chat.zodiac;

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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class Login_Activity extends AppCompatActivity {
    private Toolbar mToolbar;
    private EditText log_email;
    private EditText log_password;
    private Button log_submit_button;
    private Button log_submit_button2;
    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase;
    private ProgressDialog mProgressDialogue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_);

        log_email=(EditText)findViewById(R.id.log_email_ID);
        log_password=(EditText) findViewById(R.id.log_password_ID);
        mToolbar=(Toolbar)findViewById(R.id.log_Toolbar_ID);
        mProgressDialogue = new ProgressDialog(Login_Activity.this);

        //firebase
        mAuth = FirebaseAuth.getInstance();
        mUserDatabase= FirebaseDatabase.getInstance().getReference().child("Users");
        //toolbar
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
       log_submit_button =(Button)findViewById(R.id.log_submit_button);
        log_submit_button2 =(Button)findViewById(R.id.log_submit_button2);
       log_submit_button.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               String email=log_email.getText().toString();
               String password=log_password.getText().toString();
           if(!TextUtils.isEmpty(email)||!TextUtils.isEmpty(password))
               {    mProgressDialogue.setTitle("Loggin in");
                    mProgressDialogue.setMessage("Checking Credentials");
                    mProgressDialogue.setCanceledOnTouchOutside(false);
                    mProgressDialogue.show();

                   loginUser(email,password);
               }

           }
       });
       log_submit_button2.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent log2intent= new Intent(Login_Activity.this,Register_activity.class);
               startActivity(log2intent);
               finish();
           }
       });


    }
    private void loginUser(String email,String Password){
        mAuth.signInWithEmailAndPassword(email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){

                    String deviceToken = FirebaseInstanceId.getInstance().getToken();
                    String current_user_id=mAuth.getCurrentUser().getUid();

                    mUserDatabase.child(current_user_id).child("device_token").setValue(deviceToken).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Intent logintent= new Intent(Login_Activity.this,MainActivity.class);
                            logintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(logintent);
                            finish();

                        }
                    });


                }else {
                    mProgressDialogue.hide();
                   Toast.makeText(Login_Activity.this,"Please Check your Credentials,login failed",Toast.LENGTH_LONG).show();
                }
            }
        });


    }
}

package com.firstchat.chat.zodiac;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import org.w3c.dom.Text;

import java.util.HashMap;

public class Register_activity extends AppCompatActivity {
    private EditText mDisplayname;
    private EditText mEmail;
    private EditText mPassword;
    private Button reg_submit_button;
    private Toolbar mToolbar;
    private FirebaseAuth mAuth;
    private DatabaseReference mFirebaseDatabase;
    private DatabaseReference mUsersDatabase;

    //Progressdialog
    private ProgressDialog mProgressdialogue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_activity);
        mDisplayname=(EditText)findViewById(R.id.reg_displayname_ID);
        mEmail=(EditText)findViewById(R.id.reg_email_ID);
        mPassword=(EditText) findViewById(R.id.reg_password_ID);
        mToolbar=(Toolbar)findViewById(R.id.reg_Toolbar_ID);

        //firebase
        mAuth = FirebaseAuth.getInstance();
        mUsersDatabase=FirebaseDatabase.getInstance().getReference().child("Users");
        //toolbar
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        reg_submit_button=(Button)findViewById(R.id.reg_submit_button);
        //ButtonLISTNER
        reg_submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String display_name = mDisplayname.getText().toString();
                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();
                //progress dialogue
                mProgressdialogue = new ProgressDialog(Register_activity.this);

                if (TextUtils.isEmpty(display_name) || (TextUtils.isEmpty(email)) || (TextUtils.isEmpty(password)))
                {
                    Toast.makeText(Register_activity.this,"you have left one or two fields empty",Toast.LENGTH_LONG).show();

                }
                else
                    { mProgressdialogue.setTitle("Registering User");
                    mProgressdialogue.setMessage("Please wait");
                    mProgressdialogue.setCanceledOnTouchOutside(false);
                    mProgressdialogue.show();
                    register_user(display_name,email,password);
                }



            }
        });


        }
        private void register_user(final String display_name, String email, String password){
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    FirebaseUser currentuser= FirebaseAuth.getInstance().getCurrentUser();
                    String deviceToken = FirebaseInstanceId.getInstance().getToken();
                    String uid= currentuser.getUid();
                    mFirebaseDatabase=FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

                    HashMap<String,String> usermap= new HashMap<>();
                    usermap.put("device_token",deviceToken);
                    usermap.put("name",display_name);
                    usermap.put("status","Hello this is my first time using Zodiac Chat :')");
                    usermap.put("image","default");
                    usermap.put("thumb_image","default");
                    mFirebaseDatabase.setValue(usermap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                mProgressdialogue.dismiss();

                                Intent RegIntent=new Intent(Register_activity.this,MainActivity.class);
                                RegIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(RegIntent);
                                finish();
                            }
                        }
                    });
                  /*  */

                } else {
                    mProgressdialogue.hide();
                    Toast.makeText(Register_activity.this, "Check your details once again", Toast.LENGTH_LONG);
                }
            }
        });
        }


}

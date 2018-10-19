package com.firstchat.chat.zodiac;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class AuthActivity extends AppCompatActivity {
    //initisiliation
  private LinearLayout otplayput;
    private LinearLayout phonelayout;
    private EditText phonetext;
    private EditText otptext;
    private ProgressDialog auth_mprogressdialogue;
    private Toolbar mToolbar;

    private Button button ;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private FirebaseAuth mAuth;
    private TextView errorText;
    int btntype;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        auth_mprogressdialogue= new ProgressDialog(AuthActivity.this);
        mToolbar=(Toolbar)findViewById(R.id.auth_toolbaar);


        phonetext=(EditText)findViewById(R.id.auth_phone_ID);
        otptext=(EditText)findViewById(R.id.auth_OTP_ID);
        button=(Button)findViewById(R.id.Verify);


        otptext.setEnabled(false);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Phone Number Verification");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //firebase
        mAuth=FirebaseAuth.getInstance();
        phonetext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if ((keyEvent != null && (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (i == EditorInfo.IME_ACTION_DONE)) {
                    //do what you want on the press of 'done'
                    button.performClick();}
                return false;
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phonetext.setEnabled(false);
                if(btntype==0){

                otptext.setVisibility(View.VISIBLE);

                phonetext.setEnabled(false);
                button.setEnabled(false);
                    auth_mprogressdialogue.setTitle("Just a Minute");
                    auth_mprogressdialogue.setMessage("Sending Code to your Phone");
                    auth_mprogressdialogue.show();

                String phonenumber=phonetext.getText().toString();
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        phonenumber,60
                        ,TimeUnit.SECONDS,
                        AuthActivity.this,
                        callbacks
                );

                }

                else{
                    otptext.setEnabled(true);
                    button.setEnabled(false);
                    auth_mprogressdialogue.setTitle("Just a Minute");
                    auth_mprogressdialogue.setMessage("Verfying Your Code");
                    auth_mprogressdialogue.setCanceledOnTouchOutside(false);
                    auth_mprogressdialogue.show();

                    String verificationcode=otptext.getText().toString();
                    PhoneAuthCredential credential=PhoneAuthProvider.getCredential(mVerificationId,verificationcode);

                    signInWithPhoneAuthCredential(credential);
                }
            }
        });
          callbacks= new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
          @Override
                 public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
              auth_mprogressdialogue.setTitle("Just a Minute");
              auth_mprogressdialogue.setMessage("Verifying your Code");
              auth_mprogressdialogue.show();

        signInWithPhoneAuthCredential(phoneAuthCredential);
    }

    @Override
    public void onVerificationFailed(FirebaseException e) {
              auth_mprogressdialogue.dismiss();
        AlertDialog.Builder builder = new AlertDialog.Builder(AuthActivity.this);
        builder.setTitle("Error");
        builder.setMessage ("Verification failed");
        phonetext.setEnabled(true);
        button.setEnabled(true);
        AlertDialog dialog = builder.create();
        dialog.show();



    }
    @Override
    public void onCodeSent(String verificationId,
                           PhoneAuthProvider.ForceResendingToken token) {
              auth_mprogressdialogue.dismiss();
              otptext.setEnabled(true);

        mVerificationId = verificationId;
        mResendToken = token;
        btntype=1;
       // otplayput.setVisibility(View.VISIBLE);

        button.setText("Verify Code");
        button.setEnabled(true);

        // ...
    }
};
    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Intent mainintent = new Intent(AuthActivity.this,Login_Activity.class);
                            mainintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(mainintent);

                            FirebaseUser user = task.getResult().getUser();
                            auth_mprogressdialogue.dismiss();
                            // ...
                        } else {
                            phonetext.setEnabled(true);
                            button.setEnabled(true);
                            auth_mprogressdialogue.dismiss();
                             AlertDialog.Builder builder = new AlertDialog.Builder(AuthActivity.this);
                            builder.setTitle("Error");
                            builder.setMessage ("Please check your phone number");
                            AlertDialog dialog = builder.create();
                            dialog.show();



                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid

                                builder.setTitle("Error");
                                builder.setMessage ("The verification code entered was invalid");
                                dialog.show();

                            }
                        }
                    }
                });
    }
}

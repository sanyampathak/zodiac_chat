package com.firstchat.chat.zodiac;

import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

public class Welcome_activity extends AppCompatActivity {
    private Button get_started_btn;
    private FirebaseAuth mAuth;

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_2);

        mAuth = FirebaseAuth.getInstance();
        imageView=(ImageView)findViewById(R.id.imageView);
        //Picasso.get().load(R.drawable.zodiac_icom).placeholder(R.drawable.zodiac_icom).into(imageView);
        get_started_btn=(Button)findViewById(R.id.welcome_getstarted_ID);

       // mProgressbar.setVisibility(View.INVISIBLE);


    get_started_btn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //mProgressbar.setVisibility(view.VISIBLE);

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent welcomeIntent = new Intent(Welcome_activity.this,Login_Activity.class);
                    startActivity(welcomeIntent);
                    // Do something after 5s = 5000ms

                }
            }, 250);



          //  mProgressbar.setVisibility(View.INVISIBLE);
        }
    });
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null)
        {
            Intent authintent = new Intent(Welcome_activity.this,MainActivity.class);
            startActivity(authintent);
            finish(); }

    }

}

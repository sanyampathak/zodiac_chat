package com.firstchat.chat.zodiac;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Profile_Activity extends AppCompatActivity {
    private ImageView mProfileImage;
    private TextView mDisplayID,mProfileStatus,mProfileFriendsCount;
    private Button mSendReqButton;
    private ProgressDialog mProgressDialog;
    private String mCurrent_state;
    private DatabaseReference mFriendDatabase;
    private DatabaseReference mFriendReqDatabase;
    private DatabaseReference mNotificationDatabase;
    private DatabaseReference mRootRef;
    private DatabaseReference mUsersDatabase;
    private FirebaseUser mCurrent_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_);
        final String user_id= getIntent().getStringExtra("user_id");

        //##########################____________________FIREBASE INSTANCES__________________####################
        mFriendDatabase=FirebaseDatabase.getInstance().getReference().child("Friends");
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mUsersDatabase= FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        mFriendReqDatabase =FirebaseDatabase.getInstance().getReference().child("Friend_req");
        mNotificationDatabase=FirebaseDatabase.getInstance().getReference().child("notifications");
        mCurrent_user = FirebaseAuth.getInstance().getCurrentUser();
        //textfields and Imageview
        mDisplayID=(TextView)findViewById(R.id.profile_Displayname_ID);
        mProfileImage=(ImageView)findViewById(R.id.profile_ImageView_ID);
        mDisplayID=(TextView)findViewById(R.id.profile_Displayname_ID);
        mProfileStatus=(TextView)findViewById(R.id.profile_status_tv_ID);
       // mProfileFriendsCount=(TextView)findViewById(R.id.profile_numberofFriends_ID);
        mSendReqButton=(Button) findViewById(R.id.profile_send_request_button_ID);

        mCurrent_state="not_friends";

        //Progress Dialogie
        mProgressDialog= new ProgressDialog(Profile_Activity.this);
        mProgressDialog.setTitle("Loading Profile");
        mProgressDialog.setMessage("Please wait");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();

        mUsersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                String display_name= dataSnapshot.child("name").getValue().toString();
                String status=dataSnapshot.child("status").getValue().toString();
                String image= dataSnapshot.child("image").getValue().toString();


               mDisplayID.setText(display_name);
               mProfileStatus.setText(status);
                //############### PICASSO ###########
                Picasso.get().load(image).placeholder(R.drawable.avatar).centerCrop().fit().into(mProfileImage);

                //--------------- FRIENDS LIST / REQUEST FEATURE -----

                mFriendReqDatabase.child(mCurrent_user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.hasChild(user_id)){

                            String req_type = dataSnapshot.child(user_id).child("request_type").getValue().toString();

                            if(req_type.equals("received")){



                                mCurrent_state = "req_received";
                                mSendReqButton.setText("Accept Friend Request");




                            } else if(req_type.equals("sent")) {

                                mCurrent_state = "req_sent";
                                mSendReqButton.setText("Cancel Friend Request");



                            }

                            mProgressDialog.dismiss();


                        } else {


                            mFriendDatabase.child(mCurrent_user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    if(dataSnapshot.hasChild(user_id)){

                                        mCurrent_state = "friends";
                                        mSendReqButton.setText("Unfriend this Person");



                                    }

                                    mProgressDialog.dismiss();

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                    mProgressDialog.dismiss();

                                }
                            });

                        }



                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        mSendReqButton.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        mSendReqButton.setEnabled(false);

        // #####################NOT FRIENDS STATE@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
        if(mCurrent_state.equals("not_friends")){


            DatabaseReference newNotificationref = mRootRef.child("notifications").child(user_id).push();
            String newNotificationId = newNotificationref.getKey();

            HashMap<String, String> notificationData = new HashMap<>();
            notificationData.put("from", mCurrent_user.getUid());
            notificationData.put("type", "request");

            Map requestMap = new HashMap();
            requestMap.put("Friend_req/" + mCurrent_user.getUid() + "/" + user_id + "/request_type", "sent");
            requestMap.put("Friend_req/" + user_id + "/" + mCurrent_user.getUid() + "/request_type", "received");
            requestMap.put("notifications/" + user_id + "/" + newNotificationId, notificationData);

            mRootRef.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                    if(databaseError != null){

                        Toast.makeText(Profile_Activity.this, "There was some error in sending request", Toast.LENGTH_SHORT).show();

                    } else {

                        mCurrent_state = "req_sent";
                        mSendReqButton.setText("Cancel Friend Request");

                    }

                    mSendReqButton.setEnabled(true);


                }
            });

        }

        //@@@@@@@@@@@@@@@@@@@@@@@----CANCEL REQUEST----@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
        if(mCurrent_state.equals("req_sent")){

            mFriendReqDatabase.child(mCurrent_user.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    mFriendReqDatabase.child(user_id).child(mCurrent_user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {


                            mSendReqButton.setEnabled(true);
                            mCurrent_state = "not_friends";
                            mSendReqButton.setText("Send Friend Request");



                        }
                    });

                }
            });

        }

        // ------------ REQ RECEIVED STATE ----------

        if(mCurrent_state.equals("req_received")){

            final String currentDate = DateFormat.getDateTimeInstance().format(new Date());

            Map friendsMap = new HashMap();
            friendsMap.put("Friends/" + mCurrent_user.getUid() + "/" + user_id + "/date", currentDate);
            friendsMap.put("Friends/" + user_id + "/"  + mCurrent_user.getUid() + "/date", currentDate);


            friendsMap.put("Friend_req/" + mCurrent_user.getUid() + "/" + user_id, null);
            friendsMap.put("Friend_req/" + user_id + "/" + mCurrent_user.getUid(), null);


            mRootRef.updateChildren(friendsMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {


                    if(databaseError == null){

                        mSendReqButton.setEnabled(true);
                        mCurrent_state = "friends";
                        mSendReqButton.setText("Unfriend this Person");



                    } else {

                        String error = databaseError.getMessage();

                        Toast.makeText(Profile_Activity.this, error, Toast.LENGTH_SHORT).show();


                    }

                }
            });

        }
        // ------------ UNFRIENDS ---------

        if(mCurrent_state.equals("friends")){

            Map unfriendMap = new HashMap();
            unfriendMap.put("Friends/" + mCurrent_user.getUid() + "/" + user_id, null);
            unfriendMap.put("Friends/" + user_id + "/" + mCurrent_user.getUid(), null);

            mRootRef.updateChildren(unfriendMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {


                    if(databaseError == null){

                        mCurrent_state = "not_friends";
                        mSendReqButton.setText("Send Friend Request");


                    } else {

                        String error = databaseError.getMessage();

                        Toast.makeText(Profile_Activity.this, error, Toast.LENGTH_SHORT).show();


                    }

                    mSendReqButton.setEnabled(true);

                }
            });

        }

    }
});
    }
}

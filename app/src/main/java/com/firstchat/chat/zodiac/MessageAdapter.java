package com.firstchat.chat.zodiac;

import android.content.Context;
import android.graphics.Color;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by AkshayeJH on 24/07/17.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{


    private List<Messages> mMessageList;
    private DatabaseReference mUserDatabase;
    private FirebaseAuth mAuth;
    private RelativeLayout relativeLayout;
    public MessageAdapter(List<Messages> mMessageList) {

        this.mMessageList = mMessageList;

    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_single_layout ,parent, false);

        return new MessageViewHolder(v);

    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

       public TextView messageTime;
        public TextView messageText;
        public CircleImageView profileImage;
        public TextView displayName;
        public ImageView messageImage;

        public MessageViewHolder(View view) {
            super(view);
            mAuth=FirebaseAuth.getInstance();
            messageText = (TextView) view.findViewById(R.id.message_text_layout);
            profileImage = (CircleImageView) view.findViewById(R.id.message_profile_layout);
            displayName = (TextView) view.findViewById(R.id.message_displayname_layout);
            messageImage = (ImageView) view.findViewById(R.id.message_image_layout);
             messageTime = (TextView) view.findViewById(R.id.message_time_layout);
             relativeLayout=view.findViewById(R.id.message_single_layout);
        }
    }

    @Override
    public void onBindViewHolder(final MessageViewHolder viewHolder, int i) {
        String current_user_id=mAuth.getCurrentUser().getUid();

        Messages c = mMessageList.get(i);
        Messages t= mMessageList.get(i);

        String from_user = c.getFrom();
        String message_type = c.getType();
        String from_user_time=c.getFrom();


        if(from_user.equals(current_user_id)){
            relativeLayout.setBackgroundResource(R.color.Blue);
           //viewHolder.messageText.setBackgroundResource(R.drawable.bubble);
            //viewHolder.messageText.setTextColor(Color.rgb(23, 40, 76));
        }

           // viewHolder.messageText.setBackgroundResource(R.drawable.message_text_background);
          //  viewHolder.messageText.setText(Color.WHITE);


        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(from_user);

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString();
                String image = dataSnapshot.child("thumb_image").getValue().toString();

              viewHolder.displayName.setText(name);

                Picasso.get().load(image).placeholder(R.drawable.contact_blue).into(viewHolder.profileImage);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if(message_type.equals("text")) {

            viewHolder.messageText.setText(c.getMessage());
            DateFormat.getDateTimeInstance().format(new Date(t.getTime()));
            viewHolder.messageTime.setText(DateFormat.getDateTimeInstance().format(new Date(t.getTime())));
            viewHolder.messageImage.setVisibility(View.INVISIBLE);


        } else {

            viewHolder.messageText.setVisibility(View.INVISIBLE);
            Picasso.get().load(c.getMessage()).placeholder(R.drawable.common_google_signin_btn_icon_dark).into(viewHolder.messageImage);


        }

    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }






}

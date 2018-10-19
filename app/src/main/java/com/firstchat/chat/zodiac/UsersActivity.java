package com.firstchat.chat.zodiac;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersActivity extends AppCompatActivity {

    private RecyclerView mUserslist;
    private Toolbar mToolbar;
    private DatabaseReference mUsersDatabase;
    private LinearLayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);


        mToolbar = (Toolbar) findViewById(R.id.users_appbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("all users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        String url=mUsersDatabase.child("thumb_image").getKey();
        mLayoutManager = new LinearLayoutManager(this);
        mUserslist = (RecyclerView) findViewById(R.id.users_recycler);
        mUserslist.setHasFixedSize(true);
        mUserslist.setLayoutManager(mLayoutManager);


    }


    @Override
    protected void onStart() {
        super.onStart();
        startListening();

    }

    public void startListening() {
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Users")
                .limitToLast(100);

        FirebaseRecyclerOptions<Users> options =
                new FirebaseRecyclerOptions.Builder<Users>()
                        .setQuery(query, Users.class)
                        .build();
        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Users, UserViewHolder>(options) {
            @Override
            public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.users_2_layout, parent, false);

                return new UserViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(UserViewHolder holder, int position, Users model) {
                // Bind the Chat object to the ChatHolder
                holder.setName(model.name);
                holder.setStatus(model.status);
                holder.setImage(model.thumb_image,getApplicationContext());

               final String user_id=getRef(position).getKey();
              holder.mView.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       Intent profileIntent= new Intent(UsersActivity.this,Profile_Activity.class);
                        profileIntent.putExtra("user_id",user_id)  ;
                        startActivity(profileIntent);

                   }
               });

                // ...
            }

        };
        mUserslist.setAdapter(adapter);
        adapter.startListening();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public UserViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setName(String name) {
            TextView userNameView = (TextView) mView.findViewById(R.id.user_2DisplayName);
            userNameView.setText(name);
        }

        public void setStatus(String status) {
            TextView userStatusView=(TextView)
                    mView.findViewById(R.id.users_2_status_ID);
            userStatusView.setText(status);
        }


     public void setImage(String thumb_image, Context context) {
            CircleImageView userImageView=(CircleImageView)mView.findViewById(R.id.users_2image_ID);
            mView.getContext();
            Picasso.get().load(thumb_image).placeholder(R.drawable.contact_blue).into(userImageView);

    }
}}
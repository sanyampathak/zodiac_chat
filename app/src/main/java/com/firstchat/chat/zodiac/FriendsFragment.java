package com.firstchat.chat.zodiac;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;



/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {
    private RecyclerView mFriendsList;

    private DatabaseReference mFriendsDatabase;
    private DatabaseReference mUsersDatabase;

    private FirebaseAuth mAuth;

    private String mCurrent_user_id;

    private View mMainView;


    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMainView = inflater.inflate(R.layout.fragment_friends, container, false);

        mFriendsList = (RecyclerView) mMainView.findViewById(R.id.friends_list);
        mAuth = FirebaseAuth.getInstance();

        mCurrent_user_id = mAuth.getCurrentUser().getUid();

        mFriendsDatabase = FirebaseDatabase.getInstance().getReference().child("Friends").child(mCurrent_user_id);
        mFriendsDatabase.keepSynced(true);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersDatabase.keepSynced(true);


        mFriendsList.setHasFixedSize(true);
        mFriendsList.setLayoutManager(new LinearLayoutManager(getContext()));

        // Inflate the layout for this fragment
        return mMainView;


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Friends").child(mCurrent_user_id)
                .limitToLast(50);

        FirebaseRecyclerOptions<Friends> options =
                new FirebaseRecyclerOptions.Builder<Friends>()
                        .setQuery(query, Friends.class)
                        .build();
        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Friends,FriendsFragment.FriendsViewHolder>(options) {
            @Override
            public FriendsFragment.FriendsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.users_2_layout, parent, false);

                return new FriendsFragment.FriendsViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(final FriendsFragment.FriendsViewHolder holder, int position, Friends model) {
                // Bind the Chat object to the ChatHolder
               holder.setDate("Friends Since: "+model.date);
             final  String list_user_id=getRef(position).getKey();


               mUsersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                   @Override
                   public void onDataChange(DataSnapshot dataSnapshot) {
                       final String username=dataSnapshot.child("name").getValue().toString();
                       String userthumb=dataSnapshot.child("thumb_image").getValue().toString();

                       if(dataSnapshot.hasChild("online")){
                           String userOnline=dataSnapshot.child("online").getValue().toString();
                           holder.setUserOnline(userOnline);
                       }
                       holder.setName(username);
                        holder.setImage(userthumb);
                       holder.mView.setOnClickListener(new View.OnClickListener() {
                           @Override
                           public void onClick(View view) {

                               CharSequence options[] = new CharSequence[]{"Open Profile", "Send message"};

                               final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                               builder.setTitle("Select Options");
                               builder.setItems(options, new DialogInterface.OnClickListener() {
                                   @Override
                                   public void onClick(DialogInterface dialogInterface, int i) {

                                       //Click Event for each item.
                                       if(i == 0){

                                           Intent profileIntent = new Intent(getContext(), Profile_Activity.class);
                                           profileIntent.putExtra("user_id", list_user_id);
                                           startActivity(profileIntent);

                                       }

                                       if(i == 1){

                                           Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                                           chatIntent.putExtra("user_id", list_user_id);
                                           chatIntent.putExtra("user_name", username);
                                           startActivity(chatIntent);

                                       }

                                   }
                               });

                               builder.show();

                           }
                       });


                   }





                   @Override
                   public void onCancelled(DatabaseError databaseError) {

                   }
               });






                // ...
            }

        };mFriendsList.setAdapter(adapter);
        adapter.startListening();




    }
    public static class FriendsViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public FriendsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setName(String name) {
            TextView userNameView = (TextView) mView.findViewById(R.id.user_2DisplayName);
            userNameView.setText(name);
        }

        public void setDate(String Date) {
            TextView userStatusView = (TextView)
                    mView.findViewById(R.id.users_2_status_ID);
            userStatusView.setText(Date);

        }
        public void  setImage(String thumb_image){
            CircleImageView userImageView=(CircleImageView)mView.findViewById(R.id.users_2image_ID);
            Picasso.get().load(thumb_image).placeholder(R.drawable.contact_blue).into(userImageView);
        }


        public void setUserOnline(String online_status) {

            ImageView userOnlineView = (ImageView) mView.findViewById(R.id.user_single_online_icon);

            if(online_status.equals("true")){

                userOnlineView.setVisibility(View.VISIBLE);

            } else {

                userOnlineView.setVisibility(View.INVISIBLE);

            }

        }
    }


}




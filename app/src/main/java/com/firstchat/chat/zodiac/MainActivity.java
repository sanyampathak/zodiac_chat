package com.firstchat.chat.zodiac;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.NavigationView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class
MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private FirebaseAuth mAuth;
    String name,thumb_image,status;
    private DrawerLayout drawerLayout;
    private Toolbar mToolbar;
    private ViewPager mViewPager;
    private DatabaseReference mUserRef;
    private  SectionsPagerAdapter mPagerAdapter;
    private TabLayout mTablayout;
    private DatabaseReference mUserDatabase;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //firebase
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {


            mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
            mUserDatabase= FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        }


        mToolbar=(Toolbar)findViewById(R.id.main_page_toolbar);
       drawerLayout=(DrawerLayout) findViewById(R.id.drawer_layout);
        //tabs and viewpager
        mViewPager=(ViewPager)findViewById(R.id.main_tab_pager_ID);
        mTablayout=(TabLayout)findViewById(R.id.main_tabs);
        mPagerAdapter= new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);
        mTablayout.setupWithViewPager(mViewPager);
        //toolbar
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("ZODIAC");
       ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,drawerLayout , mToolbar    , R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(this);
            if(mAuth.getCurrentUser()!=null){
        final View navView =  navigationView.inflateHeaderView(R.layout.nav_header_main);

                {
                    mUserDatabase.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            name = dataSnapshot.child("name").getValue().toString();
                            status = dataSnapshot.child("status").getValue().toString();
                            thumb_image = dataSnapshot.child("thumb_image").getValue().toString();

                            TextView userName = (TextView) navView.findViewById(R.id.nav_username);
                            userName.setText(name);
                            TextView userStatus = (TextView) navView.findViewById(R.id.nav_status);
                            userStatus.setText(status);

                            ImageView user_imageView = (ImageView) navView.findViewById(R.id.nav_user_image);


                            Picasso.get().load(thumb_image).centerCrop().fit().placeholder(R.drawable.zodiaac_square_icon).into(user_imageView);

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                }

        }}


    @Override
    public void onStart() {

        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser==null)
        {
            sendToStart();

        }
        else {

            mUserRef.child("online").setValue("true");


        }


    }

  @Override
    protected void onStop() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser !=null)
            mUserRef.child("online").setValue(ServerValue.TIMESTAMP);
//          mUserRef.child("lastseen").setValue(ServerValue.TIMESTAMP);

        super.onStop();
    }

    private void sendToStart() {
        Intent authintent = new Intent(MainActivity.this,Welcome_activity.class);
        startActivity(authintent);
        finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu,menu);

        return  true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        super.onOptionsItemSelected(item);
        if(item.getItemId()==R.id.mainmenu_logout_ID){
            FirebaseAuth.getInstance().signOut();
            sendToStart();
        }
        if(item.getItemId()==R.id.mainmenu_account){
            Intent i= new Intent(MainActivity.this,SettingsActivity.class);
            startActivity(i);

        }
        if(item.getItemId()==R.id.mainmenu_allusers_ID)
        {Intent usersintent=new Intent(MainActivity.this,UsersActivity.class);
            startActivity(usersintent);}


        return  true;
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Fragment f= null;

        if (id==R.id.mainmenu_logout_ID) {
            FirebaseAuth.getInstance().signOut();
            sendToStart();
        }
        if(id==R.id.mainmenu_account){
            Intent i= new Intent(MainActivity.this,SettingsActivity.class);
            startActivity(i);
        }

        if(id==R.id.main_about){
        Intent i= new Intent(MainActivity.this,About_fragments.class);
        startActivity(i);
    }

        return false;
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}

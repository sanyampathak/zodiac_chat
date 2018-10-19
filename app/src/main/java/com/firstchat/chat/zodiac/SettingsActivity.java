package com.firstchat.chat.zodiac;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class SettingsActivity extends AppCompatActivity {
    //Firebase Instances
    private FirebaseUser mCurrentUser;
    private DatabaseReference mUserDatabase;
    //Buttons and TextFields
    private CircleImageView mCircleImageView;
    private TextView mDisplayname;
    private EditText mstatus;
    private Button change_DP;
    private Button change_status;
    private int btntype;
    private static  final int mGalleryPick=1;
    private ProgressDialog mProgressDialogue;
    private StorageReference mImageStorage;
    private Toolbar mToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //Buttons and TextFields Init
        mCircleImageView=(CircleImageView)findViewById(R.id.settings_image_ID);
        mDisplayname=(TextView)findViewById(R.id.settings_displayname_ID);
        mstatus=(EditText) findViewById(R.id.settings_status_ID);
        change_DP=(Button)findViewById(R.id.settings_changeDP_Btn_ID);
        change_status=(Button)findViewById(R.id.settings_changestatus_button_ID);
        //APPBAR
        mToolbar=(Toolbar)findViewById(R.id.settings_Appbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Account Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        mstatus.setEnabled(false);

        //firebase instances intitisilation
        mImageStorage= FirebaseStorage.getInstance().getReference();
        mCurrentUser= FirebaseAuth.getInstance().getCurrentUser();
        String current_uid=mCurrentUser.getUid();


        mUserDatabase= FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
        mUserDatabase.keepSynced(true);
        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //FIREBASE FETCHING THE VALUES
                String name= dataSnapshot.child("name").getValue().toString();
                String status=dataSnapshot.child("status").getValue().toString();
                String image= dataSnapshot.child("image").getValue().toString();
                final String thumb_image=dataSnapshot.child("thumb_image").getValue().toString();
                mDisplayname.setText(name);
                mstatus.setText(status);


                if(!image.equals("default")) {
                    Picasso.get().load(thumb_image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.contact_blue).into(mCircleImageView, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(thumb_image).placeholder(R.drawable.contact_blue).into(mCircleImageView);
                        }
                    });

                }      }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //CHANGING STATUS OF THE USER AND STUFF
             change_status.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(btntype==1){
                //progress bar for changing status
                mProgressDialogue=new ProgressDialog(SettingsActivity.this);
                mProgressDialogue.setTitle("Saving Changes");
                mProgressDialogue.setMessage("Please wait.....");
                mProgressDialogue.show();

                String newstatus=mstatus.getText().toString();
                mUserDatabase.child("status").setValue(newstatus).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            mProgressDialogue.dismiss();
                        }
                        else
                            Toast.makeText(getApplicationContext(),"there was some error in changing your status",Toast.LENGTH_LONG).show();
                    }
                });
                btntype=0;
                mstatus.setEnabled(false);
                change_status.setText("Change Status");



            }else {
                btntype=1;
                change_status.setText("Save Status");
                change_status.setBackgroundColor(Color.BLUE);
                mstatus.setEnabled(true);
                mstatus.setText("");

            }

        }
    });

             // DP and Thumbanail generation
   change_DP.setOnClickListener(new View.OnClickListener() {
       @Override
       public void onClick(View view) {
           Intent galleryIntent = new Intent();
           galleryIntent.setType("image/*");
           galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

           startActivityForResult(Intent.createChooser(galleryIntent,"SELECT IMAGE"),mGalleryPick);
          /* CropImage.activity()
                   .setGuidelines(CropImageView.Guidelines.ON)
                   .start(SettingsActivity.this); */
       }
   });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==mGalleryPick&&resultCode==RESULT_OK){
            Uri imageUri= data.getData();
            CropImage.activity(imageUri)
                    .setAspectRatio(1,1)
                    .start(this);
           // Toast.makeText(SettingsActivity.this,imageUri,Toast.LENGTH_LONG).show();
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {//Progress Image
                mProgressDialogue=new ProgressDialog((SettingsActivity.this));
                mProgressDialogue.setTitle("Uploading Image");
                mProgressDialogue.setMessage("please wait while the image gets uploaded");
                mProgressDialogue.setCanceledOnTouchOutside(false);
                mProgressDialogue.show();
                //image upload



                Uri resultUri = result.getUri();


                File thumb_imagefile= new File(resultUri.getPath());

                    String Current_user_id=mCurrentUser.getUid();


                Bitmap thumb_bitmap = null;
                try {
                    thumb_bitmap = new Compressor(this).setMaxHeight(200).setQuality(70).compressToBitmap(thumb_imagefile);
                } catch (IOException e) {
                    e.printStackTrace();
                }


                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                final byte[] thumb_byte = baos.toByteArray();


                StorageReference filepath=mImageStorage.child("profile_photos").child(Current_user_id+".jpg");
                final StorageReference thumb_filepath=mImageStorage.child("profile_photos").child("thumbs").child(Current_user_id+"jpg");

                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){

                            final String download_url=task.getResult().getDownloadUrl().toString();

                           //Thumnail generation
                            UploadTask uploadTask = thumb_filepath.putBytes(thumb_byte);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {

                                    String thumb_download_url=thumb_task.getResult().getDownloadUrl().toString();

                                    if(thumb_task.isSuccessful()){

                                        Map update_hashmap= new HashMap<>();
                                        update_hashmap.put("image",download_url);
                                        update_hashmap.put("thumb_image",thumb_download_url);

                                        mUserDatabase.updateChildren(update_hashmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    mProgressDialogue.dismiss();
                                                    Toast.makeText(SettingsActivity.this,"Profile picture updated",Toast.LENGTH_LONG).show();}
                                            }
                                        });
                                    }

                                    else {

                                        Toast.makeText(SettingsActivity.this," Unsuccessful",Toast.LENGTH_LONG).show();
                                        mProgressDialogue.dismiss();
                                    }
                                }
                            });




                        }
                        else {

                        }
                    }
                });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }

}


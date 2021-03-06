package com.example.sanjeev.skychat;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {
    private CircleImageView settingsDisplayProfileImage;
    private TextView settingsDisplayName;
    private TextView settingsDisplayStatus;
    private Button settingsChangeProfileImage;
    private Button settingsChangeStatus;

    private DatabaseReference getUserDataReference;
    private FirebaseAuth mAuth;
    private StorageReference storeProfileImagestorageRef;

    private final static int Gallery_Pick=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mAuth=FirebaseAuth.getInstance();
        String online_user_id=mAuth.getCurrentUser().getUid();
        getUserDataReference=FirebaseDatabase.getInstance().getReference().child("Users").child(online_user_id);
        storeProfileImagestorageRef=FirebaseStorage.getInstance().getReference().child("Profile_Images");

        settingsDisplayProfileImage=(CircleImageView)findViewById(R.id.settings_profile_image);
        settingsDisplayName=(TextView)findViewById(R.id.settings_username);
        settingsDisplayStatus=(TextView)findViewById(R.id.settings_user_status);
        settingsChangeProfileImage=(Button)findViewById(R.id.settings_change_profile_image_button);
        settingsChangeStatus=(Button)findViewById(R.id.settings_change_profile_status_button);

        getUserDataReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name=dataSnapshot.child("user_name").getValue().toString();
                String status=dataSnapshot.child("user_status").getValue().toString();
                String image=dataSnapshot.child("user_image").getValue().toString();
                String thumb_image=dataSnapshot.child("user_thumb_image").getValue().toString();
                settingsDisplayName.setText(name);
                settingsDisplayStatus.setText(status);


                //Loading Image
                Picasso.get().load(image).into(settingsDisplayProfileImage);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    settingsChangeProfileImage.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent galleryIntent=new Intent();
            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            startActivityForResult(galleryIntent,Gallery_Pick);
        }
    });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==Gallery_Pick && resultCode==RESULT_OK && data!=null)
        {
            Uri ImageUri=data.getData();
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                String user_id=mAuth.getCurrentUser().getUid();
                final Uri resultUri = result.getUri();
                final StorageReference filePath=storeProfileImagestorageRef.child(user_id+".jpg");
                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()) {
                        Toast.makeText(SettingsActivity.this, "Uploading Profile Image TO Database", Toast.LENGTH_LONG).show();
                        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                getUserDataReference.child("user_image").setValue(uri.toString())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(SettingsActivity.this,"Success",Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        });
//
               }
                    else
                    {
                        Toast.makeText(SettingsActivity.this,"Error Occurred While Uploading Image",Toast.LENGTH_LONG).show();
                    }
                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
        settingsChangeStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent statusIntent=new Intent(SettingsActivity.this,StatusActivity.class);
                startActivity(statusIntent);
            }
        });
    }
}

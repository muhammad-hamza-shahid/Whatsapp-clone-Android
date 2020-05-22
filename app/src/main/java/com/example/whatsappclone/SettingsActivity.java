package com.example.whatsappclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private Button btnUpdateAccountSetting;
    private EditText editUserName,editUserStatus;
    private CircleImageView userProfileImage;

    private String currentUserId;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth=FirebaseAuth.getInstance();
        currentUserId=mAuth.getCurrentUser().getUid();
        RootRef=FirebaseDatabase.getInstance().getReference();

        InitializeFields();

        //Setting visibity false so that the user cannot change the value of user name once set
        editUserName.setVisibility(View.INVISIBLE);

        btnUpdateAccountSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                UpdateSetting();
            }
        });

        //Retrieve user info to show in applicaton
        RetrieveUserInfo();
    }


    private void UpdateSetting() {
        //Getting values from EditText
        String setUserName=editUserName.getText().toString();
        String setUserStatus=editUserStatus.getText().toString();

        if (TextUtils.isEmpty(setUserName))
        {
            Toast.makeText(SettingsActivity.this,"Please write your user name..",Toast.LENGTH_SHORT).show();

        }else if(TextUtils.isEmpty(setUserStatus))
        {
            Toast.makeText(SettingsActivity.this,"Please write your status..",Toast.LENGTH_SHORT).show();
        }else
        {
            //Creating a HashMap to save values and then pass that HashMap object to RootRef
            HashMap<String ,String> profileMap = new HashMap<>();
                profileMap.put("uid",currentUserId);
                profileMap.put("name",setUserName);
                profileMap.put("status",setUserStatus);

            //Passing the HashMap to currentUser under User Node
            RootRef.child("Users").child(currentUserId).setValue(profileMap)
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if ((task.isSuccessful()))
                    {
                        //If the values are successfully added to current user then the user will be redirected to main activity
                        SendUserToMainActivity();
                        Toast.makeText(SettingsActivity.this,"Profie Updated Succefully..",Toast.LENGTH_SHORT).show();

                    }else
                    {
                        String message =task.getException().toString();
                        Toast.makeText(SettingsActivity.this,"Error :" +message,Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void InitializeFields() {
        btnUpdateAccountSetting = findViewById(R.id.btn_update_setting);
        editUserName=findViewById(R.id.edit_set_user_name);
        editUserStatus=findViewById(R.id.edit_set_profile_status);
        userProfileImage=findViewById(R.id.set_profile_image);
    }

    private void SendUserToMainActivity()
    {
        Intent MainIntent = new Intent(SettingsActivity.this, MainActivity.class);
        MainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(MainIntent);
        finish();
    }

    private void RetrieveUserInfo() {

        //Getting the info of current user o show in application
        RootRef.child("Users").child(currentUserId)
                .addValueEventListener(new ValueEventListener() {
                    //A DataSnapshot instance contains data from a Firebase Database location. Any time you read Database data, you receive the data as a DataSnapshot.
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        //If data exists and has name and image both
                        if((dataSnapshot.exists())&&(dataSnapshot.hasChild("name")&&(dataSnapshot.hasChild("image"))))
                        {
                            //Getting values from FireBase DataBase
                            String retrieveUserName = dataSnapshot.child("name").getValue().toString();
                            String retrieveStatus = dataSnapshot.child("status").getValue().toString();
                            String retrieveProfileImage = dataSnapshot.child("image").getValue().toString();

                            editUserName.setText(retrieveUserName);
                            editUserStatus.setText(retrieveStatus);


                        }else
                            //If data exists and only has name but not image
                            if((dataSnapshot.exists())&&(dataSnapshot.hasChild("name"))){
                            String retrieveUserName = dataSnapshot.child("name").getValue().toString();
                            String retrieveStatus = dataSnapshot.child("status").getValue().toString();

                            editUserName.setText(retrieveUserName);
                            editUserStatus.setText(retrieveStatus);

                        }else{
                                //If the dataSnapShot dont exist the user have to create info ie: name Status and image
                            editUserName.setVisibility(View.VISIBLE);
                            Toast.makeText(SettingsActivity.this,"Please Update your profile information",Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

}

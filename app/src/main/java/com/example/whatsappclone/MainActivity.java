package com.example.whatsappclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ViewPager myViewPager;
    private TabLayout myTabLayout;
    private TabsAccessorAdapter myTabsAccessorAdapter;

    private FirebaseUser currentUser=null;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initializing FireBase
        mAuth=FirebaseAuth.getInstance();
        currentUser=mAuth.getCurrentUser();
        RootRef= FirebaseDatabase.getInstance().getReference();

        //Adding custom toolbar to the main activity
        mToolbar=findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("WhatsApp");

        //Creating tabLayout to used with different Fragments
        myViewPager = findViewById(R.id.main_tabs_pager);
        myTabsAccessorAdapter = new TabsAccessorAdapter(getSupportFragmentManager());
        myViewPager.setAdapter(myTabsAccessorAdapter);

        myTabLayout=findViewById(R.id.main_tabs);
        myTabLayout.setupWithViewPager(myViewPager);



    }

    @Override
    protected void onStart() {
        super.onStart();

        //To check if the user has any account or not
        if(currentUser == null)
        {
            //Send to Login to Login user
            SendUserToLoginActivity();
        }else
        {
            //Verifying that the user has a user name and status and getting the info of current user
            VerifyUserExistence();
        }

    }

    private void VerifyUserExistence() {
        //Geting the user id of current user
        String currentUserId=mAuth.getCurrentUser().getUid();

        //Passing the ID of current user to check if he/she had set a name and status or not
        RootRef.child("Users").child((currentUserId)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //to check if the user had updated his/her profile and has a name and status
                if((dataSnapshot.child("name").exists()))
                {
                    //If the user has set a name and status he/she will be redirected to main activity
                    Toast.makeText(MainActivity.this,"Welcome",Toast.LENGTH_SHORT).show();
                }else
                {
                    //If the user has not set a user name and status he will be redirected ti create a username and a status
                    SendUserToSettingActivity();
                    Toast.makeText(MainActivity.this,"",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //Inlating the menu in the activity
        getMenuInflater().inflate(R.menu.options_menu,menu);
        return true;
    }

    //Performing action on the basis of item selected from the Toolbar Menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
         super.onOptionsItemSelected(item);
         if (item.getItemId()==R.id.main_logout_option)
         {
             //Pre compiled code for Signut provided by FireBase
            mAuth.signOut();
            SendUserToLoginActivity();
         }else if (item.getItemId()==R.id.main_settings_option)
         {
             //Sending user to Setting to update or Set username and password
            SendUserToSettingActivity();
         }else if (item.getItemId()==R.id.main_find_friends_option)
         {

         }else if(item.getItemId()==R.id.main_CreateGroup_option)
         {
             //Creating new ChatGroup
             RequestNewGroup();
         }

         return true;
    }

    private void RequestNewGroup() {

        //Creating a dialog box with 1 edit text and 2 buttons
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this,R.style.AlertDialog);

        //Setting title of dialog box
        dialogBuilder.setTitle("Enter Group Name : ");

        //Creating EditText to show on DialogBox
        final EditText EditGroupNameField=new EditText(MainActivity.this);
        EditGroupNameField.setHint("e.g Friends");

        //Setting the EditText on DialogBox
        dialogBuilder.setView(EditGroupNameField);

        //Setting button on DialogBox may be Positive means a button on right side
        dialogBuilder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                //Getting the group name which is inserted in the EditText
                String groupName=EditGroupNameField.getText().toString();
                if (TextUtils.isEmpty(groupName))
                {
                    Toast.makeText(MainActivity.this,"Please write group name",Toast.LENGTH_SHORT).show();
                }else{
                    //Create the group by passing groupname to create group function
                    CreateNewGroup(groupName);

                }
            }
        });

        //Creating the negative button which i think is left one
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                //Cancel the dialog box
               dialogInterface.cancel();
            }
        });

        //Showing the dialog box
        dialogBuilder.show();


    }

    private void CreateNewGroup(final String groupName) {

        //RootRef is DataBase Reference which is used to access DataBase of FireBase
        //Creating a child in DataBase by Groups name
        //And creating a Child ofGroups .child(groupName)
        RootRef.child("Groups").child(groupName).setValue("")
        .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(MainActivity.this,groupName+ "group is Created",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void SendUserToLoginActivity()
    {
        Intent LoginIntent = new Intent(MainActivity.this,LoginActivity.class);
        LoginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(LoginIntent);
    }

    private void SendUserToSettingActivity() {
        Intent SettingIntent = new Intent(MainActivity.this,SettingsActivity.class);

        //used to clear the task so user cannot go on pressing back button
        SettingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(SettingIntent);
        finish();
    }
}

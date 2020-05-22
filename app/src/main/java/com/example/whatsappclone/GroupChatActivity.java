package com.example.whatsappclone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

public class GroupChatActivity extends AppCompatActivity {

    private Toolbar mToolBar;
    private ImageButton SendMessageButton;
    private EditText editUserMessageInput;
    private TextView txtDisplayTextMessage;
    private ScrollView mScrollView;

    private String currentGroupName, currentUserId,currentUserName, currentDate, currentTime;

    private FirebaseAuth mAuth;
    private DatabaseReference usersRef,groupNameRef,groupMessageKeyRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        //Getting the name of the group which is selected
        currentGroupName=getIntent().getExtras().get("groupName").toString();
        Toast.makeText(GroupChatActivity.this,currentGroupName,Toast.LENGTH_SHORT).show();

        //Getting firebase instance
        mAuth=FirebaseAuth.getInstance();
        currentUserId=mAuth.getCurrentUser().getUid();

        //Getting access to database Users node
        usersRef= FirebaseDatabase.getInstance().getReference().child("Users");

        //Getting acces to database Groups node and further group name node
        groupNameRef=FirebaseDatabase.getInstance().getReference().child("Groups").child(currentGroupName);

        
        InitilizeFields();

        GetUserInfo();

        SendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SaveMessageInfoToDatabase();

                editUserMessageInput.setText(" ");

                mScrollView.fullScroll(ScrollView.FOCUS_DOWN);


            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        groupNameRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists())
                {
                    //To display all the previous messages
                    DisplayMessages(dataSnapshot);
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void InitilizeFields() {
        mToolBar= findViewById(R.id.group_chat_bar_layout);
        setSupportActionBar(mToolBar);
        //Setting title of toolbar same as selected groupname
        getSupportActionBar().setTitle(currentGroupName);

        SendMessageButton = findViewById(R.id.btn_send_message);
        editUserMessageInput = findViewById(R.id.edit_input_group_message);
        txtDisplayTextMessage = findViewById(R.id.txt_group_chat_text_display);
        mScrollView = findViewById(R.id.my_scroll_view);
    }


    private void GetUserInfo() {
        usersRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    //getting the user name of current user
                    currentUserName=dataSnapshot.child("name").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void SaveMessageInfoToDatabase() {

        String message=editUserMessageInput.getText().toString();

        //message key is made so each message has its wn unique key
        String messageKey = groupNameRef.push().getKey();

        if(TextUtils.isEmpty(message))
        {
            Toast.makeText(GroupChatActivity.this,"Please Write message..",Toast.LENGTH_SHORT).show();
        }else
        {
            //Getting the current date
            Calendar calForDate =  Calendar.getInstance();
            SimpleDateFormat currentDateFormat=new SimpleDateFormat("MMM ddd, yyyy");
            currentDate=currentDateFormat.format(calForDate.getTime());

            //Getting the current time
            Calendar calForTime =  Calendar.getInstance();
            SimpleDateFormat currentTimeFormat=new SimpleDateFormat("hh:mm a");
            currentTime=currentTimeFormat.format(calForTime.getTime());


            HashMap<String,Object> groupMessageKey = new HashMap<>();

            //passing the key of message in database reference
            groupNameRef.updateChildren(groupMessageKey);

            //Passing the child of message whose key is passed
            groupMessageKeyRef = groupNameRef.child(messageKey);

            //a hash map is created to save the message in it and then pass it to database
            HashMap<String,Object> messageInfoMap = new HashMap<>();
                messageInfoMap.put("name",currentUserName);
                messageInfoMap.put("message",message);
                messageInfoMap.put("date",currentDate);
                messageInfoMap.put("time",currentTime);

            //Updating the child of groupmessage in database
            groupMessageKeyRef.updateChildren(messageInfoMap);



        }
    }

    private void DisplayMessages(DataSnapshot snapshot)
    {
        //creating an iterator to iterate all the messages of database
        Iterator iterator = snapshot.getChildren().iterator();

        while (iterator.hasNext())
        {
            String chatDate = (String) ((DataSnapshot)iterator.next()).getValue();
            String chatMessage = (String) ((DataSnapshot)iterator.next()).getValue();
            String chatName = (String) ((DataSnapshot)iterator.next()).getValue();
            String chatTime = (String) ((DataSnapshot)iterator.next()).getValue();

            //Appending all the messages of group into textview
            txtDisplayTextMessage.append(chatName + " :\n" + chatMessage + "\n" + chatTime + "     "+chatDate + "\n\n\n");

            //This line will automatically scrolls the group mwssage whenever a new message is recieved
            mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
        }
    }
}

package com.example.whatsappclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SearchRecentSuggestionsProvider;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText edit_register_email, edit_register_password, edit_register_password_confirm;
    private Button btn_register;
    private TextView txt_already_have_account;

    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;

    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth=FirebaseAuth.getInstance();
        RootRef= FirebaseDatabase.getInstance().getReference();

        initializateFields();

        txt_already_have_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendUserToLoginActivity();
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CreateNewAccount();
            }
        });
    }
    private void CreateNewAccount(){
        String email=edit_register_email.getText().toString();
        String password=edit_register_password.getText().toString();
        String password_repeat=edit_register_password_confirm.getText().toString();
        if(TextUtils.isEmpty(email))
        {
            Toast.makeText(RegisterActivity.this,"Please enter Email",Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(password))
        {
            Toast.makeText(RegisterActivity.this,"Please enter Password",Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(password_repeat))
        {
            Toast.makeText(RegisterActivity.this,"Please repeat Password",Toast.LENGTH_SHORT).show();

        }
        else if(!password.equals(password_repeat))
        {
            Toast.makeText(RegisterActivity.this,"Password Mismatch",Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingBar.setTitle("Creating new Account");
            loadingBar.setMessage("Please wait, while we are creating new Account for you....");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();
            mAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful())
                            {
                                String currentUserId=mAuth.getCurrentUser().getUid();
                                RootRef.child("Users").child(currentUserId).setValue("");

                                SendUserToMainActivity();
                                Toast.makeText(RegisterActivity.this,"Account Created Successfully",Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }else {
                                String message= task.getException().toString();
                                Toast.makeText(RegisterActivity.this,"Error : " +message,Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });
        }

    }


    private void initializateFields() {
        edit_register_email=findViewById(R.id.edit_register_email);
        edit_register_password=findViewById(R.id.edit_register_password);
        edit_register_password_confirm=findViewById(R.id.edit_register_password_repeat);
        btn_register=findViewById(R.id.btn_register);
        txt_already_have_account=findViewById(R.id.txt_already_have_account);

        loadingBar=new ProgressDialog(this);
    }
    private void SendUserToLoginActivity()
    {
        Intent LoginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(LoginIntent);
    }
    private void SendUserToMainActivity()
    {
        Intent MainIntent = new Intent(RegisterActivity.this, MainActivity.class);
        MainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(MainIntent);
        finish();
    }
}

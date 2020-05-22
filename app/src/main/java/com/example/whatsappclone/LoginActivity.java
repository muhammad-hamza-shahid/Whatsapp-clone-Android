package com.example.whatsappclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private Button btn_login,btn_phoneLogin;
    private EditText edit_login_email, edit_login_password;
    private TextView txt_newAccount,txt_forgetPassword;

    private FirebaseAuth mAuth;

    private ProgressDialog loadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth=FirebaseAuth.getInstance();

        initializeField();

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Login user
                AllowUserToLogin();
            }
        });

        txt_newAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //to make new account
                SendUserToRegisterActivity();
            }
        });

        btn_phoneLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent phoneLoginIntent = new Intent(LoginActivity.this,PhoneLoginActivity.class);
                startActivity(phoneLoginIntent);
            }
        });
    }

    private void AllowUserToLogin()
    {
        //Getting values from text fields
        String email=edit_login_email.getText().toString();
        String password=edit_login_password.getText().toString();

        //To check if any feild is empty or not
        if(TextUtils.isEmpty(email))
        {
            Toast.makeText(LoginActivity.this,"Please enter Email",Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(password))
        {
            Toast.makeText(LoginActivity.this,"Please enter Password",Toast.LENGTH_SHORT).show();
        }else
        {
            loadingBar.setTitle("Creating new Account");
            loadingBar.setMessage("Please wait, while we are creating new Account for you....");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            //Passing email and password to FireBase Authentication Function
            mAuth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful())
                            {
                                //Sending user to MainActivity if the name and password is correct
                                SendUserToMainActivity();
                                Toast.makeText(LoginActivity.this,"Login Successfull",Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                            }else
                            {
                                String message= task.getException().toString();
                                Toast.makeText(LoginActivity.this,"Error : " +message,Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }

                        }
                    });
        }
    }

    private void initializeField() {
        btn_login=findViewById(R.id.btn_login);
        btn_phoneLogin=findViewById(R.id.btn_login_number);
        edit_login_email =findViewById(R.id.edit_login_email);
        edit_login_password =findViewById(R.id.edit_login_password);
        txt_newAccount=findViewById(R.id.txt_signup);
        txt_forgetPassword=findViewById(R.id.txt_forger_password);

        loadingBar=new ProgressDialog(this);
    }

    private void SendUserToMainActivity()
    {
        Intent MainIntent = new Intent(LoginActivity.this, MainActivity.class);
        //to disable the user from going previous age by pressing back button
        MainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(MainIntent);
        finish();
    }
    private void SendUserToRegisterActivity()
    {
        Intent LoginIntent = new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(LoginIntent);
    }
}

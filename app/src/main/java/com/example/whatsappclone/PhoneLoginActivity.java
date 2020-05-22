package com.example.whatsappclone;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class PhoneLoginActivity extends AppCompatActivity {

    private Button btnVerificationCode,btnVerify;
    private EditText editInputPhoneNumber,editInputVerificatinCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);

        InitializeField();

        btnVerificationCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnVerificationCode.setVisibility(View.INVISIBLE);
                editInputPhoneNumber.setVisibility(View.INVISIBLE);

                btnVerify.setVisibility(View.VISIBLE);
                editInputVerificatinCode.setVisibility(View.VISIBLE);
            }
        });


    }

    private void InitializeField()
    {
        btnVerificationCode=findViewById(R.id.btn_send_verification_code);
        btnVerify=findViewById(R.id.btn_verify);
        editInputPhoneNumber=findViewById(R.id.edit_phone_number_input);
        editInputVerificatinCode=findViewById(R.id.edit_verification_code_input);
    }
}

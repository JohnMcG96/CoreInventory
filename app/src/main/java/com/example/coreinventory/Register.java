package com.example.coreinventory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Register extends AppCompatActivity implements View.OnClickListener {

    private Button btnRegister;
    private EditText editEmail, editPass;
    private TextView txtSignIn;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() != null){
            finish();
            startActivity(new Intent(getApplicationContext(), MainMenu.class));
        }

        btnRegister = findViewById(R.id.btnRegister);

        editEmail = findViewById(R.id.txtEmail);
        editPass = findViewById(R.id.txtPassword);

        txtSignIn = findViewById(R.id.txtSignIn);

        btnRegister.setOnClickListener(this);
        txtSignIn.setOnClickListener(this);
    }

    private void registerUser() {
        String email = editEmail.getText().toString().trim();
        String password = editPass.getText().toString().trim();

        if(TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            return;
        }

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(Register.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                            finish();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        }
                        else {
                            Toast.makeText(Register.this, "Registration Unsuccessful", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onClick(View view) {
        if(view == btnRegister) {
            registerUser();
        }

        if(view == txtSignIn) {
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            finish();
            startActivity(new Intent(this, MainActivity.class));
            return true;
        }
        return false;
    }
}

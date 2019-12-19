package com.example.coreinventory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnLogin;
    private EditText editEmail, editPass;
    private TextView txtSignUp;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() != null){
            finish();
            startActivity(new Intent(getApplicationContext(), MainMenu.class));
        }

        btnLogin = findViewById(R.id.btnLogin);

        editEmail = findViewById(R.id.txtEmail);
        editPass = findViewById(R.id.txtPassword);

        txtSignUp = findViewById(R.id.txtSignUp);

        btnLogin.setOnClickListener(this);
        txtSignUp.setOnClickListener(this);
    }

    private void userLogin(){
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

        firebaseAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            finish();
                            startActivity(new Intent(getApplicationContext(), MainMenu.class));
                        }
                        else {
                            Toast.makeText(MainActivity.this, "Login Unsuccessful", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onClick(View view) {
        if(view == btnLogin) {
            userLogin();
        }

        if(view == txtSignUp) {
            finish();
            startActivity(new Intent(this, Register.class));
        }
    }
}

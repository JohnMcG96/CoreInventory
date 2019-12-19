package com.example.coreinventory;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class MainMenu extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth firebaseAuth;
    private Button btnLogout, btnOpenAdd, btnOpenRemove, btnEditItems, btnOpenItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }



        btnOpenAdd = findViewById(R.id.btnOpenAdd);
        btnOpenRemove = findViewById(R.id.btnOpenRemove);
        btnEditItems = findViewById(R.id.btnEditItems);
        btnOpenItems = findViewById(R.id.btnOpenItems);
        btnLogout = findViewById(R.id.btnLogout);

        btnOpenAdd.setOnClickListener(this);
        btnOpenRemove.setOnClickListener(this);
        btnEditItems.setOnClickListener(this);
        btnOpenItems.setOnClickListener(this);
        btnLogout.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view == btnOpenAdd){
            finish();
            startActivity(new Intent(this, AddItem.class));
        }
        if(view == btnOpenRemove){
            finish();
            startActivity(new Intent(this, RemoveItem.class));
        }
        if(view == btnEditItems){
            finish();
            startActivity(new Intent(this, EditItem.class));
        }
        if(view == btnOpenItems){
            finish();
            startActivity(new Intent(this, ViewItems.class));
        }
        if(view == btnLogout){
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }
    }
}

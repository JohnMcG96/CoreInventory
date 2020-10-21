package com.example.coreinventory;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class ReportMenu extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth firebaseAuth;
    private Button btnLogout, btnViewAll, btnViewByLocation, btnViewByName, btnMainMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_menu);

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }

        btnViewAll = findViewById(R.id.btnViewAll);
        btnViewByLocation = findViewById(R.id.btnViewByLocation);
        btnViewByName = findViewById(R.id.btnViewByName);
        btnMainMenu = findViewById(R.id.btnMainMenu);
        btnLogout = findViewById(R.id.btnLogout);

        btnViewAll.setOnClickListener(this);
        btnViewByLocation.setOnClickListener(this);
        btnViewByName.setOnClickListener(this);
        btnMainMenu.setOnClickListener(this);
        btnLogout.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view == btnViewAll){
            finish();
            startActivity(new Intent(this, ItemByID.class));
        }
        if(view == btnViewByLocation){
            finish();
            startActivity(new Intent(this, ItemByLocation.class));
        }
        if(view == btnViewByName){
            finish();
            startActivity(new Intent(this, ItemByName.class));
        }
        if(view == btnMainMenu){
            finish();
            startActivity(new Intent(this, MainMenu.class));
        }
        if(view == btnLogout){
            firebaseAuth.signOut();
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
            startActivity(new Intent(this, MainMenu.class));
            return true;
        }
        return false;
    }
}

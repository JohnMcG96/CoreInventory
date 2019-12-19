package com.example.coreinventory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewItems extends AppCompatActivity implements View.OnClickListener{

    private ListView lstView;

    private Button btnLogout;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private ArrayList<String> list;
    private ArrayAdapter<String> adapter;
    private ItemData itemData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_items);itemData = new ItemData();
        lstView = findViewById(R.id.lstView);
        list = new ArrayList<>();
        adapter = new ArrayAdapter<String>(this, R.layout.listlayout, R.id.txtCodeData, list);

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Items");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot itemSnap: dataSnapshot.getChildren()){
                    itemData = itemSnap.getValue(ItemData.class);
                    list.add(itemData.toString());
                }

                lstView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(this);
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

    @Override
    public void onClick(View view) {
        if(view == btnLogout){
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }
    }
}

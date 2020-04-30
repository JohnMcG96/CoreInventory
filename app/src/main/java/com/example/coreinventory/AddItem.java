package com.example.coreinventory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Application;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class AddItem extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private Button btnLogout, btnAddItem, btnDetect;
    private EditText txtItemName, txtItemQuant, txtItemPrice, txtItemCode, txtItemDate;
    private Spinner txtItemLoc;
    private CheckBox itemThird, itemCheck;
    private CameraView cameraView;
    private AlertDialog waitingDialog;

    private boolean itemExists = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        waitingDialog = new SpotsDialog.Builder().setContext(this)
                .setMessage("Please wait").setCancelable(false).build();

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Items");

        cameraView = findViewById(R.id.cameraView);
        btnDetect = findViewById(R.id.btnDetect);

        cameraView.addCameraKitListener(new CameraKitEventListener() {
            @Override
            public void onEvent(CameraKitEvent cameraKitEvent) {

            }

            @Override
            public void onError(CameraKitError cameraKitError) {

            }

            @Override
            public void onImage(CameraKitImage cameraKitImage) {
                waitingDialog.show();
                Bitmap bitmap = cameraKitImage.getBitmap();
                bitmap = Bitmap.createScaledBitmap(bitmap, cameraView.getWidth(), cameraView.getHeight(), false);
                cameraView.stop();
                runDetector(bitmap);
            }

            @Override
            public void onVideo(CameraKitVideo cameraKitVideo) {

            }
        });

        if(firebaseAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }

        btnLogout = findViewById(R.id.btnLogout);
        btnAddItem = findViewById(R.id.btnAddItem);

        txtItemName = findViewById(R.id.txtItemName);
        txtItemQuant = findViewById(R.id.txtQuant);
        txtItemPrice = findViewById(R.id.txtPrice);
        txtItemCode = findViewById(R.id.txtCode);
        txtItemLoc = findViewById(R.id.txtItemLoc);
        txtItemDate = findViewById(R.id.txtItemDate);
        itemThird = findViewById(R.id.itemThird);
        itemCheck = findViewById(R.id.itemCheck);

        btnLogout.setOnClickListener(this);
        btnDetect.setOnClickListener(this);
        btnAddItem.setOnClickListener(this);

        ArrayAdapter<String> itemLocAd = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.ItemLocation));
        itemLocAd.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        txtItemLoc.setAdapter(itemLocAd);
    }

    private void runDetector(Bitmap bitmap){
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
        FirebaseVisionBarcodeDetectorOptions options = new FirebaseVisionBarcodeDetectorOptions.Builder()
                .setBarcodeFormats(FirebaseVisionBarcode.FORMAT_ALL_FORMATS).build();
        FirebaseVisionBarcodeDetector detector =
                FirebaseVision.getInstance().getVisionBarcodeDetector(options);
        detector.detectInImage(image).addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionBarcode>>() {
            @Override
            public void onSuccess(List<FirebaseVisionBarcode> firebaseVisionBarcodes) {
                processResult(firebaseVisionBarcodes);
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddItem.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void processResult(List<FirebaseVisionBarcode> firebaseVisionBarcodes) {
        String code = "";
        for (FirebaseVisionBarcode item : firebaseVisionBarcodes) {
            code = item.getRawValue();
            txtItemCode.setText(code);
        }
        final String finalCode = code;
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    if (snapshot.child(finalCode).exists()) {
                        itemExists = true;
                        txtItemCode.setText("");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        waitingDialog.dismiss();
        cameraView.start();
    }

    private void addItem(){

        String code = txtItemCode.getText().toString().trim();
        String name = txtItemName.getText().toString().trim();
        int quant = Integer.parseInt(txtItemQuant.getText().toString().trim());
        double price = Double.parseDouble(txtItemPrice.getText().toString().trim());
        String location = txtItemLoc.getSelectedItem().toString().trim();
        String date = txtItemDate.getText().toString().trim();
        boolean third = itemThird.isChecked();
        boolean checked = itemCheck.isChecked();

        if(TextUtils.isEmpty(code)){
            Toast.makeText(this, "Please scan a valid barcode.", Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty(name)){
            Toast.makeText(this, "Please enter a valid Item Name.", Toast.LENGTH_LONG).show();
        }
        else if(quant == 0){
            Toast.makeText(this, "Please enter a valid Item Quantity.", Toast.LENGTH_LONG).show();
        }
        else if(price == 0){
            Toast.makeText(this, "Please enter a valid Item Price.", Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.equals(location, "Item Location")){
            Toast.makeText(this, "Please enter a valid Item Location.", Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty(date)){
            Toast.makeText(this, "Please enter a valid Item Purchase Date.", Toast.LENGTH_LONG).show();
        }
        else{
            ItemData itemData = new ItemData(code, name, quant, price, location, date, third, checked);

            databaseReference.child(code).setValue(itemData);
            Toast.makeText(this, "Item added.", Toast.LENGTH_LONG).show();
            txtItemCode.setText("");
            txtItemName.setText("");
            txtItemQuant.setText("");
            txtItemPrice.setText("");
            txtItemLoc.setSelection(0);
            txtItemDate.setText("");
            itemThird.setChecked(false);
            itemCheck.setChecked(false);
            itemExists = false;
        }
    }

    @Override
    public void onClick(View view) {
        if(view == btnLogout){
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }
        if(view == btnAddItem){
            addItem();
        }
        if(view == btnDetect){
            cameraView.start();
            cameraView.captureImage();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraView.start();
        if (itemExists == true)
            Toast.makeText(this, "Item already exists, please use Update Item.", Toast.LENGTH_LONG).show();
        itemExists = false;

    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraView.stop();
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

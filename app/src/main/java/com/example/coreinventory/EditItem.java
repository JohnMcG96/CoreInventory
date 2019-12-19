package com.example.coreinventory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.List;

import dmax.dialog.SpotsDialog;

public class EditItem extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    private Button btnLogout, btnDetect, btnUpdate;
    private EditText txtItemName, txtItemQuant, txtItemPrice, txtItemCode;
    private CameraView cameraView;
    private AlertDialog waitingDialog;

    private ItemData itemData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);setContentView(R.layout.activity_edit_item);setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        waitingDialog = new SpotsDialog.Builder().setContext(this)
                .setMessage("Please wait").setCancelable(false).build();

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Items");

        cameraView = findViewById(R.id.cameraView);
        btnDetect = findViewById(R.id.btnDetect);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnLogout = findViewById(R.id.btnLogout);

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

        txtItemName = findViewById(R.id.txtItemName);
        txtItemQuant = findViewById(R.id.txtQuant);
        txtItemPrice = findViewById(R.id.txtPrice);
        txtItemCode = findViewById(R.id.txtCode);

        btnLogout.setOnClickListener(this);
        btnDetect.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);
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
                        Toast.makeText(EditItem.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void processResult(List<FirebaseVisionBarcode> firebaseVisionBarcodes) {
        String code = "";
        for (FirebaseVisionBarcode item : firebaseVisionBarcodes) {
            code = item.getRawValue();
            txtItemCode.setText(code);
        }
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Items").child(code);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    ItemData itemData = dataSnapshot.getValue(ItemData.class);
                    txtItemName.setText(itemData.getItemName());
                    txtItemQuant.setText("" + itemData.getItemQuant());
                    txtItemPrice.setText("" + itemData.getItemPrice());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        waitingDialog.dismiss();
        cameraView.start();
    }

    private void editItem(String itemCode){
        databaseReference = FirebaseDatabase.getInstance()
                .getReference("Items").child(itemCode);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        Toast.makeText(EditItem.this, "Item Updated", Toast.LENGTH_SHORT).show();
        txtItemCode.setText("");
        txtItemName.setText("");
        txtItemQuant.setText("");
        txtItemPrice.setText("");
    }

    @Override
    public void onClick(View view) {
        if(view == btnLogout){
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }
        if(view == btnDetect){
            cameraView.start();
            cameraView.captureImage();
        }
        if(view == btnUpdate){
            editItem(txtItemCode.getText().toString().trim());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraView.start();
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

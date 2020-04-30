package com.example.coreinventory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
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

public class RemoveItem extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    private Button btnLogout, btnDetect, btnRemove;
    private EditText txtItemName, txtItemQuant, txtItemPrice, txtItemCode, txtItemDate, txtItemLoc;
    private CheckBox itemThird, itemCheck;
    private CameraView cameraView;
    private AlertDialog waitingDialog;

    private ItemData itemData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_item);

        waitingDialog = new SpotsDialog.Builder().setContext(this)
                .setMessage("Please wait").setCancelable(false).build();

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Items");

        cameraView = findViewById(R.id.cameraView);
        btnDetect = findViewById(R.id.btnDetect);
        btnRemove = findViewById(R.id.btnRemove);
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
        txtItemLoc = findViewById(R.id.txtItemLoc);
        txtItemDate = findViewById(R.id.txtItemDate);
        itemThird = findViewById(R.id.itemThird);
        itemCheck = findViewById(R.id.itemCheck);

        btnLogout.setOnClickListener(this);
        btnDetect.setOnClickListener(this);
        btnRemove.setOnClickListener(this);
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
                        Toast.makeText(RemoveItem.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
                    int spinSize = getResources().getStringArray(R.array.ItemLocation).length;
                    ItemData itemData = dataSnapshot.getValue(ItemData.class);
                    txtItemName.setText(itemData.getItemName());
                    txtItemQuant.setText("" + itemData.getItemQuant());
                    txtItemPrice.setText("" + itemData.getItemPrice());
                    txtItemDate.setText(itemData.getItemDatePur());
                    txtItemLoc.setText("" + itemData.getItemLoc());
                    if (itemData.getItemThird() == true)
                        itemThird.setChecked(true);
                    else
                        itemThird.setChecked(false);

                    if (itemData.getItemCheck() == true)
                        itemCheck.setChecked(true);
                    else
                        itemCheck.setChecked(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        waitingDialog.dismiss();
        cameraView.start();
    }

    private void removeItem(String itemCode){
        databaseReference = FirebaseDatabase.getInstance()
                .getReference("Items").child(itemCode);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    databaseReference.removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        Toast.makeText(RemoveItem.this, "Item Removed", Toast.LENGTH_SHORT).show();
        txtItemCode.setText("");
        txtItemName.setText("");
        txtItemQuant.setText("");
        txtItemPrice.setText("");
        txtItemLoc.setSelection(0);
        txtItemDate.setText("");
        itemThird.setChecked(false);
        itemCheck.setChecked(false);
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
        if(view == btnRemove){
            removeItem(txtItemCode.getText().toString().trim());
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

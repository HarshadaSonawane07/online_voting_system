package com.onlie.voting.onlinevotingsystem;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CameraActivity extends AppCompatActivity {

    private Button detectFaceButton;
    private PreviewView previewView;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private ExecutorService cameraExecutor;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        detectFaceButton = findViewById(R.id.detect_face_btn);
        previewView = findViewById(R.id.camera_view);

        Intent i = getIntent();
        phone = i.getStringExtra("phone");

        detectFaceButton.setOnClickListener(view -> {
            // Your logic for handling the detect face button click
            startNextActivity();
        });

        cameraExecutor = Executors.newSingleThreadExecutor();
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        // Check and request camera permissions at runtime
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            // If permissions are already granted, proceed to start the camera
            startCamera();
        }
    }

    private void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
        // Set up the Preview use case
        Preview preview = new Preview.Builder().build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                .build();

        // Set the surface provider
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        try {
            // Unbind any existing use cases before binding new ones
            cameraProvider.unbindAll();

            // Bind the Preview use case
            cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, preview);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startCamera() {
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void startNextActivity() {
        // TODO: Add code to start the next activity
        Intent intent = new Intent(CameraActivity.this, SelectParty.class);
        startActivity(intent);
        finish(); // Optional: Finish the current activity if you don't want to go back to it
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
    }
}

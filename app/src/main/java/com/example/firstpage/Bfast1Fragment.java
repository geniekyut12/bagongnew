package com.example.firstpage;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Bfast1Fragment extends AppCompatActivity {
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;
    private ImageView imageView;
    private TextView resultText;
    private Bitmap imageBitmap;

    private static final Map<String, Double> foodCO2Map = new HashMap<>();

    static {
        foodCO2Map.put("beef", 27.0);
        foodCO2Map.put("chicken", 6.9);
        foodCO2Map.put("pork", 7.6);
        foodCO2Map.put("rice", 4.5);
        foodCO2Map.put("vegetable", 2.0);
        foodCO2Map.put("fruit", 1.1);
        foodCO2Map.put("cheese", 13.5);
        foodCO2Map.put("milk", 3.2);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bfast1_fragment);

        Button captureButton = findViewById(R.id.captureButton);
        Button pickImageButton = findViewById(R.id.pickImageButton);
        imageView = findViewById(R.id.imageView);
        resultText = findViewById(R.id.resultText);

        captureButton.setOnClickListener(v -> dispatchTakePictureIntent());
        pickImageButton.setOnClickListener(v -> dispatchPickImageIntent());
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void dispatchPickImageIntent() {
        Intent pickImageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickImageIntent, REQUEST_IMAGE_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            try {
                if (requestCode == REQUEST_IMAGE_CAPTURE) {
                    Bundle extras = data.getExtras();
                    if (extras != null) {
                        imageBitmap = (Bitmap) extras.get("data");
                    }
                } else if (requestCode == REQUEST_IMAGE_PICK) {
                    Uri imageUri = data.getData();
                    if (imageUri != null) {
                        InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        if (imageStream != null) {
                            imageBitmap = BitmapFactory.decodeStream(imageStream);
                            imageStream.close(); // Close input stream to prevent memory leaks
                        }
                    } else {
                        resultText.setText("Error: Selected image is null.");
                        return;
                    }
                }

                if (imageBitmap != null) {
                    imageView.setImageBitmap(imageBitmap);
                    processImage();
                } else {
                    resultText.setText("Error: Failed to load image.");
                }

            } catch (IOException e) {
                resultText.setText("Error: " + e.getMessage());
            }
        } else {
            resultText.setText("No image selected.");
        }
    }


    private void processImage() {
        if (imageBitmap == null) {
            resultText.setText("Error: No image available.");
            return;
        }

        InputImage image = InputImage.fromBitmap(imageBitmap, 0);
        com.google.mlkit.vision.label.ImageLabeler labeler =
                ImageLabeling.getClient(new ImageLabelerOptions.Builder()
                        .setConfidenceThreshold(0.7f) // Lower confidence threshold
                        .build());

        labeler.process(image)
                .addOnSuccessListener(this::filterFoodLabels)
                .addOnFailureListener(e -> resultText.setText("Error: " + e.getMessage()));
    }

    private void filterFoodLabels(List<ImageLabel> labels) {
        boolean foodDetected = false;
        StringBuilder results = new StringBuilder();

        for (ImageLabel label : labels) {
            String labelText = label.getText().toLowerCase();

            for (String food : foodCO2Map.keySet()) {
                if (labelText.contains(food)) { // Use contains() for better matching
                    foodDetected = true;
                    double co2Emission = foodCO2Map.get(food);
                    results.append(label.getText()).append(" (Confidence: ")
                            .append(label.getConfidence()).append(")\n")
                            .append("Estimated CO2 Emission: ")
                            .append(co2Emission).append(" kg CO2/kg\n");
                    break; // Stop checking once matched
                }
            }
        }

        resultText.setText(foodDetected ? results.toString() : "No food detected.");
    }

}
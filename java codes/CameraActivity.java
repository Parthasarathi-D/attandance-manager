package app.login;



import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

public class CameraActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private ImageView imageView;
    private Button cameraButton;
    private Button confirmButton;
    private Bitmap capturedImage; // To store the captured image

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);


        imageView = findViewById(R.id.imageView);
        cameraButton = findViewById(R.id.cameraButton);
        confirmButton = findViewById(R.id.confirmButton);

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (capturedImage != null) {
                    // If the user confirms, upload the image to the database
                    uploadImageToLambdaFunction();
                } else {
                    Toast.makeText(CameraActivity.this, "No image to upload", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                capturedImage = (Bitmap) extras.get("data");
                if (capturedImage != null) {
                    imageView.setImageBitmap(capturedImage);
                }
            }
        }
    }

    private void uploadImageToLambdaFunction() {
        if (capturedImage != null) {
            // Convert the Bitmap image to base64
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            capturedImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageBytes = baos.toByteArray();
            String base64Image = Base64.encodeToString(imageBytes, Base64.DEFAULT);

            SessionManager sessionManager = new SessionManager(this);
            String studentId = sessionManager.getUserId();
            String studentName = sessionManager.getUsername();
            String staffId = "";
            String staffName = "";
            String SubjectCode = "";
            String location = "";
            String currentDate = "";

            Intent intent = getIntent();
            if (intent != null) {
                String scannedData = intent.getStringExtra("SCANNED_DATA");
                String[] splitData = scannedData.split("\n");

                if (splitData.length >= 5) {
                    staffId = splitData[0];
                    staffName = splitData[1];
                    SubjectCode = splitData[2];
                    location = splitData[3];
                    currentDate = splitData[4];
                } else {
                    // Handle invalid scanned data format
                    Toast.makeText(this, "Invalid scanned data format", Toast.LENGTH_SHORT).show();
                    return;
                }
            } else {
                // Handle missing intent data
                Toast.makeText(this, "Intent data not found", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validate required data
            if (studentId.isEmpty() || studentName.isEmpty() || staffId.isEmpty() || staffName.isEmpty() || location.isEmpty() || currentDate.isEmpty()) {
                // Handle missing required data
                Toast.makeText(this, "Missing required data", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create a JSON object with image data and other required fields
            JSONObject postData = new JSONObject();
            try {
                postData.put("rollno", studentId);
                postData.put("name", studentName);
                postData.put("staffId", staffId);
                postData.put("staffName", staffName);
                postData.put("subjectCode", SubjectCode);
                postData.put("location", location);
                postData.put("currentDate", currentDate);
                postData.put("photo", base64Image);
            } catch (JSONException e) {
                e.printStackTrace();
                // Handle JSON creation error
                Toast.makeText(this, "Error creating JSON data", Toast.LENGTH_SHORT).show();
                return;
            }

            // Send HTTP POST request with JSON payload to your Lambda function
            String lambdaUrl = "https://7miduplh0d.execute-api.eu-north-1.amazonaws.com/default/lambda2";
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, lambdaUrl, postData,
                    response -> {
                        // Handle Lambda function response if needed
                        Toast.makeText(this, "Image uploaded to Lambda function!", Toast.LENGTH_SHORT).show();
                    },
                    error -> {
                        // Handle error if the request fails
                        Toast.makeText(this, "Image uploaded to Lambda function!", Toast.LENGTH_SHORT).show();
                    });

            // Add the request to the RequestQueue (assuming you're using Volley)
            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(request);
        } else {
            Toast.makeText(this, "No image to upload", Toast.LENGTH_SHORT).show();
        }
    }


}

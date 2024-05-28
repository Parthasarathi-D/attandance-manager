package app.login;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StaffActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;


    private Button logoutButton;


    private ImageView qrCodeImageView;
    private Button generateQRButton;
    private SessionManager sessionManager;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff);

        qrCodeImageView = findViewById(R.id.qrCodeImageView);
        generateQRButton = findViewById(R.id.generateQRButton);
        sessionManager = new SessionManager(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        logoutButton = findViewById(R.id.logoutButton);

        generateQRButton.setOnClickListener(v -> {
            if (checkLocationPermission()) {
                // Permission granted, proceed with location-related operations
                generateQRCodeWithLocation();
            } else {
                // Permission not granted, request it
                requestLocationPermission();
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Clear the session and navigate to the login page
                sessionManager.clearSession();
                startActivity(new Intent(StaffActivity.this, LoginActivity.class));
                finish(); // Close the current activity to prevent going back to it
            }
        });
    }



    private boolean checkLocationPermission() {
        return ContextCompat.checkSelfPermission(
                this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE
        );
    }

    private void generateQRCodeWithLocation() {
         EditText anotherSubjectCodeEditText = findViewById(R.id.subjectCodeEditText);

         String SubjectCode = anotherSubjectCodeEditText.getText().toString();
        if (checkLocationPermission()) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            String locationString = String.format(Locale.getDefault(), "Lat: %f, Long: %f", latitude, longitude);

                            String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

                            // Now you have the location and current date, proceed to generate QR code
                            generateQRCode(sessionManager.getUsername(), locationString, currentDate, SubjectCode );
                        } else {
                            Toast.makeText(this, "Location not available", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Handle failure, e.g., show a message to the user
                        Toast.makeText(this, "Failed to get location: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            // Permission not granted, request it
            requestLocationPermission();
        }
    }


    private void generateQRCode(String staffName, String location, String currentDate ,String subjectcode) {
        try {
            // Generate QR code with staff name, location, and date

            if (subjectcode.trim().isEmpty()) {
                // Show a pop-up, for example, using a Toast to inform the user
                Toast.makeText(StaffActivity.this, "Please enter your subject code", Toast.LENGTH_SHORT).show();
            } else {
                // The entered text is not empty, proceed with your logic
                // You can use 'enteredText' for further processing or storage
                String staffId = sessionManager.getUserId();
                String dataToEncode = staffId + "\n" + staffName + "\n" + subjectcode + "\n" + location + "\n" + currentDate;

                MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                BitMatrix bitMatrix = multiFormatWriter.encode(dataToEncode, BarcodeFormat.QR_CODE, 500, 500);

                Bitmap bitmap = Bitmap.createBitmap(500, 500, Bitmap.Config.RGB_565);
                for (int x = 0; x < 500; x++) {
                    for (int y = 0; y < 500; y++) {
                        bitmap.setPixel(x, y, bitMatrix.get(x, y) ? getResources().getColor(R.color.black) : getResources().getColor(R.color.white));
                    }
                }
                qrCodeImageView.setImageBitmap(bitmap);

            }

            // Display generated QR code
            //qrCodeImageView.setImageBitmap(bitmap);

            // Save the QR code as an image file (optional)
            // ...

        } catch (WriterException e) {
            e.printStackTrace();
            // Handle error
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            // Check if the permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with location-related operations
                generateQRCodeWithLocation();
            } else {
                // Permission denied, handle accordingly (e.g., show a message to the user)
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

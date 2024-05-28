// StudentActivity.java
package app.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.result.ActivityResultLauncher;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class StudentActivity extends AppCompatActivity {

    private TextView welcomeTextView;
    private TextView scannedResultTextView;
    private Button logoutButton;
    private Button scanQRButton;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        welcomeTextView = findViewById(R.id.welcomeTextView);
        scannedResultTextView = findViewById(R.id.scannedResultTextView);
        logoutButton = findViewById(R.id.logoutButton);
        scanQRButton = findViewById(R.id.scanQRButton);
        sessionManager = new SessionManager(this);

        // Retrieve the logged-in student's name from the session using SessionManager
        String studentName = sessionManager.getUsername();

        // Set the dynamic value in the welcome TextView
        welcomeTextView.setText(getString(R.string.welcome_message, studentName));

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Clear the session and navigate to the login page
                sessionManager.clearSession();
                startActivity(new Intent(StudentActivity.this, LoginActivity.class));
                finish(); // Close the current activity to prevent going back to it
            }
        });

        scanQRButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Initiate QR code scanning
                initiateQRScan();
            }
        });
    }

    private void initiateQRScan() {
        // Assuming you have methods to get user information from the session manager
        String userId = sessionManager.getUserId();
        String userName = sessionManager.getUsername();

        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to flash on");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLauncher.launch(options);
    }




    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result -> {
        //startActivity(new Intent(StudentActivity.this, CameraActivity.class));
        if (result.getContents() != null) {
            // Retrieve scanned data
            String scannedData = result.getContents();

            // Pass the scanned data to the CameraActivity
            Intent cameraIntent = new Intent(StudentActivity.this, CameraActivity.class);
            cameraIntent.putExtra("SCANNED_DATA", scannedData);
            startActivity(cameraIntent);
        }
    });
}

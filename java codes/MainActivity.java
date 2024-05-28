package app.login;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sessionManager = new SessionManager(this);

        if (sessionManager.isLoggedIn()) {
            String role = sessionManager.getUserRole() ;
            if ("student".equalsIgnoreCase(role)) {
                startActivity(new Intent(MainActivity.this, StudentActivity.class));
            } else if ("staff".equalsIgnoreCase(role)) {
                startActivity(new Intent(MainActivity.this, StaffActivity.class));
            }
        } else {
            startActivity(new Intent(this, LoginActivity.class));
        }

        finish();
    }
}


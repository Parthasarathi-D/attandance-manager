package app.login;
// RegistrationActivity.java
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RegistrationActivity extends AppCompatActivity {

    private EditText idEditText, usernameEditText, passwordEditText, roleEditText;
    private Button registerButton,loginPageButton;

    private MyDbHelper dbHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        dbHelper = new MyDbHelper(this);
        sessionManager = new SessionManager(this);

        idEditText = findViewById(R.id.idEditText);
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        roleEditText = findViewById(R.id.roleEditText);
        registerButton = findViewById(R.id.registerButton);
        loginPageButton = findViewById(R.id.loginPageButton);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = idEditText.getText().toString();
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String role = roleEditText.getText().toString();

                if (TextUtils.isEmpty(id) || TextUtils.isEmpty(username) || TextUtils.isEmpty(password) || TextUtils.isEmpty(role)) {
                    Toast.makeText(RegistrationActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                    return;
                }
                if ("staff".equalsIgnoreCase(role) && !isValidStaffId(id)) {
                    Toast.makeText(RegistrationActivity.this, "Invalid staff ID pattern", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (dbHelper.isIdExists(id)) {
                    Toast.makeText(RegistrationActivity.this, "ID already exists. Please choose a different one.", Toast.LENGTH_SHORT).show();
                    return;
                }

                long registrationResult = dbHelper.insertUser(id, username, password, role);

                if (registrationResult != -1) {
                    sessionManager.saveSessionToken("your_session_token");
                    startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                    finish();
                } else {
                    Toast.makeText(RegistrationActivity.this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
                }

            }
            private boolean isValidStaffId(String id) {

                return id.matches("^S\\d{2,}[@#$%^&+=]$");
            }
        });


        loginPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle the button click to navigate to the LoginActivity
                startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
            }
        });
    }
}


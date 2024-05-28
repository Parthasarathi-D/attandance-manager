package app.login;
// LoginActivity.java
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText idEditText, passwordEditText, roleEditText;
    private Button loginButton,registerPageButton;

    private MyDbHelper dbHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper = new MyDbHelper(this);
        sessionManager = new SessionManager(this);

        idEditText = findViewById(R.id.idEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        roleEditText = findViewById(R.id.roleEditText);
        loginButton = findViewById(R.id.loginButton);
        registerPageButton = findViewById(R.id.registerPageButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = idEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String role = roleEditText.getText().toString();

                if (TextUtils.isEmpty(id) || TextUtils.isEmpty(password) || TextUtils.isEmpty(role)) {
                    Toast.makeText(LoginActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check credentials against the database based on role
                Cursor cursor = dbHelper.getUserByIdRole(id, password, role);

                if (cursor.moveToFirst()) {

                    String userName = cursor.getString(cursor.getColumnIndexOrThrow(MyDbHelper.COLUMN_NAME));

                    sessionManager.saveUsername(userName);
                    sessionManager.saveSessionToken("your_session_token");
                    sessionManager.saveUserRole(role);
                    sessionManager.saveUserId(id);

                    // Determine the appropriate activity based on the role
                    if ("student".equalsIgnoreCase(role)) {
                        startActivity(new Intent(LoginActivity.this, StudentActivity.class));
                    } else if ("staff".equalsIgnoreCase(role)) {
                        startActivity(new Intent(LoginActivity.this, StaffActivity.class));
                    }

                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Invalid credentials. Please try again.", Toast.LENGTH_SHORT).show();
                }


                cursor.close();
            }
        });
        registerPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle the button click to navigate to the RegistrationActivity
                startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
            }
        });
    }
}

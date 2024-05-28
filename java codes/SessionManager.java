package app.login;// SessionManager.java
import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME = "user_prefs";

    private static final String USER_ROLE = "user_prefs";

    private static final String KEY_SESSION_TOKEN = "session_token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USERNAME = "username"; // Add this line

    private final SharedPreferences sharedPreferences;

    public SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveSessionToken(String sessionToken) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_SESSION_TOKEN, sessionToken);
        editor.apply();
    }

    public String getSessionToken() {
        return sharedPreferences.getString(KEY_SESSION_TOKEN, null);
    }

    public void saveUserId(String userId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USER_ID, userId);
        editor.apply();
    }

    public void saveUserRole(String role) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USER_ROLE, role);
        editor.apply();
    }

    public String getUserRole() {
        return sharedPreferences.getString(USER_ROLE, null);
    }


    public String getUserId() {
        return sharedPreferences.getString(KEY_USER_ID, null);
    }

    // Add the following methods

    public void saveUsername(String username) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USERNAME, username);
        editor.apply();
    }

    public String getUsername() {
        return sharedPreferences.getString(KEY_USERNAME, null);
    }

    public void clearSession() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_SESSION_TOKEN);
        editor.remove(KEY_USER_ID);
        editor.remove(KEY_USERNAME); // Add this line
        editor.apply();
    }

    public boolean isLoggedIn() {
        return getSessionToken() != null && getUserId() != null;
    }
}

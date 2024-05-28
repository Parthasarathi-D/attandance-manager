package app.login;
// MyDbHelper.java
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

public class MyDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "mydatabase.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_USERS = "users";
    private static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_ROLE = "role";

    private static final String CREATE_TABLE_USERS =
            "CREATE TABLE " + TABLE_USERS + " (" +
                    COLUMN_ID + " TEXT PRIMARY KEY, " +
                    COLUMN_NAME + " TEXT, " +
                    COLUMN_PASSWORD + " TEXT, " +
                    COLUMN_ROLE + " TEXT);";

    public MyDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle database upgrades if needed
    }

    public boolean isIdExists(String id) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_USERS,
                null,
                COLUMN_ID + " = ?",
                new String[]{id},
                null,
                null,
                null
        );

        boolean idExists = cursor.getCount() > 0;

        cursor.close();
        db.close();

        return idExists;
    }

    public boolean isUsernameExists(String username) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_USERS,
                null,
                COLUMN_NAME + " = ?",
                new String[]{username},
                null,
                null,
                null
        );

        boolean usernameExists = cursor.getCount() > 0;

        cursor.close();
        db.close();

        return usernameExists;
    }

    public long insertUser(String id, String name, String password, String role) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, id);
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_ROLE, role);

        long newRowId = db.insert(TABLE_USERS, null, values);

        db.close();

        return newRowId;
    }



        public Cursor getUserByIdRole(String id, String password, String role) {
            SQLiteDatabase db = getReadableDatabase();

            return db.query(
                    TABLE_USERS,
                    new String[]{COLUMN_ID, COLUMN_NAME}, // Include the COLUMN_NAME
                    COLUMN_ID + " = ? AND " + COLUMN_PASSWORD + " = ? AND " + COLUMN_ROLE + " = ?",
                    new String[]{id, password, role},
                    null,
                    null,
                    null
            );
    }
}



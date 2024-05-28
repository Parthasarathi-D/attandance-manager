package app.login;

// CaptureAct.java (Modify as needed)
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import com.journeyapps.barcodescanner.CaptureActivity;




public class CaptureAct extends CaptureActivity {
    // You may need to modify the existing code of the CaptureAct class
    // to ensure vertical scanning. Check the ZXing documentation for guidance.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the orientation to portrait (vertical)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
}

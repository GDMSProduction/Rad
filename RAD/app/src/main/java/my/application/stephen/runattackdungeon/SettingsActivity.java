package my.application.stephen.runattackdungeon;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Spinner;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(my.application.stephen.runattackdungeon.R.layout.activity_settings);
        Spinner InputMethod = (Spinner) findViewById(R.id.spinner_InputMethod);
        InputMethod.setSelection(InputMethod.getFirstVisiblePosition());
    }
}

package de.tudresden.inf.rn.mobilis.android.xhunt.activity;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;
import de.tudresden.inf.rn.mobilis.android.xhunt.R;

public class VersionActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_version);
        
        TextView versionText = (TextView) findViewById(R.id.version_text);
        try {
			InputStream fis = (InputStream) getAssets().open("version.txt");
			versionText.setText(new Scanner(fis).useDelimiter("\\A").next());
		} catch (IOException e) {
			e.printStackTrace();
			versionText.setText("No version.txt found!");
		}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_version, menu);
        return true;
    }

}

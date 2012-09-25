package de.tudresden.inf.rn.mobilis.android.xhunt.teststarter;

import java.util.List;
import android.os.Bundle;
import android.app.Activity;
import android.content.ComponentName;
import android.content.pm.InstrumentationInfo;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	protected List<InstrumentationInfo> iiList;
	protected ComponentName testComponent;
	protected final static String TARGET_PACKAGE = "de.tudresden.inf.rn.mobilis.android.xhunt";
	private static final String TAG = "XHuntAutomatic - Starter";
	
	private Boolean visible = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iiList = this.getPackageManager().queryInstrumentation(TARGET_PACKAGE, 0);
        testComponent = instrumentationForPosition(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    public void checkOnClick(View view) {
    	
    	TextView txtPlayerCount = (TextView) findViewById(R.id.txtPlayerCount);
    	EditText editPlayerCount = (EditText) findViewById(R.id.editPlayerCount);
    	
    	if(visible) {
    		txtPlayerCount.setVisibility(View.INVISIBLE);
    		txtPlayerCount.setEnabled(false);
    		editPlayerCount.setVisibility(View.INVISIBLE);
    		editPlayerCount.setEnabled(false);
    		visible = false;
    	} else {
    		txtPlayerCount.setVisibility(View.VISIBLE);
    		txtPlayerCount.setEnabled(true);
    		editPlayerCount.setVisibility(View.VISIBLE);
    		editPlayerCount.setEnabled(true);
    		visible = true;
    	}
    	
    }
    
    public void click(View view) {
    	
    	Bundle settings = new Bundle();
    	Boolean isMisterX = false;
    	String userName = "";
    	
    	CheckBox checkMisterX = (CheckBox) findViewById(R.id.checkMisterX);
    	isMisterX = checkMisterX.isChecked();
    	
    	EditText editUser = (EditText) findViewById(R.id.editUsername);
    	userName = editUser.getText().toString();
    	
    	if(isMisterX) {
    		EditText editNumberCount = (EditText) findViewById(R.id.editPlayerCount);
    		Log.d(TAG, editNumberCount.getText().toString());
    		Integer playerCount = Integer.parseInt(editNumberCount.getText().toString());
    		Log.d(TAG, playerCount.toString());
        	settings.putInt("playerCount", playerCount);
    	}
    	
    	settings.putString("userName", userName);
    	settings.putBoolean("isMisterX", isMisterX);
    	
    	Log.d(TAG, "start XHunt-Test");
    	this.startInstrumentation(testComponent, null, settings);
    }
    
    public ComponentName instrumentationForPosition(int position)
    {
        if (iiList == null) {
            return null;
        }
        InstrumentationInfo ii = iiList.get(position);
        return new ComponentName(ii.packageName, ii.name);
    }

    
}

package de.tudresden.inf.rn.mobilis.mxa.activities;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SlidingDrawer;
import android.widget.Toast;

public class FileChooserActivity extends ListActivity {

	private File mCurrentDirectory;
	private ArrayList<String> mEntries=new ArrayList<String>();
	
	//holds the result
	public static final String EXTRA_FILE_NAME="file";
	//return code for startActivtiyforResult if everything went fine
	public static final int REQUEST_CODE=1;
	
	//choose dir name for creation
	EditText createDirEditText;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mCurrentDirectory= new File("/");
		createDirEditText= new EditText(this);
		getListView().setLongClickable(true);
		getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> av, View v,
					int pos, long id) {
				Log.v("FileChooser","long clicked");
				
			//	String selected=mEntries.get(lv.getSelectedItemPosition());
				String dir=mCurrentDirectory.getAbsolutePath()+File.separator+mEntries.get(pos);
				Toast.makeText(FileChooserActivity.this, dir, Toast.LENGTH_SHORT);
				if (!new File(dir).isDirectory())
				{
					Toast.makeText(FileChooserActivity.this, "Please choose a valid directory", Toast.LENGTH_SHORT).show();
				}else if(!new File(dir).canWrite())
				{
					Toast.makeText(FileChooserActivity.this, "Please choose a valid directory, no writing allowed", Toast.LENGTH_SHORT).show();
				}else
				{
					Intent i= new Intent();
					i.putExtra(EXTRA_FILE_NAME,dir);
					setResult(RESULT_OK, i);
					finish();
				}
				
				return true;
			}
		});
		
		
		
				
	
		listFiles();
	}
	
	
	/**
	 * Add all entries of the directory to the adapter and show them.
	 */
	private void listFiles()
	{
		mEntries.clear();
		if (mCurrentDirectory.getParent()!=null) 
		{
			mEntries.add("..");
		}
		
		if (mCurrentDirectory.isDirectory())
		{
			if (mCurrentDirectory.listFiles()!=null)
			{
				for (File f:mCurrentDirectory.listFiles())
				{
					if (f.isDirectory())mEntries.add(f.getName());
					
				}
			}
		}
		
		String e[]=new String[mEntries.size()];
		
		
		for ( int i=0;i<mEntries.size();i++ ) e[i]=mEntries.get(i); 
			
		java.util.Arrays.sort(e);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,e);
		setListAdapter(adapter);
		mEntries= new ArrayList<String>();
		for (String s:e)mEntries.add(s);
		
	}
	
	
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		String selected=mEntries.get(position);
		
		//one level up?
		if (selected.equals(".."))
		{
			mCurrentDirectory=new File(mCurrentDirectory.getParent());
			listFiles();
		}else
		{
			//file or directory selected
			File f=new File(mCurrentDirectory.getAbsolutePath()+File.separator+selected);
			
			if (f.isDirectory())
			{
				mCurrentDirectory=f;
				listFiles();
			}
				/*	}else
			{
					//return with a selected file
					Intent i= new Intent();
					i.putExtra(EXTRA_FILE_NAME, mCurrentDirectory.getAbsolutePath()+File.separator+selected);
					setResult(RESULT_OK, i);
					finish();
					return;
				
			}*/
		}
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		// add an entry
		menu.add("Create Directory");
		return true;
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		
	//	Toast.makeText(FileChooserActivity.this, "Item selected", Toast.LENGTH_SHORT).show();
		

		new AlertDialog.Builder(FileChooserActivity.this)
			.setTitle("Create Directory")
			.setMessage("Enter a file name")
			.setView(createDirEditText)
			.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					String createDirName = createDirEditText.getText().toString(); 
					if (createDirName!=null)
					{
						File dir= new File(mCurrentDirectory+File.separator+createDirName);
						try{
							if (!dir.mkdir())
							{
								Toast.makeText(FileChooserActivity.this, "Could not create directory "+dir.getAbsolutePath(), Toast.LENGTH_SHORT).show();
							}
							listFiles();
						}catch (Exception e) {
							e.printStackTrace();
						}	
						
					}
				}
			}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
	            // Do nothing.
				}
			}).show();
		
		//Log.v("FileChooserActivity", "dir: "+createDirEditText.getText().toString());
		
		
		return true;
	}
	
	

}

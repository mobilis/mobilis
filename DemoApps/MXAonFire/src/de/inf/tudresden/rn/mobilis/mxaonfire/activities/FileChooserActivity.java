package de.inf.tudresden.rn.mobilis.mxaonfire.activities;

import java.io.File;
import java.util.ArrayList;

import de.inf.tudresden.rn.mobilis.mxaonfire.util.Const;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Activity for choosing one file from the phones file system.
 * Lets you navigate through the whole system, when you click on one,
 * it is selected.
 * @author Christian Magenheimer
 *
 */

public class FileChooserActivity extends ListActivity {

	//holds current directory
	private File mCurrentDirectory;
	//all entries in current directory
	private ArrayList<String> mEntries=new ArrayList<String>();
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mCurrentDirectory= new File("/");
		
		listFiles();
	}
	
	/**
	 * Reads the entries of the current directory and attaches them to the
	 * adapter. 
	 */
	private void listFiles()
	{
		mEntries.clear();
		if (mCurrentDirectory.getParent()!=null) 
		{
			//entry for going one way up
			mEntries.add("..");
		}
		
		//get the entries
		if (mCurrentDirectory.isDirectory())
		{
			if (mCurrentDirectory.listFiles()!=null)
			{
				for (File f:mCurrentDirectory.listFiles())
				{
					
						mEntries.add(f.getName());			
				}
			}
		}
		
		String e[]=new String[mEntries.size()];
		for ( int i=0;i<mEntries.size();i++ ) e[i]=mEntries.get(i);
		//sort the entries
		java.util.Arrays.sort(e);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,e);
		setListAdapter(adapter);
		mEntries= new ArrayList<String>();
		for (String s:e)mEntries.add(s);
		
	}
	
	
/*
 * 	@Override(non-Javadoc)
 * @see android.app.ListActivity#onListItemClick(android.widget.ListView, android.view.View, int, long)
 */
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		String selected=mEntries.get(position);
		if (selected.equals(".."))
		{
			//one level up
			mCurrentDirectory=new File(mCurrentDirectory.getParent());
			listFiles();
		}else
		{
			File f=new File(mCurrentDirectory.getAbsolutePath()+File.separator+selected);
			//check if its a directory
			if (f.isDirectory())
			{
				mCurrentDirectory=f;
				listFiles();
			}else
			{
				//found the target, give the result back
				Intent i= new Intent();
				i.putExtra(Const.FILE_NAME, mCurrentDirectory.getAbsolutePath()+File.separator+selected);
				setResult(RESULT_OK, i);
				finish();
				return;
				
			}
		}
		
	}
}

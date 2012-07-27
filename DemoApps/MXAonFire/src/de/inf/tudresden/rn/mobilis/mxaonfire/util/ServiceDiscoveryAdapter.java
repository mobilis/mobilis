package de.inf.tudresden.rn.mobilis.mxaonfire.util;

import java.lang.reflect.Array;
import java.util.ArrayList;

import de.inf.tudresden.rn.mobilis.mxaonfire.R.id;
import de.tudresden.inf.rn.mobilis.mxa.services.parcelable.DiscoverItem;

import android.R;
import android.R.layout;
import android.app.Activity;
import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**Adapts elements from the result of an 
 * ServiceDiscovery and builds up the view
 * @author Christian Magenheimer
 *
 */
public class ServiceDiscoveryAdapter extends BaseAdapter{

	//holds the elements
	private ArrayList<DiscoverItem> mList;
	//needed for inflating the layout
	private Activity mContext;
	private int mLayout;
	public ServiceDiscoveryAdapter(ArrayList<DiscoverItem> items,Activity context, int layout)
	{
		mList=items;
		mContext=context;
		mLayout=layout;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mList.size();
	}

	@Override
	public Object getItem(int pos) {
		// TODO Auto-generated method stub
		return mList.get(pos-1);
	}

	@Override
	public long getItemId(int pos) {
		// TODO Auto-generated method stub
		return pos-1;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		LayoutInflater inflater=mContext.getLayoutInflater();
		View v=inflater.inflate(mLayout, parent,false);
		
		TextView tvName=(TextView)v.findViewById(id.service_discovery_item_name);
		TextView tvJid=(TextView)v.findViewById(id.service_discovery_item_jid);
		TextView tvNode=(TextView)v.findViewById(id.service_discovery_item_node);
		if (mList.get(position).name!=null) tvName.setText(mList.get(position).name);
		else tvName.setText("");
		if (mList.get(position).jid!=null) tvJid.setText(mList.get(position).jid);
		else tvJid.setText("");
		if (mList.get(position).node!=null) tvNode.setText(mList.get(position).node);
		else tvNode.setText("");
		return v;
	}

	
}

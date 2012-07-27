package de.inf.tudresden.rn.mobilis.mxaonfire.util;

import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.QuickContactBadge;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import de.inf.tudresden.rn.mobilis.mxaonfire.R;
import de.tudresden.inf.rn.mobilis.mxa.ConstMXA;
import de.tudresden.inf.rn.mobilis.mxa.ConstMXA.RosterItems;
import de.tudresden.inf.rn.mobilis.mxa.parcelable.XMPPPresence;

/**
 * Simple Adapter for the MXA Items.
 * Uses the pidgin Status images for displaying the status
 * @author elmar
 *
 */
public class RosterAdapter extends SimpleCursorAdapter{
	
	private int layout;
	private Context context;

	public RosterAdapter(Context context, int layout, Cursor c, String[] from,
			int[] to) {
		super(context, layout, c, from, to);
		this.layout=layout;
		
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		// TODO Auto-generated method stub
		LayoutInflater inflater=LayoutInflater.from(context);
		View v= inflater.inflate(layout, parent,false);
		
		return create(v,cursor);
	}
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		// TODO Auto-generated method stub
		
		//alles TextViews, einfach das Attribut davor

		create(view,cursor);
	}
	
	private View create(View v,Cursor c)
	{
		
		TextView tv_name = (TextView) v.findViewById(R.id.roster_item_name);
		
		
		
		String ressource=c.getString(c.getColumnIndex(RosterItems.RESSOURCE));
		String jid=c.getString(c.getColumnIndex(RosterItems.XMPP_ID));
		Log.v("RosterAdapter","res:#"+ressource+"#");
		if(ressource!=null && !ressource.equals(""))
			tv_name.setText(jid+"/"+ressource);
		else
			tv_name.setText(jid);
		
		
		
		ImageView imgView=(ImageView)v.findViewById(R.id.roster_item_image);
		String mode=c.getString(c.getColumnIndex(RosterItems.PRESENCE_MODE));
		
		if (mode.equals(RosterItems.MODE_UNAVAILABLE))
			imgView.setImageResource(R.drawable.offline);
		else if (mode.equals(RosterItems.MODE_AVAILABLE))
			imgView.setImageResource(R.drawable.available);
		else if (mode.equals(RosterItems.MODE_CHAT))
			imgView.setImageResource(R.drawable.chat);
		else if (mode.equals(RosterItems.MODE_AWAY))
			imgView.setImageResource(R.drawable.away);
		else if (mode.equals(RosterItems.MODE_DO_NOT_DISTURB))
			imgView.setImageResource(R.drawable.busy);
		else if (mode.equals(RosterItems.MODE_EXTENDED_AWAY))
			imgView.setImageResource(R.drawable.extended_away);

		
		TextView tvStatus = (TextView) v.findViewById(R.id.roster_item_status);
		String status=c.getString(c.getColumnIndex(RosterItems.PRESENCE_STATUS));
		tvStatus.setText(status);
		QuickContactBadge contact= (QuickContactBadge)v.findViewById(R.id.roster_item_contact_badge);
		contact.setImageResource(android.R.drawable.ic_menu_gallery);
//		tv_xmpp.setText("XMPP ID: "+c.getString(c.getColumnIndex(RosterItems.XMPP_ID)));
		return v;
	}
	
}


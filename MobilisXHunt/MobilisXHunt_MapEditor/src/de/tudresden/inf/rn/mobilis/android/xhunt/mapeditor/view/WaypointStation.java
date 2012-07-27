package de.tudresden.inf.rn.mobilis.android.xhunt.mapeditor.view;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;

import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.mapviewer.Waypoint;

import de.tudresden.inf.rn.mobilis.android.xhunt.mapeditor.Controller;
import de.tudresden.inf.rn.mobilis.android.xhunt.mapeditor.model.Station;

/**
 * The Class WaypointStation.
 */
public class WaypointStation extends Waypoint {
	
	/** The m j panel. */
	private JPanel mJPanel;
	
	/** The m jx mapviewer. */
	private JXMapViewer mJXMapviewer;
	
	/** The m station. */
	private Station mStation;
	
	/**
	 * Instantiates a new waypoint station.
	 *
	 * @param mapViewer the map viewer
	 * @param station the station
	 */
	public WaypointStation(JXMapViewer mapViewer, Station station) {
		super(station.getGeoPoint().toGeoPosition());
		mStation = station;
		
		mJXMapviewer = mapViewer;		
		
		mJPanel = new JPanel(){
			@Override
			public void paint(Graphics g) {
				setSize(new Dimension(15, 15));				
				
				g.setColor(Color.BLACK);
				g.fillOval(0, 0, getWidth(), getHeight());
				
				g.setColor(Color.YELLOW);
				g.fillOval(1, 1, getWidth() - 2, getHeight() - 2);
				
				setVisible(true);
			}
		};
		
		mJPanel.addMouseMotionListener(new MouseMotionListener() {
			
			@Override
			public void mouseDragged(MouseEvent e) {
				System.out.println("drag: " + e.getPoint().toString());
				
				Point point = mJPanel.getLocation();
				point.x += e.getPoint().getX();
				point.y += e.getPoint().getY();
				
		           
				mJPanel.setLocation(point);
				
				GeoPosition geo = mJXMapviewer.convertPointToGeoPosition(mJPanel.getLocation());
//				System.out.println("geo: " + geo.toString());
				
				setPosition(geo);
				mStation.setGeoPosition(geo);
	            
				mJPanel.repaint();
			}

			@Override
			public void mouseMoved(MouseEvent e) {}
		});
		
		mJPanel.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent arg0) {
				mJXMapviewer.repaint();
			}
			
			@Override
			public void mousePressed(MouseEvent e) {}
			
			@Override
			public void mouseExited(MouseEvent arg0) {
				mJPanel.setCursor(Cursor.getDefaultCursor());
			}
			
			@Override
			public void mouseEntered(MouseEvent arg0) {
				mJPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));		
			}
			
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if(Controller.getInstance().getMainView().isModeDelete()){
					Controller.getInstance().getRouteManagement().removeStation(mStation.getId());
					Controller.getInstance().getMainView().updateOverlays();
				}
				else if(arg0.getClickCount() == 2)
					new DialogStationInfo(mJPanel, mStation);
				
//				System.out.println("clicked point: " + arg0.getPoint().toString());
//				System.out.println("panel geo: " + getPosition().toString());
			}
		});
	}
	
	/**
	 * Gets the panel.
	 *
	 * @return the panel
	 */
	public JPanel getPanel(){
		return mJPanel;
	}

	

}

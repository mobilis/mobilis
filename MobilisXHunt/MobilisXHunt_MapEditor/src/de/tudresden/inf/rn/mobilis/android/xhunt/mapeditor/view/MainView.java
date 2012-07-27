package de.tudresden.inf.rn.mobilis.android.xhunt.mapeditor.view;

import java.awt.BorderLayout;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.Set;

import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.jdesktop.swingx.JXMapKit;
import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.mapviewer.AbstractTileFactory;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.mapviewer.Waypoint;
import org.jdesktop.swingx.mapviewer.WaypointPainter;

import de.tudresden.inf.rn.mobilis.android.xhunt.mapeditor.Controller;
import de.tudresden.inf.rn.mobilis.android.xhunt.mapeditor.model.Route;
import de.tudresden.inf.rn.mobilis.android.xhunt.mapeditor.model.Station;

/**
 * The Class MainView.
 */
public class MainView extends JFrame {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 8532717626699810267L;
	
	/** The rdbtn add. */
	private JRadioButton rdbtnAdd;
	
	/** The rdbtn delete. */
	private JRadioButton rdbtnDelete;
	
	/** The m controller. */
	protected Controller mController;
	
	/** The m basic title. */
	private String mBasicTitle = "MobilisXHunt MapEditor";
	
	/** The m jx map kit. */
	private JXMapKit mJXMapKit;
	
	/** The m waypoints. */
	private Set<Waypoint> mWaypoints;
	
	/**
	 * Instantiates a new main view.
	 *
	 * @param controller the controller
	 */
	public MainView( Controller controller ) {
		mController = controller;
		mWaypoints = new HashSet<Waypoint>();
		
		initComponents();
		
		this.setVisible( true );
	}
	
	/**
	 * Inits the components.
	 */
	private void initComponents() {
		setTitle( mBasicTitle );
		setDefaultCloseOperation( javax.swing.WindowConstants.EXIT_ON_CLOSE );
		setBounds( 5, 5, 640, 480 );
		
		mJXMapKit = new org.jdesktop.swingx.JXMapKit();	
		mJXMapKit.setName("Map");
		
		((AbstractTileFactory) mJXMapKit.getMainMap().getTileFactory()).setThreadPoolSize(50);
		mJXMapKit.setDefaultProvider( org.jdesktop.swingx.JXMapKit.DefaultProviders.OpenStreetMaps );
		mJXMapKit.setCenterPosition(new GeoPosition(51.05, 13.74));
		mJXMapKit.setZoom(5);
		mJXMapKit.setMiniMapVisible(false);
		
		mJXMapKit.getMainMap().addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent arg0) {}
			
			@Override
			public void focusGained(FocusEvent arg0) {
				if(mController.getRouteManagement().getAreaInfo() != null)
					MainView.this.setTitle(mBasicTitle 
							+ " < " + mController.getRouteManagement().getAreaInfo().Name + " >");
			}
		});
		
		mJXMapKit.getMainMap().addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent arg0) {}			
			@Override
			public void mousePressed(MouseEvent arg0) {}			
			@Override
			public void mouseExited(MouseEvent arg0) {}			
			@Override
			public void mouseEntered(MouseEvent arg0) {}			
			@Override
			public void mouseClicked(MouseEvent arg0) {				
				if(rdbtnAdd.isSelected()){
					System.out.println(mJXMapKit.getMainMap().convertPointToGeoPosition(arg0.getPoint()));
					
					if(mController.getRouteManagement().getAreaInfo() != null){
						int newId = mController.getRouteManagement().getNewStationId();
						
						Station station = new Station(newId,
								"abbrev", "newStation" + newId, mJXMapKit.getMainMap().convertPointToGeoPosition(arg0.getPoint()));
						
						mController.getRouteManagement().getStations().put(newId, station);
						
						updateOverlays();
					}
					else
						showMessageNoAreaDetected();
				}
			}
		});
		
		JMenuBar menuBar = new JMenuBar(); 
		JMenu fileMenu = new JMenu( "File" ); 
		
		JMenuItem mntmExport = new JMenuItem("Export...");
		mntmExport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setDialogTitle("Save to...");
				fileChooser.addChoosableFileFilter( new FileNameExtensionFilter("XML Files", "xml"));
				
				int fileResult = fileChooser.showSaveDialog(MainView.this);
				
			    if ( fileResult == JFileChooser.APPROVE_OPTION ){
		    		if( mController.getRouteManagement().getAreaInfo() != null) {			    	
						try {
							mController.getSqlHelper().exportToFile(fileChooser.getSelectedFile().getAbsoluteFile(),
									mController.getRouteManagement());
						} catch (Exception e) {
							
							e.printStackTrace();
						}
				    }
		    		else
		    			showMessageNoAreaDetected();
			    }
			}
		});
		fileMenu.add(mntmExport);
		
		JSeparator separator_1 = new JSeparator();
		fileMenu.add(separator_1);
		
		fileMenu.add( new JMenuItem( "Quit" ) );
		menuBar.add( fileMenu ); 
		setJMenuBar( menuBar );
		
		JMenu mnArea = new JMenu("Area");
		menuBar.add(mnArea);
		
		JMenu mnData = new JMenu("Data");
		menuBar.add(mnData);
		
		JMenuItem mntmRoutes = new JMenuItem("Routes...");
		mntmRoutes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(mController.getRouteManagement().getAreaInfo() != null){
					new DialogRoutes(MainView.this);
				}
				else
					showMessageNoAreaDetected();
			}
		});
		mnArea.add(mntmRoutes);
		
		JMenuItem mntmStations = new JMenuItem("Stations...");
		mntmStations.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new DialogStations();
			}
		});
		mnArea.add(mntmStations);
		
		JMenuItem mntmLoadFromDb = new JMenuItem("Load Area from DB...");
		mntmLoadFromDb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(mController.getSqlHelper().testConnection()){
					new DialogLoadArea(mController, MainView.this, mController.getSqlHelper().queryAreas());
				}
				else
					showMessageDbConnectionError();
			}
		});
		
		JMenuItem mntmNewArea = new JMenuItem("New Area...");
		mntmNewArea.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				mController.resetRouteManagement();
				
				new DialogNewArea(mController, MainView.this);
			}
		});
		mnData.add(mntmNewArea);
		
		JSeparator separator = new JSeparator();
		mnData.add(separator);
		mnData.add(mntmLoadFromDb);
		
		JMenuItem mntmTickets = new JMenuItem("Load Tickets from DB...");
		mntmTickets.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(mController.getSqlHelper().testConnection()){
					new DialogTickets(MainView.this, mController.getSqlHelper().queryTickets());
				}
				else
					showMessageDbConnectionError();
			}
		});
		mnData.add(mntmTickets);
		
		JMenu mnOptions = new JMenu("Options");
		menuBar.add(mnOptions);
		
		JMenuItem mntmPreferences = new JMenuItem("Preferences");
		mntmPreferences.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				new DialogPreferences(mController, MainView.this);
			}
		});
		mnOptions.add(mntmPreferences);
		
		JToolBar toolbar = new JToolBar();
		
		getContentPane().add( toolbar, BorderLayout.PAGE_START );
		
		ButtonGroup buttonGroup = new ButtonGroup();
		
		JRadioButton rdbtnSelect = new JRadioButton("Select");
		buttonGroup.add(rdbtnSelect);
		rdbtnSelect.setSelected(true);
		toolbar.add(rdbtnSelect);
		
		rdbtnAdd = new JRadioButton("Add");
		buttonGroup.add(rdbtnAdd);
		toolbar.add(rdbtnAdd);
		
		rdbtnDelete = new JRadioButton("Delete");
		buttonGroup.add(rdbtnDelete);
		toolbar.add(rdbtnDelete);
		getContentPane().add( mJXMapKit, java.awt.BorderLayout.CENTER );
    }
	
	/**
	 * Load area data.
	 */
	public void loadAreaData(){
		System.out.println("load data of area: " + mController.getRouteManagement().getAreaInfo().ID);
		
		mController.getSqlHelper().loadAreaDataToRouteManagement(mController.getRouteManagement().getAreaInfo().ID);
		System.out.println("done...");
		
		updateOverlays();
	}
	
	/**
	 * Update overlays.
	 */
	public void updateOverlays() {
		mJXMapKit.getMainMap().removeAll();
		mWaypoints.clear();
		
		for(Station station : mController.getRouteManagement().getStations().values()){
			mWaypoints.add(new WaypointStation(mJXMapKit.getMainMap(), station));
		}
		
		for (Waypoint wp : mWaypoints)
			mJXMapKit.getMainMap().add(((WaypointStation) wp).getPanel());
		
		WaypointPainter painter = new WaypointPainter() {
			@Override
			protected void doPaint(Graphics2D g, JXMapViewer map, int width,
				int height) {

				doPaintRoutes(g, map);
				doPaintWaypoints(mWaypoints, map);
				
				g.dispose();
			}
		};
		painter.setWaypoints(mWaypoints);
		mJXMapKit.getMainMap().setOverlayPainter(painter);
		mJXMapKit.getMainMap().invalidate();
		mJXMapKit.getMainMap().repaint();

		setVisible(true);
	}
	
	/**
	 * Do paint routes.
	 *
	 * @param g the g
	 * @param map the map
	 */
	private void doPaintRoutes(Graphics2D g, JXMapViewer map){
		Rectangle rect = map.getViewportBounds();
		
		for(Route route : mController.getRouteManagement().getRoutes().values()){
			if(route.isShowOnMap()){
				Point prePoint = null;
				g.setStroke(mController.getSettings().getDefaultRouteStroke());
				g.setColor(route.getColor() != null 
						? route.getColor() 
						: mController.getSettings().getDefaultRouteColor());
				
				for(int i=0; i<route.getStationIds().size(); i++){
					Point2D point2D = map.getTileFactory().geoToPixel(
							mController.getRouteManagement().getStations()
								.get(route.getStationIds().get(i)).getGeoPoint().toGeoPosition(), map.getZoom());
					Point currentPoint = new Point(
							(int) point2D.getX() - rect.x,
							(int) point2D.getY() - rect.y);
					
					if(i == 0){
						prePoint = (Point)currentPoint.clone();
					}
					else{
						g.drawLine(prePoint.x, prePoint.y,
								currentPoint.x, currentPoint.y);
						prePoint = (Point)currentPoint.clone();					
					}
				}
			}
		}
	}
	
	/**
	 * Do paint waypoints.
	 *
	 * @param waypoints the waypoints
	 * @param map the map
	 */
	private void doPaintWaypoints(Set<Waypoint> waypoints, JXMapViewer map){
		Rectangle rect = map.getViewportBounds();
		
		for (Waypoint wp : waypoints) {
			Point2D point2D = map.getTileFactory().geoToPixel(
			wp.getPosition(), map.getZoom());			
			Point currentPoint = new Point(
			(int) point2D.getX() - rect.x,
			(int) point2D.getY() - rect.y);
			
			JComponent component = ((WaypointStation) wp).getPanel();
			
			currentPoint.x -= (component.getWidth() / 2);
			currentPoint.y -= (component.getHeight() / 2);
			
			component.setLocation(currentPoint);
		}
	}
	
	/**
	 * Checks if is mode delete.
	 *
	 * @return true, if is mode delete
	 */
	public boolean isModeDelete(){
		return rdbtnDelete != null 
			? rdbtnDelete.isSelected()
			: false;
	}
	
	/**
	 * Show message db connection error.
	 */
	private void showMessageDbConnectionError(){
		JOptionPane.showMessageDialog(MainView.this, 
				"Cannot connect to database. Please check your preferences (\"Options\"->\"Preferences\")"
				+ "\nand make sure database is accessible!",
				"Connection Failure!",
				JOptionPane.ERROR_MESSAGE);
	}
	
	/**
	 * Show message no area detected.
	 */
	private void showMessageNoAreaDetected(){
		showMessageNoAreaDetected(null);
	}
	
	/**
	 * Show message no area detected.
	 *
	 * @param append the append
	 */
	private void showMessageNoAreaDetected(String append){
		String msg = "Please load an area from DB or create a new one \n(see menu: \"Data\")!";
		if(append != null)
			msg += "\n" + append;
		
		JOptionPane.showMessageDialog(MainView.this, 
				msg,
				"No Area detected!",
				JOptionPane.WARNING_MESSAGE);
	}
	

	
/*	
	private void fetchData(GeoPosition leftBottom, GeoPosition rightTop){
		String serverUrl = "http://api.openstreetmap.org";
		String apiPathString = "/api/0.6/map?bbox=";
		String urlString = "";
		int timeout = 1000000;
		
		HttpURLConnection httpConn = null;
		
		urlString = serverUrl
			+ apiPathString
			+ ("" + leftBottom.getLongitude()).substring(0, 5)
			+ ("," + leftBottom.getLatitude()).substring(0, 6)
			+ ("," + rightTop.getLongitude()).substring(0, 6)
			+ ("," + rightTop.getLatitude()).substring(0, 6);
		
		System.out.println(urlString);
		
		try {
			URL url = new URL(urlString);
			
			httpConn = (HttpURLConnection) url.openConnection();
			httpConn.setReadTimeout(timeout);
			httpConn.setConnectTimeout(timeout);
			
			httpConn.setUseCaches(false);
			httpConn.setDoInput(true);
			httpConn.setRequestMethod("HEAD");
			
			httpConn.connect();
			
			if(httpConn.getResponseCode() == HttpURLConnection.HTTP_OK){
				System.out.println("Connection established successfully.");
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassCastException e){
			e.printStackTrace();
		} finally {
        	if( httpConn != null ) {
                httpConn.disconnect();
                httpConn = null;
            }
        }

	}
*/

}

package de.tudresden.inf.rn.mobilis.android.xhunt.mapeditor.helper;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import de.tudresden.inf.rn.mobilis.android.xhunt.mapeditor.Controller;
import de.tudresden.inf.rn.mobilis.android.xhunt.mapeditor.RouteManagement;
import de.tudresden.inf.rn.mobilis.android.xhunt.mapeditor.model.AreaInfo;
import de.tudresden.inf.rn.mobilis.android.xhunt.mapeditor.model.Route;
import de.tudresden.inf.rn.mobilis.android.xhunt.mapeditor.model.Station;
import de.tudresden.inf.rn.mobilis.android.xhunt.mapeditor.model.Ticket;

/**
 * The Class SqlHelper.
 */
public class SqlHelper {
	
	/** The m mysql connection. */
	private Connection mMysqlConnection;
	
	/** The m statement. */
	private Statement mStatement = null;
	
	/** The m prepared statement. */
	private PreparedStatement mPreparedStatement = null;
	
	/** The m result set. */
	private ResultSet mResultSet = null;
	
	/** The m server address. */
	private String mServerAddress;
	
	/** The m server port. */
	private String mServerPort;
	
	/** The m db name. */
	private String mDbName;
	
	/** The m db username. */
	private String mDbUsername;
	
	/** The m db password. */
	private String mDbPassword;
	
	/** The Constant TABLE_AREA. */
	private static final String TABLE_AREA = "XHunt_Area";
	
	/** The Constant TABLE_AREA_HAS_ROUTES. */
	private static final String TABLE_AREA_HAS_ROUTES = "XHunt_Area_has_Routes";
	
	/** The Constant TABLE_ROUTE. */
	private static final String TABLE_ROUTE = "XHunt_Route";
	
	/** The Constant TABLE_ROUTE_HAS_STATIONS. */
	private static final String TABLE_ROUTE_HAS_STATIONS = "XHunt_Route_has_Stations";
	
	/** The Constant TABLE_STATION. */
	private static final String TABLE_STATION = "XHunt_Station";
	
	/** The Constant TABLE_TICKET. */
	private static final String TABLE_TICKET = "XHunt_Ticket";
	
	/** The Constant FK_AREA_HAS_ROUTES_AREA. */
	private static final String FK_AREA_HAS_ROUTES_AREA = "fk_Area_has_Routes_Area";
	
	/** The Constant FK_AREA_HAS_ROUTES_ROUTES. */
	private static final String FK_AREA_HAS_ROUTES_ROUTES = "fk_Area_has_Routes_Routes";
	
	/** The Constant FK_ROUTE_HAS_STATIONS_ROUTE. */
	private static final String FK_ROUTE_HAS_STATIONS_ROUTE = "fk_Route_has_Stations_Route";
	
	/** The Constant FK_ROUTE_HAS_STATIONS_STATIONS. */
	private static final String FK_ROUTE_HAS_STATIONS_STATIONS = "fk_Route_has_Stations_Stations";
	
	/** The Constant FK_ROUTE_TICKET. */
	private static final String FK_ROUTE_TICKET = "fk_Route_Ticket";
	
	/** The Constant COLUMN_ID. */
	private static final String COLUMN_ID = "ID";
	
	/** The Constant COLUMN_NAME. */
	private static final String COLUMN_NAME = "Name";
	
	/** The Constant COLUMN_DESCRIPTION. */
	private static final String COLUMN_DESCRIPTION = "Description";
	
	/** The Constant COLUMN_VERSION. */
	private static final String COLUMN_VERSION = "Version";
	
	/** The Constant COLUMN_AREA_ID. */
	private static final String COLUMN_AREA_ID = "Area_ID";
	
	/** The Constant COLUMN_ROUTE_ID. */
	private static final String COLUMN_ROUTE_ID = "Route_ID";
	
	/** The Constant COLUMN_TICKET_ID. */
	private static final String COLUMN_TICKET_ID = "Ticket_ID";
	
	/** The Constant COLUMN_STARTNAME. */
	private static final String COLUMN_STARTNAME = "StartName";
	
	/** The Constant COLUMN_ENDNAME. */
	private static final String COLUMN_ENDNAME = "EndName";
	
	/** The Constant COLUMN_ICON. */
	private static final String COLUMN_ICON = "Icon";
	
	/** The Constant COLUMN_ISSUPERIOR. */
	private static final String COLUMN_ISSUPERIOR = "Is_Superior";
	
	/** The Constant COLUMN_STATION_ID. */
	private static final String COLUMN_STATION_ID = "Station_ID";
	
	/** The Constant COLUMN_POSITION. */
	private static final String COLUMN_POSITION = "Position";
	
	/** The Constant COLUMN_ABBREAVIATION. */
	private static final String COLUMN_ABBREAVIATION = "Abbreviation";
	
	/** The Constant COLUMN_LATITUDE. */
	private static final String COLUMN_LATITUDE = "Latitude";
	
	/** The Constant COLUMN_LONGITUDE. */
	private static final String COLUMN_LONGITUDE = "Longitude";
	
	/** The Constant XML_TAG_AREA. */
	private static final String XML_TAG_AREA = "area";
	
	/** The Constant XML_TAG_ID. */
	private static final String XML_TAG_ID = "id";
	
	/** The Constant XML_TAG_NAME. */
	private static final String XML_TAG_NAME = "name";
	
	/** The Constant XML_TAG_DESC. */
	private static final String XML_TAG_DESC = "desc";
	
	/** The Constant XML_TAG_VERSION. */
	private static final String XML_TAG_VERSION = "version";
	
	/** The Constant XML_TAG_TICKETS. */
	private static final String XML_TAG_TICKETS = "Tickets";
	
	/** The Constant XML_TAG_TICKET. */
	private static final String XML_TAG_TICKET = "Ticket";
	
	/** The Constant XML_TAG_STATIONS. */
	private static final String XML_TAG_STATIONS = "Stations";
	
	/** The Constant XML_TAG_STATION. */
	private static final String XML_TAG_STATION = "Station";
	
	/** The Constant XML_TAG_ROUTES. */
	private static final String XML_TAG_ROUTES = "Routes";
	
	/** The Constant XML_TAG_ROUTE. */
	private static final String XML_TAG_ROUTE = "Route";
	
	/** The Constant XML_TAG_STOP. */
	private static final String XML_TAG_STOP = "stop";
	
	/** The Constant XML_ATTR_ID. */
	private static final String XML_ATTR_ID = "id";
	
	/** The Constant XML_ATTR_NAME. */
	private static final String XML_ATTR_NAME = "name";
	
	/** The Constant XML_ATTR_TYPE. */
	private static final String XML_ATTR_TYPE = "type";
	
	/** The Constant XML_ATTR_START. */
	private static final String XML_ATTR_START = "start";
	
	/** The Constant XML_ATTR_END. */
	private static final String XML_ATTR_END = "end";
	
	/** The Constant XML_ATTR_POS. */
	private static final String XML_ATTR_POS = "pos";
	
	/** The Constant XML_ATTR_ABBREV. */
	private static final String XML_ATTR_ABBREV = "abbrev";
	
	/** The Constant XML_ATTR_LATITUDE. */
	private static final String XML_ATTR_LATITUDE = "latitude";
	
	/** The Constant XML_ATTR_LONGITUDE. */
	private static final String XML_ATTR_LONGITUDE = "longitude";
	
	/** The Constant XML_ATTR_ICON. */
	private static final String XML_ATTR_ICON = "icon";
	
	/** The Constant XML_ATTR_ISSUPERIOR. */
	private static final String XML_ATTR_ISSUPERIOR = "issuperior";
	
	
	/** The m controller. */
	private Controller mController;

	/**
	 * Instantiates a new sql helper.
	 *
	 * @param controller the controller
	 */
	public SqlHelper(Controller controller) {
		this.mController = controller;
		loadDbDriver();
	}
	
	/**
	 * Instantiates a new sql helper.
	 *
	 * @param controller the controller
	 * @param serverAddress the server address
	 * @param serverPort the server port
	 * @param dbName the db name
	 * @param dbUsername the db username
	 * @param dbPassword the db password
	 */
	public SqlHelper(Controller controller, String serverAddress, String serverPort, String dbName,
			String dbUsername, String dbPassword) {
		this.mController = controller;
		this.mServerAddress = serverAddress;
		this.mServerPort = serverPort;
		this.mDbName = dbName;
		this.mDbUsername = dbUsername;
		this.mDbPassword = dbPassword;
		
		loadDbDriver();
	}
	
	/**
	 * Check db structure.
	 *
	 * @return true, if successful
	 */
	public boolean checkDbStructure(){
		boolean isStructureOk = false;
		
		try {
			mMysqlConnection = DriverManager
				.getConnection(getConnectionURI());
			
			mPreparedStatement = mMysqlConnection
				.prepareStatement("select ID, Name, Description, Version from " + mDbName + "." + TABLE_AREA);			
			mPreparedStatement.executeQuery();
			
			mPreparedStatement = mMysqlConnection
				.prepareStatement("select Area_ID, Route_ID from " + mDbName + "." + TABLE_AREA_HAS_ROUTES);			
			mPreparedStatement.executeQuery();
		
			mPreparedStatement = mMysqlConnection
				.prepareStatement("select ID, Ticket_ID, Name, StartName, EndName from " + mDbName + "." + TABLE_ROUTE);			
			mPreparedStatement.executeQuery();
	
			mPreparedStatement = mMysqlConnection
				.prepareStatement("select Route_ID, Station_ID, Position from " + mDbName + "." + TABLE_ROUTE_HAS_STATIONS);			
			mPreparedStatement.executeQuery();

			mPreparedStatement = mMysqlConnection
				.prepareStatement("select ID, Name, Abbreviation, Latitude, Longitude from " + mDbName + "." + TABLE_STATION);			
			mPreparedStatement.executeQuery();

			mPreparedStatement = mMysqlConnection
				.prepareStatement("select ID, Name, Icon, Is_Superior from " + mDbName + "." + TABLE_TICKET);			
			mPreparedStatement.executeQuery();
			
			isStructureOk = true;			
		} catch (SQLException e) {
			e.printStackTrace();
			isStructureOk = false;
		}
		
		return isStructureOk;
	}
	
	/**
	 * Close all.
	 */
	private void closeAll() {
		try {
			if (mResultSet != null) {
				mResultSet.close();
			}

			if (mStatement != null) {
				mStatement.close();
			}

			if (mMysqlConnection != null) {
				mMysqlConnection.close();
			}
		} catch (Exception e) {

		}
	}
	
	/**
	 * Creates the db structure.
	 *
	 * @return true, if successful
	 */
	public boolean createDbStructure(){
		boolean isStructureCreated = false;
		try {
			mMysqlConnection = DriverManager
				.getConnection(getConnectionURI());
			
			// create table station
			mPreparedStatement = mMysqlConnection
				.prepareStatement("CREATE  TABLE IF NOT EXISTS " + mDbName + "." + TABLE_STATION + " (" +
						COLUMN_ID + " INT NOT NULL ," +
						COLUMN_NAME + " VARCHAR(45) NOT NULL ," +
						COLUMN_ABBREAVIATION + " VARCHAR(5) NULL ," +
						COLUMN_LATITUDE + " INT NOT NULL ," +
						COLUMN_LONGITUDE + " INT NOT NULL ," +
						" PRIMARY KEY (" + COLUMN_ID + ") )");
			mPreparedStatement.executeUpdate();
			
			//create tabel ticket
			mPreparedStatement = mMysqlConnection
				.prepareStatement("CREATE  TABLE IF NOT EXISTS " + mDbName + "." + TABLE_TICKET + " (" +
						COLUMN_ID + " INT NOT NULL ," +
						COLUMN_NAME + " VARCHAR(45) NOT NULL ," +
						COLUMN_ICON + " VARCHAR(45) NOT NULL ," +
						COLUMN_ISSUPERIOR + " INT NOT NULL ," +
						" PRIMARY KEY (" + COLUMN_ID + ") )");
			mPreparedStatement.executeUpdate();
			
			// create table route
			mPreparedStatement = mMysqlConnection
				.prepareStatement("CREATE  TABLE IF NOT EXISTS " + mDbName + "." + TABLE_ROUTE + " (" +
						COLUMN_ID + " INT NOT NULL ," +
						COLUMN_TICKET_ID + " INT NOT NULL ," +
						COLUMN_NAME + " VARCHAR(45) NOT NULL ," +
						COLUMN_STARTNAME + " VARCHAR(45) NOT NULL ," +
						COLUMN_ENDNAME + " VARCHAR(45) NOT NULL ," +
						" PRIMARY KEY (" + COLUMN_ID + ", " + COLUMN_TICKET_ID + ") ," +
						" CONSTRAINT " + FK_ROUTE_TICKET + 
						" FOREIGN KEY (" + COLUMN_TICKET_ID + " )" +
						" REFERENCES " + mDbName + "." + TABLE_TICKET + " (" + COLUMN_ID + " )" +
						" ON DELETE NO ACTION" +
						" ON UPDATE NO ACTION)");
			
			mPreparedStatement.executeUpdate();
			
			// create fk route ticket index
			/*mPreparedStatement = mMysqlConnection
				.prepareStatement("CREATE INDEX " + FK_ROUTE_TICKET + " ON " + mDbName + "." + TABLE_ROUTE +
						" (" + COLUMN_TICKET_ID + " ASC)");
			mPreparedStatement.execute();*/
			
			// create table area
			mPreparedStatement = mMysqlConnection
				.prepareStatement("CREATE  TABLE IF NOT EXISTS " + mDbName + "." + TABLE_AREA + " (" +
						COLUMN_ID + " INT NOT NULL ," +
						COLUMN_NAME + " VARCHAR(45) NOT NULL ," +
						COLUMN_DESCRIPTION + " VARCHAR(45) NULL ," +
						COLUMN_VERSION + " INT NOT NULL ," +
						" PRIMARY KEY (" + COLUMN_ID + ") )");
			mPreparedStatement.executeUpdate();
			
			// create table area has routes
			mPreparedStatement = mMysqlConnection
				.prepareStatement("CREATE  TABLE IF NOT EXISTS " + mDbName + "." + TABLE_AREA_HAS_ROUTES + " (" +
						COLUMN_AREA_ID + " INT NOT NULL ," +
						COLUMN_ROUTE_ID + " INT NOT NULL ," +
						" PRIMARY KEY (" + COLUMN_AREA_ID + ", " + COLUMN_ROUTE_ID + ") ," +
						" CONSTRAINT " + FK_AREA_HAS_ROUTES_AREA + 
						" FOREIGN KEY (" + COLUMN_AREA_ID + " )" +
						" REFERENCES " + mDbName + "." + TABLE_AREA + " (" + COLUMN_ID + " )" +
						" ON DELETE NO ACTION" +
						" ON UPDATE NO ACTION," +
						" CONSTRAINT " + FK_AREA_HAS_ROUTES_ROUTES + 
						" FOREIGN KEY (" + COLUMN_ROUTE_ID + " )" +
						" REFERENCES " + mDbName + "." + TABLE_ROUTE + " (" + COLUMN_ID + " )" +
						" ON DELETE NO ACTION" +
						" ON UPDATE NO ACTION)");
			mPreparedStatement.executeUpdate();
			
			// create index area has routes
			/*mPreparedStatement = mMysqlConnection
				.prepareStatement("CREATE INDEX " + FK_AREA_HAS_ROUTES_ROUTES + " ON " + mDbName + "." + TABLE_AREA_HAS_ROUTES + 
						" (" + COLUMN_ROUTE_ID + " ASC)");
			mPreparedStatement.executeUpdate();*/
			
			// create table route has stations
			mPreparedStatement = mMysqlConnection
				.prepareStatement("CREATE  TABLE IF NOT EXISTS " + mDbName + "." + TABLE_ROUTE_HAS_STATIONS + " (" +
						COLUMN_ROUTE_ID + " INT NOT NULL ," +
						COLUMN_STATION_ID + " INT NOT NULL ," +
						COLUMN_POSITION + " INT NOT NULL ," +
						" PRIMARY KEY (" + COLUMN_ROUTE_ID + ", " + COLUMN_STATION_ID + ") ," +
						" CONSTRAINT " + FK_ROUTE_HAS_STATIONS_ROUTE + 
						" FOREIGN KEY (" + COLUMN_ROUTE_ID + " )" +
						" REFERENCES " + mDbName + "." + TABLE_ROUTE + "(" + COLUMN_ID + " )" +
						" ON DELETE NO ACTION" +
						" ON UPDATE NO ACTION," +
						" CONSTRAINT " + FK_ROUTE_HAS_STATIONS_STATIONS + 
						" FOREIGN KEY (" + COLUMN_STATION_ID + " )" +
						" REFERENCES " + mDbName + "." + TABLE_STATION + " (" + COLUMN_ID + " )" +
						" ON DELETE NO ACTION" +
						" ON UPDATE NO ACTION)");
			mPreparedStatement.executeUpdate();
			
			// create index route has stations
			/*mPreparedStatement = mMysqlConnection
				.prepareStatement("CREATE INDEX " + FK_ROUTE_HAS_STATIONS_STATIONS + " ON " + mDbName + "." + TABLE_ROUTE_HAS_STATIONS +
						" (" + COLUMN_STATION_ID + " ASC)");
			mPreparedStatement.executeUpdate();*/
			
			isStructureCreated = true;
		} catch (SQLException e) {
			e.printStackTrace();
			isStructureCreated = false;
		}
		
		return isStructureCreated;
	}
	
	/**
	 * Export to file.
	 *
	 * @param exportFile the export file
	 * @param rm the rm
	 */
	public void exportToFile(File exportFile, RouteManagement rm){		
		try {
			
			writeAreaDataToXml(rm.getAreaInfo().ID,
					rm.getAreaInfo().Name,
					rm.getAreaInfo().Description,
					rm.getAreaInfo().Version,
					new ArrayList<Route>(rm.getRoutes().values()),
					new ArrayList<Station>(rm.getStations().values()),
					new ArrayList<Ticket>(rm.getAreaTickets().values()),
					exportFile);
			
		} catch (IOException e) {
			mController.log("!EXCEPTION: " + e.getMessage());
		}
	}
	
	/**
	 * Load area data to route management.
	 *
	 * @param areaId the area id
	 */
	public void loadAreaDataToRouteManagement(int areaId) {
		mController.resetRouteManagement();
		RouteManagement rm = mController.getRouteManagement();
		AreaInfo areaInfo = new AreaInfo();
		areaInfo.ID = areaId;
		
		ArrayList<Route> routes = new ArrayList<Route>();
		ArrayList<Station> stations = new ArrayList<Station>();
		ArrayList<Ticket> tickets = new ArrayList<Ticket>();
		
		try {
			mMysqlConnection = DriverManager
				.getConnection(getConnectionURI());
			
			mStatement = mMysqlConnection.createStatement();
			mResultSet = mStatement
					.executeQuery("select * from " + mDbName + "." + TABLE_AREA
							+ " where " + COLUMN_ID + "=" + areaId);
			
			// query general area data
			if (mResultSet.next()) {
				areaInfo.Description = mResultSet.getString(COLUMN_DESCRIPTION);
				areaInfo.Name = mResultSet.getString(COLUMN_NAME);
				areaInfo.Version= mResultSet.getInt(COLUMN_VERSION);
			}
			
			rm.setAreaInfo(areaInfo);
			
			tickets = queryAreaTickets(areaId);
			stations = queryAreaStations(areaId);
			routes = queryAreaRoutes(areaId);
			
			HashMap<Integer, Ticket> rmTickets = new HashMap<Integer, Ticket>();
			HashMap<Integer, Route> rmRoutes = new HashMap<Integer, Route>();
			HashMap<Integer, Station> rmStations = new HashMap<Integer, Station>();
			
			for(Ticket ticket : tickets){
				rmTickets.put(ticket.getId(), ticket);
			}
			rm.setAreaTickets(rmTickets);
			
			for(Route route : routes){
				rmRoutes.put(route.getId(), route);
			}
			rm.setAreaRoutes(rmRoutes);
			
			for(Station station : stations){
				rmStations.put(station.getId(), station);
			}
			rm.setAreaStations(rmStations);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		closeAll();

	}
	
	/**
	 * Gets the route from attributes.
	 *
	 * @param parser the parser
	 * @return the route from attributes
	 */
	private Route getRouteFromAttributes(XmlPullParser parser){
		Route route = new Route();
		
		for(int i=0; i<parser.getAttributeCount(); i++){
			if(parser.getAttributeName(i).equals(XML_ATTR_ID))
				route.setId(Integer.valueOf(parser.getAttributeValue(i)));
			else if(parser.getAttributeName(i).equals(XML_ATTR_NAME))
				route.setName(parser.getAttributeValue(i));
			else if(parser.getAttributeName(i).equals(XML_ATTR_TYPE))
				route.setTicketId(Integer.valueOf(parser.getAttributeValue(i)));
			else if(parser.getAttributeName(i).equals(XML_ATTR_START))
				route.setStart(parser.getAttributeValue(i));
			else if(parser.getAttributeName(i).equals(XML_ATTR_END))
				route.setEnd(parser.getAttributeValue(i));
		}
		
		boolean done = false;
		try {
			do {
				switch (parser.getEventType()){				
					case XmlPullParser.START_TAG:
						String tagName = parser.getName();
						
						if (tagName.equals(XML_TAG_ROUTE)){
							parser.next();
						}
						else if (tagName.equals(XML_TAG_STOP)) {
							int pos = -1;
							int num = -1;
							
							if(parser.getAttributeCount() > 0){
								if(parser.getAttributeName(0).equals(XML_ATTR_POS))
									pos = Integer.valueOf(parser.getAttributeValue(0));
							}
							
							num = Integer.valueOf(parser.nextText());
							
							if(num > -1 && pos > -1)
								route.addStation(pos, num);
						}
						else
							parser.next();
						break;
					case XmlPullParser.END_TAG:
						if (parser.getName().equals(XML_TAG_ROUTE)){
							done = true;
						}
						else
							parser.next();
						break;
					case XmlPullParser.END_DOCUMENT:
						done = true;
						break;
					default:
						parser.next();
				}
			} while (!done);
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return route;
	}
	
	/**
	 * Gets the station from attributes.
	 *
	 * @param parser the parser
	 * @return the station from attributes
	 */
	private Station getStationFromAttributes(XmlPullParser parser){
		Station station = new Station();
		int lat = 0;
		int lon = 0;
		
		for(int i=0; i<parser.getAttributeCount(); i++){
			if(parser.getAttributeName(i).equals(XML_ATTR_ID))
				station.setId(Integer.valueOf(parser.getAttributeValue(i)));
			else if(parser.getAttributeName(i).equals(XML_ATTR_ABBREV))
				station.setAbbrevation(parser.getAttributeValue(i));
			else if(parser.getAttributeName(i).equals(XML_ATTR_NAME))
				station.setName(parser.getAttributeValue(i));
			else if(parser.getAttributeName(i).equals(XML_ATTR_LATITUDE))
				lat = Integer.valueOf(parser.getAttributeValue(i));
			else if(parser.getAttributeName(i).equals(XML_ATTR_LONGITUDE))
				lon = Integer.valueOf(parser.getAttributeValue(i));
		}
		station.setGeoPoint(lat, lon);
		
		return station;
	}
	
	/**
	 * Gets the ticket from attributes.
	 *
	 * @param parser the parser
	 * @return the ticket from attributes
	 */
	private Ticket getTicketFromAttributes(XmlPullParser parser){
		Ticket ticket = new Ticket();
		
		for(int i=0; i<parser.getAttributeCount(); i++){
			if(parser.getAttributeName(i).equals(XML_ATTR_ID))
				ticket.setId(Integer.valueOf(parser.getAttributeValue(i)));
			else if(parser.getAttributeName(i).equals(XML_ATTR_NAME))
				ticket.setName(parser.getAttributeValue(i));
			else if(parser.getAttributeName(i).equals(XML_ATTR_ICON))
				ticket.setIcon(parser.getAttributeValue(i));
			else if(parser.getAttributeName(i).equals(XML_ATTR_ISSUPERIOR))
				ticket.setSuperior(Boolean.valueOf(parser.getAttributeValue(i)));
		}

		return ticket;
	}
	
	/**
	 * Insert xml data into db.
	 *
	 * @param filePath the file path
	 * @return true, if successful
	 * @throws Exception the exception
	 */
	public boolean insertXmlDataIntoDB(String filePath) throws Exception{
		boolean success = false;
		boolean areaAdded = false;
		
		int areaId = -1;
		String areaName = null;
		String areaDesc = null;
		int areaVersion = -1;
		
		
		mMysqlConnection = DriverManager
				.getConnection(getConnectionURI());
		
		XmlPullParserFactory factory;
		try {
			factory = XmlPullParserFactory.newInstance();
			XmlPullParser parser = factory.newPullParser();
			parser.setInput(new FileReader(filePath));
			
			boolean done = false;
			
			do {
				switch (parser.getEventType()){
				
					case XmlPullParser.START_TAG:
						String tagName = parser.getName();
						
						if (tagName.equals(XML_TAG_AREA)){
							parser.next();
						}
						else if (tagName.equals(XML_TAG_ID)) {
							areaId = Integer.valueOf(parser.nextText()).intValue();
						}
						else if (tagName.equals(XML_TAG_NAME)) {
							areaName = parser.nextText();
						}
						else if (tagName.equals(XML_TAG_DESC)) {
							areaDesc = parser.nextText();
						}
						else if (tagName.equals(XML_TAG_VERSION)) {
							areaVersion = Integer.valueOf(parser.nextText()).intValue();
						}
						else if(areaAdded){
							if (tagName.equals(XML_TAG_TICKET)) {
								Ticket ticket = getTicketFromAttributes(parser);
								
								mPreparedStatement = mMysqlConnection
									.prepareStatement("select * from " + mDbName + "." + TABLE_TICKET + " where id=?");
								
								mPreparedStatement.setInt(1, ticket.getId());
								mResultSet = mPreparedStatement.executeQuery();
								
								if(mResultSet.next()){
									prepareDbUpdateTicket(ticket);
								}
								else{
									prepareDbInsertIntoTicket(ticket);
								}
								
								mPreparedStatement.executeUpdate();
								
								System.out.println("Ticket added: " + ticket.getName());
								
								parser.next();
							}
							else if (tagName.equals(XML_TAG_STATION)) {
								Station station = getStationFromAttributes(parser);
								
								mPreparedStatement = mMysqlConnection
									.prepareStatement("select * from " + mDbName + "." + TABLE_STATION + " where id=?");
								
								mPreparedStatement.setInt(1, station.getId());
								mResultSet = mPreparedStatement.executeQuery();
								
								if(mResultSet.next()){
									prepareDbUpdateStation(station);
								}
								else{
									prepareDbInsertIntoStation(station);
								}
								
								mPreparedStatement.executeUpdate();
								
								System.out.println("Station added: " + station.getName());
								
								parser.next();
							}
							else if (tagName.equals(XML_TAG_ROUTE)) {
								Route route = getRouteFromAttributes(parser);
								
								mPreparedStatement = mMysqlConnection
									.prepareStatement("select * from " + mDbName + "." + TABLE_ROUTE + " where id=?");
								
								mPreparedStatement.setInt(1, route.getId());
								mResultSet = mPreparedStatement.executeQuery();
								
								if(mResultSet.next()){
									prepareDbUpdateRoute(route);
								}
								else{
									prepareDbInsertIntoRoute(route);
								}
								
								mPreparedStatement.executeUpdate();
								
								
								
								mPreparedStatement = mMysqlConnection
									.prepareStatement("select * from " + mDbName + "." + TABLE_AREA_HAS_ROUTES 
											+ " where " + COLUMN_AREA_ID + "=? and " + COLUMN_ROUTE_ID + "=?");
								
								mPreparedStatement.setInt(1, areaId);
								mPreparedStatement.setInt(2, route.getId());
								mResultSet = mPreparedStatement.executeQuery();
								
								if(!mResultSet.next()){
									// assign route to area
									mPreparedStatement = mMysqlConnection
										.prepareStatement("insert into  " + mDbName + "." + TABLE_AREA_HAS_ROUTES + " values (?, ?)");
									
									mPreparedStatement.setInt(1, areaId);
									mPreparedStatement.setInt(2, route.getId());
									
									mPreparedStatement.executeUpdate();
								}
								
								System.out.println("Route added: " + route.getName());		
								
								
								
								// assign stations to route
								for(Map.Entry<Integer, Integer> entry : route.getStationIds().entrySet()){
									mPreparedStatement = mMysqlConnection
										.prepareStatement("select * from " + mDbName + "." + TABLE_ROUTE_HAS_STATIONS
												+ " where " + COLUMN_ROUTE_ID + "=? and " + COLUMN_STATION_ID + "=?");
									
									mPreparedStatement.setInt(1, route.getId());
									mPreparedStatement.setInt(2, entry.getValue());
									mResultSet = mPreparedStatement.executeQuery();
									
									if(mResultSet.next()){
										mPreparedStatement = mMysqlConnection
											.prepareStatement("update " + mDbName + "." + TABLE_ROUTE_HAS_STATIONS 
													+ " set " + COLUMN_POSITION + "=? where " + COLUMN_ROUTE_ID 
													+ "=? and " + COLUMN_STATION_ID + "=?");
										
										mPreparedStatement.setInt(1, entry.getKey());
										mPreparedStatement.setInt(2, route.getId());
										mPreparedStatement.setInt(3, entry.getValue());																				
									}
									else{
										mPreparedStatement = mMysqlConnection
											.prepareStatement("insert into  " + mDbName + "." + TABLE_ROUTE_HAS_STATIONS 
													+ " values (?, ?, ?)");
										
										mPreparedStatement.setInt(1, route.getId());
										mPreparedStatement.setInt(2, entry.getValue());
										mPreparedStatement.setInt(3, entry.getKey());
									}
									
									mPreparedStatement.executeUpdate();
								}
								
								System.out.println("route assignment complete.");
								
								parser.next();
							}
							else
								parser.next();
						}
						else if(areaId > 0 && areaName != null
								&& areaDesc != null && areaVersion > 0){

							mPreparedStatement = mMysqlConnection
								.prepareStatement("select * from " + mDbName + "." + TABLE_AREA + " where id=?");
							
							mPreparedStatement.setInt(1, areaId);
							mResultSet = mPreparedStatement.executeQuery();
							
							if(mResultSet.next()){
								prepareDbUpdateArea(areaId, areaName, areaDesc, areaVersion);
							}
							else{
								prepareDbInsertIntoArea(areaId, areaName, areaDesc, areaVersion);
							}
							
							mPreparedStatement.executeUpdate();
							
							System.out.println("Area added: " + areaName);
							
							areaAdded = true;
							parser.next();
						}
						else
							parser.next();
						break;
					case XmlPullParser.END_TAG:
						if (parser.getName().equals("area")){
							done = true;
						}
						else
							parser.next();
						break;
					case XmlPullParser.END_DOCUMENT:
						done = true;
						break;
					default:
						parser.next();
				}
			} while (!done);
			
			success = true;

		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		closeAll();
		
		return success;
	}
	
	/**
	 * Load db driver.
	 */
	private void loadDbDriver(){
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	
	// Prepare Insert and Update functions
	
	/**
	 * Prepare db insert into area.
	 *
	 * @param areaId the area id
	 * @param areaName the area name
	 * @param areaDesc the area desc
	 * @param areaVersion the area version
	 * @throws SQLException the sQL exception
	 */
	private void prepareDbInsertIntoArea(int areaId, String areaName,
			String areaDesc, int areaVersion) throws SQLException{
		mPreparedStatement = mMysqlConnection
			.prepareStatement("insert into  " + mDbName + "." + TABLE_AREA + " values (?, ?, ?, ?)");
	
		mPreparedStatement.setInt(1, areaId);
		mPreparedStatement.setString(2, areaName);
		mPreparedStatement.setString(3, areaDesc);
		mPreparedStatement.setInt(4, areaVersion);
	}
	
	/**
	 * Prepare db insert into route.
	 *
	 * @param route the route
	 * @throws SQLException the sQL exception
	 */
	private void prepareDbInsertIntoRoute(Route route) throws SQLException {
		mPreparedStatement = mMysqlConnection
			.prepareStatement("insert into  " + mDbName + "." + TABLE_ROUTE + " values (?, ?, ?, ?, ?)");
		
		mPreparedStatement.setInt(1, route.getId());
		mPreparedStatement.setInt(2, route.getTicketId());
		mPreparedStatement.setString(3, route.getName());
		mPreparedStatement.setString(4, route.getStart());
		mPreparedStatement.setString(5, route.getEnd());
	}
	
	/**
	 * Prepare db insert into station.
	 *
	 * @param station the station
	 * @throws SQLException the sQL exception
	 */
	private void prepareDbInsertIntoStation(Station station) throws SQLException {
		mPreparedStatement = mMysqlConnection
			.prepareStatement("insert into  " + mDbName + "." + TABLE_STATION + " values (?, ?, ?, ?, ?)");
		
		mPreparedStatement.setInt(1, station.getId());
		mPreparedStatement.setString(2, station.getName());
		mPreparedStatement.setString(3, station.getAbbrevation());
		mPreparedStatement.setInt(4, station.getGeoPoint().getLatitudeE6());
		mPreparedStatement.setInt(5, station.getGeoPoint().getLongitudeE6());
	}
	
	/**
	 * Prepare db insert into ticket.
	 *
	 * @param ticket the ticket
	 * @throws SQLException the sQL exception
	 */
	private void prepareDbInsertIntoTicket(Ticket ticket) throws SQLException {
		mPreparedStatement = mMysqlConnection
			.prepareStatement("insert into  " + mDbName + "." + TABLE_TICKET + " values (?, ?, ?, ?)");
		
		mPreparedStatement.setInt(1, ticket.getId());
		mPreparedStatement.setString(2, ticket.getName());
		mPreparedStatement.setString(3, ticket.getIcon());
		mPreparedStatement.setBoolean(4, ticket.isSuperior());
	}
	
	/**
	 * Prepare db update area.
	 *
	 * @param areaId the area id
	 * @param areaName the area name
	 * @param areaDesc the area desc
	 * @param areaVersion the area version
	 * @throws SQLException the sQL exception
	 */
	private void prepareDbUpdateArea(int areaId, String areaName,
			String areaDesc, int areaVersion) throws SQLException {
		mPreparedStatement = mMysqlConnection
			.prepareStatement("update " + mDbName + "." + TABLE_AREA 
					+ " set " + COLUMN_NAME + "=?, " + COLUMN_DESCRIPTION 
					+ "=?, " + COLUMN_VERSION + "=? where " + COLUMN_ID + "=?");
		
		mPreparedStatement.setString(1, areaName);
		mPreparedStatement.setString(2, areaDesc);
		mPreparedStatement.setInt(3, areaVersion);
		mPreparedStatement.setInt(4, areaId);
	}
	
	/**
	 * Prepare db update route.
	 *
	 * @param route the route
	 * @throws SQLException the sQL exception
	 */
	private void prepareDbUpdateRoute(Route route) throws SQLException {
		mPreparedStatement = mMysqlConnection
			.prepareStatement("update " + mDbName + "." + TABLE_ROUTE 
					+ " set " + COLUMN_TICKET_ID + "=?, " + COLUMN_NAME 
					+ "=?, " + COLUMN_STARTNAME + "=?, " + COLUMN_ENDNAME 
					+ "=? where " + COLUMN_ID + "=?");

		mPreparedStatement.setInt(1, route.getTicketId());
		mPreparedStatement.setString(2, route.getName());
		mPreparedStatement.setString(3, route.getStart());
		mPreparedStatement.setString(4, route.getEnd());
		mPreparedStatement.setInt(5, route.getId());
	}
	
	/**
	 * Prepare db update station.
	 *
	 * @param station the station
	 * @throws SQLException the sQL exception
	 */
	private void prepareDbUpdateStation(Station station) throws SQLException {
		mPreparedStatement = mMysqlConnection
			.prepareStatement("update " + mDbName + "." + TABLE_STATION 
					+ " set " + COLUMN_NAME + "=?, " + COLUMN_ABBREAVIATION 
					+ "=?, " + COLUMN_LATITUDE + "=?, " + COLUMN_LONGITUDE 
					+ "=? where " + COLUMN_ID + "=?");

		mPreparedStatement.setString(1, station.getName());
		mPreparedStatement.setString(2, station.getAbbrevation());
		mPreparedStatement.setInt(3, station.getGeoPoint().getLatitudeE6());
		mPreparedStatement.setInt(4, station.getGeoPoint().getLongitudeE6());
		mPreparedStatement.setInt(5, station.getId());
	}
	
	/**
	 * Prepare db update ticket.
	 *
	 * @param ticket the ticket
	 * @throws SQLException the sQL exception
	 */
	private void prepareDbUpdateTicket(Ticket ticket) throws SQLException {
		mPreparedStatement = mMysqlConnection
			.prepareStatement("update " + mDbName + "." + TABLE_TICKET 
					+ " set " + COLUMN_NAME + "=?, " + COLUMN_ICON 
					+ "=?, " + COLUMN_ISSUPERIOR + "=? where " + COLUMN_ID + "=?");

		mPreparedStatement.setString(1, ticket.getName());
		mPreparedStatement.setString(2, ticket.getIcon());
		mPreparedStatement.setBoolean(3, ticket.isSuperior());
		mPreparedStatement.setInt(4, ticket.getId());
	}
	
	
	/**
	 * Query area routes.
	 *
	 * @param areaId the area id
	 * @return the array list
	 * @throws SQLException the sQL exception
	 */
	private ArrayList<Route> queryAreaRoutes(int areaId) throws SQLException {
		ArrayList<Route> routes = new ArrayList<Route>();
		
		String strStatement = "select routes." + COLUMN_ID + ", routes." + COLUMN_TICKET_ID + 
			", routes." + COLUMN_NAME + ", routes." + COLUMN_STARTNAME +
			", routes." + COLUMN_ENDNAME + " from " + mDbName + "." + TABLE_AREA +
			" as area, ( select distinct route." + COLUMN_ID + ", route." + COLUMN_TICKET_ID +
			", route." + COLUMN_NAME + ", route." + COLUMN_STARTNAME + ", route." + COLUMN_ENDNAME +
			" from " + mDbName + "." + TABLE_AREA_HAS_ROUTES + " as ahr, " + mDbName + 
			"." + TABLE_ROUTE + " as route ) as routes where area." + COLUMN_ID + "=" + areaId;
//		System.out.println(strStatement);
		
		mStatement = mMysqlConnection.createStatement();
		mResultSet = mStatement.executeQuery(strStatement);
		
		while (mResultSet.next()) {
			Route route = new Route();
			route.setId(mResultSet.getInt(COLUMN_ID));
			route.setName(mResultSet.getString(COLUMN_NAME));
			route.setTicketId(mResultSet.getInt(COLUMN_TICKET_ID));
			route.setStart(mResultSet.getString(COLUMN_STARTNAME));
			route.setEnd(mResultSet.getString(COLUMN_ENDNAME));
			
			routes.add(route);
			System.out.println("Read: " + route.toString());
		}
		
		for(Route route : routes){
			String strStatement2 = "select pos." + COLUMN_STATION_ID + ", pos." + COLUMN_POSITION + 
				" from " + mDbName + "." + TABLE_AREA + " as area, ( select pos." +
				COLUMN_ROUTE_ID + ", pos." + COLUMN_STATION_ID + ", pos." + COLUMN_POSITION +
				" from " + mDbName + "." + TABLE_AREA_HAS_ROUTES + " as ahr, ( select rhs." + COLUMN_ROUTE_ID +
				", rhs." + COLUMN_STATION_ID + ", rhs." + COLUMN_POSITION + 
				" from "+ mDbName + "." + TABLE_ROUTE + " as route, " + mDbName + 
				"." + TABLE_ROUTE_HAS_STATIONS + " as rhs) as pos where pos." + COLUMN_ROUTE_ID +
				"=" + route.getId() + " group by pos." + COLUMN_STATION_ID +
				") as pos where area." + COLUMN_ID + "=" + areaId + " order by pos." + COLUMN_POSITION;
//			System.out.println(strStatement2);
			
			mStatement = mMysqlConnection.createStatement();
			mResultSet = mStatement.executeQuery(strStatement2);
			
			while (mResultSet.next()) {
				route.addStation(mResultSet.getInt(COLUMN_POSITION), mResultSet.getInt(COLUMN_STATION_ID));
				System.out.println("Read: position");
			}
		}
		
		return routes;
	}
	
	/**
	 * Query area stations.
	 *
	 * @param areaId the area id
	 * @return the array list
	 * @throws SQLException the sQL exception
	 */
	private ArrayList<Station> queryAreaStations(int areaId) throws SQLException {
		ArrayList<Station> stations = new ArrayList<Station>();
		
		String strStatement = "select stations." + COLUMN_ID + ", stations." + COLUMN_NAME +
			", stations." + COLUMN_ABBREAVIATION + ", stations." + COLUMN_LATITUDE +
			" , stations." + COLUMN_LONGITUDE + " from " + mDbName + "." + TABLE_AREA + " as area, " +
			" (	select distinct stations." + COLUMN_ID + ", stations." + COLUMN_NAME +
			", stations." + COLUMN_ABBREAVIATION + ", stations." + COLUMN_LATITUDE +
			" , stations." + COLUMN_LONGITUDE + " from " + mDbName + "." + TABLE_AREA_HAS_ROUTES + " as ahr, " + 
			" (	select stations." + COLUMN_ID + ", stations." + COLUMN_NAME +
			", stations." + COLUMN_ABBREAVIATION + ", stations." + COLUMN_LATITUDE +
			" , stations." + COLUMN_LONGITUDE + " from " + mDbName + "." + TABLE_ROUTE + " as route, " + 
			" (	select distinct station." + COLUMN_ID + ", station." + COLUMN_NAME +
			", station." + COLUMN_ABBREAVIATION + ", station." + COLUMN_LATITUDE +
			" , station." + COLUMN_LONGITUDE + " from " + mDbName + "." + TABLE_ROUTE_HAS_STATIONS + " as rhs, " +
			mDbName + "." + TABLE_STATION + " as station ) as stations " +
			") as stations ) as stations where area." + COLUMN_ID + "=" + areaId;
//		System.out.println(strStatement);
			
		mStatement = mMysqlConnection.createStatement();
		mResultSet = mStatement.executeQuery(strStatement);
		
		while (mResultSet.next()) {
			Station station = new Station();
			station.setId(mResultSet.getInt(COLUMN_ID));
			station.setName(mResultSet.getString(COLUMN_NAME));
			station.setAbbrevation(mResultSet.getString(COLUMN_ABBREAVIATION));
			station.setGeoPoint(mResultSet.getInt(COLUMN_LATITUDE), mResultSet.getInt(COLUMN_LONGITUDE));
			
			stations.add(station);
			System.out.println("Read: " + station.toString());
		}
		
		return stations;
	}
	
	/**
	 * Query area tickets.
	 *
	 * @param areaId the area id
	 * @return the array list
	 * @throws SQLException the sQL exception
	 */
	private ArrayList<Ticket> queryAreaTickets(int areaId) throws SQLException {
		ArrayList<Ticket> tickets = new ArrayList<Ticket>();
		
		String strStatement = "select tickets." + COLUMN_ID + ", tickets." + COLUMN_NAME +
			", tickets." + COLUMN_ICON + ", tickets." + COLUMN_ISSUPERIOR + 
			" from " + mDbName + "." + TABLE_AREA + " as area, " +
			" (	select distinct tickets." + COLUMN_ID + ", tickets." + COLUMN_NAME +
			", tickets." + COLUMN_ICON + ", tickets." + COLUMN_ISSUPERIOR +
			" from " + mDbName + "." + TABLE_AREA_HAS_ROUTES + " as ahr, " +
			" (	select ticket." + COLUMN_ID + ", ticket." + COLUMN_NAME +
			", ticket." + COLUMN_ICON + ", ticket." + COLUMN_ISSUPERIOR +
			" from " + mDbName + "." + TABLE_ROUTE + " as route, " + 
			mDbName + "." + TABLE_TICKET + " as ticket ) as tickets " + 
			") as tickets where area." + COLUMN_ID + "=" + areaId;
//		System.out.println(strStatement);
		
		mStatement = mMysqlConnection.createStatement();
		mResultSet = mStatement.executeQuery(strStatement);
		
		while (mResultSet.next()) {
			Ticket ticket = new Ticket();
			ticket.setId(mResultSet.getInt(COLUMN_ID));
			ticket.setName(mResultSet.getString(COLUMN_NAME));
			ticket.setIcon(mResultSet.getString(COLUMN_ICON));
			ticket.setSuperior(mResultSet.getInt(COLUMN_ISSUPERIOR) == 1);
			
			tickets.add(ticket);
			System.out.println("Read: " + ticket.toString());
		}
		
		return tickets;
	}
	
	/**
	 * Query areas.
	 *
	 * @return the array list
	 */
	public ArrayList<AreaInfo> queryAreas(){
		ArrayList<AreaInfo> areaInfos = new ArrayList<AreaInfo>();
		
		try {
			mMysqlConnection = DriverManager
				.getConnection(getConnectionURI());
			
			mStatement = mMysqlConnection.createStatement();
			mResultSet = mStatement
					.executeQuery("select * from " + mDbName + "." + TABLE_AREA);
			
			while (mResultSet.next()) {
				AreaInfo info = new AreaInfo();
				
				info.ID = mResultSet.getInt(COLUMN_ID);
				info.Name = mResultSet.getString(COLUMN_NAME);
				info.Description = mResultSet.getString(COLUMN_DESCRIPTION);				
				info.Version = mResultSet.getInt(COLUMN_VERSION);
				
				areaInfos.add(info);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		closeAll();
		return areaInfos;
	}
	
	/**
	 * Query tickets.
	 *
	 * @return the array list
	 */
	public ArrayList<Ticket> queryTickets(){
		ArrayList<Ticket> tickets = new ArrayList<Ticket>();
		
		try {
			mMysqlConnection = DriverManager
				.getConnection(getConnectionURI());
			
			mStatement = mMysqlConnection.createStatement();
			mResultSet = mStatement
					.executeQuery("select * from " + mDbName + "." + TABLE_TICKET);
			
			while (mResultSet.next()) {
				Ticket ticket = new Ticket();
				
				ticket.setId(mResultSet.getInt(COLUMN_ID));
				ticket.setName(mResultSet.getString(COLUMN_NAME));
				ticket.setIcon(mResultSet.getString(COLUMN_ICON));				
				ticket.setSuperior(mResultSet.getInt(COLUMN_ISSUPERIOR) == 1);
				
				tickets.add(ticket);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		closeAll();
		return tickets;
	}
	
	
	/**
	 * Test connection.
	 *
	 * @return true, if successful
	 */
	public boolean testConnection(){
		boolean connected = false;
		try {			
			mMysqlConnection = DriverManager
				.getConnection(getConnectionURI());
			
			connected = true;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return connected;
	}	
	
	/**
	 * Write area data to xml.
	 *
	 * @param areaId the area id
	 * @param areaName the area name
	 * @param areaDesc the area desc
	 * @param areaVersion the area version
	 * @param routes the routes
	 * @param stations the stations
	 * @param tickets the tickets
	 * @param exportFile the export file
	 * @return the file
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private File writeAreaDataToXml(int areaId, String areaName, String areaDesc, int areaVersion,
			ArrayList<Route> routes, ArrayList<Station> stations, ArrayList<Ticket> tickets,
			File exportFile) 
			throws IOException {
		FileWriter fileWriter;
		
		File xmlFile = exportFile != null
			? exportFile
			: new File("area_" + areaId + "_v" + areaVersion + ".xml");
		
		if(xmlFile.exists())
			xmlFile.delete();
		else
			xmlFile.createNewFile();
		
		fileWriter = new FileWriter(xmlFile, true);
		
		BufferedWriter bufferedWriter = new BufferedWriter(fileWriter); 
		
		bufferedWriter.write("<" + XML_TAG_AREA + ">");
		bufferedWriter.newLine();
		bufferedWriter.newLine();
		
		
		// write general area info
		bufferedWriter.write("<" + XML_TAG_ID + ">" + areaId + "</" + XML_TAG_ID + ">");
		bufferedWriter.newLine();
		bufferedWriter.write("<" + XML_TAG_NAME + ">" + areaName + "</" + XML_TAG_NAME + ">");
		bufferedWriter.newLine();
		bufferedWriter.write("<" + XML_TAG_DESC + ">" + areaDesc + "</" + XML_TAG_DESC + ">");
		bufferedWriter.newLine();
		bufferedWriter.write("<" + XML_TAG_VERSION + ">" + areaVersion + "</" + XML_TAG_VERSION + ">");
		bufferedWriter.newLine();
		bufferedWriter.newLine();
		
		
		// write tickets
		bufferedWriter.write("<" + XML_TAG_TICKETS + ">");
		bufferedWriter.newLine();
		
		for(Ticket ticket : tickets){
			bufferedWriter.write("<" + XML_TAG_TICKET + " " 
					+ XML_ATTR_ID + "=\"" + ticket.getId() + "\" "
					+ XML_ATTR_NAME + "=\"" + ticket.getName() + "\" "
					+ XML_ATTR_ICON + "=\"" + ticket.getIcon() + "\" "
					+ XML_ATTR_ISSUPERIOR + "=\"" + ticket.isSuperior() + "\" " 
					+ "></" + XML_TAG_TICKET + ">");
			bufferedWriter.newLine();
			System.out.println("Wrote to file: " + ticket.toString());
		}
		
		bufferedWriter.write("</" + XML_TAG_TICKETS + ">");
		bufferedWriter.newLine();
		bufferedWriter.newLine();
		
		
		// write stations
		bufferedWriter.write("<" + XML_TAG_STATIONS + ">");
		bufferedWriter.newLine();
		
		for(Station station : stations){
			bufferedWriter.write("<" + XML_TAG_STATION + " " 
					+ XML_ATTR_ID + "=\"" + station.getId() + "\" "
					+ XML_ATTR_ABBREV + "=\"" + station.getAbbrevation() + "\" "
					+ XML_ATTR_NAME + "=\"" + station.getName() + "\" "
					+ XML_ATTR_LATITUDE + "=\"" + station.getGeoPoint().getLatitudeE6() + "\" "
					+ XML_ATTR_LONGITUDE + "=\"" + station.getGeoPoint().getLongitudeE6() + "\" " 
					+ "></" + XML_TAG_STATION + ">");
			bufferedWriter.newLine();
			System.out.println("Wrote to file: " + station.toString());
		}
		
		bufferedWriter.write("</" + XML_TAG_STATIONS + ">");
		bufferedWriter.newLine();
		bufferedWriter.newLine();
		
		
		// write routes
		bufferedWriter.write("<" + XML_TAG_ROUTES + ">");
		bufferedWriter.newLine();
		
		for(Route route : routes){
			bufferedWriter.write("<" + XML_TAG_ROUTE + " " 
					+ XML_ATTR_ID + "=\"" + route.getId() + "\" "
					+ XML_ATTR_NAME + "=\"" + route.getName() + "\" "
					+ XML_ATTR_TYPE + "=\"" + route.getTicketId() + "\" "
					+ XML_ATTR_START + "=\"" + route.getStart() + "\" "
					+ XML_ATTR_END + "=\"" + route.getEnd() + "\" >");
			
			for(Map.Entry<Integer, Integer> entry : route.getStationIds().entrySet()){
				bufferedWriter.newLine();
				bufferedWriter.write("<" + XML_TAG_STOP + " " 
						+ XML_ATTR_POS + "=\"" + entry.getKey() + "\" >"
						+ entry.getValue()
						+ "</" + XML_TAG_STOP + ">");				
			}
			
			bufferedWriter.newLine();					
			bufferedWriter.write("</" + XML_TAG_ROUTE + ">");
			
			bufferedWriter.newLine();
			System.out.println("Wrote to file: " + route.toString());
		}
		
		bufferedWriter.write("</" + XML_TAG_ROUTES + ">");
		bufferedWriter.newLine();
		bufferedWriter.newLine();
		
		
		bufferedWriter.write("</" + XML_TAG_AREA + ">");
		
		bufferedWriter.close(); 
		
		return xmlFile;
	}
	
	
	// Getter And Setter
	
	/**
	 * Gets the connection uri.
	 *
	 * @return the connection uri
	 */
	private String getConnectionURI(){
		return "jdbc:mysql://"
			+ mServerAddress + ":" + mServerPort
			+ "/" + mDbName + "?"
			+ "user=" + mDbUsername + "&password=" + mDbPassword;
	}

	/**
	 * Gets the server address.
	 *
	 * @return the server address
	 */
	public String getServerAddress() {
		return mServerAddress;
	}

	/**
	 * Sets the server address.
	 *
	 * @param mDbServerAddress the new server address
	 */
	public void setServerAddress(String mDbServerAddress) {
		this.mServerAddress = mDbServerAddress;
	}

	/**
	 * Gets the server port.
	 *
	 * @return the server port
	 */
	public String getServerPort() {
		return mServerPort;
	}

	/**
	 * Sets the server port.
	 *
	 * @param mServerPort the new server port
	 */
	public void setServerPort(String mServerPort) {
		this.mServerPort = mServerPort;
	}

	/**
	 * Gets the db name.
	 *
	 * @return the db name
	 */
	public String getDbName() {
		return mDbName;
	}

	/**
	 * Sets the db name.
	 *
	 * @param mDbName the new db name
	 */
	public void setDbName(String mDbName) {
		this.mDbName = mDbName;
	}

	/**
	 * Gets the db username.
	 *
	 * @return the db username
	 */
	public String getDbUsername() {
		return mDbUsername;
	}

	/**
	 * Sets the db username.
	 *
	 * @param mDbUsername the new db username
	 */
	public void setDbUsername(String mDbUsername) {
		this.mDbUsername = mDbUsername;
	}

	/**
	 * Gets the db password.
	 *
	 * @return the db password
	 */
	public String getDbPassword() {
		return mDbPassword;
	}

	/**
	 * Sets the db password.
	 *
	 * @param mDbPassword the new db password
	 */
	public void setDbPassword(String mDbPassword) {
		this.mDbPassword = mDbPassword;
	}	

}

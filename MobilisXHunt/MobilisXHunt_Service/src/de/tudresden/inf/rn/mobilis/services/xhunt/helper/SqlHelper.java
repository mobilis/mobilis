/*******************************************************************************
 * Copyright (C) 2010 Technische Universität Dresden
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * 	http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * Dresden, University of Technology, Faculty of Computer Science
 * Computer Networks Group: http://www.rn.inf.tu-dresden.de
 * mobilis project: http://mobilisplatform.sourceforge.net
 ******************************************************************************/
package de.tudresden.inf.rn.mobilis.services.xhunt.helper;


import java.io.BufferedWriter;
import java.io.File;
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
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import de.tudresden.inf.rn.mobilis.services.xhunt.model.Route;
import de.tudresden.inf.rn.mobilis.services.xhunt.model.Station;
import de.tudresden.inf.rn.mobilis.services.xhunt.model.Ticket;
import de.tudresden.inf.rn.mobilis.services.xhunt.proxy.AreaInfo;

/**
 * The Class SqlHelper provides functions to query data of 
 * XHunt specific tables out of the Mobilis-DB. It is also 
 * possible to export the whole data for an area into an xml file.
 */
public class SqlHelper {
	
	/** The SQL connection class. */
	private Connection mMysqlConnection;
	
	/** The current SQL statement. */
	private Statement mStatement = null;
	
	/** The current prepared SQL statement. */
	private PreparedStatement mPreparedStatement = null;
	
	/** The current result set of an SQL action. */
	private ResultSet mResultSet = null;
	
	/** The server address of the database. */
	private String mServerAddress;
	
	/** The server port of the database. */
	private String mServerPort;
	
	/** The name for the XHunt database. */
	private String mDbName;
	
	/** The username to log in into the database. */
	private String mDbUsername;
	
	/** The password to log in into the database. */
	private String mDbPassword;
	
	/** The name of the TABLE_AREA. */
	private static final String TABLE_AREA = "XHunt_Area";
	
	/** The name of the TABLE_AREA_HAS_ROUTES. */
	private static final String TABLE_AREA_HAS_ROUTES = "XHunt_Area_has_Routes";
	
	/** The name of the TABLE_ROUTE. */
	private static final String TABLE_ROUTE = "XHunt_Route";
	
	/** The name of the TABLE_ROUTE_HAS_STATIONS. */
	private static final String TABLE_ROUTE_HAS_STATIONS = "XHunt_Route_has_Stations";
	
	/** The name of the TABLE_STATION. */
	private static final String TABLE_STATION = "XHunt_Station";
	
	/** The name of the TABLE_TICKET. */
	private static final String TABLE_TICKET = "XHunt_Ticket";
	
	/** The name of the FK_AREA_HAS_ROUTES_AREA. */
	private static final String FK_AREA_HAS_ROUTES_AREA = "fk_Area_has_Routes_Area";
	
	/** The name of the FK_AREA_HAS_ROUTES_ROUTES. */
	private static final String FK_AREA_HAS_ROUTES_ROUTES = "fk_Area_has_Routes_Routes";
	
	/** The name of the FK_ROUTE_HAS_STATIONS_ROUTE. */
	private static final String FK_ROUTE_HAS_STATIONS_ROUTE = "fk_Route_has_Stations_Route";
	
	/** The name of the FK_ROUTE_HAS_STATIONS_STATIONS. */
	private static final String FK_ROUTE_HAS_STATIONS_STATIONS = "fk_Route_has_Stations_Stations";
	
	/** The name of the FK_ROUTE_TICKET. */
	private static final String FK_ROUTE_TICKET = "fk_Route_Ticket";
	
	/** The name of the COLUMN_ID. */
	private static final String COLUMN_ID = "ID";
	
	/** The name of the COLUMN_NAME. */
	private static final String COLUMN_NAME = "Name";
	
	/** The name of the COLUMN_DESCRIPTION. */
	private static final String COLUMN_DESCRIPTION = "Description";
	
	/** The name of the COLUMN_VERSION. */
	private static final String COLUMN_VERSION = "Version";
	
	/** The name of the COLUMN_AREA_ID. */
	private static final String COLUMN_AREA_ID = "Area_ID";
	
	/** The name of the COLUMN_ROUTE_ID. */
	private static final String COLUMN_ROUTE_ID = "Route_ID";
	
	/** The name of the COLUMN_TICKET_ID. */
	private static final String COLUMN_TICKET_ID = "Ticket_ID";
	
	/** The name of the COLUMN_STARTNAME. */
	private static final String COLUMN_STARTNAME = "StartName";
	
	/** The name of the COLUMN_ENDNAME. */
	private static final String COLUMN_ENDNAME = "EndName";
	
	/** The name of the COLUMN_ICON. */
	private static final String COLUMN_ICON = "Icon";
	
	/** The name of the COLUMN_ISSUPERIOR. */
	private static final String COLUMN_ISSUPERIOR = "Is_Superior";
	
	/** The name of the COLUMN_STATION_ID. */
	private static final String COLUMN_STATION_ID = "Station_ID";
	
	/** The name of the COLUMN_POSITION. */
	private static final String COLUMN_POSITION = "Position";
	
	/** The name of the COLUMN_ABBREAVIATION. */
	private static final String COLUMN_ABBREAVIATION = "Abbreviation";
	
	/** The name of the COLUMN_LATITUDE. */
	private static final String COLUMN_LATITUDE = "Latitude";
	
	/** The name of the COLUMN_LONGITUDE. */
	private static final String COLUMN_LONGITUDE = "Longitude";
	
	/** The name of the XML_TAG_AREA. */
	private static final String XML_TAG_AREA = "area";
	
	/** The name of the XML_TAG_ID. */
	private static final String XML_TAG_ID = "id";
	
	/** The name of the XML_TAG_NAME. */
	private static final String XML_TAG_NAME = "name";
	
	/** The name of the XML_TAG_DESC. */
	private static final String XML_TAG_DESC = "desc";
	
	/** The name of the XML_TAG_VERSION. */
	private static final String XML_TAG_VERSION = "version";
	
	/** The name of the XML_TAG_TICKETS. */
	private static final String XML_TAG_TICKETS = "Tickets";
	
	/** The name of the XML_TAG_TICKET. */
	private static final String XML_TAG_TICKET = "Ticket";
	
	/** The name of the XML_TAG_STATIONS. */
	private static final String XML_TAG_STATIONS = "Stations";
	
	/** The name of the XML_TAG_STATION. */
	private static final String XML_TAG_STATION = "Station";
	
	/** The name of the XML_TAG_ROUTES. */
	private static final String XML_TAG_ROUTES = "Routes";
	
	/** The name of the XML_TAG_ROUTE. */
	private static final String XML_TAG_ROUTE = "Route";
	
	/** The name of the XML_TAG_STOP. */
	private static final String XML_TAG_STOP = "stop";
	
	/** The name of the XML_ATTR_ID. */
	private static final String XML_ATTR_ID = "id";
	
	/** The name of the XML_ATTR_NAME. */
	private static final String XML_ATTR_NAME = "name";
	
	/** The name of the XML_ATTR_TYPE. */
	private static final String XML_ATTR_TYPE = "type";
	
	/** The name of the XML_ATTR_START. */
	private static final String XML_ATTR_START = "start";
	
	/** The name of the XML_ATTR_END. */
	private static final String XML_ATTR_END = "end";
	
	/** The name of the XML_ATTR_POS. */
	private static final String XML_ATTR_POS = "pos";
	
	/** The name of the XML_ATTR_ABBREV. */
	private static final String XML_ATTR_ABBREV = "abbrev";
	
	/** The name of the XML_ATTR_LATITUDE. */
	private static final String XML_ATTR_LATITUDE = "latitude";
	
	/** The name of the XML_ATTR_LONGITUDE. */
	private static final String XML_ATTR_LONGITUDE = "longitude";
	
	/** The name of the XML_ATTR_ICON. */
	private static final String XML_ATTR_ICON = "icon";
	
	/** The name of the XML_ATTR_ISSUPERIOR. */
	private static final String XML_ATTR_ISSUPERIOR = "issuperior";
	
	/** The class specific Logger object. */
	private final static Logger LOGGER = Logger.getLogger(SqlHelper.class.getCanonicalName());
	

	/**
	 * Instantiates a new SqlHelper.
	 */
	public SqlHelper() {
		loadDbDriver();
	}
	
	/**
	 * Check if tables are well defined. This is necessary to modify 
	 * the data in the right tables. If one query of a table fails, 
	 * the function is returning false.
	 *
	 * @return true, if structure is well defined
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
			LOGGER.severe("!EXCEPTION: " + e.getMessage());
			isStructureOk = false;
		}
		
		return isStructureOk;
	}
	
	/**
	 * Disconnect the database connection.
	 */
	public void disconnect(){
		try {
			if (mMysqlConnection != null) {
				mMysqlConnection.close();
			}
		} catch (SQLException e) {
			LOGGER.severe("!EXCEPTION: " + e.getMessage());
		}
	}
	
	/**
	 * Flush/release the current SQL statement and result set objects.
	 */
	private void flush() {
		try {
			if (mResultSet != null) {
				mResultSet.close();
			}

			if (mStatement != null) {
				mStatement.close();
			}
		} catch (Exception e) {

		}
	}
	
	/**
	 * Creates the necessary database structure. Use {@link checkDbStructure} to 
	 * verify the correct structure of the database.
	 *
	 * @return true, if creation was successfuly
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
			
			//create table ticket
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
			LOGGER.severe("!EXCEPTION: " + e.getMessage());
			isStructureCreated = false;
		}
		
		return isStructureCreated;
	}
	
	/**
	 * Export area data, queried from the database, to a file.
	 *
	 * @param areaId the if of the area to export
	 * @param folderPath the folder path where the output file should be placed
	 * @return the exported area file
	 */
	public File exportAreaData(int areaId, String folderPath) {
		File exportFile = null;
		
		// contains area information
		String areaName = null;
		String areaDescription = null;
		int areaVersion = -1;
		
		// contains all area elements
		ArrayList<Route> routes = new ArrayList<Route>();
		ArrayList<Station> stations = new ArrayList<Station>();
		ArrayList<Ticket> tickets = new ArrayList<Ticket>();
		
		try {
			mMysqlConnection = DriverManager
				.getConnection(getConnectionURI());
			
			// query general area data
			mStatement = mMysqlConnection.createStatement();
			mResultSet = mStatement
					.executeQuery("select * from " + mDbName + "." + TABLE_AREA
							+ " where " + COLUMN_ID + "=" + areaId);
			
			// save general area data
			if (mResultSet.next()) {
				areaDescription = mResultSet.getString(COLUMN_DESCRIPTION);
				areaName = mResultSet.getString(COLUMN_NAME);
				areaVersion= mResultSet.getInt(COLUMN_VERSION);
			}
			
			// query area elements
			tickets = queryAreaTickets(areaId);
			stations = queryAreaStations(areaId);
			routes = queryAreaRoutes(areaId);
			
			// write all queried data to output file 'exportFile'
			exportFile = writeAreaDataToXml(areaId, areaName, areaDescription, areaVersion,
					routes, stations, tickets, folderPath);
			
		} catch (SQLException e) {
			LOGGER.severe("!EXCEPTION: " + e.getMessage());
		} catch (IOException e) {
			LOGGER.severe("!EXCEPTION: " + e.getMessage());
		}
		
		// release statement and result set objects
		flush();

		return exportFile;
	}
	
	/**
	 * Load database driver for MySQL.
	 */
	private void loadDbDriver(){
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			LOGGER.severe("!EXCEPTION: " + e.getMessage());
		}
	}
	
	
	/**
	 * Put all routes of an area in a hash map.
	 *
	 * @param areaId the id of the area
	 * @return a hash map that contains the id of the route as key 
	 * 		and the related route as value
	 */
	public HashMap<Integer, Route> queryAreaRoutesMap(int areaId) {
		HashMap<Integer, Route> routes = new HashMap<Integer, Route>();
		
		try {
			
			ArrayList<Route> areaRoutes = queryAreaRoutes(areaId);
			for(Route route : areaRoutes){
				routes.put(route.getId(), route);
			}
		} catch (SQLException e) {
			LOGGER.severe("!EXCEPTION: " + e.getMessage());
		}
		
		return routes;
	}
	
	/**
	 * Query all routes of an area.
	 *
	 * @param areaId the id of the area
	 * @return the array list of related routes to this area
	 * @throws SQLException the SQL exception if a query fails
	 */
	public ArrayList<Route> queryAreaRoutes(int areaId) throws SQLException {
		ArrayList<Route> routes = new ArrayList<Route>();
		
		// query string for all routes of an area
		String strStatement = "select routes." + COLUMN_ID + ", routes." + COLUMN_TICKET_ID + 
			", routes." + COLUMN_NAME + ", routes." + COLUMN_STARTNAME +
			", routes." + COLUMN_ENDNAME + " from " + mDbName + "." + TABLE_AREA +
			" as area, ( select distinct route." + COLUMN_ID + ", route." + COLUMN_TICKET_ID +
			", route." + COLUMN_NAME + ", route." + COLUMN_STARTNAME + ", route." + COLUMN_ENDNAME +
			" from " + mDbName + "." + TABLE_AREA_HAS_ROUTES + " as ahr, " + mDbName + 
			"." + TABLE_ROUTE + " as route ) as routes where area." + COLUMN_ID + "=" + areaId;
//		mController.log(strStatement);
		
		if (mMysqlConnection == null) {
			mMysqlConnection = DriverManager
					.getConnection(getConnectionURI());
		}
		
		mStatement = mMysqlConnection.createStatement();
		mResultSet = mStatement.executeQuery(strStatement);
		
		// for each result set found, create a new Route instance and store the related information 
		// of the route and add each to the routes list
		while (mResultSet.next()) {
			Route route = new Route();
			route.setId(mResultSet.getInt(COLUMN_ID));
			route.setName(mResultSet.getString(COLUMN_NAME));
			route.setTicketId(mResultSet.getInt(COLUMN_TICKET_ID));
			route.setStart(mResultSet.getString(COLUMN_STARTNAME));
			route.setEnd(mResultSet.getString(COLUMN_ENDNAME));
			
			routes.add(route);			
		}
		
		// for each related station in a route query the id and the position of the station 
		// from start to end in this route
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
//			mController.log(strStatement2);
			
			mStatement = mMysqlConnection.createStatement();
			mResultSet = mStatement.executeQuery(strStatement2);
			
			while (mResultSet.next()) {
				route.addStation(mResultSet.getInt(COLUMN_POSITION), mResultSet.getInt(COLUMN_STATION_ID));
			}
		}
		
		LOGGER.info("Read " + routes.size() + " routes from DB");
		return routes;
	}
	
	/**
	 * Put all stations of an area in a hash map.
	 *
	 * @param areaId the id of the area
	 * @return a hash map that contains the id of the station as key 
	 * 		and the related station as value
	 */
	public HashMap<Integer, Station> queryAreaStationsMap(int areaId) {
		HashMap<Integer, Station> stations = new HashMap<Integer, Station>();
		
		try {
			for(Station station : queryAreaStations(areaId)){
				stations.put(station.getId(), station);
			}
		} catch (SQLException e) {
			LOGGER.severe("!EXCEPTION: " + e.getMessage());
		}
		
		return stations;
	}
	
	/**
	 * Query all stations of an area.
	 *
	 * @param areaId the id of the area
	 * @return the array list of related stations to this area
	 * @throws SQLException the SQL exception if a query fails
	 */
	public ArrayList<Station> queryAreaStations(int areaId) throws SQLException {
		ArrayList<Station> stations = new ArrayList<Station>();
		
		// query string for all stations of an area
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
//		mController.log(strStatement);
		if (mMysqlConnection == null) {
			mMysqlConnection = DriverManager
					.getConnection(getConnectionURI());
		}
			
		mStatement = mMysqlConnection.createStatement();
		mResultSet = mStatement.executeQuery(strStatement);
		
		// for each result set found, create a new Station instance and store the related information 
		// of the station and add each to the stations list
		while (mResultSet.next()) {
			Station station = new Station();
			station.setId(mResultSet.getInt(COLUMN_ID));
			station.setName(mResultSet.getString(COLUMN_NAME));
			station.setAbbreviation(mResultSet.getString(COLUMN_ABBREAVIATION));
			station.setGeoPoint(mResultSet.getInt(COLUMN_LATITUDE), mResultSet.getInt(COLUMN_LONGITUDE));
			
			stations.add(station);
		}

		LOGGER.info("Read " + stations.size() + " stations from DB");
		return stations;
	}
	
	/**
	 * Put all tickets of an area in a hash map.
	 *
	 * @param areaId the id of the area
	 * @return a hash map that contains the id of the ticket as key 
	 * 		and the related ticket as value
	 */
	public HashMap<Integer, Ticket> queryAreaTicketsMap(int areaId) {
		HashMap<Integer, Ticket> tickets = new HashMap<Integer, Ticket>();
		
		try {
			for(Ticket ticket : queryAreaTickets(areaId)){
				tickets.put(ticket.getId(), ticket);
			}
		} catch (SQLException e) {
			LOGGER.severe("!EXCEPTION: " + e.getMessage());
		}
		
		return tickets;
	}
	
	/**
	 * Query area tickets.
	 *
	 * @param areaId the area id
	 * @return the array list
	 * @throws SQLException the sQL exception
	 */
	public ArrayList<Ticket> queryAreaTickets(int areaId) throws SQLException {
		ArrayList<Ticket> tickets = new ArrayList<Ticket>();
		
		// query string for all tickets of an area
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
//		mController.log(strStatement);
		
		if (mMysqlConnection == null) {
			mMysqlConnection = DriverManager
					.getConnection(getConnectionURI());
		}
		
		mStatement = mMysqlConnection.createStatement();
		mResultSet = mStatement.executeQuery(strStatement);
		
		// for each result set found, create a new Ticket instance and store the related information 
		// of the ticket and add each to the tickets list
		while (mResultSet.next()) {
			Ticket ticket = new Ticket();
			ticket.setId(mResultSet.getInt(COLUMN_ID));
			ticket.setName(mResultSet.getString(COLUMN_NAME));
			ticket.setIcon(mResultSet.getString(COLUMN_ICON));
			ticket.setSuperior(mResultSet.getInt(COLUMN_ISSUPERIOR) == 1);
			
			tickets.add(ticket);
		}

		LOGGER.info("Read " + tickets.size() + " tickets from DB");
		return tickets;
	}
	
	/**
	 * Query all areas stored in the database. This will only contain general area information 
	 * of an area and the related tickets no routes or stations will be queried.
	 *
	 * @return the array list of the areas
	 */
	public ArrayList<AreaInfo> queryAreas(){
		ArrayList<AreaInfo> areaInfos = new ArrayList<AreaInfo>();
		
		try {
			mMysqlConnection = DriverManager
				.getConnection(getConnectionURI());
			
			mStatement = mMysqlConnection.createStatement();
			mResultSet = mStatement
					.executeQuery("select * from " + mDbName + "." + TABLE_AREA);
			
			// store information of each area in the areInfos list
			while (mResultSet.next()) {
				AreaInfo info = new AreaInfo();
				
				info.setAreaId( mResultSet.getInt(COLUMN_ID) );
				info.setAreaName( mResultSet.getString(COLUMN_NAME) );
				info.setAreaDescription( mResultSet.getString(COLUMN_DESCRIPTION) );				
				info.setVersion( mResultSet.getInt(COLUMN_VERSION) );
				
				areaInfos.add(info);
			}
			
			// query tickets of an area
			for(AreaInfo info : areaInfos){
				List< de.tudresden.inf.rn.mobilis.services.xhunt.proxy.Ticket> ticketTypes 
					= new ArrayList< de.tudresden.inf.rn.mobilis.services.xhunt.proxy.Ticket >();
				ArrayList<Ticket> tickets = queryAreaTickets(info.getAreaId());
				
				for(Ticket ticket : tickets){
					ticketTypes.add( new de.tudresden.inf.rn.mobilis.services.xhunt.proxy.Ticket( ticket.getId(), ticket.getName() ));
				}
				
				info.setTickets( ticketTypes );
			}
			
		} catch (SQLException e) {
			LOGGER.severe("!EXCEPTION: " + e.getMessage());
		}
		
		flush();
		
		return areaInfos;
	}
	
	
	/**
	 * Test the database connection to verify the settings.
	 *
	 * @return true, if connection and the settings are correct
	 */
	public boolean testConnection(){
		boolean connected = false;
		try {			
			mMysqlConnection = DriverManager
				.getConnection(getConnectionURI());
			
			connected = true;
		} catch (SQLException e) {
			LOGGER.severe("!EXCEPTION: " + e.getMessage());
		}

		return connected;
	}	
	
	/**
	 * Write all information related to an area into a xml file.
	 *
	 * @param areaId the area id
	 * @param areaName the area name
	 * @param areaDesc the area description
	 * @param areaVersion the area version
	 * @param routes the routes
	 * @param stations the stations
	 * @param tickets the tickets
	 * @param folderPath the folder path
	 * @return the file which contains the data
	 * @throws IOException Signals that an I/O exception has occurred while writing to the file.
	 */
	private File writeAreaDataToXml(int areaId, String areaName, String areaDesc, int areaVersion,
			ArrayList<Route> routes, ArrayList<Station> stations, ArrayList<Ticket> tickets, String folderPath) 
			throws IOException {
		
		FileWriter fileWriter;
		
		// create a new file of format:
		// area_#id of the area#_v#version of the area#.xml
		// sample: area_1_v5 stands for the area with the database id '1' and the version '5'
		File xmlFile = new File(folderPath + File.separator + "area_" + areaId + "_v" + areaVersion + ".xml");
		
		// overwrite existing file or create a new one
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
			LOGGER.fine("Wrote to file: " + ticket.toString());
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
					+ XML_ATTR_ABBREV + "=\"" + station.getAbbreviation() + "\" "
					+ XML_ATTR_NAME + "=\"" + station.getName() + "\" "
					+ XML_ATTR_LATITUDE + "=\"" + station.getLatitude() + "\" "
					+ XML_ATTR_LONGITUDE + "=\"" + station.getLongitude() + "\" " 
					+ "></" + XML_TAG_STATION + ">");
			bufferedWriter.newLine();
			LOGGER.fine("Wrote to file: " + station.toString());
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
			LOGGER.fine("Wrote to file: " + route.toString());
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
	 * Gets the URI of the database connection.
	 *
	 * @return the database connection URI
	 */
	private String getConnectionURI(){
		return "jdbc:mysql://"
			+ mServerAddress + ":" + mServerPort
			+ "/" + mDbName + "?"
			+ "user=" + mDbUsername + "&password=" + mDbPassword;
	}
	
	/**
	 * Sets the SQL connection data.
	 *
	 * @param serverAddress the address of the database server
	 * @param serverPort the port of the database server
	 * @param dbName the name of the XHunt database
	 * @param dbUsername the username to log in into the database
	 * @param dbPassword the password to log in into the database
	 */
	public void setSqlConnectionData(String serverAddress, String serverPort, String dbName,
			String dbUsername, String dbPassword) {
		this.mServerAddress = serverAddress;
		this.mServerPort = serverPort;
		this.mDbName = dbName;
		this.mDbUsername = dbUsername;
		this.mDbPassword = dbPassword;
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
	 * Gets the database name.
	 *
	 * @return the database name
	 */
	public String getDbName() {
		return mDbName;
	}

	/**
	 * Sets the database name.
	 *
	 * @param mDbName the new database name
	 */
	public void setDbName(String mDbName) {
		this.mDbName = mDbName;
	}

	/**
	 * Gets the database username.
	 *
	 * @return the database username
	 */
	public String getDbUsername() {
		return mDbUsername;
	}

	/**
	 * Sets the database username.
	 *
	 * @param mDbUsername the new database username
	 */
	public void setDbUsername(String mDbUsername) {
		this.mDbUsername = mDbUsername;
	}

	/**
	 * Gets the database password.
	 *
	 * @return the database password
	 */
	public String getDbPassword() {
		return mDbPassword;
	}

	/**
	 * Sets the database password.
	 *
	 * @param mDbPassword the new database password
	 */
	public void setDbPassword(String mDbPassword) {
		this.mDbPassword = mDbPassword;
	}	

}

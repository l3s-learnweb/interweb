package de.l3s.interwebj.db;


import static de.l3s.interwebj.util.Assertions.*;

import java.sql.*;
import java.util.*;

import org.apache.log4j.*;

import de.l3s.interwebj.*;
import de.l3s.interwebj.config.*;
import de.l3s.interwebj.core.*;


public class JDBCDatabase
    implements Database
{
	
	private static final String PSTMT_HAS_PRINCIPAL = "SELECT count(*) FROM iwj_principals WHERE user=?";
	private static final String PSTMT_SELECT_PRINCIPAL_BY_NAME = "SELECT password,email,oauth_key,oauth_secret FROM iwj_principals WHERE user=?";
	private static final String PSTMT_SELECT_PRINCIPAL_BY_KEY = "SELECT user,email,oauth_secret FROM iwj_principals WHERE oauth_key=?";
	private static final String PSTMT_INSERT_PRINCIPAL = "INSERT INTO iwj_principals (user,password,email,oauth_key,oauth_secret) VALUES (?,?,?,?,?)";
	private static final String PSTMT_UPDATE_PRINCIPAL = "UPDATE iwj_principals SET email=?,oauth_key=?,oauth_secret=? WHERE user=?";
	private static final String PSTMT_DELETE_PRINCIPAL = "DELETE FROM iwj_principals WHERE user=?";
	
	private static final String PSTMT_HAS_PRINCIPAL_ROLE = "SELECT count(*) FROM iwj_principals_roles WHERE user=? AND role=?";
	private static final String PSTMT_SELECT_PRINCIPAL_ROLES = "SELECT role FROM iwj_principals_roles WHERE user=?";
	private static final String PSTMT_INSERT_PRINCIPAL_ROLE = "INSERT INTO iwj_principals_roles (user,role) VALUES (?,?)";
	private static final String PSTMT_DELETE_PRINCIPAL_ROLES = "DELETE FROM iwj_principals_roles WHERE user=?";
	
	private static final String PSTMT_HAS_CONNECTOR = "SELECT count(*) FROM iwj_connectors WHERE name=?";
	private static final String PSTMT_SELECT_CONNECTOR_AUTH_CREDENTIALS = "SELECT `key`, secret FROM iwj_connectors WHERE name=?";
	private static final String PSTMT_UPDATE_CONNECTOR_AUTH_CREDENTIALS = "UPDATE iwj_connectors SET `key`=?,secret=? WHERE name=?";
	private static final String PSTMT_INSERT_CONNECTOR = "INSERT INTO iwj_connectors (name,`key`,secret) VALUES (?,?,?)";
	private static final String PSTMT_DELETE_CONNECTOR = "DELETE FROM iwj_connectors WHERE name=?";
	
	private static final String PSTMT_SELECT_CONSUMER_BY_KEY = "SELECT name, url, description, secret FROM iwj_consumers WHERE `key`=?";
	private static final String PSTMT_SELECT_CONSUMERS = "SELECT name, url, description, `key`, secret FROM iwj_consumers WHERE user=?";
	private static final String PSTMT_INSERT_CONSUMER = "INSERT INTO iwj_consumers (user,name,url,description,`key`,secret) VALUES (?,?,?,?,?,?)";
	private static final String PSTMT_DELETE_CONSUMER = "DELETE FROM iwj_consumers WHERE user=? AND name=?";
	
	private static final String PSTMT_HAS_USER_AUTH_CREDENTIALS = "SELECT count(*) FROM iwj_users_auth_data WHERE connector=? AND user=?";
	
	//	private static final String PSTMT_SELECT_USER_AUTH_CREDENTIALS = "SELECT `key`, secret FROM iwj_users_auth_data WHERE connector=? AND user=?";
	private static final String PSTMT_SELECT_USER_AUTH_CREDENTIALS = "SELECT CASE WHEN u.key IS NOT NULL THEN u.key ELSE m.key END, "
	                                                                 + "CASE WHEN u.key IS NOT NULL THEN u.secret ELSE m.secret END "
	                                                                 + "FROM iwj_users_auth_data u LEFT OUTER JOIN iwj_mediators um ON(u.user=um.user) "
	                                                                 + "LEFT OUTER JOIN iwj_users_auth_data m ON(um.mediator=m.user AND u.connector=m.connector) "
	                                                                 + "WHERE u.user=? AND u.connector=?";
	private static final String PSTMT_SELECT_CONNECTOR_USER_ID = "SELECT connector_uid, secret FROM iwj_users_auth_data WHERE connector=? AND user=?";
	private static final String PSTMT_INSERT_USER_AUTH_CREDENTIALS = "INSERT INTO iwj_users_auth_data (connector,user,connector_uid,`key`,secret) VALUES (?,?,?,?,?)";
	private static final String PSTMT_DELETE_USER_AUTH_CREDENTIALS = "DELETE FROM iwj_users_auth_data WHERE connector=? AND user=?";
	
	private static final String PSTMT_HAS_MEDIATOR = "SELECT count(*) FROM iwj_mediators WHERE user=?";
	private static final String PSTMT_SELECT_MEDIATOR = "SELECT mediator FROM iwj_mediators WHERE name=?";
	private static final String PSTMT_INSERT_MEDIATOR = "INSERT INTO iwj_mediators (user,mediator) VALUES (?,?)";
	private static final String PSTMT_DELETE_MEDIATOR = "DELETE FROM iwj_mediators WHERE user=?";
	
	private Logger logger;
	
	private Connection dbConnection = null;
	private String connectionUserName = null;
	private String connectionPassword = null;
	private String connectionURL = null;
	private String driverName = null;
	private Driver driver = null;
	
	private Map<String, PreparedStatement> preparedStatements;
	
	private ResultSet rs = null;
	

	public JDBCDatabase(Configuration configuration)
	{
		init(configuration);
	}
	

	@Override
	public InterWebPrincipal authenticate(String userName, String password)
	{
		notNull(userName, "userName");
		notNull(password, "userPassword");
		Environment.logger.debug("authenticating InterWebJ user [" + userName
		                         + "]");
		InterWebPrincipal dbPrincipal = getPrincipal(userName, password);
		if (dbPrincipal != null)
		{
			List<String> roles = getRoles(userName);
			for (String role : roles)
			{
				dbPrincipal.addRole(role);
			}
			Environment.logger.debug("InterWebJ user [" + userName
			                         + "] authenticated");
		}
		return dbPrincipal;
	}
	

	@Override
	public void close()
	{
		silentCloseResultSet(rs);
		rs = null;
		if (preparedStatements != null)
		{
			for (String key : preparedStatements.keySet())
			{
				Statement stmt = preparedStatements.get(key);
				silentCloseStatement(stmt);
			}
			preparedStatements = null;
		}
		silentCloseConnection(dbConnection);
		dbConnection = null;
	}
	

	@Override
	public void deleteConnector(String connectorName)
	{
		notNull(connectorName, "connectorName");
		try
		{
			if (openConnection())
			{
				PreparedStatement pstmt = preparedStatements.get(PSTMT_DELETE_CONNECTOR);
				pstmt.setString(1, connectorName);
				pstmt.executeUpdate();
				dbConnection.commit();
			}
		}
		catch (SQLException e)
		{
			logger.error(e);
			close();
		}
	}
	

	@Override
	public void deleteConsumer(String userName, String consumerName)
	{
		notNull(userName, "userName");
		notNull(consumerName, "consumerName");
		try
		{
			if (openConnection())
			{
				PreparedStatement pstmt = preparedStatements.get(PSTMT_DELETE_CONSUMER);
				pstmt.setString(1, userName);
				pstmt.setString(2, consumerName);
				pstmt.executeUpdate();
				dbConnection.commit();
			}
		}
		catch (SQLException e)
		{
			logger.error(e);
			close();
		}
	}
	

	@Override
	public void deleteMediator(String userName)
	{
		notNull(userName, "userName");
		try
		{
			if (openConnection())
			{
				PreparedStatement pstmt = preparedStatements.get(PSTMT_DELETE_MEDIATOR);
				pstmt.setString(1, userName);
				pstmt.executeUpdate();
				dbConnection.commit();
			}
		}
		catch (SQLException e)
		{
			logger.error(e);
			close();
		}
	}
	

	@Override
	public boolean hasConnector(String connectorName)
	{
		notNull(connectorName, "connectorName");
		boolean exists = false;
		try
		{
			if (openConnection())
			{
				PreparedStatement pstmt = preparedStatements.get(PSTMT_HAS_CONNECTOR);
				pstmt.setString(1, connectorName);
				rs = pstmt.executeQuery();
				if (rs.next())
				{
					exists = (rs.getInt(1) == 1);
				}
				silentCloseResultSet(rs);
			}
		}
		catch (SQLException e)
		{
			logger.error(e);
			close();
		}
		return exists;
	}
	

	@Override
	public boolean hasPrincipal(String userName)
	{
		notNull(userName, "userName");
		boolean exists = true;
		try
		{
			if (openConnection())
			{
				PreparedStatement pstmt = preparedStatements.get(PSTMT_HAS_PRINCIPAL);
				pstmt.setString(1, userName);
				rs = pstmt.executeQuery();
				if (rs.next())
				{
					exists = (rs.getInt(1) == 1);
				}
				silentCloseResultSet(rs);
			}
		}
		catch (SQLException e)
		{
			logger.error(e);
			close();
		}
		return exists;
	}
	

	@Override
	public AuthCredentials readConnectorAuthCredentials(String connectorName)
	{
		notNull(connectorName, "connectorName");
		AuthCredentials authCredentials = null;
		try
		{
			String key;
			String secret;
			if (openConnection())
			{
				PreparedStatement pstmt = preparedStatements.get(PSTMT_SELECT_CONNECTOR_AUTH_CREDENTIALS);
				pstmt.setString(1, connectorName);
				rs = pstmt.executeQuery();
				if (rs.next())
				{
					key = rs.getString(1);
					secret = rs.getString(2);
					if (key != null)
					{
						authCredentials = new AuthCredentials(key, secret);
					}
				}
				silentCloseResultSet(rs);
			}
		}
		catch (SQLException e)
		{
			logger.error(e);
			close();
		}
		return authCredentials;
	}
	

	@Override
	public String readConnectorUserId(String connectorName, String userName)
	{
		notNull(connectorName, "connectorName");
		notNull(userName, "userName");
		String userId = null;
		try
		{
			if (openConnection())
			{
				PreparedStatement pstmt = preparedStatements.get(PSTMT_SELECT_CONNECTOR_USER_ID);
				pstmt.setString(1, connectorName);
				pstmt.setString(2, userName);
				rs = pstmt.executeQuery();
				if (rs.next())
				{
					userId = rs.getString(1);
				}
				silentCloseResultSet(rs);
			}
		}
		catch (SQLException e)
		{
			logger.error(e);
			close();
		}
		return userId;
	}
	

	@Override
	public Consumer readConsumerByKey(String key)
	{
		notNull(key, "key");
		Consumer consumer = null;
		try
		{
			if (openConnection())
			{
				PreparedStatement pstmt = preparedStatements.get(PSTMT_SELECT_CONSUMER_BY_KEY);
				pstmt.setString(1, key);
				rs = pstmt.executeQuery();
				while (rs.next())
				{
					String name = rs.getString(1);
					String url = rs.getString(2);
					String description = rs.getString(3);
					String secret = rs.getString(4);
					AuthCredentials credentials = new AuthCredentials(key,
					                                                  secret);
					consumer = new Consumer(name, url, description, credentials);
				}
				silentCloseResultSet(rs);
			}
		}
		catch (SQLException e)
		{
			logger.error(e);
			close();
		}
		return consumer;
	}
	

	@Override
	public List<Consumer> readConsumers(String userName)
	{
		notNull(userName, "userName");
		ArrayList<Consumer> consumers = new ArrayList<Consumer>();
		try
		{
			if (openConnection())
			{
				PreparedStatement pstmt = preparedStatements.get(PSTMT_SELECT_CONSUMERS);
				pstmt.setString(1, userName);
				rs = pstmt.executeQuery();
				while (rs.next())
				{
					String name = rs.getString(1);
					String url = rs.getString(2);
					String description = rs.getString(3);
					String key = rs.getString(4);
					String secret = rs.getString(5);
					AuthCredentials credentials = new AuthCredentials(key,
					                                                  secret);
					Consumer consumer = new Consumer(name,
					                                 url,
					                                 description,
					                                 credentials);
					consumers.add(consumer);
				}
				silentCloseResultSet(rs);
			}
		}
		catch (SQLException e)
		{
			logger.error(e);
			close();
		}
		return consumers;
	}
	

	@Override
	public String readMediator(String userName)
	{
		notNull(userName, "userName");
		String mediator = null;
		try
		{
			if (openConnection())
			{
				PreparedStatement pstmt = preparedStatements.get(PSTMT_SELECT_MEDIATOR);
				pstmt.setString(1, userName);
				rs = pstmt.executeQuery();
				if (rs.next())
				{
					mediator = rs.getString(1);
				}
				silentCloseResultSet(rs);
			}
		}
		catch (SQLException e)
		{
			logger.error(e);
			close();
		}
		return mediator;
	}
	

	@Override
	public InterWebPrincipal readPrincipalByKey(String key)
	{
		if (key == null)
		{
			return null;
		}
		InterWebPrincipal principal = null;
		try
		{
			if (openConnection())
			{
				PreparedStatement pstmt = preparedStatements.get(PSTMT_SELECT_PRINCIPAL_BY_KEY);
				pstmt.setString(1, key);
				rs = pstmt.executeQuery();
				while (rs.next())
				{
					String name = rs.getString(1);
					String email = rs.getString(2);
					String secret = rs.getString(3);
					AuthCredentials authCredentials = new AuthCredentials(key,
					                                                      secret);
					principal = new InterWebPrincipal(name, email);
					principal.setOauthCredentials(authCredentials);
				}
				silentCloseResultSet(rs);
				if (principal != null)
				{
					readRoles(principal);
				}
			}
		}
		catch (SQLException e)
		{
			logger.error(e);
			close();
		}
		return principal;
	}
	

	@Override
	public InterWebPrincipal readPrincipalByName(String userName)
	{
		if (userName == null)
		{
			return null;
		}
		InterWebPrincipal principal = null;
		try
		{
			if (openConnection())
			{
				PreparedStatement pstmt = preparedStatements.get(PSTMT_SELECT_PRINCIPAL_BY_NAME);
				pstmt.setString(1, userName);
				rs = pstmt.executeQuery();
				while (rs.next())
				{
					String email = rs.getString(2);
					String key = rs.getString(3);
					String secret = rs.getString(4);
					principal = new InterWebPrincipal(userName, email);
					if (key != null)
					{
						AuthCredentials authCredentials = new AuthCredentials(key,
						                                                      secret);
						principal.setOauthCredentials(authCredentials);
					}
				}
				silentCloseResultSet(rs);
				if (principal != null)
				{
					readRoles(principal);
				}
			}
		}
		catch (SQLException e)
		{
			logger.error(e);
			close();
		}
		return principal;
	}
	

	@Override
	public AuthCredentials readUserAuthCredentials(String connectorName,
	                                               String userName)
	{
		notNull(connectorName, "connectorName");
		notNull(userName, "userName");
		AuthCredentials authCredentials = null;
		try
		{
			String key;
			String secret;
			if (openConnection())
			{
				PreparedStatement pstmt = preparedStatements.get(PSTMT_SELECT_USER_AUTH_CREDENTIALS);
				pstmt.setString(1, userName);
				pstmt.setString(2, connectorName);
				rs = pstmt.executeQuery();
				if (rs.next())
				{
					key = rs.getString(1);
					secret = rs.getString(2);
					if (key != null)
					{
						authCredentials = new AuthCredentials(key, secret);
					}
				}
				silentCloseResultSet(rs);
			}
		}
		catch (SQLException e)
		{
			logger.error(e);
			close();
		}
		return authCredentials;
	}
	

	@Override
	public void saveConnector(String connectorName,
	                          AuthCredentials authCredentials)
	{
		notNull(connectorName, "connectorName");
		deleteConnector(connectorName);
		try
		{
			if (openConnection())
			{
				PreparedStatement pstmt = preparedStatements.get(PSTMT_INSERT_CONNECTOR);
				pstmt.setString(1, connectorName);
				String key = (authCredentials == null)
				    ? null : authCredentials.getKey();
				String secret = (authCredentials == null)
				    ? null : authCredentials.getSecret();
				setString(pstmt, 2, key);
				setString(pstmt, 3, secret);
				pstmt.executeUpdate();
				dbConnection.commit();
			}
		}
		catch (SQLException e)
		{
			logger.error(e);
			close();
		}
	}
	

	@Override
	public void saveConsumer(String userName, Consumer consumer)
	{
		notNull(userName, "userName");
		notNull(consumer, "consumer");
		deleteConsumer(userName, consumer.getName());
		try
		{
			if (openConnection())
			{
				PreparedStatement pstmt = preparedStatements.get(PSTMT_INSERT_CONSUMER);
				pstmt.setString(1, userName);
				pstmt.setString(2, consumer.getName());
				pstmt.setString(3, consumer.getUrl());
				pstmt.setString(4, consumer.getDescription());
				AuthCredentials authCredentials = consumer.getAuthCredentials();
				String key = (authCredentials == null)
				    ? null : authCredentials.getKey();
				String secret = (authCredentials == null)
				    ? null : authCredentials.getSecret();
				setString(pstmt, 5, key);
				setString(pstmt, 6, secret);
				pstmt.executeUpdate();
				dbConnection.commit();
			}
		}
		catch (SQLException e)
		{
			logger.error(e);
			close();
		}
	}
	

	@Override
	public void saveMediator(String userName, String mediator)
	{
		notNull(userName, "userName");
		notNull(mediator, "mediator");
		deleteMediator(mediator);
		try
		{
			if (openConnection())
			{
				PreparedStatement pstmt = preparedStatements.get(PSTMT_INSERT_MEDIATOR);
				pstmt.setString(1, userName);
				pstmt.setString(2, mediator);
				pstmt.executeUpdate();
				dbConnection.commit();
			}
		}
		catch (SQLException e)
		{
			logger.error(e);
			close();
		}
	}
	

	@Override
	public void savePrincipal(InterWebPrincipal principal, String password)
	{
		notNull(principal, "principal");
		notNull(password, "password");
		try
		{
			if (openConnection())
			{
				PreparedStatement pstmt = preparedStatements.get(PSTMT_INSERT_PRINCIPAL);
				pstmt.setString(1, principal.getName());
				setString(pstmt, 2, password);
				setString(pstmt, 3, principal.getEmail());
				AuthCredentials authCredentials = principal.getOauthCredentials();
				String key = (authCredentials == null)
				    ? null : authCredentials.getKey();
				String secret = (authCredentials == null)
				    ? null : authCredentials.getSecret();
				setString(pstmt, 4, key);
				setString(pstmt, 5, secret);
				pstmt.executeUpdate();
				for (String role : principal.getRoles())
				{
					pstmt = preparedStatements.get(PSTMT_INSERT_PRINCIPAL_ROLE);
					pstmt.setString(1, principal.getName());
					pstmt.setString(2, role);
					pstmt.executeUpdate();
				}
				dbConnection.commit();
			}
		}
		catch (SQLException e)
		{
			logger.error(e);
			close();
		}
	}
	

	@Override
	public void saveUserAuthCredentials(String connectorName,
	                                    String userName,
	                                    String userId,
	                                    AuthCredentials authCredentials)
	{
		notNull(connectorName, "connectorName");
		notNull(userName, "userName");
		try
		{
			if (openConnection())
			{
				PreparedStatement pstmt = preparedStatements.get(PSTMT_DELETE_USER_AUTH_CREDENTIALS);
				pstmt.setString(1, connectorName);
				pstmt.setString(2, userName);
				pstmt.executeUpdate();
				pstmt = preparedStatements.get(PSTMT_INSERT_USER_AUTH_CREDENTIALS);
				pstmt.setString(1, connectorName);
				pstmt.setString(2, userName);
				pstmt.setString(3, userId);
				String key = (authCredentials == null)
				    ? null : authCredentials.getKey();
				String secret = (authCredentials == null)
				    ? null : authCredentials.getSecret();
				setString(pstmt, 4, key);
				setString(pstmt, 5, secret);
				pstmt.executeUpdate();
				dbConnection.commit();
			}
		}
		catch (SQLException e)
		{
			logger.error(e);
			close();
		}
	}
	

	@Override
	public void updatePrincipal(InterWebPrincipal principal)
	{
		notNull(principal, "principal");
		try
		{
			if (openConnection())
			{
				PreparedStatement pstmt = preparedStatements.get(PSTMT_UPDATE_PRINCIPAL);
				pstmt.setString(1, principal.getEmail());
				AuthCredentials authCredentials = principal.getOauthCredentials();
				String key = (authCredentials == null)
				    ? null : authCredentials.getKey();
				String secret = (authCredentials == null)
				    ? null : authCredentials.getSecret();
				setString(pstmt, 2, key);
				setString(pstmt, 3, secret);
				pstmt.setString(4, principal.getName());
				pstmt.executeUpdate();
				dbConnection.commit();
			}
		}
		catch (SQLException e)
		{
			logger.error(e);
			close();
		}
	}
	

	private void addPreparedStatement(String sqlQuery)
	    throws SQLException
	{
		preparedStatements.put(sqlQuery,
		                       dbConnection.prepareStatement(sqlQuery));
	}
	

	private InterWebPrincipal getPrincipal(String userName, String userPassword)
	{
		InterWebPrincipal dbPrincipal = null;
		try
		{
			if (openConnection())
			{
				PreparedStatement pstmt = preparedStatements.get(PSTMT_SELECT_PRINCIPAL_BY_NAME);
				pstmt.setString(1, userName);
				rs = pstmt.executeQuery();
				if (rs.next())
				{
					String dbPassword = rs.getString(1);
					String dbEmail = rs.getString(2);
					String key = rs.getString(3);
					String secret = rs.getString(4);
					if (dbPassword != null && dbPassword.equals(userPassword))
					{
						dbPrincipal = new InterWebPrincipal(userName, dbEmail);
						if (key != null)
						{
							AuthCredentials authCredentials = new AuthCredentials(key,
							                                                      secret);
							dbPrincipal.setOauthCredentials(authCredentials);
						}
					}
				}
				silentCloseResultSet(rs);
				if (dbPrincipal != null)
				{
					readRoles(dbPrincipal);
				}
			}
		}
		catch (SQLException e)
		{
			logger.error(e);
			close();
		}
		return dbPrincipal;
	}
	

	private ArrayList<String> getRoles(String userName)
	{
		notNull(userName, "userName");
		ArrayList<String> roles = new ArrayList<String>();
		try
		{
			if (openConnection())
			{
				PreparedStatement pstmt = preparedStatements.get(PSTMT_SELECT_PRINCIPAL_ROLES);
				pstmt.setString(1, userName);
				rs = pstmt.executeQuery();
				while (rs.next())
				{
					String role = rs.getString(1);
					if (role != null)
					{
						roles.add(role);
					}
				}
				silentCloseResultSet(rs);
			}
		}
		catch (SQLException e)
		{
			logger.error(e);
			close();
		}
		return roles;
	}
	

	private void init(Configuration configuration)
	{
		logger = Environment.logger;
		connectionUserName = configuration.getValue("database.connection.user-name");
		connectionPassword = configuration.getValue("database.connection.user-password");
		connectionURL = configuration.getValue("database.connection.url");
		driverName = configuration.getValue("database.driver-name");
		try
		{
			openConnection();
		}
		catch (SQLException e)
		{
			logger.error(e);
		}
		Runtime.getRuntime().addShutdownHook(new Thread()
		{
			
			@Override
			public void run()
			{
				logger.info("Shutdown intercepted. Cleaning up Database resources");
				close();
			}
		});
	}
	

	private void initPreparedStatements()
	    throws SQLException
	{
		preparedStatements = new HashMap<String, PreparedStatement>();
		
		addPreparedStatement(PSTMT_HAS_PRINCIPAL);
		addPreparedStatement(PSTMT_SELECT_PRINCIPAL_BY_NAME);
		addPreparedStatement(PSTMT_SELECT_PRINCIPAL_BY_KEY);
		addPreparedStatement(PSTMT_INSERT_PRINCIPAL);
		addPreparedStatement(PSTMT_UPDATE_PRINCIPAL);
		addPreparedStatement(PSTMT_DELETE_PRINCIPAL);
		
		addPreparedStatement(PSTMT_HAS_PRINCIPAL_ROLE);
		addPreparedStatement(PSTMT_SELECT_PRINCIPAL_ROLES);
		addPreparedStatement(PSTMT_INSERT_PRINCIPAL_ROLE);
		addPreparedStatement(PSTMT_DELETE_PRINCIPAL_ROLES);
		
		addPreparedStatement(PSTMT_HAS_CONNECTOR);
		addPreparedStatement(PSTMT_SELECT_CONNECTOR_AUTH_CREDENTIALS);
		addPreparedStatement(PSTMT_UPDATE_CONNECTOR_AUTH_CREDENTIALS);
		addPreparedStatement(PSTMT_INSERT_CONNECTOR);
		addPreparedStatement(PSTMT_DELETE_CONNECTOR);
		
		addPreparedStatement(PSTMT_SELECT_CONSUMER_BY_KEY);
		addPreparedStatement(PSTMT_SELECT_CONSUMERS);
		addPreparedStatement(PSTMT_INSERT_CONSUMER);
		addPreparedStatement(PSTMT_DELETE_CONSUMER);
		
		addPreparedStatement(PSTMT_HAS_USER_AUTH_CREDENTIALS);
		addPreparedStatement(PSTMT_SELECT_USER_AUTH_CREDENTIALS);
		addPreparedStatement(PSTMT_SELECT_CONNECTOR_USER_ID);
		addPreparedStatement(PSTMT_INSERT_USER_AUTH_CREDENTIALS);
		addPreparedStatement(PSTMT_DELETE_USER_AUTH_CREDENTIALS);
		
		addPreparedStatement(PSTMT_HAS_MEDIATOR);
		addPreparedStatement(PSTMT_SELECT_MEDIATOR);
		addPreparedStatement(PSTMT_INSERT_MEDIATOR);
		addPreparedStatement(PSTMT_DELETE_MEDIATOR);
	}
	

	private boolean openConnection()
	    throws SQLException
	{
		if (dbConnection != null && !dbConnection.isValid(10))
		{
			close();
		}
		int numberOfTries = 3;
		while (dbConnection == null && numberOfTries >= 0)
		{
			if (driver == null)
			{
				try
				{
					driver = (Driver) Class.forName(driverName).newInstance();
				}
				catch (Throwable e)
				{
					throw new SQLException(e.getMessage());
				}
			}
			Properties properties = new Properties();
			if (connectionUserName != null)
			{
				properties.put("user", connectionUserName);
			}
			if (connectionPassword != null)
			{
				properties.put("password", connectionPassword);
			}
			dbConnection = driver.connect(connectionURL, properties);
			dbConnection.setAutoCommit(false);
			numberOfTries--;
		}
		if (dbConnection == null)
		{
			logger.error("Opening connection to database " + connectionURL
			             + " failed!");
		}
		else
		{
			initPreparedStatements();
		}
		return (dbConnection != null);
	}
	

	private void readRoles(InterWebPrincipal principal)
	{
		try
		{
			if (openConnection())
			{
				PreparedStatement pstmt = preparedStatements.get(PSTMT_SELECT_PRINCIPAL_ROLES);
				pstmt.setString(1, principal.getName());
				rs = pstmt.executeQuery();
				while (rs.next())
				{
					String role = rs.getString(1);
					principal.addRole(role);
				}
				silentCloseResultSet(rs);
			}
		}
		catch (SQLException e)
		{
			logger.error(e);
			close();
		}
	}
	

	private void setString(PreparedStatement pstmt, int index, String value)
	    throws SQLException
	{
		if (value == null)
		{
			pstmt.setNull(index, java.sql.Types.VARCHAR);
		}
		else
		{
			pstmt.setString(index, value);
		}
	}
	

	private void silentCloseConnection(Connection dbConnection)
	{
		if (dbConnection != null)
		{
			try
			{
				dbConnection.close();
			}
			catch (SQLException e)
			{
				logger.error(e.getMessage());
			}
		}
	}
	

	private void silentCloseResultSet(ResultSet rs)
	{
		if (rs != null)
		{
			try
			{
				rs.close();
			}
			catch (SQLException e)
			{
				logger.error(e);
			}
		}
	}
	

	private void silentCloseStatement(Statement stmt)
	{
		if (stmt != null)
		{
			try
			{
				stmt.close();
			}
			catch (SQLException e)
			{
				logger.error(e);
			}
		}
	}
}

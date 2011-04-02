package de.l3s.interwebj.db;


import java.sql.*;
import java.util.*;

import org.apache.log4j.*;

import de.l3s.interwebj.*;
import de.l3s.interwebj.config.*;
import de.l3s.interwebj.core.*;


public class JDBCDatabase
    implements Database
{
	
	private Logger logger;
	
	private Connection dbConnection = null;
	private String connectionUserName = null;
	private String connectionPassword = null;
	private String connectionURL = null;
	private Driver driver = null;
	private String driverName = null;
	private String userTable = null;
	private String userNameCol = null;
	private String userPasswordCol = null;
	private String userEmailCol = null;
	private String roleTable = null;
	private String roleNameCol = null;
	private String userRolesTable = null;
	
	private Statement stmt = null;
	private ResultSet rs = null;
	

	public JDBCDatabase(Configuration configuration)
	{
		init(configuration);
	}
	

	@Override
	public IWPrincipal authenticate(String userName, String userPassword)
	{
		if (userName == null)
		{
			throw new NullPointerException("Argument [userName] can not be null");
		}
		if (userPassword == null)
		{
			throw new NullPointerException("Argument [userPassword] can not be null");
		}
		Environment.logger.debug("authenticating InterWebJ user [" + userName
		                         + "]");
		IWPrincipal dbPrincipal = getPrincipal(userName, userPassword);
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
		silentCloseStatement(stmt);
		stmt = null;
		silentCloseConnection(dbConnection);
		dbConnection = null;
	}
	

	private PreparedStatement createInsertPrincipalRolesStmt(String name,
	                                                         String role)
	    throws SQLException
	{
		StringBuilder sb = new StringBuilder();
		sb.append(" INSERT INTO ").append(userRolesTable);
		sb.append(" (user,").append(roleNameCol).append(")");
		sb.append(" SELECT name, ? FROM ").append(userTable);
		sb.append(" WHERE ").append(userNameCol).append("=?");
		Environment.logger.debug("sql query: " + sb);
		PreparedStatement pstmt = dbConnection.prepareStatement(sb.toString());
		pstmt.setString(1, role);
		pstmt.setString(2, name);
		return pstmt;
	}
	

	private PreparedStatement createInsertPrincipalStmt(IWPrincipal principal,
	                                                    String password)
	    throws SQLException
	{
		StringBuilder sb = new StringBuilder();
		sb.append(" INSERT INTO ").append(userTable);
		sb.append(" (").append(userNameCol).append(",").append(userPasswordCol).append(",").append(userEmailCol).append(")");
		sb.append(" VALUES (?,?,?)");
		Environment.logger.debug("sql query: " + sb);
		PreparedStatement pstmt = dbConnection.prepareStatement(sb.toString());
		pstmt.setString(1, principal.getName());
		pstmt.setString(2, password);
		pstmt.setString(3, principal.getEmail());
		return pstmt;
	}
	

	private PreparedStatement createInsertRoleStatement(String role)
	    throws SQLException
	{
		StringBuilder sb = new StringBuilder();
		sb.append(" INSERT INTO ").append(roleTable);
		sb.append(" (").append(roleNameCol).append(")");
		sb.append(" VALUES (?)");
		Environment.logger.debug("sql query: " + sb);
		PreparedStatement pstmt = dbConnection.prepareStatement(sb.toString());
		pstmt.setString(1, role);
		return pstmt;
	}
	

	@Override
	public void deleteConsumer(String provider, String consumer)
	{
		if (provider == null)
		{
			throw new NullPointerException("Argument [provider] can not be null");
		}
		if (consumer == null)
		{
			throw new NullPointerException("Argument [consumer] can not be null");
		}
		try
		{
			boolean exists = hasConsumer(provider, consumer);
			if (exists)
			{
				openConnection();
				String sqlQuery = "DELETE FROM iwj_connectors";
				sqlQuery += " WHERE provider='" + provider + "' AND consumer='"
				            + consumer + "'";
				Environment.logger.debug("sql query: " + sqlQuery);
				Statement stmt = dbConnection.createStatement();
				stmt.execute(sqlQuery);
				silentCloseStatement(stmt);
				dbConnection.commit();
			}
		}
		catch (SQLException e)
		{
			logger.error(e);
			close();
		}
	}
	

	private IWPrincipal getPrincipal(String userName, String userPassword)
	{
		IWPrincipal dbPrincipal = null;
		try
		{
			openConnection();
			StringBuilder sb = new StringBuilder();
			sb.append(" SELECT ").append(userPasswordCol).append(",").append(userEmailCol);
			sb.append(" FROM ").append(userTable);
			sb.append(" WHERE ").append(userNameCol).append("='").append(userName).append("'");
			Environment.logger.debug("sql query: " + sb);
			stmt = dbConnection.createStatement();
			rs = stmt.executeQuery(sb.toString());
			String dbPassword = null;
			String dbEmail = null;
			if (rs.next())
			{
				dbPassword = rs.getString(1);
				dbEmail = rs.getString(2);
			}
			if (dbPassword != null && dbPassword.equals(userPassword))
			{
				dbPrincipal = new IWPrincipal(userName, dbEmail);
			}
			silentCloseResultSet(rs);
			silentCloseStatement(stmt);
		}
		catch (SQLException e)
		{
			logger.error(e);
			close();
		}
		return dbPrincipal;
	}
	

	private ArrayList<String> getRoles(String usermame)
	{
		if (usermame == null)
		{
			throw new NullPointerException("Argument [userName] can not be null");
		}
		ArrayList<String> roles = new ArrayList<String>();
		try
		{
			openConnection();
			StringBuilder sb = new StringBuilder();
			sb.append("SELECT distinct(").append(roleNameCol).append(")");
			sb.append(" FROM ").append(userTable).append(" NATURAL JOIN ").append(userRolesTable);
			sb.append(" WHERE ").append(userNameCol).append("='").append(usermame).append("'");
			Environment.logger.debug("sql query: " + sb);
			stmt = dbConnection.createStatement();
			rs = stmt.executeQuery(sb.toString());
			while (rs.next())
			{
				String role = rs.getString(1);
				if (role != null)
				{
					roles.add(role.trim());
				}
			}
			silentCloseResultSet(rs);
			silentCloseStatement(stmt);
		}
		catch (SQLException e)
		{
			logger.error(e);
			close();
		}
		return roles;
	}
	

	private boolean hasConsumer(String provider, String consumer)
	    throws SQLException
	{
		boolean exists = false;
		openConnection();
		String sqlQuery = "SELECT count(*) FROM iwj_connectors WHERE provider='"
		                  + provider + "' AND consumer='" + consumer + "'";
		Environment.logger.debug("sql query: " + sqlQuery);
		Statement stmt = dbConnection.createStatement();
		rs = stmt.executeQuery(sqlQuery);
		if (rs.next())
		{
			exists = (rs.getInt(1) == 1);
		}
		silentCloseResultSet(rs);
		silentCloseStatement(stmt);
		return exists;
	}
	

	@Override
	public boolean hasUser(String username)
	{
		if (username == null)
		{
			throw new NullPointerException("Argument [userName] can not be null");
		}
		boolean userExists = true;
		try
		{
			openConnection();
			String sqlQuery = " SELECT count(*) FROM " + userTable;
			sqlQuery += " WHERE " + userNameCol + "='" + username + "'";
			Environment.logger.debug("sql query: " + sqlQuery);
			stmt = dbConnection.createStatement();
			rs = stmt.executeQuery(sqlQuery);
			if (rs.next())
			{
				userExists = (rs.getInt(1) == 1);
			}
			silentCloseResultSet(rs);
			silentCloseStatement(stmt);
		}
		catch (SQLException e)
		{
			logger.error(e);
			close();
		}
		return userExists;
	}
	

	private boolean hasUserAuthCredentials(String provider, String userName)
	    throws SQLException
	{
		boolean exists = false;
		openConnection();
		String sqlQuery = "SELECT count(*) FROM iwj_users_auth_data WHERE provider='"
		                  + provider + "' AND user='" + userName + "'";
		Environment.logger.debug("sql query: " + sqlQuery);
		Statement stmt = dbConnection.createStatement();
		rs = stmt.executeQuery(sqlQuery);
		if (rs.next())
		{
			exists = (rs.getInt(1) == 1);
		}
		silentCloseResultSet(rs);
		silentCloseStatement(stmt);
		return exists;
	}
	

	private void init(Configuration configuration)
	{
		logger = Environment.logger;
		connectionUserName = configuration.getProperty("database.connection.user-name");
		connectionPassword = configuration.getProperty("database.connection.user-password");
		connectionURL = configuration.getProperty("database.connection.url");
		driverName = configuration.getProperty("database.driver-name");
		userTable = configuration.getProperty("database.user-table.name");
		userNameCol = configuration.getProperty("database.user-table.user-column");
		userPasswordCol = configuration.getProperty("database.user-table.password-column");
		userEmailCol = configuration.getProperty("database.user-table.email-column");
		roleTable = configuration.getProperty("database.role-table.name");
		userRolesTable = configuration.getProperty("database.user-role-table.name");
		roleNameCol = configuration.getProperty("database.user-role-table.role-column");
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
	

	private void openConnection()
	    throws SQLException
	{
		int numberOfTries = 2;
		while (numberOfTries > 0)
		{
			try
			{
				if (dbConnection == null)
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
					properties.put("autoReconnect", "true");
					dbConnection = driver.connect(connectionURL, properties);
					dbConnection.setAutoCommit(false);
				}
			}
			catch (SQLException e)
			{
				if (dbConnection != null)
				{
					close();
				}
			}
			numberOfTries--;
		}
	}
	

	@Override
	public AuthCredentials readConsumerAuthCredentials(String provider,
	                                                   String consumer)
	{
		if (provider == null)
		{
			throw new NullPointerException("Argument [provider] can not be null");
		}
		if (consumer == null)
		{
			throw new NullPointerException("Argument [consumer] can not be null");
		}
		AuthCredentials consumerAuthCredentials = null;
		try
		{
			String key;
			String secret;
			openConnection();
			String sqlQuery = "SELECT consumer_key, consumer_secret FROM iwj_connectors WHERE provider='"
			                  + provider + "' AND consumer='" + consumer + "'";
			Environment.logger.debug("sql query: " + sqlQuery);
			stmt = dbConnection.createStatement();
			rs = stmt.executeQuery(sqlQuery);
			if (rs.next())
			{
				key = rs.getString(1);
				secret = rs.getString(2);
				if (key != null)
				{
					consumerAuthCredentials = new AuthCredentials(key, secret);
				}
			}
			silentCloseResultSet(rs);
			silentCloseStatement(stmt);
		}
		catch (SQLException e)
		{
			logger.error(e);
			close();
		}
		return consumerAuthCredentials;
	}
	

	@Override
	public AuthCredentials readUserAuthCredentials(String provider,
	                                               String userName)
	{
		if (provider == null)
		{
			throw new NullPointerException("Argument [provider] can not be null");
		}
		if (userName == null)
		{
			throw new NullPointerException("Argument [userName] can not be null");
		}
		AuthCredentials authCredentials = null;
		try
		{
			String key;
			String secret;
			openConnection();
			String sqlQuery = "SELECT user_key, user_secret FROM iwj_users_auth_data WHERE provider='"
			                  + provider + "' AND user='" + userName + "'";
			//			Environment.logger.debug("sql query: " + sqlQuery);
			stmt = dbConnection.createStatement();
			rs = stmt.executeQuery(sqlQuery);
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
			silentCloseStatement(stmt);
		}
		catch (SQLException e)
		{
			logger.error(e);
			close();
		}
		return authCredentials;
	}
	

	@Override
	public void saveConsumer(String provider,
	                         String consumer,
	                         AuthCredentials authCredentials)
	{
		if (provider == null)
		{
			throw new NullPointerException("Argument [provider] can not be null");
		}
		if (consumer == null)
		{
			throw new NullPointerException("Argument [consumer] can not be null");
		}
		try
		{
			boolean exists = hasConsumer(provider, consumer);
			openConnection();
			PreparedStatement pstmt = null;
			String sqlQuery = null;
			if (exists)
			{
				sqlQuery = "UPDATE iwj_connectors SET consumer_key=?, consumer_secret=? WHERE provider ='"
				           + provider + "' AND consumer='" + consumer + "'";
				Environment.logger.debug("sql query: " + sqlQuery);
				pstmt = dbConnection.prepareStatement(sqlQuery);
				if (authCredentials == null)
				{
					pstmt.setNull(1, java.sql.Types.VARCHAR);
					pstmt.setNull(2, java.sql.Types.VARCHAR);
				}
				else
				{
					pstmt.setString(1, authCredentials.getKey());
					if (authCredentials.getSecret() == null)
					{
						pstmt.setNull(2, java.sql.Types.VARCHAR);
					}
					else
					{
						pstmt.setString(2, authCredentials.getSecret());
					}
				}
			}
			else
			{
				sqlQuery = "INSERT INTO iwj_connectors (provider, consumer, consumer_key, consumer_secret) VALUES (?,?,?,?)";
				Environment.logger.debug("sql query: " + sqlQuery);
				pstmt = dbConnection.prepareStatement(sqlQuery);
				pstmt.setString(1, provider);
				pstmt.setString(2, consumer);
				if (authCredentials.getKey() == null)
				{
					Environment.logger.info("consumer key is null");
					pstmt.setNull(3, java.sql.Types.VARCHAR);
				}
				else
				{
					pstmt.setString(3, authCredentials.getKey());
				}
				if (authCredentials.getSecret() == null)
				{
					pstmt.setNull(4, java.sql.Types.VARCHAR);
				}
				else
				{
					pstmt.setString(4, authCredentials.getSecret());
				}
			}
			pstmt.executeUpdate();
			silentCloseStatement(pstmt);
			dbConnection.commit();
		}
		catch (SQLException e)
		{
			logger.error(e);
			close();
		}
	}
	

	@Override
	public boolean savePrincipal(IWPrincipal principal, String password)
	{
		if (principal == null)
		{
			throw new NullPointerException("Argument [principal] can not be null");
		}
		if (password == null)
		{
			throw new NullPointerException("Argument [password] can not be null");
		}
		try
		{
			openConnection();
			PreparedStatement pstmt = createInsertPrincipalStmt(principal,
			                                                    password);
			if (pstmt.executeUpdate() != 1)
			{
				silentCloseStatement(pstmt);
				return false;
			}
			for (String role : principal.getRoles())
			{
				pstmt = createInsertPrincipalRolesStmt(principal.getName(),
				                                       role);
				if (pstmt.executeUpdate() != 1)
				{
					silentCloseStatement(pstmt);
					return false;
				}
			}
			silentCloseStatement(pstmt);
			dbConnection.commit();
		}
		catch (SQLException e)
		{
			logger.error(e);
			close();
		}
		return true;
	}
	

	@Override
	public boolean saveRole(String role)
	{
		if (role == null)
		{
			throw new NullPointerException("Argument [role] can not be null");
		}
		try
		{
			openConnection();
			PreparedStatement pstmt = createInsertRoleStatement(role);
			if (pstmt.executeUpdate() != 1)
			{
				silentCloseStatement(pstmt);
				return false;
			}
			silentCloseStatement(pstmt);
			dbConnection.commit();
		}
		catch (SQLException e)
		{
			logger.error(e);
			close();
		}
		return true;
	}
	

	@Override
	public void saveUserAuthCredentials(String provider,
	                                    String userName,
	                                    AuthCredentials authCredentials)
	{
		if (provider == null)
		{
			throw new NullPointerException("Argument [provider] can not be null");
		}
		if (userName == null)
		{
			throw new NullPointerException("Argument [userName] can not be null");
		}
		try
		{
			boolean exists = hasUserAuthCredentials(provider, userName);
			openConnection();
			PreparedStatement pstmt = null;
			String sqlQuery = null;
			if (exists)
			{
				sqlQuery = "UPDATE iwj_users_auth_data SET user_key=?, user_secret=? WHERE provider ='"
				           + provider + "' AND user='" + userName + "'";
				Environment.logger.debug("sql query: " + sqlQuery);
				pstmt = dbConnection.prepareStatement(sqlQuery);
				if (authCredentials == null)
				{
					pstmt.setNull(1, java.sql.Types.VARCHAR);
					pstmt.setNull(2, java.sql.Types.VARCHAR);
				}
				else
				{
					pstmt.setString(1, authCredentials.getKey());
					if (authCredentials.getSecret() == null)
					{
						pstmt.setNull(2, java.sql.Types.VARCHAR);
					}
					else
					{
						pstmt.setString(2, authCredentials.getSecret());
					}
				}
			}
			else
			{
				sqlQuery = "INSERT INTO iwj_users_auth_data (provider, user, user_key, user_secret) VALUES (?,?,?,?)";
				Environment.logger.debug("sql query: " + sqlQuery);
				pstmt = dbConnection.prepareStatement(sqlQuery);
				pstmt.setString(1, provider);
				pstmt.setString(2, userName);
				if (authCredentials.getKey() == null)
				{
					Environment.logger.info("consumer key is null");
					pstmt.setNull(3, java.sql.Types.VARCHAR);
				}
				else
				{
					pstmt.setString(3, authCredentials.getKey());
				}
				if (authCredentials.getSecret() == null)
				{
					pstmt.setNull(4, java.sql.Types.VARCHAR);
				}
				else
				{
					pstmt.setString(4, authCredentials.getSecret());
				}
			}
			pstmt.executeUpdate();
			silentCloseStatement(pstmt);
			dbConnection.commit();
		}
		catch (SQLException e)
		{
			logger.error(e);
			close();
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

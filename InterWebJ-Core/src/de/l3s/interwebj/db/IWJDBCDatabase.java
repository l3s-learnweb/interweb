package de.l3s.interwebj.db;


import java.sql.*;
import java.util.*;

import org.apache.log4j.*;

import de.l3s.interwebj.core.*;


public class IWJDBCDatabase
    implements IWDatabase
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
	

	public IWJDBCDatabase(IWConfiguration iWConfiguration)
	{
		init(iWConfiguration);
	}
	

	@Override
	public boolean addPrincipal(IWPrincipal principal, String password)
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
			logger.error(e.getMessage());
			close();
		}
		return true;
	}
	

	@Override
	public boolean addRole(String role)
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
			logger.error(e.getMessage());
			close();
		}
		return true;
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
		IWPrincipal dbPrincipal = getPrincipal(userName, userPassword);
		if (dbPrincipal != null)
		{
			List<String> roles = getRoles(userName);
			for (String role : roles)
			{
				dbPrincipal.addRole(role);
			}
		}
		return dbPrincipal;
	}
	

	@Override
	public void close()
	{
		silentCloseResultSet(rs);
		silentCloseStatement(stmt);
		silentCloseConnection(dbConnection);
		//		try
		//		{
		//			if (preparedPasswords != null)
		//			{
		//				preparedPasswords.close();
		//				preparedPasswords = null;
		//			}
		//		}
		//		catch (SQLException e)
		//		{
		//			logger.error(e.getMessage());
		//		}
		//		try
		//		{
		//			if (preparedRoles != null)
		//			{
		//				preparedRoles.close();
		//				preparedRoles = null;
		//			}
		//		}
		//		catch (SQLException e)
		//		{
		//			logger.error(e.getMessage());
		//		}
	}
	

	private PreparedStatement createInsertPrincipalRolesStmt(String name,
	                                                         String role)
	    throws SQLException
	{
		StringBuilder sb = new StringBuilder();
		sb.append(" INSERT INTO ").append(userRolesTable);
		sb.append(" (id,").append(roleNameCol).append(")");
		sb.append(" SELECT id, ? FROM ").append(userTable);
		sb.append(" WHERE ").append(userNameCol).append("=?");
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
		PreparedStatement pstmt = dbConnection.prepareStatement(sb.toString());
		pstmt.setString(1, role);
		return pstmt;
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
			logger.error(e.getMessage());
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
			sb.append(" SELECT ").append(roleNameCol);
			sb.append(" FROM ").append(userTable).append(" NATURAL JOIN ").append(userRolesTable);
			sb.append(" WHERE ").append(userNameCol).append("='").append(usermame).append("'");
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
			logger.error(e.getMessage());
			close();
		}
		return roles;
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
			StringBuilder sb = new StringBuilder();
			sb.append(" SELECT count(*)");
			sb.append(" FROM ").append(userTable);
			sb.append(" WHERE ").append(userNameCol).append("='").append(username).append("'");
			stmt = dbConnection.createStatement();
			rs = stmt.executeQuery(sb.toString());
			if (rs.next())
			{
				userExists = (rs.getInt(1) == 1);
			}
			silentCloseResultSet(rs);
			silentCloseStatement(stmt);
		}
		catch (SQLException e)
		{
			logger.error(e.getMessage());
			close();
		}
		return userExists;
	}
	

	private void init(IWConfiguration configuration)
	{
		logger = IWEnvironment.logger;
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
			logger.error(e.getMessage());
		}
		Runtime.getRuntime().addShutdownHook(new Thread()
		{
			
			@Override
			public void run()
			{
				logger.info("Shutdown intercepted. Cleaning up resources");
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
				logger.error(e.getMessage());
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
				logger.error(e.getMessage());
			}
		}
	}
}

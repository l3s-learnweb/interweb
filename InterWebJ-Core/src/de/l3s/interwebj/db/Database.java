package de.l3s.interwebj.db;


import de.l3s.interwebj.*;
import de.l3s.interwebj.core.*;


public interface Database
{
	
	public IWPrincipal authenticate(String userName, String userPassword);
	

	public void close();
	

	public void deleteConsumer(String provider, String consumer);
	

	public boolean hasUser(String username);
	

	public AuthData readConsumerAuthData(String provider, String consumer);
	

	public AuthData readUserAuthData(String provider, String userName);
	

	public void saveConsumer(String provider, String consumer, AuthData authData);
	

	public boolean savePrincipal(IWPrincipal principal, String password);
	

	public boolean saveRole(String role);
	

	public void saveUserAuthData(String provider,
	                             String userName,
	                             AuthData authData);
	
}

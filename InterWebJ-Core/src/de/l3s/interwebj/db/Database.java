package de.l3s.interwebj.db;


import de.l3s.interwebj.*;
import de.l3s.interwebj.core.*;


public interface Database
{
	
	public IWPrincipal authenticate(String userName, String userPassword);
	

	public void close();
	

	public void deleteConsumer(String provider, String consumer);
	

	public boolean hasUser(String username);
	

	public AuthCredentials readConsumerAuthCredentials(String provider,
	                                                   String consumer);
	

	public AuthCredentials readUserAuthCredentials(String provider,
	                                               String userName);
	

	public void saveConsumer(String provider,
	                         String consumer,
	                         AuthCredentials authCredentials);
	

	public boolean savePrincipal(IWPrincipal principal, String password);
	

	public boolean saveRole(String role);
	

	public void saveUserAuthCredentials(String provider,
	                                    String userName,
	                                    AuthCredentials authCredentials);
	
}

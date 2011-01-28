package de.l3s.interwebj.db;


import de.l3s.interwebj.core.*;


public interface IWDatabase
{
	
	public boolean addPrincipal(IWPrincipal principal, String password);
	

	public boolean addRole(String role);
	

	public IWPrincipal authenticate(String userName, String userPassword);
	

	public void close();
	

	public boolean hasUser(String username);
	
}

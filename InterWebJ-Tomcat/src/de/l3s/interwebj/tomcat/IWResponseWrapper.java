package de.l3s.interwebj.tomcat;


import java.io.*;

import javax.servlet.http.*;


public class IWResponseWrapper
    extends HttpServletResponseWrapper
{
	
	private int httpStatus;
	

	public IWResponseWrapper(HttpServletResponse response)
	{
		super(response);
	}
	

	public int getStatus()
	{
		return httpStatus;
	}
	

	@Override
	public void sendError(int sc)
	    throws IOException
	{
		httpStatus = sc;
		super.sendError(sc);
	}
	

	@Override
	public void sendError(int sc, String msg)
	    throws IOException
	{
		httpStatus = sc;
		super.sendError(sc, msg);
	}
	

	@Override
	public void setStatus(int sc)
	{
		httpStatus = sc;
		super.setStatus(sc);
	}
	
}

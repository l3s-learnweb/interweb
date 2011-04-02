package de.l3s.interwebj.rest;


import java.io.*;

import javax.servlet.http.*;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

import de.l3s.interwebj.util.*;


@Path("/users/{user}/uploads")
public class Upload
{
	
	@Context
	private UriInfo uriInfo;
	
	@Context
	private HttpServletRequest request;
	

	@POST
	@Produces(MediaType.TEXT_PLAIN)
	//	public IWUploadResponse getQueryResult()
	public String getQueryResult(InputStream is)
	    throws IOException
	{
		System.out.println(request.getMethod());
		System.out.println(request.getContentType());
		System.out.println(request.getCharacterEncoding());
		System.out.println(request.getContentLength());
		System.out.println(CoreUtils.convertToString(request.getAttributeNames()));
		System.out.println(CoreUtils.convertToString(request.getHeaderNames()));
		System.out.println(request.getParameterMap());
		StringBuilder sb = new StringBuilder();
		BufferedReader br = new BufferedReader(new InputStreamReader(is,
		                                                             "UTF-8"));
		int c;
		while ((c = br.read()) != -1)
		{
			sb.append((char) c);
		}
		br.close();
		System.out.println(sb);
		return "hello";
	}
}

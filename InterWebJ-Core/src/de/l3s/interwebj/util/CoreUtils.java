package de.l3s.interwebj.util;


import java.io.*;
import java.security.*;
import java.util.*;

import javax.xml.bind.*;

import org.apache.commons.codec.binary.*;

import com.sun.jersey.api.client.*;


public class CoreUtils
{
	
	public static <T> String collectionToString(Collection<T> c)
	{
		StringBuilder sb = new StringBuilder();
		for (Iterator<T> i = c.iterator(); i.hasNext();)
		{
			T t = i.next();
			sb.append(t.toString());
			if (i.hasNext())
			{
				sb.append(',');
			}
		}
		return sb.toString();
	}
	

	@SuppressWarnings("unchecked")
	public static <T> T create(Class<T> clazz, InputStream is)
	    throws JAXBException
	{
		JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		return (T) unmarshaller.unmarshal(is);
	}
	

	public static String generateMD5Hash(byte[] data)
	{
		try
		{
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.reset();
			md.update(data);
			final byte[] resultByte = md.digest();
			return new String(Hex.encodeHex(resultByte));
		}
		catch (NoSuchAlgorithmException shouldNeverOccurs)
		{
			shouldNeverOccurs.printStackTrace();
		}
		return null;
	}
	

	public static String getClientResponseContent(ClientResponse response)
	    throws IOException
	{
		StringBuilder sb = new StringBuilder();
		InputStream is = response.getEntityInputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(is,
		                                                             "UTF-8"));
		int c;
		while ((c = br.read()) != -1)
		{
			sb.append((char) c);
		}
		br.close();
		return sb.toString();
	}
	

	public static <T> String setToString(Set<T> s)
	{
		StringBuilder sb = new StringBuilder();
		for (Iterator<T> i = s.iterator(); i.hasNext();)
		{
			T t = i.next();
			sb.append(t.toString());
			if (i.hasNext())
			{
				sb.append(',');
			}
		}
		return sb.toString();
	}
	
}

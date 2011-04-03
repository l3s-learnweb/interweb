package de.l3s.interwebj.util;


import java.io.*;
import java.security.*;
import java.text.*;
import java.util.*;

import javax.xml.bind.*;

import org.apache.commons.codec.binary.*;

import com.sun.jersey.api.client.*;


public class CoreUtils
{
	
	private static final DateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	

	public static List<String> convertToUniqueList(String s)
	{
		Set<String> list = new HashSet<String>();
		String[] tokens = s.split("[,\\s]");
		for (String token : tokens)
		{
			if (token.length() > 0)
			{
				list.add(token);
			}
		}
		return new ArrayList<String>(list);
	}
	

	public static <T> String convertToString(Collection<T> c)
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
	

	public static <T> String convertToString(Enumeration<T> e)
	{
		StringBuilder sb = new StringBuilder();
		while (e.hasMoreElements())
		{
			T t = e.nextElement();
			sb.append(t.toString());
			if (e.hasMoreElements())
			{
				sb.append(',');
			}
		}
		return sb.toString();
	}
	

	public static <T> String convertToString(Set<T> s)
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
	

	@SuppressWarnings("unchecked")
	public static <T> T create(Class<T> clazz, InputStream is)
	    throws JAXBException
	{
		JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		return (T) unmarshaller.unmarshal(is);
	}
	

	public static String formatDate(DateFormat df, long millis)
	{
		return df.format(new Date(millis));
	}
	

	public static String formatDate(long millis)
	{
		return formatDate(DEFAULT_DATE_FORMAT, millis);
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
	

	public static void main(String[] args)
	{
		String s = "te\nst1,te,,,\t st2";
		System.out.println(convertToUniqueList(s));
	}
	

	public static long parseDate(DateFormat df, String dateString)
	    throws ParseException
	{
		return df.parse(dateString).getTime();
	}
	

	public static long parseDate(String dateString)
	    throws ParseException
	{
		return parseDate(DEFAULT_DATE_FORMAT, dateString);
	}
	

	public static String printclientResponse(ClientResponse response)
	{
		StringBuilder sb = new StringBuilder();
		try
		{
			BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntityInputStream(),
			                                                             "UTF-8"));
			int c;
			while ((c = br.read()) != -1)
			{
				sb.append((char) c);
			}
			br.close();
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return sb.toString();
	}
	
}

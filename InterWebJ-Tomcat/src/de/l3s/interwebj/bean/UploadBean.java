package de.l3s.interwebj.bean;


import java.io.*;
import java.nio.charset.*;
import java.util.*;

import javax.faces.bean.*;
import javax.faces.model.*;

import org.richfaces.event.*;
import org.richfaces.model.*;

import de.l3s.interwebj.*;
import de.l3s.interwebj.connector.*;
import de.l3s.interwebj.core.*;
import de.l3s.interwebj.query.*;
import de.l3s.interwebj.webutil.*;


/**
 * @author olex
 * 
 */
@ManagedBean
@ViewScoped
public class UploadBean
    implements Serializable
{
	
	private static final long serialVersionUID = -3906461569264684939L;
	
	private String selectedContentType;
	private List<String> selectedConnectors;
	private byte[] data;
	private String title;
	private String description;
	private String tags;
	private String text;
	private String fileName;
	

	public UploadBean()
	{
		title = "No Title";
		description = "No Description";
	}
	

	public List<SelectItem> getConnectors()
	    throws InterWebException
	{
		List<SelectItem> connectors = new ArrayList<SelectItem>();
		Engine engine = Environment.getInstance().getEngine();
		IWPrincipal principal = FacesUtils.getSessionBean().getPrincipal();
		for (ServiceConnector connector : engine.getConnectors())
		{
			if (connector.supportContentType(selectedContentType)
			    && connector.isRegistered()
			    && engine.isUserAuthenticated(connector, principal))
			{
				SelectItem selectItem = new SelectItem(connector.getName());
				connectors.add(selectItem);
			}
		}
		return connectors;
	}
	

	public List<SelectItem> getContentTypes()
	    throws InterWebException
	{
		List<SelectItem> contentTypes = new ArrayList<SelectItem>();
		Engine engine = Environment.getInstance().getEngine();
		for (String contentType : engine.getContentTypes())
		{
			SelectItem selectItem = new SelectItem(contentType);
			contentTypes.add(selectItem);
		}
		return contentTypes;
	}
	

	public String getDescription()
	{
		return description;
	}
	

	public List<String> getSelectedConnectors()
	{
		return selectedConnectors;
	}
	

	public String getSelectedContentType()
	{
		return selectedContentType;
	}
	

	public String getTags()
	{
		return tags;
	}
	

	public String getText()
	{
		return text;
	}
	

	public String getTitle()
	{
		return title;
	}
	

	public boolean isFileUpload()
	{
		return Query.CT_IMAGE.equals(selectedContentType)
		       || Query.CT_VIDEO.equals(selectedContentType)
		       || Query.CT_AUDIO.equals(selectedContentType);
	}
	

	public void listener(FileUploadEvent event)
	    throws Exception
	{
		UploadedFile uploadedFile = event.getUploadedFile();
		fileName = uploadedFile.getName();
		Environment.logger.debug("File to upload: " + uploadedFile.getName());
		Environment.logger.debug("Content type: "
		                         + uploadedFile.getContentType());
		data = uploadedFile.getData();
		Environment.logger.debug("Read data size: " + data.length);
	}
	

	public void save()
	{
	}
	

	public void setDescription(String description)
	{
		this.description = description;
	}
	

	public void setSelectedConnectors(List<String> selectedConnectors)
	{
		this.selectedConnectors = selectedConnectors;
	}
	

	public void setSelectedContentType(String selectedContentType)
	{
		this.selectedContentType = selectedContentType;
	}
	

	public void setTags(String tags)
	{
		this.tags = tags;
	}
	

	public void setText(String text)
	{
		this.text = text;
	}
	

	public void setTitle(String title)
	{
		this.title = title;
	}
	

	public void upload()
	    throws InterWebException
	{
		Environment.logger.debug("uploading binary data...");
		if (data != null && selectedConnectors != null)
		{
			Engine engine = Environment.getInstance().getEngine();
			IWPrincipal principal = FacesUtils.getSessionBean().getPrincipal();
			Parameters params = new Parameters();
			params.add(Parameters.TITLE, title);
			params.add(Parameters.DESCRIPTION, description);
			params.add(Parameters.TAGS, tags);
			params.add("filename", fileName);
			engine.upload(data,
			              principal,
			              selectedConnectors,
			              selectedContentType,
			              params);
			data = null;
		}
	}
	

	public void uploadText()
	    throws InterWebException
	{
		Environment.logger.debug("text to upload: [" + text + "]");
		if (text != null && selectedConnectors != null)
		{
			Engine engine = Environment.getInstance().getEngine();
			IWPrincipal principal = FacesUtils.getSessionBean().getPrincipal();
			Parameters params = new Parameters();
			params.add(Parameters.TITLE, title);
			params.add(Parameters.DESCRIPTION, description);
			params.add(Parameters.TAGS, tags);
			engine.upload(text.getBytes(Charset.forName("UTF-8")),
			              principal,
			              selectedConnectors,
			              selectedContentType,
			              new Parameters());
			text = null;
		}
	}
}

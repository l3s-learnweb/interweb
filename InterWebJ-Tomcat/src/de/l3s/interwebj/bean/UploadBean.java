package de.l3s.interwebj.bean;


import java.util.*;

import javax.faces.bean.*;
import javax.faces.model.*;

import org.richfaces.event.*;
import org.richfaces.model.*;

import de.l3s.interwebj.connector.*;
import de.l3s.interwebj.core.*;
import de.l3s.interwebj.webutil.*;


/**
 * @author olex
 * 
 */
@ManagedBean
@SessionScoped
public class UploadBean
{
	
	private static final long serialVersionUID = -4894599353026933768L;
	
	private String selectedContentType;
	private List<String> selectedConnectors;
	private byte[] data;
	

	public List<SelectItem> getConnectors()
	    throws InterWebException
	{
		List<SelectItem> connectors = new ArrayList<SelectItem>();
		Engine engine = Environment.getInstance().getEngine();
		IWPrincipal principal = FacesUtils.getPrincipalBean().getPrincipal();
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
	

	public List<String> getSelectedConnectors()
	{
		return selectedConnectors;
	}
	

	public String getSelectedContentType()
	{
		return selectedContentType;
	}
	

	public void listener(FileUploadEvent event)
	    throws Exception
	{
		UploadedFile uploadedFile = event.getUploadedFile();
		Environment.logger.debug("File to upload: " + uploadedFile.getName());
		data = uploadedFile.getData();
		Environment.logger.debug("Read data size: " + data.length);
	}
	

	public void save()
	{
	}
	

	public void setSelectedConnectors(List<String> selectedConnectors)
	{
		this.selectedConnectors = selectedConnectors;
	}
	

	public void setSelectedContentType(String selectedContentType)
	{
		this.selectedContentType = selectedContentType;
	}
	

	public void upload()
	    throws InterWebException
	{
		Environment.logger.debug("selectedConnectors: " + selectedConnectors);
		if (data != null && selectedConnectors != null)
		{
			Engine engine = Environment.getInstance().getEngine();
			IWPrincipal principal = FacesUtils.getPrincipalBean().getPrincipal();
			engine.upload(data,
			              principal,
			              selectedConnectors,
			              selectedContentType);
			data = null;
		}
	}
}

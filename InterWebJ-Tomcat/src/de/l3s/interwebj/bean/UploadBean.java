package de.l3s.interwebj.bean;


import java.io.*;
import java.util.*;

import javax.faces.application.*;
import javax.faces.bean.*;
import javax.faces.context.*;
import javax.faces.model.*;

import org.primefaces.event.*;

import de.l3s.interwebj.connector.*;
import de.l3s.interwebj.core.*;
import de.l3s.interwebj.util.*;


@ManagedBean
@RequestScoped
public class UploadBean
{
	
	private static final long serialVersionUID = -4894599353026933768L;
	

	public void fileUpload(FileUploadEvent event)
	{
		Environment.logger.debug(event.getFile().getFileName());
		try
		{
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			int i;
			byte[] buffer = new byte[1024];
			InputStream is = event.getFile().getInputstream();
			while ((i = is.read(buffer)) != -1)
			{
				out.write(buffer, 0, i);
				out.flush();
			}
			byte data[] = out.toByteArray();
			out.close();
			is.close();
			Engine engine = Utils.getEngine();
			Environment.logger.debug("uploading done");
			FacesMessage msg = new FacesMessage("Succesful",
			                                    event.getFile().getFileName()
			                                        + " is uploaded.");
			FacesContext.getCurrentInstance().addMessage(null, msg);
			
		}
		catch (IOException e)
		{
			e.printStackTrace();
			FacesMessage error = new FacesMessage("The files were not uploaded!");
			FacesContext.getCurrentInstance().addMessage(null, error);
		}
		catch (InterWebException e)
		{
			e.printStackTrace();
			FacesMessage error = new FacesMessage("The files were not uploaded!");
			FacesContext.getCurrentInstance().addMessage(null, error);
		}
	}
	

	public List<SelectItem> getAllConnectors()
	    throws InterWebException
	{
		List<SelectItem> allConnectors = new ArrayList<SelectItem>();
		Engine engine = Utils.getEngine();
		IWPrincipal principal = Utils.getPrincipalBean().getPrincipal();
		for (ServiceConnector connector : engine.getConnectors())
		{
			if (engine.isConnectorRegistered(connector)
			    && engine.isUserAuthenticated(connector, principal))
			{
				SelectItem selectItem = new SelectItem(connector.getName());
				selectItem.setNoSelectionOption(true);
				allConnectors.add(selectItem);
			}
		}
		return allConnectors;
	}
	

	public List<String> getSelectedConnectors()
	{
		SessionBean sessionBean = (SessionBean) Utils.getManagedBean("sessionBean");
		return sessionBean.getSelectedConnectors();
	}
}

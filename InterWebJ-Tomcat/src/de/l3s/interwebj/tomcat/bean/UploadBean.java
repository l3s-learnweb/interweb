package de.l3s.interwebj.tomcat.bean;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.richfaces.event.FileUploadEvent;
import org.richfaces.model.UploadedFile;

import de.l3s.interwebj.core.InterWebException;
import de.l3s.interwebj.core.Parameters;
import de.l3s.interwebj.core.core.Engine;
import de.l3s.interwebj.core.core.Environment;
import de.l3s.interwebj.core.core.InterWebPrincipal;
import de.l3s.interwebj.core.core.ServiceConnector;
import de.l3s.interwebj.core.query.Query;
import de.l3s.interwebj.tomcat.webutil.FacesUtils;

/**
 * @author olex
 * 
 */
@Named
@ViewScoped
public class UploadBean implements Serializable
{
	private static final Logger log = LogManager.getLogger(UploadBean.class);
    private static final long serialVersionUID = -3906461569264684939L;

    private String selectedContentType;
    private List<String> selectedConnectors;
    private byte[] data;
    private String title;
    private String description;
    private String tags;
    private String text;
    private String fileName;
    private boolean publicAccess;

    public UploadBean()
    {
	publicAccess = true;
    }

    public List<SelectItem> getConnectors() throws InterWebException
    {
	List<SelectItem> connectors = new ArrayList<SelectItem>();
	Engine engine = Environment.getInstance().getEngine();
	InterWebPrincipal principal = FacesUtils.getSessionBean().getPrincipal();
	for(ServiceConnector connector : engine.getConnectors())
	{
	    if(connector.supportContentType(selectedContentType) && connector.isRegistered() && engine.isUserAuthenticated(connector, principal))
	    {
		SelectItem selectItem = new SelectItem(connector.getName());
		connectors.add(selectItem);
	    }
	}
	return connectors;
    }

    public List<SelectItem> getContentTypes() throws InterWebException
    {
	List<SelectItem> contentTypes = new ArrayList<SelectItem>();
	Engine engine = Environment.getInstance().getEngine();
	for(String contentType : engine.getContentTypes())
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
	return Query.CT_IMAGE.equals(selectedContentType) || Query.CT_VIDEO.equals(selectedContentType) || Query.CT_AUDIO.equals(selectedContentType);
    }

    public boolean isPublicAccess()
    {
	return publicAccess;
    }

    public void listener(FileUploadEvent event) throws Exception
    {
	UploadedFile uploadedFile = event.getUploadedFile();
	fileName = uploadedFile.getName();
	log.info("File to upload: " + uploadedFile.getName());
	log.info("Content type: " + uploadedFile.getContentType());
	data = uploadedFile.getData();
	log.info("Size: " + data.length);
    }

    public void save()
    {
    }

    public void setDescription(String description)
    {
	this.description = description;
    }

    public void setPublicAccess(boolean publicAccess)
    {
	this.publicAccess = publicAccess;
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

    public void upload() throws InterWebException
    {
	log.info("uploading binary data...");
	if(data != null && selectedConnectors != null)
	{
	    Engine engine = Environment.getInstance().getEngine();
	    InterWebPrincipal principal = FacesUtils.getSessionBean().getPrincipal();
	    Parameters params = new Parameters();
	    if(title != null)
	    {
		params.add(Parameters.TITLE, title);
	    }
	    if(description != null)
	    {
		params.add(Parameters.DESCRIPTION, description);
	    }
	    if(tags != null)
	    {
		params.add(Parameters.TAGS, tags);
	    }
	    String privacy = isPublicAccess() ? "0" : "1";
	    params.add(Parameters.PRIVACY, privacy);
	    params.add(Parameters.FILENAME, fileName);
	    engine.upload(data, principal, selectedConnectors, selectedContentType, params);
	    data = null;
	}
    }

    public void uploadText() throws InterWebException
    {
	log.info("text to upload: [" + text + "]");
	if(text != null && selectedConnectors != null)
	{
	    Engine engine = Environment.getInstance().getEngine();
	    InterWebPrincipal principal = FacesUtils.getSessionBean().getPrincipal();
	    Parameters params = new Parameters();
	    if(title != null)
	    {
		params.add(Parameters.TITLE, title);
	    }
	    if(description != null)
	    {
		params.add(Parameters.DESCRIPTION, description);
	    }
	    if(tags != null)
	    {
		params.add(Parameters.TAGS, tags);
	    }
	    String privacy = isPublicAccess() ? "0" : "1";
	    params.add(Parameters.PRIVACY, privacy);
	    engine.upload(text.getBytes(StandardCharsets.UTF_8), principal, selectedConnectors, selectedContentType, params);
	    text = null;
	}
    }
}

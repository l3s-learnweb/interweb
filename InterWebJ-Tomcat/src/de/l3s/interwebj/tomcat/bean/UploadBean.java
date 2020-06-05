package de.l3s.interwebj.tomcat.bean;

import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.http.Part;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.io.ByteStreams;

import de.l3s.interwebj.core.InterWebException;
import de.l3s.interwebj.core.Parameters;
import de.l3s.interwebj.core.connector.ServiceConnector;
import de.l3s.interwebj.core.core.Engine;
import de.l3s.interwebj.core.core.Environment;
import de.l3s.interwebj.core.core.InterWebPrincipal;
import de.l3s.interwebj.core.query.ContentType;
import de.l3s.interwebj.tomcat.webutil.FacesUtils;

/**
 * @author olex
 */
@Named
@ViewScoped
public class UploadBean implements Serializable {
    private static final Logger log = LogManager.getLogger(UploadBean.class);
    private static final long serialVersionUID = -3906461569264684939L;

    private ContentType selectedContentType;
    private List<String> selectedConnectors;
    private String title;
    private String description;
    private String tags;
    private String text;
    private boolean publicAccess;

    private transient Part uploadedFile;

    public UploadBean() {
        publicAccess = true;
    }

    public List<SelectItem> getConnectors() throws InterWebException {
        List<SelectItem> connectors = new ArrayList<>();
        Engine engine = Environment.getInstance().getEngine();
        InterWebPrincipal principal = FacesUtils.getSessionBean().getPrincipal();
        for (ServiceConnector connector : engine.getConnectors()) {
            if (connector.supportContentType(selectedContentType) && connector.isRegistered() && engine.isUserAuthenticated(connector, principal)) {
                SelectItem selectItem = new SelectItem(connector.getName());
                connectors.add(selectItem);
            }
        }
        return connectors;
    }

    public List<ContentType> getContentTypes() throws InterWebException {
        Engine engine = Environment.getInstance().getEngine();
        return engine.getContentTypes();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getSelectedConnectors() {
        return selectedConnectors;
    }

    public void setSelectedConnectors(List<String> selectedConnectors) {
        this.selectedConnectors = selectedConnectors;
    }

    public ContentType getSelectedContentType() {
        return selectedContentType;
    }

    public void setSelectedContentType(ContentType selectedContentType) {
        this.selectedContentType = selectedContentType;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isFileUpload() {
        return Arrays.asList(ContentType.image, ContentType.video, ContentType.audio).contains(selectedContentType);
    }

    public boolean isPublicAccess() {
        return publicAccess;
    }

    public void setPublicAccess(boolean publicAccess) {
        this.publicAccess = publicAccess;
    }

    public Part getUploadedFile() {
        return uploadedFile;
    }

    public void setUploadedFile(Part uploadedFile) {
        this.uploadedFile = uploadedFile;
    }

    public void upload() throws InterWebException, IOException {
        log.info("uploading binary data...");

        String fileName = uploadedFile.getName();
        byte[] data = ByteStreams.toByteArray(uploadedFile.getInputStream());

        if (selectedConnectors != null) {
            Engine engine = Environment.getInstance().getEngine();
            InterWebPrincipal principal = FacesUtils.getSessionBean().getPrincipal();
            Parameters params = new Parameters();
            if (title != null) {
                params.add(Parameters.TITLE, title);
            }
            if (description != null) {
                params.add(Parameters.DESCRIPTION, description);
            }
            if (tags != null) {
                params.add(Parameters.TAGS, tags);
            }
            String privacy = isPublicAccess() ? "0" : "1";
            params.add(Parameters.PRIVACY, privacy);
            params.add(Parameters.FILENAME, fileName);
            engine.upload(data, principal, selectedConnectors, selectedContentType, params);
        }
    }

    public void uploadText() throws InterWebException {
        log.info("text to upload: [" + text + "]");
        if (text != null && selectedConnectors != null) {
            Engine engine = Environment.getInstance().getEngine();
            InterWebPrincipal principal = FacesUtils.getSessionBean().getPrincipal();
            Parameters params = new Parameters();
            if (title != null) {
                params.add(Parameters.TITLE, title);
            }
            if (description != null) {
                params.add(Parameters.DESCRIPTION, description);
            }
            if (tags != null) {
                params.add(Parameters.TAGS, tags);
            }
            String privacy = isPublicAccess() ? "0" : "1";
            params.add(Parameters.PRIVACY, privacy);
            engine.upload(text.getBytes(StandardCharsets.UTF_8), principal, selectedConnectors, selectedContentType, params);
            text = null;
        }
    }
}

package l3s.facebook.listresponse.links;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


import l3s.facebook.object.link.Sharedlink;
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "data",
    "paging"
})
@XmlRootElement(name = "links")
public class Links {
	@XmlElement(required = true)
	protected List<Sharedlink> data;
	@XmlElement(required = true)
	protected Paging paging;
	public List<Sharedlink> getData() {
		if(data==null)
			return new ArrayList<Sharedlink>();
		return data;
	}
	public void setData(List<Sharedlink> data) {
		this.data = data;
	}
	public Paging getPaging() {
		return paging;
	}
	public void setPaging(Paging paging) {
		this.paging = paging;
	}
	
	

}

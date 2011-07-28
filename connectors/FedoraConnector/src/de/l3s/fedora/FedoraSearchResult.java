package de.l3s.fedora;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Vector;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.text.StrBuilder;


@XmlRootElement(name="results")
public class FedoraSearchResult {

	@XmlElement(name = "resource")
	Vector<FedoraResource>	resource=new Vector<FedoraResource>();
		

		public FedoraSearchResult()
		{
			
		}
		
		@Override
		public String toString()
		{
			StrBuilder sb = new StrBuilder();
			try
			{
				JAXBContext context = JAXBContext.newInstance(getClass());
				Marshaller m = context.createMarshaller();
				m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				m.marshal(this, baos);
				sb.append(baos.toString("UTF8"));
			}
			catch (JAXBException e)
			{
				e.printStackTrace();
			}
			catch (UnsupportedEncodingException e)
			{
				e.printStackTrace();
			}
			return sb.toString();
		}

		public Vector<FedoraResource> getResource() {
			return resource;
		}
}

package l3s.facebook.listresponse.profilefeed;

import java.util.ArrayList;

import l3s.facebook.listresponse.books.Paging;
import l3s.facebook.objects.video.Data;

public class Videosuploaded {
	
	public Videosuploaded() {
		// TODO Auto-generated constructor stub
	}
    
	protected ArrayList<Data> data;
	public ArrayList<Data> getData() {
		return data;
	}

	public void setData(ArrayList<Data> data) {
		this.data = data;
	}

	protected Paging paging;

	public Paging getPaging() {
		return paging;
	}

	public void setPaging(Paging paging) {
		this.paging = paging;
	}

	

}

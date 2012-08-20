package de.l3s.interwebj.query;

import java.util.TreeMap;

public class UserSocialNetworkResult {
	
	private String userid;
	private TreeMap<String, ContactFromSocialNetwork> socialnetwork;
	private long elapsedTime;
	private long createdTime;
	
	private long totalResultCount;

	public UserSocialNetworkResult(String userid) {
		super();
		this.userid = userid;
		socialnetwork= new TreeMap<String, ContactFromSocialNetwork>();
	}

	public void addSocialNetworkResult(UserSocialNetworkResult Result)
	{
		socialnetwork.putAll(Result.socialnetwork);
		totalResultCount = totalResultCount + Result.getTotalResultCount();
	}
	public String getUserid() {
		return userid;
	}

	public TreeMap<String, ContactFromSocialNetwork> getResultItems() {
		return socialnetwork;
	}

	public void setResultItems(TreeMap<String, ContactFromSocialNetwork> resultItems) {
		this.socialnetwork = resultItems;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public long getElapsedTime() {
		return elapsedTime;
	}

	

	public long getTotalResultCount() {
		return totalResultCount;
	}

	

	public long getCreatedTime() {
		return createdTime;
	}
	
	public TreeMap<String, ContactFromSocialNetwork> getSocialnetwork() {
		return socialnetwork;
	}

	public void setSocialnetwork(TreeMap<String, ContactFromSocialNetwork> socialnetwork) {
		this.socialnetwork = socialnetwork;
	}

	public void setElapsedTime(long elapsedTime) {
		this.elapsedTime = elapsedTime;
	}

	public void setCreatedTime(long createdTime) {
		this.createdTime = createdTime;
	}

	public void setTotalResultCount(long totalResultCount) {
		this.totalResultCount = totalResultCount;
	}




	
	

}

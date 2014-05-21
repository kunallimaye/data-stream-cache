/**
 * 
 */
package com.kunal.data.stream.cache;

import java.io.Serializable;

/**
 * @author kunallimaye
 * 
 */
public class Tweet implements Serializable {

	/**
    * 
    */
	private static final long serialVersionUID = 7058894008050185890L;

	private String username;
	private String searchCriteria;
	private String tweet;

	public Tweet() {
		super();
	}

	public Tweet(String username, String searchCriteria, String tweet) {
		super();
		this.username = username;
		this.searchCriteria = searchCriteria;
		this.tweet = tweet;
	}

	public void populate(String username, String searchCriteria, String tweet){
		this.username = username;
		this.searchCriteria = searchCriteria;
		this.tweet = tweet;
		
		return ;
	}
	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username
	 *            the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the searchCriteria
	 */
	public String getSearchCriteria() {
		return searchCriteria;
	}

	/**
	 * @param searchCriteria
	 *            the searchCriteria to set
	 */
	public void setSearchCriteria(String searchCriteria) {
		this.searchCriteria = searchCriteria;
	}

	/**
	 * @return the tweet
	 */
	public String getTweet() {
		return tweet;
	}

	/**
	 * @param tweet
	 *            the tweet to set
	 */
	public void setTweet(String tweet) {
		this.tweet = tweet;
	}

}

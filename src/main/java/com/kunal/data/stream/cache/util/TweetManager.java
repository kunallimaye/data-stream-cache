/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kunal.data.stream.cache.util;

import org.infinispan.commons.api.BasicCache;

import com.kunal.data.stream.cache.Tweet;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;

/**
 * Adds, retrieves, removes new cars from the cache. Also returns a list of cars
 * stored in the cache.
 * 
 * @author Martin Gencur
 * 
 */
@Stateless
@Path("/cache")
public class TweetManager {
	public static final String AUTHOR_TO_TWEETS_CACHE = "author-to-tweets-cache";
	public static final String AUTHOR_TO_TWEETS_KEY = "author";

	public static final String SEARCH_TO_TWEETS_CACHE = "search-to-tweets-cache";
	public static final String SEARCH_TO_TWEETS_KEY = "search-term";

	@Inject
	private CacheContainerProvider provider;

	private BasicCache<String, Object> authorToTweetCache;
	private BasicCache<String, Object> searchToTweetCache;

	public TweetManager() {
	}

	@GET
	@Produces("application/json")
	@Path("/test")
	public Tweet test() {
		Tweet t = new Tweet();
		t.setUsername("testingUserName");
		t.setSearchCriteria("testingSearchCriteria");
		t.setTweet("testingTweet");
		newTweet(t);

		// retrieve a cache
		searchToTweetCache = provider.getCacheContainer().getCache(
				SEARCH_TO_TWEETS_CACHE);
		// retrieve a list of number plates from the cache
		return ((List<Tweet>) searchToTweetCache.get(encode(t
				.getSearchCriteria()))).get(0);

	}

	@POST
	@Consumes("application/json")
	public void newTweet(Tweet tweet) {
		authorToTweetCache = provider.getCacheContainer().getCache(
				AUTHOR_TO_TWEETS_CACHE);
		List<Tweet> authorTweets = (List<Tweet>) authorToTweetCache
				.get(TweetManager.encode(tweet.getUsername()));
		if (authorTweets == null) {
			authorTweets = new LinkedList<Tweet>();
		}
		authorTweets.add(tweet);
		authorToTweetCache.put(TweetManager.encode(tweet.getUsername()),
				authorTweets);

		List<String> authors = getAuthors();
		if (authors == null)
			authors = new LinkedList<String>();
		if(!authors.contains(tweet.getUsername()))
			authors.add(tweet.getUsername());
		authorToTweetCache.put(AUTHOR_TO_TWEETS_KEY, authors);

		searchToTweetCache = provider.getCacheContainer().getCache(
				SEARCH_TO_TWEETS_CACHE);
		List<Tweet> searchTermTweets = (List<Tweet>) searchToTweetCache
				.get(TweetManager.encode(tweet.getSearchCriteria()));
		if (searchTermTweets == null) {
			searchTermTweets = new LinkedList<Tweet>();
		}
		searchTermTweets.add(tweet);
		searchToTweetCache.put(TweetManager.encode(tweet.getSearchCriteria()),
				searchTermTweets);

		List<String> searchTerms = getSearchTerms();
		if (searchTerms == null)
			searchTerms = new LinkedList<String>();
		if(!searchTerms.contains(tweet.getSearchCriteria()))
			searchTerms.add(tweet.getSearchCriteria());
		searchToTweetCache.put(SEARCH_TO_TWEETS_KEY, searchTerms);

		return;
	}

	@SuppressWarnings("unchecked")
	@GET
	@Produces("application/json")
	@Path("/search-terms")
	public List<String> getSearchTerms() {
		searchToTweetCache = provider.getCacheContainer().getCache(
				SEARCH_TO_TWEETS_CACHE);
		return (List<String>) searchToTweetCache.get(SEARCH_TO_TWEETS_KEY);
	}

	@SuppressWarnings("unchecked")
	@GET
	@Produces("application/json")
	@Path("/authors")
	public List<String> getAuthors() {
		authorToTweetCache = provider.getCacheContainer().getCache(
				AUTHOR_TO_TWEETS_CACHE);
		return (List<String>) authorToTweetCache.get(AUTHOR_TO_TWEETS_KEY);
	}

	@GET
	@Produces("application/json")
	@Path("/author/{author}")
	public List<Tweet> showTweetsForUser(@PathParam("author") String userName) {
		authorToTweetCache = provider.getCacheContainer().getCache(
				AUTHOR_TO_TWEETS_CACHE);
		return (List<Tweet>) authorToTweetCache.get(encode(userName));
	}

	@GET
	@Produces("application/json")
	@Path("/search-term/{searchTerm}")
	public List<Tweet> showTweetsForSearchTerms(
			@PathParam("searchTerm") String searchTerm) {
		// retrieve a cache
		searchToTweetCache = provider.getCacheContainer().getCache(
				SEARCH_TO_TWEETS_CACHE);
		// retrieve a list of number plates from the cache
		return (List<Tweet>) searchToTweetCache.get(encode(searchTerm));
	}

	public static String encode(String key) {
		try {
			return URLEncoder.encode(key, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public static String decode(String key) {
		try {
			return URLDecoder.decode(key, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
}

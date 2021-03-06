/*
 * QueryComposer.java
 * 16/08/2009
 * Twitter API Micro Edition
 * Copyright(c) Ernandes Mourao Junior (ernandes@gmail.com)
 * All rights reserved
 */
package com.twitterapime.search;

import java.util.Calendar;
import java.util.Date;

import com.twitterapime.rest.Timeline;

/**
 * <p>
 * This class is responsible for composing queries that are used to be sent to
 * Twitter Search API. This class provides methods for most parameters/operators
 * that are supported by Twitter Search API.
 * </p>
 * 
 * @author Ernandes Mourao Junior (ernandes@gmail.com)
 * @version 1.6
 * @since 1.0
 * @see Query
 * @see SearchDevice
 * @see Timeline
 */
public final class QueryComposer {
	/**
	 * <p>
	 * Constant that represents the "filter links" parameter.
	 * </p>
	 */
	static final String PM_FILTER_LINKS = "filter=links";

	/**
	 * <p>
	 * Constant that represents the "from" parameter.
	 * </p>
	 */
	static final String PM_FROM = "from=";

	/**
	 * <p>
	 * Constant that represents a "positive attitude" parameter.
	 * </p>
	 */
	static final String PM_POSITIVE_ATTITUDE = "tude[]=:)";

	/**
	 * <p>
	 * Constant that represents a "negative attitude" parameter.
	 * </p>
	 */
	static final String PM_NEGATIVE_ATTITUDE = "tude[]=:(";

	/**
	 * <p>
	 * Constant that represents a "asking a question" parameter.
	 * </p>
	 */
	static final String PM_ASKING_QUESTION = "tude[]=?";

	/**
	 * <p>
	 * Constant that represents the "contain all" parameter.
	 * </p>
	 */
	static final String PM_CONTAIN_ALL = "ands=";

	/**
	 * <p>
	 * Constant that represents the "contain exact" parameter.
	 * </p>
	 */
	static final String PM_CONTAIN_EXACT = "phrase=";

	/**
	 * <p>
	 * Constant that represents the "contain any" parameter.
	 * </p>
	 */
	static final String PM_CONTAIN_ANY = "ors=";

	/**
	 * <p>
	 * Constant that represents the "contain none" parameter.
	 * </p>
	 */
	static final String PM_CONTAIN_NONE = "nots=";

	/**
	 * <p>
	 * Constant that represents the "hashtag" parameter.
	 * </p>
	 */
	static final String PM_CONTAIN_HASHTAG = "tag=";

	/**
	 * <p>
	 * Constant that represents the "reference" parameter.
	 * </p>
	 */
	static final String PM_REFERENCE = "ref=";

	/**
	 * <p>
	 * Constant that represents the "since" parameter.
	 * </p>
	 */
	static final String PM_SINCE = "since=";

	/**
	 * <p>
	 * Constant that represents the "source" parameter.
	 * </p>
	 */
	static final String PM_SOURCE = "source=";

	/**
	 * <p>
	 * Constant that represents the "to" parameter.
	 * </p>
	 */
	static final String PM_TO = "to=";

	/**
	 * <p>
	 * Constant that represents the "until" parameter.
	 * </p>
	 */
	static final String PM_UNTIL = "until=";

	/**
	 * <p>
	 * Constant that represents the "lang" parameter.
	 * </p>
	 */
	static final String PM_LANG = "lang=";

	/**
	 * <p>
	 * Constant that represents the "rpp" parameter.
	 * </p>
	 */
	static final String PM_RPP = "rpp=";

	/**
	 * <p>
	 * Constant that represents the "page" parameter.
	 * </p>
	 */
	static final String PM_PAGE = "page=";

	/**
	 * <p>
	 * Constant that represents the "since_id" parameter.
	 * </p>
	 */
	static final String PM_SINCE_ID = "since_id=";

	/**
	 * <p>
	 * Constant that represents the "geocode" parameter.
	 * </p>
	 */
	static final String PM_GEOCODE = "geocode=";

	/**
	 * <p>
	 * Constant that represents the "count" parameter.
	 * </p>
	 */
	static final String PM_COUNT = "count=";

	/**
	 * <p>
	 * Constant that represents the "max_id" parameter.
	 * </p>
	 */
	static final String PM_MAX_ID = "max_id=";
	
	/**
	 * <p>
	 * Constant that represents the "date" parameter.
	 * </p>
	 */
	static final String PM_DATE = "date=";
	
	/**
	 * <p>
	 * Constant that represents the "exclude=hashtags" parameter.
	 * </p>
	 */
	static final String PM_EXCLUDE_HASHTAGS = "exclude=hashtags";

	/**
	 * <p>
	 * Constant that represents the "include_entities=true" parameter.
	 * </p>
	 */
	static final String PM_INCLUDE_ENTITIES = "include_entities=true";
	
	/**
	 * <p>
	 * Constant that represents the "per_page" parameter.
	 * </p>
	 */
	static final String PM_PER_PAGE = "per_page=";

	/**
	 * <p>
	 * Constant that represents the "q" parameter.
	 * </p>
	 */
	static final String PM_QUERY = "q=";

	/**
	 * <p>
	 * Constant that represents the "cursor" parameter.
	 * </p>
	 */
	static final String PM_CURSOR = "cursor=";

	/**
	 * <p>
	 * Constant that represents the "user_id" parameter.
	 * </p>
	 */
	static final String PM_USER_ID = "user_id=";

	/**
	 * <p>
	 * Constant that represents the "screen_name" parameter.
	 * </p>
	 */
	static final String PM_SCREEN_NAME = "screen_name=";

	/**
	 * <p>
	 * Constant that represents the "trim_user=true" parameter.
	 * </p>
	 */
	static final String PM_TRIM_USER = "trim_user=true";
	
	/**
	 * <p>
	 * Constant that represents the "include_rts=true" parameter.
	 * </p>
	 */
	static final String PM_INCLUDE_RTS = "include_rts=true";

	/**
	 * <p>
	 * Constant that represents the "skip_status=true" parameter.
	 * </p>
	 */
	static final String PM_SKIP_STATUS = "skip_status=true";

	/**
	 * <p>
	 * Append a query to another one.
	 * </p>
	 * @param q1 Query 1.
	 * @param q2 Query 2.
	 * @return A new query object with the content from both queries
	 *         concatenated.
	 * @throws IllegalArgumentException If q1/q2 is null.
	 */
	public static Query append(Query q1, Query q2) {
		if (q1 == null || q2 == null) {
			throw new IllegalArgumentException("Q1/Q2 must not be null.");
		}
		//
		return new Query(q1.toString() + '&' + q2.toString());
	}
	
	/**
	 * <p>
	 * Create a query to perform a search on Twitter based on a given query.
	 * </p>
	 * @param query Query.
	 * @return A new query.
	 */
	public static Query query(String query) {
		return new Query(PM_QUERY + query);
	}

	/**
	 * <p>
	 * Create a query to search for tweets that contain all the given words. To
	 * specify more than one word, the words must be separated by a white space,
	 * e.g., "java sun microsystems".
	 * </p>
	 * @param words The words.
	 * @return A new query.
	 */
	public static Query containAll(String words) {
		return new Query(PM_CONTAIN_ALL + words);
	}
	
	/**
	 * <p>
	 * Create a query to search for tweets that exactly match the given phrase,
	 * e.g., "The sky is blue.".
	 * </p>
	 * @param phrase The phrase.
	 * @return A new query.
	 */
	public static Query containExact(String phrase) {
		return new Query(PM_CONTAIN_EXACT + phrase);
	}

	/**
	 * <p>
	 * Create a query to search for tweets that contain at least one of the
	 * given words. To specify more than one word, the words must be separated
	 * by a white space, e.g., "java .net linux".
	 * </p>
	 * @param words The words.
	 * @return A new query.
	 */
	public static Query containAny(String words) {
		return new Query(PM_CONTAIN_ANY + words);
	}
	
	/**
	 * <p>
	 * Create a query to search for tweets that do not contain none of the
	 * given words. To specify more than one word, the words must be separated
	 * by a white space, e.g., ".net microsoft".
	 * </p>
	 * @param words The words.
	 * @return A new query.
	 */
	public static Query containNone(String words) {
		return new Query(PM_CONTAIN_NONE + words);
	}

	/**
	 * <p>
	 * Create a query to search for tweets that contain the given hashtag, e.g.,
	 * "twitter".
	 * </p>
	 * @param tag The tag.
	 * @return A new query.
	 */
	public static Query containHashtag(String tag) {
		return new Query(PM_CONTAIN_HASHTAG + tag);
	}

	/**
	 * <p>
	 * Create a query to search for tweets that were sent by a given person,
	 * e.g., "twitteruser".
	 * </p>
	 * @param person The person.
	 * @return A new query.
	 */
	public static Query from(String person) {
		return new Query(PM_FROM + person);
	}

	/**
	 * <p>
	 * Create a query to search for tweets that reference a given person, e.g.,
	 * "twitteruser".
	 * </p>
	 * @param person The person.
	 * @return A new query.
	 */
	public static Query reference(String person) {
		return new Query(PM_REFERENCE + person);
	}

	/**
	 * <p>
	 * Create a query to search for tweets that were sent to a given person,
	 * e.g., "twitteruser".
	 * </p>
	 * @param person The person.
	 * @return A new query.
	 */
	public static Query to(String person) {
		return new Query(PM_TO + person);
	}

	/**
	 * <p>
	 * Create a query to search for tweets that were sent since a given date,
	 * e.g., "01/01/2009".
	 * </p>
	 * <p>
	 * This query must be appended to another one, otherwise Twitter Search API
	 * may return an error. 
	 * <p/>
	 * @param date The date.
	 * @return A new query.
	 * @throws IllegalArgumentException If date is null.
	 */
	public static Query since(Date date) {
		if (date == null) {
			throw new IllegalArgumentException("Date must not be null.");
		}
		//
		return new Query(PM_SINCE + convertDate(date));
	}

	/**
	 * <p>
	 * Create a query to search for tweets that were sent until a given date,
	 * e.g., "01/10/2009".
	 * </p>
	 * <p>
	 * This query must be appended to another one, otherwise Twitter Search API
	 * may return an error. 
	 * <p/>
	 * @param date The date.
	 * @return A new query.
	 * @throws IllegalArgumentException If date is null.
	 */
	public static Query until(Date date) {
		if (date == null) {
			throw new IllegalArgumentException("Date must not be null.");
		}
		//
		return new Query(PM_UNTIL + convertDate(date));
	}

	/**
	 * <p>
	 * Create a query to search for tweets that are greater than a given ID,
	 * e.g., "123549789".
	 * </p>
	 * <p>
	 * This query must be appended to another one, otherwise Twitter Search API
	 * may return an error. 
	 * <p/>
	 * @param id The ID.
	 * @return A new query.
	 */
	public static Query sinceID(String id) {
		return new Query(PM_SINCE_ID + id);
	}
	
	/**
	 * <p>
	 * Create a query to search for tweets that are lesser than or equal to a
	 * given ID, e.g., "123549789".
	 * </p>
	 * @param id The ID.
	 * @return A new query.
	 */
	public static Query maxID(String id) {
		return new Query(PM_MAX_ID + id);
	}

	/**
	 * <p>
	 * Create a query to search for tweets that are from given language, e.g.,
	 * "en".
	 * </p>
	 * <p>
	 * This query must be appended to another one, otherwise Twitter Search API
	 * may return an error. 
	 * <p/>
	 * @param lang The language code.
	 * @return A new query.
	 */
	public static Query lang(String lang) {
		return new Query(PM_LANG + lang);
	}

	/**
	 * <p>
	 * Create a query to search for tweets that were sent from a given source
	 * application, e.g., "web".
	 * </p>
	 * <p>
	 * This query must be appended to another one, otherwise Twitter Search API
	 * may return an error. 
	 * <p/>
	 * @param appName The application name.
	 * @return A new query.
	 */
	public static Query source(String appName) {
		return new Query(PM_SOURCE + appName);
	}

	/**
	 * <p>
	 * Create a query to define the tweet count to be returned.
	 * </p>
	 * <p>
	 * This query must be appended to another one, otherwise Twitter Search API
	 * may return an error. 
	 * <p/>
	 * @param count The count.
	 * @return A new query.
	 */
	public static Query resultCount(int count) {
		return new Query(PM_RPP + count);
	}

	/**
	 * <p>
	 * Create a query to define the tweet count to be returned.
	 * </p>
	 * <p>
	 * This query must be used only with REST API search methods. For Search API
	 * search methods, use {@link QueryComposer#resultCount(int)} instead.
	 * </p>
	 * @param count The count.
	 * @return A new query.
	 */
	public static Query count(int count) {
		return new Query(PM_COUNT + count);
	}
	
	/**
	 * <p>
	 * Create a query to define the number of tweets page to be returned.
	 * </p>
	 * <p>
	 * This query must be appended to another one, otherwise Twitter Search API
	 * may return an error. 
	 * <p/>
	 * @param number The page number.
	 * @return A new query.
	 */
	public static Query page(int number) {
		return new Query(PM_PAGE + number);
	}

	/**
	 * <p>
	 * Create a query to paginate the results to be returned.
	 * </p>
	 * <p>
	 * This query must be appended to another one, otherwise Twitter Search API
	 * may return an error. 
	 * <p/>
	 * @param resultCount The result count.
	 * @param pageNumber The page number.
	 * @return A new query.
	 */
	public static Query paginate(int resultCount, int pageNumber) {
		return append(resultCount(resultCount), page(pageNumber));
	}

	/**
	 * <p>
	 * Create a query to search for tweets that were sent from a user that was
	 * located within a given radius of the given latitude/longitude.
	 * e.g., "40.757929,-73.985506,25km".
	 * </p>
	 * @param lat The latitude.
	 * @param lon The longitude.
	 * @param rad The radius.
	 * @param unit The radius unit ("mi" or "km").
	 * @return A new query.
	 */
	public static Query geocode(String lat, String lon, int rad, String unit) {
		return new Query(PM_GEOCODE + lat + ',' + lon + ',' + rad + unit);
	}

	/**
	 * <p>
	 * Create a query to search for tweets that contain a positive attitude
	 * ":)".
	 * </p>
	 * @return A new query.
	 */
	public static Query positiveAttitude() {
		return new Query(PM_POSITIVE_ATTITUDE);
	}

	/**
	 * <p>
	 * Create a query to search for tweets that contain a negative attitude
	 * ":(".
	 * </p>
	 * @return A new query.
	 */
	public static Query negativeAttitude() {
		return new Query(PM_NEGATIVE_ATTITUDE);
	}

	/**
	 * <p>
	 * Create a query to search for tweets that contain link(s).
	 * </p>
	 * <p>
	 * This query must be appended to another one, otherwise Twitter Search API
	 * may return an error. 
	 * <p/>
	 * @return A new query.
	 */
	public static Query containLink() {
		return new Query(PM_FILTER_LINKS);
	}

	/**
	 * <p>
	 * Create a query to search for tweets that contain a question "?".
	 * </p>
	 * @return A new query.
	 */
	public static Query containQuestion() {
		return new Query(PM_ASKING_QUESTION);
	}
	
	/**
	 * <p>
	 * Create a query to search for trend topics from a given date, e.g., 
	 * "01/01/2009".
	 * </p>
	 * @param date The date.
	 * @return A new query.
	 * @throws IllegalArgumentException If date is null.
	 */
	public static Query date(Date date) {
		if (date == null) {
			throw new IllegalArgumentException("Date must not be null.");
		}
		//
		return new Query(PM_DATE + convertDate(date));
	}
	
	/**
	 * <p>
	 * Create a query to flag a trend topics search to remove any occurrence of 
	 * hashtags from the result.
	 * </p>
	 * @return A new query.
	 */
	public static Query excludeHashtags() {
		return new Query(PM_EXCLUDE_HASHTAGS);
	}

	/**
	 * <p>
	 * Create a query to flag a timeline to return tweet entities data.
	 * </p>
	 * @return A new query.
	 */
	public static Query includeEntities() {
		return new Query(PM_INCLUDE_ENTITIES);
	}
	
	/**
	 * <p>
	 * Create a query to define the tweet count to be returned per page.
	 * </p>
	 * @param count The count.
	 * @return A new query.
	 */
	public static Query perPage(int count) {
		return new Query(PM_PER_PAGE + count);
	}

	/**
	 * <p>
	 * Create a query to define the page's index in the cursor.
	 * </p>
	 * @param index Cursor index.
	 * @return A new query.
	 */
	public static Query cursor(long index) {
		return new Query(PM_CURSOR + index);
	}

	/**
	 * <p>
	 * Create a query to define the user's id.
	 * </p>
	 * @param id User ID.
	 * @return A new query.
	 */
	public static Query userID(String id) {
		return new Query(PM_USER_ID + id);
	}

	/**
	 * <p>
	 * Create a query to define the user's screen name (username).
	 * </p>
	 * @param name User's screen name (username).
	 * @return A new query.
	 */
	public static Query screenName(String name) {
		return new Query(PM_SCREEN_NAME + name);
	}
	
	/**
	 * <p>
	 * Create a query to define whether the user's information must be trimmed.
	 * </p>
	 * @return A new query.
	 */
	public static Query trimUser() {
		return new Query(PM_TRIM_USER);
	}

	/**
	 * <p>
	 * Create a query to define whether the retweets must be included.
	 * </p>
	 * @return A new query.
	 */
	public static Query includeRetweets() {
		return new Query(PM_INCLUDE_RTS);
	}

	/**
	 * <p>
	 * Create a query to define whether the statuses must not be included.
	 * </p>
	 * @return A new query.
	 */
	public static Query skipStatus() {
		return new Query(PM_SKIP_STATUS);
	}
	
	/**
	 * <p>
	 * Create a query to define the users' screen name (username).
	 * </p>
	 * @param names Users' screen name (username).
	 * @return A new query.
	 * @throws IllegalArgumentException If names is null.
	 */
	public static Query screenNames(String[] names) {
		if (names == null) {
			throw new IllegalArgumentException("Names must not be null.");
		}
		//
		StringBuffer strNames = new StringBuffer();
		//
		for (int i = 0; i < names.length; i++) {
			if (i > 0) {
				strNames.append(',');	
			}
			strNames.append(names[i]);
		}
		//
		return new Query(PM_SCREEN_NAME + strNames.toString());
	}
	
	/**
	 * <p>
	 * Create a query to define the users' id.
	 * </p>
	 * @param ids User IDs.
	 * @return A new query.
	 * @throws IllegalArgumentException If ids is null.
	 */
	public static Query userIDs(String[] ids) {
		if (ids == null) {
			throw new IllegalArgumentException("Ids must not be null.");
		}
		//
		StringBuffer strIds = new StringBuffer();
		//
		for (int i = 0; i < ids.length; i++) {
			if (i > 0) {
				strIds.append(',');	
			}
			strIds.append(ids[i]);
		}
		//
		return new Query(PM_USER_ID + strIds.toString());
	}

	/**
	 * <p>
	 * Convert a given date object to a string date, e.g, "2009-10-20".
	 * </p>
	 * @param date The date.
	 * @return The date in string format.
	 */
	private static String convertDate(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		//
		return c.get(Calendar.YEAR) + "-" +	(c.get(Calendar.MONTH) + 1) + "-" +
			c.get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * <p>
	 * Private constructor to avoid object instantiation.
	 * </p>
	 */
	private QueryComposer() {
	}
}
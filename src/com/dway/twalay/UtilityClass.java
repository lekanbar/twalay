package com.dway.twalay;

import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.TimeZone;
import java.util.Vector;

import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;

import com.twitterapime.xauth.Token;

public class UtilityClass {
	
	final static long KEY = 0x7824c240795aa705L;
	
	static Vector HomeTweets, MentionsTweets, FavoritesTweets, 
	              DMReceivedTweets, DMSentTweets, DMFriendsTweets;
	
	public static Vector getHomeTweets(){
		if(HomeTweets == null)
			HomeTweets = new Vector();
		return HomeTweets;
	}
	
	public static Vector getMentionsTweets(){
		if(MentionsTweets == null)
			MentionsTweets = new Vector();
		return MentionsTweets;
	}
	
	public static Vector getFavoritesTweets(){
		if(FavoritesTweets == null)
			FavoritesTweets = new Vector();
		return FavoritesTweets;
	}
	
	public static Vector getDMReceivedTweets(){
		if(DMReceivedTweets == null)
			DMReceivedTweets = new Vector();
		return DMReceivedTweets;
	}
	public static void setDMReceivedTweets(Vector dnew){
		DMReceivedTweets = dnew;
	}
	
	public static Vector getDMSentTweets(){
		if(DMSentTweets == null)
			DMSentTweets = new Vector();
		return DMSentTweets;
	}
	public static void setDMSentTweets(Vector dnew){
		DMSentTweets = dnew;
	}
	
	public static Vector getDMFriendsTweets(){
		if(DMFriendsTweets == null)
			DMFriendsTweets = new Vector();
		return DMFriendsTweets;
	}
	public static void setDMFriendsTweets(Vector dnew){
		DMFriendsTweets = dnew;
	}
	
	public static String getConsumerKey(){
		return "ZbxG8o1s59gACYKndqqdw";
	}
	
	public static String getConsumerSecret(){
		return "gxslNLGgtY4B24tmIDhqNhwj2qufo1VXe8iieuJ0";
	}
	
	public static String getCallBackUrl(){
		return "http://twalay.iolines.com/twalay.html";
	}
	
	public static Token getUserToken(){
		PersistentObject pob = PersistentStore.getPersistentObject(KEY);
		Hashtable db = (Hashtable)pob.getContents();
		String[] userToken = (String[])db.get("tokenTable");
		
		//Construct token
		Token token = new Token(userToken[0], userToken[1], userToken[2], userToken[3]);
		
		return token;
	}
	
	public static void signOut(){
		PersistentObject pob = PersistentStore.getPersistentObject(KEY);
		Hashtable db = (Hashtable)pob.getContents();
		db.remove("tokenTable");		
		pob.commit();
	}
	
	public static String decodeHtml(String theStr){
		theStr = replaceAll(theStr, "&nbsp;", " ");
		theStr = replaceAll(theStr, "&quot;", "\"");
		theStr = replaceAll(theStr, "&apos;", "'");
		theStr = replaceAll(theStr, "&lt;", "<");
		theStr = replaceAll(theStr, "&gt;", ">");
		theStr = replaceAll(theStr, "&amp;", "&");
		theStr = replaceAll(theStr, "&trade;", "™");
		theStr = replaceAll(theStr, "&copy;", "©");
		theStr = replaceAll(theStr, "&reg;", "®");
		return theStr;
	}
	
	public static String cleanString(String theStr){
		theStr = replaceAll(theStr, " ", "");
		theStr = replaceAll(theStr, ";", "");
		theStr = replaceAll(theStr, ":", "");
		theStr = replaceAll(theStr, "\"", "");
		theStr = replaceAll(theStr, ">", "");
		theStr = replaceAll(theStr, "-", "");
		theStr = replaceAll(theStr, "'", "");
		theStr = replaceAll(theStr, "/", "");
		theStr = replaceAll(theStr, "\\", "");
		theStr = replaceAll(theStr, "<", "");
		theStr = replaceAll(theStr, "(", ""); theStr = replaceAll(theStr, ")", "");
		theStr = replaceAll(theStr, "[", ""); theStr = replaceAll(theStr, "]", "");
		theStr = replaceAll(theStr, "{", ""); theStr = replaceAll(theStr, "}", "");
		theStr = replaceAll(theStr, "!", ""); theStr = replaceAll(theStr, "|", "");
		theStr = replaceAll(theStr, "*", ""); theStr = replaceAll(theStr, "^", "");
		return theStr;
	}
	
	public static String cleanUrlString(String theStr){
		theStr = replaceAll(theStr, " ", "");
		theStr = replaceAll(theStr, ";", "");
		if(theStr.endsWith(":")) theStr = theStr.substring(0, theStr.length() - 2);
		theStr = replaceAll(theStr, "\"", "");
		theStr = replaceAll(theStr, ">", "");
		theStr = replaceAll(theStr, "'", "");
		theStr = replaceAll(theStr, "\\", "");
		theStr = replaceAll(theStr, "<", "");
		theStr = replaceAll(theStr, "(", ""); theStr = replaceAll(theStr, ")", "");
		theStr = replaceAll(theStr, "[", ""); theStr = replaceAll(theStr, "]", "");
		theStr = replaceAll(theStr, "{", ""); theStr = replaceAll(theStr, "}", "");
		theStr = replaceAll(theStr, "!", ""); theStr = replaceAll(theStr, "|", "");
		theStr = replaceAll(theStr, "%", ""); theStr = replaceAll(theStr, "*", "");
		theStr = replaceAll(theStr, "^", ""); 
		return theStr;
	}
	
	public static boolean contains(String fullString, String searchedString) {
        try {
            if (fullString.toLowerCase().indexOf(searchedString.toLowerCase()) != -1)
                return true;
        } catch (Exception e) {
            return false;
        }
        
        return false;
	}
	
	public static String[] split(String strString, String strDelimiter)
	{
		int iOccurrences = 0;
		int iIndexOfInnerString = 0;
		int iIndexOfDelimiter = 0;
		int iCounter = 0;

		// Check for null input strings.
		if (strString == null)
		{
			throw new NullPointerException("Input string cannot be null.");
		}
		// Check for null or empty delimiter
		// strings.
		if (strDelimiter.length() <= 0 || strDelimiter == null)
		{
			throw new NullPointerException("Delimeter cannot be null or empty.");
		}

		// If strString begins with delimiter
		// then remove it in
		// order
		// to comply with the desired format.

		if (strString.startsWith(strDelimiter))
		{
			strString = strString.substring(strDelimiter.length());
		}

		// If strString does not end with the
		// delimiter then add it
		// to the string in order to comply with
		// the desired format.
		if (!strString.endsWith(strDelimiter))
		{
			strString += strDelimiter;
		}

		// Count occurrences of the delimiter in
		// the string.
		// Occurrences should be the same amount
		// of inner strings.
		while((iIndexOfDelimiter= strString.indexOf(strDelimiter,iIndexOfInnerString))!=-1)
		{
			iOccurrences += 1;
			iIndexOfInnerString = iIndexOfDelimiter + strDelimiter.length();
		}

		// Declare the array with the correct
		// size.
		String[] strArray = new String[iOccurrences];

		// Reset the indices.
		iIndexOfInnerString = 0;
		iIndexOfDelimiter = 0;

		// Walk across the string again and this
		// time add the
		// strings to the array.
		while((iIndexOfDelimiter= strString.indexOf(strDelimiter,iIndexOfInnerString))!=-1)
		{

			// Add string to
			// array.
			strArray[iCounter] = strString.substring(iIndexOfInnerString, iIndexOfDelimiter);

			// Increment the
			// index to the next
			// character after
			// the next
			// delimiter.
			iIndexOfInnerString = iIndexOfDelimiter + strDelimiter.length();

			// Inc the counter.
			iCounter += 1;
		}
            return strArray;
	}
	
	public static String replaceAll(String source, String pattern, String replacement)
	{    

	    //If source is null then Stop
	    //and retutn empty String.
	    if (source == null)
	    {
	        return "";
	    }

	    StringBuffer sb = new StringBuffer();
	    //Intialize Index to -1
	    //to check agaist it later 
	    int idx = -1;
	    //Search source from 0 to first occurrence of pattern
	    //Set Idx equal to index at which pattern is found.

	    String workingSource = source;
	    
	    //Iterate for the Pattern till idx is not be -1.
	    while ((idx = workingSource.indexOf(pattern)) != -1)
	    {
	        //append all the string in source till the pattern starts.
	        sb.append(workingSource.substring(0, idx));
	        //append replacement of the pattern.
	        sb.append(replacement);
	        //Append remaining string to the String Buffer.
	        sb.append(workingSource.substring(idx + pattern.length()));
	        
	        //Store the updated String and check again.
	        workingSource = sb.toString();
	        
	        //Reset the StringBuffer.
	        sb.delete(0, sb.length());
	    }

	    return workingSource;
	}
	
	public static String getTweetPublishDate(String tweetdate){
		String ret = "";
		Date d = new Date(Long.parseLong(tweetdate));
		Date devicetime = new Date();
		long[] array = getTimeDifference(d, devicetime);
		
		if(array[0] > 0) ret = array[0] + "d";
		else if(array[1] > 0) ret = array[1] + "h";
		else if(array[2] > 0) ret = array[2] + "m";
		else if(array[3] > 0) ret = array[3] + "s";
		else ret = "now";
		
		return ret;
	}
	
    public static long[] getTimeDifference(Date d1, Date d2) {
	        long[] result = new long[5];
	        Calendar cal = Calendar.getInstance();
	        cal.setTimeZone(TimeZone.getTimeZone("UTC"));
	        cal.setTime(d1);
	        
	        long t1 = cal.getTime().getTime();
	        cal.setTime(d2);

	        long diff = Math.abs(cal.getTime().getTime() - t1);
	        final int ONE_DAY = 1000 * 60 * 60 * 24;
	        final int ONE_HOUR = ONE_DAY / 24;
	        final int ONE_MINUTE = ONE_HOUR / 60;
	        final int ONE_SECOND = ONE_MINUTE / 60;

	        long d = diff / ONE_DAY;
	        diff %= ONE_DAY;

	        long h = diff / ONE_HOUR;
	        diff %= ONE_HOUR;

	        long m = diff / ONE_MINUTE;
	        diff %= ONE_MINUTE;

	        long s = diff / ONE_SECOND;
	        long ms = diff % ONE_SECOND;
	        result[0] = d;
	        result[1] = h;
	        result[2] = m;
	        result[3] = s;
	        result[4] = ms;

	        return result;
	}
}

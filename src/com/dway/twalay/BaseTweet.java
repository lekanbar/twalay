package com.dway.twalay;

import java.util.Vector;

import com.twitterapime.model.MetadataSet;
import com.twitterapime.rest.UserAccount;
import com.twitterapime.search.Tweet;

import me.regexp.RE;
import net.rim.device.api.system.EncodedImage;

public class BaseTweet {

	private String TweetID;
	private String TweetText;
	private EncodedImage TweetImage;
	private String TweetAuthorName;
	private String TweetAuthorHandle;
	private EncodedImage TweetAuthorImage;
	private String TweetAuthorImageUrl;
	private String TweetDate;
	private String TweetComment; //show if replied, if retweeted, etc
	private boolean IsFavorite; //client has favorited this tweet
	private boolean IsRetweet;
	private boolean IsReply;
	private boolean IsUnread;
	private String RetweetAuthor; //who retweeted this tweet
	private String ReplyAuthor; //who is being replied
	private String TwitterClient;
	private boolean IsDownloaded; 
	private UserAccount ua;
	private Tweet MainTweet;
	private String RawDate;

	public BaseTweet(String tempHandle)
	{
		TweetAuthorHandle = tempHandle;		
	}
	
	public BaseTweet(String text, String authorHandle, EncodedImage authorImage, String time)
	{
		TweetText = text;		
		TweetAuthorHandle = authorHandle;
		TweetAuthorImage = authorImage;
		TweetDate = time;
	}
	
	public BaseTweet(Tweet tweet)
	{
		MainTweet = tweet;
		Tweet rt = tweet.getRepostedTweet();
		
		if(rt == null){
			ua = tweet.getUserAccount();			
			TweetID = tweet.getString(MetadataSet.TWEET_ID);
			TweetText = UtilityClass.decodeHtml(tweet.getString(MetadataSet.TWEET_CONTENT));
			TweetAuthorHandle = tweet.getUserAccount().getString(MetadataSet.USERACCOUNT_USER_NAME);
			TweetAuthorName = tweet.getUserAccount().getString(MetadataSet.USERACCOUNT_NAME);
			TweetAuthorImageUrl = tweet.getUserAccount().getString(MetadataSet.USERACCOUNT_PICTURE_URI_MINI);			
			TweetDate = UtilityClass.getTweetPublishDate(tweet.getString(MetadataSet.TWEET_PUBLISH_DATE));
			RawDate = tweet.getString(MetadataSet.TWEET_PUBLISH_DATE);
			IsUnread = true;
			IsDownloaded = false;
			
			String fav = tweet.getString(MetadataSet.TWEET_FAVOURITE);
			if(fav != null)
				IsFavorite = (fav.equals("true") ? true : false);
			
			IsRetweet = false;
			if(tweet.getString(MetadataSet.TWEET_IN_REPLY_TO_TWEET_ID) != null){
				IsReply = true;
				Vector v = checkForUserNames(TweetText);
				if(v.size() > 0){
					ReplyAuthor = v.elementAt(0).toString();
					TweetComment = "replied"; 
					for(int i = 0;i < v.size();i++){
						TweetComment += " " + v.elementAt(i).toString();
					}
				}
			}
			TwitterClient = tweet.getString(MetadataSet.TWEET_SOURCE);
		}
		else{
			ua = rt.getUserAccount();
			TweetID = tweet.getString(MetadataSet.TWEET_ID);
			TweetText = UtilityClass.decodeHtml(rt.getString(MetadataSet.TWEET_CONTENT));
			TweetAuthorHandle = rt.getUserAccount().getString(MetadataSet.USERACCOUNT_USER_NAME);
			TweetAuthorName = rt.getUserAccount().getString(MetadataSet.USERACCOUNT_NAME);
			TweetAuthorImageUrl = rt.getUserAccount().getString(MetadataSet.USERACCOUNT_PICTURE_URI_MINI);
			TweetDate = UtilityClass.getTweetPublishDate(rt.getString(MetadataSet.TWEET_PUBLISH_DATE));
			RawDate = rt.getString(MetadataSet.TWEET_PUBLISH_DATE);
			IsUnread = true;
			IsDownloaded = false;
			
			String fav = tweet.getString(MetadataSet.TWEET_FAVOURITE);
			if(fav != null)
				IsFavorite = (fav.equals("true") ? true : false);
			
			IsRetweet = true;
			RetweetAuthor = tweet.getUserAccount().getString(MetadataSet.USERACCOUNT_USER_NAME);
			TweetComment = "Retweeted by @" + RetweetAuthor;	
			if(rt.getString(MetadataSet.TWEET_IN_REPLY_TO_TWEET_ID) != null){
				IsReply = true;
				TweetComment = "RT by @" + RetweetAuthor;
				Vector v = checkForUserNames(TweetText);
				if(v.size() > 0){
					ReplyAuthor = v.elementAt(0).toString();
					TweetComment += " replied"; 
					for(int i = 0;i < v.size();i++){
						TweetComment += " " + v.elementAt(i).toString();
					}
				}
			}
			TwitterClient = rt.getString(MetadataSet.TWEET_SOURCE);
		}
	}
	
	public Vector checkForUserNames(String content){
		  String pattern = "[@](\\w+)[^\\W]";
		  String[] users = null;
		  Vector v = new Vector();
		  
		  RE regex = new RE(pattern);
		  if(regex.match(content))
			  users = regex.grep(UtilityClass.split(content, " "));
		  
		  if(users != null)
			  for(int i = 0; i < users.length; i++){
				  String stry = UtilityClass.cleanString(users[i]);
				  if(!v.contains(stry) || !stry.startsWith(TweetAuthorHandle))
					  v.addElement(stry);
			  }
		  
		return v;
	}
	
	public Tweet getMainTweet()
	{
		return MainTweet;
	}
	public void setMainTweet(Tweet value)
	{
		MainTweet = value;
	}
	
	public UserAccount getUserAccount()
	{
		return ua;
	}
	public void setUserAccount(UserAccount value)
	{
		ua = value;
	}
	
	public String getTweetID()
	{
		return TweetID;
	}
	public void setTweetID(String value)
	{
		TweetID = value;
	}
	
	public String getTweetText()
	{
		return TweetText;
	}
	public void setTweetText(String value)
	{
		TweetText = value;
	}
	
	public EncodedImage getTweetImage()
	{
		return TweetImage;
	}
	public void SetTweetImage(EncodedImage value)
	{
		TweetImage = value;
	}
	
	public String getAuthorHandle()
	{
		return TweetAuthorHandle;
	}
	public void setAuthorHandle(String value)
	{
		TweetAuthorHandle = value;
	}
	
	public String getAuthorName()
	{
		return TweetAuthorName;
	}
	public void setAuthorName(String value)
	{
		TweetAuthorName = value;
	}
	
	public EncodedImage getAuthorImage()
	{
		return TweetAuthorImage;
	}
	public void setAuthorImage(EncodedImage value)
	{
		TweetAuthorImage = value;
	}
	
	public String getAuthorImageUrl()
	{
		return TweetAuthorImageUrl;
	}
	public void setAuthorImageUrl(String value)
	{
		TweetAuthorImageUrl = value;
	}
	
	public void setIsDownloaded(boolean value){
		IsDownloaded = value;
	}
	public boolean getIsDownloaded(){
		return IsDownloaded;
	}
	
	public String getRawDate()
	{
		return RawDate;
	}
	
	public String getTweetDate()
	{
		return TweetDate;
	}
	public void setTweetDate(String value)
	{
		TweetDate = UtilityClass.getTweetPublishDate(value);
	}
	
	public String getTweetComment()
	{
		return TweetComment;
	}
	public void setTweetComment(String value)
	{
		TweetComment = value;
	}
	
	public boolean getIsFavorite()
	{
		return IsFavorite;
	}
	public void setIsFavorite( boolean value)
	{
		IsFavorite= value;
	}
	
	public boolean getIsRetweet()
	{
		return IsRetweet;
	}
	public void setIsRetweet( boolean value)
	{
		IsRetweet = value;
	}
	
	public boolean getIsReply()
	{
		return IsReply;
	}
	public void setIsReply(boolean value)
	{
		IsReply = value;
	}
	
	public boolean getIsUnread()
	{
		return IsUnread;
	}
	public void setIsUnread( boolean value)
	{
		IsUnread = value;
	}

	public String getRetweetAuthor()
	{
		return RetweetAuthor;
	}
	public void setRetweetAuthor(String value)
	{
		RetweetAuthor = value;
	}
	
	public String getReplyAuthor()
	{
		return ReplyAuthor;
	}
	public void setReplyAuthor(String value)
	{
		ReplyAuthor = value;
	}

	public String getTwitterClient()
	{
		return TwitterClient;
	}
	public void setTwitterClient(String value)
	{
		TwitterClient = value;
	}
}

package com.dway.twalay;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

import com.twitterapime.model.MetadataSet;
import com.twitterapime.rest.UserAccount;
import com.twitterapime.rest.UserAccountManager;
import com.twitterapime.search.LimitExceededException;
import com.twitterapime.search.Query;
import com.twitterapime.search.QueryComposer;
import com.twitterapime.search.SearchDevice;
import com.twitterapime.search.Tweet;

import me.regexp.RE;
import net.rim.blackberry.api.browser.Browser;
import net.rim.blackberry.api.browser.BrowserSession;
import net.rim.device.api.i18n.SimpleDateFormat;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontFamily;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.ui.container.*;

public class TweetDetails extends BaseScreen{
	
	  private FontFamily fontFamily[] = FontFamily.getFontFamilies();
	  private Font handleFont = fontFamily[1].getFont(Font.BOLD, 20);
	  private Font dateFont = fontFamily[1].getFont(Font.ITALIC, 16);
	  private Font tweetFont = fontFamily[1].getFont(Font.PLAIN, 20);
	  private int txtPad = 5;	
	  private ListStyleButtonField linkFfUnff, linkBlockUnblock;
	  private UserAccountManager m;
	  private BitmapField avatar;
	  private GetAvatarThread getAvatar = null;
	  private Bitmap image = null;
	  private UserAccount ua = null;
	  private String username;

      private BaseTweet temp = null;
      private ListStyleButtonField lblFollowers, lblFollowing, lblTweets, lblFavorites;
      private LabelField name, handle, lasttweet, tweettime, comment;
      private ColorLabel bioDetails, locationDetails, websiteDetails, timeDetails;
      
	public TweetDetails(UserAccountManager m, String username)
	{
		super(username.substring(1));
		
		this.m = m;
		this.username = username.substring(1);
		new GetUserLastTweet(username.substring(1)).start();
		temp = new BaseTweet(username.substring(1));
		//please also have a class for the Twitter user account

	    LoadTitle();
	    add(new NullField(NullField.FOCUSABLE));
	    LoadTweet();
	    add(new NullField(NullField.FOCUSABLE));
	    //if (temp.getAuthorImage() != null)
	    	//LoadTwitpic();
	
	    add(new NullField(NullField.FOCUSABLE));
	    LoadActions();
	    add(new NullField(NullField.FOCUSABLE));
	    LoadBio();
	    add(new NullField(NullField.FOCUSABLE));
	    add(new LabelField());
	}

	
	void LoadTitle()
	{
		HorizontalFieldManager pageTitle = new HorizontalFieldManager();
		
		avatar = new BitmapField();
		if(temp.getAuthorImage() != null)
			avatar.setBitmap(temp.getAuthorImage().getBitmap());
		else{
			avatar.setBitmap(EncodedImage.getEncodedImageResource("avatarpic.png").getBitmap());
		}
		
		handle = new LabelField(temp.getAuthorHandle());
		handle.setFont(handleFont);
		name = new LabelField();
		name.setFont(tweetFont);
		//LabelField ffStatus = new LabelField("You follow each other");
		//ffStatus.setFont(dateFont);
		
		VerticalFieldManager nameStack = new VerticalFieldManager();
		nameStack.add(handle);
		nameStack.add(name);
		//nameStack.add(ffStatus);
		nameStack.setMargin(0, 0, 0, txtPad);
		
		pageTitle.setMargin(txtPad, 0, 0, txtPad);
		pageTitle.add(avatar);
		pageTitle.add(nameStack);
		
		this.add(pageTitle);
		
		
	}
	
	void LoadTweet()
	{

		VerticalFieldManager tweetStack = new VerticalFieldManager();
		tweetStack.setMargin(txtPad, 0, 0, txtPad);
		
		lasttweet = new LabelField();
		lasttweet.setFont(tweetFont);
		tweettime  = new LabelField();
		tweettime.setFont(dateFont);
	    comment = new LabelField();
		comment.setFont(dateFont);
		
		SeparatorField separator   =  new SeparatorField();
		separator.setMargin(txtPad,0,0,0);
		
		tweetStack.add(lasttweet);
		tweetStack.add(tweettime);
		tweetStack.add(comment);
		tweetStack.add(separator);
		
		this.add(tweetStack);
		
	}

	void LoadActions(){
	    Bitmap _caret = Bitmap.getBitmapResource( "chevron_right_black_15x22.png" );
	    
        ListStyleButtonSet buttonSet = new ListStyleButtonSet();
       
        lblTweets = new ListStyleButtonField("Tweets ()", _caret);
        lblTweets.setChangeListener(new FieldChangeListener(){
	    	
			public void fieldChanged(Field field, int context) {
				FriendsTweets frtwt = new FriendsTweets(m, username);
            	TwalayHome.getUiApplication().pushScreen(frtwt);
			}
        });
        buttonSet.add( lblTweets );
        
        lblFollowers = new ListStyleButtonField("Followers ()", _caret );
        lblFollowers.setChangeListener(new FieldChangeListener(){
	    	
			public void fieldChanged(Field field, int context) {
				// Get the default sessionBrowserSession 
    			BrowserSession browserSession = Browser.getDefaultSession();
    			// now launch the URL
    			browserSession.displayPage("http://twitter.com/" + username + "/followers");
			}
        });
        buttonSet.add( lblFollowers );

        lblFollowing = new ListStyleButtonField("Following ()", _caret );
        lblFollowing.setChangeListener(new FieldChangeListener(){
	    	
			public void fieldChanged(Field field, int context) {
				// Get the default sessionBrowserSession 
    			BrowserSession browserSession = Browser.getDefaultSession();
    			// now launch the URL
    			browserSession.displayPage("http://twitter.com/" + username + "/following");
			}
        });
        buttonSet.add( lblFollowing );
        
        lblFavorites = new ListStyleButtonField("Favorites ()", _caret );
        lblFavorites.setChangeListener(new FieldChangeListener(){
	    	
			public void fieldChanged(Field field, int context) {
				// Get the default sessionBrowserSession 
    			BrowserSession browserSession = Browser.getDefaultSession();
    			// now launch the URL
    			browserSession.displayPage("http://twitter.com/" + username + "/favorites");
			}
        });
        buttonSet.add( lblFavorites );

        linkFfUnff = new ListStyleButtonField("Loading...", _caret );
        linkFfUnff.setChangeListener(new FieldChangeListener(){
	    	
			public void fieldChanged(Field field, int context) {
				// TODO Auto-generated method stub
				try {
					if(linkFfUnff.toString().equals("Loading...")){
						
					}
					else if(linkFfUnff.toString().equals("Follow")){
						m.follow(ua);
						linkFfUnff.setText("Unfollow");
					}
					else{
						m.unfollow(ua);
						linkFfUnff.setText("Follow");
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (LimitExceededException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}			
		});
        buttonSet.add( linkFfUnff );
        
        linkBlockUnblock = new ListStyleButtonField("Loading...", _caret );
        linkBlockUnblock.setChangeListener(new FieldChangeListener(){
	    	
			public void fieldChanged(Field field, int context) {
				// TODO Auto-generated method stub
				try {
					if(linkBlockUnblock.toString().equals("Loading...")){
						
					}
					else if(linkBlockUnblock.toString().equals("Block")){
						m.follow(ua);
						linkBlockUnblock.setText("UnBlock");
					}
					else{
						m.unfollow(ua);
						linkBlockUnblock.setText("Block");
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (LimitExceededException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}			
		});
        buttonSet.add( linkBlockUnblock );

		this.add(buttonSet);
	}
	
	void LoadBio()
	{
		VerticalFieldManager bioStack  = new VerticalFieldManager(Manager.USE_ALL_WIDTH);
		bioStack.setMargin(txtPad,0,0,txtPad);

		ColorLabel bioHeader = new ColorLabel("Bio", 0, Color.BLACK);
		bioHeader.setFont(handleFont);
		
		bioDetails = new ColorLabel("", 0,Color.BLACK);
		bioDetails.setFont(tweetFont);
		bioDetails.setMargin(0,0,txtPad,0);

		
		ColorLabel locationHeader = new ColorLabel("Location", 0, Color.BLACK);
		locationHeader.setFont(handleFont);
		
		locationDetails = new ColorLabel("", 0,Color.BLACK);
		locationDetails.setFont(tweetFont);
		locationDetails.setMargin(0,0,txtPad,0);

		
		ColorLabel websiteHeader = new ColorLabel("Website", 0, Color.BLACK);
		websiteHeader.setFont(handleFont);
		
		websiteDetails = new ColorLabel("", 0, Color.BLACK);
		websiteDetails.setFont(tweetFont);
		websiteDetails.setMargin(0,0,txtPad,0);

		
		ColorLabel timeHeader = new ColorLabel("Tweeting since",0,Color.BLACK);
		timeHeader.setFont(handleFont);
	
		timeDetails = new ColorLabel("", 0, Color.BLACK);
		timeDetails.setFont(tweetFont);
		timeDetails.setMargin(0,0,20,0);
		
		bioStack.add(bioHeader);
		bioStack.add(bioDetails);
		bioStack.add(locationHeader);
		bioStack.add(locationDetails);
		bioStack.add(websiteHeader);
		bioStack.add(websiteDetails);
		bioStack.add(timeHeader);
		bioStack.add(timeDetails);
		
		this.add(bioStack);
		
	}
	
	final class GetAvatarThread extends Thread{
	      private String _imgurl;
	      
	      GetAvatarThread(String imageUrl){
	    	  _imgurl = imageUrl;
	      }
	      
	      public void run(){
	    	  HttpConnection connection = null;
			  InputStream in = null;
	    	  try {
	    		  Vector dvec = Home.vec;
		  		  for(int i = 0; i < dvec.size(); i++){
		  		     BaseTweet ava = ((BaseTweet)dvec.elementAt(i));
		  	    	    if(temp.getAuthorHandle().equals(ava.getAuthorHandle())){
		  	    		  if(ava.getIsDownloaded())
		  	    		   	image = ava.getAuthorImage().getBitmap();
		  	    		  else
		  	    		   	image = null;
		  	    		  
		  	    		  break;
		  	    	 }
		  		  }
	    	      
	    	      if(image == null){
		    		  connection = (HttpConnection) Connector.open(_imgurl);
					  connection.setRequestMethod(HttpConnection.GET);
					  in = connection.openInputStream();
					  byte[] buffer = new byte[(int) connection.getLength()]; 
					  in.read(buffer, 0, buffer.length);
					  image = EncodedImage.createEncodedImage(buffer, 0, buffer.length).getBitmap();
					  
					  UiApplication.getUiApplication().invokeLater(new Runnable() {
				            public void run() {
				            	synchronized (UiApplication.getEventLock()) {
				            		avatar.setBitmap(image);
				            	}
				            }
					  });
	    	      }
	    	      else{
	    	    	  UiApplication.getUiApplication().invokeLater(new Runnable() {
				            public void run() {
				            	synchronized (UiApplication.getEventLock()) {
				            		avatar.setBitmap(image);
				            	}
				            }
					  });
	    	      }
	    	  }catch(final Exception ex){
	    		  ex.printStackTrace();
	    	  }
	      }
	  }
	   

	class CheckIsFollowing extends Thread 
	  {	     
		  UserAccountManager m; UserAccount ua;
		  CheckIsFollowing(UserAccountManager m, UserAccount ua){
			  this.m = m;
			  this.ua = ua;
		  }
		  
	      public void run(){
  		  UiApplication.getUiApplication().invokeLater(new Runnable() {
		            public void run() {
		            	synchronized (UiApplication.getEventLock()) {
		            		try{
		            			linkFfUnff.setText(m.isFollowing(ua) ? "Unfollow" : "Follow");
			            	}catch(final Exception ex){
			  	    		  ex.printStackTrace();
			  	    	  }
		            	}
		            }
			    });
	      }
	  }
	  
	  class CheckIsBlocking extends Thread 
	  {	     
		  UserAccountManager m; UserAccount ua;
		  CheckIsBlocking(UserAccountManager m, UserAccount ua){
			  this.m = m;
			  this.ua = ua;
		  }
		  
	      public void run(){
  		  UiApplication.getUiApplication().invokeLater(new Runnable() {
		            public void run() {
		            	synchronized (UiApplication.getEventLock()) {
		            		try{
		            			linkBlockUnblock.setText(m.isBlocking(ua) ? "Unblock" : "Block");
			            	}catch(final Exception ex){
			  	    		  ex.printStackTrace();
			  	    	  }
		            	}
		            }
			    });
	      }
	  }
	  
	  //For getting User's latest tweet and then fetching their 
	  class GetUserLastTweet extends Thread 
	  {
		  String uname;
		  GetUserLastTweet(String uname){
			  this.uname = uname;
		  }
		  
	      public void run(){
	    	  try{
		    	  SearchDevice s = SearchDevice.getInstance();
	          	  Query q = QueryComposer.resultCount(1);
	          	  q = QueryComposer.append(q, QueryComposer.from(uname));
	          	  Tweet[] twts = s.searchTweets(q);
	          	  
	          	  if(twts != null){
	          		  temp = new BaseTweet(twts[0]);
	          		  
	          		  UiApplication.getUiApplication().invokeLater(new Runnable() {
			              public void run() {
			             	  synchronized (UiApplication.getEventLock()) {
			            	 	  try{
			            	 		 lasttweet.setText(temp.getTweetText());
				            	  }catch(final Exception ex){
				  	    		    ex.printStackTrace();
				  	    	      }
			            	  }
			              }
				      });
	          		  
	          		  new GetUserAccount(m).start();
	          	  }
	    	  }catch(Exception ex){
	    		  ex.printStackTrace();
	    	  }
	      }
	  }
	  
	  //Get user account
	  class GetUserAccount extends Thread 
	  {	     
		  UserAccountManager m;
		  GetUserAccount(UserAccountManager m){
			  this.m = m;
		  }
		  
	      public void run(){
	    	  try{
          		  temp.setUserAccount(m.getUserAccount(temp.getUserAccount()));
          		  ua = temp.getUserAccount();
          		  Tweet temp2 = (Tweet) ua.getObject(MetadataSet.USERACCOUNT_LAST_TWEET);
    			  
          		  String tre = temp2.getString(MetadataSet.TWEET_IN_REPLY_TO_TWEET_ID);          		  
	          	  if(tre != null){
	          		  temp.setIsReply((tre.equals("true") ? true : false));
	          		  if(temp.getIsReply()){
		    			  Vector v = checkForUserNames(temp.getTweetText());
		    		      if(v.size() > 0){
		    		    	  temp.setReplyAuthor(v.elementAt(0).toString());
		    		    	  temp.setTweetComment("replied"); 
		    		    	  for(int i = 0;i < v.size();i++){
		    		    		  temp.setTweetComment(temp.getTweetComment() + " " + v.elementAt(i).toString());
		    		    	  }
		    			  }
	          		  }
          		  }
          		  
          		  UiApplication.getUiApplication().invokeLater(new Runnable() {
		              public void run() {
		             	  synchronized (UiApplication.getEventLock()) {
		            	 	  try{
		            	 		 name.setText(ua.getString(MetadataSet.USERACCOUNT_NAME));
		            	 		 handle.setText(ua.getString(MetadataSet.USERACCOUNT_USER_NAME));
		            	 		 tweettime.setText(temp.getTweetDate() + ", from " + temp.getTwitterClient());
		            	 		 comment.setText(temp.getTweetComment());
		            	 		 
		            	 		 locationDetails.setText(ua.getString(MetadataSet.USERACCOUNT_LOCATION));
		            	 		 websiteDetails.setText(ua.getString(MetadataSet.USERACCOUNT_URL));
		            	 		 
		            	 		 Date d = new Date(Long.parseLong(ua.getString(MetadataSet.USERACCOUNT_CREATE_DATE)));
		            			 String pattern = "dd/MM/yyyy";
		            			 String dateInString = new SimpleDateFormat(pattern).format(d);
		            	 		 timeDetails.setText(dateInString);
		            	 		 
		            	 		 String bio = ua.getString(MetadataSet.USERACCOUNT_DESCRIPTION);
		            	 		 bioDetails.setText((bio == null ? "" : ua.getString(MetadataSet.USERACCOUNT_DESCRIPTION)));
		            	 		 
		            	 		 lblTweets.setText("Tweets (" + ua.getString(MetadataSet.USERACCOUNT_TWEETS_COUNT) + ")");
		            		     lblFollowers.setText("Followers (" + ua.getString(MetadataSet.USERACCOUNT_FOLLOWERS_COUNT) + ")");
		            		     lblFollowing.setText("Following (" + ua.getString(MetadataSet.USERACCOUNT_FRIENDS_COUNT) + ")");
		            		     lblFavorites.setText("Favorites (" + ua.getString(MetadataSet.USERACCOUNT_FAVOURITES_COUNT) + ")");
			            	  }catch(final Exception ex){
			  	    		    ex.printStackTrace();
			  	    	      }
		            	  }
		              }
			      });
          		  
          		  getAvatar = new GetAvatarThread(ua.getString(MetadataSet.USERACCOUNT_PICTURE_URI_NORMAL));
      			  getAvatar.start();
      			 
      			  new CheckIsFollowing(m, ua).start();
      			  new CheckIsBlocking(m, ua).start();
	    	  }catch(Exception ex){
	    		  ex.printStackTrace();
	    	  }
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
				  if(!v.contains(stry) || !stry.startsWith(temp.getAuthorHandle()))
					  v.addElement(stry);
			  }
		  
		return v;
	}
}

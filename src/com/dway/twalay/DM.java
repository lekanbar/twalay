package com.dway.twalay;

import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

import com.twitterapime.model.MetadataSet;
import com.twitterapime.rest.Timeline;
import com.twitterapime.rest.UserAccount;
import com.twitterapime.rest.UserAccountManager;
import com.twitterapime.search.Query;
import com.twitterapime.search.QueryComposer;
import com.twitterapime.search.SearchDeviceListener;
import com.twitterapime.search.Tweet;

import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.TextField;

public class DM extends BaseScreen{
	
	DMCustomList TL;
	int val;
	String uname;
	UserAccountManager m;
	HttpRequestDispatcher dispatcher;
	TextField txtTweetBox;
	LabelField lblLoading;
	public PersistentObject pob;
	public Hashtable db;
	static final long KEY = 0x7824c240795aa705L;
	static boolean isfirst = true;
	Timer timer;
	
	public DM(final UserAccountManager m, String uname){
		super(uname);
		
		//Set action menu focus
    	this.getActionMenu().b5.setFocus();
		
		this.m = m;
		this.uname = uname;
		
    	TL = new DMCustomList(UtilityClass.getDMFriendsTweets());
    	TL.setEmptyString("Loading DMs...", DrawStyle.HCENTER);
		TL.setSize(UtilityClass.getDMFriendsTweets().size());
		TL.setChangeListener(new FieldChangeListener(){

			public void fieldChanged(Field field, int context) {
				// TODO Auto-generated method stub
				if(field == TL){
					if(!isfirst){
						BaseTweet fua = (BaseTweet) UtilityClass.getDMFriendsTweets().elementAt(TL.getSelectedIndex());
						fua.setIsUnread(false);
						sortReceivedAndSent(fua.getUserAccount());
					}
					isfirst = false;
				}
			}
			
		});
		add(TL);
		
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask(){
			  public void run(){
				  UiApplication.getUiApplication().invokeLater(new Runnable() {
			            public void run() {
			            	synchronized (UiApplication.getEventLock()) {
			            		HttpRequestDispatcher hrd = new HttpRequestDispatcher(DM.this, m, 2);
			            		hrd.run();
			            	}
			            }
				  });
			  }
		}, 300000, 300000);
		
		//Get the First set of tweets
		try {
			if(!m.isVerified()){
				if(m.verifyCredential()){
					HttpRequestDispatcher hrd = new HttpRequestDispatcher(DM.this, m, 2);
	        		hrd.run();
				}
			}
			else{
				HttpRequestDispatcher hrd = new HttpRequestDispatcher(DM.this, m, 2);
        		hrd.run();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			UiApplication.getUiApplication().invokeLater(new Runnable() {
	            public void run() {
	            	synchronized (UiApplication.getEventLock()) {
	            		Dialog.alert("Connection error :(, check and please refresh.");
	                	//System.exit(0);
	            	}
	            }
			});
			e.printStackTrace();
		}
		
		AvatarFetch af = new AvatarFetch();
		af.start();
	}
	
	class AvatarFetch extends Thread {
		
		public void run() {
			  HttpConnection connection = null;
			  InputStream in = null;
			  try {
				  for(int i = 0; i < Home.vec.size(); i++){
					  BaseTweet useravatar = (BaseTweet)Home.vec.elementAt(i);
					  if(!useravatar.getIsDownloaded()){
						  connection = (HttpConnection) Connector.open(useravatar.getAuthorImageUrl());
						  connection.setRequestMethod(HttpConnection.GET);
						  in = connection.openInputStream();
						  byte[] buffer = new byte[(int) connection.getLength()]; 
						  in.read(buffer, 0, buffer.length);
						  EncodedImage img = EncodedImage.createEncodedImage(buffer, 0, buffer.length);
						  
						  ((BaseTweet)Home.vec.elementAt(i)).setAuthorImage(img);
						  ((BaseTweet)Home.vec.elementAt(i)).setIsDownloaded(true);
						  
						  //clean up
						  in.close();
						  connection.close();
						  
						  UiApplication.getUiApplication().invokeLater(new Runnable() {
					            public void run() {
					            	synchronized (UiApplication.getEventLock()) {
					            		isfirst = true;
					            		int len = UtilityClass.getDMFriendsTweets().size();
					            		TL.setSize(len);
					            		TL.invalidate();
					            	}
					            }
						    });
					  }
				  }				  
			  } catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
			  }
			  catch(Exception ex){
			  }
			  finally{
				  try {
					  if(in != null)
						  in.close();
					  if(connection != null)
						  connection.close();
				  } catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				  }
			  }
		}
	}
	
	public void sortReceivedAndSent(UserAccount ua){
		Vector recsent = new Vector();
		for(int i = 0; i < UtilityClass.getDMReceivedTweets().size(); i++){
			BaseTweet rec = (BaseTweet) UtilityClass.getDMReceivedTweets().elementAt(i);
			UserAccount dua = rec.getUserAccount();
			if(dua.equals(ua))
				recsent.addElement(rec);
		}
		for(int i = 0; i < UtilityClass.getDMSentTweets().size(); i++){
			BaseTweet sen = (BaseTweet) UtilityClass.getDMSentTweets().elementAt(i);
			UserAccount rua = sen.getMainTweet().getRecipientAccount();
			if(rua.equals(ua))
				recsent.addElement(sen);
		}
		
		recsent = InsertionSort(recsent);
		TwalayHome.getUiApplication().pushScreen(new DMThread(m, recsent, ua, uname));
	}
	
	public Vector InsertionSort(Vector data){
	  int len = data.size();
	  BaseTweet key = null;
	  int i = 0;
	  for(int j = 1;j<len;j++){
	    key = ((BaseTweet)data.elementAt(j));
	    i = j-1;
	    while(i>=0 && ((BaseTweet)data.elementAt(i)).getMainTweet().getDate(MetadataSet.TWEET_PUBLISH_DATE).getTime() < key.getMainTweet().getDate(MetadataSet.TWEET_PUBLISH_DATE).getTime()){
	    	data.setElementAt((BaseTweet)data.elementAt(i), i+1);
	      i = i-1;
	      data.setElementAt(key, i+1);
	    }
	  }
	  return data;
	}
	
	public void updateUI(String msg, Vector bucket1, int todo){
		if(msg.equals("successful")){
			if(todo == 2){
				UtilityClass.setDMReceivedTweets(bucket1);
				UiApplication.getUiApplication().invokeLater(new Runnable() {
		            public void run() {
		            	synchronized (UiApplication.getEventLock()) {
							for(int i = 0; i < UtilityClass.getDMReceivedTweets().size(); i++){
								boolean isthere = false;
								for(int j = 0; j < UtilityClass.getDMFriendsTweets().size(); j++){
									if(((BaseTweet)UtilityClass.getDMReceivedTweets().elementAt(i)).getUserAccount().getString(MetadataSet.USERACCOUNT_USER_NAME).equals(
											((BaseTweet)UtilityClass.getDMFriendsTweets().elementAt(j)).getUserAccount().getString(MetadataSet.USERACCOUNT_USER_NAME))){
										isthere = true;
										break;
									}
								}
								
								if(!isthere)
									if(!UtilityClass.getUserToken().getUsername().equals(
											((BaseTweet)UtilityClass.getDMReceivedTweets().elementAt(i)).getUserAccount().getString(MetadataSet.USERACCOUNT_USER_NAME))){
										if(!((BaseTweet)UtilityClass.getDMReceivedTweets().elementAt(i)).getIsUnread())
											((BaseTweet)UtilityClass.getDMReceivedTweets().elementAt(i)).setIsUnread(false);
										UtilityClass.getDMFriendsTweets().addElement((BaseTweet)UtilityClass.getDMReceivedTweets().elementAt(i));
									}
							}
							
							HttpRequestDispatcher hrd = new HttpRequestDispatcher(DM.this, m, 3);
							hrd.run();
		            	}
		            }
				});
			}
			else if(todo == 3){
				UtilityClass.setDMSentTweets(bucket1);
				UiApplication.getUiApplication().invokeLater(new Runnable() {
		            public void run() {
		            	synchronized (UiApplication.getEventLock()) {
		            		for(int i = 0; i < UtilityClass.getDMSentTweets().size(); i++){
								boolean isthere = false;
								for(int j = 0; j < UtilityClass.getDMFriendsTweets().size(); j++){
									if(((BaseTweet)UtilityClass.getDMSentTweets().elementAt(i)).getUserAccount().getString(MetadataSet.USERACCOUNT_USER_NAME).equals(
											((BaseTweet)UtilityClass.getDMFriendsTweets().elementAt(j)).getUserAccount().getString(MetadataSet.USERACCOUNT_USER_NAME))){
										isthere = true;
										break;
									}
								}
								
								if(!isthere){
									if(!UtilityClass.getUserToken().getUsername().equals(
											((BaseTweet)UtilityClass.getDMSentTweets().elementAt(i)).getUserAccount().getString(MetadataSet.USERACCOUNT_USER_NAME))){
										if(!((BaseTweet)UtilityClass.getDMSentTweets().elementAt(i)).getIsUnread())
											((BaseTweet)UtilityClass.getDMSentTweets().elementAt(i)).setIsUnread(false);
										UtilityClass.getDMFriendsTweets().addElement((BaseTweet)UtilityClass.getDMSentTweets().elementAt(i));
									}
								}
							}							
		            		render();
		            	}
		            }
				});
			}
		}
		else{
			render();
		}
	}
	
	public void render(){
		if(UtilityClass.getDMFriendsTweets().size() > 0){
			for(int x = 0; x < UtilityClass.getDMFriendsTweets().size(); x++){
				BaseTweet tweet = (BaseTweet)UtilityClass.getDMFriendsTweets().elementAt(x);
				
				boolean isthere = false;
				for(int i = 0; i < Home.vec.size(); i++){
					BaseTweet useravatar = (BaseTweet)Home.vec.elementAt(i);
					if(useravatar.getAuthorHandle().equals(tweet.getAuthorHandle())){
						isthere = true;
						break;
					}
				}
				
				if(!isthere){
					Home.vec.addElement(tweet);
				}
			}
			
			UiApplication.getUiApplication().invokeLater(new Runnable() {
	            public void run() {
	            	synchronized (UiApplication.getEventLock()) {
	            		isfirst = true;
	            		int len = UtilityClass.getDMFriendsTweets().size();
	            		TL.setSize(len);
	            		TL.invalidate();
	            	}
	            }
		    });	
			
			AvatarFetch af = new AvatarFetch();
			af.start();
		}
		else{
			UiApplication.getUiApplication().invokeLater(new Runnable() {
	            public void run() {
	            	synchronized (UiApplication.getEventLock()) {
	            		isfirst = true;
	            		int len = UtilityClass.getDMFriendsTweets().size();
	            		TL.setEmptyString("Connection error :(, please refresh.", DrawStyle.HCENTER);
	            		TL.setSize(len);
	            		TL.invalidate();
	            	}
	            }
		    });
		}
	}
	
	class HttpRequestDispatcher implements Runnable {
		private DM screen;
		private Vector dtweets;
		private UserAccountManager m;
		private int todo;
		Timer timer;
		
		public HttpRequestDispatcher(DM screen, UserAccountManager m, int todo)
		{
			this.screen = screen;
			this.m = m;
			this.todo = todo;
		}
		
		public void run() {
			try{
				Timeline tml = Timeline.getInstance(m);
				if(todo == 2){
					dtweets = new Vector();
					
					Query q = QueryComposer.count(30);
					tml.startGetDirectMessages(q, true, new SearchDeviceListener() {
						public void tweetFound(Tweet tweet) {
							dtweets.addElement(new BaseTweet(tweet));
							System.out.println("Tweet: " + tweet);
						}
			
						public void searchCompleted() {
							// TODO Auto-generated method stub
							screen.updateUI("successful", dtweets, todo);
							System.out.println("completed");
						}
			
						public void searchFailed(Throwable cause) {
							// TODO Auto-generated method stub
							screen.updateUI("", null, todo);
							System.out.println("failed: " + cause.getMessage());
						}
				    });
				}
				else if(todo == 3){
					dtweets = new Vector();
					
					Query q = QueryComposer.count(30);
					tml.startGetDirectMessages(q, false, new SearchDeviceListener() {
						public void tweetFound(Tweet tweet) {
							dtweets.addElement(new BaseTweet(tweet));
							System.out.println("Tweet: " + tweet);
						}
			
						public void searchCompleted() {
							// TODO Auto-generated method stub	
							screen.updateUI("successful", dtweets, todo);
							System.out.println("completed");
						}
			
						public void searchFailed(Throwable cause) {
							// TODO Auto-generated method stub
							screen.updateUI("", null, todo);
							System.out.println("failed: " + cause.getMessage());
						}
				  });
				}
			}catch(Exception ex){
				UiApplication.getUiApplication().invokeLater(new Runnable() {
		            public void run() {
		            	synchronized (UiApplication.getEventLock()) {
		            		Dialog.alert("There is no internet connection :(, check and please try again.");
		                	//System.exit(0);
		            	}
		            }
				});
			}
		}
	}
	
	protected void makeMenu(Menu menu, int instance) {
		super.makeMenu(menu, instance);
		menu.add(new MenuItem("Refresh", 1, 1){

			public void run() {
				if(m.isVerified()){
					HttpRequestDispatcher hrd = new HttpRequestDispatcher(DM.this, m, 2);
	        		hrd.run();
				}
				else{
					try {
						if(m.verifyCredential()){
							HttpRequestDispatcher hrd = new HttpRequestDispatcher(DM.this, m, 2);
			        		hrd.run();
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						UiApplication.getUiApplication().invokeLater(new Runnable() {
				            public void run() {
				            	synchronized (UiApplication.getEventLock()) {
				            		Dialog.alert("Connection error :(, check and please refresh.");
				                	//System.exit(0);
				            	}
				            }
						});
						e.printStackTrace();
					}
				}
			}
		});
		
		menu.add(MenuItem.separator(2));		
		
		menu.add(new MenuItem("Sign Out", 3, 1){

			public void run() {
				// TODO Auto-generated method stub
				signOut();
			}			
		});
	}
	
	public void signOut(){
		UtilityClass.signOut();
		DM.this.close();
		System.exit(0);
	}
}



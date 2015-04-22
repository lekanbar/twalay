package com.dway.twalay;

import java.io.IOException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import com.twitterapime.model.MetadataSet;
import com.twitterapime.rest.Timeline;
import com.twitterapime.rest.TweetER;
import com.twitterapime.rest.UserAccount;
import com.twitterapime.rest.UserAccountManager;
import com.twitterapime.search.LimitExceededException;
import com.twitterapime.search.Query;
import com.twitterapime.search.QueryComposer;
import com.twitterapime.search.SearchDeviceListener;
import com.twitterapime.search.Tweet;

import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.XYEdges;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.TextField;
import net.rim.device.api.ui.decor.Background;
import net.rim.device.api.ui.decor.BackgroundFactory;
import net.rim.device.api.ui.decor.Border;
import net.rim.device.api.ui.decor.BorderFactory;

public class DMThread extends BaseScreen{
	
	DMThreadCustomList TL;
	Vector convo;
	int val;
	UserAccountManager m;
	UserAccount ua;
	TextField txtTweetBox;
	LabelField lblLoading;
	static boolean isfirst = true;
	
	public DMThread(UserAccountManager m1, Vector convo, UserAccount ua2, String uname){
		super(uname);
		
		//Set action menu focus
    	this.getActionMenu().b5.setFocus();
		
		this.convo = convo;
		this.m = m1;
		this.ua = ua2;
		
		txtTweetBox = new TextField();
		XYEdges padding = new XYEdges(5, 5, 5, 5);
		Border roundedBorder = BorderFactory.createRoundedBorder(padding, Color.YELLOWGREEN, Border.STYLE_SOLID);
		Background solidBackground = BackgroundFactory.createSolidBackground(Color.WHITE);
		txtTweetBox.setBorder(roundedBorder);
		txtTweetBox.setBackground(solidBackground);
		add(txtTweetBox);
		ButtonField bf = new ButtonField("Send");	
		bf.setChangeListener(new FieldChangeListener(){
			public void fieldChanged(Field field, int context) {
				// TODO Auto-generated method stub
				Tweet t = new Tweet(ua.getString(MetadataSet.USERACCOUNT_USER_NAME), txtTweetBox.getText());
				TweetER ter = TweetER.getInstance(m);
				try {
					t = ter.send(t);
					getConvo(txtTweetBox.getText());
					txtTweetBox.setText("");
					Timer timer = new Timer();
					timer.schedule(new TimerTask(){
						  public void run(){
							  UiApplication.getUiApplication().invokeLater(new Runnable() {
						            public void run() {
						            	synchronized (UiApplication.getEventLock()) {
						            		HttpRequestDispatcher hrd = new HttpRequestDispatcher(DMThread.this, m, 2);
							        		hrd.run();
						            	}
						            }
							  });
						  }
					}, 60000);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					Dialog.alert("Sorry there was a connection error :(, please try again.");
					e.printStackTrace();
				} catch (LimitExceededException e) {
					// TODO Auto-generated catch block
					Dialog.alert("Oops sorry, you have exceeded your tweeting limit (Tweet Jail) :'(");
					e.printStackTrace();
				}
			}
			
		});
		add(bf);
		
    	TL = new DMThreadCustomList(convo);
		TL.setSize(convo.size());
		TL.setChangeListener(new FieldChangeListener(){

			public void fieldChanged(Field field, int context) {
				// TODO Auto-generated method stub
				if(field == TL){
					if(!isfirst){
						BaseTweet twt = (BaseTweet)DMThread.this.convo.elementAt(TL.getSelectedIndex());
						TweetDetails twtdetails = new TweetDetails(m, "@" + twt.getAuthorHandle());
		            	TwalayHome.getUiApplication().pushScreen(twtdetails);
					}
					isfirst = false;
				}
			}
			
		});
		add(TL);
	}
	
	protected void makeMenu(Menu menu, int instance) {
		super.makeMenu(menu, instance);
		menu.add(new MenuItem("Refresh", 1, 1){

			public void run() {
				if(m.isVerified()){
					HttpRequestDispatcher hrd = new HttpRequestDispatcher(DMThread.this, m, 2);
	        		hrd.run();
				}
				else{
					try {
						if(m.verifyCredential()){
							HttpRequestDispatcher hrd = new HttpRequestDispatcher(DMThread.this, m, 2);
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
	
	
	public void getConvo(String msg){
		String uname = UtilityClass.getUserToken().getUsername();
		
		BaseTweet t = new BaseTweet(msg, uname, null, UtilityClass.getTweetPublishDate(String.valueOf(new Date().getTime())));
		
		Vector dvec = Home.vec;
	    for(int i = 0; i < dvec.size(); i++){
	    	BaseTweet ava = ((BaseTweet)dvec.elementAt(i));
    	    if(t.getAuthorHandle().equals(ava.getAuthorHandle())){
    		    if(ava.getIsDownloaded())
    		    	t.setAuthorImage(ava.getAuthorImage());
    		    else
    		    	t.setAuthorImage(EncodedImage.getEncodedImageResource("avatarpic.png"));
    		  
    		    break;
    	    }
	    }
	    
	    if(t.getAuthorImage() == null)
	    	t.setAuthorImage(EncodedImage.getEncodedImageResource("avatarpic.png"));
	    
		convo.insertElementAt(t, 0);
		
		UiApplication.getUiApplication().invokeLater(new Runnable() {
            public void run() {
            	synchronized (UiApplication.getEventLock()) {
            		int len = convo.size();
            		TL.setSize(len);
            		TL.invalidate();
            	}
            }
	    });
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
							
							HttpRequestDispatcher hrd = new HttpRequestDispatcher(DMThread.this, m, 3);
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
	            		sortReceivedAndSent(ua);
	            		isfirst = true;
	            		int len = convo.size();
	            		TL.setSize(len);
	            		TL.invalidate();
	            	}
	            }
		    });
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
		
		convo = InsertionSort(recsent);
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
	
	class HttpRequestDispatcher implements Runnable {
		private DMThread screen;
		private Vector dtweets;
		private UserAccountManager m;
		private int todo;
		Timer timer;
		
		public HttpRequestDispatcher(DMThread screen, UserAccountManager m, int todo)
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
	
	
	public void signOut(){
		UtilityClass.signOut();
		DMThread.this.close();
		System.exit(0);
	}
}



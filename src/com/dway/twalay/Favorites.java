package com.dway.twalay;

import java.io.IOException;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

import com.twitterapime.model.MetadataSet;
import com.twitterapime.rest.Timeline;
import com.twitterapime.rest.TweetER;
import com.twitterapime.rest.UserAccountManager;
import com.twitterapime.search.Query;
import com.twitterapime.search.QueryComposer;
import com.twitterapime.search.SearchDeviceListener;
import com.twitterapime.search.Tweet;

import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.Menu;

/**
 * A class extending the MainScreen class, which provides default standard
 * behavior for BlackBerry GUI applications.
 */
public final class Favorites extends BaseScreen
{
    /**
     * Creates a new Home object
     */
	private TimelineList TL;
	private Timer timer;
	private Refresh re;
	private static boolean isfirst = true;
	private UserAccountManager m;
	
    public Favorites(final UserAccountManager m, String uname)
    {        
    	super(uname);
    	
    	//Initializations
    	this.m = m;   	
    	
    	TL = new TimelineList(UtilityClass.getFavoritesTweets());
    	TL.setEmptyString("Loading favorite tweets...", DrawStyle.HCENTER);
    	
    	//Set callback
    	TL.setSize(UtilityClass.getFavoritesTweets().size());
    	TL.setChangeListener(new FieldChangeListener(){
			public void fieldChanged(Field field, int context) {
				// TODO Auto-generated method stub
				if(field == TL){
					if(!isfirst){
						BaseTweet tweet = (BaseTweet)UtilityClass.getFavoritesTweets().elementAt(TL.getSelectedIndex());
						ViewTweet vt = ViewTweet.getInstance(m, tweet);
						vt.show();
					}
					isfirst = false;
				}
			}
		});
    	this.add(TL);
    	
    	timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask(){
			  public void run(){
				  UiApplication.getUiApplication().invokeLater(new Runnable() {
			            public void run() {
			            	synchronized (UiApplication.getEventLock()) {
			            		int x = 0;
			        			if(UtilityClass.getFavoritesTweets().size() > 0)
			        				x = 2;
			        			else
			        				x = 1;
			        			
			            		re = new Refresh(x);
			            		re.start();
			            	}
			            }
				  });
			  }
		}, 600000, 600000);
		
		//Get the First set of tweets
		try {
			int x = 0;
			if(UtilityClass.getFavoritesTweets().size() > 0)
				x = 2;
			else
				x = 1;
			
			if(!m.isVerified()){
				if(m.verifyCredential()){
					re = new Refresh(x);
					re.start();
				}
			}
			else{
				re = new Refresh(x);
				re.start();
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
    
    public class Refresh extends Thread{
		int todo;
		
		Refresh(int todo){
			this.todo = todo;
		}
		
		public void run(){
			try{
				final Timer localtimer = new Timer();
				localtimer.scheduleAtFixedRate(new TimerTask(){
					  public void run(){
						  render();
					  }
				}, 60000, 60000);
				
				Timeline tml = Timeline.getInstance(m);
            	Query q = QueryComposer.count(m.getUserAccount().getInt(MetadataSet.USERACCOUNT_FAVOURITES_COUNT));
    			
    			tml.startGetFavoriteTweets(q, new SearchDeviceListener() {
    				
    				public void tweetFound(Tweet tweet) {
    					boolean isthere = false;
    				    for(int x = 0; x < UtilityClass.getFavoritesTweets().size(); x++){
    				    	if(((BaseTweet)UtilityClass.getFavoritesTweets().elementAt(x)).getTweetID().equals(tweet.getString(MetadataSet.TWEET_ID))){
    				    		isthere = true;
    				    		break;
    				    	}
    				    	
    				    	//Update tweet date and time
    				    	((BaseTweet)UtilityClass.getFavoritesTweets().elementAt(x)).setTweetDate(
    				    			((BaseTweet)UtilityClass.getFavoritesTweets().elementAt(x)).getRawDate());
    				    }
    				    
    				    if(!isthere){
    				    	if(todo == 1)
    				    		UtilityClass.getFavoritesTweets().addElement(new BaseTweet(tweet));
    				    	else    				    
    				    		UtilityClass.getFavoritesTweets().insertElementAt(new BaseTweet(tweet), 0);
    				    }
    				    
    					System.out.println("Tweet: " + tweet);
    				}
    	
    				public void searchCompleted() {
    					// TODO Auto-generated method stub
    					localtimer.cancel();
    					render();    					
    					System.out.println("completed");
    				}
    	
    				public void searchFailed(Throwable cause) {
    					// TODO Auto-generated method stub
    					//screen.updateUI("", null, todo);
    					localtimer.cancel();
    					render();
    					System.out.println("failed: " + cause.getMessage());
    				}
    		    });
			}catch(Exception ex){
				UiApplication.getUiApplication().invokeLater(new Runnable() {
		            public void run() {
		            	synchronized (UiApplication.getEventLock()) {
		            		Dialog.alert("Connection error :(, check and please refresh.");
		                	//System.exit(0);
		            	}
		            }
				});
			}
		}
	
		public void render(){
			if(UtilityClass.getFavoritesTweets().size() > 0){
				for(int x = 0; x < UtilityClass.getFavoritesTweets().size(); x++){
					BaseTweet tweet = (BaseTweet)UtilityClass.getFavoritesTweets().elementAt(x);
					
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
		            		int len = UtilityClass.getFavoritesTweets().size();
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
		            		int len = UtilityClass.getFavoritesTweets().size();
		            		TL.setEmptyString("Connection error :(, please refresh.", DrawStyle.HCENTER);
		            		TL.setSize(len);
		            		TL.invalidate();
		            	}
		            }
			    });
			}
		}
    }
	
	class AvatarFetch extends Thread {
		
		public AvatarFetch()
		{
		}
		
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
					            		int len = UtilityClass.getFavoritesTweets().size();
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
	
	protected void makeMenu(Menu menu, int instance) {
		super.makeMenu(menu, instance);
		
		menu.add(new MenuItem("Refresh", 1, 1){

			public void run() {
				// TODO Auto-generated method stub
				int x = 0;
				if(UtilityClass.getFavoritesTweets().size() > 0)
					x = 2;
				else
					x = 1;
				
				if(m.isVerified()){
					Refresh re = new Refresh(x);
					re.run();
				}
				else{
					try {
						if(m.verifyCredential()){
							re = new Refresh(x);
							re.start();
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
		
		menu.add(new MenuItem("My Favorites", 1, 2){
			
			public void run() {
				// TODO Auto-generated method stub
				TwalayHome.getUiApplication().pushScreen(new Favorites(TwalayHome.m, UtilityClass.getUserToken().getUsername()));
			}			
		});
		
		menu.add(MenuItem.separator(1));
		
		if(UtilityClass.getFavoritesTweets().size() > 0){
			menu.add(new MenuItem("Retweet", 1, 3){
	
				public void run() {
					// TODO Auto-generated method stub
					BaseTweet tweet = (BaseTweet)UtilityClass.getFavoritesTweets().elementAt(TL.getSelectedIndex());
					ReTweet rt = new ReTweet(tweet);
					PleaseWaitPopupScreen.showScreenAndWait(rt, "Retweeting, please wait. :)");
				}			
			});
			
			menu.add(new MenuItem("Reply Tweet", 1, 4){
	
				public void run() {
					// TODO Auto-generated method stub
					BaseTweet tweet = (BaseTweet)UtilityClass.getFavoritesTweets().elementAt(TL.getSelectedIndex());
					QuoteReply qrp = QuoteReply.getInstance(m, tweet, false);
					qrp.show();
				}			
			});
			
			menu.add(new MenuItem("Quote Tweet", 1, 5){
	
				public void run() {
					// TODO Auto-generated method stub
					BaseTweet tweet = (BaseTweet)UtilityClass.getFavoritesTweets().elementAt(TL.getSelectedIndex());
					QuoteReply qrp = QuoteReply.getInstance(m, tweet, true);
					qrp.show();
				}			
			});
			
			menu.add(new MenuItem("Favorite", 1, 6){
	
				public void run() {
					// TODO Auto-generated method stub
					BaseTweet tweet = (BaseTweet)UtilityClass.getFavoritesTweets().elementAt(TL.getSelectedIndex());
					FavTweet fav = new FavTweet(tweet);
					PleaseWaitPopupScreen.showScreenAndWait(fav, "Favoriting, please wait. :)");
				}			
			});
		}
		
		menu.add(MenuItem.separator(2));		
		
		menu.add(new MenuItem("Sign Out", 3, 1){

			public void run() {
				// TODO Auto-generated method stub
				signOut();
			}			
		});
	}
	
	class ReTweet implements Runnable {
		private BaseTweet dtweet;
		
		public ReTweet(BaseTweet dtweet)
		{
			this.dtweet = dtweet;
		}
		
		public void run() {
			TweetER ter = TweetER.getInstance(m);
			try {
				ter.repost(dtweet.getMainTweet());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				UiApplication.getUiApplication().invokeLater(new Runnable() {
		            public void run() {
		            	synchronized (UiApplication.getEventLock()) {
		            		Dialog.alert("Sorry there was a connection error :(, please try again.");
		            	}
		            }
				});
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				UiApplication.getUiApplication().invokeLater(new Runnable() {
		            public void run() {
		            	synchronized (UiApplication.getEventLock()) {
		            		Dialog.alert("There is no internet connection :(, check and please try again.");
		            	}
		            }
				});
			}
		}
    }
  
    class FavTweet implements Runnable {
		private BaseTweet dtweet;
		
		public FavTweet(BaseTweet dtweet)
		{
			this.dtweet = dtweet;
		}
		
		public void run() {
			TweetER ter = TweetER.getInstance(m);
			try {
				ter.favorite(dtweet.getMainTweet());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				UiApplication.getUiApplication().invokeLater(new Runnable() {
		            public void run() {
		            	synchronized (UiApplication.getEventLock()) {
		            		Dialog.alert("Sorry there was a connection error :(, please try again.");
		            	}
		            }
				});
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				UiApplication.getUiApplication().invokeLater(new Runnable() {
		            public void run() {
		            	synchronized (UiApplication.getEventLock()) {
		            		Dialog.alert("There is no internet connection :(, check and please try again.");
		            	}
		            }
				});
			}
		}
    }	
  
	protected boolean keyDown(int keycode, int status) {
	    if (Keypad.key(keycode) == Keypad.KEY_ESCAPE) {
	    	close();
	    }
	    return super.keyDown(keycode, status);	    
	}
	
	
	public void close(){
		System.exit(0);
	}
	
	public void signOut(){
		UtilityClass.signOut();
		close();
	}
}

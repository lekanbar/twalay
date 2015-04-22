package com.dway.twalay;

import me.regexp.RE;
import net.rim.blackberry.api.browser.Browser;
import net.rim.blackberry.api.browser.BrowserSession;
import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;

import java.lang.Thread;
import java.util.Hashtable;
import java.util.Vector;
import java.io.IOException;
import java.io.InputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

import com.twitterapime.model.MetadataSet;
import com.twitterapime.rest.TweetER;
import com.twitterapime.rest.UserAccount;
import com.twitterapime.rest.UserAccountManager;
import com.twitterapime.search.Tweet;

public class ViewTweet extends PopupScreen{
		ButtonField btnTweet, btnRT, btnVoice, btnPic, btnFav;
		GetAvatarThread getAvatar = null;
		BitmapField bmf;
		String theUrl, imgResult, voiceResult;
		static boolean isfirst = true;
		LabelField txtTweetBox;
		LabelField lbl2, lblCount;
		BaseTweet tweet;
		final UserAccountManager m;
	    private static ViewTweet instance = null;
	    EncodedImage img = null;
	    Bitmap image = null;
	    Vector v2;

	  private ViewTweet(final UserAccountManager m, BaseTweet dtweet){
		super(new VerticalFieldManager(Manager.USE_ALL_HEIGHT|Manager.VERTICAL_SCROLL|Manager.NO_HORIZONTAL_SCROLL));    

		this.m = m;
		this.tweet = dtweet;
		
		final UserAccount ua = tweet.getUserAccount();
		
		HorizontalFieldManager hfm1 = new HorizontalFieldManager(Manager.HORIZONTAL_SCROLL);
		bmf = new BitmapField(null, BitmapField.FOCUSABLE){
            protected boolean navigationClick(int status,int time)
            {
            	TweetDetails twtdetails = new TweetDetails(m, "@" + lbl2.getText());
            	TwalayHome.getUiApplication().pushScreen(twtdetails);
                return true;
            }
   
        };
		hfm1.add(bmf);
		
		getAvatar = new GetAvatarThread(ua.getString(MetadataSet.USERACCOUNT_PICTURE_URI_NORMAL));
		getAvatar.start();
		
		VerticalFieldManager vfm = new VerticalFieldManager(Manager.NO_VERTICAL_SCROLL);
		LabelField lbl1 = new LabelField(ua.getString(MetadataSet.USERACCOUNT_NAME));
		lbl1.setFont(lbl1.getFont().derive(Font.BOLD | Font.DROP_SHADOW_RIGHT_EFFECT));
		vfm.add(lbl1);
		
		lbl2 = new LabelField(ua.getString(MetadataSet.USERACCOUNT_USER_NAME), LabelField.FOCUSABLE){
            protected boolean navigationClick(int status,int time)
            {
            	TweetDetails twtdetails = new TweetDetails(m, "@" + this.getText());
            	TwalayHome.getUiApplication().pushScreen(twtdetails);
                return true;
            }
   
        };		
		vfm.add(lbl2);
		hfm1.add(vfm);
		add(hfm1);
		
		add(new SeparatorField());
		
		String tc = tweet.getTweetText();
		txtTweetBox = new LabelField(tc);
		add(txtTweetBox);
		
		add(new SeparatorField());
				
		Vector v = checkForUserNames(tc),
		       v1 = checkForHashTags(tc);
		       v2 = checkForUrls(tc);
		
		for(int i = 0; i < v2.size(); i++){
			v2.setElementAt(getOriginalUrl(v2.elementAt(i).toString()), i);
		}
		
		HorizontalFieldManager hfm8 = new HorizontalFieldManager(Manager.HORIZONTAL_SCROLL);
		HorizontalFieldManager hfm9 = new HorizontalFieldManager(Manager.HORIZONTAL_SCROLL);
		HorizontalFieldManager hfm10 = new HorizontalFieldManager(Manager.HORIZONTAL_SCROLL);
		for(int i = 0; i < v2.size(); i++){
			if(i <= 3){
				boolean[] b = IsUrlPixOrVoice(v2.elementAt(i).toString());
				String str = "";
				if(v2.size() == 1){
					if(b[0]) str = "Play vTweet";
					else if(b[1]) str = "Open Picture Link";
					else str = "Open Link";
				}
				else{
					if(b[0]) str = "Play vTweet " + (i + 1);
					else if(b[1]) str = "Open Picture Link " + (i + 1);
					else str = "Open Link " + (i + 1);
				}
				hfm8.add(new LabelField(str, LabelField.FOCUSABLE){
		            protected boolean navigationClick(int status,int time)
		            {
		            	// TODO Auto-generated method stub
		            	final String tex = this.getText();
		            	UiApplication.getUiApplication().invokeLater(new Runnable() {
				            public void run() {
				            	synchronized (UiApplication.getEventLock()) {
				            		if(UtilityClass.contains(tex, "Picture")){
				            			if(tex.endsWith("k"))
				            				theUrl = v2.elementAt(0).toString();
				            			else
				            				theUrl = v2.elementAt(Integer.parseInt(tex.substring(tex.length() - 1)) - 1).toString();
				            			
				            			ViewPicture vt = ViewPicture.getInstance(theUrl);
										vt.show();
				            		}
				            		else if(UtilityClass.contains(tex, "vTweet")){
				            			if(tex.endsWith("t"))
				            				theUrl = convertToTwalayRealUrl(v2.elementAt(0).toString());
				            			else
				            				theUrl = convertToTwalayRealUrl(v2.elementAt(Integer.parseInt(tex.substring(tex.length() - 1)) - 1).toString());
				            			
				            			UiApplication.getUiApplication().invokeLater(new Runnable() {
				        		            public void run() {
				        		            	synchronized (UiApplication.getEventLock()) {
				        		            		TwalayHome.getUiApplication().pushScreen(new PlayerScreen(m, theUrl));
				        		            	}
				        		            }
				        				});
				            		}
				            		else{
				            			if(tex.endsWith("k"))
				            				theUrl = v2.elementAt(0).toString();
				            			else
				            				theUrl = v2.elementAt(Integer.parseInt(tex.substring(tex.length() - 1)) - 1).toString();
				            			
				            			// Get the default sessionBrowserSession 
				            			BrowserSession browserSession = Browser.getDefaultSession();
				            			// now launch the URL
				            			browserSession.displayPage(theUrl);
				            		}
				            	}
				            }
						});
		                return true;
		            }	   
		        });
			}
			else if(i > 3 && i <= 7){
				boolean[] b = IsUrlPixOrVoice(v2.elementAt(i).toString());
				String str = "";				
				if(b[0]) str = "Play vTweet " + (i + 1);
				else if(b[1]) str = "Open Picture Link " + (i + 1);
				else str = "Open Link " + (i + 1);
				
				hfm9.add(new LabelField(str, LabelField.FOCUSABLE){
		            protected boolean navigationClick(int status,int time)
		            {
		            	// TODO Auto-generated method stub
		            	final String tex = this.getText();
		            	UiApplication.getUiApplication().invokeLater(new Runnable() {
				            public void run() {
				            	synchronized (UiApplication.getEventLock()) {
				            		if(UtilityClass.contains(tex, "Picture")){
				            			if(tex.endsWith("k"))
				            				theUrl = v2.elementAt(0).toString();
				            			else
				            				theUrl = v2.elementAt(Integer.parseInt(tex.substring(tex.length() - 1)) - 1).toString();
				            			
				            			ViewPicture vt = ViewPicture.getInstance(theUrl);
										vt.show();
				            		}
				            		else if(UtilityClass.contains(tex, "vTweet")){
				            			if(tex.endsWith("t"))
				            				theUrl = convertToTwalayRealUrl(v2.elementAt(0).toString());
				            			else
				            				theUrl = convertToTwalayRealUrl(v2.elementAt(Integer.parseInt(tex.substring(tex.length() - 1)) - 1).toString());
				            			
				            			UiApplication.getUiApplication().invokeLater(new Runnable() {
				        		            public void run() {
				        		            	synchronized (UiApplication.getEventLock()) {
				        		            		TwalayHome.getUiApplication().pushScreen(new PlayerScreen(m, theUrl));
				        		            	}
				        		            }
				        				});
				            		}
				            		else{
				            			if(tex.endsWith("k"))
				            				theUrl = v2.elementAt(0).toString();
				            			else
				            				theUrl = v2.elementAt(Integer.parseInt(tex.substring(tex.length() - 1)) - 1).toString();
				            			
				            			// Get the default sessionBrowserSession 
				            			BrowserSession browserSession = Browser.getDefaultSession();
				            			// now launch the URL
				            			browserSession.displayPage(theUrl);
				            		}
				            	}
				            }
						});
		                return true;
		            }	   
		        });
			}
			else{
				boolean[] b = IsUrlPixOrVoice(v2.elementAt(i).toString());
				String str = "";				
				if(b[0]) str = "Play vTweet " + (i + 1);
				else if(b[1]) str = "Open Picture Link " + (i + 1);
				else str = "Open Link " + (i + 1);
				
				hfm10.add(new LabelField(str, LabelField.FOCUSABLE){
		            protected boolean navigationClick(int status,int time)
		            {
		            	// TODO Auto-generated method stub
		            	final String tex = this.getText();
		            	UiApplication.getUiApplication().invokeLater(new Runnable() {
				            public void run() {
				            	synchronized (UiApplication.getEventLock()) {
				            		if(UtilityClass.contains(tex, "Picture")){
				            			if(tex.endsWith("k"))
				            				theUrl = v2.elementAt(0).toString();
				            			else
				            				theUrl = v2.elementAt(Integer.parseInt(tex.substring(tex.length() - 1)) - 1).toString();
				            			
				            			ViewPicture vt = ViewPicture.getInstance(theUrl);
										vt.show();
				            		}
				            		else if(UtilityClass.contains(tex, "vTweet")){
				            			if(tex.endsWith("t"))
				            				theUrl = convertToTwalayRealUrl(v2.elementAt(0).toString());
				            			else
				            				theUrl = convertToTwalayRealUrl(v2.elementAt(Integer.parseInt(tex.substring(tex.length() - 1)) - 1).toString());
				            			
				            			UiApplication.getUiApplication().invokeLater(new Runnable() {
				        		            public void run() {
				        		            	synchronized (UiApplication.getEventLock()) {
				        		            		TwalayHome.getUiApplication().pushScreen(new PlayerScreen(m, theUrl));
				        		            	}
				        		            }
				        				});
				            		}
				            		else{
				            			if(tex.endsWith("k"))
				            				theUrl = v2.elementAt(0).toString();
				            			else
				            				theUrl = v2.elementAt(Integer.parseInt(tex.substring(tex.length() - 1)) - 1).toString();
				            			
				            			// Get the default sessionBrowserSession 
				            			BrowserSession browserSession = Browser.getDefaultSession();
				            			// now launch the URL
				            			browserSession.displayPage(theUrl);
				            		}
				            	}
				            }
						});
		                return true;
		            }	   
		        });
			}
		}
		
		if(!v2.isEmpty()){
			add(hfm8); add(hfm9); add(hfm10);
			add(new SeparatorField());
		}
	
		HorizontalFieldManager hfm2 = new HorizontalFieldManager(Manager.HORIZONTAL_SCROLL);
		HorizontalFieldManager hfm3 = new HorizontalFieldManager(Manager.HORIZONTAL_SCROLL);
		HorizontalFieldManager hfm4 = new HorizontalFieldManager(Manager.HORIZONTAL_SCROLL);
		
		hfm2.add(new LabelField("@" + ua.getString(MetadataSet.USERACCOUNT_USER_NAME), LabelField.FOCUSABLE){
            protected boolean navigationClick(int status,int time)
            {
            	TweetDetails twtdetails = new TweetDetails(m, this.getText());
            	TwalayHome.getUiApplication().pushScreen(twtdetails);
                return true;
            }	   
        });
		for(int i = 0; i < v.size(); i++){
			if(i < 1){
				hfm2.add(new LabelField(v.elementAt(i).toString(), LabelField.FOCUSABLE){
		            protected boolean navigationClick(int status,int time)
		            {
		            	TweetDetails twtdetails = new TweetDetails(m, this.getText());
		            	TwalayHome.getUiApplication().pushScreen(twtdetails);
		                return true;
		            }	   
		        });
			}
			else if(i >= 1 && i < 3){
				hfm3.add(new LabelField(v.elementAt(i).toString(), LabelField.FOCUSABLE){
		            protected boolean navigationClick(int status,int time)
		            {
		            	TweetDetails twtdetails = new TweetDetails(m, this.getText());
		            	TwalayHome.getUiApplication().pushScreen(twtdetails);
		                return true;
		            }	   
		        });
			}
			else{
				hfm4.add(new LabelField(v.elementAt(i).toString(), LabelField.FOCUSABLE){
		            protected boolean navigationClick(int status,int time)
		            {
		            	TweetDetails twtdetails = new TweetDetails(m, this.getText());
		            	TwalayHome.getUiApplication().pushScreen(twtdetails);
		                return true;
		            }	   
		        });
			}
		}
		add(hfm2); add(hfm3); add(hfm4);
		
		if(!v1.isEmpty())
			add(new SeparatorField());
		
		HorizontalFieldManager hfm5 = new HorizontalFieldManager(Manager.HORIZONTAL_SCROLL);
		HorizontalFieldManager hfm6 = new HorizontalFieldManager(Manager.HORIZONTAL_SCROLL);
		HorizontalFieldManager hfm7 = new HorizontalFieldManager(Manager.HORIZONTAL_SCROLL);
		for(int i = 0; i < v1.size(); i++){
			if(i <= 3){
				hfm5.add(new LabelField(v1.elementAt(i).toString(), LabelField.FOCUSABLE){
		            protected boolean navigationClick(int status,int time)
		            {
		            	// TODO Auto-generated method stub
		            	final String str = this.getText();
		            	UiApplication.getUiApplication().invokeLater(new Runnable() {
				            public void run() {
				            	synchronized (UiApplication.getEventLock()) {
				            		TwalayHome.getUiApplication().pushModalScreen(new Search(m, str));
				            	}
				            }
						});
		                return true;
		            }	   
		        });
			}
			else if(i > 3 && i <= 7){
				hfm6.add(new LabelField(v1.elementAt(i).toString(), LabelField.FOCUSABLE){
		            protected boolean navigationClick(int status,int time)
		            {
		            	// TODO Auto-generated method stub
		            	final String str = this.getText();
		            	UiApplication.getUiApplication().invokeLater(new Runnable() {
				            public void run() {
				            	synchronized (UiApplication.getEventLock()) {
				            		TwalayHome.getUiApplication().pushModalScreen(new Search(m, str));
				            	}
				            }
						});
		                return true;
		            }	   
		        });
			}
			else{
				hfm7.add(new LabelField(v1.elementAt(i).toString(), LabelField.FOCUSABLE){
		            protected boolean navigationClick(int status,int time)
		            {
		            	final String str = this.getText();
		            	UiApplication.getUiApplication().invokeLater(new Runnable() {
				            public void run() {
				            	synchronized (UiApplication.getEventLock()) {
				            		TwalayHome.getUiApplication().pushModalScreen(new Search(m, str));//, tms));
				            	}
				            }
						});
		                return true;
		            }	   
		        });
			}
		}
		
		if(!v1.isEmpty())
			add(hfm5); add(hfm6); add(hfm7);
	  }
	  
	  public Vector checkForUrls(String content){
		  String pattern = "http://t.co/(\\w+)[^\\W]";
		  String[] urls = null;
		  Vector v = new Vector();
		  
		  RE regex = new RE(pattern);
		  if(regex.match(content))
			  urls = regex.grep(UtilityClass.split(content, " "));
		  
		  if(urls != null)
			  for(int i = 0; i < urls.length; i++)
				  v.addElement(UtilityClass.cleanUrlString(urls[i]));
		  
		  return v;
	  }
	  
	  public String convertToTwalayRealUrl(String url){
		  String code = url.substring(url.indexOf("v/") + 2);
		  return "http://twalay.com/mp3files/" + code + ".mp3";
	  }
	  
	  public String getOriginalUrl(String url){
		  int resCode;
		  String location = url;
		  
		  try{
			  while (true) {  
			       HttpConnection connection = (HttpConnection) Connector.open(location + ";deviceside=true");
			       connection.setRequestMethod(HttpConnection.GET);
			       connection.setRequestProperty(HttpHeaders.HEADER_CONNECTION, "close");
			       connection.setRequestProperty(HttpHeaders.HEADER_CONTENT_LENGTH, "0");
			       resCode = connection.getResponseCode();
			       if(resCode == HttpConnection.HTTP_TEMP_REDIRECT
			          || resCode == HttpConnection.HTTP_MOVED_TEMP
			          || resCode == HttpConnection.HTTP_MOVED_PERM) {
			            location = connection.getHeaderField("location").trim();
			       } else {
			            break;
			       }
			  }
		  }catch(Exception ex){
			  
		  }
		  
		  return location;
	  }
	  
	  public boolean[] IsUrlPixOrVoice(String url){
		  boolean[] b = new boolean[3]; 
		  if(UtilityClass.contains(url, "twalay")){
			  b[0] = true; b[1] = false; b[2] = false;
			  return b;
		  }
		  else if(UtilityClass.contains(url, "pic.twitter")){
			  b[0] = false; b[1] = true; b[2] = false;
			  return b;
		  }
		  else if(UtilityClass.contains(url, "/photo/")){
			  b[0] = false; b[1] = true; b[2] = false;
			  return b;
		  }
		  else if(UtilityClass.contains(url, "lockerz")){
			  b[0] = false; b[1] = true; b[2] = false;
			  return b;
		  }
		  else if(UtilityClass.contains(url, "instagr")){
			  b[0] = false; b[1] = true; b[2] = false;
			  return b;
		  }
		  else if(UtilityClass.contains(url, "yfrog")){
			  b[0] = false; b[1] = true; b[2] = false;
			  return b;
		  }
		  else if(UtilityClass.contains(url, "twitpic")){
			  b[0] = false; b[1] = true; b[2] = false;
			  return b;
		  }
		  else{
			  b[0] = false; b[1] = false; b[2] = true;
			  return b;
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
				  if(!v.contains(stry))
					  v.addElement(stry);
			  }
		  
		return v;
	  }
	  
	  public Vector checkForHashTags(String content){
		  String[] tags = null;
		  Vector v = new Vector();
		  
		  String pattern = "[#](\\w+)[^\\W]";
		  RE regex = new RE(pattern);
		  if(regex.match(content))
			  tags = regex.grep(UtilityClass.split(content, " "));
		  
		  if(tags != null)
			  for(int i = 0; i < tags.length; i++)
				  v.addElement(UtilityClass.cleanString(tags[i]));
		  
		  return v;
	  }
	  
	  class SendTweet implements Runnable {
			private String message;
			
			public SendTweet(String message)
			{
				this.message = message;
			}
			
			public void run() {
				try {
					Tweet t = new Tweet(message);
					TweetER ter = TweetER.getInstance(m);
					t = ter.post(t);
					
					UiApplication.getUiApplication().invokeLater(new Runnable() {
			            public void run() {
			            	synchronized (UiApplication.getEventLock()) {
			            		clean();
			            	}
			            }
					});
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
	  
	  
	  
	  class TwitPic implements Runnable {
			//private BackToSender screen;
			private String message, fileName;
			private byte[] fileBytes;
			private UserAccountManager m;
			
			public TwitPic(byte[] fileBytes, String fileName, String message, UserAccountManager m)
			{
				this.message = message;
				this.fileBytes = fileBytes;
				this.fileName = fileName;
				this.m = m;
			}
			
			public void run() {
				HttpMultipartRequest hmul = null;
				try {
					String fileExt = fileName.substring(fileName.lastIndexOf('.') + 1);
					String url = "http://bookeong.com/thehikeprice.aspx?message=" + URLEncoder.encode(message, "UTF-8") +
			    		         "&token=" + m.getAccessToken().getToken() + "&secret=" + m.getAccessToken().getSecret();				
				
					hmul = new HttpMultipartRequest(url, new Hashtable(), "media", fileName, "image/" + fileExt, fileBytes);
					hmul.send();					
					UiApplication.getUiApplication().invokeLater(new Runnable() {
			            public void run() {
			            	synchronized (UiApplication.getEventLock()) {
			            		clean();
			            	}
			            }
					});
				} catch (Exception e) {
					// TODO Auto-generated catch block
					UiApplication.getUiApplication().invokeLater(new Runnable() {
			            public void run() {
			            	synchronized (UiApplication.getEventLock()) {
			            		Dialog.alert("Sorry there was a connection error :(, please try again.");
			            	}
			            }
					});
					e.printStackTrace();
				}
				
			}
	  }
	  
	  public static ViewTweet getInstance(UserAccountManager m, BaseTweet tweet){//, TimelineScreen tms) {
	    if ( instance == null )
	      instance = new ViewTweet(m, tweet);//, tms);
	    return instance;
	  }
	  
	  public void show() {
	    UiApplication.getUiApplication().pushModalScreen(ViewTweet.this);
	  }
	  
	  protected boolean keyDown(int keycode, int status) {
	    if (Keypad.key(keycode) == Keypad.KEY_ESCAPE) {
	    	clean();
	    }
	    return super.keyDown(keycode, status);	    
	  }
	  
	  public void clean(){
		  instance = null;
	      close();
	  }
	  
	  final class GetAvatarThread extends Thread 
	  {
	      private String _imgurl;
	      
	      GetAvatarThread(String imageUrl){
	    	  _imgurl = imageUrl;
	      }
	      
	      public void run(){
	    	  HttpConnection connection = null;
			  InputStream in = null;
	    	  try {
	    		  Vector dvec = Home.vec;
	    		  boolean isthere = false;
		  		  for(int i = 0; i < dvec.size(); i++){
		  		     BaseTweet ava = ((BaseTweet)dvec.elementAt(i));
		  	    	   if(tweet.getAuthorHandle().equals(ava.getAuthorHandle())){
		  	    		  if(ava.getIsDownloaded())
		  	    		   	image = ava.getAuthorImage().getBitmap();
		  	    		  else
		  	    		   	image = null;
		  	    		  
		  	    		  isthere = true;
		  	    		  break;
		  	    	   }
		  		  }
	    	      
	    	      if(image == null){
		    		  connection = (HttpConnection) Connector.open(_imgurl);
					  connection.setRequestMethod(HttpConnection.GET);
					  in = connection.openInputStream();
					  byte[] buffer = new byte[(int) connection.getLength()]; 
					  in.read(buffer, 0, buffer.length);
					  img = EncodedImage.createEncodedImage(buffer, 0, buffer.length);
					  
					  if(!isthere){
						  tweet.setAuthorImage(img);
						  tweet.setIsDownloaded(true);
			  			  Home.vec.addElement(tweet);
					  }
					  
					  UiApplication.getUiApplication().invokeLater(new Runnable() {
				            public void run() {
				            	synchronized (UiApplication.getEventLock()) {
				            		bmf.setImage(img);
				            	}
				            }
					  });
	    	      }
	    	      else{
	    	    	  UiApplication.getUiApplication().invokeLater(new Runnable() {
				            public void run() {
				            	synchronized (UiApplication.getEventLock()) {
				            		bmf.setBitmap(image);
				            	}
				            }
					  });
	    	      }
	    	  }catch(final Exception ex){
	    		  ex.printStackTrace();
	    	  }
	      }
	  }
}
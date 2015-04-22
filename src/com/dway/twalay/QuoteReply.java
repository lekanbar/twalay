package com.dway.twalay;

import me.regexp.RE;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.XYEdges;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.component.TextField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.decor.Background;
import net.rim.device.api.ui.decor.BackgroundFactory;
import net.rim.device.api.ui.decor.Border;
import net.rim.device.api.ui.decor.BorderFactory;

import java.util.Hashtable;
import java.util.Vector;
import java.io.IOException;
import java.io.InputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

import com.twitterapime.model.MetadataSet;
import com.twitterapime.rest.TweetER;
import com.twitterapime.rest.UserAccount;
import com.twitterapime.rest.UserAccountManager;
import com.twitterapime.search.Tweet;

public class QuoteReply extends PopupScreen{
		LabelField lblTodo, lblCount, lblTweet;
		ButtonField btnTweet, btnPic, btnVoice;
		String theUrl, imgResult, voiceResult;
		UserAccount ua = null;
		final UserAccountManager m;
		TextField txtTweetBox;
	
	  private static QuoteReply instance = null;

	  private QuoteReply(final UserAccountManager m, BaseTweet tweet, final boolean isquote) {
		super(new VerticalFieldManager(Manager.USE_ALL_HEIGHT|Manager.VERTICAL_SCROLL|Manager.NO_HORIZONTAL_SCROLL));    

		this.m = m;
		ua = tweet.getUserAccount();
		
		lblTodo = new LabelField((isquote ? "Quote" : "Reply" ));
		add(lblTodo);
		add(new SeparatorField());
		
		Vector v = checkForUserNames(tweet.getTweetText());
		String unames = "";
		for(int i = 0; i < v.size(); i++){
			unames += v.elementAt(i) + " ";
		}
		
		txtTweetBox = new TextField();
		XYEdges padding = new XYEdges(5, 5, 5, 5);
		Border roundedBorder = BorderFactory.createRoundedBorder(padding, Color.YELLOWGREEN, Border.STYLE_SOLID);
		Background solidBackground = BackgroundFactory.createSolidBackground(Color.BLACK);
		txtTweetBox.setBorder(roundedBorder);
		txtTweetBox.setBackground(solidBackground);
		txtTweetBox.setText((isquote ? "\"RT @" + ua.getString(MetadataSet.USERACCOUNT_USER_NAME) + ": " + tweet.getTweetText() + "\"" : "@" + ua.getString(MetadataSet.USERACCOUNT_USER_NAME) + " " + unames));
		txtTweetBox.setChangeListener(new FieldChangeListener(){

			public void fieldChanged(Field field, int context) {
				// TODO Auto-generated method stub
				int val = 140 - txtTweetBox.getText().length();
				lblCount.setText("" + val);
				
				if(val < 0){
					btnTweet.setEditable(false);
					btnPic.setEditable(false);
				}
				else{
					btnTweet.setEditable(true);
					btnPic.setEditable(true);
				}
			}
			
		});
		add(txtTweetBox);
		
		lblCount = new LabelField();
		add(lblCount);
		add(new SeparatorField());
		
		btnTweet = new ButtonField("Tweet");
	    btnTweet.setChangeListener(new FieldChangeListener(){
	    	
			public void fieldChanged(Field field, int context) {
				if(imgResult != null){
					try {
						FileConnection fileConnection = (FileConnection)Connector.open(imgResult);
						InputStream inputStream = fileConnection.openInputStream();
						byte[] imageBytes = new byte[(int)fileConnection.fileSize()];
						inputStream.read(imageBytes);
						inputStream.close();
						
						TwitPic tp = new TwitPic(imageBytes, imgResult, txtTweetBox.getText(), m);
						PleaseWaitPopupScreen.showScreenAndWait(tp, "Sending twitpic, please wait. :)");
					}catch(Exception ex){
						
					}
				}
				else{
					String message = txtTweetBox.getText();
					if(!message.equals("")){
						SendTweet st = new SendTweet(message);
						PleaseWaitPopupScreen.showScreenAndWait(st, "Sending tweet, please wait. :)");
					}
					else{
						Dialog.alert("Err ... Sorry you can't tweet empty stuff -__-");
					}
				}
			}
	    });
	    add(btnTweet);
	    
	    HorizontalFieldManager hfm2 = new HorizontalFieldManager(Manager.HORIZONTAL_SCROLL);
	    btnVoice = new ButtonField("Voice");
	    btnVoice.setChangeListener(new FieldChangeListener(){
	    	
			public void fieldChanged(Field field, int context) {
				// TODO Auto-generated method stub
				final VoiceRecorderDialog v = VoiceRecorderDialog.getInstance();
				v.show();
				UiApplication.getUiApplication().invokeLater(new Runnable() {
		            public void run() {
		            	synchronized (UiApplication.getEventLock()) {
		            		voiceResult = TweetPostDialog.voiceResult;
		            		txtTweetBox.setText(txtTweetBox.getText() + " " + voiceResult);
		            	}
		            }
				});
				
				if(TweetPostDialog.voiceResult != null)
					Dialog.alert("Voice note loaded, please enter tweet. :)");
			}		
		});
	    hfm2.add(btnVoice);
	    
	    btnPic = new ButtonField("Pix");
	    btnPic.setChangeListener(new FieldChangeListener(){
	    	
			public void fieldChanged(Field field, int context) {
				// TODO Auto-generated method stub 18
				int val = 140 - (txtTweetBox.getText().length() + 19);
				
				if(val < 0){
					Dialog.alert("Oops, sorry tweet + pix url too long. :(");
				}
				else{
					imgResult = ImagePickerDialog.getInstance().show();
					
					if(imgResult != null)
						Dialog.alert("Image loaded, please enter tweet. :)");
					else
						Dialog.alert("No Image chosen. -_-");
				}
			}			
		});
	    hfm2.add(btnPic);
	    
	    int val = 140 - txtTweetBox.getText().length();
		lblCount.setText("" + val);		
		if(val < 0){
			btnTweet.setEditable(false);
			btnPic.setEditable(false);
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
	  
	  public static QuoteReply getInstance(UserAccountManager m, BaseTweet tweet, boolean isquote) {
	    if ( instance == null )
	      instance = new QuoteReply(m, tweet, isquote);
	    return instance;
	  }
	  
	  public void show() {
	    UiApplication.getUiApplication().pushModalScreen(this);
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
	  
	  class ReplyTweet implements Runnable {
			private String message;
			
			public ReplyTweet(String message)
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
}
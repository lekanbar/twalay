package com.dway.twalay;

import net.rim.device.api.io.Base64OutputStream;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
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
import java.io.IOException;
import java.io.InputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

import com.twitterapime.rest.TweetER;
import com.twitterapime.rest.UserAccountManager;
import com.twitterapime.search.Tweet;

public class TweetPostDialog extends PopupScreen {
		private String imgResult;
		private TextField txtTweetBox;
		private LabelField lblCount;
		private ButtonField btnPic, btnVoice, btnTweet;
		static String voiceResult;
	
	  private static TweetPostDialog instance = null;

	  private TweetPostDialog() {
		super(new VerticalFieldManager(Manager.VERTICAL_SCROLL|Manager.NO_HORIZONTAL_SCROLL));
	    
		LabelField lbl1 = new LabelField("Post Tweet");
		lbl1.setFont(lbl1.getFont().derive(Font.BOLD | Font.DROP_SHADOW_RIGHT_EFFECT));
		add(lbl1);
		add(new SeparatorField());
		
		txtTweetBox = new TextField();
		XYEdges padding = new XYEdges(5, 5, 5, 5);
		Border roundedBorder = BorderFactory.createRoundedBorder(padding, Color.YELLOWGREEN, Border.STYLE_SOLID);
		Background solidBackground = BackgroundFactory.createSolidBackground(Color.BLACK);
		txtTweetBox.setBorder(roundedBorder);
		txtTweetBox.setBackground(solidBackground);
		txtTweetBox.setChangeListener(new FieldChangeListener(){

			public void fieldChanged(Field field, int context) {
				// TODO Auto-generated method stub
				int val = 140 - txtTweetBox.getText().length();
				lblCount.setText("" + val);
				
				if(val < 0){
					btnTweet.setEditable(false);
					btnPic.setEditable(false);
					btnVoice.setEditable(false);
				}
				else{
					btnTweet.setEditable(true);
					btnPic.setEditable(true);
					btnVoice.setEditable(true);
				}
			}
		});
		add(txtTweetBox);
		
		lblCount = new LabelField("140");
		add(lblCount);
		
		HorizontalFieldManager hfm = new HorizontalFieldManager(Manager.HORIZONTAL_SCROLL);
		btnTweet = new ButtonField("Tweet");
	    btnTweet.setChangeListener(new FieldChangeListener(){
	    	
			public void fieldChanged(Field field, int context) {
				// TODO Auto-generated method stub
				if(imgResult != null){
					try {
						FileConnection fileConnection = (FileConnection)Connector.open(imgResult);
						InputStream inputStream = fileConnection.openInputStream();
						byte[] imageBytes = new byte[(int)fileConnection.fileSize()];
						inputStream.read(imageBytes);
						inputStream.close();
						
						TwitPic tp = new TwitPic(imageBytes, imgResult, txtTweetBox.getText(), TwalayHome.m);
						PleaseWaitPopupScreen.showScreenAndWait(tp, "Sending twitpic, please wait. :)");
					}catch(Exception ex){
						Dialog.alert("An unexpected error just occurred, please try again. :(");
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
	    hfm.add(btnTweet);
	    
		btnPic = new ButtonField("Add Pic");
		btnPic.setChangeListener(new FieldChangeListener(){
			public void fieldChanged(Field field, int context) {
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
		hfm.add(btnPic);
		
		btnVoice = new ButtonField("Add Voice");
		btnVoice.setChangeListener(new FieldChangeListener(){
			public void fieldChanged(Field field, int context) {
				final VoiceRecorderDialog v = VoiceRecorderDialog.getInstance();
				v.show();
				UiApplication.getUiApplication().invokeLater(new Runnable() {
		            public void run() {
		            	synchronized (UiApplication.getEventLock()) {
		            		txtTweetBox.setText(voiceResult);
		            	}
		            }
				});
				
				if(voiceResult != null)
					Dialog.alert("Voice note loaded, please enter tweet. :D");
			}
			
		});		
		hfm.add(btnVoice);
		add(hfm);
	  }
	  
	  public String BaseAndEncryptVoiceNote(byte[] b){
		  String encryptedString = "";
		  
		  //base 64 encode the file
		  byte[] encodedfile = null;
		      
		  try {
				encodedfile = Base64OutputStream.encode(b, 0, b.length, false, false);
		  } catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		  }
		      
		  encryptedString = new String(encodedfile);
		  
		  return encryptedString;
	  }
	  
	  public static TweetPostDialog getInstance() {
	    if ( instance == null )
	      instance = new TweetPostDialog();
	    return instance;
	  }
	  
	  protected boolean keyDown(int keycode, int status) {
		    if (Keypad.key(keycode) == Keypad.KEY_ESCAPE) {
		    	instance = null;
		    	close();
		    }
		    return super.keyDown(keycode, status);	    
	  }
	  
	  public void show() {
	    UiApplication.getUiApplication().pushModalScreen(this);
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
				TweetER ter = TweetER.getInstance(TwalayHome.m);
				t = ter.post(t);
				
				UiApplication.getUiApplication().invokeLater(new Runnable() {
		            public void run() {
		            	synchronized (UiApplication.getEventLock()) {
		            		instance = null;
		            		close();
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
				imgResult = null;
				
				UiApplication.getUiApplication().invokeLater(new Runnable() {
		            public void run() {
		            	synchronized (UiApplication.getEventLock()) {
		            		instance = null;
		            		close();
		            	}
		            }
			    });
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
}
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

import java.lang.Thread;
import java.util.Timer;
import java.util.TimerTask;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.media.control.RecordControl;

import org.json.me.JSONException;
import org.json.me.JSONObject;

import com.twitterapime.rest.TweetER;
import com.twitterapime.rest.UserAccountManager;
import com.twitterapime.search.Tweet;

public class TweetVoiceRecorderDialog extends PopupScreen {
		LabelField lbltime;
		ButtonField btnStop, btnRecord, btnPlay, btnSend, btnCancel;
		Timer timer;
		static int timeCount = 0;
		AudioRecorderThread recorder = null;
		String theUrl;
		private TextField txtTweetBox;
		private LabelField lblCount;
		private ButtonField btnTweet;
		private UserAccountManager m;
	
	  private static TweetVoiceRecorderDialog instance = null;

	  private TweetVoiceRecorderDialog(UserAccountManager m) {
		super(new VerticalFieldManager(Manager.VERTICAL_SCROLL|Manager.NO_HORIZONTAL_SCROLL));    

		this.m = m;
		
		LabelField lbl1 = new LabelField("Record n Tweet");
		lbl1.setFont(lbl1.getFont().derive(Font.BOLD | Font.DROP_SHADOW_RIGHT_EFFECT));
		add(lbl1);
		add(new SeparatorField());
		
		timeCount = 0;
		lbltime = new LabelField("00:00/00:30");
		add(lbltime);
		
		recorder = new AudioRecorderThread();
		
	    HorizontalFieldManager hfm = new HorizontalFieldManager(Manager.NO_HORIZONTAL_SCROLL);
	    btnRecord = new ButtonField("Record");
	    btnRecord.setChangeListener(new FieldChangeListener(){
	    	
			public void fieldChanged(Field field, int context) {
				// TODO Auto-generated method stub
				recorder.start();
				
				timer = new Timer();
				timer.scheduleAtFixedRate(new TimerTask(){
					  public void run(){
						  timeCount += 1;
						  final String tim = "00:" + ((String.valueOf(timeCount).length() == 1 ? "0" + timeCount : "" + timeCount)) + "/00:30";
						  UiApplication.getUiApplication().invokeLater(new Runnable() {
					            public void run() {
					            	synchronized (UiApplication.getEventLock()) {
					            		lbltime.setText(tim);
					            	}
					            }
						  });
						  
						  if(timeCount >= 30){
							  timer.cancel();
							  recorder.stop();
							  btnPlay.setEditable(true);
							  btnStop.setEditable(false);
						  }
					  }
				}, 1000, 1000);
			}			
		});
	    
	    btnStop = new ButtonField("Stop");
	    btnStop.setChangeListener(new FieldChangeListener(){
	    	
			public void fieldChanged(Field field, int context) {
				// TODO Auto-generated method stub
				timer.cancel();
				recorder.stop();
				btnPlay.setEditable(true);
				btnStop.setEditable(false);
				btnTweet.setEditable(true);
			}
	    });
	    
	    btnCancel = new ButtonField("Cancel");
	    btnCancel.setChangeListener(new FieldChangeListener(){
	    	
			public void fieldChanged(Field field, int context) {
				// TODO Auto-generated method stub
				theUrl = "nil";
				close();
			}
	    });
	    
	    btnPlay = new ButtonField("Play");
	    btnPlay.setEditable(false);
	    btnPlay.setChangeListener(new FieldChangeListener(){
	    	
			public void fieldChanged(Field field, int context) {
				// TODO Auto-generated method stub
				btnStop.setEditable(false);				
				Thread pThread = new Thread(){
					public void run(){
						Player player = null;
						try {
							ByteArrayInputStream bi = new ByteArrayInputStream(recorder.getVoiceNote());
							player = javax.microedition.media.Manager.createPlayer(bi, "audio/amr");
							player.realize();
							player.prefetch();
							player.start();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (MediaException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				};				
				pThread.start();
			}
	    });
	    
	    hfm.add(btnRecord);
	    hfm.add(btnPlay);
	    hfm.add(btnStop);
	    
	    add(hfm);
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
				int val = 112 - txtTweetBox.getText().length();
				lblCount.setText("" + val);
				
				if(val < 0){
					btnTweet.setEditable(false);
				}
				else{
					byte[] b = recorder.getVoiceNote();
					if(b != null)
						btnTweet.setEditable(true);
				}
			}
		});
		add(txtTweetBox);
		
		lblCount = new LabelField("112");
		add(lblCount);
		
		btnTweet = new ButtonField("Tweet");
		btnTweet.setEditable(false);
	    btnTweet.setChangeListener(new FieldChangeListener(){
	    	
			public void fieldChanged(Field field, int context) {
				// TODO Auto-generated method stub
				GetTwalayUrl gtu = new GetTwalayUrl();
			    PleaseWaitPopupScreen.showScreenAndWait(gtu, "Sending voice tweet, please wait. :)");
			}			
		});
	    add(btnTweet);
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
	  
	  public static TweetVoiceRecorderDialog getInstance(UserAccountManager m) {
	    if ( instance == null )
	      instance = new TweetVoiceRecorderDialog(m);
	    return instance;
	  }
	  
	  protected boolean keyDown(int keycode, int status) {
		    if (Keypad.key(keycode) == Keypad.KEY_ESCAPE) {
		    	instance = null;
		    	close();
		    }
		    return super.keyDown(keycode, status);	    
	  }
	  
	  public String getUrl(){
		  return theUrl;
	  }
	  
	  public void show() {
	    UiApplication.getUiApplication().pushModalScreen(this);
	  }
	  
	  class GetTwalayUrl implements Runnable {
		
		public void run() {
			// TODO Auto-generated method stub
			byte[] b = recorder.getVoiceNote();
			if(b != null){
				HttpMultipartRequest hmul = null;
				
				String url = "http://twalay.com/api/sound/new";				
		
				String thehash = BaseAndEncryptVoiceNote(b);
				
				hmul = new HttpMultipartRequest(url, thehash);
				String fakeUrl = new String(hmul.send(false));
				
				try {
					JSONObject json = new JSONObject(fakeUrl);
					theUrl = json.getString("filename");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println(theUrl);
				
				String comment = txtTweetBox.getText();
				if(comment.equals(""))
					new SendTweet(theUrl).start();
				else
					new SendTweet(comment + " " + theUrl).start();
				
				UiApplication.getUiApplication().invokeLater(new Runnable() {
		            public void run() {
		            	synchronized (UiApplication.getEventLock()) {
		            		instance = null;
		            		close();
		            	}
		            }
				});
			}
		 }
	  }
	  
	  class SendTweet extends Thread {
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
		
	  
	  final class AudioRecorderThread extends Thread 
	  {
	      private Player _player;
	      private RecordControl _rcontrol;
	      private ByteArrayOutputStream _output;
	      private byte _data[];
	      
	      AudioRecorderThread(){
	    	  _data = null;
	      }
	      
	      public void run(){
	    	  try {
	    	       _player = javax.microedition.media.Manager.createPlayer("capture://audio");
	    	       _player.realize();
	    	       _rcontrol = (RecordControl)_player.getControl("RecordControl");
	    	       _output = new ByteArrayOutputStream();
	    	       _rcontrol.setRecordStream(_output);
	    	       _rcontrol.startRecord(); 
	    	       _player.start();    	       
	    	  }catch(final Exception ex){
	    		  ex.printStackTrace();
	    	  }
	      }
	      
	      public void stop() {
	    	try {
    	        _rcontrol.commit();
		    	_data = _output.toByteArray();
		    	_output.close(); 
		    	_player.close();
	    	} catch (final Exception e) {
	    	    e.printStackTrace();
	    	}
	      }

	      public byte[] getVoiceNote(){
	         return _data;
	      }
	  }
}
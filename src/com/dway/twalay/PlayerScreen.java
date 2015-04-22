package com.dway.twalay;

import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.media.PlayerListener;
import javax.microedition.media.control.VolumeControl;

import com.twitterapime.rest.UserAccountManager;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.KeyListener;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.decor.BackgroundFactory;
import rimx.media.streaming.StreamingPlayer;
import rimx.media.streaming.StreamingPlayerListener;

public class PlayerScreen extends MainScreen implements StreamingPlayerListener, KeyListener, TimeLineFieldListener {
	private StreamingPlayer sp;	
	private VolumeControl volC;	
	private PlayerScreen playerScreen;
	private long lastBufferUpdate = System.currentTimeMillis();	

	private String url="";
	private String contentType = "audio/mpeg";
	private int bitRate = 128000;
	private int initBuffer = (bitRate/8)*5;	
	private int restartThreshold = (bitRate/8)*3;
	private int bufferCapacity = 4194304;
	private int bufferLeakSize = bufferCapacity/3;
	private int connectionTimeout = 6000;
	private boolean eventLogEnabled = false;
	private boolean sdLogEnabled = false;
	private int logLevel = 0;
	
	UserAccountManager m;
	
	private AlbumArtField albumArt;
	private Field videoField;
	private TimeLineField timeSeeker;
	private TimeLineField byteSeeker;
	
	private long bufferStartsAt = 0;
	private long len = 0;
	private String status = "Welcome! Click play to start streaming";
	private long nowPlaying = 0;
	private long nowReading = 0;	
	
	public PlayerScreen(final UserAccountManager m, String url){
		super(Screen.NO_HORIZONTAL_SCROLL|Screen.NO_VERTICAL_SCROLL);		
		this.playerScreen = this;
		this.url = url;
		this.m = m;
		timeSeeker = new TimeLineField(Display.getWidth(), 40, 3);
		timeSeeker.setBufferVisible(false);
		timeSeeker.addTimeLineFieldListener(this);
		byteSeeker = new TimeLineField(Display.getWidth(), 40, 16000);
		byteSeeker.addTimeLineFieldListener(this);
		byteSeeker.setFocusable(false);
		albumArt = new AlbumArtField(Bitmap.getBitmapResource("icon.png"), "");
		
		add(albumArt);
		//add(timeSeeker);
		add(byteSeeker);
		
		HorizontalFieldManager hfm = new HorizontalFieldManager(Field.FIELD_HCENTER);
		hfm.setBackground(BackgroundFactory.createSolidBackground(Color.BLACK));
		ButtonField btnPlay = new ButtonField("Play");
		btnPlay.setChangeListener(new FieldChangeListener(){
			public void fieldChanged(Field field, int context) {
				play();
			}
		});		
		hfm.add(btnPlay);
		
		ButtonField btnPause = new ButtonField("Pause");
		btnPause.setChangeListener(new FieldChangeListener(){
			public void fieldChanged(Field field, int context) {
				pause();
			}
		});		
		hfm.add(btnPause);
		
		ButtonField btnStop = new ButtonField("Stop");
		btnStop.setChangeListener(new FieldChangeListener(){
			public void fieldChanged(Field field, int context) {
				stop();
			}
		});		
		hfm.add(btnStop);
		add(hfm);
		
		MenuItem mi1 = new MenuItem("Play", 1, 1) {
			public void run() {
				play();							
			}
		};
		MenuItem mi2 = new MenuItem("Pause", 1, 2) {
			public void run() {
				pause();
			}
		};
		
		MenuItem mi3 = new MenuItem("Resume", 1, 3) {
			public void run() {
				resume();
			}
		};
		
		MenuItem mi4 = new MenuItem("Stop", 1, 4) {
			public void run() {
				stop();
			}
		};	
				
		addMenuItem(mi1);
		addMenuItem(mi2);
		addMenuItem(mi3);
		addMenuItem(mi4);
		addMenuItem(MenuItem.separator(5));
	}
	
	public void play(){
		new Thread(){
			public void run(){
				try{
					if(sp==null){						
						sp = new StreamingPlayer(url, contentType);						
						sp.setBufferCapacity(bufferCapacity);						
						sp.setInitialBuffer(initBuffer);
						sp.setRestartThreshold(restartThreshold);
						sp.setBufferLeakSize(bufferLeakSize);
						sp.setConnectionTimeout(connectionTimeout);
						sp.setLogLevel(logLevel);
						sp.enableLogging(eventLogEnabled, sdLogEnabled);					
						sp.addStreamingPlayerListener(playerScreen);
						sp.realize();
						
						volC = (VolumeControl)sp.getControl("VolumeControl");
						
						if(contentType.toLowerCase().indexOf("audio")!=-1){
							if(!(getField(0)==albumArt)){
								UiApplication.getUiApplication().invokeLater(new Runnable(){
									public void run(){
										replace(videoField, (Field)albumArt);
									} 
								});								
							}
						}						
						sp.start();												
					} else{
						sp.stop();
						sp.close();
						sp = null;
						run();
					}
					
				} catch(Throwable t){
					//log(t.toString());
					UiApplication.getUiApplication().invokeLater(new Runnable() {
			            public void run() {
			            	synchronized (UiApplication.getEventLock()) {
			            		Dialog.alert("No internet connection :(, or the vTweet doesn't exist, check and please try again.");
			                	//System.exit(0);
			            		close();
			            	}
			            }
					});
				}
			}
		}.start();
	}
	
	/*class HomeMenuItem extends MenuItem {
		public HomeMenuItem() {
			super("Home", 10, 10);
		}
		public void run() {
			TwalayHome.getUiApplication().pushScreen(new Home(m));
			close();
		}
	}*/

	public void seek(final long position){		
		new Thread(){
			public void run(){
				try{
					sp.setMediaTime(position*1000000);
				} catch(MediaException me){
					
				}
			}
		}.start();
	}
	
	public void pause(){
		try{
			if(sp!=null){
				sp.stop();
			}
		} catch(Throwable t){
			
		}
	}
	
	public void resume(){
		try{
			if(sp!=null){
				if(sp.getState()!=Player.STARTED){
					sp.start();
				}
			}
		} catch(Throwable t){
			
		}
	}
	
	public void stop(){
		try{
			if(sp!=null){
				sp.close();
				timeSeeker.setBuffered(0, 0);
				timeSeeker.setLength(0);
				timeSeeker.setNow(0);
				byteSeeker.setBuffered(0, 0);
				byteSeeker.setLength(0);
				byteSeeker.setNow(0);
				byteSeeker.setStatus("Closed.");
				timeSeeker.update();
				byteSeeker.update();
			}
		} catch(Throwable t){
			//log(t.toString());
		}
	}
	
	
	
	public void setMediaTitle(String titleText){
		albumArt.setMediaTitle(titleText);
	}
	
	public void setVideoField(Field vField){
		replace(getField(0), vField);
	}
	
	public int getVideoAreaHeight(){
		return albumArt.getHeight();
	}
	public int getVideoAreaWidth(){
		return albumArt.getWidth();
	}

	protected void paintBackground(Graphics graphics) {		
		super.paintBackground(graphics);
		graphics.setColor(Color.BLACK);
		graphics.fillRect(0, 0, getWidth(), getHeight());		
	}
	
	
	/** StreamingPlayerListener Implementation */
	public void bufferStatusChanged(long bufferStartsAt, long len) {
		this.bufferStartsAt = bufferStartsAt;
		this.len = len;		
		
		boolean update = (System.currentTimeMillis()-lastBufferUpdate)>25;
		if(!update)
			return;
		lastBufferUpdate = System.currentTimeMillis();		
		
		timeSeeker.setBuffered(bufferStartsAt, len);
		timeSeeker.setNow(nowPlaying);
		byteSeeker.setBuffered(bufferStartsAt, len);
		byteSeeker.setNow(nowReading);
		byteSeeker.setStatus(this.status);		
		timeSeeker.update();
		byteSeeker.update();
	}

	public void downloadStatusUpdated(final long totalDownloaded) {		
									
	}

	public void feedPaused(final long available) {
		this.status = "Buffering..";
		
		boolean update = (System.currentTimeMillis()-lastBufferUpdate)>25;
		if(!update)
			return;
		lastBufferUpdate = System.currentTimeMillis();
		
		timeSeeker.setBuffered(bufferStartsAt, len);
		timeSeeker.setNow(nowPlaying);
		byteSeeker.setBuffered(bufferStartsAt, len);
		byteSeeker.setNow(nowReading);
		byteSeeker.setStatus(this.status);	
		timeSeeker.update();
	}

	public void feedRestarted(final long available) {
		this.status = "Reading..";
		
		boolean update = (System.currentTimeMillis()-lastBufferUpdate)>25;
		if(!update)
			return;
		lastBufferUpdate = System.currentTimeMillis();		
		
		timeSeeker.setBuffered(bufferStartsAt, len);
		timeSeeker.setNow(nowPlaying);
		byteSeeker.setBuffered(bufferStartsAt, len);
		byteSeeker.setNow(nowReading);
		byteSeeker.setStatus(this.status);	
		timeSeeker.update();
		byteSeeker.update();
	}

	public void initialBufferCompleted(final long available) {
		this.status = "Reading..";
		
		boolean update = (System.currentTimeMillis()-lastBufferUpdate)>25;
		if(!update)
			return;
		lastBufferUpdate = System.currentTimeMillis();		
		
		timeSeeker.setBuffered(bufferStartsAt, len);
		timeSeeker.setNow(nowPlaying);
		byteSeeker.setBuffered(bufferStartsAt, len);
		byteSeeker.setNow(nowReading);
		byteSeeker.setStatus(this.status);	
		timeSeeker.update();
		byteSeeker.update();
	}

	public void playerUpdate(String event, Object eventData) {
		if(event.equals(PlayerListener.END_OF_MEDIA)){
			stop();
		} else if(event.equalsIgnoreCase(PlayerListener.ERROR)){
			Dialog.inform(event+ ": " + eventData);
		}
	}

	public byte[] preprocessData(byte[] bytes, int off, int len) {	
		return null;		
	}
	
	public void nowReading(long now) {
		this.nowReading = now;
		
		boolean update = (System.currentTimeMillis()-lastBufferUpdate)>25;
		if(!update)
			return;
		lastBufferUpdate = System.currentTimeMillis();		
			
		timeSeeker.setBuffered(bufferStartsAt, len);
		timeSeeker.setNow(nowPlaying);
		byteSeeker.setBuffered(bufferStartsAt, len);
		byteSeeker.setNow(nowReading);
		byteSeeker.setStatus(this.status);	
		timeSeeker.update();
		byteSeeker.update();
	}
	
	public void nowPlaying(long now){
		this.nowPlaying = now/1000000;
		
		boolean update = (System.currentTimeMillis()-lastBufferUpdate)>25;
		if(!update)
			return;
		lastBufferUpdate = System.currentTimeMillis();
				
		timeSeeker.setBuffered(bufferStartsAt, len);
		timeSeeker.setNow(nowPlaying);
		byteSeeker.setBuffered(bufferStartsAt, len);
		byteSeeker.setNow(nowReading);
		byteSeeker.setStatus(this.status);	
		timeSeeker.update();
		byteSeeker.update();
	}
	
	public void contentLengthUpdated(long contentLength){
		timeSeeker.setLength(sp.getDuration()/1000000);		
		byteSeeker.setLength(contentLength);
		//albumArt.setMediaTitle(sp.getLocator().substring(sp.getLocator().lastIndexOf('/')+1, sp.getLocator().indexOf(';')));
	}	
	
	public void streamingError(final int code){
		UiApplication.getUiApplication().invokeLater(new Runnable(){
			public void run(){
				switch(code){
					case StreamingPlayerListener.ERROR_DOWNLOADING:
						byteSeeker.setStatus("An error occured while downloading..");
						break;
					case StreamingPlayerListener.ERROR_SEEKING:
						byteSeeker.setStatus("Error seeking..");
						break;
					case StreamingPlayerListener.ERROR_OPENING_CONNECTION:
						byteSeeker.setStatus("Error openning connection..");
						break;
					case StreamingPlayerListener.ERROR_PLAYING_MEDIA:
						byteSeeker.setStatus("Error Playing Media..Closed!");
						break;
				} 
				
			}
		});
	}
		
	private class AlbumArtField extends Field{
		Bitmap bitmap;
		String title;
		public AlbumArtField(Bitmap bitmap, String title){			
			this.bitmap = bitmap;
			this.title = title;
		}
		protected void layout(int width, int height) {			
			setExtent(Display.getWidth(), Display.getHeight()-timeSeeker.getHeight()-byteSeeker.getHeight());
			timeSeeker.setWidth(Display.getWidth());			
			byteSeeker.setWidth(Display.getWidth());
		}

		protected void paintBackground(Graphics graphics) {				
			super.paintBackground(graphics);			
			graphics.setColor(Color.BLACK);
			graphics.fillRect(0, 0, getWidth(), getHeight());
		}				
		
		protected void paint(Graphics graphics){
			if(bitmap!=null)
				graphics.drawBitmap((getWidth()/2)-(bitmap.getWidth()/2), (getHeight()/2)-(bitmap.getHeight()/2), bitmap.getWidth(), bitmap.getHeight(), bitmap, 0, 0);
			graphics.setColor(Color.YELLOWGREEN);
			graphics.drawText(title, (0), (getHeight()/2)+(bitmap.getHeight()/2)+10, DrawStyle.HCENTER, getWidth());
			
		}
		public void setMediaTitle(String titleText){
			title = titleText;
			invalidate();
		}
	}
	
	public boolean keyChar(char key, int status, int time) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean keyDown(int keycode, int time) {
		if(volC!=null){
			int key = Keypad.key(keycode);
			if(key==Keypad.KEY_VOLUME_UP)
				volC.setLevel(volC.getLevel()+10);
			else if(key==Keypad.KEY_VOLUME_DOWN)
				volC.setLevel(volC.getLevel()-10);
		}
		return false;
	}

	public boolean keyRepeat(int keycode, int time) {
		if(volC!=null){
			int key = Keypad.key(keycode);
			if(key==Keypad.KEY_VOLUME_UP)
				volC.setLevel(volC.getLevel()+10);
			else if(key==Keypad.KEY_VOLUME_DOWN)
				volC.setLevel(volC.getLevel()-10);
		}
		return false;		
	}

	public boolean keyStatus(int keycode, int time) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean keyUp(int keycode, int time) {
		// TODO Auto-generated method stub
		return false;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	
	public void setLogLevel(int level){
		this.logLevel = level;
	}
	
	public void setEventLogEnabled(boolean value){
		this.eventLogEnabled = value;
	}
	
	public void setSDLogEnabled(boolean value){
		this.sdLogEnabled = value;
	}
		
	public boolean getEventLogEnabled(){
		return this.eventLogEnabled;
	}
	
	public boolean getSDLogEnabled(){
		return this.sdLogEnabled;
	}

	public int getBitRate() {
		return bitRate;
	}

	public void setBitRate(int bitRate) {
		this.bitRate = bitRate;
	}

	public int getInitBuffer() {
		return initBuffer;
	}

	public void setInitBuffer(int initBuffer) {
		this.initBuffer = initBuffer;
	}

	public int getRestartThreshold() {
		return restartThreshold;
	}

	public void setRestartThreshold(int restartThreshold) {
		this.restartThreshold = restartThreshold;
	}

	public int getBufferCapacity() {
		return bufferCapacity;
	}

	public int getBufferLeakSize() {
		return bufferLeakSize;
	}

	public void setBufferLeakSize(int bufferLeakSize) {
		this.bufferLeakSize = bufferLeakSize;
	}

	public int getConnectionTimeout() {
		return connectionTimeout;
	}

	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	public void setBufferCapacity(int bufferCapacity) {
		this.bufferCapacity = bufferCapacity;
	}

	public boolean onClose() {
		if(sp!=null){
			try {
				sp.close();
			} catch (Throwable e) {				
				return super.onClose();
			}
		}	
		return super.onClose();
	}

	protected boolean onSavePrompt() {
		return true;
	}

}

package com.dway.twalay;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.decor.Background;
import net.rim.device.api.ui.decor.BackgroundFactory;

import java.lang.Thread;
import java.io.InputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

public class ViewPicture extends PopupScreen{
		BitmapField bmf;
		EncodedImage img = null;
	    Bitmap image = null;
	    GetAvatarThread getAvatar = null;
	    Background background;
	    VerticalFieldManager body;
	
	  private static ViewPicture instance = null;
	  
	  private ViewPicture(String url) {
		super(new VerticalFieldManager(Manager.USE_ALL_HEIGHT|Manager.VERTICAL_SCROLL|Manager.NO_HORIZONTAL_SCROLL));
		
		//bmf = new BitmapField();
		//add(bmf);
		background = BackgroundFactory.createBitmapBackground(Bitmap.getBitmapResource("icon.png"), Background.POSITION_X_LEFT, Background.POSITION_Y_BOTTOM, Background.REPEAT_NONE);
		body = new VerticalFieldManager(USE_ALL_WIDTH|USE_ALL_HEIGHT);
		body.setBackground(background);
		add(body);
		
		getAvatar = new GetAvatarThread(url);
		getAvatar.start();
	  }
	  
	  public void clean(){
		  instance = null;
	      close();
	  }
	  
	  public static ViewPicture getInstance(String url) {
	    if ( instance == null )
	      instance = new ViewPicture(url);
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
	    		  connection = (HttpConnection) Connector.open(_imgurl);
				  connection.setRequestMethod(HttpConnection.GET);
				  in = connection.openInputStream();
				  byte[] buffer = new byte[(int) connection.getLength()]; 
				  in.read(buffer, 0, buffer.length);
				  img = EncodedImage.createEncodedImage(buffer, 0, buffer.length);
	    		  
	    		  //UrlToImage img = new UrlToImage(_imgurl);
	    		  //final Bitmap bit = img.getbitmap();
	    		  
				  UiApplication.getUiApplication().invokeLater(new Runnable() {
			            public void run() {
			            	synchronized (UiApplication.getEventLock()) {
			            		//bmf.setImage(img);
			            		background = BackgroundFactory.createBitmapBackground(img.getBitmap(), Background.POSITION_X_LEFT, Background.POSITION_Y_BOTTOM, Background.REPEAT_NONE);

			            		// create a field manager and set its BG to this one
			            		body.setBackground(background);
			            	}
			            }
				  });
	    	  }catch(final Exception ex){
	    		  ex.printStackTrace();
	    	  }
	      }
	  }
}
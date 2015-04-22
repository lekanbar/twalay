package com.dway.twalay;

import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.ui.decor.* ;
import net.rim.device.api.system.*;
import java.util.*;
import net.rim.device.api.ui.container.MainScreen;

/**
 * A class extending the MainScreen class, which provides default standard
 * behavior for BlackBerry GUI applications.
 */
public final class SplashScreen extends MainScreen
{
	private MainScreen next;
	private UiApplication application;
	private Timer timer = new Timer();
	
	
    public SplashScreen(UiApplication ui, MainScreen next)
    {        
    	super(Field.USE_ALL_HEIGHT | Field.FIELD_LEFT);
        
    	try
    	{

    	this.application = ui;
        this.next = next;
        
        paintScreen();
        
        //this.add(new BitmapField(_bitmap)); 
        SplashScreenListener listener = new SplashScreenListener(this);
        this.addKeyListener(listener);
                
        timer.schedule(new CountDown(), 5000);
        application.pushScreen(this);
        }
    	catch (Exception ex)
    	{
    		Dialog.alert(ex.getMessage());
    	}
    }
    
    public void paintScreen()
    {
        Bitmap bgPNG = Bitmap.getBitmapResource("SplashScreen.jpg");        
        Background bg = BackgroundFactory.createBitmapBackground(bgPNG, Background.POSITION_X_CENTER, Background.POSITION_Y_CENTER, Background.REPEAT_NONE);
        getMainManager().setBackground(bg);
    }
    
    public void dismiss() {
        timer.cancel();
        application.popScreen(this);
        application.pushScreen(next);
     }
     private class CountDown extends TimerTask {
        public void run() {
           DismissThread dThread = new DismissThread();
           application.invokeLater(dThread);
        }
     }
     private class DismissThread implements Runnable {
        public void run() {
           dismiss();
        }
     }
     protected boolean navigationClick(int status, int time) {
        dismiss();
        return true;
     }
     protected boolean navigationUnclick(int status, int time) {
        return false;
     }
     protected boolean navigationMovement(int dx, int dy, int status, int time) {
        return false;
     }

    public static class SplashScreenListener implements
       KeyListener {
       private SplashScreen screen;
       public boolean keyChar(char key, int status, int time) {
          //intercept the ESC and MENU key - exit the splash screen
          boolean retval = false;
          switch (key) {
             case Characters.CONTROL_MENU:
             case Characters.ESCAPE:
             screen.dismiss();
             retval = true;
             break;
          }
          return retval;
       }
       public boolean keyDown(int keycode, int time) {
          return false;
       }
       public boolean keyRepeat(int keycode, int time) {
          return false;
       }
       public boolean keyStatus(int keycode, int time) {
          return false;
       }
       public boolean keyUp(int keycode, int time) {
          return false;
       }
       public SplashScreenListener(SplashScreen splash) {
          screen = splash;
       }
    }
}

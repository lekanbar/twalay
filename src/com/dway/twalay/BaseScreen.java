package com.dway.twalay;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.*;
import net.rim.device.api.ui.container.*;
import net.rim.device.api.ui.decor.Background;
import net.rim.device.api.ui.decor.BackgroundFactory;


/**
 * The base screen for all the twitter screens in the twalay Project
 */
public class BaseScreen extends MainScreen 
{  
	
	private String userHandle;
	private ActionMenu _menu;

	//get Twitter handle of current account
	public String getUserHandle()
	{
		return userHandle;	
	}
	
	public ActionMenu getActionMenu()
	{
		return _menu;	
	}
	
	// set Twitter handle of current account
	public void setUserHandle( String value)
	{
		userHandle = value;
	}	        

    public BaseScreen( ) 
    {
    	this("Welcome");
    }    

	public BaseScreen(String _userHandle)
	{		
    	super(Manager.VERTICAL_SCROLLBAR | Manager.VERTICAL_SCROLL);
    	
		userHandle = _userHandle;
		
    	_menu = new ActionMenu(Manager.USE_ALL_WIDTH);
        Bitmap bgPNG = Bitmap.getBitmapResource("bgMenu.png"); 
        Background bg = BackgroundFactory.createBitmapBackground(bgPNG, Background.POSITION_X_LEFT, Background.POSITION_Y_TOP, Background.REPEAT_VERTICAL);
        _menu.setBackground(bg);
        _menu.setMargin(0,0,0,0);

    	paintScreen();
    	
    	HorizontalFieldManager titleBar = new HorizontalFieldManager();
    	
        ColorLabel header = new ColorLabel ("Twalay", 0, Color.WHITE);
        header.setMargin(5,0,5,10);
        header.setBorder(null);
                
        ColorLabel userHandleLabel = new ColorLabel (" - " + userHandle, 0, Color.YELLOW);
        userHandleLabel.setMargin(5,0,5,0);
        userHandleLabel.setBorder(null);

        //change font for labels
        FontFamily fontFamily[] = FontFamily.getFontFamilies();
        Font font = fontFamily[1].getFont(FontFamily.CBTF_FONT, 15);
        header.setFont(font);
        titleBar.setFont(font);
        
        titleBar.add(header);
        titleBar.add(userHandleLabel);
        
        this.add(titleBar);
    	
    	this.add(_menu);
	}
	
	
	public void paintScreen()
    {
        Bitmap bgPNG = Bitmap.getBitmapResource("bg.png");        
        Background bg = BackgroundFactory.createBitmapBackground(bgPNG, Background.POSITION_X_LEFT, Background.POSITION_Y_TOP, Background.REPEAT_VERTICAL);
        getMainManager().setBackground(bg);
    }
    
}

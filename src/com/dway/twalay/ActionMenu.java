package com.dway.twalay;

import net.rim.device.api.system.*;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.container.*;


public class ActionMenu extends HorizontalFieldManager{
	BitmapButtonField b1, b2, b3, b4, b5, b6;
	
	ActionMenu(long ScreenWidth){
		super(ScreenWidth);
		
		JustifiedEvenlySpacedHorizontalFieldManager mgr = new JustifiedEvenlySpacedHorizontalFieldManager (Manager.HORIZONTAL_SCROLL|Manager.HORIZONTAL_SCROLLBAR);
		
		b1 = new BitmapButtonField(Bitmap.getBitmapResource("btnRecord.png"), Bitmap.getBitmapResource("btnRecordHover.png"), Bitmap.getBitmapResource("btnRecordActive.png"), USE_ALL_WIDTH );
		b1.setChangeListener(new FieldChangeListener(){
	    	
			public void fieldChanged(Field field, int context) {
            	TweetVoiceRecorderDialog v = TweetVoiceRecorderDialog.getInstance(TwalayHome.m);
				v.show();
			}
        });		
		
		b2 = new BitmapButtonField(Bitmap.getBitmapResource("btnWrite.png"), Bitmap.getBitmapResource("btnWriteHover.png"), Bitmap.getBitmapResource("btnWriteActive.png"), USE_ALL_WIDTH ) ;
		b2.setChangeListener(new FieldChangeListener(){
	    	
			public void fieldChanged(Field field, int context) {
            	TweetPostDialog v = TweetPostDialog.getInstance();
				v.show();
			}
        });
		
		b3 = new BitmapButtonField(Bitmap.getBitmapResource("btnTimeline.png"), Bitmap.getBitmapResource("btnTimelineHover.png"), Bitmap.getBitmapResource("btnTimelineActive.png"), USE_ALL_WIDTH ) ;
		b3.setChangeListener(new FieldChangeListener(){
	    	
			public void fieldChanged(Field field, int context) {
				TwalayHome.getUiApplication().pushScreen(new Home(TwalayHome.m, UtilityClass.getUserToken().getUsername()));
			}
        });
		
		b4 = new BitmapButtonField(Bitmap.getBitmapResource("btnMentions.png"), Bitmap.getBitmapResource("btnMentionsHover.png"), Bitmap.getBitmapResource("btnMentionsActive.png"), USE_ALL_WIDTH ) ;
		b4.setChangeListener(new FieldChangeListener(){
	    	
			public void fieldChanged(Field field, int context) {
				TwalayHome.getUiApplication().pushScreen(new Mentions(TwalayHome.m, UtilityClass.getUserToken().getUsername()));
			}
        });
		
		b5 = new BitmapButtonField(Bitmap.getBitmapResource("btnMessages.png"), Bitmap.getBitmapResource("btnMessagesHover.png"), Bitmap.getBitmapResource("btnMessagesActive.png"), USE_ALL_WIDTH ) ;
		b5.setChangeListener(new FieldChangeListener(){
	    	
			public void fieldChanged(Field field, int context) {
				TwalayHome.getUiApplication().pushScreen(new DM(TwalayHome.m, UtilityClass.getUserToken().getUsername()));
			}
        });
		
		b6 = new BitmapButtonField(Bitmap.getBitmapResource("btnSearch.png"), Bitmap.getBitmapResource("btnSearchHover.png"), Bitmap.getBitmapResource("btnSearchActive.png"), USE_ALL_WIDTH ) ;
		b6.setChangeListener(new FieldChangeListener(){
	    	
			public void fieldChanged(Field field, int context) {
				TwalayHome.getUiApplication().pushScreen(new Search(TwalayHome.m, ""));
			}
        });

		mgr.add(b1);
        mgr.add(b2);
        mgr.add(b3);
        mgr.add(b4);
        mgr.add(b5);
        mgr.add(b6);
        mgr.setMargin(0,10,0,10);
        
		add(mgr);
		
		//this.setExtent( Manager.USE_ALL_WIDTH, Manager.USE_ALL_HEIGHT);
        setMargin( 0, 0, 0, 0 );
	}
}



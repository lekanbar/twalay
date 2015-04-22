/*
 * TwitterAPIMERIMOauthSample.java
 * 06/10/2011
 * Twitter API Micro Edition
 * Copyright(c) Ernandes Mourao Junior (ernandes@gmail.com)
 * All rights reserved
 */
package com.dway.twalay;

import java.util.Hashtable;

import impl.rim.com.twitterapime.xauth.ui.BrowserContentManagerOAuthDialogWrapper;
import net.rim.device.api.browser.field.BrowserContentManager;
import net.rim.device.api.browser.field.RenderingOptions;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.UiEngine;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.GaugeField;
import net.rim.device.api.ui.container.MainScreen;

import com.twitterapime.rest.Credential;
import com.twitterapime.rest.UserAccountManager;
import com.twitterapime.xauth.Token;
import com.twitterapime.xauth.ui.OAuthDialogListener;
import com.twitterapime.xauth.ui.OAuthDialogWrapper;

/**
 * @author ernandes@gmail.com
 */
public class TwalayHome extends UiApplication {
	public PersistentObject pob;
	public Hashtable db;
	static final long KEY = 0x7824c240795aa705L;
	static UserAccountManager m;
	
	GaugeField gauge;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TwalayHome app = new TwalayHome();
		app.enterEventDispatcher();
	}

	/**
	 * 
	 */
	public TwalayHome() {
		pob = PersistentStore.getPersistentObject(KEY);
		Hashtable db = (Hashtable)pob.getContents();
		
		if(db == null || db.get("tokenTable") == null){			
			pushScreen(new BrowserFieldScreen(pob));
		}
		else{
			Credential c = new Credential(UtilityClass.getConsumerKey(), UtilityClass.getConsumerSecret(), UtilityClass.getUserToken());
			m = UserAccountManager.getInstance(c);
			
			//pushScreen(new TimelineScreen(m));
			new SplashScreen(this, new Home(m, UtilityClass.getUserToken().getUsername()));
		}
	}
	
	public static void showDialog(String msg, final int todo){
		synchronized(Application.getEventLock()){    
			UiEngine ui = Ui.getUiEngine();
			Screen screen = new Dialog(Dialog.D_OK, msg, Dialog.OK, Bitmap.getPredefinedBitmap(Bitmap.EXCLAMATION), Manager.VERTICAL_SCROLL);
			screen.setChangeListener(new FieldChangeListener(){

				public void fieldChanged(Field arg0, int arg1) {
					// TODO Auto-generated method stub
					
				}
				
			});
			
	        ui.pushGlobalScreen(screen, 1, UiEngine.GLOBAL_QUEUE);//
	        
			if(todo == 2)
				System.exit(0);
	    }
	}
}

/**
 * @author ernandes@gmail.com
 */
class BrowserFieldScreen extends MainScreen implements OAuthDialogListener {
	private PersistentObject pob;
	private Hashtable db;
	private GaugeField gauge;

	/**
	 * 
	 */
	public BrowserFieldScreen(PersistentObject pob1) {
		pob = pob1;
		gauge = new GaugeField("Please wait...", 0, 100, 20, GaugeField.LABEL_AS_PROGRESS);
		setTitle("Twalay");
		add(gauge);
		
		BrowserContentManager browserMngr = new BrowserContentManager(0);
		RenderingOptions rendOptions = browserMngr.getRenderingSession().getRenderingOptions();
		rendOptions.setProperty(
			RenderingOptions.CORE_OPTIONS_GUID,
			RenderingOptions.SHOW_IMAGES_IN_HTML,
			false);
		rendOptions.setProperty(
				RenderingOptions.IMAGE_OPTIONS_GUID,
				RenderingOptions.SHOW_IMAGES_IN_WML,
				false);
		
		add(browserMngr);
		gauge.setValue(40);
		
		OAuthDialogWrapper pageWrapper = 
			new BrowserContentManagerOAuthDialogWrapper(browserMngr);
		
		pageWrapper.setConsumerKey(UtilityClass.getConsumerKey());
		pageWrapper.setConsumerSecret(UtilityClass.getConsumerSecret());
		pageWrapper.setCallbackUrl(UtilityClass.getCallBackUrl());
		pageWrapper.setOAuthListener(this);
		gauge.setValue(60);
		pageWrapper.login();
		gauge.setValue(80);
	}

	/**
	 * @see com.twitterapime.xauth.ui.OAuthDialogListener#onAuthorize(com.twitterapime.xauth.Token)
	 */
	public void onAuthorize(Token token) {
		Credential c = new Credential(UtilityClass.getConsumerKey(), UtilityClass.getConsumerSecret(), token);
		TwalayHome.m = UserAccountManager.getInstance(c);
		db = new Hashtable();
		gauge.setValue(100); gauge.setLabel("Done");
		
		try {
			if (TwalayHome.m.verifyCredential()) {
				db.put("tokenTable", new String[]{token.getToken(), token.getSecret(), token.getUserId(), token.getUsername()});
				pob.setContents(db);
				pob.commit();
				gauge.setValue(0); gauge.setLabel("");
				
				final String stry = token.getUsername();
				//TwalayHome.showDialog("Authorization successful. You are welcome! :)", 1);
				UiApplication.getUiApplication().invokeLater(new Runnable() {
		            public void run() {
		            	synchronized (UiApplication.getEventLock()) {
		            		TwalayHome.getUiApplication().pushScreen(new Home(TwalayHome.m, stry));
		            	}
		            }
				});
			}
			else{
				TwalayHome.showDialog("Oops! Sorry, there was a problem with authorizing your account :(", 2);
			}
		} catch (Exception e) {
			TwalayHome.showDialog("Oops! Sorry, something went awfully wrong there :'(. Please close the app and try again.", 2);
		}
	}

	/**
	 * @see com.twitterapime.xauth.ui.OAuthDialogListener#onAccessDenied(java.lang.String)
	 */
	public void onAccessDenied(String message) {
		TwalayHome.showDialog("Oops! Sorry, access to your account was denied :(. Visit twitter.com to resolve.", 2);
	}

	/**
	 * @see com.twitterapime.xauth.ui.OAuthDialogListener#onFail(java.lang.String,
	 *      java.lang.String)
	 */
	public void onFail(String error, String message) {
		TwalayHome.showDialog("Oops! Sorry, access to your account failed :(. Visit twitter.com to resolve.", 2);		
	}
}

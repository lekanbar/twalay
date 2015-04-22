package com.dway.twalay;

import net.rim.device.api.system.Bitmap;

public class UserAvatar {
	Bitmap img;
	boolean isDownloaded;
	String imageUrl;
	String authorName;
	
	public UserAvatar(){
		img = null;
		imageUrl = "";
		authorName = "";
		isDownloaded = false;
	}
	
	public void setImage(Bitmap value){
		img = value;
	}
	public Bitmap getImage(){
		return img;
	}
	
	public void setIsDownloaded(boolean value){
		isDownloaded = value;
	}
	public boolean getIsDownloaded(){
		return isDownloaded;
	}
	
	public void setImageUrl(String value){
		imageUrl = value;
	}
	public String getImageUrl(){
		return imageUrl;
	}
	
	public void setAuthorName(String value){
		authorName = value;
	}
	public String getAuthorName(){
		return authorName;
	}
}

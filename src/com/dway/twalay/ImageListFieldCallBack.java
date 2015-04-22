package com.dway.twalay;

import java.io.UnsupportedEncodingException;
import java.util.Vector;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;

public class ImageListFieldCallBack implements ListFieldCallback {
  private Vector listitems = new Vector();
  private Vector thumbnail = new Vector();
  private ListField list;
  
  private int imageWidth = 50;
  private int imageHeight = 50;
  private int padding = 2;
  private int ypadding = 2;

  public ImageListFieldCallBack(ListField list) {
    this.list = list;
    this.list.setCallback(this);
  }
  
  public void clear() {
    listitems.removeAllElements();
    thumbnail.removeAllElements();
    synchronized(Application.getEventLock()) {
      list.setSize(0);
    }
  }

  public int add( Bitmap bitmap, Object text ) {
    listitems.addElement(text);
    thumbnail.addElement(bitmap);
    synchronized(Application.getEventLock()) {
      int size = listitems.size()-1;
      list.insert(listitems.size()-1);
      return size;
    }
  }
  
  public void insert( int index, Bitmap bitmap, Object text ) {
    listitems.insertElementAt(text, index);
    thumbnail.insertElementAt(bitmap, index);
    synchronized(Application.getEventLock()) {
      list.insert(index);
    }
  }
  
  public void delete( Object o ) {
    int index = listitems.indexOf( o );
    if ( index >= 0 ) {
      delete(index);
    }
  }
  
  public void delete( int index ) {
    listitems.removeElementAt(index);
    thumbnail.removeElementAt(index);
    synchronized(Application.getEventLock()) {
      list.delete(index);
    }
  }
  
  public void update( int index, Bitmap bitmap, Object text ) {
    listitems.setElementAt(text, index);
    thumbnail.setElementAt(bitmap, index);
    synchronized(Application.getEventLock()) {
      list.invalidate(index);
    }
  }
  
  public int size() {
    return thumbnail.size();
  }

  public void drawListRow(ListField listField, Graphics g, int index, int y, int w) {
     Object object = listitems.elementAt(index);
     Bitmap thumb = (Bitmap) thumbnail.elementAt(index); 
     
     String filepath = object.toString();
     String text = filepath;
     int pos = filepath.lastIndexOf('/');
     
     if ( filepath.endsWith("/") ) {
       pos = filepath.lastIndexOf('/', pos-1);
     }
     
     try {
      text = TextUtils.urlDecode( new String( filepath.substring(pos+1) ));
     } catch (UnsupportedEncodingException e) {
      //Log.error("");
     }
     
     int padding = getPadding();
     int ypadding = getYPadding();
     int imageWidth = getImageWidth();

     g.drawText(text, imageWidth + padding, y+ypadding, DrawStyle.LEADING | DrawStyle.ELLIPSIS, w - imageWidth - padding);
     g.drawBitmap(0, y+ypadding, thumb.getWidth(), thumb.getHeight(), thumb, 0, 0);
     
  }

  public Object get(ListField listField, int index) {
    return listitems.elementAt(index);
  }

  public int getPreferredWidth(ListField listField) {
    return listField.getPreferredWidth();
  }

  public int indexOfList(ListField listField, String prefix, int start) {
    return -1;
  }

  public int getImageWidth() {
    return imageWidth;
  }

  public void setImageWidth(int imageWidth) {
    this.imageWidth = imageWidth;
  }

  public int getImageHeight() {
    return imageHeight;
  }

  public void setImageHeight(int imageHeight) {
    this.imageHeight = imageHeight;
  }

  public int getPadding() {
    return padding;
  }

  public void setPadding(int padding) {
    this.padding = padding;
  }
  
  public int getYPadding() {
    return ypadding;
  }

  public void setYPadding(int padding) {
    this.ypadding = padding;
  }
}

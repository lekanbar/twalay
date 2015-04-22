package com.dway.twalay;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.io.file.FileSystemRegistry;

import net.rim.device.api.math.Fixed32;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.TouchEvent;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.util.Comparator;
import net.rim.device.api.util.SimpleSortingVector;

public class ImagePickerDialog extends PopupScreen {
	  private static final String FILE_SEPARATOR = System.getProperty("file.separator");
	  private static final String PARENT_DIRECTORY = "..";
	  private static final String FILE_PROTOCOL = "file://";
	  private static ImagePickerDialog instance = null;
	  
	  private ListField listField;
	  private ImageListFieldCallBack listFieldCallback;
	  private String currentDir = null;
	  private String rootDir = null;
	  protected volatile boolean keepRunning = true;
	  
	  private Bitmap folderBitmap = Bitmap.getBitmapResource("folder.png");
	  private Bitmap fileBitmap = Bitmap.getBitmapResource("file.png");
	  private Bitmap defaultBitmap = Bitmap.getPredefinedBitmap(Bitmap.QUESTION);
	  private BitmapField preview = new BitmapField(defaultBitmap);
	  
	  private String result = null;
	  private PreviewWorker previewWorker = null;
	  
	  class PreviewWorker implements Runnable {
	    private Vector queue = new Vector();
	    private Bitmap bitmap;
	    private volatile Thread thread = null;
	    
	    public void start() {
	      thread = new Thread(this);
	      thread.setPriority(Thread.MIN_PRIORITY);
	      thread.start();
	    }
	    
	    public void stop() {
	      thread = null;
	    }
	    
	    public void preview( String filename ) {
	      queue.addElement(filename);
	    }
	    
	    public void run() {
	      while (thread != null) {
	        if ( queue.isEmpty() ) {
	          try {
	            Thread.sleep(100);
	          } catch (InterruptedException e) {
	            e.printStackTrace();
	          }
	          continue;
	        }
	        
	        String selected = (String)queue.elementAt(queue.size()-1);
	        if ( selected == null ) {
	          try {
	            Thread.sleep(100);
	          } catch (InterruptedException e) {
	            //e.printStackTrace();
	          }
	          continue;
	        }
	        
	        queue.removeAllElements();
	        
	        try {
	          bitmap = readBitmapFromFile(selected, defaultBitmap.getWidth(), defaultBitmap.getHeight());
	        } catch (IOException e) {
	          bitmap = defaultBitmap;
	        }
	        
	        UiApplication.getUiApplication().invokeLater(new Runnable() {
	          public void run() {
	            preview.setBitmap(bitmap);
	          }
	        });
	        
	        try {
	          Thread.sleep(50);
	        } catch (InterruptedException e) {
	          e.printStackTrace();
	        }
	      }
	    }
	  }
	  
	  private ImagePickerDialog() {
		super(new HorizontalFieldManager(Field.USE_ALL_HEIGHT|Field.USE_ALL_WIDTH|Manager.NO_HORIZONTAL_SCROLL));
	    
	    rootDir = "file:///" + getCurrentRoot();
	    int height = Font.getDefault().getHeight();
	    int thumbSize = Math.max(height, 24);
	    int ypadding = 2;
	    int padding = 2;
	    
	    previewWorker = new PreviewWorker();
	    
	    listField = new ListField() {
	      //protected boolean navigationClick(int status, int time) {
	        //selectField();
	        //return super.navigationClick(status, time);
	      //}

	      protected int moveFocus(int amount, int status, int time) {
	        int r = super.moveFocus(amount, status, time);
	        previewSelected();
	        return r;
	      }
	      
	      protected boolean navigationClick(int status, int time) {
	          this.fieldChangeNotify(2);
	          selectField();
	          return true;
	      }

	      protected boolean touchEvent(TouchEvent message) {
	          int x = message.getX( 1 );
	          int y = message.getY( 1 );
	          if( x < 0 || y < 0 || x > getExtent().width || y > getExtent().height ) {
	                  // Outside the field
	                  return false;
	          }
	          // If click, process Field changed
	          if ( message.getEvent() == TouchEvent.CLICK ) {
	              this.fieldChangeNotify(2);
	              return true;
	          }
	          return super.touchEvent(message);
	      }

	    };
	    listField.setRowHeight(thumbSize+(ypadding*2));
	    

	    VerticalFieldManager vfm = new VerticalFieldManager( Manager.USE_ALL_HEIGHT|Manager.VERTICAL_SCROLL|Manager.NO_HORIZONTAL_SCROLL) {
	      protected void sublayout(int width, int height) {
	        //Log.info( Display.getWidth() + ", " + getManager().getWidth() + ", " + defaultBitmap.getWidth() );
	        
	        int w = getManager().getWidth() - ( 20 + defaultBitmap.getWidth() + preview.getMarginLeft() + preview.getMarginRight());
	        super.sublayout(w, height);
	        setExtent(w, height);
	      }
	    };
	    vfm.add(listField);
	    
	    add(vfm);
	    add(preview);
	    preview.setPadding(5, 5, 5, 5);
	    
	    
	    listFieldCallback = new ImageListFieldCallBack(listField);
	    listFieldCallback.setImageHeight(thumbSize);
	    listFieldCallback.setImageWidth(thumbSize);
	    listFieldCallback.setPadding(padding);
	    listFieldCallback.setYPadding(ypadding);
	  }
	  
	  public static ImagePickerDialog getInstance() {
	    if ( instance == null )
	      instance = new ImagePickerDialog();
	    return instance;
	  }
	  
	  public String show() {
	    listFiles( rootDir );
	    
	    //started
	    result = null;
	    previewWorker.start();
	    
	    UiApplication.getUiApplication().pushModalScreen(this);
	    
	    //dismissed
	    previewWorker.stop();

	    return result;
	  }
	  
	  protected boolean keyDown(int keycode, int status) {
	    if (Keypad.key(keycode) == Keypad.KEY_ESCAPE) {
	      result = null;
	      if ( currentDir.equals(rootDir) ) {
	        close();
	      } else {
	        int pos = currentDir.lastIndexOf('/', currentDir.length()-2);
	        String dir = new String( currentDir.substring(0, pos+1) );
	        listFiles( dir );
	      }
	    }
	    return super.keyDown(keycode, status);
	  }
	  
	  protected void previewSelected() {
	    int index = listField.getSelectedIndex();
	    int size = listField.getSize();
	    if ( index >= 0 && index < size ) {
	      final String selected = (String)listFieldCallback.get(listField, index);
	      if ( selected.endsWith(".gif") || selected.endsWith(".png") || selected.endsWith(".jpg") || selected.endsWith(".bmp") ) {
	        previewWorker.preview(selected);
	        ImagePickerDialog.this.result = selected;
	      } else {
	        ImagePickerDialog.this.result = null;
	      }
	    } else {
	      UiApplication.getUiApplication().invokeLater(new Runnable() {
	        public void run() {
	          preview.setBitmap(defaultBitmap);
	        }
	      });
	    }
	  }
	  
	  protected Thread listThread = null;
	  
	  protected void listFiles(String dir) {
	    keepRunning = false;
	    if ( listThread != null ) {
	      try { listThread.join(); }
	      catch(InterruptedException ie) {}
	    }
	    
	    currentDir = dir;
	    keepRunning = true;
	    
	    UiApplication.getUiApplication().invokeLater(new Runnable() {
	      public void run() {
	        preview.setBitmap(defaultBitmap);
	      }
	    });
	    
	    listThread = new Thread() {
	      public void run() {
	        FileConnection fc = null;
	        try {
	          fc = (FileConnection)Connector.open(currentDir, Connector.READ);
	          if ( fc.exists() ) {
	            recurseDirectory( fc );
	          }
	        } catch(IOException ioe) {
	          ioe.printStackTrace();
	        } finally {
	          if ( fc != null ) {
	            try { fc.close(); }
	            catch(Exception ex) {}
	          }
	        }
	      }
	    };
	    listThread.setPriority(Thread.MIN_PRIORITY);
	    listThread.start();
	  }
	  
	  protected void recurseDirectory(FileConnection dir) throws IOException {
	    listFieldCallback.clear();
	    
	    if ( !currentDir.equals(rootDir) ) {
	      listFieldCallback.add(folderBitmap, PARENT_DIRECTORY);
	    }
	    
	    SimpleSortingVector sv = new SimpleSortingVector();
	    sv.setSortComparator(new Comparator() {
	      public int compare(Object arg0, Object arg1) {
	        FileItem f1 = (FileItem)arg0;
	        FileItem f2 = (FileItem)arg1;
	        
	        if ( f1.type < f2.type ) {
	          return -1;
	        } else if ( f1.type > f2.type ) {
	          return 1;
	        } else if ( f1.timestamp < f2.timestamp ) {
	          return 1;
	        } else if ( f1.timestamp > f2.timestamp ) {
	          return -1;
	        } else {
	          return f1.name.compareTo(f2.name);
	        }
	      }
	    });
	    sv.setSort(false);
	    
	    Enumeration list = dir.list();
	    while (list.hasMoreElements() && keepRunning) {
	      String file = (String) list.nextElement();
	      String fl = file.toLowerCase();
	      if (fl.endsWith(FILE_SEPARATOR) || fl.endsWith(".jpg") || fl.endsWith(".gif") || fl.endsWith(".bmp") || fl.endsWith(".png") ) {
	        try {
	          StringBuffer filename = new StringBuffer();
	          filename.append(FILE_PROTOCOL).append(dir.getPath()).append(dir.getName()).append(file);
	          FileItem item = new FileItem(filename.toString());
	          sv.addElement(item);
	        } catch(Exception ex) {
	          //
	        }
	      }
	      
	      /*
	      if (file.endsWith(FILE_SEPARATOR)) {
	        StringBuffer filename = new StringBuffer();
	        filename.append(FILE_PROTOCOL).append(dir.getPath()).append(dir.getName()).append(file);
	        listFieldCallback.add(folderBitmap, filename.toString());
	      } else {
	        String fl = file.toLowerCase();
	        if ( fl.endsWith(".jpg") || fl.endsWith(".gif") || fl.endsWith(".bmp") || fl.endsWith(".png") ) {
	          StringBuffer filename = new StringBuffer();
	          filename.append(FILE_PROTOCOL).append(dir.getPath()).append(dir.getName()).append(file);
	          listFieldCallback.add(fileBitmap, filename.toString());
	        }
	      }
	      */
	    }
	    
	    sv.reSort();
	    Enumeration sorted = sv.elements();
	    while( sorted.hasMoreElements() && keepRunning ) {
	      FileItem item = (FileItem)sorted.nextElement();
	      if ( item.type == 0 ) {
	        listFieldCallback.add(folderBitmap, item.name);
	      } else {
	        listFieldCallback.add(fileBitmap, item.name);
	      }
	    }
	  }
	  
	  class FileItem {
	    public int type;
	    public String name;
	    public long timestamp;
	    
	    public FileItem(String name) throws IOException {
	      this.name = name;
	      if ( name.endsWith(FILE_SEPARATOR) ) {
	        type = 0;
	      } else {
	        type = 1;
	        FileConnection fc = null;
	        try {
	          fc = (FileConnection)Connector.open(name, Connector.READ);
	          timestamp = fc.lastModified();
	        } finally {
	          if (fc != null) fc.close();
	        }
	      }
	    }
	  }
	  
	  protected void selectField() {
	    int index = listField.getSelectedIndex();
	    int size = listField.getSize();
	    if ( index >= 0 && index < size ) {
	      String selected = (String)listFieldCallback.get(listField, index);
	      if ( selected.endsWith(FILE_SEPARATOR) ) {
	        listFiles( selected );
	      } else if ( selected.equals(PARENT_DIRECTORY) ) {
	        int pos = currentDir.lastIndexOf('/', currentDir.length()-2);
	        String dir = new String( currentDir.substring(0, pos+1) );
	        listFiles( dir );
	      } else {
	        close();
	      }
	    }
	  }
	    
	  public static String getCurrentRoot() {
	    Enumeration rootEnum = FileSystemRegistry.listRoots();
	    String currentRoot = null;
	    while (rootEnum.hasMoreElements()) {
	      String root = (String) rootEnum.nextElement();
	      if (root.endsWith("Media Card/") || root.endsWith("Device Memory/") || root.endsWith("store/") || root.endsWith("CFCard/") || root.endsWith("SDCard/") || root.endsWith("MemoryStick/")) {
	        currentRoot = root;
	        break;
	      }
	    }
	    return currentRoot;
	  }
	    
	  public static Bitmap readBitmapFromFile(String filename, int width, int height) throws IOException {
	    Bitmap bitmap = null;
	    byte[] data;
	    if (filename != null) {
	      FileConnection file = (FileConnection) Connector.open(filename, Connector.READ);
	      int fileSize = (int) file.fileSize();
	      if (fileSize > 0) {
	        data = new byte[fileSize];
	        InputStream input = file.openInputStream();
	        input.read(data);
	        input.close();
	        
	        EncodedImage image = EncodedImage.createEncodedImage(data, 0, data.length);
	        EncodedImage result = resizeImage(image, width, height);
	        bitmap = result.getBitmap();
	      }
	      file.close();
	    }
	    return bitmap;
	  }
	    
	  public static EncodedImage resizeImage(EncodedImage image, int width, int height) {
	    if (image == null || (image.getWidth() == width && image.getHeight() == height)) {
	      return image;
	      }
	    
	      double scaleHeight, scaleWidth;

	      if (image.getWidth() > width && image.getHeight() > height) {  //actual image is bigger than scale size
	        if (image.getWidth() > image.getHeight()) {  //actual image width is more that height then scale with width
	          scaleWidth = width;
	          scaleHeight = (double)width / image.getWidth() * image.getHeight();
	        } else { //scale with height
	          scaleHeight = height;
	          scaleWidth = (double)height / image.getHeight() * image.getWidth();
	        }
	      } else if (width < image.getWidth()) { //scale with scale width or height
	        scaleWidth = width;
	        scaleHeight = (double)width / image.getWidth() * image.getHeight();
	      } else {
	        scaleHeight = height;
	        scaleWidth = (double)height / image.getHeight() * image.getWidth();
	      }
	      int w = Fixed32.div(Fixed32.toFP(image.getWidth()), Fixed32.toFP((int)scaleWidth));
	      int h = Fixed32.div(Fixed32.toFP(image.getHeight()), Fixed32.toFP((int)scaleHeight));
	      return image.scaleImage32(w, h);
	  }

	}
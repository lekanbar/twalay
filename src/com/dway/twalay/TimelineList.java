package com.dway.twalay;

import java.util.Vector;

import net.rim.device.api.system.Display;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.*;

public class TimelineList extends ListField {
	
	private Menu _contextMenu = new Menu();
	private Vector _data = null;
	private ListEngine callback = null;
	private Font font = null;
	private int currentPosition = 0;
	
	public TimelineList (Vector content) {
	    this._data = content;
	    initCallbackListening();
	    font = this.getFont().derive(Font.PLAIN, 13);
	    this.setFont(font);
	    this.setRowHeight(-3);
	}

	private void initCallbackListening() {
	  callback = new ListEngine();
	  this.setCallback(callback);
	}
	
	public void addToContextMenu(MenuItem menuItem)
	{
		if (menuItem != null)
			_contextMenu.add(menuItem);		
	}
	
	protected boolean navigationClick(int status, int time) {
	      this.fieldChangeNotify(2);
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

	public ContextMenu getContextMenu()
	{
		ContextMenu newMenu = super.getContextMenu();
		newMenu.clear();
		
		int _size = _contextMenu.getSize();
		
		for (int i=0; i<_size; i++)
		{
			newMenu.addItem(_contextMenu.getItem(i));		
		}
		
		return(newMenu);		
	}
	
	private class ListEngine implements ListFieldCallback {
		
		
		/** actionListener interface 
		public interface ActionListenerIF {

		void actionPerformed(Object source);

		}//end interface ActionListenerIF
	*/
	      //private Vector _data;
		  //private ListField _view;
		  private int txtPad= 5;
		  private int pad = 10;
		  private int aviW = 50, aviH = 50; 
			
		  private FontFamily fontFamily[] = FontFamily.getFontFamilies();
		  private Font handleFont = fontFamily[1].getFont(Font.BOLD, 20);
		  private Font dateFont = fontFamily[1].getFont(Font.ITALIC, 16);
		  private Font tweetFont = fontFamily[1].getFont(Font.PLAIN, 20);
		  
		/*public ListEngine(ListField list, Vector tweets)
		{
			_view = list;
			_data = tweets;
			_view.setCallback(this);
		}*/
		
		
		//implement call back interface
		//render row
		public void drawListRow(ListField list, Graphics g, int index, int y, int w) {

			BaseTweet tweet = (BaseTweet) _data.elementAt(index);
			TweetRowDimensions d = getTweetDimensions(list, tweet, y, w);
			

			//draw background		
			if (tweet.getIsFavorite() == true)
				g.setColor(Color.GOLD);		
			else if (tweet.getIsReply() == true)
				g.setColor(Color.LIGHTGREEN);
			else
				g.setColor(Color.LIGHTGRAY);
			
			g.fillRoundRect(0 + txtPad, y + txtPad, w - (txtPad*2), d.rowHeight - txtPad*2, 25, 25);
			g.setColor(Color.BLACK);
			

			//scale images
			Vector dvec = Home.vec;
		    for(int i = 0; i < dvec.size(); i++){
		    	BaseTweet ava = ((BaseTweet)dvec.elementAt(i));
	    	    if(tweet.getAuthorHandle().equals(ava.getAuthorHandle())){
	    		    if(ava.getIsDownloaded())
	    		    	tweet.setAuthorImage(ava.getAuthorImage());
	    		    else
	    		    	tweet.setAuthorImage(EncodedImage.getEncodedImageResource("avatarpic.png"));
	    		  
	    		    break;
	    	    }
		    }
		    
		    if(tweet.getAuthorImage() == null)
		    	tweet.setAuthorImage(EncodedImage.getEncodedImageResource("avatarpic.png"));
		    
			EncodedImage imgAuthor = ImageManipulator.ResizeImage(tweet.getAuthorImage(),
					d.avatarW, d.avatarH);
			
			EncodedImage imgTwitpic = null;
			if(tweet.getTweetImage() != null)
				imgTwitpic = ImageManipulator.ResizeImage(tweet.getTweetImage(),
					d.tpxW, d.tpxH);
			

			//draw images

			//draw avatar
		    g.drawImage(d.avatarX, d.avatarY, d.avatarW, d.avatarH, imgAuthor, 0, 0, 0);
		    
		    //draw twitpic
		    if (imgTwitpic != null)
		    	g.drawImage(d.tpxX, d.tpxY, d.tpxW, d.tpxH, imgTwitpic, 0, 0, 0);
		    
		    //draw retweet icon (if retweet)
		    if (tweet.getIsRetweet() == true)
		    	g.drawImage(d.avatarX + 12, d.avatarY + d.avatarH + txtPad, 25, 25, 
		    			EncodedImage.getEncodedImageResource("retweetpic.png"), 0, 0, 0);
			
			//wrap the lines to draw
			list.setFont(tweetFont);
			Vector lines = TextWrapping.Wrap(tweet.getTweetText(), d.tweetW, list);
			

			//draw handle
			g.setFont(handleFont);		
			g.drawText(tweet.getAuthorHandle(), d.handleX , y + txtPad,
		               DrawStyle.LEFT , d.handleW );	
			
			//draw wrapped tweet lines
			//list.setFont(tweetFont);
			g.setFont(tweetFont);
			int txtWritePos;

			for (int i = 0; i < lines.size(); i++) 
			{			
				txtWritePos = d.tweetY + (i * tweetFont.getHeight())  ;		      
				g.drawText(lines.elementAt(i).toString(), d.tweetX , txtWritePos,
				               DrawStyle.LEFT , d.tweetW );	
			}	
			

			//draw date	
			g.setFont(dateFont);
			g.drawText(tweet.getTweetDate() + ", from " + tweet.getTwitterClient(), d.timeX , d.timeY,
		               DrawStyle.RIGHT, d.timeW );		

			//draw comment	
			if (tweet.getTweetComment() != null)
			g.drawText(tweet.getTweetComment(), d.commentX , d.commentY,
		               DrawStyle.RIGHT, d.commentW );		

			
		    //get row height
		    int rowHeight =  d.rowHeight;
		    list.setRowHeight(index, rowHeight );
		    
		}

		
		private TweetRowDimensions getTweetDimensions( ListField list, BaseTweet t, int y, int w)
		{
			TweetRowDimensions d  = new TweetRowDimensions();

			//avatar
			d.avatarH = aviH;
			d.avatarW = aviW;
			d.avatarX = pad;
			d.avatarY = y + pad;

			//handle
			d.handleH = handleFont.getHeight();	
			d.handleW = w -  d.avatarW - (pad * 3);
			d.handleX = d.avatarW + (pad * 2);
			d.handleY = y + pad;

			
			//twitpic
			//if there is a tweetpic
			if (t.getTweetImage() == null)
			{
				
				//calculate tweet dimensions
				d.tweetX = d.avatarW + pad * 2;
				d.tweetY = y + pad + d.handleH ;
				d.tweetW =  w - d.tweetX - pad  ;
				
				//calculate tweet height
				Vector lines = TextWrapping.Wrap(t.getTweetText(), d.tweetW, list);
				d.tweetH = lines.size() * tweetFont.getHeight();
				
			}
			else
			{	
				//calculate twitpic dimensions
				d.tpxH = aviH;
				d.tpxW = aviW;
				d.tpxX = w - pad - aviW;
				d.tpxY = y + pad + d.handleH + txtPad;
				
				//calculate tweet dimensions
				d.tweetX = d.avatarW + pad * 2;
				d.tweetY = y + pad + d.handleH ;
				d.tweetW =  w - d.tweetX - pad - d.tpxW - pad  ;
				
				//calculate tweet height
				Vector lines = TextWrapping.Wrap(t.getTweetText(), d.tweetW, list);
				d.tweetH = lines.size() * tweetFont.getHeight();	
				
			}

			
			//date
			d.timeX = d.tweetX;
			d.timeW = d.tweetW;
			d.timeH = dateFont.getHeight();
			d.timeY = d.tweetY + + d.tweetH + txtPad;
			
			//comment
			d.commentX = d.tweetX;
			d.commentW = d.tweetW;
			d.commentH = dateFont.getHeight();
			d.commentY = d.timeY + d.timeH ;
			
			
		    //get row height by comparing the ffing options and getting the maximum
			//(1) - handle, tweet, date, comment (2) - handle, tweetpic (3) - avatar
			int temp1;
			if (t.getTweetComment() != null)
				temp1 = pad + d.handleH + d.tweetH + txtPad + d.timeH + d.commentH + pad;
			else
				temp1 = pad + d.handleH + d.tweetH + txtPad + d.timeH + pad;
			int temp2 = pad + d.handleH + txtPad + d.tpxH + pad ;
			int temp3 = pad + d.avatarH + pad;
				
			d.rowHeight = Math.max(temp1, temp2);
			d.rowHeight = Math.max(d.rowHeight, temp3);
			
			return d;
		}
		
		//get row item
		public Object get(ListField listField, int index) {
			return _data.elementAt(index);
		}

		//used for rendering list, providing the width in pixels
		public int getPreferredWidth(ListField listField) {
			return Display.getWidth();
		}

		public int indexOfList(ListField listField, String prefix, int start) {
			return _data.indexOf(prefix, start);
		}
		
		public int getCurrentPosition() {
		    return currentPosition;
		}

		

		  //XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
		  // data manipulation methods...  not part of the interface
		  //XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

		  /** mutator, which syncs model and view */
		  public void insert(BaseTweet toInsert, int index) {
		    // update the model
		    _data.addElement(toInsert);

		    // update the view
		    TimelineList.this.insert(index);
		  }
		  
		  /** mutator, which syncs model and view */
		  public void delete(int index) {
		    // update the model
		    _data.removeElementAt(index);

		    // update the view
		    TimelineList.this.delete(index);
		  }
		  
		  /** mutator, which syncs model and view */
		  public void erase() {
		    int size = _data.size();

		    // update the view
		    for (int i = 0; i < size; i++) {
		      delete(i);
		    }
		  }
		  
		  public void modify(BaseTweet newValue, int index) {
		    // update the model
		    _data.removeElementAt(index);
		    _data.insertElementAt(newValue, index);

		    // update the view
		    TimelineList.this.invalidate(index);
		  }
		  
		  public int size() {
		    return _data.size();
		  }


		  //XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
		  // Action handlers
		  //XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

		  /*public ActionListenerIF getNewTweet() {
		    return new ActionListenerIF() {
		      public void actionPerformed(Object source) {
		        // add a row to the bottom
		        int size = size();
		        BaseTweet temp = new BaseTweet("this is a smaple tweet that is just meant to suffice for up to 140 characters apparently so I have to keep typing till it's up to 140 yeah x = " + size) ;
		        temp.setAuthorHandle("xoAFRO");
		        temp.setTweetDate("5 minutes ago");
		        temp.setIsRetweet(true); 
		        temp.setAuthorImage( EncodedImage.getEncodedImageResource("avatarpic.png"));
		        temp.SetTweetImage( EncodedImage.getEncodedImageResource("twitpic.png"));
		        //temp.setIsFavorite(true);
		        temp.setReplyAuthor("@Rozay");
		        temp.setTwitterClient("UberSocial");
		        temp.setTweetComment("in reply to @iamdiddy");
		        insert(temp, size);
		      }
		    };
		  }

		  public ActionListenerIF getDeleteTweet() {
		    return new ActionListenerIF() {
		      public void actionPerformed(Object source) {
		        // remove from the top
		        if (size() > 0) {
		          delete(0);
		        }
		      }
		    };
		  }
		  
		  
		  //XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
		  // Menu Items
		  //XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

		  public MenuItem getAddMenuItem(int ordinal, int priority) {
		    return new MenuItem("Add new tweet", ordinal, priority) {
		      public void run() {
		        getNewTweet().actionPerformed(_view);
		      }
		    };
		  }

		  public MenuItem getRemoveMenuItem(int ordinal, int priority) {
		    return new MenuItem("Remove oldest tweet", ordinal, priority) {
		      public void run() {
		        getDeleteTweet().actionPerformed(_view);
		      }
		    };
		  }*/

		  //XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
		  // CLass for dimensions
		  //XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

		  private class TweetRowDimensions
		  {
			  int handleX, handleY, handleW, handleH;
			  int avatarX, avatarY, avatarW, avatarH;
			  int tweetX, tweetY, tweetW, tweetH;
			  int tpxX, tpxY, tpxW, tpxH;
			  int commentX, commentY, commentW, commentH;
			  int timeX,timeY, timeW, timeH;
			  int rowHeight;
			  
			  TweetRowDimensions()
			  {}
			  
		  }

	}
}

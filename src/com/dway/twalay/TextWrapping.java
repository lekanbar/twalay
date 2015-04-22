package com.dway.twalay;

import java.util.Vector;

import net.rim.device.api.ui.component.ListField;

public class TextWrapping {

	
	public static Vector Wrap (String text, int width, ListField list) 
	{
	    Vector result = new Vector ();
	    if (text == null)
	       return result;
	 
	    boolean hasMore = true;
	 
	    // The current index of the cursor
	    int current = 0;
	 
	    // The next line break index
	    int lineBreak = -1;
	 
	    // The space after line break
	    int nextSpace = -1;
	 
	    while (hasMore) 
	    {
	       //Find the line break
	       while (true) 
	       {
	           lineBreak = nextSpace;
	           if (lineBreak == text.length() - 1) 
	           {
	               // We have reached the last line
	               hasMore = false;
	               break;
	           } 
	           else 
	           {
	               nextSpace = text.indexOf(' ', lineBreak+1);
	               if (nextSpace == -1)
	                  nextSpace = text.length() -1;
	               int linewidth = list.getFont().getAdvance(text,current, nextSpace-current);
	               // If too long, break out of the find loop
	               if (linewidth > width) 
	                  break;
	           }
	      }
	      String line = text.substring(current, lineBreak + 1);
	      if (line != null)
	    	  result.addElement(line);
	      current = lineBreak + 1;
	 }
	 return result;
	}
	
}
	

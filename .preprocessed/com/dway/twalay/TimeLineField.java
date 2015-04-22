
package com.dway.twalay;

import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;





public class TimeLineField extends Field{
	private int width;
	private int height;
	
	private double length=0;
	private double buffered=0;
	private double bufferStartsAt=0;
	private double now=0;	
	private double seekCursor=0;
	private String status="";
	
	private boolean focused = false;
	private boolean seekMode = false;
	private boolean focusable = true;
	private boolean bufferVisible = true;
	
	
	private int timeBase;
	
	private TimeLineFieldListener listener;
	
	public TimeLineField(int width, int height, int timeBase){
		super(Field.FIELD_TOP|Field.FIELD_LEFT);
		setExtent(width, height);	
		this.width = width;
		this.height = height;		
		this.timeBase = timeBase;
	}
	
	public void addTimeLineFieldListener(TimeLineFieldListener listener){
		this.listener = listener;
	}
	
	public void removeTimeLineFieldListener(){
		this.listener = null;
	}
	
	public void setLength(long length){
		this.length = length;		
	}
	
	public void setBuffered(long bufferStartsAt, long len){
		this.buffered = len;
		this.bufferStartsAt = bufferStartsAt;		
	}
	
	public void setNow(long now){
		if(!seekMode){
			this.now = now;			
		}
	}
	
	protected void layout(int width, int height) {
		setExtent(this.width, this.height);			
	}

	protected void paintBackground(Graphics graphics) {
		super.paintBackground(graphics);			
		graphics.setColor(Color.BLACK);
		graphics.fillRect(0, 0, getWidth(), getHeight());
	}

	protected void paint(Graphics graphics) {
		setExtent(width, height);	
		// draw the base timeline;
		double timelineX = 0;
		double timelineY = 0;
		double timelineWidth = width;
		double timelineHeight = height-25;	
		
		graphics.setColor(Color.YELLOW);
		graphics.drawRoundRect(round(timelineX), round(timelineY), round(timelineWidth), round(timelineHeight), 5, 5);
		if(focused){
			graphics.setColor(Color.YELLOW);		
			graphics.fillRoundRect(round(timelineX+1), round(timelineY+1), round(timelineWidth-2), round(timelineHeight-2), 5, 5);			
		} else{
			graphics.setColor(Color.WHITE);		
			graphics.fillRoundRect(round(timelineX+1), round(timelineY+1), round(timelineWidth-2), round(timelineHeight-2), 5, 5);
		}
		
		// draw the buffer
		double bufferX;
		if(length>0)
			bufferX = (bufferStartsAt/length)*width;
		else
			bufferX = 0;
		
		double bufferY = timelineY+1;		
		double bufferWidth = (buffered/length)*width;
		double bufferHeight = timelineHeight-2;
		Font bufferFont = Font.getDefault().derive(Font.PLAIN, round(bufferHeight));		
		if(bufferVisible){			
			graphics.setColor(Color.RED);		
			graphics.fillRoundRect(round(bufferX), round(bufferY), round(bufferWidth), round(bufferHeight), 5, 5);
			graphics.setColor(Color.BLACK);
			graphics.setFont(bufferFont);
			graphics.drawText("Buffered: "+Integer.toString(round(buffered)) + " bytes", round(bufferX+2), round(bufferY), DrawStyle.LEFT|DrawStyle.TOP);
			graphics.setColor(Color.RED);
			graphics.drawText(status, 0, round(height-bufferFont.getHeight()-1), DrawStyle.TOP|DrawStyle.RIGHT, width);
		} else{
			graphics.setFont(bufferFont);
			graphics.setColor(Color.RED);
			if(length>0)
				graphics.drawText("Click and Scroll to seek..", 0, round(bufferY), DrawStyle.TOP|DrawStyle.HCENTER, width);
			else
				graphics.drawText("Streaming vTweet...", 0, round(bufferY), DrawStyle.TOP|DrawStyle.HCENTER, width);
		}
		//draw the current position marker
		double markerX;
		if(length>0)
			markerX = (now/length)*width+1;
		else
			markerX = 0;
		
		double markerY = timelineY+1;
		double markerWidth = 8;
		double markerHeight = timelineHeight-2;
		Font markerFont = Font.getDefault().derive(Font.ITALIC, round(timelineHeight-2));
		
		
		
		if(seekMode){
			markerX = ((seekCursor/length)*width)+1;			
			graphics.setColor(Color.BLACK);
			graphics.drawRoundRect(round(markerX), round(markerY), round(markerWidth), round(markerHeight), 5, 5);		
			graphics.setColor(Color.WHITE);
			graphics.setGlobalAlpha(120);
			graphics.fillRoundRect(round(markerX+1), round(markerY+1), round(markerWidth-2), round(markerHeight-2), 5, 5);
			graphics.setGlobalAlpha(255);
			graphics.setColor(Color.YELLOW);
			graphics.setFont(markerFont);
			graphics.drawText(round(seekCursor)+"/"+round(length), round(markerX), round(timelineHeight-2+7), DrawStyle.LEFT|DrawStyle.VCENTER);			
		} else{
			graphics.setColor(Color.BLACK);
			graphics.drawRoundRect(round(markerX), round(markerY), round(markerWidth), round(markerHeight), 5, 5);
			graphics.setColor(Color.CHOCOLATE);
			graphics.setGlobalAlpha(120);
			graphics.fillRoundRect(round(markerX+1), round(markerY+1), round(markerWidth-2), round(markerHeight-2), 5, 5);
			graphics.setGlobalAlpha(255);
			graphics.setColor(Color.YELLOW);
			graphics.setFont(markerFont);
			graphics.drawText(round(now)+"/"+round(length), round(markerX), round(timelineHeight-2+7), DrawStyle.LEFT|DrawStyle.VCENTER);	
		}
		setExtent(width, height);			
	}
	
	private int round(double d){
		return (int)Math.ceil(d);
	}

	protected void onFocus(int direction) {
		focused = true;
		super.onFocus(direction);
		invalidate();
	}

	protected void onUnfocus() {
		focused = false;
		super.onUnfocus();
		invalidate();
	}

	
	protected boolean navigationClick(int status, int time) {
		
		if(length>0){
			if(!seekMode){
				seekMode = true;
				seekCursor = now;
			}
			else{
				seekMode = false;
				
				if(seekCursor>now+buffered || seekCursor<now)
					buffered = 0;
				else
					buffered = (buffered-(seekCursor-now));
				
				now = seekCursor;
				
				if(listener!=null)
					listener.seek((long)now);
			}
			invalidate();
		}
		
		return true;
		
	}


	protected boolean navigationMovement(int dx, int dy, int status, int time) {
		
		if(seekCursor>length){
			seekCursor=length;
			return true;
		}
		if(seekCursor<0){
			seekCursor=0;
			return true;
		}
		
		if(seekMode && length>0){
			seekCursor += (dx+dy)*timeBase;
			invalidate();
			return true;
		}
		else 
			return false;
			
	}

	























	
	


	public boolean isFocusable() {
		return focusable;
	}
	
	

	protected void drawFocus(Graphics graphics, boolean on) {
		setExtent(width, height);	
	}
	
	public void setFocusable(boolean value){
		focusable = value;
	}
	
	public void setBufferVisible(boolean value){
		bufferVisible = value;
	}
	
	public void setStatus(String status){
		this.status = status;		
	}
	
	public void update(){
		invalidate();
	}
	
	public void setWidth(int width){
		this.width = width;		
	}
	
	public void setHeight(int height){
		this.height= height;
	}
	
	
}

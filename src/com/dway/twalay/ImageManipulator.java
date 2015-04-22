package com.dway.twalay;

import net.rim.device.api.math.Fixed32;
import net.rim.device.api.system.EncodedImage;

public class ImageManipulator {
	
	public static EncodedImage ResizeImage (EncodedImage image, int width, 
			  int height)
	{

		  EncodedImage result = null;

		  int currentWidthFixed32 = Fixed32.toFP(image.getWidth());
		  int currentHeightFixed32 = Fixed32.toFP(image.getHeight());

		  int requiredWidthFixed32 = Fixed32.toFP(width);
		  int requiredHeightFixed32 = Fixed32.toFP(height);

		  int scaleXFixed32 = Fixed32.div(currentWidthFixed32,
		    requiredWidthFixed32);
		  int scaleYFixed32 = Fixed32.div(currentHeightFixed32,
		    requiredHeightFixed32);

		  result = image.scaleImage32(scaleXFixed32, scaleYFixed32);
		  
		  return result;
		
	}


	public static EncodedImage ResizeImageWithWidth (EncodedImage image, int width, 
			  int height)
	{

		  EncodedImage result = null;

		  int currentWidthFixed32 = Fixed32.toFP(image.getWidth());

		  int requiredWidthFixed32 = Fixed32.toFP(width);

		  int scaleXFixed32 = Fixed32.div(currentWidthFixed32,
		    requiredWidthFixed32);

		  result = image.scaleImage32(scaleXFixed32, scaleXFixed32);
		  
		  return result;
		
	}
}

package com.dway.twalay;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Hashtable;
 
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

import net.rim.blackberry.api.browser.URLEncodedPostData;
 
public class HttpMultipartRequest
{
	static final String BOUNDARY = "----------V2ymHFg03ehbqgZCaKO6jy";
 
	byte[] postBytes = null;
	String url = null, thehash;
 
	public HttpMultipartRequest(String url, Hashtable params, String fileField, String fileName, String fileType, byte[] fileBytes) //throws Exception
	{
		this.url = url;
 
		String boundary = getBoundaryString();
 
		String boundaryMessage = getBoundaryMessage(boundary, params, fileField, fileName, fileType);
 
		String endBoundary = "\r\n--" + boundary + "--\r\n";
 
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
 
		try{
			bos.write(boundaryMessage.getBytes());
	 
			bos.write(fileBytes);
	 
			bos.write(endBoundary.getBytes());
	 
			this.postBytes = bos.toByteArray();
	 
			bos.close();
		}catch(IOException ioe){
			ioe.printStackTrace();
		}
	}
	
	public HttpMultipartRequest(String url, String thehash)
	{
		this.url = url;
		
		try{
			this.thehash = thehash;
		
		//this.postBytes = ("{\"filehash\": " + thehash + "}").getBytes("UTF-8");
 
		//String boundary = getBoundaryString();
 
		//String boundaryMessage = getBoundaryMessage(boundary, params);
 
		//String endBoundary = "\r\n--" + boundary + "--\r\n";
 
		//ByteArrayOutputStream bos = new ByteArrayOutputStream();
 
		
			//bos.write(boundaryMessage.getBytes());
	 
			//bos.write(endBoundary.getBytes());
	 
			//this.postBytes = bos.toByteArray();
	 
			//bos.close();
		}catch(Exception ioe){
			ioe.printStackTrace();
		}
	}
 
	String getBoundaryString()
	{
		return BOUNDARY;
	}
 
	String getBoundaryMessage(String boundary, Hashtable params, String fileField, String fileName, String fileType)
	{
		StringBuffer res = new StringBuffer("--").append(boundary).append("\r\n");
 
		Enumeration keys = params.keys();
 
		while(keys.hasMoreElements())
		{
			String key = (String)keys.nextElement();
			String value = (String)params.get(key);
 
			res.append("Content-Disposition: form-data; name=\"").append(key).append("\"\r\n")    
				.append("\r\n").append(value).append("\r\n")
				.append("--").append(boundary).append("\r\n");
		}
		
		res.append("Content-Disposition: form-data; name=\"").append(fileField).append("\"; filename=\"").append(fileName).append("\"\r\n") 
			.append("Content-Type: ").append(fileType).append("\r\n\r\n");
 
		return res.toString();
	}
	
	String getBoundaryMessage(String boundary, Hashtable params)
	{
		StringBuffer res = new StringBuffer("--").append(boundary).append("\r\n");
 
		Enumeration keys = params.keys();
 
		while(keys.hasMoreElements())
		{
			String key = (String)keys.nextElement();
			String value = (String)params.get(key);
 
			res.append("Content-Disposition: form-data; name=\"").append(key).append("\"\r\n")    
				.append("\r\n").append(value).append("\r\n")
				.append("--").append(boundary).append("\r\n");
		}
 
		return res.toString();
	}
 
	public byte[] send()
	{
		HttpConnection hc = null;
 
		InputStream is = null;
 
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
 
		byte[] res = null;
 
		try
		{
			hc = (HttpConnection) Connector.open(url);
 
			hc.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + getBoundaryString());
 
			hc.setRequestMethod(HttpConnection.POST);
 
			OutputStream dout = hc.openOutputStream();
 
			dout.write(postBytes);
 
			dout.close();
 
			int ch;
 
			is = hc.openInputStream();
 
			while ((ch = is.read()) != -1)
			{
				bos.write(ch);
			}
			res = bos.toByteArray();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(bos != null)
					bos.close();
 
				if(is != null)
					is.close();
 
				if(hc != null)
					hc.close();
			}
			catch(Exception e2)
			{
				e2.printStackTrace();
			}
		}
		return res;
	}
	
	public byte[] send(boolean isMulti)
	{
		HttpConnection hc = null;
 
		InputStream is = null;
 
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
 
		byte[] res = null;
 
		try
		{
			hc = (HttpConnection) Connector.open(url);
 
			hc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
 
			hc.setRequestMethod(HttpConnection.POST);
			
			//int len = (this.obj.toString().length() - 2);
			hc.setRequestProperty("Content-Length", "" + this.thehash.length());
			//hc.setRequestProperty("Content-Length", "" + "12".length());
			
			
			URLEncodedPostData oPostData = new URLEncodedPostData(URLEncodedPostData.DEFAULT_CHARSET, false);

	        oPostData.append("filehash", thehash);
			//oPostData.append("test", "12");
	        byte [] postBytesw = oPostData.getBytes();
 
			OutputStream dout = hc.openOutputStream();
 
			//dout.write(postBytes);
			dout.write(postBytesw);
 
			dout.close();
 
			int ch, rc;
 
			//rc = hc.getResponseCode();
			
			//if(rc == HttpConnection.HTTP_OK){
				is = hc.openInputStream();
	 
				while ((ch = is.read()) != -1)
				{
					bos.write(ch);
				}
				res = bos.toByteArray();
			//}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(bos != null)
					bos.close();
 
				if(is != null)
					is.close();
 
				if(hc != null)
					hc.close();
			}
			catch(Exception e2)
			{
				e2.printStackTrace();
			}
		}
		return res;
	}
}
/*
 * OAuthDialogWrapper.java
 * 21/07/2011
 * Twitter API Micro Edition
 * Copyright(c) Ernandes Mourao Junior (ernandes@gmail.com)
 * All rights reserved
 */
package com.twitterapime.xauth.ui;

import java.io.IOException;

import com.twitterapime.io.HttpConnection;
import com.twitterapime.io.HttpRequest;
import com.twitterapime.io.HttpResponse;
import com.twitterapime.util.StringUtil;
import com.twitterapime.xauth.OAuthSigner;
import com.twitterapime.xauth.Token;

/**
 * <p>
 * This class defines a wrapper for a browser component, which will hold the 
 * authentication process to Twitter. It is responsible for informing to
 * Twitter all the parameters about the application that is trying to get 
 * access to a given user account.
 * </p>
 * <p>
 * In addition, this class also tracks the process in order to identify when the 
 * authorization is granted or denied, so it can notify the application. This 
 * notification is done through a listener object that implements 
 * {@link OAuthDialogListener}. 
 * </p>
 * <p>
 * Before using this class, the developer must register an app on Twitter
 * Developer website.
 * </p>
 * @author Ernandes Mourao Junior (ernandes@gmail.com)
 * @version 1.0
 * @since 1.8
 */
public abstract class OAuthDialogWrapper {
	/**
	 * <p>
	 * OAuth request token failed key.
	 * </p>
	 */
	public static final String OAUTH_REQUEST_TOKEN_FAILED = 
		"oauth_request_token_failed";
	
	/**
	 * <p>
	 * OAuth I/O failed key.
	 * </p>
	 */
	public static final String OAUTH_IO_FAILED = "oauth_io_exception";

	/**
	 * <p>
	 * OAuth access token failed key.
	 * </p>
	 */
	public static final String OAUTH_ACCESS_TOKEN_FAILED = 
		"oauth_access_token_failed";
	
	/**
	 * <p>
	 * OAuth access token denied key.
	 * </p>
	 */
	public static final String OAUTH_ACCESS_TOKEN_DENIED = 
		"oauth_access_token_denied";

	/**
	 * <p>
	 * Consumer key.
	 * </p>
	 */
	protected String consumerKey;

	/**
	 * <p>
	 * Consumer Secret.
	 * </p>
	 */
	protected String consumerSecret;

	/**
	 * <p>
	 * Callback Url.
	 * </p>
	 */
	protected String callbackUrl;
	
	/**
	 * <p>
	 * OAuth listener.
	 * </p>
	 */
	protected OAuthDialogListener oauthListener;
	
	/**
	 * <p>
	 * OAuth signer.
	 * </p>
	 */
	protected OAuthSigner signer;
	
	/**
	 * <p>
	 * Token.
	 * </p>
	 */
	protected Token token;
	
	/**
	 * <p>
	 * Request Token Url.
	 * </p>
	 */
	protected String requestTokenUrl =
		"https://api.twitter.com/oauth/request_token";

	/**
	 * <p>
	 * Login Page Url.
	 * </p>
	 */
	protected String loginPageUrl =
		"https://api.twitter.com/oauth/authorize";;
	
	/**
	 * <p>
	 * Access Token Url.
	 * </p>
	 */
	protected String accessTokenUrl =
		"https://api.twitter.com/oauth/access_token";
	
	/**
	 * <p>
	 * Flag to display custom result pages.
	 * </p>
	 */
	protected boolean enableCustomResultPages;
	
	/**
	 * <p>
	 * Custom success page Html.
	 * </p>
	 */
	protected String successPageHtml;
	
	/**
	 * <p>
	 * Custom denied page Html.
	 * </p>
	 */
	protected String deniedPageHtml;

	/**
	 * <p>
	 * Custom error page Html.
	 * </p>
	 */
	protected String errorPageHtml;
	
	/**
	 * <p>
	 * Create an instance of OAuthDialogWrapper class.
	 * </p>
	 * @param consumerKey Consumer key.
	 * @param consumerSecret Consumer secret.
	 * @param callbackUrl Callback Url. (null for "Out-of-band" mode)
	 * @param authListener OAuth listener.
	 */
	protected OAuthDialogWrapper(String consumerKey, String consumerSecret,
		String callbackUrl,	OAuthDialogListener authListener) {
		//
		setConsumerKey(consumerKey);
		setConsumerSecret(consumerSecret);
		setCallbackUrl(callbackUrl);
		setEnableCustomResultPages(true);
		setOAuthListener(authListener);
	}

	/**
	 * <p>
	 * Set the consumer key.
	 * </p>
	 * @param consumerKey Key.
	 */
	public void setConsumerKey(String consumerKey) {
		this.consumerKey = consumerKey;
	}
	
	/**
	 * <p>
	 * Set the consumer secret.
	 * </p>
	 * @param consumerSecret Secret.
	 */
	public void setConsumerSecret(String consumerSecret) {
		this.consumerSecret = consumerSecret;
	}

	/**
	 * <p>
	 * Set the callback url.
	 * </p>
	 * @param callbackUrl Url. (null for "Out-of-band" mode)
	 */
	public void setCallbackUrl(String callbackUrl) {
		this.callbackUrl =
			StringUtil.isEmpty(callbackUrl) ? "oob" : callbackUrl;
	}

	/**
	 * <p>
	 * Set the Request Token url.
	 * </p>
	 * @param requestTokenUrl Url.
	 */
	public void setRequestTokenUrl(String requestTokenUrl) {
		this.requestTokenUrl = requestTokenUrl;
	}

	/**
	 * <p>
	 * Set the Request Token url.
	 * </p>
	 * @param loginPageUrl Url.
	 */
	public void setLoginPageUrl(String loginPageUrl) {
		this.loginPageUrl = loginPageUrl;
	}

	/**
	 * <p>
	 * Set the Request Token url.
	 * </p>
	 * @param accessTokenUrl Url.
	 */
	public void setAccessTokenUrl(String accessTokenUrl) {
		this.accessTokenUrl = accessTokenUrl;
	}
	
	/**
	 * <p>
	 * Enable custom result (grant, deny or error) pages. A result page is 
	 * displayed in place of the page pointed by Callback url.
	 * </p>
	 * @param enabled Enabled (true).
	 */
	public void setEnableCustomResultPages(boolean enabled) {
		enableCustomResultPages = enabled;
	}

	/**
	 * <p>
	 * Set a Html script for the custom success page.
	 * </p>
	 * @param html Html script.
	 */
	public void setCustomSuccessPageHtml(String html) {
		successPageHtml = html;
	}

	/**
	 * <p>
	 * Set a Html script for the custom denied page.
	 * </p>
	 * @param html Html script.
	 */
	public void setCustomDeniedPageHtml(String html) {
		deniedPageHtml = html;
	}

	/**
	 * <p>
	 * Set a Html script for the custom error page.
	 * </p>
	 * @param html Html script.
	 */
	public void setCustomErrorPageHtml(String html) {
		errorPageHtml = html;
	}

	/**
	 * <p>
	 * Set the OAuth listener.
	 * </p>
	 * @param listener Listener.
	 */
	public void setOAuthListener(OAuthDialogListener listener) {
		oauthListener = listener;
	}

	/**
	 * <p>
	 * Load the Twitter's login page, so the user can enter his credentials.
	 * </p>
	 * @throws IllegalArgumentException If consumer key, secret or Url is null.
	 */
	public void login() {
		if (StringUtil.isEmpty(consumerKey)) {
			throw new IllegalArgumentException(
				"Consumer Key must not be null.");
		}
		if (StringUtil.isEmpty(consumerSecret)) {
			throw new IllegalArgumentException(
				"Consumer Secret must not be null.");
		}
		if (StringUtil.isEmpty(callbackUrl)) {
			throw new IllegalArgumentException(
				"Callback Url must not be null.");
		}
		//
		token = null;
		signer = new OAuthSigner(consumerKey, consumerSecret);
		//
		new Thread() {
			public void run() {
				requestToken();
			};
		}.start();
	}
	
	/**
	 * <p>
	 * Process the authentication using the PIN-code informed by the Twitter's
	 * authentication page.
	 * </p>
	 * <p>
	 * Use this method only if you are using "Out-of-band" mode.
	 * </p>
	 * @param pinCode PIN-code.
	 * @throws IllegalArgumentException If PIN-code is empty/null.
	 * @throws IllegalStateException If {@link OAuthDialogWrapper#login()} was 
	 *                               not previously called.
	 */
	public void login(final String pinCode) {
		if (StringUtil.isEmpty(pinCode)) {
			throw new IllegalArgumentException("PIN-code must not be null.");
		}
		//
		if (token == null) {
			throw new IllegalStateException(
				"Invalid state: call login() method first.");
		}
		//
		new Thread() {
			public void run() {
				trackUrl(callbackUrl + "?oauth_verifier=" + pinCode);
			};
		}.start();
	}
	
	/**
	 * <p>
	 * Load the given Url in the browser component.
	 * </p>
	 * @param url Url.
	 */
	protected abstract void loadUrl(String url);
	
	/**
	 * <p>
	 * Load the given HTML content in the browser component.
	 * </p>
	 * @param htmlContent HTML content.
	 */
	protected abstract void loadHTML(String htmlContent);

	/**
	 * <p>
	 * Process request token.
	 * </p>
	 */
	protected void requestToken() {
		HttpRequest req = new HttpRequest(requestTokenUrl);
		//
		signer.signForRequestToken(req, callbackUrl);
		//
		try {
			HttpResponse resp = req.send();
			String body = resp.getBodyContent();
			//
			if (resp.getCode() != HttpConnection.HTTP_OK) {
				displayOAuthErrorPage(OAUTH_REQUEST_TOKEN_FAILED, body);
				triggerOnFail(OAUTH_REQUEST_TOKEN_FAILED, body);
				//
				return;
			}
			//
			String conf =
				StringUtil.getUrlParamValue(body, "oauth_callback_confirmed");
			//
			if ("true".equals(conf)) { //went ok?
				token = Token.parse(body);
				//
				String oauthUrl =
					loginPageUrl + "?oauth_token=" + token.getToken() + 
					"&force_login=true";
				//
				loadUrl(oauthUrl);
			} else {
				displayOAuthErrorPage(OAUTH_REQUEST_TOKEN_FAILED, body);
				triggerOnFail(OAUTH_REQUEST_TOKEN_FAILED, body);
			}
		} catch (IOException e) {
			displayOAuthErrorPage(OAUTH_IO_FAILED, e.getMessage());
			triggerOnFail(OAUTH_IO_FAILED, e.getMessage());
		} finally {
			try {
				req.close();
			} catch (IOException e) {
			}
		}
	}
	
	/**
	 * <p>
	 * Track the given Url in order to identify any event related to the
	 * authentication process.
	 * </p>
	 * @param url Url.
	 */
	protected void trackUrl(String url) {
		if (url.startsWith(callbackUrl)	&& oauthListener != null) {
			if (url.indexOf("oauth_verifier=") != -1) {
				String verifier =
					StringUtil.getUrlParamValue(url, "oauth_verifier");
				HttpRequest req = new HttpRequest(accessTokenUrl);
				//
				signer.signForAccessToken(req, token, verifier);
				//
				try {
					HttpResponse resp = req.send();
					String body = resp.getBodyContent();
					//
					if (resp.getCode() == HttpConnection.HTTP_OK) {
						displayOAuthSuccessPage();
						token = Token.parse(body);
						triggerOnAuthorize(token);
					} else {
						displayOAuthErrorPage(OAUTH_ACCESS_TOKEN_FAILED, body);
						triggerOnFail(OAUTH_ACCESS_TOKEN_FAILED, body);
					}
				} catch (IOException e) {
					displayOAuthErrorPage(OAUTH_IO_FAILED, e.getMessage());
					triggerOnFail(OAUTH_IO_FAILED, e.getMessage());
				} finally {
					try {
						req.close();
					} catch (IOException e) {
					}
				}
			} else if (url.indexOf("denied=") != -1) {
				displayOAuthDeniedPage("Access Denied");
				triggerOnAccessDenied(OAUTH_ACCESS_TOKEN_DENIED);
			}
		}
	}
	
	/**
	 * <p>
	 * Display the OAuth success page. Displayed when the authorization is 
	 * granted.
	 * </p>
	 */
	protected void displayOAuthSuccessPage() {
		if (enableCustomResultPages) {
			String html =
				"<html><body>" +
				"<center><font color=\"blue\">" +
				"<br/><b>Twitter</b></font></center><br/>" +
				"<center>Authorization granted!<br/>" + 
				"<br/>Close this page.</center>" + 
				"</body></html>";
			//
			if (successPageHtml != null) {
				html = successPageHtml;
			}
			//
			loadHTML(html);
		}
	}
	
	/**
	 * <p>
	 * Display the OAuth denied page. Displayed when the authorization is 
	 * denied.
	 * </p>
	 * @param message Message.
	 */
	protected void displayOAuthDeniedPage(String message) {
		if (enableCustomResultPages) {
			String html =
				"<html><body>" +
				"<center><font color=\"blue\">" +
				"<br/><b>Twitter</b></font></center><br/>"+
				"<center>Authorization denied: " + message + 
				"<br/><br/>Close this page.</center>" + 
				"</body></html>";
			//
			if (deniedPageHtml != null) {
				html = deniedPageHtml;
			}
			//
			loadHTML(html);
		}
	}

	/**
	 * <p>
	 * Display the OAuth error page. Displayed when the authorization fails.
	 * </p>
	 * @param error Error.
	 * @param message Message.
	 */
	protected void displayOAuthErrorPage(String error, String message) {
		if (enableCustomResultPages) {
			String html =
				"<html><body>" +
				"<center><font color=\"blue\">" + 
				"<br/><b>Twitter</b></font></center><br/>" +
				"<center>Authorization failed: " + message + " (" + error + ")"+ 
				"<br/><br/>Close this page.</center>" + 
				"</body></html>";
			//
			if (errorPageHtml != null) {
				html = errorPageHtml;
			}
			//
			loadHTML(html);
		}
	}

	/**
	 * <p>
	 * Trigger authentication listeners about on authorize event.
	 * </p>
	 * @param accessToken Access token.
	 */
	protected void triggerOnAuthorize(Token accessToken) {
		if (oauthListener != null) {
			oauthListener.onAuthorize(accessToken);
		}
	}

	/**
	 * <p>
	 * Trigger authentication listeners about on fail event.
	 * </p>
	 * @param error Error.
	 * @param message Message.
	 */
	protected void triggerOnFail(String error, String message) {
		if (oauthListener != null) {
			oauthListener.onFail(error, message);
		}
	}
	
	/**
	 * <p>
	 * Trigger authentication listeners about on access denied event.
	 * </p>
	 * @param message Message.
	 */
	protected void triggerOnAccessDenied(String message) {
		if (oauthListener != null) {
			oauthListener.onAccessDenied(message);
		}
	}
}

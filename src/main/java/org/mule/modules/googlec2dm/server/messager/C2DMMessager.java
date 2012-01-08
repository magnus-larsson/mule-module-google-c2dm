package org.mule.modules.googlec2dm.server.messager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import org.mule.modules.googlec2dm.server.http.HttpStatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class C2DMMessager {
	
	private static final Logger log = LoggerFactory.getLogger(C2DMMessager.class);

	private String googleUsername;
	private String googlePassword;
	private String googleAuthUrl;
	private String googlePushUrl;	
	private String googleSource;

	public C2DMMessager(String googleUsername, String googlePassword, String googleAuthUrl, String googlePushUrl, String googleSource) {
		this.googleUsername = googleUsername;
		this.googlePassword = googlePassword;
		this.googleAuthUrl = googleAuthUrl;
		this.googlePushUrl = googlePushUrl;
		this.googleSource = googleSource;
	}
	
	public String fetchGoogleAuthToken() {
		
		try {
			final URL url = new URL(this.googleAuthUrl);
			final HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
			
			final StringBuilder builder = new StringBuilder();
			builder.append("accountType").append("=").append("HOSTED_OR_GOOGLE").append("&");
			builder.append("Email").append("=").append(this.googleUsername).append("&");
			builder.append("Passwd").append("=").append(this.googlePassword).append("&");
			builder.append("service").append("=").append("ac2dm").append("&");
			builder.append("source").append("=").append(this.googleSource);
			
			con.setDoOutput(true);
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			
			final byte[] data = builder.toString().getBytes("UTF-8");
			final OutputStream out = con.getOutputStream();
			
			out.write(data);
			out.close();
			
			final int response = con.getResponseCode();
			if (response == HttpStatusCode.SC_FORBIDDEN) {
				throw new IOException("Could not login with specified Google account. Please verify the credentials");
			}
			
			if (response == HttpStatusCode.SC_OK) {
				log.debug("Call was successful");
			
				final BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String authToken = null;
				String line = "";
				
				log.debug("Parsing input stream");
				while ((line = reader.readLine()) != null) {
					
					final String[] split = line.split("=");
					if (split[0].equals("Auth")) {
						authToken = split[1];
						break;
					}
				}
				
				if (authToken != null) {
					log.debug("Found auth token");
					return authToken;
				} else {
					throw new IOException("Could not find any auth token in the response");
				}
			}
			
		} catch (IOException e) {
			log.warn("Caught exception when trying to fetch auth token from Google", e);
			throw new RuntimeException("Caught exception when trying to fetch auth token from Google", e);
		}
		
		return null;
	}
	
	public void sendGooglePushNotification(
			final String authToken
			, final String registrationId
			, final String title
			, final String message
			, final String refType
			, final String refValue) {
		
		try {
			
			final StringBuilder builder = new StringBuilder();
			builder.append("registration_id").append("=").append(registrationId).append("&");
			builder.append("collapse_key").append("=").append("netcare").append("&");
			builder.append("data.ref").append("=").append(refType + "," + refValue).append("&");
			builder.append("data.title").append("=").append(title).append("&");
			builder.append("data.message").append("=").append(message).append("&");
			builder.append("data.timestamp").append("=").append(new Long(System.currentTimeMillis())).append("&");
			builder.append("delay_while_idle").append("=").append("true");
			
			/*
			 * Need this because we cannot verify google's host
			 */
			final HostnameVerifier verifier = new HostnameVerifier() {
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			};
			
			final URL url = new URL(this.googlePushUrl);
			final HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
			con.setHostnameVerifier(verifier);
			
			final byte[] data = builder.toString().getBytes("UTF-8");
			
			con.setDoOutput(true);
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			con.setRequestProperty("Content-Length", null);
			con.setRequestProperty("Authorization", "GoogleLogin auth=" + authToken);
			
			final OutputStream out = con.getOutputStream();
			out.write(data);
			out.close();
			
			int response = con.getResponseCode();
			if (response == HttpStatusCode.SC_OK) {
				log.debug("Got OK from Google server. Extracting information from the response...");
				
				final BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String line = null;
				
				while ((line = reader.readLine()) != null) {
					final String[] split = line.split("=");
					if (split[0].equals("Error")) {
						throw new IOException("There was an error in the response from Google. Description: " + split[1]);
					}
				}
				
			}
		} catch (IOException e) {
			log.warn("Could not send push notification to Google server.", e);
		}
	}
}

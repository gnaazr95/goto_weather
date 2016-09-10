package com.example.go_toweather;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.os.AsyncTask;

public class WeatherGenerator extends AsyncTask<URL, Integer, String> {
    protected String doInBackground(URL... urls) {
    	String query = "";
    	String charset = "UTF-8";
    	URL currUrl = urls[0];
    	URL url = null;
		try {
			url = new URL(currUrl + URLEncoder.encode(query, charset));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
    	BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(
			url.openStream()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	String str = "";
    	StringBuilder total = new StringBuilder();
    	try {
			while ((str = in.readLine()) != null) {
				
				total.append("\n" +  str);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return total.toString();
    }

}

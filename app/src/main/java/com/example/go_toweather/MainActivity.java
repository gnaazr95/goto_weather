package com.example.go_toweather;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.xml.parsers.ParserConfigurationException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.xml.sax.SAXException;


public class MainActivity extends ListActivity  {
	TextView cityName;
	String theCity;
	ListView list;
	Button locPreference;
	DatabaseLocations myDB;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    	this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		//Remove notification bar
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		//set content view AFTER ABOVE sequence (to avoid crash)
		this.setContentView(R.layout.main); 
        //this.setContentView(R.layout.main);
    	Bundle b = getIntent().getExtras();
    	theCity = b.getString("key");
    	String[] citySplit = theCity.split(",");
    	String city = citySplit[0].replaceAll("\\s","");
    	String nfdNormalizedString = Normalizer.normalize(city, Normalizer.Form.NFD); 
    	Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
    	city = pattern.matcher(nfdNormalizedString).replaceAll("");
        setJSON(city);
        myDB = new DatabaseLocations(MainActivity.this);
        locPreference = (Button)findViewById(R.id.database);    
        AddData();
    }
    
    
    
    public void setJSON(String theCityName)
    {
    	String apiCode = returnInfo(theCityName);
    	
			try {
				parseXML(apiCode);
			}  catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

    public String returnInfo(String city)
    {
    	URL url = null;
 		try {
 			url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=" + city + "&units=imperial&cnt=7&appid=a38cb81486e1382509ff3900cebb1d7c");
 		} catch (MalformedURLException e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		}
 		   AsyncTask<URL, Integer, String> theResult = new WeatherGenerator().execute(url);
 		   String totalString = "";
 		try {
 			totalString = theResult.get();
 		} catch (InterruptedException e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		} catch (ExecutionException e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		}
 		return totalString;
    }
    
    public void parseXML(String XML) throws IOException, ParserConfigurationException, SAXException, JSONException
    {
  	  	String JSON_DATA = XML;
  	  	System.out.println(JSON_DATA);
  		JSONObject obj = new JSONObject(JSON_DATA);
      	JSONObject geodata = obj.getJSONObject("city");
      	String thecity = geodata.getString("name");
    	cityName = (TextView)findViewById(R.id.citynametext);
    	Typeface type = Typeface.createFromAsset(getAssets(),"fonts/raleway.ttf"); 
    	cityName.setTypeface(type);
    	cityName.setText(theCity);

      	JSONArray listArray = obj.getJSONArray("list");
      	List<String> listOfDates = new ArrayList<String>();
      	List<Integer> listOfIcons = new ArrayList<Integer>();
      	List<String> listOfDescription = new ArrayList<String>();

      	DateFormat dateFormat = new SimpleDateFormat("dd",Locale.ENGLISH);
        Date date = new Date();
        String day = "";
        String month = "";
      	for(int i = 0; i < listArray.length(); i++)
      	{
          	JSONObject currData = listArray.getJSONObject(i);
          	JSONObject innerData = currData.getJSONObject("temp");
          	JSONArray innerDataDescription = currData.getJSONArray("weather");
          	JSONObject description = innerDataDescription.getJSONObject(0);
          	String describe = description.getString("main");
          	String picIcon = description.getString("icon");
          	String furtherDescription = description.getString("description");
          	furtherDescription = furtherDescription.substring(0, 1).toUpperCase() + furtherDescription.substring(1);
          	String fullWord = "a"+picIcon;
          	int resID = getResources().getIdentifier(fullWord, "drawable", getPackageName());
          	listOfIcons.add(resID);
          	String temp = innerData.getString("day");
          	String min = innerData.getString("min");
          	String max = innerData.getString("max");
          	listOfDescription.add(furtherDescription + " with a high of: " + max + " and a low of: " + min);
	        String newDate = dateFormat.format(date);
	        if(i == 0)
	        {
		        Calendar first = Calendar.getInstance(); 
		        date = first.getTime();
		        day = new SimpleDateFormat("EEEE",Locale.ENGLISH).format(date.getTime());
		        month = new SimpleDateFormat("MMM",Locale.ENGLISH).format(date.getTime());
	        }
	        listOfDates.add(day + ", " + month + " " + newDate  + " - " + describe + " - " + temp + "\u00B0F");
	        Calendar c = Calendar.getInstance(); 
	        c.setTime(date); 
	        c.add(Calendar.DATE, 1);
	        date = c.getTime();
	        day = new SimpleDateFormat("EEEE",Locale.ENGLISH).format(date.getTime());
      	}
      	Integer[] ret = new Integer[listOfIcons.size()];
        Iterator<Integer> iterator = listOfIcons.iterator();
        for (int i = 0; i < ret.length; i++)
        {
            ret[i] = iterator.next().intValue();
        }
      	final String[] strarray = listOfDates.toArray(new String[0]);
      	final String[] strDescription = listOfDescription.toArray(new String[0]);
      	CustomListAdapter adapter=new CustomListAdapter(this, strarray,strDescription, ret);

		list=(ListView)findViewById(android.R.id.list);
		list.setAdapter(adapter);

		list.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				String Slecteditem= strarray[+position];
				Toast.makeText(getApplicationContext(), Slecteditem, Toast.LENGTH_SHORT).show();
				
			}
		});
    }
    
    
    public void AddData()
    {
        locPreference.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
                Cursor currRes = myDB.getAllData();

                currRes.moveToFirst();
                while (!currRes.isAfterLast()) {
                    System.out.println( currRes.getString(1));
                    if(theCity.equals(currRes.getString(1)))
                    {
						Toast.makeText(MainActivity.this, "Location already exists", Toast.LENGTH_LONG).show();
						return;
                    }
                    currRes.moveToNext();
                }
                //else
               // {
				boolean isInserted = myDB.insertData(theCity);
				if(isInserted == true)
				{
					Toast.makeText(MainActivity.this, "Successfully Added!!", Toast.LENGTH_LONG).show();
				}
				else	
				{
					Toast.makeText(MainActivity.this, "Data Not Inserted", Toast.LENGTH_LONG).show();
				}
             //  }
			}
		});
    }
    
}
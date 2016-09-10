package com.example.go_toweather;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONException;
import org.xml.sax.SAXException;
import android.app.AlertDialog.Builder;


import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.content.DialogInterface.OnMultiChoiceClickListener;


import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import android.view.KeyEvent;

public class MainMenu extends ActionBarActivity{
 	public String result = ""; 
 	public Button viewData;
 	public Button deleteData;
 	public AutoCompleteTextView acTextView;
	public ArrayList mSelectedItems;
 	DatabaseLocations myDb;
 	public final static String EXTRA_MESSAGE = "com.example.theweathertracker.MESSAGE";
	@Override
	    protected void onCreate(final Bundle savedInstanceState)
	 	{
	        super.onCreate(savedInstanceState);
	    	this.requestWindowFeature(Window.FEATURE_NO_TITLE);

			//Remove notification bar
			this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

			//set content view AFTER ABOVE sequence (to avoid crash)
			this.setContentView(R.layout.activity_launch); 
	        myDb = new DatabaseLocations(this);
	     	TextView enterLocation = (TextView)findViewById(R.id.enterlocation);
	        Typeface type = Typeface.createFromAsset(getAssets(),"fonts/font2.ttf"); 
	        enterLocation.setTypeface(type);
	        acTextView = (AutoCompleteTextView) findViewById(R.id.outputcities);
	        acTextView.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					// TODO Auto-generated method stub
					acTextView.setCursorVisible(true);
					//displayDatabaseObjects();

				}
			});
	 	     acTextView.addTextChangedListener(new TextWatcher() {
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					// TODO Auto-generated method stub
					if(s.length() > 0)
                    	acTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.smallsearch, 0, R.drawable.erasex, 0);
					else if(s.length() == 0)
						acTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.smallsearch, 0, R.mipmap.plus, 0);

					if(s.length() > 2)
					{
				 		Object param = s.toString();
				 		AsyncTask<Object, Integer, ArrayList<String>> theResult = new CityGenerator().execute(param);
				 		try {
							 parseXML(theResult.get());
						} catch (IOException e) {
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
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (ExecutionException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				}
				
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {
					// TODO Auto-generated method stub
					//acTextView.setCursorVisible(true);
				}
				
				public void afterTextChanged(Editable s) {
					// TODO Auto-generated method stub
					
				}
			});
	 	     
	 	    acTextView.setOnTouchListener(new OnTouchListener() {
	 	        public boolean onTouch(View v, MotionEvent event) {
	 	            final int DRAWABLE_RIGHT = 2;
	 	            if(event.getAction() == MotionEvent.ACTION_UP) {
	 	                if(event.getRawX() >= (acTextView.getRight() - acTextView.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
	 	                    // your action here

							Drawable[] drawables = acTextView.getCompoundDrawables();
                            if(acTextView.getText().toString().length() == 0) {
								//onCreateDialog(savedInstanceState);
								//useExistingLocation(savedInstanceState).show();
								View view = MainMenu.this.getCurrentFocus();
								if (view != null) {
									InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
									imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
								}
								popupDialog(savedInstanceState).show();
								InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
								imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
								popupDialog(savedInstanceState).getWindow().setLayout(600, 400);
							}
							else {
								acTextView.getText().clear();
							}
	 	                }
	 	            }
	 	            return false;
	 	        }
	 	    });
	    }

	public Dialog useExistingLocation(Bundle savedInstanceState) {
		mSelectedItems = new ArrayList();  // Where we track the selected items
		AlertDialog.Builder builder = new AlertDialog.Builder(MainMenu.this);
	    final String[] databaseData = categoryStrings();
		if(databaseData == null)
		{
			builder.setTitle("There are no locations to show!");
		}
		else
		{
			boolean[] clickedCategories = new boolean[databaseData.length];
			String cityPicked = "";
			builder.setTitle("Pick a location")
					// Specify the list array, the items to be selected by default (null for none),
					// and the listener through which to receive callbacks when items are selected
					.setSingleChoiceItems(databaseData, -1, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface, int i) {
							result = databaseData[i];
						}
					})
					.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int id) {
							// User clicked OK, so save the mSelectedItems results somewhere
							// or return them to the component that opened the dialog
							processResult(result);
						}
					})
					.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int id) {

						}
					});
		}
		return builder.create();
	}

	public Dialog popupDialog(final Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(MainMenu.this);
		String cityPicked = "";
		builder.setTitle("What would you like to do?")
				// Specify the list array, the items to be selected by default (null for none),
				// and the listener through which to receive callbacks when items are selected
				.setPositiveButton("Pick location", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						// User clicked OK, so save the mSelectedItems results somewhere
						// or return them to the component that opened the dialog
						//processResult(result);
						useExistingLocation(savedInstanceState).show();
					}
				})
				.setNeutralButton("Edit locations", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						deleteLocations(savedInstanceState).show();
					}
				});


		return builder.create();
	}

	public Dialog deleteLocations(Bundle savedInstanceState) {
		mSelectedItems = new ArrayList();  // Where we track the selected items
		AlertDialog.Builder builder = new AlertDialog.Builder(MainMenu.this);
		final String[] databaseData = categoryStrings();
		if(databaseData == null)
		{
			builder.setTitle("There are no locations to show!");
		}
		else {
			boolean[] clickedCategories = new boolean[databaseData.length];
			String cityPicked = "";
			builder.setTitle("Pick locations to delete")
					// Specify the list array, the items to be selected by default (null for none),
					// and the listener through which to receive callbacks when items are selected
					.setMultiChoiceItems(databaseData, null, new OnMultiChoiceClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface, int i, boolean isChecked) {
							if (isChecked) {
								// If the user checked the item, add it to the selected items
								mSelectedItems.add(databaseData[i]);
							} else if (mSelectedItems.contains(databaseData[i])) {
								// Else, if the item is already in the array, remove it
								mSelectedItems.remove(databaseData[i]);
							}
						}
					})
					.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int id) {
							// User clicked OK, so save the mSelectedItems results somewhere
							// or return them to the component that opened the dialog
							//processResult(result);
							for (int j = 0; j < mSelectedItems.size(); j++) {
								myDb.deleteData(mSelectedItems.get(j).toString());
							}
						}
					})
					.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int id) {

						}
					});
		}
		return builder.create();
	}

	 public String parseXML(ArrayList<String> XML) throws IOException, ParserConfigurationException, SAXException, JSONException
	 {
	 	 ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainMenu.this,android.R.layout.select_dialog_singlechoice, XML);
	 	 AutoCompleteTextView acTextView = (AutoCompleteTextView) findViewById(R.id.outputcities);
         acTextView.setThreshold(1);
         acTextView.setAdapter(adapter);
         acTextView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				result = parent.getItemAtPosition(position).toString();
				processResult(result);
			}
		});
         
         return result;
	 }
	 
	 public void processResult(String inputCity) {
		    String newvalue = inputCity;
            acTextView.getText().clear();

		    try {
				Class ourClass = Class.forName("com.example.go_toweather.MainActivity");
				Intent ourIntent = new Intent(MainMenu.this, ourClass);
				ourIntent.putExtra("key", newvalue);
				startActivity(ourIntent);
				}
				catch(ClassNotFoundException e)
				{
					e.printStackTrace();
				}		    //intent.putExtra("key", newvalue);
		    //startActivity(intent);
		}

	 	public String[] categoryStrings()
		{
			Cursor res = myDb.getAllData();
			if(res.getCount() == 0) {
				return null;
			}
			StringBuffer buffer = new StringBuffer();
			String[] data = new String[res.getCount()];
			int i = 0;
			while (res.moveToNext()) {
				//buffer.append("Id :"+ res.getString(0)+"\n");
				//buffer.append(res.getString(1)+"\n");
				data[i] = (res.getString(1));
				i++;
			}
			return data;
		}
}

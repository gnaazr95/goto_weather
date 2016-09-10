package com.example.go_toweather;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseLocations extends SQLiteOpenHelper{
	public static final String DATABASE_NAME = "locationfavs.db";
	public static final String TABLE_NAME = "location_table";
	public static final String COLUMN_1 = "ID";
	public static final String COLUMN_2 = "CITY";



	
	public DatabaseLocations(Context context) {
		super(context, DATABASE_NAME, null, 2);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
        db.execSQL("create table " + TABLE_NAME +" (ID INTEGER PRIMARY KEY AUTOINCREMENT, CITY TEXT)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);
	}
	
	public boolean insertData(String city)
	{
		   SQLiteDatabase db = this.getWritableDatabase();
	        ContentValues contentValues = new ContentValues();
	        contentValues.put(COLUMN_2,city);
	        long result = db.insert(TABLE_NAME,null ,contentValues);
	        if(result == -1)
	            return false;
	        else
	            return true;

	}
	
	public Cursor getAllData()
	{
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor res = db.rawQuery("select * from " + TABLE_NAME, null);
		return res;
	}

	   public void deleteData (String id) {
	        SQLiteDatabase db = this.getWritableDatabase();
		    db.execSQL("DELETE FROM '" +  TABLE_NAME +"'" +
					" WHERE CITY = '" + id + "'");
	        //return db.delete(TABLE_NAME, "ID = ?",new String[] {id});
	    }
	   
	   public void deleteAllData()
	   {
	       SQLiteDatabase db = this.getWritableDatabase();
	       db.delete(TABLE_NAME, null, null);
	   }

}

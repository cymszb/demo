package com.zglue.zgluesports.bluetooth;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

/**
 * Created by yuancui on 12/10/17.
 */

public class RecordDatabase {

    private static final String  TAG = "RecordDatabase";

    /**
     * database file name
     */
    private static final String DB_FILE="record.db";

    /**
     * database version
     */
    private static final int DATABASE_VERSION=2;

    /**
     *  database table
     */
    private static final String TABLE_HEART_RATE="heart_rate";
    private static final String TABLE_TEMPERATURE="temperature";

    public final static int RECORD_TYPE_HEART_RATE = 1;
    public final static int RECORD_TYPE_TEMPERATURE = 2;

    /**
     * database columns
     */
    private static final String COL_ID = "_id"; 			//contact id
    private static final String COL_VALUE = "value"; 	//sip address
    private static final String COL_DATE = "date";
    private static final String COL_TIME = "time";
    private static final String COL_DATETIME = "date_time";	//only for order item in a query
    /*
    private static final String COL_ID = "_id"; 			//contact id
    private static final String COL_ADDRESS = "address"; 	//sip address
    private static final String COL_DATE = "date";
    private static final String COL_TIME = "time";
    private static final String COL_DATETIME = "date_time";	//only for order item in a query
    private static final String COL_FLAG_HISTORY_TYPE = "type";
    */


    private static SQLiteDatabase mDb=null;

    // synchronize lock
    private final Object mDbLock = new Object();

    private static RecordDatabase mInstance = null;

    private RecordDatabase(){}


    public synchronized static RecordDatabase getInstance(Context context){
        if(mInstance == null){
            mInstance = new RecordDatabase();
            mDb = context.openOrCreateDatabase(DB_FILE, 0, null);
        }
        mDb.beginTransaction();
        if(mDb != null){
            try{
                initDBFile();
                mDb.setTransactionSuccessful();
            }finally{
                mDb.endTransaction();
            }
        }

        return mInstance;

    }


    private static void initDBFile(){
        if(mDb != null &&  mDb.getVersion()!=DATABASE_VERSION ){
            try{
                //version certification
                if(mDb.getVersion()<DATABASE_VERSION)
                {
                    //Drop table first
                    String dropCmd = "DROP TABLE IF EXISTS "
                            + TABLE_HEART_RATE + ";";
                    mDb.execSQL(dropCmd);

                    dropCmd = "DROP TABLE IF EXISTS "
                            + TABLE_TEMPERATURE + ";";
                    mDb.execSQL(dropCmd);

                    /*Create heart rate table*/
                    String createCmd = "CREATE TABLE "
                            + TABLE_HEART_RATE
                            + "("
                            + COL_ID + " INTEGER PRIMARY KEY,"
                            + COL_VALUE + " INTEGER,"
                            + COL_DATE + " DATE,"
                            + COL_TIME + " TIME,"
                            + COL_DATETIME + " DATETIME"
                            + ");";

                    mDb.execSQL(createCmd);

                    /*Create temperature table*/
                    createCmd = "CREATE TABLE "
                            + TABLE_TEMPERATURE
                            + "("
                            + COL_ID + " INTEGER PRIMARY KEY,"
                            + COL_VALUE + " INTEGER,"
                            + COL_DATE + " DATE,"
                            + COL_TIME + " TIME,"
                            + COL_DATETIME + " DATETIME"
                            + ");";

                    mDb.execSQL(createCmd);
                }

            } catch (SQLiteException e){
                Log.e(TAG, Log.getStackTraceString(e));
            }

            mDb.setVersion(DATABASE_VERSION);
        }
    }


    public void addRecord(final RecordItem item){
        if(mDb==null)
            return ;

        final int value      = item._value;
        final Date date 	 = item._date;
        final Time time 	 = item._time;


        synchronized(mDbLock){
            ContentValues cv = new ContentValues();
            cv.put(COL_VALUE, value);
            cv.put(COL_DATE, date.toString());
            cv.put(COL_TIME, time.toString());
            cv.put(COL_DATETIME, date.toString() + " " +time.toString());
            if(item._type == RECORD_TYPE_HEART_RATE) {
                mDb.insert(TABLE_HEART_RATE, null, cv);
            }else if(item._type == RECORD_TYPE_TEMPERATURE){
                mDb.insert(TABLE_TEMPERATURE, null, cv);
            }
        }
    }

    public void removeRecord(final RecordItem item){
        if(mDb == null)
            return ;

        final String whereClause = "("
                + COL_VALUE + "==?) AND ("
                + COL_DATE + "==?) AND ("
                + COL_TIME + "==?)";
        final String[] whereArgs = new String[]{String.valueOf(item._value), item._date.toString(), item._time.toString()};
        synchronized(mDbLock){
            if(item._type == RECORD_TYPE_HEART_RATE) {
                mDb.delete(TABLE_HEART_RATE, whereClause, whereArgs);
            }else if(item._type == RECORD_TYPE_TEMPERATURE){
                mDb.delete(TABLE_TEMPERATURE, whereClause, whereArgs);
            }
        }
    }

    /*
    public final Cursor queryAllHistory(){
		if(mDb == null)
			return null;
		synchronized(mDbLock) {
			Cursor cor = mDb.query(TABLE_CONTACT, null, null, null, null, null, COL_DATETIME);
			return cor;
		}
    }
    */

    public final ArrayList<RecordItem> queryAllHeartRateRecord(){
        if(mDb == null)
            return null;


        synchronized(mDbLock) {
            Cursor cor = mDb.query(TABLE_HEART_RATE, null, null, null, null, null, COL_DATETIME + " DESC", "5");
            final ArrayList<RecordItem> items = getItemArrayFormCursor(cor);
            cor.close();
            return items;
        }
    }

    public final ArrayList<RecordItem> queryAllTemperatureRecord(){
        if(mDb == null)
            return null;


        synchronized(mDbLock) {
            Cursor cor = mDb.query(TABLE_TEMPERATURE, null, null, null, null, null, COL_DATETIME + " DESC", "5");
            final ArrayList<RecordItem> items = getItemArrayFormCursor(cor);
            cor.close();
            return items;
        }
    }

    /*
    private boolean hasAddress(String address){
        if(mDb == null)
            return false;

        final String[] columns = new String[]{
                COL_ADDRESS
        };

        final String selection = "(" + COL_ADDRESS + "==?)";
        final String[] selectionArgs = new String[]{address};

        synchronized(mDbLock){
            Cursor cor = mDb.query(TABLE_HISTORY, columns, selection, selectionArgs, null, null, null);
            boolean result = cor.moveToFirst();
            cor.close();
            return result;
        }
    }
    */

    /*
    public final HistoryItem[] queryHistoryByAddress(final String address){

        if(mDb == null)
            return null;

        final String selection = "(" + COL_ADDRESS + "==?)";
        final String[] selectionArgs = new String[]{address};

        synchronized(mDbLock) {
            Cursor cor = mDb.query(TABLE_HISTORY, null, selection, selectionArgs, null, null, COL_DATETIME + " DESC");
            final HistoryItem[] items = getItemFormCursor(cor);
            cor.close();
            return items;
        }
    }

    public final HistoryItem[] queryHistoryByDate(final String date){
        if(mDb == null)
            return null;

        final String selection = "(" + COL_DATE + "==?)";
        final String[] selectionArgs = new String[]{date};

        synchronized(mDbLock) {
            Cursor cor = mDb.query(TABLE_HISTORY, null, selection, selectionArgs, null, null, COL_DATETIME + " DESC");
            final HistoryItem[] items = getItemFormCursor(cor);
            cor.close();
            return items;
        }
    }


    public final HistoryItem[] queryHistoryByIsIncoming(){
        return null;

    }

    public final HistoryItem[] queryHistoryByIsOutgoing(){
        return null;
    }

    public final HistoryItem[] queryHistoryByIsMissing(){
        return null;
    }
    */
    public void clearRecord(int type){
        if(mDb == null)
            return;

        final String whereClause = "1";
        final String[] whereArgs = null;

        synchronized(mDbLock) {
            if(type == RECORD_TYPE_HEART_RATE) {
                mDb.delete(TABLE_HEART_RATE, whereClause, whereArgs);
            }else if(type == RECORD_TYPE_TEMPERATURE){
                mDb.delete(TABLE_TEMPERATURE, whereClause, whereArgs);
            }
        }
    }
    /*
    private final HistoryItem[] getItemFormCursor(final Cursor c){
        if(c == null )
            return null;

        if(c.moveToFirst() == false){
            c.close();
            return null;
        }

        final int row_count = c.getCount();
        HistoryItem[] items = new HistoryItem[row_count];

        int i = 0;
        do{
            items[i]._address = c.getString(c.getColumnIndex(COL_ADDRESS));
            items[i]._date = Date.valueOf(c.getString(c.getColumnIndex(COL_DATE)));
            items[i]._time = Time.valueOf(c.getString(c.getColumnIndex(COL_TIME)));
            items[i]._type = c.getInt(c.getColumnIndex(COL_FLAG_HISTORY_TYPE));

            i++;
        }while(c.moveToNext() != false);
        return items;
    }
    */
    private final ArrayList<RecordItem> getItemArrayFormCursor(final Cursor c){


        ArrayList<RecordItem> items = new ArrayList<>();

        if(c == null )
            return items;

        if(c.moveToFirst() == false){
            c.close();
            return items;
        }

        do{
            RecordItem item = new RecordItem();;
            item._value = c.getInt(c.getColumnIndex(COL_VALUE));
            item._date = Date.valueOf(c.getString(c.getColumnIndex(COL_DATE)));
            item._time = Time.valueOf(c.getString(c.getColumnIndex(COL_TIME)));
            items.add(item);
        }while(c.moveToNext() != false);
        return items;
    }
}

package com.zglue.zgluesports.bluetooth;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

/**
 * Created by yuancui on 12/10/17.
 */

public class RecordManager {

    private static final String LOG_TAG = "RecordManager";



    private static Context mContext;
    //private static HistoryDatabase mDatabase = null;

    /*Handle database in worker thread*/
    private HandlerThread	 mHistoryThread;
    private HistoryHandler   mHistoryHandler;

    //private QueryCallback mQueryCallback = null;

    //Task
    private static final int QUERY_ALL_HB = 1;
    private static final int QUERY_ALL_TEMP = 2;
    private static final int INTSERT = 3;
    private static final int CLEAR_HB = 4;
    private static final int CLEAR_TEMP = 5;



    private static class HistoryHandler extends Handler{

        public HistoryHandler(Looper looper){
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case QUERY_ALL_HB:{
                    QueryCallback cb = (QueryCallback)msg.obj;
                    if(cb == null)return;
                    ArrayList<RecordItem> datas = RecordDatabase.getInstance(mContext).queryAllHeartRateRecord();
                    cb.onDone(datas);
                }
                break;
                case QUERY_ALL_TEMP:{
                    QueryCallback cb = (QueryCallback)msg.obj;
                    if(cb == null)return;
                    ArrayList<RecordItem> datas = RecordDatabase.getInstance(mContext).queryAllTemperatureRecord();
                    cb.onDone(datas);
                }
                break;
                case INTSERT:
                    RecordDatabase.getInstance(mContext).addRecord((RecordItem)msg.obj);
                    break;
                case CLEAR_HB:
                    RecordDatabase.getInstance(mContext).clearRecord(RecordDatabase.RECORD_TYPE_HEART_RATE);
                    break;
                case CLEAR_TEMP:
                    RecordDatabase.getInstance(mContext).clearRecord(RecordDatabase.RECORD_TYPE_TEMPERATURE);
                    break;
                default:
                    Log.v(LOG_TAG, "Unhandled message: " + msg.what);
                    break;
            }
        }
    }

    public static interface QueryCallback{
        public abstract void onDone(ArrayList<RecordItem> items);
    }


    private RecordManager(Context context){
        mContext = context;
        init();
    }

    //singleton
    private static RecordManager mInstance = null;
    public synchronized static RecordManager getInstance(Context context){

        if(mInstance == null){
            mInstance = new RecordManager(context);
        }
        return mInstance;
    }

    /**
     * initialize
     */
    private void init(){
        //mDatabase = HistoryDatabase.getInstance(mContext);
        mHistoryThread = new HandlerThread("HistoryThread");
        mHistoryThread.start();
        mHistoryHandler = new HistoryHandler(mHistoryThread.getLooper());
    }


    public void queryAllHeartRate(QueryCallback cb){
        mHistoryHandler.sendMessage(mHistoryHandler.obtainMessage(QUERY_ALL_HB,cb));
    }

    public void queryAllTemperature(QueryCallback cb){
        mHistoryHandler.sendMessage(mHistoryHandler.obtainMessage(QUERY_ALL_HB,cb));
    }

    public void addRecordNow( final int type , final int value){
        Calendar c = Calendar.getInstance();
        Date date = new Date(c.get(Calendar.YEAR)-1900,c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH));
        Time time = new Time(c.get(Calendar.HOUR_OF_DAY),c.get(Calendar.MINUTE),c.get(Calendar.SECOND));

        mHistoryHandler.sendMessage(mHistoryHandler.obtainMessage(INTSERT, new RecordItem(type,value,date,time)));

        //mDatabase.addHistory(new HistoryItem(address,date,time,type));

    }

    public void clearHB(){
        mHistoryHandler.sendMessage(mHistoryHandler.obtainMessage(CLEAR_HB));
        //mDatabase.clearHistory();
    }
    public void clearTemp(){
        mHistoryHandler.sendMessage(mHistoryHandler.obtainMessage(CLEAR_TEMP));
        //mDatabase.clearHistory();
    }
}

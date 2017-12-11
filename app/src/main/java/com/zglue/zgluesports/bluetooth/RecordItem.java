package com.zglue.zgluesports.bluetooth;


import java.sql.Date;
import java.sql.Time;

/**
 * Created by yuancui on 12/10/17.
 */

public class RecordItem {


    public RecordItem(
            int type,
            int value,
            Date date,
            Time time){
        _type = type;
        _value = value;
        _date = date;
        _time = time;
    }

    public RecordItem(){}

    public int _type;
    public int _value;
    public Date _date;
    public Time _time;

}

package com.justforfun.android.bloodpressurecontrol;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;
import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Dimas on 06.02.2018.
 */

public class ListItem extends RealmObject{
    @PrimaryKey
    private int id;

    private int High; //Верхнее давление
    private int Lower; //Нижнее давление
    private int Pulse; //Пульс
    private Date Date;
    private String flag;
    private String comment;



    public ListItem(){
        Date = new Date();
    }

    public int getHigh() {
        return High;
    }

    public void setHigh(int high) {
        High = high;
    }

    public int getLower() {
        return Lower;
    }

    public void setLower(int lower) {
        Lower = lower;
    }

    public int getPulse() {
        return Pulse;
    }

    public void setPulse(int pulse) {
        Pulse = pulse;
    }

    public Date getDate() {
        return Date;
    }

    public void setDate(Date mDate) {
        this.Date = mDate;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}

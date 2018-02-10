package com.justforfun.android.bloodpressurecontrol;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;

import org.w3c.dom.Text;

import java.util.Date;
import java.text.SimpleDateFormat;

import io.realm.Realm;
import io.realm.RealmResults;

public class DialogActivity extends AppCompatActivity implements View.OnClickListener{
    //объявляю переменные
    NumberPicker nmbHigh, nmbLow, nmbPulse;
    ImageButton btnOk;
    Realm DBrealm;
    TextView txtDialogDate, txtDialogTime;
    EditText edComment;
    DatePicker datePicker;
    TimePicker timePicker;
    int id;
    int[] time;//0-год; 1-месяц; 2-день; 3-час; 4-минута
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);

        //Присваиваю елементы вью к лбъектам
        nmbHigh = (NumberPicker) findViewById(R.id.nmbHigh);
        nmbLow = (NumberPicker) findViewById(R.id.nmbLow);
        nmbPulse = (NumberPicker) findViewById(R.id.nmbPulse);
        txtDialogDate = (TextView) findViewById(R.id.txtDialogDate);
        txtDialogTime = (TextView) findViewById(R.id.txtDialogTime);
        btnOk = (ImageButton) findViewById(R.id.bntOK);
        edComment = (EditText) findViewById(R.id.edComent);

        time = new int[5];
        DBrealm = Realm.getDefaultInstance();


        //получаю данные из интента переданные при вызове этого активити

        Intent intent = getIntent();
        id = intent.getIntExtra("id",-1);
        //если переданное id НЕ ранво -1, тоесть мы редактируем запись, то мы редактируем объект
        if (id != -1) {
            DBrealm.beginTransaction();

            SetNumberPickerDefaults();
            RealmResults<ListItem> results = DBrealm.where(ListItem.class).equalTo("id", id).findAll();
            ListItem listItem = results.first();

            nmbHigh.setValue(listItem.getHigh());
            nmbLow.setValue(listItem.getLower());
            nmbPulse.setValue(listItem.getPulse());
            edComment.setText(listItem.getComment());
            Date date = listItem.getDate();
            time[0] = date.getYear();
            time[1] = date.getMonth();
            time[2] = date.getDate();
            time[3] = date.getHours();
            time[4] = date.getMinutes();


            SimpleDateFormat sdfDate = new SimpleDateFormat("dd.MM.yyyy");
            SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");


            txtDialogTime.setText(sdfTime.format(listItem.getDate()));
            txtDialogDate.setText(sdfDate.format(listItem.getDate()));

            DBrealm.commitTransaction();


        }else {//если равно -1, то мы добавляем
            SetNumberPickerDefaults();
            DBrealm.beginTransaction();

            Number results = DBrealm.where(ListItem.class).max("id");//получаем максимальное значение id
            if (results != null) {//Если нащли макс значение то прибавляем к нему 1
                id = results.intValue() + 1;
            }else {//если ничего не нашли то даем id 1
                id = 1;
            }
            DBrealm.commitTransaction();

        }


        btnOk.setOnClickListener(this);
        txtDialogDate.setOnClickListener(this);
        txtDialogTime.setOnClickListener(this);
    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.bntOK:
                Intent intent = new Intent();
                intent.putExtra("High", nmbHigh.getValue());
                intent.putExtra("Low", nmbLow.getValue());
                intent.putExtra("Pulse", nmbPulse.getValue());
                intent.putExtra("id", id);
                intent.putExtra("date", time);
                intent.putExtra("comment", edComment.getText().toString());
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.txtDialogDate:
                ShowDatePickerDialog(this);

                break;
            case R.id.txtDialogTime:
                ShowTimePickerDialog(this);
                break;
        }
    }

    public void SetNumberPickerDefaults(){
        //Ставлю максимальныйе значения
        nmbHigh.setMaxValue(250);
        nmbLow.setMaxValue(250);
        nmbPulse.setMaxValue(250);
        //Ставлю минимальные значения
        nmbHigh.setMinValue(10);
        nmbLow.setMinValue(10);
        nmbPulse.setMinValue(10);

        //Ставлю дэфолтные значения
        nmbHigh.setValue(120);
        nmbLow.setValue(80);
        nmbPulse.setValue(60);
    }

    public void ShowDatePickerDialog(Context context){
        LayoutInflater layoutInflater = getLayoutInflater();
        View v = layoutInflater.inflate(R.layout.date_picker,null);
        datePicker = (DatePicker) v.findViewById(R.id.datePicker);
        new AlertDialog.Builder(context)
                .setView(v)
                .setNegativeButton("Отмена", null)
                .setPositiveButton("Ок", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Date date = new Date(0);

                        date.setYear(datePicker.getYear()-1900);
                        time[0] = datePicker.getYear()-1900;
                        date.setMonth(datePicker.getMonth());
                        time[1] = datePicker.getMonth();
                        date.setDate(datePicker.getDayOfMonth());
                        time[2] = datePicker.getDayOfMonth();
                        SimpleDateFormat sdfDate = new SimpleDateFormat("dd.MM.yyyy");

                        txtDialogDate.setText(sdfDate.format(date));

                    }
                }).create().show();
    }
    public void ShowTimePickerDialog(Context context){
        LayoutInflater layoutInflater = getLayoutInflater();
        View v = layoutInflater.inflate(R.layout.time_picker,null);
        timePicker = (TimePicker) v.findViewById(R.id.timePicker);

        new AlertDialog.Builder(context)
                .setView(v)
                .setNegativeButton("Отмена", null)
                .setPositiveButton("Ок", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Date date = new Date(0);

                        date.setHours(timePicker.getHour());
                        time[3] = timePicker.getHour();

                        date.setMinutes(timePicker.getMinute());
                        time[4] = timePicker.getMinute();

                        SimpleDateFormat sdfDate = new SimpleDateFormat("HH:mm");


                        txtDialogTime.setText(sdfDate.format(date));

                    }
                }).create().show();
    }

}


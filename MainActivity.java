package com.justforfun.android.bloodpressurecontrol;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {

    public ItemLab mItemLab;
    public RecyclerView recyclerView;
    public ItemAdapter itemAdapter;
    public Realm DBrealm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mItemLab = ItemLab.get(this);
        DBrealm.init(this);
        DBrealm = Realm.getDefaultInstance();

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));



        updateUI(DBrealm);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                StartActivity(-1);

                updateUI(DBrealm);

            }
        });

    }

    public void StartActivity(int id){
        Intent intent = new Intent(this, DialogActivity.class);
        intent.putExtra("id", id);
        startActivityForResult(intent, 1);
    }

    public void updateUI(Realm db){
        mItemLab = ItemLab.get(this);
        recyclerView.setAdapter(new ItemAdapter(this, DBrealm.where(ListItem.class).findAllAsync(), db));
    }

    

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DBrealm.close();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data == null){return;}
        DBrealm.beginTransaction();

        ListItem listItem = new ListItem();
        listItem.setId(data.getIntExtra("id", -1));
        listItem.setHigh(data.getIntExtra("High", -1));
        listItem.setLower(data.getIntExtra("Low", -1));
        listItem.setPulse(data.getIntExtra("Pulse", -1));
        listItem.setComment(data.getStringExtra("comment"));
        Date date = new Date(0);
        int[] Date = data.getIntArrayExtra("date");
        date.setYear(Date[0]);
        date.setMonth(Date[1]);
        date.setDate(Date[2]);
        date.setHours(Date[3]);
        date.setMinutes(Date[4]);
        listItem.setDate(date);
        DBrealm.copyToRealmOrUpdate(listItem);


        DBrealm.commitTransaction();


    }
}

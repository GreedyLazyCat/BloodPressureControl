package com.justforfun.android.bloodpressurecontrol;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.os.Bundle;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {


    public RecyclerView recyclerView;
    public ItemAdapter itemAdapter;
    public Realm DBrealm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        //обновлять список и принимает базуданных реалм, которую надо передать, чтобы адаптер работал с ней
        itemAdapter = new ItemAdapter(this, DBrealm.where(ListItem.class).findAllAsync(), db);
        //устанваливаем recycler view адаптер
        recyclerView.setAdapter(itemAdapter);
        //пока хз как правильно описать
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                showDialog(MainActivity.this, (int)viewHolder.itemView.getTag());
            }
        }).attachToRecyclerView(recyclerView);
    }

    /**
     *  метод вызывающий простой диалог
     * @param context - контекст
     * @param Id - ID объекта listview в базе данных
     */
    public void showDialog(Context context, final int Id){
        /*принимает контекст и idшник элемента в базе который будет удален, если юзверь согласится*/

        new AlertDialog.Builder(context)
                .setTitle("Подтвержедение")
                .setMessage("Вы действительно хотите удалить эту запись?")
                .setNegativeButton("Нет", null)
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DBrealm.beginTransaction();
                        //ищем обЪект с idшником
                        RealmResults<ListItem> listItem = DBrealm.where(ListItem.class).equalTo("id", Id).findAll();
                        //удаляем
                        listItem.deleteAllFromRealm();
                        DBrealm.commitTransaction();
                        //опповещаем адапатер о том, что данные поменялись и нужно обновить список
                        itemAdapter.notifyDataSetChanged();
                    }
                }).create().show();

    }

    @Override
    protected void onDestroy() {
        //как сказали создатели надо закрывать соединение с базой при уничтожении активити, тоесть при выходн
        super.onDestroy();
        DBrealm.close();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //здесь принимаем данные от DialogActivity
        super.onActivityResult(requestCode, resultCode, data);

        if(data == null){return;}
        DBrealm.beginTransaction();
        //создаем обЪект listitem и устанавливаем значения его полей, чтобы записать его в базу
        ListItem listItem = new ListItem();

        listItem.setId(data.getIntExtra("id", -1));
        listItem.setHigh(data.getIntExtra("High", -1));
        listItem.setLower(data.getIntExtra("Low", -1));
        listItem.setPulse(data.getIntExtra("Pulse", -1));
        listItem.setComment(data.getStringExtra("comment"));
        //тут костыль с датой
        //создаем обЪект даты и передаем дату месяц и т.д., чтобы получить нужную нам дату
        Date date = new Date(0);
        int[] Date = data.getIntArrayExtra("date");
        date.setYear(Date[0]);
        date.setMonth(Date[1]);
        date.setDate(Date[2]);
        date.setHours(Date[3]);
        date.setMinutes(Date[4]);
        //устанавливаем обЪекту дату
        listItem.setDate(date);
        //пишем в базу или обновляем данные
        DBrealm.copyToRealmOrUpdate(listItem);


        DBrealm.commitTransaction();


    }
}

package com.justforfun.android.bloodpressurecontrol;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.BlockingDeque;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmQuery;
import io.realm.RealmResults;



/**
 * Created by Dimas on 06.02.2018.
 */

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemHolder> implements RealmChangeListener {

    Context context;
    ItemLab mItemLab;
    List<ListItem> mListItemList;
    ListItem mListItem;
    Realm DBrealm;
    private final RealmResults<ListItem> results;

    ItemAdapter(Context ctx, RealmResults<ListItem> mResults, Realm db){
        context = ctx;
        results = mResults;
        results.addChangeListener(this);
        DBrealm = db;

    }


    /*Создает ViewHolder и помещает в него вью объект на основе лайаута item*/
    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        return new ItemHolder(layoutInflater.inflate(R.layout.item, null));
    }


    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {
        holder.Bind(position);//вызываем метот холдера который свяжеты элементы с данными
    }

    @Override
    public int getItemCount() {
        return results.size();
    }


    @Override
    public void onChange(Object o) {
        notifyDataSetChanged();
    }


    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener{//Это сам холдер, его основная функция держать View элемент
        //Но я заставляюю его делать слегка больше чем он должен

        final String TAG = "LOGcatDD";
        TextView txtHigh, txtLow, txtPulse, txtDate,txtTime, txtComment;
        ImageButton btnDelete, btnEdit;
        ListItem mListItem;
        int pos;


        public ItemHolder(View itemView) {
            super(itemView);
        }

        public void Bind ( int position){
            pos = position;
            txtComment = (TextView) itemView.findViewById(R.id.txtComment);
            txtHigh = (TextView) itemView.findViewById(R.id.txtHigh);
            txtLow = (TextView) itemView.findViewById(R.id.txtLow);
            txtPulse = (TextView) itemView.findViewById(R.id.txtPulse);
            txtDate = (TextView) itemView.findViewById(R.id.txtDate);
            txtTime = (TextView) itemView.findViewById(R.id.txtTime);
            btnDelete = (ImageButton) itemView.findViewById(R.id.btnDelete);
            btnEdit = (ImageButton) itemView.findViewById(R.id.btnEdit);
            mListItem = results.get(position);

            btnDelete.setOnClickListener(this);
            btnEdit.setOnClickListener(this);

            SimpleDateFormat sdfDate = new SimpleDateFormat("dd.MM.yyyy");
            SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");


            txtTime.setText(sdfTime.format(mListItem.getDate()));
            txtDate.setText(sdfDate.format(mListItem.getDate()));
            txtHigh.setText(String.valueOf(mListItem.getHigh()));
            txtLow.setText(String.valueOf(mListItem.getLower()));
            txtPulse.setText(String.valueOf(mListItem.getPulse()));
            txtComment.setText(mListItem.getComment());


        }

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.btnDelete:
                    showDialog(context);
                    break;
                case R.id.btnEdit:
                    Intent intent = new Intent(context, DialogActivity.class);
                    intent.putExtra("id", mListItem.getId());
                    ((Activity)context).startActivityForResult(intent,1);
                    break;
            }
        }

        public void showDialog(Context context){

            new AlertDialog.Builder(context)
                    .setTitle("Подтвержедение")
                    .setMessage("Вы действительно хотите удалить эту запись?")
                    .setNegativeButton("Нет", null)
                    .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            DBrealm.beginTransaction();
                            RealmResults<ListItem> listItem = DBrealm.where(ListItem.class).equalTo("id", mListItem.getId()).findAll();
                            listItem.deleteAllFromRealm();
                            DBrealm.commitTransaction();
                            notifyDataSetChanged();
                        }
                    }).create().show();

        }

    }
}

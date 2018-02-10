package com.justforfun.android.bloodpressurecontrol;


import android.content.Context;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dimas on 06.02.2018.
 */

public class ItemLab {


    private static ItemLab itemLab;
    private static Context mContext;
    private static List<ListItem> mListItems;



    public static ItemLab get(Context context){
        if (itemLab == null)
            itemLab = new ItemLab(context);


     return itemLab;

    }


    private ItemLab(Context context){
        mContext = context.getApplicationContext();
        mListItems = new ArrayList<>();
    }



    public ListItem getItem(int position) {
        return mListItems.get(position);
    }

    public void addItem(ListItem listItem){
        mListItems.add(listItem);
    }

    public List<ListItem> getItems(){
        return mListItems;
    }

}

package gq.fokia.eatwhat;

import android.content.ContentValues;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import static gq.fokia.eatwhat.AllFoodFragment.foodList;
import static gq.fokia.eatwhat.AllFoodFragment.recyclerView;
import static gq.fokia.eatwhat.MainActivity.db;

/**
 * Created by fokia on 17-3-28.
 */

public class RecentCallBack extends MyCallBack {
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        Log.d("RecentCallBack", getClass().toString());
        int position = viewHolder.getAdapterPosition();
        //此food的recent属性清零，将数据写入数据库，实现持久化
        ContentValues contentValues = new ContentValues();
        contentValues.put("recent", 0);
        String[] args = {foodList.get(position).getName()};
        db.update("food", contentValues, "name=?", args);
        foodList.remove(position);
        recyclerView.getAdapter().notifyItemRemoved(position);
    }
}

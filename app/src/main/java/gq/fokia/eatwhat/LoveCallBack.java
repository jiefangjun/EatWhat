package gq.fokia.eatwhat;

import android.content.ContentValues;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import static gq.fokia.eatwhat.AllFoodFragment.foodList;
import static gq.fokia.eatwhat.AllFoodFragment.recyclerView;
import static gq.fokia.eatwhat.MainActivity.db;

/**
 * Created by fokia on 17-3-24.
 */

public class LoveCallBack extends MyCallBack {
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        Log.d("LoveCallBack", getClass().toString());
        int position = viewHolder.getAdapterPosition();
        //设置此food为不喜欢，将数据写入数据库，实现持久化
        //foodList.get(position).setIsLike(0);
        ContentValues contentValues = new ContentValues();
        contentValues.put("like", 0);
        String[] args = {foodList.get(position).getName()};
        db.update("food", contentValues, "name=?", args);
        recyclerView.getAdapter().notifyItemRemoved(position);
        foodList.remove(position);
        //Todo 通知，通知，数据变动
    }
}

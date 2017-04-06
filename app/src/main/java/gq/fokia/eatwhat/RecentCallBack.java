package gq.fokia.eatwhat;

import android.content.ContentValues;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

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
        Food food = foodList.get(position);
        ContentValues contentValues = new ContentValues();
        contentValues.put("recent", 0);
        String[] args = {foodList.get(position).getName()};
        db.update("food", contentValues, "name=?", args);
        foodList.remove(position);
        recyclerView.getAdapter().notifyItemRemoved(position);
        showSnackBar(viewHolder, "已从最近列表移除", food, position);
    }
    //TODO 交换顺序

    @Override
    public void showSnackBar(RecyclerView.ViewHolder view, String sentence, Food food, int position) {
        current_food = food;
        current_position = position;
        Snackbar snackbar = Snackbar.make(view.itemView, sentence, Snackbar.LENGTH_SHORT);
        snackbar.setAction("撤销", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues contentValues = new ContentValues();
                contentValues.put("recent", System.currentTimeMillis());
                String args[] = {current_food.getName()};
                db.update("food", contentValues, "name=?", args);
                foodList.add(current_position, current_food);
                recyclerView.getAdapter().notifyDataSetChanged();
            }
        });
        snackbar.show();
    }
}

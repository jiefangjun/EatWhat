package gq.fokia.eatwhat;

import android.content.ContentValues;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import static gq.fokia.eatwhat.AllFoodFragment.foodList;
import static gq.fokia.eatwhat.AllFoodFragment.recyclerView;
import static gq.fokia.eatwhat.LoveFoodFragment.current_foods;
import static gq.fokia.eatwhat.MainActivity.db;

/**
 * Created by fokia on 17-3-24.
 */

public class LoveCallBack extends MyCallBack {
    public static String disLike;
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        Log.d("LoveCallBack", getClass().toString());
        int position = viewHolder.getAdapterPosition();
        //操作的food在foodlist中顺序位置
        //设置此food为不喜欢，将数据写入数据库，实现持久化
        Food food = foodList.get(position);
        ContentValues contentValues = new ContentValues();
        contentValues.put("like", 0);
        String[] args = {foodList.get(position).getName()};
        db.update("food", contentValues, "name=?", args);
        disLike = foodList.get(position).getName();
        traversal(0);
        foodList.remove(position);
        recyclerView.getAdapter().notifyItemRemoved(position);
        showSnackBar(viewHolder, "已从喜欢列表移除", food, position);
    }

    private void traversal(int d){
        if(disLike != null){
            for (int i = 0; i < current_foods.size(); i++){
                if (disLike.equals(current_foods.get(i).getName())){
                    int p = i;
                    current_foods.get(p).setIsLike(d);
                    break;
                }
            }
        }
        return;
    }

    @Override
    public void showSnackBar(RecyclerView.ViewHolder view, String sentence, Food food, int position) {
        current_food = food;
        current_position = position;
        Snackbar snackbar = Snackbar.make(view.itemView, sentence, Snackbar.LENGTH_SHORT);
        snackbar.setAction("撤销", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                foodList.add(current_position, current_food);
                disLike = current_food.getName();
                ContentValues contentValues = new ContentValues();
                contentValues.put("like", 1);
                String [] args = {current_food.getName()};
                db.update("food", contentValues, "name=?", args);
                traversal(1);
                recyclerView.getAdapter().notifyDataSetChanged();
            }
        });
        snackbar.show();
    }
}

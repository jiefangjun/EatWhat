package gq.fokia.eatwhat;

import android.content.ContentValues;
import android.content.Context;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.util.Collections;

import static gq.fokia.eatwhat.AllFoodFragment.foodList;
import static gq.fokia.eatwhat.AllFoodFragment.recyclerView;
import static gq.fokia.eatwhat.MainActivity.db;

/**
 * Created by fokia on 17-3-11.
 */

public class MyCallBack extends ItemTouchHelper.Callback {
    private int dragFlags;
    private int swipeFlags;
    public Food current_food;
    public int current_position;
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        dragFlags = 0;
        swipeFlags = 0;
        if (recyclerView.getLayoutManager() instanceof GridLayoutManager
                || recyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {
            dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN
                    | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
        } else {
            dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            if (viewHolder.getAdapterPosition() != 0)
              swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        }
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        int fromPosition = viewHolder.getAdapterPosition();
        int toPosition = target.getAdapterPosition();
        //这里的toPosition和fromPosition结合可以设置那一项不能改变
        if (toPosition >= 2 && fromPosition != 0 && fromPosition !=1){
            if (fromPosition < toPosition) {
                //向下拖动
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(foodList, i, i + 1);
                }
            }else {
                //向上拖动
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(foodList, i, i - 1);
                }
            }
            recyclerView.getAdapter().notifyItemMoved(fromPosition,toPosition);
        }
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        final int position = viewHolder.getAdapterPosition();
        recyclerView.getAdapter().notifyItemRemoved(position);
        Food food = foodList.get(position);
        Log.d("position food name", food.getName());
        db.delete("food", "name=?", new String[]{food.getName()});
        foodList.remove(position);
        String picturePath = Environment.getExternalStorageDirectory().toString() + "/EatWhat/";
        Log.d(getClass().toString(), picturePath);
        File dir = new File(picturePath);
        if (!dir.exists()){
            dir.mkdirs();
        }
        File file = new File(picturePath, food.getName()+".jpg");
        file.delete();
        showSnackBar(viewHolder, "已删除", food, position);
    }

    public void showSnackBar(final RecyclerView.ViewHolder view, String sentence, Food food, int position){
        current_food = food;
        current_position = position;
        Snackbar snackbar = Snackbar.make(view.itemView, sentence, Snackbar.LENGTH_SHORT);
        snackbar.setAction("撤销", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                foodList.add(current_position, current_food);
                ContentValues values = new ContentValues();
                String path = Environment.getExternalStorageDirectory().toString() + "/EatWhat/" +
                        current_food.getName() + ".jpg";
                Log.d("callBack_path",path);
                values.put("name", current_food.getName());
                values.put("price", current_food.getPrice());
                values.put("introduce",current_food.getIntroduce());
                values.put("image", path);
                //TODO savepicture
                AddFoodFragment.savePicture(current_food.getBitmap(), current_food.getName());
                values.put("like", current_food.getIsLike());
                db.insert("food", null, values);
                recyclerView.getAdapter().notifyDataSetChanged();
            }
        });
        snackbar.show();
    }


    //选中放大viewHolder
    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            viewHolder.itemView.setScaleX(1.05f);
            viewHolder.itemView.setScaleY(1.05f);
        }
        super.onSelectedChanged(viewHolder, actionState);
    }

    //松开后还原viewHolder
    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        viewHolder.itemView.setScaleX(1.0f);
        viewHolder.itemView.setScaleY(1.0f);
    }

}

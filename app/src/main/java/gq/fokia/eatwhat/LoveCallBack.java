package gq.fokia.eatwhat;

import android.support.v7.widget.RecyclerView;
import android.util.Log;

import static gq.fokia.eatwhat.AllFoodFragment.foodList;
import static gq.fokia.eatwhat.AllFoodFragment.recyclerView;

/**
 * Created by fokia on 17-3-24.
 */

public class LoveCallBack extends MyCallBack {
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        Log.d("LoveCallBack", getClass().toString());
        int position = viewHolder.getAdapterPosition();
        recyclerView.getAdapter().notifyItemRemoved(position);
        foodList.get(position).setIsLike(0);

    }
}

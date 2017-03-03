package gq.fokia.eatwhat;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by fokia on 17-2-21.
 */

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.ViewHolder> implements View.OnClickListener{

    private List<Food> mFoodList;
    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    static class ViewHolder extends RecyclerView.ViewHolder{
        View foodView;
        ImageView foodImage;
        TextView foodName;
        TextView foodPrice;

        public ViewHolder(View view){
            super(view);
            foodView = view;
            foodImage = (ImageView) view.findViewById(R.id.food_image);
            foodName = (TextView) view.findViewById(R.id.food_name);
            foodPrice = (TextView) view.findViewById(R.id.food_price);
        }
    }

    public FoodAdapter(List<Food> foodList){
        mFoodList = foodList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_item ,parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.itemView.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
        Food food = mFoodList.get(position);
        holder.foodImage.setImageBitmap(food.getBitmap());
        holder.foodName.setText(food.getName());
        holder.foodPrice.setText(food.getPrice()+"");
        holder.itemView.setTag(food);
        Log.d("setTag",food.toString());
    }

    @Override
    public int getItemCount(){
        return mFoodList.size();
    }

    public static interface OnRecyclerViewItemClickListener {
        void onItemClick(View view , Food data);
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取数据
            mOnItemClickListener.onItemClick(v, (Food) v.getTag());
            if(v.getTag() != null)
            Log.d("getTag",v.getTag().toString());
        }
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
}

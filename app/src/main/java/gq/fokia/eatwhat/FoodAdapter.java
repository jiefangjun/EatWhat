package gq.fokia.eatwhat;

import android.support.v7.widget.RecyclerView;
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

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.ViewHolder> {

    private List<Food> mFoodList;

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
        holder.foodImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Food food = mFoodList.get(position);
                Toast.makeText(v.getContext(),"you clicked view"+ food.getName(),
                        Toast.LENGTH_LONG).show();
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
        Food food = mFoodList.get(position);
        holder.foodImage.setImageResource(food.getImageId());
        holder.foodName.setText(food.getName());
        holder.foodPrice.setText(food.getPrice()+"");
    }

    @Override
    public int getItemCount(){
        return mFoodList.size();
    }
}

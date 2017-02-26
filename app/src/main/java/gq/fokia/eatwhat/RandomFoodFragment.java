package gq.fokia.eatwhat;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by fokia on 17-2-22.
 */

public class RandomFoodFragment extends Fragment {
    private ImageView foodImage;
    private TextView name;
    private TextView price;
    private TextView introduce;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.random_food_fragment, container, false);
        foodImage = (ImageView) view.findViewById(R.id.random_food_image);
        name = (TextView) view.findViewById(R.id.random_name);
        price = (TextView) view.findViewById(R.id.random_price);
        introduce = (TextView) view.findViewById(R.id.introduce);
        //getRandomFood();
        return view;
    }
    /*private void getRandomFood(){
        Food food = new Food("random", 666, R.mipmap.ic_launcher);
        foodImage.setImageResource(food.getImageId());
        name.setText(food.getName());
        price.setText(food.getPrice()+"");
        introduce.setText("To Do");
    }*/
}

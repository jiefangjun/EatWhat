package gq.fokia.eatwhat;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by fokia on 17-2-21.
 */

public class AllFoodFragment extends Fragment {

    private List<Food> foodList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.allfood_fragment, container, false);
        initFoods();
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycle_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
//        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3,
//                StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        FoodAdapter adapter = new FoodAdapter(foodList);
        recyclerView.setAdapter(adapter);
        return view;
    }

    private void initFoods(){
        for (int i = 0; i < 4; i++){
            Food chicken = new Food("大盘鸡", 88, R.mipmap.ic_launcher);
            foodList.add(chicken);
            Food noodles = new Food("面条", 88, R.mipmap.ic_launcher);
            foodList.add(noodles);
            Food rice = new Food("米饭", 88, R.mipmap.ic_launcher);
            foodList.add(rice);
            Food milk = new Food("牛奶", 88, R.mipmap.ic_launcher);
            foodList.add(milk);
            Food dumpling = new Food("饺子", 88, R.mipmap.ic_launcher);
            foodList.add(dumpling);
        }
    }
//    private String getRandomLengthName(String name){
//        Random random = new Random();
//        int length = random.nextInt(20)+1;
//        StringBuilder builder = new StringBuilder();
//        for(int i=0;i<length;i++){
//            builder.append(name);
//        }
//        return builder.toString();
//    }
}

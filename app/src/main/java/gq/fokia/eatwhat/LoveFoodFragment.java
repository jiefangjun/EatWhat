package gq.fokia.eatwhat;


import android.support.v7.widget.helper.ItemTouchHelper;

import java.util.ArrayList;
import java.util.List;

import static gq.fokia.eatwhat.MainActivity.db;

/**
 * Created by fokia on 17-3-15.
 */

public class LoveFoodFragment extends AllFoodFragment {
    private List<Food> current_foods = new ArrayList<>();

    @Override
    public void onStart() {
        super.onStart();
        if(foodList.isEmpty()){
            getFoodsData();
        }
        copyList();
        cursor.close();
        foodList.clear();
        cursor = db.query("food", null, "like = 1", null, null, null, null);
        //取消recycleview上已经关联的helper
        helper.attachToRecyclerView(null);
        helper = new ItemTouchHelper(new LoveCallBack());
        helper.attachToRecyclerView(recyclerView);
    }

    public void copyList(){
        for(int i=0; i<foodList.size(); i++){
            current_foods.add(foodList.get(i));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        foodList = current_foods;
    }


}

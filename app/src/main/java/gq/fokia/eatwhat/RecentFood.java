package gq.fokia.eatwhat;

import android.support.v7.widget.helper.ItemTouchHelper;

import java.util.ArrayList;
import java.util.List;

import static gq.fokia.eatwhat.MainActivity.db;

/**
 * Created by fokia on 17-3-20.
 */

public class RecentFood extends AllFoodFragment {
    private static List<Food> recentFoods = new ArrayList<>();
    @Override
    public void onStart() {
        super.onStart();
        if(foodList.isEmpty()){
            getFoodsData();
        }
        copyList(foodList, recentFoods);
        cursor.close();
        foodList.clear();
        cursor = db.query("food", null, "recent LIKE '%'", null, null, null, "recent DESC");
        helper.attachToRecyclerView(null);
        helper = new ItemTouchHelper(new RecentCallBack());
        helper.attachToRecyclerView(recyclerView);
    }

    public void copyList(List<Food> originList, List<Food> targetList){
        targetList.clear();
        for (int i = 0; i < originList.size(); i++) {
            targetList.add(originList.get(i));
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        copyList(recentFoods, foodList);
    }
}

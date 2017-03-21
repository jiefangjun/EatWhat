package gq.fokia.eatwhat;

import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static gq.fokia.eatwhat.MainActivity.db;

/**
 * Created by fokia on 17-3-20.
 */

public class RecentFood extends AllFoodFragment {
    private List<Food> recentFoods = new ArrayList<>();
    @Override
    public void onStart() {
        super.onStart();
        if(foodList.isEmpty()){
            getFoodsData();
        }
        copyList();
        cursor.close();
        foodList.clear();
        cursor = db.query("food", null, "recent LIKE '%'", null, null, null, "recent DESC");
    }

    public void copyList(){
        for(int i=0; i<foodList.size(); i++){
            recentFoods.add(foodList.get(i));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        foodList = recentFoods;
    }
}

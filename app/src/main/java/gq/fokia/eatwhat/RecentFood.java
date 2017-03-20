package gq.fokia.eatwhat;

import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fokia on 17-3-20.
 */

public class RecentFood extends AllFoodFragment {
    public static List<Food> recentFoods = new ArrayList<>();
    @Override
    public void onStart() {
        super.onStart();
        if(recentFoods.isEmpty()){
            //Toast.makeText(getContext(),"")
        }
        foodList = recentFoods;
    }
}

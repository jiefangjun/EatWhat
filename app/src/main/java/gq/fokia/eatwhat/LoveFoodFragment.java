package gq.fokia.eatwhat;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fokia on 17-3-15.
 */

public class LoveFoodFragment extends AllFoodFragment {
    private List<Food> lovefoods = new ArrayList<>();
    //private List<Food> foodList = new ArrayList<>();
    /*public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate()
    }*/

    @Override
    public void onStart() {
        lovefoods = foodList;
        super.onStart();
        String[] columns = {"like"};
        String[] where = {"like = 1"};
        cursor.close();
        //lovefoods.clear();
        String queryLike = "SELECT * FROM food WHERE like=1";
        cursor = db.query("food", columns, "?", where, null, null, null);
        foodList = lovefoods;
    }


}

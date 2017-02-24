package gq.fokia.eatwhat;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by fokia on 17-2-23.
 */

public class AddFoodFragment extends Fragment {

    private EditText editName;
    private EditText editPrice;
    private EditText editIntroduce;
    private Button editSaveData;
    private FoodDBOpenHelper foodDBOpenHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.add_food_fragment, container, false);
        foodDBOpenHelper = new FoodDBOpenHelper(getContext(),
                "FoodClub.db", null, 1);
        editName = (EditText) view.findViewById(R.id.edit_name);
        editPrice = (EditText) view.findViewById(R.id.edit_price);
        editIntroduce = (EditText) view.findViewById(R.id.edit_introduce);
        editSaveData = (Button) view.findViewById(R.id.edit_save_data);
        editSaveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFoodsData();
                Toast.makeText(getContext(),"存储成功",Toast.LENGTH_LONG).show();
            }
        });
        return view;
    }

    private void addFoodsData(){
        SQLiteDatabase db = foodDBOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", editName.getText().toString());
        values.put("price", editPrice.getText().toString());
        values.put("introduce", editIntroduce.getText().toString());
        db.insert("food", null, values);
    }
}

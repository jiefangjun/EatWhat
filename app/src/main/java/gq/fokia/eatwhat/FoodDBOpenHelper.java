package gq.fokia.eatwhat;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Created by fokia on 17-2-22.
 */

public class FoodDBOpenHelper extends SQLiteOpenHelper {
    private static final String CREATE_FOOD = "create table food (" +
            "id integer primary key autoincrement, " +
            "name text, " +
            "price real, " +
            "image text, " +
            "introduce text, " +
            "like text, " +
            "recent text)";
    private Context mContext;
    public FoodDBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory
            factory, int version){
        super(context, name, factory, version);
        mContext = context;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_FOOD);
        Toast.makeText(mContext, "Create succeeded", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

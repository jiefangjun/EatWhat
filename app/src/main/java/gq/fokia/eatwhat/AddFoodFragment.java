package gq.fokia.eatwhat;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;

/**
 * Created by fokia on 17-2-23.
 */

public class AddFoodFragment extends Fragment {

    private EditText editName;
    private EditText editPrice;
    private EditText editIntroduce;
    private ImageView editImage;
    private Button editSaveData;
    private FoodDBOpenHelper foodDBOpenHelper;
    private Uri imageUri;
    private Bitmap bitmapImage;

    public static final int TAKE_PHOTO = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.add_food_fragment, container, false);
        foodDBOpenHelper = new FoodDBOpenHelper(getContext(),
                "FoodClub.db", null, 1);
        editName = (EditText) view.findViewById(R.id.edit_name);
        editPrice = (EditText) view.findViewById(R.id.edit_price);
        editImage = (ImageView) view.findViewById(R.id.edit_image);
        editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });
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
        //values.put("image", img().toString());
        db.insert("food", null, values);
    }

    private void takePhoto(){
        File outputImage = new File(getContext().getExternalCacheDir(), "output_image.jpg");
        try{
            if (outputImage.exists()){
                outputImage.delete();
            }
            outputImage.createNewFile();
        }catch (IOException e){
            e.printStackTrace();
        }
        if(Build.VERSION.SDK_INT >= 24){
            imageUri = FileProvider.getUriForFile(getContext(), "gq.eatwhat.fileprovider", outputImage);
        }else {
            imageUri = Uri.fromFile(outputImage);
        }
        //启动相机
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, TAKE_PHOTO);
    }

    public void setImage() {
        try {
            bitmapImage = BitmapFactory.decodeStream(getContext().getContentResolver()
                    .openInputStream(imageUri));
            editImage.setImageBitmap(bitmapImage);
            Log.d(getClass().toString(), "setImage success");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        switch (requestCode){
            case TAKE_PHOTO:
                if(resultCode == RESULT_OK){
                    setImage();
                }
                break;
            default:
                break;
        }
    }

    public byte[] img() {
             ByteArrayOutputStream image = new ByteArrayOutputStream();
             bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, image);
             return image.toByteArray();
        }
}

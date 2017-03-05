package gq.fokia.eatwhat;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;
import static gq.fokia.eatwhat.AllFoodFragment.foodList;

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
    private SQLiteDatabase db;
    private Uri imageUri;
    private Bitmap bitmapImage;
    private String absoluteImagePath;

    private String name;
    private Double price;
    private String introduce;
    private Bitmap bitmap;//初始addFoodfragment时传递的bitmap


    public static final int TAKE_PHOTO = 1;
    public static final int TAKE_CUT = 2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.add_food_fragment, container, false);
        foodDBOpenHelper = new FoodDBOpenHelper(getContext(),
                "FoodClub.db", null, 1);
        db = foodDBOpenHelper.getWritableDatabase();
        editName = (EditText) view.findViewById(R.id.edit_name);
        editPrice = (EditText) view.findViewById(R.id.edit_price);
        editImage = (ImageView) view.findViewById(R.id.edit_image);

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
        savePicture(bitmapImage);
        ContentValues values = new ContentValues();
        values.put("name", editName.getText().toString());
        values.put("price", editPrice.getText().toString());
        values.put("introduce", editIntroduce.getText().toString());
        values.put("image", absoluteImagePath);
        db.insert("food", null, values);
        String ss = editPrice.getText().toString();
        double price;
        if("".equals(ss)){
            price = 0;
        }else
        price = Double.valueOf(ss);
        foodList.add(0, new Food(editName.getText().toString(), price,
                editIntroduce.getText().toString(),
                bitmapImage));
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

    public Bitmap getBitmap(){
        try {
            BitmapFactory bitmapFactory = new BitmapFactory();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            options.inJustDecodeBounds = false;
            options.inSampleSize = 2;
            bitmap = bitmapFactory.decodeStream(getContext().getContentResolver()
                    .openInputStream(imageUri), null, options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public void setImage() {
        try {
            BitmapFactory bitmapFactory = new BitmapFactory();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            options.inJustDecodeBounds = false;
            options.inSampleSize = 2;
            bitmapImage = bitmapFactory.decodeStream(getContext().getContentResolver()
                    .openInputStream(imageUri), null, options);
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
                    cutPhoto(2,1,280,160,imageUri,TAKE_CUT);
                }
                break;
            case TAKE_CUT:
                if(resultCode == RESULT_OK) {
                    getBitmap();
                }
                break;
            default:
                break;
        }
    }

    public void savePicture(Bitmap bitmap) {
        String picturePath = Environment.getExternalStorageDirectory().toString() + "/EatWhat/";
        Log.d(getClass().toString(), picturePath);
        File dir = new File(picturePath);
        if (!dir.exists()){
            dir.mkdirs();
        }
        File file = new File(picturePath, editName.getText().toString()+".jpg");
        absoluteImagePath = picturePath + editName.getText().toString()+ ".jpg";
        Log.d("absoluteImagePath", absoluteImagePath);
        FileOutputStream out;
        try {
                out = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 40, out);
                out.flush();
                out.close();
        } catch (FileNotFoundException e) {
                e.printStackTrace();
        } catch (IOException e) {
                e.printStackTrace();
        }
    }

    //传参数的构造函数，用来初始化编辑界面
    public AddFoodFragment(String name, Double price, String introduce, Bitmap image){
        this.name = name;
        this.price = price;
        this.introduce = introduce;
        this.bitmap = image;
    }

    //默认无参构造方法
    public AddFoodFragment(){
        //nothing
    }

    @Override
    public void onResume() {
        super.onResume();
        if(name != null && price != null && bitmap != null){
            editName.setText(name);
            editPrice.setText(price+"");
            editIntroduce.setText(introduce);
            editImage.setImageBitmap(bitmap);

            editImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    takePhoto();
                }
            });

            editSaveData.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    updateData(name);
                }
            });
        }
    }

    private void updateData(String name){
        if(imageUri == null){
            String path = Environment.getExternalStorageDirectory().toString() + "/EatWhat/" + editName.toString() + ".jpg";
            imageUri = Uri.parse(path);
        }
        //setImage();
        savePicture(bitmap);
        ContentValues values = new ContentValues();
        values.put("name", editName.getText().toString());
        values.put("price", editPrice.getText().toString());
        values.put("introduce", editIntroduce.getText().toString());
        values.put("image", absoluteImagePath);
        String args[] = {name};
        db.update("food", values, "name=?" , args);
        foodList.clear();
    }

    private void cutPhoto(int aspectX,int aspectY,int outputX,int outputY,Uri uri,int requestCode) {
        // 裁剪图片意图
        Intent in = new Intent("com.android.camera.action.CROP");
        in.setDataAndType(uri, "image/*");
        in.putExtra("crop", "true");
        // 裁剪框的比例，2：1
        in.putExtra("aspectX", aspectX);
        in.putExtra("aspectY", aspectY);
        // 裁剪后输出图片的尺寸大小
        in.putExtra("outputX", outputX);
        in.putExtra("outputY", outputY);
        in.putExtra("scale", true);
        in.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        in.putExtra("return-data", true);
        in.putExtra("outputFormat", "jpg");// 图片格式
        in.putExtra("noFaceDetection", true);// 取消人脸识别
        startActivityForResult(in, requestCode);// 开启一个带有返回值的Activity
    }

}

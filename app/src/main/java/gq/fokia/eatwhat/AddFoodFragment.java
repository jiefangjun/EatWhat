package gq.fokia.eatwhat;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;
import static gq.fokia.eatwhat.AllFoodFragment.foodList;
import static gq.fokia.eatwhat.MainActivity.db;

/**
 * Created by fokia on 17-2-23.
 */

public class AddFoodFragment extends Fragment {

    private EditText editName;
    private EditText editPrice;
    private EditText editIntroduce;
    private ImageView editImage;
    private CheckBox editLike;
    private Button editSaveData;
    private Uri imageUri;
    private String absoluteImagePath;

    private String name;
    private Double price;
    private String introduce;
    private Bitmap bitmap;//初始addFoodfragment时传递的bitmap
    private int like;//传递过来的checkbox的值


    public static final int TAKE_PHOTO = 1;
    public static final int TAKE_CUT = 2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.add_food_fragment, container, false);
        editName = (EditText) view.findViewById(R.id.edit_name);
        editPrice = (EditText) view.findViewById(R.id.edit_price);
        editImage = (ImageView) view.findViewById(R.id.edit_image);
        editIntroduce = (EditText) view.findViewById(R.id.edit_introduce);
        editLike = (CheckBox) view.findViewById(R.id.edit_like);
        editSaveData = (Button) view.findViewById(R.id.edit_save_data);
        editSaveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission
                        (getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(getActivity(), new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                }else {
                    addFoodsData();
                }

            }
        });
        return view;
    }

    private void addFoodsData(){
        if(editName.getText().length() == 0 || editPrice.getText().length() == 0){
            Toast.makeText(getContext(), "至少得输入名字和价格哦", Toast.LENGTH_SHORT).show();
            return;
        }else if (bitmap == null){
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.d0);
        }
        savePicture(bitmap);

        ContentValues values = new ContentValues();
        values.put("name", editName.getText().toString());
        values.put("price", editPrice.getText().toString());
        values.put("introduce", editIntroduce.getText().toString());
        values.put("image", absoluteImagePath);
        values.put("like", setEditLike());
        db.insert("food", null, values);
        String ss = editPrice.getText().toString();
        double price;
        if("".equals(ss)){
            price = 0;
        }else
        price = Double.valueOf(ss);
        foodList.add(0, new Food(editName.getText().toString(), price,
                editIntroduce.getText().toString(),
                bitmap, setEditLike()));
        Toast.makeText(getContext(),"添加成功",Toast.LENGTH_SHORT).show();
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
            imageUri = FileProvider.getUriForFile(getContext(), "gq.fokia.eatwhat.fileprovider", outputImage);
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
            bitmap = bitmapFactory.decodeStream(getContext().getContentResolver()
            .openInputStream(imageUri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        switch (requestCode){
            case TAKE_PHOTO:
                if(resultCode == RESULT_OK){
                    cutPhoto(4,3,600,450,imageUri,TAKE_CUT);
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
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();
        } catch (FileNotFoundException e) {
                e.printStackTrace();
        } catch (IOException e) {
                e.printStackTrace();
        }
    }

    //传参数的构造函数，用来初始化编辑界面
    public AddFoodFragment(String name, Double price, String introduce, Bitmap image, int like){
        this.name = name;
        this.price = price;
        this.introduce = introduce;
        this.bitmap = image;
        this.like = like;
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
            editLike.setChecked(like == 1);

            editSaveData.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    updateData(name);
                }
            });

        }else if(bitmap != null){
            editImage.setImageBitmap(bitmap);
        }
        editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });

    }

    private void updateData(String name){
        /*if(imageUri == null){
            String path = Environment.getExternalStorageDirectory().toString() + "/EatWhat/" + editName.toString() + ".jpg";
            imageUri = Uri.parse(path);
        }
        //setImage();*/
        savePicture(bitmap);
        ContentValues values = new ContentValues();
        values.put("name", editName.getText().toString());
        values.put("price", editPrice.getText().toString());
        values.put("introduce", editIntroduce.getText().toString());
        values.put("image", absoluteImagePath);
        values.put("like", setEditLike());
        String args[] = {name};
        db.update("food", values, "name=?" , args);
        foodList.clear();
    }

    private void cutPhoto(int aspectX,int aspectY,int outputX,int outputY,Uri uri,int requestCode) {
        // 裁剪图片意图
        Intent in = new Intent("com.android.camera.action.CROP");
        in.setDataAndType(uri, "image/*");
        in.putExtra("crop", "true");
        // 裁剪框的比例
        in.putExtra("aspectX", aspectX);
        in.putExtra("aspectY", aspectY);
        // 裁剪后输出图片的尺寸大小
        in.putExtra("outputX", outputX);
        in.putExtra("outputY", outputY);
        in.putExtra("scale", true);
        in.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        in.putExtra("return-data", false);
        in.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());// 图片格式
        in.putExtra("noFaceDetection", true);// 取消人脸识别
        startActivityForResult(in, requestCode);// 开启一个带有返回值的Activity
    }

    public int setEditLike(){
        if (editLike.isChecked())
            return 1;
        else
            return 0;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 2:
                if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    addFoodsData();
                }else {
                    Toast.makeText(getContext(), "权限未被授予", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        bitmap.recycle();
    }
}

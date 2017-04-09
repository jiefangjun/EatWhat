package gq.fokia.eatwhat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import static gq.fokia.eatwhat.AllFoodFragment.doneRefresh;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private DrawerLayout mDrawerLayout;
    private Toolbar toolbar;
    private FloatingActionButton fab;
    private FragmentManager fragmentManager;
    private NavigationView navigationView;
    private AllFoodFragment allFoodFragment;
    private RecentFood recentFood;
    private LoveFoodFragment loveFoodFragment;
    private long currentTime;
    private long lastTime;
    private Boolean isFirst = true;//是否第一次打开

    public static FoodDBOpenHelper foodDBOpenHelper;
    public static SQLiteDatabase db;
    public static int lastposition = 999;//初始化数据是给定一个值，避免只有一个数据时出错，上一次随机位置

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDataBase();
        setContentView(R.layout.activity_main);
        if(ContextCompat.checkSelfPermission
                (MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
        }else {
            replaceFragment(new RandomFood());
        }
        mDrawerLayout = (DrawerLayout) findViewById(R.id.root_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        transparentState();
        initToolbar();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        //设置navigationItem图标按照自己本来颜色显示
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(doneRefresh == true || isFirst == true)
                {
                    replaceFragment(new RandomFood());
                }
            }
        });
    }
    public void transparentState(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //5.0 全透明实现
            //getWindow.setStatusBarColor(Color.TRANSPARENT)
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //4.4 全透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }
    public void initToolbar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if(toolbar != null){
            setSupportActionBar(toolbar);
        }
    }

    public void replaceFragment(Fragment fragment){
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.right_layout, fragment);
        transaction.commitAllowingStateLoss();//允许状态丢失
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_all:
                if(allFoodFragment == null){
                    allFoodFragment = new AllFoodFragment();
                }
                replaceFragment(allFoodFragment);
                navigationView.setCheckedItem(R.id.nav_all);
                break;
            case R.id.nav_add:
                replaceFragment(new AddFoodFragment());
                navigationView.setCheckedItem(R.id.nav_add);
                break;
            case R.id.nav_like:
                if(loveFoodFragment == null){
                    loveFoodFragment = new LoveFoodFragment();
                }
                replaceFragment(loveFoodFragment);
                navigationView.setCheckedItem(R.id.nav_like);
                break;
            case R.id.nav_recent:
                if (recentFood == null){
                    recentFood = new RecentFood();
                }
                replaceFragment(recentFood);
                navigationView.setCheckedItem(R.id.nav_recent);
                break;
            default:
                break;
        }
        mDrawerLayout.closeDrawers();
        isFirst = false;
        return false;
    }

    @Override
    public void onBackPressed() {
        currentTime = System.currentTimeMillis();
        if(currentTime - lastTime < 2 * 1000){
            finish();
        }else {
            Toast.makeText(this,"再按一次退出程序",Toast.LENGTH_SHORT).show();
            lastTime = currentTime;
        }
    }

    protected void initDataBase() {
        foodDBOpenHelper = new FoodDBOpenHelper(getBaseContext(),
                "FoodClub.db", null, 1);
        db = foodDBOpenHelper.getWritableDatabase();
        Log.d("MainActivity", "onStart");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 3:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    replaceFragment(new RandomFood());
                }else {
                    Toast.makeText(MainActivity.this, "权限未被授予", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }
}


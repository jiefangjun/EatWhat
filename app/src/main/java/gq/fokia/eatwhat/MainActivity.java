package gq.fokia.eatwhat;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import gq.fokia.eatwhat.Random.RandomFoodFragment;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private DrawerLayout mDrawerLayout;
    private Toolbar toolbar;
    private FloatingActionButton fab;
    private FragmentManager fragmentManager;
    private NavigationView navigationView;
    private AllFoodFragment allFoodFragment;
    private long currentTime;
    private long lastTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        replaceFragment(new RandomFoodFragment());
        mDrawerLayout = (DrawerLayout) findViewById(R.id.root_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        transparentState();
        initToolbar();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new RandomFood());
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
        //transaction.addToBackStack(null)
        transaction.commit();
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
                replaceFragment(new LoveFoodFragment());
                navigationView.setCheckedItem(R.id.nav_like);
            default:
                break;
        }
        mDrawerLayout.closeDrawers();
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
}


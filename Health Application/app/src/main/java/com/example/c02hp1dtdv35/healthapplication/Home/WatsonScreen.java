package com.example.c02hp1dtdv35.healthapplication.Home;

import android.content.Intent;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.c02hp1dtdv35.healthapplication.BarcodeScanner.BarcodeScannerActivity;
import com.example.c02hp1dtdv35.healthapplication.FoodDataDisplay.FoodDisplayFragment;
import com.example.c02hp1dtdv35.healthapplication.LoggedFoodDisplay.ShowLoggedFoodActivity;
import com.example.c02hp1dtdv35.healthapplication.Login.LoginActivity;
import com.example.c02hp1dtdv35.healthapplication.Login.UserProfileActivity;
import com.example.c02hp1dtdv35.healthapplication.R;
import com.example.c02hp1dtdv35.healthapplication.Remind.RemindActivity;
import com.example.c02hp1dtdv35.healthapplication.Remind.ReminderActivity;
import com.example.c02hp1dtdv35.healthapplication.Watson.WatsonFragment;
import com.example.c02hp1dtdv35.healthapplication.WeatherApi.WeatherActivity;

import java.util.ArrayList;
import java.util.List;

public class WatsonScreen extends AppCompatActivity {

    Toolbar mToolbar;
    ViewPager mViewPager;
    TabLayout tabLayout;

    public final String  watsonFragmentTitle="MAIN SCREEN";;
    public final String cameraFragmentTitle="CAPTURE";;
    public final String foodDisplayFragmentTitle="GET LOGS";;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watson_screen);
        initTabBar();

    }

   private void initTabBar(){
        mToolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        mViewPager=(ViewPager)findViewById(R.id.viewpager);
        initViewPager(mViewPager);
    }
    private void initViewPager(final ViewPager mViewPager){
        ViewPagerAdapter adapter=new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new WatsonFragment(),watsonFragmentTitle);
        adapter.addFragment(new CameraFragment(),cameraFragmentTitle);
        adapter.addFragment(new FoodDisplayFragment(),foodDisplayFragmentTitle);

        mViewPager.setAdapter(adapter);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                onTabChangeAction((String) tab.getText());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                onTabChangeAction((String)tab.getText());
            }
        });

    }
    public void onTabChangeAction(String tabName)throws NullPointerException{
        if(tabName==null)return;
        final int REQUEST_CAMERA_CODE=1;
        switch (tabName){
            case watsonFragmentTitle:
                break;
            case cameraFragmentTitle:
                break;
            case foodDisplayFragmentTitle:
                break;

        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater settingsMenuInflater= getMenuInflater();
        settingsMenuInflater.inflate(R.menu.menu_settings,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
//            case R.id.action_weatherApi:
//                Intent weatherIntent= new Intent(WatsonScreen.this, WeatherActivity.class);
//                startActivity(weatherIntent);
//                break;
//            case R.id.action_remind:
//                Intent reminderIntent= new Intent(WatsonScreen.this, RemindActivity.class);
//                startActivity(reminderIntent);
//                break;
            case R.id.action_get_logs:
                Intent foodLogsIntent= new Intent(WatsonScreen.this, ShowLoggedFoodActivity.class);
                startActivity(foodLogsIntent);
                break;
            case R.id.action_userprofile:
                Intent userProfileIntent= new Intent(WatsonScreen.this, UserProfileActivity.class);
                startActivity(userProfileIntent);
                break;
            case R.id.action_log_food_camera:
                Intent cameraIntent= new Intent(WatsonScreen.this,DetectorActivity.class);
                startActivity(cameraIntent);
                break;

            case R.id.action_food_barcode:
                Intent barcodeScannerIntent= new Intent(WatsonScreen.this, BarcodeScannerActivity.class);
                startActivity(barcodeScannerIntent);
                break;
            case R.id.action_remindActivity:
                Intent reminderActIntent= new Intent(WatsonScreen.this, ReminderActivity.class);
                startActivity(reminderActIntent);
                break;
            case R.id.action_signout:
                Intent signOutIntent= new Intent(WatsonScreen.this, LoginActivity.class);
                startActivity(signOutIntent);
                break;

        }


        return super.onOptionsItemSelected(item);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter{

        List<Fragment> mFragementList= new ArrayList<>();
        List<String>mFragmentNameList= new ArrayList<>();

         public ViewPagerAdapter(FragmentManager manager) {
             super(manager);
         }


         @Override
         public Fragment getItem(int position) {
             return mFragementList.get(position);
         }

         @Override
         public int getCount() {
             return mFragementList.size();
         }

         public void addFragment(Fragment fragment, String fragmentName){
             mFragementList.add(fragment);
             mFragmentNameList.add(fragmentName);
         }
         @Override
         public CharSequence getPageTitle(int position){
             return mFragmentNameList.get(position);
         }
     }

}

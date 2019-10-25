package com.alva.testvoice.Activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.alva.testvoice.R;
import com.alva.testvoice.fragment.SettingFragment;
import com.alva.testvoice.fragment.TestFragment;
import com.alva.testvoice.fragment.TopFragment;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {


   public static TextToSpeech textToSpeech;
    private ListView drawerList;
    private String[] drawTitles;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //设置状态栏透明
        Window window = getWindow();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        //设置底部导航栏颜色
        window.setNavigationBarColor(getResources().getColor(R.color.colorPrimary));

        setContentView(R.layout.activity_main);
        if(!isEnabled()){
            startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
        }
        textToSpeech = new TextToSpeech(getApplicationContext(),new TextToSpeech.OnInitListener() {

            @Override

            public void onInit(int status) {

                if (status==TextToSpeech.SUCCESS) {

                    //设置朗读语言

                    //这里要要注意一下初始化的步骤，这里是一个异步操作

                    int supported =textToSpeech.setLanguage(Locale.CHINA);

                    if ((supported!=TextToSpeech.LANG_AVAILABLE)&&(supported!=TextToSpeech.LANG_COUNTRY_AVAILABLE)) {

                        Toast.makeText(MainActivity.this, "不支持当前语言！", Toast.LENGTH_SHORT).show();

                    }

                }

            }

        });
        moveTaskToBack(true);
        drawTitles = getResources().getStringArray(R.array.drawer_title);
        drawerList = findViewById(R.id.drawer);
        drawerList.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_activated_1, drawTitles));
        drawerList.setOnItemClickListener(new DrawerItemClickListener());
    }


    private boolean isEnabled() {
        String pkgName = getPackageName();
        final String flat = Settings.Secure.getString(getContentResolver(), "enabled_notification_listeners");
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (int i = 0; i < names.length; i++) {
                final ComponentName cn = ComponentName.unflattenFromString(names[i]);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        Fragment fragment;
        switch (position) {
            case 1:
                fragment = new SettingFragment();
                break;
            case 2:
                fragment = new TestFragment();
                break;
            default:
                fragment = new TopFragment();
        }
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(drawerList);
    }


    @Override
    protected void onDestroy() {
        textToSpeech.shutdown();
        super.onDestroy();
    }
}

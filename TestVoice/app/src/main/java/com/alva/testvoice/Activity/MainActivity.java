package com.alva.testvoice.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.alva.testvoice.R;
import com.alva.testvoice.fragment.SettingDatabaseHelper;
import com.alva.testvoice.fragment.SettingFragment;
import com.alva.testvoice.fragment.TestFragment;
import com.alva.testvoice.fragment.TopFragment;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {


    public static TextToSpeech textToSpeech;
    public static boolean notificationSwitch;
    private ListView drawerList;
    private String[] drawTitles;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;

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
        if (!isEnabled()) {
            startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
        }

        InitCreate(savedInstanceState);
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
        setActionBarTitle(position);
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(drawerList);
    }

    private void setActionBarTitle(int position) {
        String title;
        if (position == 0) {
            title = getResources().getString(R.string.app_name);
        } else if (position == 1) {
            title = getResources().getString(R.string.setting);
        } else {
            title = getResources().getString(R.string.debug);
        }
        getSupportActionBar().setTitle(title);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case R.id.notification_switch:
                ContentValues settingValues = new ContentValues();
                if (notificationSwitch) {
                    notificationSwitch = false;
                    settingValues.put("VALUE", 0);
                    Toast.makeText(MainActivity.this, "stop ！", Toast.LENGTH_SHORT).show();
                } else {
                    notificationSwitch = true;
                    settingValues.put("VALUE", 1);
                    Toast.makeText(MainActivity.this, "start ！", Toast.LENGTH_SHORT).show();
                }
                SQLiteOpenHelper settingDatabaseHelper = new SettingDatabaseHelper(this);
                try {
                    SQLiteDatabase db = settingDatabaseHelper.getWritableDatabase();
                    db.update("TEXTTOSPEACH", settingValues, "NAME = ?", new String[]{"Switch"});
                    db.close();
                } catch (SQLiteException e) {
                    Toast toast = Toast.makeText(this, "Database unavailable", Toast.LENGTH_SHORT);
                    toast.show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void InitCreate(Bundle savedInstanceState) {
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {

            @Override

            public void onInit(int status) {

                if (status == TextToSpeech.SUCCESS) {

                    //设置朗读语言

                    //这里要要注意一下初始化的步骤，这里是一个异步操作

                    int supported = textToSpeech.setLanguage(Locale.CHINA);

                    if ((supported != TextToSpeech.LANG_AVAILABLE) && (supported != TextToSpeech.LANG_COUNTRY_AVAILABLE)) {

                        Toast.makeText(MainActivity.this, "不支持当前语言！", Toast.LENGTH_SHORT).show();

                    }

                }
            }

        });

        drawTitles = getResources().getStringArray(R.array.drawer_title);
        drawerLayout = findViewById(R.id.drawer_layout);
        drawerList = findViewById(R.id.drawer);
        drawerList.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_activated_1, drawTitles));
        drawerList.setOnItemClickListener(new DrawerItemClickListener());
        if (savedInstanceState == null) {
            selectItem(0);
        }
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.string.open_drawer, R.string.close_drawer) {
        };
        drawerLayout.setDrawerListener(drawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        try {
            SQLiteOpenHelper settingDatabaseHelper = new SettingDatabaseHelper(this);
            SQLiteDatabase db = settingDatabaseHelper.getWritableDatabase();
            Cursor cursor = db.query("TEXTTOSPEACH", new String[]{"NAME", "VALUE"}, null, null, null, null, "_id");
            if (cursor.moveToLast()) {
                int isOpen = cursor.getInt(1);
                if (isOpen == 1) {
                    notificationSwitch = true;
                } else {
                    notificationSwitch = false;
                }
            }
            cursor.close();
            db.close();
        } catch (SQLiteException e) {
            Toast toast = Toast.makeText(this, "Database unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }

    }

    @Override
    protected void onDestroy() {
        textToSpeech.shutdown();
        super.onDestroy();
    }
}

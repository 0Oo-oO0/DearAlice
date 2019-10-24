package com.alva.dearalice.fragment;


import android.app.Fragment;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.alva.dearalice.Activity.MainActivity;
import com.alva.dearalice.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_setting, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        try {
            SQLiteOpenHelper settingDatabaseHelper = new SettingDatabaseHelper(getActivity().getApplicationContext());
            SQLiteDatabase db = settingDatabaseHelper.getWritableDatabase();
            Cursor cursor = db.query("TEXTTOSPEACH", new String[]{"NAME", "VALUE"}, null, null, null, null, "_id");
            if(cursor.moveToFirst()){
                int rate = cursor.getInt(1);
                cursor.moveToNext();
                int pitch = cursor.getInt(1);

                Spinner spinnerRate = getView().findViewById(R.id.spinner_rate);
                spinnerRate.setOnItemSelectedListener(new RateOnItemSelectedListener());
                spinnerRate.setSelection(rate);

                Spinner spinnerPitch = getView().findViewById(R.id.spinner_pitch);
                spinnerPitch.setOnItemSelectedListener(new PitchOnItemSelectedListener());
                spinnerPitch.setSelection(pitch);
            }
            cursor.close();
            db.close();
        } catch (SQLiteException e){
            Toast toast = Toast.makeText(getActivity().getApplicationContext(),"Database unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private class RateOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String choseRate = parent.getItemAtPosition(position).toString();
            MainActivity.textToSpeech.setSpeechRate(Float.valueOf(choseRate));

            ContentValues settingValues = new ContentValues();
            settingValues.put("VALUE",position);
            SQLiteOpenHelper settingDatabaseHelper = new SettingDatabaseHelper(getActivity().getApplicationContext());
            try{
                SQLiteDatabase db = settingDatabaseHelper.getWritableDatabase();
                db.update("TEXTTOSPEACH",settingValues,"NAME = ?",new String[] {"Rate"});
                db.close();
            }catch (SQLiteException e){
                Toast toast =Toast.makeText(getActivity().getApplicationContext(),"Database unavailable",Toast.LENGTH_SHORT);
                toast.show();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    private class PitchOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String chosePitch = parent.getItemAtPosition(position).toString();
            MainActivity.textToSpeech.setPitch(Float.valueOf(chosePitch));

            ContentValues settingValues = new ContentValues();
            settingValues.put("VALUE",position);
            SQLiteOpenHelper settingDatabaseHelper = new SettingDatabaseHelper(getActivity().getApplicationContext());
            try{
                SQLiteDatabase db = settingDatabaseHelper.getWritableDatabase();
                db.update("TEXTTOSPEACH",settingValues,"NAME = ?",new String[] {"Pitch"});
                db.close();
            }catch (SQLiteException e){
                Toast toast =Toast.makeText(getActivity().getApplicationContext(),"Database unavailable",Toast.LENGTH_SHORT);
                toast.show();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }
}



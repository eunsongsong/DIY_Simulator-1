package com.example.diy_simulator;

import android.content.Context;
import android.content.SharedPreferences;

/**
 *  SharedPreferencse - 어플내에 파일 형태로 데이터를 저장하고 어플 삭제 전까지 보존
 *  SharedPreferences 를 쉽게 사용하기 위한 클래스
 */
public class PreferenceUtil {

    //프리퍼런스 KEY값
    public static final String PREFERENCE_NAME="preference_nm";
    private static PreferenceUtil preferencemodule = null;
    private static Context mContext;
    private static SharedPreferences prefs;
    private static SharedPreferences.Editor editor;

    public static PreferenceUtil getInstance(Context context) {
        mContext = context;

        if (preferencemodule == null) {
            preferencemodule = new PreferenceUtil();
        }
        if(prefs==null){
            prefs = mContext.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
            editor = prefs.edit();
        }
        return preferencemodule;
    }

    //Integer 값 저장
    public void putIntExtra(String key, int value) {
        editor.putInt(key, value);
        editor.commit();
    }

    //String 값 저장
    public void putStringExtra(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }

    public void putLongExtra(String key, long value) {
        editor.putLong(key, value);
        editor.commit();
    }

    public void putBooleanExtra(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.commit();
    }


    public int getIntExtra(String key) {
        return prefs.getInt(key, 0);
    }

    public String getStringExtra(String key) {
        return prefs.getString(key, "");
    }

    public long getLongExtra(String key) {
        return prefs.getLong(key, 0);
    }

    public boolean getBooleanExtra(String key) {
        return prefs.getBoolean(key, false);
    }

    // 저장했던 값 삭제
    public void removePreference(String key) {
        editor.remove(key).commit();
    }

    public boolean containCheck(String key) {
        return prefs.contains(key);
    }
}
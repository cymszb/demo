package com.zglue.zgluesports.bluetooth;

import com.zglue.zgluesports.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuancui on 12/9/17.
 */

public class PersonalDataManager {

    public final static String KEY_NAME="user_name";
    public final static String KEY_GENDER = "user_gender";
    public final static String KEY_AGE = "user_age";
    public final static String KEY_HEIGHT = "user_height";
    public final static String KEY_WEIGHT = "user_weight";

    public final static String KEY_TARGET_STEPS = "target_steps";
    public final static String KEY_TARGET_WEIGHT = "target_weight";
    public final static String KEY_EX_TIME = "target_ex_time";


    public final static int GENDER_MALE = 0;
    public final static int GENDER_FEMALE = 1;

    //goals
    String mName;
    int mGender;
    int mAge;
    int mHeight;
    int mWeight;

    //goals
    int mTargetSteps;
    int mTargetWeight;
    int mExerciseTime;

    private Context mContext;
    private SharedPreferences mSharedPreferences;

    private static PersonalDataManager mInstance = null;

    private PersonalDataManager(Context context){
        mContext = context;
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        initPrefs();
    }



    public static synchronized PersonalDataManager getInstance(Context context){
        if(mInstance == null){
            mInstance = new PersonalDataManager(context);
        }
        return mInstance;
    }

    private void initPrefs(){
        //SharedPreferences.Editor editor = mSharedPreferences.edit();
        //editor.putString(KEY_NAME,"zGlue");
        //editor.putInt(KEY_GENDER,0);
        //editor.putInt(KEY_AGE,24);
        //editor.putInt(KEY_HEIGHT,178);
        //editor.putInt(KEY_WEIGHT,68);
    }


    ArrayList<PersonalEditListener> PersonalEditListeners = new ArrayList<>();

    public void addPersonalEditListener(PersonalEditListener listener){
        if(PersonalEditListeners != null){
            PersonalEditListeners.add(listener);
        }
    }

    public void removePersonalEditListener(PersonalEditListener listener){
        if(PersonalEditListeners != null){
            PersonalEditListeners.remove(listener);
        }
    }



    private void notifyNameEdited(){
        if(PersonalEditListeners!=null){
            for(int i=0;i<PersonalEditListeners.size();i++){
                PersonalEditListeners.get(i).onNameEdited();
            }
        }
    }

    private void notifyGenderEdited(){
        if(PersonalEditListeners!=null){
            for(int i=0;i<PersonalEditListeners.size();i++){
                PersonalEditListeners.get(i).onGenderEdited();
            }
        }
    }

    private void notifyAgeEdited(){
        if(PersonalEditListeners!=null){
            for(int i=0;i<PersonalEditListeners.size();i++){
                PersonalEditListeners.get(i).onAgeEdited();
            }
        }
    }

    private void notifyHeightEdited(){
        if(PersonalEditListeners!=null){
            for(int i=0;i<PersonalEditListeners.size();i++){
                PersonalEditListeners.get(i).onHeightEdited();
            }
        }
    }

    private void notifyWeightEdited(){
        if(PersonalEditListeners!=null){
            for(int i=0;i<PersonalEditListeners.size();i++){
                PersonalEditListeners.get(i).onWeightEdited();
            }
        }
    }

    //My information
    public String getName(){
        return mSharedPreferences.getString(KEY_NAME,"zGlue");
    }
    public void setName(String name){
        mName = name;
        mSharedPreferences.edit().putString(KEY_NAME,name).apply();

        notifyNameEdited();
    }

    public int getGender(){return mSharedPreferences.getInt(KEY_GENDER,GENDER_MALE);}
    public void setGender(int gender){
        if(gender == GENDER_MALE || gender == GENDER_FEMALE){
            mGender = gender;
            mSharedPreferences.edit().putInt(KEY_GENDER,gender).apply();
        }
    }

    public int getAge(){
        return  mSharedPreferences.getInt(KEY_AGE,24);
    }
    public void setAge(int age){
        if(age >= 0 && age <= 120) {
            mAge = age;
            mSharedPreferences.edit().putInt(KEY_AGE,age).apply();
        }
    }


    public int getHeight(){
        return  mSharedPreferences.getInt(KEY_HEIGHT,178);
    }
    public void setHeight(int height){
        if(height >= 0 && height<=300) {
            mHeight = height;
            mSharedPreferences.edit().putInt(KEY_HEIGHT,height).apply();
            //notifyHeightEdited();
        }
    }

    public int getWeight(){
        return mSharedPreferences.getInt(KEY_WEIGHT,68);
    }
    public void setWeight(int weight){
        if(weight >= 0 && weight <= 500) {
            mWeight = weight;
            mSharedPreferences.edit().putInt(KEY_WEIGHT,weight).apply();
            //notifyWeightEdited();
        }
    }

    //My target
    public int getTargetSteps(){return mSharedPreferences.getInt(KEY_TARGET_STEPS,12000);}
    public void setTargetSteps(int steps){
        mTargetSteps = steps;
        mSharedPreferences.edit().putInt(KEY_TARGET_STEPS,steps).apply();
    }

    public int getTargetWeight(){return mSharedPreferences.getInt(KEY_TARGET_WEIGHT,68);}
    public void setTargetWeight(int weight){
        mTargetWeight = weight;
        mSharedPreferences.edit().putInt(KEY_TARGET_WEIGHT,weight).apply();
    }

    public int getExerciseTime(){return mSharedPreferences.getInt(KEY_EX_TIME,2);}
    public void setExerciseTime(int time){
        mExerciseTime = time;
        mSharedPreferences.edit().putInt(KEY_EX_TIME,time).apply();
    }


}

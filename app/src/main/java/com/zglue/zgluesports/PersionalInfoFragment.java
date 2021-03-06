package com.zglue.zgluesports;

import com.zglue.zgluesports.bluetooth.PersonalDataManager;
import com.zglue.zgluesports.bluetooth.PersonalEditListener;

import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Micki on 2017/11/5.
 */

public class PersionalInfoFragment extends Fragment {
    private static final String Tag = PersionalInfoFragment.class.getName();

    public final static int DATA_TYPE_NAME = 1;
    public final static int DATA_TYPE_GENDER = 2;
    public final static int DATA_TYPE_AGE = 3;
    public final static int DATA_TYPE_HEIGHT = 4;
    public final static int DATA_TYPE_WEIGHT = 5;

    public final static int DATA_TYPE_TARGET_STEPS = 6;
    public final static int DATA_TYPE_TARGET_WEIGHT = 7;
    public final static int DATA_TYPE_TARGET_EX_TIME = 8;

    final static String[] GENDER_LIST = {"Male","Female"};

    private View mFragmentView;
    private ListView mGoalList;
    private ListView mInfoList;

    private MeListAdapter mInfoAdapter;
    private MeListAdapter mGoalAdapter;

    private PersonalDataManager mPersonalDataManager ;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mPersonalDataManager = PersonalDataManager.getInstance(getContext());
        mFragmentView = inflater.from(this.getContext()).inflate(R.layout.me,null);

        mInfoList = (ListView)mFragmentView.findViewById(R.id.me_list_current);
        mInfoAdapter = new MeListAdapter();
        mInfoAdapter.addEntity(new MeEntity(DATA_TYPE_NAME,R.drawable.circle_blue, "Name", mPersonalDataManager.getName(),"",true));
        mInfoAdapter.addEntity(new MeEntity(DATA_TYPE_GENDER,R.drawable.circle_green, "Gender", GENDER_LIST[mPersonalDataManager.getGender()],"",true));
        mInfoAdapter.addEntity(new MeEntity(DATA_TYPE_AGE,R.drawable.circle_orange, "Age", String.valueOf(mPersonalDataManager.getAge()),"",true));
        mInfoAdapter.addEntity(new MeEntity(DATA_TYPE_HEIGHT,R.drawable.circle_pink, "Height", String.valueOf(mPersonalDataManager.getHeight())," cm",true));
        mInfoAdapter.addEntity(new MeEntity(DATA_TYPE_WEIGHT,R.drawable.circle_purple, "Weight",String.valueOf( mPersonalDataManager.getWeight())," kg",true));
        mInfoList.setAdapter(mInfoAdapter);
        mInfoList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showEditDialog((MeEntity) mInfoAdapter.getItem(position));
            }
        });

        mGoalList = (ListView)mFragmentView.findViewById(R.id.me_list_goal);
        mGoalAdapter = new MeListAdapter();
        mGoalAdapter.addEntity(new MeEntity(DATA_TYPE_TARGET_STEPS,R.drawable.circle_blue, "Target Daily Steps", String.valueOf(mPersonalDataManager.getTargetSteps()),"",true));
        mGoalAdapter.addEntity(new MeEntity(DATA_TYPE_TARGET_WEIGHT,R.drawable.circle_green, "Target Weight", String.valueOf(mPersonalDataManager.getTargetWeight())," kg",true));
        mGoalAdapter.addEntity(new MeEntity(DATA_TYPE_TARGET_EX_TIME,R.drawable.circle_purple, "Exercise time", String.valueOf(mPersonalDataManager.getExerciseTime())," hours",true));
        mGoalList.setAdapter(mGoalAdapter);

        mGoalList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showEditDialog((MeEntity) mGoalAdapter.getItem(position));
            }
        });
        return mFragmentView;
    }

    public  class MeListAdapter extends BaseAdapter{

        ArrayList<MeEntity> MeEntities = new ArrayList<>();

        public void addEntity(MeEntity entity){
            MeEntities.add(entity);
        }
        public void remoteEntity(MeEntity entity){
            MeEntities.remove(entity);
        }

        @Override
        public int getCount() {
            return MeEntities.size();
        }


        public Object getItem(int position) {
            return MeEntities.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null){
                convertView = LayoutInflater
                              .from(PersionalInfoFragment.this.getContext())
                              .inflate(R.layout.me_list_item,null);
                ViewHolder holder = new ViewHolder();
                holder.avatar = (ImageView) convertView.findViewById(R.id.item_avatar);
                holder.arrow = (ImageView) convertView.findViewById(R.id.item_arrow);
                holder.title = (TextView) convertView.findViewById(R.id.item_title);
                holder.value = (TextView) convertView.findViewById(R.id.item_value);
                convertView.setTag(holder);
            }
            ViewHolder holder = (ViewHolder)convertView.getTag();
            if(holder != null) {
                MeEntity entity = MeEntities.get(position);
                holder.title.setText(entity.title);
                holder.value.setText(entity.value + entity.unit);
                holder.avatar.setImageDrawable(getResources().getDrawable(entity.avatar_res_id));

                holder.arrow.setVisibility(entity.canEdit? View.VISIBLE : View.INVISIBLE);
            }


            return convertView;

        }
    }


    public void showEditDialog( final MeEntity entity){


        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setTitle(entity.title.toUpperCase());
        alertDialog.setIcon(entity.avatar_res_id);

        if(entity.type == DATA_TYPE_GENDER){
            alertDialog.setSingleChoiceItems(GENDER_LIST, mPersonalDataManager.getGender(), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String select = GENDER_LIST[which];
                    entity.value = select;
                    mPersonalDataManager.setGender(which);
                    dialog.cancel();
                    mInfoAdapter.notifyDataSetChanged();
                }
            });

        }else{
            View dialog = LayoutInflater.from(getContext()).inflate(R.layout.me_edit_view,null);
            final EditText text =(EditText) dialog.findViewById(R.id.edit_box);
            text.setText(entity.value);
            if(entity.type == DATA_TYPE_NAME){
                text.setInputType(EditorInfo.TYPE_CLASS_TEXT);
            } else{
                text.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
            }
            alertDialog.setView(dialog);

            alertDialog.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            switch (entity.type){
                                case DATA_TYPE_AGE:
                                    mPersonalDataManager.setAge(Integer.valueOf(text.getText().toString()));
                                    break;
                                case DATA_TYPE_NAME:
                                    mPersonalDataManager.setName(text.getText().toString());
                                    break;
                                case DATA_TYPE_HEIGHT:
                                    mPersonalDataManager.setHeight(Integer.valueOf(text.getText().toString()));
                                    break;
                                case DATA_TYPE_WEIGHT:
                                    mPersonalDataManager.setWeight(Integer.valueOf(text.getText().toString()));
                                    break;
                                case DATA_TYPE_TARGET_STEPS:
                                    mPersonalDataManager.setTargetSteps(Integer.valueOf(text.getText().toString()));
                                    break;
                                case DATA_TYPE_TARGET_WEIGHT:
                                    mPersonalDataManager.setTargetWeight(Integer.valueOf(text.getText().toString()));
                                    break;
                                case DATA_TYPE_TARGET_EX_TIME:
                                    mPersonalDataManager.setExerciseTime(Integer.valueOf(text.getText().toString()));
                                    break;
                                default:
                                    break;
                            }
                            //result = text.getText().toString();
                            entity.value = text.getText().toString();

                            dialog.cancel();
                            mInfoAdapter.notifyDataSetChanged();

                        }
                    });
        }






        alertDialog.setNegativeButton("CANCEL",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();

    }



    public class ViewHolder{
        ImageView avatar;
        TextView title;
        TextView value;
        ImageView arrow;
    }

    public  class MeEntity{
        int type;
        int avatar_res_id;
        String title;
        String value;
        String unit;
        boolean canEdit;

        public MeEntity(int tp, int a, String b, String c, String d, boolean e){
            type = tp;
            avatar_res_id = a;
            title = b;
            value = c;
            unit = d;
            canEdit = e;
        }
    }



}

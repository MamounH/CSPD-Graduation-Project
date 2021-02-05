package com.example.civilaffairs;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class OurData {

    private Context context;

    public OurData(Context context) {
        this.context = context;
    }

    public List<String> getPlace() {

        List<String> placeList = new ArrayList<>();

        placeList.add("دائرة الاحوال المدنية والجوازات - إربد");
        placeList.add("دائرة الاحوال المدنية والجوازات - البلقاء");
        placeList.add("دائرة الاحوال المدنية والجوازات - جرش");
        placeList.add("دائرة الاحوال المدنية والجوازات - الزرقاء");
        placeList.add("دائرة الاحوال المدنية والجوازات - الطفيلة");
        placeList.add("دائرة الاحوال المدنية والجوازات - عجلون");
        placeList.add("دائرة الاحوال المدنية والجوازات - العقبة");
        placeList.add("دائرة الاحوال المدنية والجوازات -الكرك");
        placeList.add("دائرة الاحوال المدنية والجوازات - عمان");
        placeList.add("دائرة الاحوال المدنية والجوازات - مادبا");
        placeList.add("دائرة الاحوال المدنية والجوازات - معان");
        placeList.add("دائرة الاحوال المدنية والجوازات - المفرق");

        return placeList;

    }

    public List<String> getSocial() {

        List<String> socialList = new ArrayList<>();

        socialList.add(context.getString(R.string.single));
        socialList.add(context.getString(R.string.married));
        socialList.add(context.getString(R.string.widower));

        return socialList;

    }

    public List<String> getOrderStatus() {

        List<String> orderStatusList = new ArrayList<>();

        orderStatusList.add(context.getString(R.string.renewing_the_family_book));


        return orderStatusList;
    }

    public List<String> getOStatus() {

        List<String> statusList = new ArrayList<>();

        statusList.add(context.getString(R.string.rejected));
        statusList.add(context.getString(R.string.approve));


        return statusList;
    }
    public List<String> getRelations(){
        List<String> relationList = new ArrayList<>();
        relationList.add(context.getString(R.string.son));
        relationList.add(context.getString(R.string.parent));
        relationList.add(context.getString(R.string.spouse));
        relationList.add(context.getString(R.string.brother));


        return relationList;
    }

}

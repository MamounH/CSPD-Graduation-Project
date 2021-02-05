package com.example.civilaffairs;

public interface ReplaceFragment  {


   void showFragment(FragmentSelect fragmentSelect);


   enum FragmentSelect{
      fragment_services,
      fragment_guide,
      fragment_setting,
      fragment_time,
   }

}


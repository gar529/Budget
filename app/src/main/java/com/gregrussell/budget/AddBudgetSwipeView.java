package com.gregrussell.budget;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by greg on 3/21/2018.
 */

public class AddBudgetSwipeView extends Activity {

    private static final int NUM_PAGES = 3;
    public static ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    public static TextView fragTitle;
    public static View topBar;
    public static ImageView optionsIcon;
    public static int swipePosition;
    DataBaseHelperCategory myDBHelper;
    ViewGroup swipeViewsLayoutContainer;
    int longClickPos;


    private static int copiedBudgetID;
    private static List<CategoryObj> unusedCategoriesList;
    private static String budgetName;
    private static ListDataObj usedCategoryListData;

    public ListDataObj getUsedCategoryListData(){
        return this.usedCategoryListData;
    }
    public void setUsedCategoryListData(ListDataObj usedCategoryListData){
        this.usedCategoryListData = usedCategoryListData;
    }

    public String getBudgetName(){
        return this.budgetName;
    }
    public void setBudgetName(String budgetName){
        this.budgetName = budgetName;
    }


    public int getCopiedBudgetID(){
        return this.copiedBudgetID;
    }
    public void setCopiedBudgetID(int copiedBudgetID){
        this.copiedBudgetID = copiedBudgetID;
    }

    public List<CategoryObj> getUnusedCategoriesList(){
        return this.unusedCategoriesList;
    }
    public void setUnusedCategoriesList(List<CategoryObj> unusedCategoriesList){
        this.unusedCategoriesList = unusedCategoriesList;
    }



    @Override
    protected void onPause(){

        super.onPause();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus){
        if(hasFocus){
            ImageView img = (ImageView)findViewById(R.id.image_view);
            Log.d("slidingTabImage test", "width of my image view is " + img.getWidth());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean previouslyStarted = prefs.getBoolean("FIRST_TIME_ADD_BUDGET", false);
        if(!previouslyStarted) {
            SharedPreferences.Editor edit = prefs.edit();
            edit.putBoolean("FIRST_TIME_ADD_BUDGET", Boolean.TRUE);
            edit.commit();
            Log.d("First Time", "Add budget for first time");
        }else{
            Log.d("First Time", "Add budget already");
        }

        setContentView(R.layout.swipe_views_layout_add_budget);
        swipePosition = 0;
        swipeViewsLayoutContainer = (ViewGroup)findViewById(R.id.swipeContainerAddBudget);

        //custom pager disables swiping between pages
        mPager = (ViewPager)findViewById(R.id.pagerAddBudget);
        mPagerAdapter = new SwipeViewsPagerAdapterAddBudget(getFragmentManager());
        mPager.setAdapter(mPagerAdapter);

        mPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        //sliding tab class allows for icon to display which fragment we are on
        SlidingTabLayoutAddBudget slide = (SlidingTabLayoutAddBudget) findViewById(R.id.sliding_tabsAddBudget);
        slide.setViewPager(mPager);



        ImageView backArrow = (ImageView)findViewById(R.id.backButtonAddNewBudgetFragment);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mPager.getCurrentItem() < 1 || mPager.getCurrentItem() > 2){
                finish();
                }
                else {
                    mPager.setCurrentItem(mPager.getCurrentItem() - 1);
                }

            }
        });




    }




    public class SwipeViewsPagerAdapterAddBudget extends FragmentStatePagerAdapter {
        public SwipeViewsPagerAdapterAddBudget(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            //display current budget first, display budget list on view to the right
            switch (position) {
                case 0:
                    return new NameBudgetFragment();
                case 1:
                    return new SelectTypeFragment();
                case 2:
                    return new AddBudgetFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }

        //icons to be use in slidingTab
        private int[] imageResId={R.drawable.circle_fragment_selector,R.drawable.circle_fragment_selector,R.drawable.circle_fragment_selector};

        @Override
        public CharSequence getPageTitle(int position) {

            //sets up tabStrip icons/text
            Drawable image = getResources().getDrawable(imageResId[position]);
            image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
            SpannableString sb = new SpannableString(" ");
            ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
            sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return sb;
        }

        public int getDrawableId(int position) {
            return imageResId[position];
        }
    }



}

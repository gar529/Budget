package com.gregrussell.budget;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * To be used with ViewPager to provide a tab indicator component which give constant feedback as to
 * the user's scroll progress.
 * <p>
 * To use the component, simply add it to your view hierarchy. Then in your
 * {@link android.app.Activity} or {@link android.support.v4.app.Fragment} call
 * {@link #setViewPager(ViewPager)} providing it the ViewPager this layout is being used for.
 * <p>
 * The colors can be customized in two ways. The first and simplest is to provide an array of colors
 * via {@link #setSelectedIndicatorColors(int...)} and {@link #setDividerColors(int...)}. The
 * alternative is via the {@link TabColorizer} interface which provides you complete control over
 * which color is used for any individual position.
 * <p>
 * The views used as tabs can be customized by calling {@link #setCustomTabView(int, int)},
 * providing the layout ID of your custom layout.
 */
public class SlidingTabLayoutAddBudget extends HorizontalScrollView {

    /**
     * Allows complete control over the colors drawn in the tab layout. Set with
     * {@link #setCustomTabColorizer(TabColorizer)}.
     */
    public interface TabColorizer {

        /**
         * @return return the color of the indicator used when {@code position} is selected.
         */
        int getIndicatorColor(int position);

        /**
         * @return return the color of the divider drawn to the right of {@code position}.
         */
        int getDividerColor(int position);

    }

    private static final int TITLE_OFFSET_DIPS = 0;
    private static final int TAB_VIEW_PADDING_DIPS_BOTTOM = 16;
    private static final int TAB_VIEW_PADDING_DIPS = 0;
    private static final int TAB_VIEW_TEXT_SIZE_SP = 12;

    private int mTitleOffset;





    private int mTabViewLayoutId;
    private int mTabViewTextViewId;

    private ViewPager mViewPager;
    private ViewPager.OnPageChangeListener mViewPagerPageChangeListener;

    private final SlidingTabStripAddBudget mTabStrip;

    public SlidingTabLayoutAddBudget(Context context) {
        this(context, null);
    }

    public SlidingTabLayoutAddBudget(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlidingTabLayoutAddBudget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        // Disable the Scroll Bar
        setHorizontalScrollBarEnabled(false);
        // Make sure that the Tab Strips fills this View
        setFillViewport(true);

        mTitleOffset = (int) (TITLE_OFFSET_DIPS * getResources().getDisplayMetrics().density);

        mTabStrip = new SlidingTabStripAddBudget(context);
        addView(mTabStrip, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    }

    /**
     * Set the custom {@link TabColorizer} to be used.
     *
     * If you only require simple custmisation then you can use
     * {@link #setSelectedIndicatorColors(int...)} and {@link #setDividerColors(int...)} to achieve
     * similar effects.
     */
    public void setCustomTabColorizer(TabColorizer tabColorizer) {
        mTabStrip.setCustomTabColorizer(tabColorizer);
    }

    /**
     * Sets the colors to be used for indicating the selected tab. These colors are treated as a
     * circular array. Providing one color will mean that all tabs are indicated with the same color.
     */
    public void setSelectedIndicatorColors(int... colors) {
        mTabStrip.setSelectedIndicatorColors(colors);
    }

    /**
     * Sets the colors to be used for tab dividers. These colors are treated as a circular array.
     * Providing one color will mean that all tabs are indicated with the same color.
     */
    public void setDividerColors(int... colors) {
        mTabStrip.setDividerColors(colors);
    }

    /**
     * Set the {@link ViewPager.OnPageChangeListener}. When using {@link SlidingTabLayoutAddBudget} you are
     * required to set any {@link ViewPager.OnPageChangeListener} through this method. This is so
     * that the layout can update it's scroll position correctly.
     *
     * @see ViewPager#setOnPageChangeListener(ViewPager.OnPageChangeListener)
     */
    public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        mViewPagerPageChangeListener = listener;
    }

    /**
     * Set the custom layout to be inflated for the tab views.
     *
     * @param layoutResId Layout id to be inflated
     * @param textViewId id of the {@link TextView} in the inflated view
     */
    public void setCustomTabView(int layoutResId, int textViewId) {
        mTabViewLayoutId = layoutResId;
        mTabViewTextViewId = textViewId;
    }

    /**
     * Sets the associated view pager. Note that the assumption here is that the pager content
     * (number of tabs and tab titles) does not change after this call has been made.
     */
    public void setViewPager(ViewPager viewPager) {
        mTabStrip.removeAllViews();

        mViewPager = viewPager;
        if (viewPager != null) {
            viewPager.setOnPageChangeListener(new InternalViewPagerListener());
            populateTabStrip();
        }
    }

    /**
     * Create a default view to be used for tabs. This is called if a custom tab view is not set via
     * {@link #setCustomTabView(int, int)}.
     */
    protected TextView createDefaultTabView(Context context) {
        TextView textView = new TextView(context);
        textView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, TAB_VIEW_TEXT_SIZE_SP);
        textView.setTypeface(Typeface.DEFAULT_BOLD);
        textView.setTextColor(Color.WHITE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // If we're running on Honeycomb or newer, then we can use the Theme's
            // selectableItemBackground to ensure that the View has a pressed state
            TypedValue outValue = new TypedValue();
            getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground,
                    outValue, true);
            textView.setBackgroundResource(outValue.resourceId);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            // If we're running on ICS or newer, enable all-caps to match the Action Bar tab style
            textView.setAllCaps(true);
        }
        int topPadding = (int) (2 * getResources().getDisplayMetrics().density);
        int padding = (int) (TAB_VIEW_PADDING_DIPS * getResources().getDisplayMetrics().density);
        textView.setPadding(padding, topPadding, padding, padding);

        return textView;
    }

    private void populateTabStrip() {
        final AddBudgetSwipeView.SwipeViewsPagerAdapterAddBudget adapter = (AddBudgetSwipeView.SwipeViewsPagerAdapterAddBudget)mViewPager.getAdapter();
        //final View.OnClickListener tabClickListener = new TabClickListener();

        for (int i = 0; i < adapter.getCount(); i++) {
            View tabView = null;
            //TextView tabTitleView = null;
            ImageView tabIconView = null;

            /*if (mTabViewLayoutId != 0) {
                // If there is a custom tab view layout id set, try and inflate it
                tabView = LayoutInflater.from(getContext()).inflate(mTabViewLayoutId, mTabStrip,
                        false);
                tabTitleView = (TextView) tabView.findViewById(mTabViewTextViewId);
            }

            if (tabView == null) {
                tabView = createDefaultTabView(getContext());
            }

            if (tabTitleView == null && TextView.class.isInstance(tabView)) {
                tabTitleView = (TextView) tabView;
            }*/

            if (tabView == null) {
                tabView = createDefaultImageView(getContext());
                Log.d("slidingTabImageAbove", "imageViw width is " + String.valueOf(createDefaultImageView(getContext()).getWidth()));
            }

            if (tabIconView == null && ImageView.class.isInstance(tabView)) {
                tabIconView = (ImageView) tabView;
            }



            tabIconView.setImageDrawable(getResources().getDrawable(adapter.getDrawableId(i)));
            if (mViewPager.getCurrentItem() == i) {
                tabIconView.setSelected(true);

            }
            //tabTitleView.setText(adapter.getPageTitle(i));
            //tabView.setOnClickListener(tabClickListener);

            mTabStrip.addView(tabIconView);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (mViewPager != null) {
            scrollToTab(mViewPager.getCurrentItem(), 0);
        }
    }

    private void scrollToTab(int tabIndex, int positionOffset) {
        final int tabStripChildCount = mTabStrip.getChildCount();
        if (tabStripChildCount == 0 || tabIndex < 0 || tabIndex >= tabStripChildCount) {
            return;
        }

        View selectedChild = mTabStrip.getChildAt(tabIndex);
        if (selectedChild != null) {
            int targetScrollX = selectedChild.getLeft() + positionOffset;

            if (tabIndex > 0 || positionOffset > 0) {
                // If we're not at the first child and are mid-scroll, make sure we obey the offset
                targetScrollX -= mTitleOffset;
            }

            scrollTo(targetScrollX, 0);
        }
    }

    private class InternalViewPagerListener implements ViewPager.OnPageChangeListener {
        private int mScrollState;

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            int tabStripChildCount = mTabStrip.getChildCount();
            if ((tabStripChildCount == 0) || (position < 0) || (position >= tabStripChildCount)) {
                return;
            }

            mTabStrip.onViewPagerPageChanged(position, positionOffset);

            View selectedTitle = mTabStrip.getChildAt(position);
            int extraOffset = (selectedTitle != null)
                    ? (int) (positionOffset * selectedTitle.getWidth())
                    : 0;
            scrollToTab(position, extraOffset);

            if (mViewPagerPageChangeListener != null) {
                mViewPagerPageChangeListener.onPageScrolled(position, positionOffset,
                        positionOffsetPixels);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            mScrollState = state;

            if (mViewPagerPageChangeListener != null) {
                mViewPagerPageChangeListener.onPageScrollStateChanged(state);
            }
        }

        @Override
        public void onPageSelected(int position) {
            Log.d("mViewPager", String.valueOf(position));



            //Change the color of the top bar, status bar, and recent app view color
            /*if(position == 0) {
                SwipeViews.swipePosition = 0;
                SwipeViews.fragTitle.setText(CurrentBudgetFragment.budgetName);
                SwipeViews.topBar.setBackgroundColor(CurrentBudgetFragment.topBarColor);
                if(Build.VERSION.SDK_INT >= 21){

                    Window window = ((Activity)getContext()).getWindow();

                    // clear FLAG_TRANSLUCENT_STATUS flag:
                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

                    // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

                    // finally change the color
                    if(CurrentBudgetFragment.topBarColor == getResources().getColor(R.color.colorListRed)) {
                        window.setStatusBarColor(ContextCompat.getColor(getContext(), R.color.colorListRedDark));
                        ActivityManager.TaskDescription taskDescription = new ActivityManager.TaskDescription(
                                getResources().getString(R.string.app_name), BitmapFactory.decodeResource(
                                getResources(), R.mipmap.budget_logo), getResources().getColor(R.color.colorListRed));
                        ((Activity)getContext()).setTaskDescription(taskDescription);
                    }else if(CurrentBudgetFragment.topBarColor == getResources().getColor(R.color.colorListGreen)) {
                        window.setStatusBarColor(ContextCompat.getColor(getContext(), R.color.colorListGreenDark));
                        ActivityManager.TaskDescription taskDescription = new ActivityManager.TaskDescription(
                                getResources().getString(R.string.app_name), BitmapFactory.decodeResource(
                                getResources(), R.mipmap.budget_logo), getResources().getColor(R.color.colorListGreen));
                        ((Activity)getContext()).setTaskDescription(taskDescription);
                    }else{
                        window.setStatusBarColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
                        ActivityManager.TaskDescription taskDescription = new ActivityManager.TaskDescription(
                                getResources().getString(R.string.app_name), BitmapFactory.decodeResource(
                                getResources(), R.mipmap.budget_logo), getResources().getColor(R.color.colorPrimary));
                        ((Activity)getContext()).setTaskDescription(taskDescription);

                    }
                }

            }*/
            //else if(position == 1) {
                SwipeViews.swipePosition = 1;
                SwipeViews.fragTitle.setText(SlidingTabLayoutAddBudget.this.getResources().getText(R.string.all_budgets));
                SwipeViews.topBar.setBackgroundColor(getResources().getColor(R.color.colorListNeutral));
                if(Build.VERSION.SDK_INT >= 21){
                    Window window = ((Activity)getContext()).getWindow();

                    // clear FLAG_TRANSLUCENT_STATUS flag:
                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

                    // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

                    // finally change the color

                    window.setStatusBarColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
                    ActivityManager.TaskDescription taskDescription = new ActivityManager.TaskDescription(
                            getResources().getString(R.string.app_name), BitmapFactory.decodeResource(
                            getResources(), R.mipmap.budget_logo), getResources().getColor(R.color.colorPrimary));
                    ((Activity)getContext()).setTaskDescription(taskDescription);

                //}
            }


            for (int i = 0; i < mTabStrip.getChildCount(); i++) {
                mTabStrip.getChildAt(i).setSelected(false);
            }
            mTabStrip.getChildAt(position).setSelected(true);

            if (mScrollState == ViewPager.SCROLL_STATE_IDLE) {
                mTabStrip.onViewPagerPageChanged(position, 0f);
                scrollToTab(position, 0);
            }

            if (mViewPagerPageChangeListener != null) {
                mViewPagerPageChangeListener.onPageSelected(position);
            }
        }

    }

    /*private class TabClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            for (int i = 0; i < mTabStrip.getChildCount(); i++) {
                if (v == mTabStrip.getChildAt(i)) {
                    mViewPager.setCurrentItem(i);
                    return;
                }
            }
        }
    }*/

    protected ImageView createDefaultImageView(Context context) {
        ImageView imageView = new ImageView(context);

        int padding = (int) (TAB_VIEW_PADDING_DIPS * getResources().getDisplayMetrics().density);
        imageView.setPadding(padding, padding, padding, padding);

        int width = (int) (getResources().getDisplayMetrics().widthPixels / mViewPager.getAdapter().getCount() );

        //width of image view is 12% of screen width / number of icons
        Log.d("getCount", String.valueOf(mViewPager.getAdapter().getCount()));
        double customWidth = (getResources().getDisplayMetrics().widthPixels / mViewPager.getAdapter().getCount() )*.12;



        imageView.setMinimumWidth((int)customWidth);


        //ses image view to center of screen by placing it at half the screen length, then subtracting width of the imageview

        //float centerx = imageView.getX() + imageView.getWidth() / 2;
        //imageView.setX(centerX);


        float screenWidth = getResources().getDisplayMetrics().widthPixels;
        float middleScreen = screenWidth / 2;
        double imagePosition = middleScreen - customWidth;

        Log.d("SlidingTabImage", "customWidth is " + customWidth + "; screen width is " + screenWidth +
                "; half is " + middleScreen + "; image view should be placed at " +
                imagePosition);

        imageView.setX((float)(((getResources().getDisplayMetrics().widthPixels) /2)- (customWidth*1.5)) );

        imageView.setId(R.id.image_view);




        return imageView;
    }

}

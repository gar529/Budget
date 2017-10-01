package com.gregrussell.budget;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.provider.ContactsContract;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by greg on 3/25/2017.
 */

public class ListViewAdapterAllBudgets extends BaseAdapter {

    List<BudgetListItemObj> budgetListItemList = new ArrayList<BudgetListItemObj>();

    private LayoutInflater inflater;
    Context context;
    int swapPosition = 0;

    String budName;
    double exp;
    double earn;
    double totSpent ;
    String ovUn;
    String fmtExp;
    String fmtEarn;
    String fmtTotSpent;
    String fmtDiff;
    String fmtEarnDiff;

    double diff;
    double earnDiff;




    public ListViewAdapterAllBudgets(Context context, List<BudgetListItemObj> budgetListItemList){

        Log.d("listViewAdapterAll", "List view adapter class has run " + budgetListItemList.size() );

        this.context = context;

        this.budgetListItemList = budgetListItemList;
        inflater = ((Activity)context).getLayoutInflater();



    }


    @Override
    public int getCount() {
        return budgetListItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {


        Log.d("listViewAdapterAll", "List view adapter getView method");
        View currentView = convertView;
        if(currentView == null){
            currentView = inflater.inflate(R.layout.list_item_layout_all_budgets,parent,false);
        }
        final View currentView1 = currentView;
        final TextView difference = (TextView)currentView.findViewById(R.id.differenceItemLayoutAllBudgets);
        final TextView overUnder = (TextView)currentView.findViewById(R.id.overUnderItemLayoutAllBudgets);
        final TextView budgetName = (TextView)currentView.findViewById(R.id.categoryNameItemLayoutAllBudgets);
        final TextView expenses = (TextView)currentView.findViewById(R.id.projectedValueItemLayoutAllBudgets);
        final TextView spent = (TextView)currentView.findViewById(R.id.spentValueItemLayoutAllBudgets);
        final TextView spentOrEarned = (TextView)currentView.findViewById(R.id.spentItemLayoutAllBudgets);
        final TextView projected = (TextView)currentView.findViewById(R.id.projectedItemLayoutAllBudgets);
        ImageView swap = (ImageView)currentView.findViewById(R.id.swapProjectedWithEarned);

        //make spent text view read "earned" for income field, otherwise read "spent"





        spentOrEarned.setText(context.getResources().getText(R.string.spent));
        projected.setText(context.getResources().getText(R.string.projected_expenses));






        //pass data from list to objects
        budName = budgetListItemList.get(position).getBudgetName();
        exp = budgetListItemList.get(position).getExpenses();
        earn = budgetListItemList.get(position).getEarned();

        totSpent = budgetListItemList.get(position).getSpent();

        //round because double doesn't know how to math
        totSpent = Math.round(totSpent *100.0)/100.0;
        exp = Math.round(exp *100.0)/100.0;
        earn = Math.round(earn *100.0)/100.0;


        diff = totSpent - exp;
        earnDiff = totSpent - earn;

        Log.d("differenceAllBudget", budName + " expenses: " + exp + ", total spent: " + totSpent + ", difference: " + diff);


        //formatter to convert double under 1000 to currency (only for difference text view)
        DecimalFormat lowFmt = new DecimalFormat("+$#,##0.00;-$#,##0.00");

        //formatter to convert double over 1000 to currency (only for difference text view)
        DecimalFormat highFmt = new DecimalFormat("+$#,##0.0;-$#,##0.0");

        //standard currency format
        NumberFormat fmt = NumberFormat.getCurrencyInstance();

        fmtExp = fmt.format(exp);
        fmtEarn = fmt.format(earn);
        fmtTotSpent = fmt.format(totSpent);



        //create shortened format of difference
        if(Math.abs(diff) < 1000){
            fmtDiff = lowFmt.format(diff);
        }else if(Math.abs(diff) >= 1000 && Math.abs(diff) < 1000000 ){
            diff = diff / 1000;
            fmtDiff = highFmt.format(diff) + "K";
        }else if (Math.abs(diff) >= 1000000 && Math.abs(diff) < 1000000000){
            diff = diff / 1000000;
            fmtDiff = highFmt.format(diff) + "M";
        }else{
            diff = diff / 1000000000;
            fmtDiff = highFmt.format(diff) + "B";
        }

        if(Math.abs(earnDiff) < 1000){
            fmtEarnDiff = lowFmt.format(earnDiff);
        }else if(Math.abs(earnDiff) >= 1000 && Math.abs(earnDiff) < 1000000 ){
            earnDiff = earnDiff / 1000;
            fmtEarnDiff = highFmt.format(earnDiff) + "K";
        }else if (Math.abs(earnDiff) >= 1000000 && Math.abs(earnDiff) < 1000000000){
            earnDiff = earnDiff / 1000000;
            fmtEarnDiff = highFmt.format(earnDiff) + "M";
        }else{
            earnDiff = earnDiff / 1000000000;
            fmtEarnDiff = highFmt.format(earnDiff) + "B";
        }

        Log.d("listviewadapter","diff is " + diff + "exp is " + exp);

        if(swapPosition == 0) {
            //convert dp value of drawable stroke to pixels
            float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, context.getResources().getDisplayMetrics());
            if (totSpent < exp) {
                ovUn = "Under";

                difference.setTextColor(context.getResources().getColor(R.color.colorListGreen));
                overUnder.setTextColor(context.getResources().getColor(R.color.colorListGreen));
                //change color of circle border
                GradientDrawable drawable = (GradientDrawable) currentView.findViewById(R.id.rightSideLayoutItemLayoutAllBudgets).getBackground();
                drawable.setStroke((int) px, context.getResources().getColor(R.color.colorListGreen));


            } else if (totSpent == exp) {
                ovUn = "Even";
                difference.setTextColor(context.getResources().getColor(R.color.colorListNeutral));
                overUnder.setTextColor(context.getResources().getColor(R.color.colorListNeutral));
                //change color of circle border
                GradientDrawable drawable = (GradientDrawable) currentView.findViewById(R.id.rightSideLayoutItemLayoutAllBudgets).getBackground();
                drawable.setStroke((int) px, context.getResources().getColor(R.color.colorListNeutral));
            } else {
                ovUn = "Over";

                difference.setTextColor(context.getResources().getColor(R.color.colorListRed));
                overUnder.setTextColor(context.getResources().getColor(R.color.colorListRed));
                //change color of circle border
                GradientDrawable drawable = (GradientDrawable) currentView.findViewById(R.id.rightSideLayoutItemLayoutAllBudgets).getBackground();
                drawable.setStroke((int) px, context.getResources().getColor(R.color.colorListRed));

            }

            Log.d("listviewadapterTrue", "over under is " + ovUn);

            budgetName.setText(budName);
            expenses.setText(fmtExp);
            spent.setText(fmtTotSpent);
            difference.setText(fmtDiff);
            overUnder.setText(ovUn);

        }else{

            //convert dp value of drawable stroke to pixels
            float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, context.getResources().getDisplayMetrics());
            if (totSpent < earn) {
                ovUn = "Under";

                difference.setTextColor(context.getResources().getColor(R.color.colorListGreen));
                overUnder.setTextColor(context.getResources().getColor(R.color.colorListGreen));
                //change color of circle border
                GradientDrawable drawable = (GradientDrawable) currentView.findViewById(R.id.rightSideLayoutItemLayoutAllBudgets).getBackground();
                drawable.setStroke((int) px, context.getResources().getColor(R.color.colorListGreen));


            } else if (totSpent == earn) {
                ovUn = "Even";
                difference.setTextColor(context.getResources().getColor(R.color.colorListNeutral));
                overUnder.setTextColor(context.getResources().getColor(R.color.colorListNeutral));
                //change color of circle border
                GradientDrawable drawable = (GradientDrawable) currentView.findViewById(R.id.rightSideLayoutItemLayoutAllBudgets).getBackground();
                drawable.setStroke((int) px, context.getResources().getColor(R.color.colorListNeutral));
            } else {
                ovUn = "Over";

                difference.setTextColor(context.getResources().getColor(R.color.colorListRed));
                overUnder.setTextColor(context.getResources().getColor(R.color.colorListRed));
                //change color of circle border
                GradientDrawable drawable = (GradientDrawable) currentView.findViewById(R.id.rightSideLayoutItemLayoutAllBudgets).getBackground();
                drawable.setStroke((int) px, context.getResources().getColor(R.color.colorListRed));

            }

            Log.d("listviewadapterElse", "over under is " + ovUn);

            budgetName.setText(budName);
            expenses.setText(fmtEarn);
            spent.setText(fmtTotSpent);
            difference.setText(fmtEarnDiff);
            overUnder.setText(ovUn);

        }

        swap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("ListViewAllBudgets", "Click " + projected.getText() + " swap position = " + swapPosition);
               if(swapPosition == 1){

                   budName = budgetListItemList.get(position).getBudgetName();
                   exp = budgetListItemList.get(position).getExpenses();
                   earn = budgetListItemList.get(position).getEarned();

                   totSpent = budgetListItemList.get(position).getSpent();

                   //round because double doesn't know how to math
                   totSpent = Math.round(totSpent *100.0)/100.0;
                   exp = Math.round(exp *100.0)/100.0;
                   earn = Math.round(earn *100.0)/100.0;


                   diff = totSpent - exp;
                   earnDiff = totSpent - earn;

                   Log.d("differenceAllBudget", budName + " expenses: " + exp + ", total spent: " + totSpent + ", difference: " + diff);


                   //formatter to convert double under 1000 to currency (only for difference text view)
                   DecimalFormat lowFmt = new DecimalFormat("+$#,##0.00;-$#,##0.00");

                   //formatter to convert double over 1000 to currency (only for difference text view)
                   DecimalFormat highFmt = new DecimalFormat("+$#,##0.0;-$#,##0.0");

                   //standard currency format
                   NumberFormat fmt = NumberFormat.getCurrencyInstance();

                   fmtExp = fmt.format(exp);
                   fmtEarn = fmt.format(earn);
                   fmtTotSpent = fmt.format(totSpent);



                   //create shortened format of difference
                   if(Math.abs(diff) < 1000){
                       fmtDiff = lowFmt.format(diff);
                   }else if(Math.abs(diff) >= 1000 && Math.abs(diff) < 1000000 ){
                       diff = diff / 1000;
                       fmtDiff = highFmt.format(diff) + "K";
                   }else if (Math.abs(diff) >= 1000000 && Math.abs(diff) < 1000000000){
                       diff = diff / 1000000;
                       fmtDiff = highFmt.format(diff) + "M";
                   }else{
                       diff = diff / 1000000000;
                       fmtDiff = highFmt.format(diff) + "B";
                   }

                   if(Math.abs(earnDiff) < 1000){
                       fmtEarnDiff = lowFmt.format(earnDiff);
                   }else if(Math.abs(earnDiff) >= 1000 && Math.abs(earnDiff) < 1000000 ){
                       earnDiff = earnDiff / 1000;
                       fmtEarnDiff = highFmt.format(earnDiff) + "K";
                   }else if (Math.abs(earnDiff) >= 1000000 && Math.abs(earnDiff) < 1000000000){
                       earnDiff = earnDiff / 1000000;
                       fmtEarnDiff = highFmt.format(earnDiff) + "M";
                   }else{
                       earnDiff = earnDiff / 1000000000;
                       fmtEarnDiff = highFmt.format(earnDiff) + "B";
                   }


                   //convert dp value of drawable stroke to pixels
                   float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, context.getResources().getDisplayMetrics());
                   if(totSpent < exp){
                       ovUn = "Under";
                       Log.d("CircleColor", "projected is more than spending, circle should be green");
                       difference.setTextColor(context.getResources().getColor(R.color.colorListGreen));
                       overUnder.setTextColor(context.getResources().getColor(R.color.colorListGreen));
                       //change color of circle border
                       GradientDrawable drawable = (GradientDrawable)currentView1.findViewById(R.id.rightSideLayoutItemLayoutAllBudgets).getBackground();
                       drawable.setStroke((int)px,context.getResources().getColor(R.color.colorListGreen));


                   }else if(totSpent == exp){
                       ovUn = "Even";
                       difference.setTextColor(context.getResources().getColor(R.color.colorListNeutral));
                       overUnder.setTextColor(context.getResources().getColor(R.color.colorListNeutral));
                       //change color of circle border
                       GradientDrawable drawable = (GradientDrawable)currentView1.findViewById(R.id.rightSideLayoutItemLayoutAllBudgets).getBackground();
                       drawable.setStroke((int)px,context.getResources().getColor(R.color.colorListNeutral));
                   }else {
                       ovUn = "Over";
                       Log.d("CircleColor", "projected is less than spending, circle should be red");
                       difference.setTextColor(context.getResources().getColor(R.color.colorListRed));
                       overUnder.setTextColor(context.getResources().getColor(R.color.colorListRed));
                       //change color of circle border
                       GradientDrawable drawable = (GradientDrawable)currentView1.findViewById(R.id.rightSideLayoutItemLayoutAllBudgets).getBackground();
                       drawable.setStroke((int)px,context.getResources().getColor(R.color.colorListRed));

                   }

                   Log.d("listviewadapterClk0", "over under is " + ovUn);

                   budgetName.setText(budgetListItemList.get(position).getBudgetName());
                   expenses.setText(fmtExp);
                   spent.setText(fmtTotSpent);
                   difference.setText(fmtDiff);
                   overUnder.setText(ovUn);

                   projected.setText(context.getResources().getText(R.string.projected_expenses));
                   populateListItem(position);
                   swapPosition = 0;
               }else{

                   budName = budgetListItemList.get(position).getBudgetName();
                   exp = budgetListItemList.get(position).getExpenses();
                   earn = budgetListItemList.get(position).getEarned();

                   totSpent = budgetListItemList.get(position).getSpent();

                   //round because double doesn't know how to math
                   totSpent = Math.round(totSpent *100.0)/100.0;
                   exp = Math.round(exp *100.0)/100.0;
                   earn = Math.round(earn *100.0)/100.0;


                   diff = totSpent - exp;
                   earnDiff = totSpent - earn;

                   Log.d("differenceAllBudget", budName + " expenses: " + exp + ", total spent: " + totSpent + ", difference: " + diff);


                   //formatter to convert double under 1000 to currency (only for difference text view)
                   DecimalFormat lowFmt = new DecimalFormat("+$#,##0.00;-$#,##0.00");

                   //formatter to convert double over 1000 to currency (only for difference text view)
                   DecimalFormat highFmt = new DecimalFormat("+$#,##0.0;-$#,##0.0");

                   //standard currency format
                   NumberFormat fmt = NumberFormat.getCurrencyInstance();

                   fmtExp = fmt.format(exp);
                   fmtEarn = fmt.format(earn);
                   fmtTotSpent = fmt.format(totSpent);



                   //create shortened format of difference
                   if(Math.abs(diff) < 1000){
                       fmtDiff = lowFmt.format(diff);
                   }else if(Math.abs(diff) >= 1000 && Math.abs(diff) < 1000000 ){
                       diff = diff / 1000;
                       fmtDiff = highFmt.format(diff) + "K";
                   }else if (Math.abs(diff) >= 1000000 && Math.abs(diff) < 1000000000){
                       diff = diff / 1000000;
                       fmtDiff = highFmt.format(diff) + "M";
                   }else{
                       diff = diff / 1000000000;
                       fmtDiff = highFmt.format(diff) + "B";
                   }

                   if(Math.abs(earnDiff) < 1000){
                       fmtEarnDiff = lowFmt.format(earnDiff);
                   }else if(Math.abs(earnDiff) >= 1000 && Math.abs(earnDiff) < 1000000 ){
                       earnDiff = earnDiff / 1000;
                       fmtEarnDiff = highFmt.format(earnDiff) + "K";
                   }else if (Math.abs(earnDiff) >= 1000000 && Math.abs(earnDiff) < 1000000000){
                       earnDiff = earnDiff / 1000000;
                       fmtEarnDiff = highFmt.format(earnDiff) + "M";
                   }else{
                       earnDiff = earnDiff / 1000000000;
                       fmtEarnDiff = highFmt.format(earnDiff) + "B";
                   }


                   //convert dp value of drawable stroke to pixels
                   float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, context.getResources().getDisplayMetrics());
                   if (totSpent < earn) {
                       ovUn = "Under";
                       Log.d("CircleColor", "earning is more than spending, circle should be green");
                       difference.setTextColor(context.getResources().getColor(R.color.colorListGreen));
                       overUnder.setTextColor(context.getResources().getColor(R.color.colorListGreen));
                       //change color of circle border
                       GradientDrawable drawable = (GradientDrawable) currentView1.findViewById(R.id.rightSideLayoutItemLayoutAllBudgets).getBackground();
                       drawable.setStroke((int) px, context.getResources().getColor(R.color.colorListGreen));


                   } else if (totSpent == earn) {
                       ovUn = "Even";
                       difference.setTextColor(context.getResources().getColor(R.color.colorListNeutral));
                       overUnder.setTextColor(context.getResources().getColor(R.color.colorListNeutral));
                       //change color of circle border
                       GradientDrawable drawable = (GradientDrawable) currentView1.findViewById(R.id.rightSideLayoutItemLayoutAllBudgets).getBackground();
                       drawable.setStroke((int) px, context.getResources().getColor(R.color.colorListNeutral));
                   } else {
                       ovUn = "Over";
                       Log.d("CircleColor", "earning is less than spending, circle should be red");
                       difference.setTextColor(context.getResources().getColor(R.color.colorListRed));
                       overUnder.setTextColor(context.getResources().getColor(R.color.colorListRed));
                       //change color of circle border
                       GradientDrawable drawable = (GradientDrawable) currentView1.findViewById(R.id.rightSideLayoutItemLayoutAllBudgets).getBackground();
                       drawable.setStroke((int) px, context.getResources().getColor(R.color.colorListRed));

                   }

                   Log.d("listviewadapterClk1", "over under is " + ovUn);

                   budgetName.setText(budgetListItemList.get(position).getBudgetName());
                   expenses.setText(fmtEarn);
                   spent.setText(fmtTotSpent);
                   difference.setText(fmtEarnDiff);
                   overUnder.setText(ovUn);

                   projected.setText(context.getResources().getText(R.string.earned));
                   populateListItem(position);
                   swapPosition = 1;
               }
            }
        });


        return currentView;


    }

    private void populateListItem(int position){

        Log.d("ListViewAllBudgets", "position is " + position);




    }
}
package com.gregrussell.budget;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by greg on 3/24/2018.
 */

public class ListViewAdapterAddBudgetFragment extends BaseAdapter {

    ListDataObj listData = new ListDataObj();
    private LayoutInflater inflater;
    Context context;

    public ListViewAdapterAddBudgetFragment(Context context, ListDataObj listData){

        Log.d("listViewAdapterAll", "List view adapter class has run " + listData.getCategoryList().size() );
        this.context = context;
        this.listData = listData;
        inflater = ((Activity)context).getLayoutInflater();
    }


    @Override
    public int getCount() {
        return listData.getCategoryList().size();
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

        View currentView = convertView;
        if(currentView == null){
            currentView = inflater.inflate(R.layout.list_item_layout_copy_budgets,parent,false);
        }

        TextView categoryName = (TextView)currentView.findViewById(R.id.copyBudgetListCategoryName);
        TextView spentText = (TextView)currentView.findViewById(R.id.copyBudgetListSpent);
        TextView amount = (TextView)currentView.findViewById(R.id.copyBudgetAmount);

        List<String> categoryList = listData.getCategoryList();
        List<Double> expenseList = listData.getSpentList();
        List<Double> income = listData.getSpentList();

        //first item in the list will be income related. not using that so skip


        String category;
        double expense;


        //standard currency format
        NumberFormat fmt = NumberFormat.getCurrencyInstance();



        if(position == 0){
            category = (context.getResources().getString(R.string.total_expenses));
            expense = listData.getTotalSpent();
        }else{
            category = categoryList.get(position);
            spentText.setText(R.string.projected_expense);
            expense = expenseList.get(position);
        }

        String fmtExpense = fmt.format(expense);
        categoryName.setText(category);
        amount.setText(fmtExpense);
        Log.d("CopyBudget", "formated Category is " + category + "; expense is " + fmtExpense);


        return currentView;


    }
}

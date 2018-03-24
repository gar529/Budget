package com.gregrussell.budget;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by greg on 3/31/2017.
 */

public class ListViewAdapterAddBudget extends BaseAdapter {

    ListDataObj listData = new ListDataObj();
    private LayoutInflater inflater;
    Context context;

    public ListViewAdapterAddBudget(Context context, ListDataObj listData){

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

        Log.d("listViewAdapterAll", "List view adapter getView method");
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



        /*if(position == 0){
            category = (context.getResources().getString(R.string.total_expenses));
            expense = listData.getTotalSpent();
        }else{
            category = categoryList.get(position);
            spentText.setText(R.string.projected_expense);
            expense = expenseList.get(position);
        }*/


        category = categoryList.get(position);
        spentText.setText(R.string.projected_expense);
        expense = expenseList.get(position);

        String fmtExpense = fmt.format(expense);
        categoryName.setText(category);
        amount.setText(fmtExpense);


        /*remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("categoriesList adapter", "categoryObjList size is " + categoryObjList.size() + " removed will be " + categoryObjList.get(position).getCategoryName());
                Log.d("categoriesList adapter", "position is " + categoryObjList.size());
                Log.d("categoriesList adapter", "removed category id is " + categoryObjList.get(position).getID());

                //categories in the DB will have an ID. only add those
                if(categoryObjList.get(position).getID() != 0) {
                    AddBudget.unusedCategoriesList.add(categoryObjList.get(position));
                }
                Log.d("categoriesList adapter", "added to unused list is " + categoryObjList.get(position).getCategoryName());
                AddBudget.usedCategoriesList.remove(position);

                ListViewAdapterAddBudget adapter = new ListViewAdapterAddBudget(context, categoryObjList);
                AddBudget.listViewCategoriesAdd.setAdapter(null);
                AddBudget.listViewCategoriesAdd.setAdapter(adapter);
            }
        });*/

        return currentView;


    }
}
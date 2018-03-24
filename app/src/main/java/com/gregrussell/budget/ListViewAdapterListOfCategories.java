package com.gregrussell.budget;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by greg on 3/21/2018.
 */

public class ListViewAdapterListOfCategories extends BaseAdapter {

    List<BudgetListItemObj> budgetListItemList = new ArrayList<BudgetListItemObj>();
    private LayoutInflater inflater;
    Context context;

    public ListViewAdapterListOfCategories(Context context, List<BudgetListItemObj> budgetListItemList){



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
    public View getView(int position, View convertView, ViewGroup parent) {

        Log.d("listOfCategories", "made it here");

        View currentView = convertView;
        if(currentView == null){
            currentView = inflater.inflate(R.layout.list_of_categories,parent,false);
        }

        TextView categoriesText = (TextView)currentView.findViewById(R.id.listOfCategories);
        String category = budgetListItemList.get(position).getBudgetName();
        categoriesText.setText(category);

        return currentView;
    }
}

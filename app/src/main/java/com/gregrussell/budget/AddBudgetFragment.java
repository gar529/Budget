package com.gregrussell.budget;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.database.SQLException;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;

/**
 * Created by greg on 3/21/2018.
 */

public class AddBudgetFragment extends Fragment {


    public static ListView categoryList;

    Context mContext;

    @Override
    public void onResume(){
        super.onResume();

        Log.d("AddBudgetFragment", "resuming");
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {



        mContext = getActivity();
        DataBaseHelperCategory myDBHelper = new DataBaseHelperCategory(mContext);
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.add_budget_fragment_layout, container, false);
        categoryList = (ListView)rootView.findViewById(R.id.addBudgetFragmentListView);
        AddBudgetSwipeView addBudgetObject = new AddBudgetSwipeView();
        TextView budgetNameTextView = (TextView)rootView.findViewById(R.id.addBudgetFragmentName);
        budgetNameTextView.setText(addBudgetObject.getBudgetName());

        Log.d("addBudgetFragment", "created " + addBudgetObject.getBudgetName());

        try {
            myDBHelper.createDataBase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }
        try {
            myDBHelper.openDataBase();
        } catch (SQLException sqle) {
            throw sqle;
        }

        try {
            ListViewAdapterAddBudgetFragment adapter = new ListViewAdapterAddBudgetFragment(mContext, addBudgetObject.getUsedCategoryListData());
            categoryList.setAdapter(adapter);

        }catch (Exception e){
            Log.d("addBudgetFragment", "used category list data probably null");
        }






        return rootView;

    }

}


package com.gregrussell.budget;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.database.SQLException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;

/**
 * Created by greg on 3/21/2018.
 */

public class SelectTypeFragment extends Fragment {




    Context mContext;
    AddBudgetSwipeView addBudgetObject = new AddBudgetSwipeView();
    String myString;


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        mContext = getActivity();
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.select_type_fragment_layout, container, false);



        Button copyBudgetButton = (Button)rootView.findViewById(R.id.selectTypeFragmentCopy);
        copyBudgetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //AddBudgetSwipeView.mPager.setCurrentItem(2);


                myString = addBudgetObject.getBudgetName();
                Log.d("typebudgetFrag", "clicked the button, budget name is " + myString);
                copyBudgetListDialog();
            }
        });

        Button newBudgetButton = (Button)rootView.findViewById(R.id.selectTypeFragmentNew);
        newBudgetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AsyncNewBudget task = new AsyncNewBudget();
                task.execute();

            }
        });

        DataBaseHelperCategory myDBHelper = new DataBaseHelperCategory(mContext);
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

        if(myDBHelper.getMostRecentBudget() == -1){
            copyBudgetButton.setEnabled(false);
        }else{
            copyBudgetButton.setEnabled(true);
        }





        return rootView;

    }

    private void copyBudgetListDialog(){

        DataBaseHelperCategory myDBHelper = new DataBaseHelperCategory(mContext);
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

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View copyBudgetCategoryList = inflater.inflate(R.layout.copy_category_list_dialog,null);
        final ListView categoryList = (ListView)copyBudgetCategoryList.findViewById(R.id.copy_category_list_dialog_list_view);

        final List<BudgetListItemObj> budgetList = myDBHelper.getAllBudgetsList();
        final ListViewAdapterListOfCategories adapter = new ListViewAdapterListOfCategories(mContext,budgetList);

        categoryList.setAdapter(adapter);


        //create a dialog box to enter new projected expenses
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(copyBudgetCategoryList);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setTitle("Copy Budget")
                .setPositiveButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        // create alert dialog
        final AlertDialog alertDialog = alertDialogBuilder.create();
        categoryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                addBudgetObject.setCopiedBudgetID(budgetList.get(position).getBudgetID());
                Log.d("copyBudget", "selected budget is " + addBudgetObject.getCopiedBudgetID());
                alertDialog.dismiss();
                copyBudgetSelectedDialog(addBudgetObject.getCopiedBudgetID());
            }
        });


        alertDialog.show();
    }

    private void copyBudgetSelectedDialog(int budgetID){

        DataBaseHelperCategory myDBHelper = new DataBaseHelperCategory(mContext);
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

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View copyBudgetCategoryList = inflater.inflate(R.layout.copy_category_list_dialog,null);
        final ListView listView = (ListView)copyBudgetCategoryList.findViewById(R.id.copy_category_list_dialog_list_view);

        //final ListDataObj listData = myDBHelper.createListData(budgetID);
        addBudgetObject.setUsedCategoryListData(myDBHelper.createListData(budgetID));
        final ListViewAdapterCopyBudgetList adapter = new ListViewAdapterCopyBudgetList(mContext,addBudgetObject.getUsedCategoryListData());
        listView.setAdapter(adapter);


        //create a dialog box to enter new projected expenses
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(copyBudgetCategoryList);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setTitle("Copy Budget \"" + addBudgetObject.getUsedCategoryListData().getBudgetName()+ "\"?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        AsyncCopyPreviousBudget task = new AsyncCopyPreviousBudget();
                        task.execute();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setNeutralButton(getResources().getString(R.string.back), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        copyBudgetListDialog();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();



        alertDialog.show();

    }

    private class AsyncCopyPreviousBudget extends AsyncTask<Void,Void,Boolean> {

        int budgetID = addBudgetObject.getCopiedBudgetID();
        ListDataObj listData = addBudgetObject.getUsedCategoryListData();



        @Override
        protected Boolean doInBackground(Void... params) {

            DataBaseHelperCategory myDBHelper = new DataBaseHelperCategory(mContext);
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

            addBudgetObject.setUnusedCategoriesList(myDBHelper.getUnusedCategories(budgetID));

            return null;
        }

        @Override
        protected void onPostExecute(Boolean result){

            LayoutInflater inflater = getActivity().getLayoutInflater();
            ViewGroup view = (ViewGroup)inflater.inflate(R.layout.add_budget_fragment_layout,null);
            //ListView list = (ListView)view.findViewById(R.id.addBudgetFragmentListView);
            ListViewAdapterAddBudgetFragment adapter = new ListViewAdapterAddBudgetFragment(mContext, listData);
            AddBudgetFragment.categoryList.setAdapter(null);
            AddBudgetFragment.categoryList.setAdapter(adapter);
            adapter.notifyDataSetChanged();

            //standard currency format
            NumberFormat fmt = NumberFormat.getCurrencyInstance();

            AddBudgetFragment.expensesAmount.setText(fmt.format(listData.getTotalSpent()));
            AddBudgetSwipeView.mPager.setCurrentItem(2);
        }

    }


    private class AsyncNewBudget extends AsyncTask<Void,Void,Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {

            DataBaseHelperCategory myDBHelper = new DataBaseHelperCategory(mContext);
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


            addBudgetObject.setUsedCategoryListData(myDBHelper.getDefaultCategoriesAsListData());

            return null;
        }

        @Override
        protected void onPostExecute(Boolean result){

            LayoutInflater inflater = getActivity().getLayoutInflater();
            ViewGroup view = (ViewGroup)inflater.inflate(R.layout.add_budget_fragment_layout,null);
            //ListView list = (ListView)view.findViewById(R.id.addBudgetFragmentListView);
            ListViewAdapterAddBudgetFragment adapter = new ListViewAdapterAddBudgetFragment(mContext, addBudgetObject.getUsedCategoryListData());
            AddBudgetFragment.categoryList.setAdapter(null);
            AddBudgetFragment.categoryList.setAdapter(adapter);
            adapter.notifyDataSetChanged();

            //standard currency format
            NumberFormat fmt = NumberFormat.getCurrencyInstance();

            AddBudgetFragment.expensesAmount.setText(fmt.format(addBudgetObject.getUsedCategoryListData().getTotalSpent()));
            AddBudgetSwipeView.mPager.setCurrentItem(2);
        }

    }

}

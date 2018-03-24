package com.gregrussell.budget;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.database.SQLException;
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
import java.util.List;

/**
 * Created by greg on 3/21/2018.
 */

public class SelectTypeFragment extends Fragment {



    Context mContext;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        mContext = getActivity();
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.select_type_fragment_layout, container, false);



        Button copyBudgetButton = (Button)rootView.findViewById(R.id.selectTypeFragmentCopy);
        copyBudgetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //AddBudgetSwipeView.mPager.setCurrentItem(2);
                copyBudgetListDialog();
            }
        });

        Button newBudgetButton = (Button)rootView.findViewById(R.id.selectTypeFragmentNew);
        newBudgetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AddBudgetSwipeView.mPager.setCurrentItem(2);
                



            }
        });




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
                int budgetID = budgetList.get(position).getBudgetID();
                Log.d("copyBudget", "selected budget is " + budgetID);
                alertDialog.dismiss();
                copyBudgetSelectedDialog(budgetID);
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

        final ListDataObj listData = myDBHelper.createListData(budgetID);
        final ListViewAdapterCopyBudgetList adapter = new ListViewAdapterCopyBudgetList(mContext,listData);
        listView.setAdapter(adapter);


        //create a dialog box to enter new projected expenses
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(copyBudgetCategoryList);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setTitle("Copy Budget \"" + listData.getBudgetName()+ "\"?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
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

}

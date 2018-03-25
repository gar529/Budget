package com.gregrussell.budget;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.database.SQLException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;

/**
 * Created by greg on 3/21/2018.
 */

public class AddBudgetFragment extends Fragment {


    public static ListView categoryList;
    public static TextView expensesAmount;
    AddBudgetSwipeView addBudgetObject = new AddBudgetSwipeView();
    Parcelable state;

    Context mContext;

    @Override
    public void onPause(){
        Log.d("AddBudgetFragment", "pausing");
        super.onPause();
    }

    @Override
    public void onResume(){

        Log.d("AddBudgetFragment", "resuming");
        super.onResume();


    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {



        mContext = getActivity();
        DataBaseHelperCategory myDBHelper = new DataBaseHelperCategory(mContext);
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.add_budget_fragment_layout, container, false);
        categoryList = (ListView)rootView.findViewById(R.id.addBudgetFragmentListView);
        expensesAmount = (TextView)rootView.findViewById(R.id.addBudgetFragmentExpensesAmount);
        TextView budgetNameTextView = (TextView)rootView.findViewById(R.id.addBudgetFragmentName);
        budgetNameTextView.setText(addBudgetObject.getBudgetName());
        FloatingActionButton addCategoryButton = (FloatingActionButton)rootView.findViewById(R.id.addBudgetFragmentCategoryButton);
        addCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


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
            categoryList.setAdapter(null);
            categoryList.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            //standard currency format
            NumberFormat fmt = NumberFormat.getCurrencyInstance();
            expensesAmount.setText(fmt.format(addBudgetObject.getUsedCategoryListData().allExpenses));
        }catch (Exception e){
            Log.d("addBudgetFragment", "used category list data probably null");
        }




        categoryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                onCategoryClick(position, addBudgetObject.getUsedCategoryListData().getCategoryList().get(position));

            }
        });



        return rootView;

    }

    private void onCategoryClick(final int position, final String category){

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View editCategoryDialog = inflater.inflate(R.layout.add_budget_edit_category_dialog,null);
        final EditText editText = (EditText)editCategoryDialog.findViewById(R.id.addBudgetEditDialogEditText);
        final TextView categoryName = (TextView)editCategoryDialog.findViewById(R.id.addBudgetCategoryDialogCategoryName);
        categoryName.setText(category);

        //create a dialog box to enter new projected expenses
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(editCategoryDialog);

        // set dialog message
        alertDialogBuilder
                .setCancelable(true)
                .setPositiveButton(android.R.string.yes,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                String editTextString = String.valueOf(editText.getText()).trim();
                                double editTextDouble;

                                //checks if input is a double, and updates projected expenses if it is
                                try{
                                    editTextDouble = Double.parseDouble(editTextString);
                                    updateProjectedExpense(position, editTextDouble);



                                }catch (Exception e){
                                    e.printStackTrace();
                                    onCategoryClick(position, category);
                                }
                            }


                        })
                .setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                dialog.cancel();
                            }
                        })
                .setNeutralButton(R.string.menu_delete,
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){

                                deleteCategory(position);
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        //TextWatcher to prevent user from inputting more than 2 digits after the decimal
        TextWatcher textWatcher = new TextWatcher() {
            int decimal;
            String afterDecimal;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                Log.d("onTextChanged", afterDecimal + "decimal is " + String.valueOf(decimal));
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                //if there is a decimal, get string of digits following decimal
                decimal = s.toString().indexOf(".");
                if(decimal >= 0) {
                    afterDecimal = s.toString().substring(decimal + 1);
                }
                Log.d("onTextChanged", afterDecimal + "decimal is " + String.valueOf(decimal));
            }

            @Override
            public void afterTextChanged(Editable s) {

                //prevent more than two digits being added after decimal
                if(afterDecimal != null && afterDecimal.length() > 2){
                    s = new SpannableStringBuilder(s.toString().substring(0,decimal+3));
                    editText.setText(s.toString());
                }
                Log.d("afterTextChanged", s.toString());
            }
        };
        editText.addTextChangedListener(textWatcher);
        // show it
        alertDialog.show();
    }

    private void updateProjectedExpense(int position, double expense){

        //save state of listview position
        if(categoryList != null) {
            state = categoryList.onSaveInstanceState();
        }

        addBudgetObject.getUsedCategoryListData().getSpentList().set(position, expense);
        ListViewAdapterAddBudgetFragment adapter = new ListViewAdapterAddBudgetFragment(mContext,addBudgetObject.getUsedCategoryListData());
        categoryList.setAdapter(null);
        categoryList.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        //return to listView position
        if(state != null){
            Log.d("categoryListView save", "trying to restore list view positon");
            categoryList.onRestoreInstanceState(state);
        }

    }

    private void deleteCategory(int position){

        ListDataObj listData = addBudgetObject.getUsedCategoryListData();
        double oldTotalSpent = listData.getTotalSpent();
        double deletedSpent = listData.getSpentList().get(position);
        double newTotalSpent = oldTotalSpent - deletedSpent;


        listData.setTotalSpent(newTotalSpent);
        listData.getCategoryIDList().remove(position);
        listData.getCategoryList().remove(position);
        listData.getExpenseList().remove(position);
        listData.getSpentList().remove(position);

        //save state of listview position
        if(categoryList != null) {
            state = categoryList.onSaveInstanceState();
        }

        addBudgetObject.setUsedCategoryListData(listData);
        ListViewAdapterAddBudgetFragment adapter = new ListViewAdapterAddBudgetFragment(mContext,addBudgetObject.getUsedCategoryListData());
        categoryList.setAdapter(null);
        categoryList.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        //return to listView position
        if(state != null){
            Log.d("categoryListView save", "trying to restore list view positon");
            categoryList.onRestoreInstanceState(state);
        }

    }

    private void addCategory(){


    }

}


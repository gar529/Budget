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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by greg on 3/21/2018.
 */

public class AddBudgetFragment extends Fragment {


    public static ListView categoryList;
    public static TextView expensesAmount;
    AddBudgetSwipeView addBudgetObject = new AddBudgetSwipeView();
    Parcelable state;
    AlertDialog dialogAddCategory;
    FloatingActionButton addCategoryButton;

    int spinnerPosition;

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
        addCategoryButton = (FloatingActionButton)rootView.findViewById(R.id.addBudgetFragmentCategoryButton);
        addCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addCategoryButton.setEnabled(false);
                addCategory();

            }
        });

        final Button createBudgetButton = (Button)rootView.findViewById(R.id.createAddNewBudgetFragment);
        createBudgetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                createBudgetButton.setEnabled(false);
                long currentTime = System.currentTimeMillis();
                while (currentTime + 150 > System.currentTimeMillis()){}
                AsyncCreateBudget task = new AsyncCreateBudget();
                task.execute();
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
        updateTotalSpent();
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

    private void updateTotalSpent(){

        double newTotalSpent = 0.00;
        for(int i = 0; i < addBudgetObject.getUsedCategoryListData().getSpentList().size(); i++){
            newTotalSpent = newTotalSpent + addBudgetObject.getUsedCategoryListData().getSpentList().get(i);
        }
        addBudgetObject.getUsedCategoryListData().setTotalSpent(newTotalSpent);
        //standard currency format
        NumberFormat fmt = NumberFormat.getCurrencyInstance();
        expensesAmount.setText(fmt.format(newTotalSpent));
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

        Log.d("addBudgetFragment", "remove category. unusedCategoryList size before = " + addBudgetObject.getUnusedCategoriesList().size());
        //only add the budget if it already exists in the category table. new categories have an id of 0
        if(addBudgetObject.getUsedCategoryObjList().get(position).getID()!= 0) {
            addBudgetObject.getUnusedCategoriesList().add(addBudgetObject.getUsedCategoryObjList().get(position));
        }
        Log.d("addBudgetFragment", "remove category. usedCategoryList size before = " + addBudgetObject.getUsedCategoryObjList().size());
        addBudgetObject.getUsedCategoryObjList().remove(position);
        Log.d("addBudgetFragment", "remove category. usedCategoryList size after = " + addBudgetObject.getUsedCategoryObjList().size());
        Log.d("addBudgetFragment", "remove category. unusedCategoryList size after = " + addBudgetObject.getUnusedCategoriesList().size());



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

        //standard currency format
        NumberFormat fmt = NumberFormat.getCurrencyInstance();
        expensesAmount.setText(fmt.format(newTotalSpent));

    }

    private void addCategory() {

        /**Bring up dialog x
         * allow user to pick from already created category that's not being used x
         * or
         * name new category - MUST ENTER A NAME x
         * decide if category will become a default - not default by default x
         * expense $0.00 by default
         *
         */

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View addCategoryDialog = inflater.inflate(R.layout.add_category_dialog_layout, null);
        final CheckBox checkBox = (CheckBox) addCategoryDialog.findViewById(R.id.checkBoxAddCategoryDialog);
        final RadioButton radioUseExisting = (RadioButton) addCategoryDialog.findViewById(R.id.radioUseExistingAddCategoryDialog);
        final RadioButton radioAddNew = (RadioButton) addCategoryDialog.findViewById(R.id.radioAddNewAddCategoryDialog);
        final Spinner categorySpinner = (Spinner) addCategoryDialog.findViewById(R.id.spinnerAddCategoryDialog);
        final EditText categoryNameEditText = (EditText) addCategoryDialog.findViewById(R.id.editTextNameAddCategoryDialog);
        final List<CategoryObj> categoryObjList = new ArrayList<CategoryObj>();


        //create a dialog box to add new category
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);

        //set the layout the dialog uses
        alertDialogBuilder.setView(addCategoryDialog);

        //set up dialog
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                Log.d("spinnerPosition", String.valueOf(spinnerPosition));
                                if (!radioAddNew.isChecked() && !radioUseExisting.isChecked()) {
                                    new AlertDialog.Builder(mContext)
                                            .setTitle("Invalid Category Name")
                                            .setMessage("Must select or enter a name for the new category.")
                                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {

                                                    dialogAddCategory = null;
                                                    dialog.dismiss();
                                                    addCategory();

                                                }
                                            })
                                            .show();
                                } else if (radioAddNew.isChecked() && String.valueOf(categoryNameEditText.getText()).trim().isEmpty()) {
                                    new AlertDialog.Builder(mContext)
                                            .setTitle("Invalid Category Name")
                                            .setMessage("Must select or enter a name for the new category.")
                                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {

                                                    dialogAddCategory = null;
                                                    dialog.dismiss();
                                                    addCategory();
                                                }
                                            })
                                            .show();
                                } else if (radioAddNew.isChecked()) {
                                    CategoryObj categoryObj = new CategoryObj();
                                    AddBudgetSwipeView addBudgetObject = new AddBudgetSwipeView();
                                    int isDefault;
                                    if (checkBox.isChecked()) {
                                        isDefault = 1;
                                    } else isDefault = 0;

                                    if(checkCategoryName(String.valueOf(categoryNameEditText.getText()).trim()) == true){
                                        categoryObj.setCategoryName(String.valueOf(categoryNameEditText.getText()).trim());
                                        addBudgetObject.getUsedCategoryListData().getCategoryList().add(String.valueOf(categoryNameEditText.getText()).trim());
                                        addBudgetObject.getUsedCategoryListData().getSpentList().add(0.00);
                                        addBudgetObject.getUsedCategoryListData().getExpenseList().add(0.00);
                                        addBudgetObject.getUsedCategoryListData().getCategoryIDList().add(null);
                                        categoryObj.setDefaultCategory(isDefault);
                                        //usedCategoriesList.add(categoryObj);
                                        addBudgetObject.getUsedCategoryObjList().add(categoryObj);
                                        //Log.d("categoriesListNew","usedCategories size " + usedCategoriesList.size());
                                        //Log.d("categoriesListNew","unusedCategories size " + unusedCategoriesList.size());
                                        Log.d("categoriesListNew", "usedCategories size " + addBudgetObject.getUsedCategoryListData().getCategoryList().size());
                                        Log.d("categoriesListNew", "unusedCategories size " + addBudgetObject.getUnusedCategoriesList().size());
                                        //ListViewAdapterAddBudget adapter = new ListViewAdapterAddBudget(AddBudget.this, usedCategoriesList);  old comment
                                        //listViewCategoriesAdd.setAdapter(null); old comment
                                        //listViewCategoriesAdd.setAdapter(adapter); old comment

                                        //save state of listview position
                                        if (categoryList != null) {
                                            state = categoryList.onSaveInstanceState();
                                        }
                                        ListViewAdapterAddBudgetFragment adapter = new ListViewAdapterAddBudgetFragment(mContext, addBudgetObject.getUsedCategoryListData());
                                        categoryList.setAdapter(null);
                                        categoryList.setAdapter(adapter);
                                        adapter.notifyDataSetChanged();

                                        //return to listView position
                                        if (state != null) {
                                            Log.d("categoryListView save", "trying to restore list view positon");
                                            categoryList.onRestoreInstanceState(state);
                                        }
                                        dialogAddCategory = null;

                                    }else{
                                        new AlertDialog.Builder(mContext)
                                                .setTitle("Invalid Category Name")
                                                .setMessage("Must enter a unique name for the new category.")
                                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {

                                                        dialogAddCategory = null;
                                                        dialog.dismiss();
                                                        addCategory();
                                                    }
                                                })
                                                .show();
                                    }


                                } //else if (spinnerPosition >= unusedCategoriesList.size()) {
                                else if (spinnerPosition >= addBudgetObject.getUnusedCategoriesList().size()) {
                                    new AlertDialog.Builder(mContext)
                                            .setTitle("Invalid Category Name")
                                            .setMessage("Must select or enter a name for the new category.")
                                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {

                                                    dialogAddCategory = null;
                                                    dialog.dismiss();
                                                    addCategory();
                                                }
                                            })
                                            .show();
                                } else {

                                    //CategoryObj categoryObj = unusedCategoriesList.get(spinnerPosition);
                                    CategoryObj categoryObj = addBudgetObject.getUnusedCategoriesList().get(spinnerPosition);
                                    Log.d("existingCategory", categoryObj.getCategoryName());
                                    //usedCategoriesList.add(categoryObj);
                                    addBudgetObject.getUsedCategoryObjList().add(categoryObj);
                                    addBudgetObject.getUsedCategoryListData().getCategoryList().add(categoryObj.getCategoryName());
                                    addBudgetObject.getUsedCategoryListData().getSpentList().add(0.00);
                                    addBudgetObject.getUsedCategoryListData().getExpenseList().add(0.00);
                                    addBudgetObject.getUsedCategoryListData().getCategoryIDList().add(categoryObj.getID());
                                    //Log.d("categoriesListNew","usedCategories size " + usedCategoriesList.size());
                                    //Log.d("categoriesListNew","unusedCategories size " + unusedCategoriesList.size());
                                    Log.d("categoriesListNew", "usedCategories size " + addBudgetObject.getUsedCategoryListData().getCategoryList().size());
                                    Log.d("categoriesListNew", "unusedCategories size " + addBudgetObject.getUnusedCategoriesList().size());
                                    //ListViewAdapterAddBudget adapter = new ListViewAdapterAddBudget(AddBudget.this, usedCategoriesList); old comment
                                    //unusedCategoriesList.remove(spinnerPosition);
                                    addBudgetObject.getUnusedCategoriesList().remove(spinnerPosition);
                                    //listViewCategoriesAdd.setAdapter(null);

                                    //save state of listview position
                                    if(categoryList != null) {
                                        state = categoryList.onSaveInstanceState();
                                    }
                                    ListViewAdapterAddBudgetFragment adapter = new ListViewAdapterAddBudgetFragment(mContext,addBudgetObject.getUsedCategoryListData());
                                    categoryList.setAdapter(null);
                                    categoryList.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();
                                    //return to listView position
                                    if(state != null){
                                        Log.d("categoryListView save", "trying to restore list view positon");
                                        categoryList.onRestoreInstanceState(state);
                                    }

                                    //listViewCategoriesAdd.setAdapter(adapter); old comment
                                    dialogAddCategory = null;
                                }

                                Log.d("addbudgetFragment", "a cateogry was added. category id = " +
                                        addBudgetObject.getUsedCategoryObjList().get(addBudgetObject.getUsedCategoryObjList().size()-1).getID() +
                                ". category name = " + addBudgetObject.getUsedCategoryObjList().get(addBudgetObject.getUsedCategoryObjList().size()-1).getCategoryName() +
                                ". cateogry default status = " + addBudgetObject.getUsedCategoryObjList().get(addBudgetObject.getUsedCategoryObjList().size()-1).getDefaultCategory());
                            }


                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                dialogAddCategory = null;
                                dialog.cancel();
                            }
                        });

        if(dialogAddCategory == null){
            // create alert dialog
            dialogAddCategory = alertDialogBuilder.create();

            //disable spinner and existing radio button if no existing categories, check radioAddNew
            //if (unusedCategoriesList.size() == 0) {
            if (addBudgetObject.getUnusedCategoriesList().size() == 0) {
                radioUseExisting.setEnabled(false);
                categorySpinner.setEnabled(false);
                radioAddNew.setChecked(true);
            }

            //setting what happens on click of radio buttons, spinner, and edit text
            radioAddNew.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //check radioAddNew, uncheck radioUseExisting, give focus to edit text, make
                    // keyboard appear
                    checkBox.setVisibility(View.VISIBLE);
                    radioAddNew.setChecked(true);
                    radioUseExisting.setChecked(false);
                    categoryNameEditText.requestFocus();
                    InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
                }
            });
            radioUseExisting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //uncheck radioAddNew, check radioUseExisting take focus from edit text, make
                    // keyboard disappear, hide checkbox
                    checkBox.setVisibility(View.INVISIBLE);
                    radioAddNew.setChecked(false);
                    radioUseExisting.setChecked(true);
                    categoryNameEditText.clearFocus();
                    InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(categoryNameEditText.getWindowToken(), 0);
                }
            });
            categoryNameEditText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //check radioAddNew, uncheck radioUseExisting
                    radioAddNew.setChecked(true);
                    radioUseExisting.setChecked(false);
                    checkBox.setVisibility(View.VISIBLE);
                }
            });
            categoryNameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        //when gains focus, check radioAddNew, uncheck radioUseExisting
                        radioAddNew.setChecked(true);
                        radioUseExisting.setChecked(false);
                        checkBox.setVisibility(View.VISIBLE);
                    }
                }
            });
            categorySpinner.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    //uncheck radioAddNew, check radioUseExisting, take focus from edit text, make
                    // keyboard disappear, hide checkbox
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        radioAddNew.setChecked(false);
                        radioUseExisting.setChecked(true);
                        categoryNameEditText.clearFocus();
                        checkBox.setVisibility(View.INVISIBLE);
                        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(categoryNameEditText.getWindowToken(), 0);
                    }
                    return false;
                }
            });

            //adapter for categorySpinner
            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(mContext, R.layout.spinner_layout_add_category_dialog) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {

                    View v = super.getView(position, convertView, parent);
                    if (position == getCount()) {
                        ((TextView) v.findViewById(R.id.spinnerLayoutTextView)).setText("");

                        //set the hint for the spinner
                        ((TextView) v.findViewById(R.id.spinnerLayoutTextView)).setHint(getItem(getCount()));
                    }
                    return v;
                }


                @Override
                public int getCount() {

                    //last item in adapter will be hint, which shouldn't be displayed in drop down
                    return super.getCount() - 1;
                }
            };

            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            //add categories in unusedCategoryList
                /*for (int i = 0; i < unusedCategoriesList.size(); i++) {
                    spinnerAdapter.add(unusedCategoriesList.get(i).getCategoryName());
                }*/

            for (int i = 0; i < addBudgetObject.getUnusedCategoriesList().size(); i++) {
                spinnerAdapter.add(addBudgetObject.getUnusedCategoriesList().get(i).getCategoryName());
            }

            //add hint to end of adapter
            //adapter.add("Test");
            //if (unusedCategoriesList.size() > 0) {
            if (addBudgetObject.getUnusedCategoriesList().size() > 0) {
                spinnerAdapter.add(this.getResources().getString(R.string.spinner_add_category_hint));
            } else {

                //display no categories if there are none
                spinnerAdapter.add("No Categories");
                spinnerAdapter.add("No Categories");
            }

            //set spinner adapter
            categorySpinner.setAdapter(spinnerAdapter);

            //set default selection to hint
            categorySpinner.setSelection(spinnerAdapter.getCount());

            //onItemSelectListener for spinner
            categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    spinnerPosition = position;
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });


            // show it
            dialogAddCategory.show();
        }
        addCategoryButton.setEnabled(true);
    }

    private Boolean checkCategoryName(String categoryName){

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
        return myDBHelper.checkCategoryName(categoryName);

    }


    private class AsyncCreateBudget extends AsyncTask<Void,Void,Boolean>{

        @Override
        protected Boolean doInBackground(Void... params){

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
            CurrentBudgetFragment.currentBudget = myDBHelper.createBudget(addBudgetObject.getBudgetName(),
                    addBudgetObject.getUsedCategoryListData(), addBudgetObject.getUsedCategoryObjList());
            return null;
        }

        @Override
        protected void onPostExecute(Boolean result){

            getActivity().finish();

        }
    }

}


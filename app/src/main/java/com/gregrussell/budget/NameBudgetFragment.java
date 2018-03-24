package com.gregrussell.budget;

import android.app.Fragment;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;

/**
 * Created by greg on 3/21/2018.
 */

public class NameBudgetFragment extends Fragment {



    Context mContext;
    EditText nameField;
    AddBudgetSwipeView obj = new AddBudgetSwipeView();



    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        mContext = getActivity();
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.name_budget_fragment_layout, container, false);


        Button nextButton = (Button)rootView.findViewById(R.id.nameBudgetFragmentNext);
        nameField = (EditText)rootView.findViewById(R.id.nameBudgetFragmentNameField);






        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AsyncNextPage task = new AsyncNextPage();
                task.execute(nameField.getText().toString());

            }
        });

        return rootView;

    }

    private class AsyncNextPage extends AsyncTask<String,Void,Boolean>{

        DataBaseHelperCategory myDBHelper = new DataBaseHelperCategory(mContext);



        @Override
        protected Boolean doInBackground(String... budgetName) {

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

            String name = budgetName[0];
            if(name == null || name.trim().isEmpty()){
                return false;
            }

            else if (myDBHelper.checkBudgetName(name)) {

                obj.setBudgetName(name);
                return true;
            } else {
                return false;
            }
        }

        @Override

        protected void onPreExecute(){

            nameField.setEnabled(false);

        }

        @Override
        protected void onPostExecute(Boolean added){

            Log.d("AsyncAddBudget", String.valueOf(added));

            if(!added){
                new AlertDialog.Builder(getActivity())
                        .setTitle("Invalid Budget Name")
                        .setMessage("The budget name entered is invalid or already exists. Please enter a new name.")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                nameField.setEnabled(true);
                            }
                        })
                        .show();
            }else {


                //close out of the activity and return to SwipeViews
                nameField.setEnabled(true);
                nextPage();

            }


        }

}
    private void nextPage(){



        Log.d("nameBudgetFrag", "before moving to next page, budget name is " + obj.getBudgetName());
        AddBudgetSwipeView.mPager.setCurrentItem(1);


    }

}
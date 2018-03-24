package com.gregrussell.budget;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by greg on 3/21/2018.
 */

public class AddBudgetFragment extends Fragment {



    Context mContext;

    @Override
    public void onResume(){
        super.onResume();

        Log.d("AddBudgetFragment", "resuming");
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {



        mContext = getActivity();
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.add_budget_fragment_layout, container, false);



        Button backButton = (Button)rootView.findViewById(R.id.addBudgetFragmentBack);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddBudgetSwipeView.mPager.setCurrentItem(1);


            }
        });

        return rootView;

    }

}


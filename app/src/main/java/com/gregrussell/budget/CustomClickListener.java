package com.gregrussell.budget;

import android.view.View;

/**
 * Created by greg on 3/24/2018.
 */

/**
 * This class allows a single click and prevents multiple clicks on
 * the same button in rapid succession. Setting unclickable is not enough
 * because click events may still be queued up.
 *
 * Override onOneClick() to handle single clicks. Call reset() when you want to
 * accept another click.
 * Made by Ken on StackOverflow
 */

public abstract class CustomClickListener implements View.OnClickListener {
    private boolean clickable = true;

    /**
     * Override onOneClick() instead.
     */
    @Override
    public final void onClick(View v) {
        if (clickable) {
            clickable = false;
            onOneClick(v);
            //reset(); // uncomment this line to reset automatically
        }
    }

    /**
     * Override this function to handle clicks.
     * reset() must be called after each click for this function to be called
     * again.
     * @param v
     */
    public abstract void onOneClick(View v);

    /**
     * Allows another click.
     */
    public void reset() {
        clickable = true;
    }



}

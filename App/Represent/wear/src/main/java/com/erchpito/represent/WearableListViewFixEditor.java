package com.erchpito.represent;

import android.content.Context;
import android.support.wearable.view.WearableListView;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by erchpito on 12/3/2016.
 */
public class WearableListViewFixEditor extends WearableListView {
    public WearableListViewFixEditor(Context context) {
        super(context);
    }

    public WearableListViewFixEditor(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WearableListViewFixEditor(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    /**
     * Override this method to implement the choice for the intellij android designer
     */
    @Override
    public ViewHolder getChildViewHolder(View child) {
        if (!isInEditMode()) {
            return super.getChildViewHolder(child);
        } else {
            /**
             * Override this with an empty body to avoid an error in intellij android designer
             */
            return new WearableListView.ViewHolder(new View(getContext())) {
                @Override
                protected void onCenterProximity(boolean isCentralItem, boolean animate) {

                }
            };
        }
    }
}

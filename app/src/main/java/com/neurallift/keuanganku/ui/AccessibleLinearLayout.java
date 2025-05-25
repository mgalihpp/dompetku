package com.neurallift.keuanganku.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

public class AccessibleLinearLayout extends LinearLayout {
    private float x1;
    private OnSwipeListener swipeListener;

    public AccessibleLinearLayout(Context context) {
        super(context);
    }

    public AccessibleLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AccessibleLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    // Interface untuk menangani swipe
    public interface OnSwipeListener {
        void onSwipeLeft();
        void onSwipeRight();
        void onClick();
    }

    public void setOnSwipeListener(OnSwipeListener listener) {
        this.swipeListener = listener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                return true;
            case MotionEvent.ACTION_UP:
                float x2 = event.getX();
                float deltaX = x2 - x1;
                if (Math.abs(deltaX) > 100) { // Minimum swipe distance
                    if (deltaX > 0 && swipeListener != null) {
                        swipeListener.onSwipeRight();
                    } else if (deltaX < 0 && swipeListener != null) {
                        swipeListener.onSwipeLeft();
                    }
                } else {
                    // Treat as a click if swipe distance is too small
                    if (swipeListener != null) {
                        swipeListener.onClick();
                        performClick(); // Call performClick for accessibility
                    }
                }
                return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean performClick() {
        // Call the super implementation to handle accessibility
        super.performClick();
        // Optionally, you can trigger the click listener here if needed
        return true;
    }
}
package com.llk.vp;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by zhouhao on 2018/3/18.
 */

public class SRViewPager extends ViewPager {
    public SRViewPager(Context context) {
        super(context);
    }

    public SRViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Swaps the X and Y coordinates of your touch event.
     */
    private MotionEvent swapXY(MotionEvent ev) {
        float width = getWidth();
        float height = getHeight();

        float newX = ((height - ev.getY()) / height) * width;
        float newY = (ev.getX() / width) * height;

        ev.setLocation(newX, newY);

        return ev;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev){
        float x = ev.getX();
        float y = ev.getY();
        boolean intercepted = super.onInterceptTouchEvent(swapXY(ev));
//        swapXY(ev); // return touch coordinates to original reference frame for any child views
        ev.setLocation(x, y);
        return intercepted;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return super.onTouchEvent(swapXY(ev));
    }
}

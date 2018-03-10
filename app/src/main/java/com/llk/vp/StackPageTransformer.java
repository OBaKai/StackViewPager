package com.llk.vp;

import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import com.llk.vp.utilities.ValueInterpolator;

public class StackPageTransformer implements ViewPager.PageTransformer {

    public enum Orientation {
        VERTICAL(OrientedViewPager.Orientation.VERTICAL),
        HORIZONTAL(OrientedViewPager.Orientation.HORIZONTAL);

        private final OrientedViewPager.Orientation mOrientation;

        Orientation(OrientedViewPager.Orientation orientation) {
            mOrientation = orientation;
        }

        public OrientedViewPager.Orientation getViewPagerOrientation() {
            return mOrientation;
        }
    }

    public enum Gravity {
        TOP, CENTER, BOTTOM
    }

    private int mNumberOfStacked;

    private float mAlphaFactor; //堆的透明度因素, 影响由显示的堆数量决定
    private float mZeroPositionScale; //第一页的显示比例
    private float mStackedScaleFactor;
    private float mOverlapFactor;
    private float mOverlap;
    private float mAboveStackSpace;
    private float mBelowStackSpace;

    private boolean mInitialValuesCalculated = false;

    private Orientation mOrientation;
    private Gravity mGravity;

    private Interpolator mScaleInterpolator;
    private Interpolator mRotationInterpolator;

    private ValueInterpolator mValueInterpolator;

    public StackPageTransformer(int numberOfStacked, Orientation orientation, float currentPageScale, float topStackedScale, float overlapFactor, Gravity gravity) {
        //值的校验
        validateValues(currentPageScale, topStackedScale, overlapFactor);

        mNumberOfStacked = numberOfStacked;
        mAlphaFactor = 1.0f / (mNumberOfStacked + 1);
        mZeroPositionScale = currentPageScale;
        mStackedScaleFactor = (currentPageScale - topStackedScale) / mNumberOfStacked;
        mOverlapFactor = overlapFactor;
        mOrientation = orientation;
        mGravity = gravity;

        //DecelerateInterpolator : 在动画开始的地方快然后慢
        //AccelerateInterpolator : 在动画开始的地方速率改变比较慢，然后开始加速
        mScaleInterpolator = new DecelerateInterpolator(1.3f);
        mRotationInterpolator = new AccelerateInterpolator(0.6f);
        mValueInterpolator = new ValueInterpolator(0, 1, 0, mZeroPositionScale);
    }

    @Override
    public void transformPage(View view, float position) {

        int dimen = 0;
        switch (mOrientation) {
            case VERTICAL:
                dimen = view.getHeight();
                break;
            case HORIZONTAL:
                dimen = view.getWidth();
                break;
        }

        if (!mInitialValuesCalculated) {
            mInitialValuesCalculated = true;
            calculateInitialValues(dimen);
        }

        switch (mOrientation) {
            case VERTICAL:
                view.setRotationX(0);

                //设置轴心在view的中心点
                view.setPivotY(dimen / 2f);
                view.setPivotX(view.getWidth() / 2f);
                break;
            case HORIZONTAL:
                view.setRotationY(0);
                view.setPivotX(dimen / 2f);
                view.setPivotY(view.getHeight() / 2f);
                break;
        }

        //Log.e("llk", "position= " + position);
        if (position < -mNumberOfStacked - 1) { //已经看不到的view
            view.setAlpha(0f);
        } else if (position <= 0) { //正在滑进的view
            float scale = mZeroPositionScale + (position * mStackedScaleFactor);
            float baseTranslation = (-position * dimen);
            float shiftTranslation = calculateShiftForScale(position, scale, dimen);
            view.setScaleX(scale);
            view.setScaleY(scale);

            //静止时 当前显示页position=0显示不透明, 第一个堆position=-1所以显示半透
            //动态时 当前页会逐渐变透明, 第一个堆会逐渐不透明
            view.setAlpha(1.0f + (position * mAlphaFactor));

            switch (mOrientation) {
                case VERTICAL:
                    view.setTranslationY(baseTranslation + shiftTranslation);
                    break;
                case HORIZONTAL:
                    view.setTranslationX(baseTranslation + shiftTranslation);
                    break;
            }
//            Log.e("llk", "view=" + view +" poi=" + position
//                    + " scale=" + scale
//            + " baseTranslation=" + baseTranslation
//            + " shiftTranslation=" + shiftTranslation
//                    + " mAlphaFactor=" + mAlphaFactor
//            + " 1.0f + (position * mAlphaFactor)=" + (1.0f + (position * mAlphaFactor)));
        } else if (position <= 1) { //正在滑出的view
            float baseTranslation = position * dimen;
            float scale = mZeroPositionScale - mValueInterpolator.map(mScaleInterpolator.getInterpolation(position));
            scale = (scale < 0) ? 0f : scale;
            float shiftTranslation = (1.0f - position) * mOverlap;
            float rotation = -mRotationInterpolator.getInterpolation(position) * 90;
            rotation = (rotation < -90) ? -90 : rotation;
            float alpha = 1.0f - position;
            alpha = (alpha < 0) ? 0f : alpha;
            Log.e("llk", "pos=" + position
                    + " baseTranslation=" + baseTranslation
                    + " scale=" + scale
                    + " shiftTranslation=" + shiftTranslation
            + " rotation=" + rotation
            + " alpha=" + alpha);
            view.setAlpha(alpha);
            switch (mOrientation) {
                case VERTICAL:
                    view.setPivotY(dimen);
                    view.setRotationX(rotation);
                    view.setScaleX(mZeroPositionScale);
                    view.setScaleY(scale);
                    view.setTranslationY(-baseTranslation - mBelowStackSpace - shiftTranslation);
                    break;
                case HORIZONTAL:
                    view.setPivotX(dimen);
                    view.setRotationY(-rotation);
                    view.setScaleY(mZeroPositionScale);
                    view.setScaleX(scale);
                    view.setTranslationX(-baseTranslation - mBelowStackSpace - shiftTranslation);
                    break;
            }
        } else if (position > 1) { //另外一边看不到的view
            view.setAlpha(0f);
        }
    }

    private void calculateInitialValues(int dimen) {
        //当前页的显示的最大高度
        float scaledDimen = mZeroPositionScale * dimen;

        float overlapBase = (dimen - scaledDimen) / (mNumberOfStacked + 1);
        mOverlap = overlapBase * mOverlapFactor;

        float availableSpaceUnit = 0.5f * dimen * (1 - mOverlapFactor) * (1 - mZeroPositionScale);
        Log.e("llk",
                "dimen=" + dimen + "\n"
                + " mZeroPositionScale=" + mZeroPositionScale + "\n"
                + " scaledDimen=" + scaledDimen + "\n"
                + " overlapBase=" + overlapBase + "\n"
                + " mOverlapFactor=" + mOverlapFactor + "\n"
                + " availableSpaceUnit: " + availableSpaceUnit);
        switch (mGravity) {
            case TOP:
                mAboveStackSpace = 0;
                mBelowStackSpace = 2 * availableSpaceUnit;
                break;
            case CENTER:
                mAboveStackSpace = availableSpaceUnit;
                mBelowStackSpace = availableSpaceUnit;
                break;
            case BOTTOM:
                mAboveStackSpace = 2 * availableSpaceUnit;
                mBelowStackSpace = 0;
                break;
        }
    }

    private float calculateShiftForScale(float position, float scale, int dimen) {
        //difference between centers
        return mAboveStackSpace + ((mNumberOfStacked + position) * mOverlap) + (dimen * 0.5f * (scale - 1));
    }

    //值的校验
    private void validateValues(float currentPageScale, float topStackedScale, float overlapFactor) {
        if (currentPageScale <= 0 || currentPageScale > 1) {
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": Current page scale not correctly defined. " +
                    "Be sure to set it to value from (0, 1].");
        }

        if (topStackedScale <= 0 || topStackedScale > currentPageScale) {
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": Top stacked page scale not correctly defined. " +
                    "Be sure to set it to value from (0, currentPageScale].");
        }

        if (overlapFactor < 0 || overlapFactor > 1) {
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": Overlap factor not correctly defined. " +
                    "Be sure to set it to value from [0, 1].");
        }
    }

}

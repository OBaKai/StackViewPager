package com.llk.vp;


import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;

public class StackTransformer2 implements ViewPager.PageTransformer {

    private String TAG = getClass().getSimpleName();

    private static final float DEFAULT_CURRENT_PAGE_SCALE = 0.9f;
    private static final float DEFAULT_NEXT_PAGE_SCALE = 0.7f;
    private static final float DEFAULT_STACK_HEIGHT_FACTOR = 0.7f;
    private static final float DEFAULT_STACK_ALPHA_FACTOR = 0.2f;

    private float mCurrentPageScale; //当前页大小比例
    private float mNextPageScale;   //堆页大小比例
    private float mStackHeightFactor;   //堆显示高度因素
    private float mStackAlphaFactor;    //堆显示透明度因素

    public StackTransformer2() {
        initDefaultValues();
    }

    /**
     * ViewPage 堆动画参数说明
     * @param currentPageScale 当前页的大小比例 (0, 1]
     * @param nextPageScale    下一页的大小比例 (0, currentPageScale)
     * @param stackHeightFactor 堆的显示高度因素 (0, 1)
     * @param stackAlphaFactor 堆的显示透明度因素 (0, 1)
     */
    public StackTransformer2(float currentPageScale,
                             float nextPageScale,
                             float stackHeightFactor,
                             float stackAlphaFactor) {
        //传入参数校验
        boolean isOk = validateValues(currentPageScale, nextPageScale, stackHeightFactor, stackAlphaFactor);

        if (!isOk){
            initDefaultValues();
        }
    }

    private void initDefaultValues(){
        mCurrentPageScale = DEFAULT_CURRENT_PAGE_SCALE;
        mNextPageScale = DEFAULT_NEXT_PAGE_SCALE;
        mStackHeightFactor = DEFAULT_STACK_HEIGHT_FACTOR;
        mStackAlphaFactor = DEFAULT_STACK_ALPHA_FACTOR;

        Log.e("StackTransformer", "Use default values,"
                + " mCurrentPageScale=" + mCurrentPageScale
                + " mNextPageScale=" + mNextPageScale
                + " mStackHeightFactor=" + mStackHeightFactor
                + " mStackAlphaFactor=" + mStackAlphaFactor);
    }

    @Override
    public void transformPage(View page, float position) {
        float baseHeight = page.getHeight();
        float baseWidth = page.getWidth();

        if(position <= -1){ //上面看不见的view
            page.setAlpha(0f);
        }
        else if (position <= 0.0f) { //滑进来view
            page.setAlpha(1.0f + (position * mStackAlphaFactor));

            float scale = mCurrentPageScale + (((mCurrentPageScale - mNextPageScale) / 2) * position);
            page.setPivotX(baseWidth / 2f);
            page.setPivotY(baseHeight / 2f);
            page.setScaleX(scale);
            page.setScaleY(scale);

            float diffHeight = ((baseHeight * mCurrentPageScale) - (baseHeight * scale));
            float showStackSize = (diffHeight / 2) / mStackHeightFactor;
            page.setTranslationY(-showStackSize);
            page.setTranslationX(baseWidth * position * -1f);
        }
        else if(position <= 1.0f){ //滑出去view
            page.setAlpha(1.0f - (position * mStackAlphaFactor));

            float scale = mCurrentPageScale + (((mCurrentPageScale - mNextPageScale) / 2) * -position);
            page.setPivotX(baseWidth / 2f);
            page.setPivotY(baseHeight / 2f);
            page.setScaleX(scale);
            page.setScaleY(scale);

            float diffHeight = ((baseHeight * mCurrentPageScale) - (baseHeight * scale));
            float showStackSize = (diffHeight / 2) / mStackHeightFactor;
            page.setTranslationY(-showStackSize);
            page.setTranslationX(baseWidth * position * -1f);
            Log.e("llk", "scale=" + scale
            + " diffHeight=" + diffHeight
            + " showStackSize=" + showStackSize);
        }
        else {
            page.setAlpha(0f);
        }
    }

    //值的校验
    private boolean validateValues(float currentPageScale, float nextPageScale, float stackHeightFactor, float stackAlphaFactor) {
        if (currentPageScale <= 0 || currentPageScale > 1) {
            Log.e("StackTransformer", "validateValues Fail, currentPageScale must be (0, 1]. currentPageScale=" + currentPageScale);
            return false;
        }

        if (nextPageScale <= 0 || nextPageScale < currentPageScale) {
            Log.e("StackTransformer", "validateValues Fail, nextPageScale must be (0, currentPageScale). nextPageScale=" + nextPageScale);
            return false;
        }

        if (stackHeightFactor < 0 || stackHeightFactor > 1) {
            Log.e("StackTransformer", "validateValues Fail, stackHeightFactor must be (0, 1). stackHeightFactor=" + stackHeightFactor);
            return false;
        }

        if (stackAlphaFactor < 0 || stackAlphaFactor > 1) {
            Log.e("StackTransformer", "validateValues Fail, stackAlphaFactor must be (0, 1). stackAlphaFactor=" + stackAlphaFactor);
            return false;
        }

        return true;
    }
}

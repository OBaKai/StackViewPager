package com.llk.vp;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.AttributeSet;

public class FlippableStackView extends OrientedViewPager {
    private static final float DEFAULT_CURRENT_PAGE_SCALE = 0.9f;
    private static final float DEFAULT_TOP_STACKED_SCALE = 0.7f;
    private static final float DEFAULT_OVERLAP_FACTOR = 0.4f;

    public FlippableStackView(Context context) {
        super(context);
    }

    public FlippableStackView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void initStack(int numberOfStacked) {
        initStack(numberOfStacked,
                StackPageTransformer.Orientation.VERTICAL,
                DEFAULT_CURRENT_PAGE_SCALE,
                DEFAULT_TOP_STACKED_SCALE,
                DEFAULT_OVERLAP_FACTOR,
                StackPageTransformer.Gravity.CENTER);
    }

    public void initStack(int numberOfStacked, StackPageTransformer.Orientation orientation) {
        initStack(numberOfStacked,
                orientation,
                DEFAULT_CURRENT_PAGE_SCALE,
                DEFAULT_TOP_STACKED_SCALE,
                DEFAULT_OVERLAP_FACTOR,
                StackPageTransformer.Gravity.CENTER);
    }

    /**
     * @param numberOfStacked  当前页上的重叠堆的个数 (当前页头上显示的凸起个数 >=1)
     *
     * @param orientation      滑动方向
     *
     * @param currentPageScale 当前页显示的大小比例。 值区间 (0, 1] (>= topStackedScale)
     *
     * @param topStackedScale  重叠堆显示的大小比例。值区间 (0, currentPageScale]。
     *
     * @param overlapFactor    影响重叠堆大小因素，值区间[0, 1]。如果是1，大由currentPageScale决定。
     *                         如果是0，则不会显示重叠的堆
     *
     * @param gravity          显示位置
     */
    public void initStack(int numberOfStacked,
                          StackPageTransformer.Orientation orientation,
                          float currentPageScale,
                          float topStackedScale,
                          float overlapFactor,
                          StackPageTransformer.Gravity gravity) {
        setOrientation(orientation.getViewPagerOrientation());

        //设置viewpager动画
        setPageTransformer(false,
                new StackPageTransformer(numberOfStacked,
                        orientation,
                        currentPageScale,
                        topStackedScale,
                        overlapFactor,
                        gravity));

        setOffscreenPageLimit(numberOfStacked + 1);
    }

    @Override
    public void setAdapter(PagerAdapter adapter) {
        super.setAdapter(adapter);
        setCurrentItem(adapter.getCount() - 1);
    }
}

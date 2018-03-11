package com.llk.vp;


import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;

import com.gu.library.utils.ScreenUtils;


/**
 * Created by Nate on 2016/7/22.
 */
public class VerticalStackTransformer implements ViewPager.PageTransformer {

    private Context context;
    private int spaceBetweenFirAndSecWith = 10 * 2;//第一张卡片和第二张卡片宽度差  dp单位
    private int spaceBetweenFirAndSecHeight = 10;//第一张卡片和第二张卡片高度差   dp单位

    public VerticalStackTransformer(Context context) {
        this.context = context;
    }

    public VerticalStackTransformer(Context context, int spaceBetweenFirAndSecWith, int spaceBetweenFirAndSecHeight) {
        this.context = context;
        this.spaceBetweenFirAndSecWith = spaceBetweenFirAndSecWith;
        this.spaceBetweenFirAndSecHeight = spaceBetweenFirAndSecHeight;
    }


    //当前页比例
    float currentScale = 0.9f;
    //堆页的比例（这个值必须比currentScale小 需要做个校验）
    float firstStackScale = 0.7f;
    //堆头显示的高度 值越小，显示的头高度越低
    float stackShowSize = 0.4f;

    @Override
    public void transformPage(View page, float position) {
        Log.e("llk", "position=" + position + " p_h" + page.getHeight());

        //基础偏移量
        float baseTranslation = position * page.getHeight();

        //只用在pos<=0的页缩放 以及 moveY的计算
        float f = (currentScale - firstStackScale) / 2;
        float scale = currentScale + (f * position);

        //偏移1:：一半空白区域的高度
        float moveY = (page.getHeight() - (page.getHeight() * scale)) / 2;
        //当前view高度跟最大显示高度的差值
        float diff = ((page.getHeight() * currentScale) - (page.getHeight() * scale));
        //显示堆
        float showStackSize = ((diff /2) * stackShowSize);

        //只显示一个堆，所以弄到-2
        if(position < -2){ //上面看不见的view
            page.setAlpha(0f);
        }
        else if (position <= 0.0f) { //滑进来view
            page.setPivotX(page.getWidth() / 2f);
            page.setPivotY(page.getHeight() / 2f);
            page.setScaleX(scale);
            page.setScaleY(scale);

            page.setAlpha(1.0f + (position * 0.5f));

            page.setTranslationY(-baseTranslation - moveY - showStackSize);
        } else if(position <= 1.0f){ //滑出去view
            page.setAlpha(1.0f - (position * 0.5f));

            page.setPivotX(page.getWidth() / 2f);
            page.setPivotY(page.getHeight() / 2f);
            page.setScaleX(currentScale);
            page.setScaleY(currentScale);
            page.setTranslationY(baseTranslation- moveY - showStackSize);
        }
        else { //下面看不见的view
            page.setAlpha(0f);
        }
    }
}

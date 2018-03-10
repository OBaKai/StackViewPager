/**
 * Copyright 2015 Bartosz Lipinski
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.llk.vp.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

import com.llk.vp.FlippableStackView;
import com.llk.vp.R;
import com.llk.vp.StackPageTransformer;
import com.llk.vp.fragment.ColorFragment;
import com.llk.vp.utilities.ValueInterpolator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bartosz Lipinski
 * 12.12.14
 */
public class MainActivity extends ActionBarActivity {

    private static final int NUMBER_OF_FRAGMENTS = 15;

    private FlippableStackView mFlippableStack;

    private ColorFragmentAdapter mPageAdapter;

    private List<Fragment> mViewPagerFragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        createViewPagerFragments();
        mPageAdapter = new ColorFragmentAdapter(getSupportFragmentManager(), mViewPagerFragments);

//        boolean portrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;

        mFlippableStack = (FlippableStackView) findViewById(R.id.flippable_stack_view);
        mFlippableStack.initStack(1, StackPageTransformer.Orientation.VERTICAL);
        mFlippableStack.setAdapter(mPageAdapter);
    }

    private void createViewPagerFragments() {
        mViewPagerFragments = new ArrayList<>();

        int startColor = getResources().getColor(R.color.emerald);
        int startR = Color.red(startColor);
        int startG = Color.green(startColor);
        int startB = Color.blue(startColor);

        int endColor = getResources().getColor(R.color.wisteria);
        int endR = Color.red(endColor);
        int endG = Color.green(endColor);
        int endB = Color.blue(endColor);

        ValueInterpolator interpolatorR = new ValueInterpolator(0, NUMBER_OF_FRAGMENTS - 1, endR, startR);
        ValueInterpolator interpolatorG = new ValueInterpolator(0, NUMBER_OF_FRAGMENTS - 1, endG, startG);
        ValueInterpolator interpolatorB = new ValueInterpolator(0, NUMBER_OF_FRAGMENTS - 1, endB, startB);

        for (int i = 0; i < NUMBER_OF_FRAGMENTS; ++i) {
            mViewPagerFragments.add(ColorFragment.newInstance(Color.argb(255, (int) interpolatorR.map(i), (int) interpolatorG.map(i), (int) interpolatorB.map(i))));
        }
    }

    private class ColorFragmentAdapter extends FragmentPagerAdapter {
        private List<Fragment> fragments;

        public ColorFragmentAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return this.fragments.get(position);
        }

        @Override
        public int getCount() {
            return this.fragments.size();
        }
    }

    public void click_1(View view){
        mFlippableStack.setCurrentItem(5, true);
    }

    public void click_2(View view){
        int i = (mFlippableStack.getCurrentItem() - 1) < 0 ? 0 : (mFlippableStack.getCurrentItem() - 1);
        mFlippableStack.setCurrentItem(i);
    }

    public void click_3(View view){
        int i = (mFlippableStack.getCurrentItem() + 1) > (mViewPagerFragments.size() - 1) ? (mViewPagerFragments.size() - 1) : (mFlippableStack.getCurrentItem() + 1);
        mFlippableStack.setCurrentItem(i);
    }

}

/*
 * Copyright (C) 2017 Sysdata Spa.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sysdata.widget.accordion;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.sysdata.widget.accordion.utils.ThemeUtils;

/**
 * ImageView showing an up/down arrow. It can be used with FancyAccordionView in order to animate the
 * expand/collapse action of each item view.
 *
 * Simply add this view to your item layouts, it will be automagically detected and animated.
 */
public class ArrowImageView extends AppCompatImageView {

    public ArrowImageView(Context context) {
        this(context, null);
    }

    public ArrowImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ArrowImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setId(R.id.accordion_arrow_image_view);
        setScaleType(ScaleType.CENTER);
        setBackgroundDrawable(ThemeUtils.resolveDrawable(context, R.attr.selectableItemBackgroundBorderless));

        boolean expanded = false;
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ArrowImageView, 0, 0);
            expanded = a.getBoolean(R.styleable.ArrowImageView_expanded, false);
            a.recycle();
        }
        setImageResource(expanded ? R.drawable.ic_caret_up : R.drawable.ic_caret_down);
    }
}

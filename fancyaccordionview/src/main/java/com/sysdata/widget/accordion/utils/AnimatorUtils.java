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

package com.sysdata.widget.accordion.utils;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.Property;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.ImageView;

public class AnimatorUtils {

    public static final Interpolator INTERPOLATOR_FAST_OUT_SLOW_IN =
            new FastOutSlowInInterpolator();

    public static final Property<View, Integer> BACKGROUND_ALPHA =
            new Property<View, Integer>(Integer.class, "background.alpha") {
                @Override
                public Integer get(View view) {
                    Drawable background = view.getBackground();
                    if (background instanceof LayerDrawable
                            && ((LayerDrawable) background).getNumberOfLayers() > 0) {
                        background = ((LayerDrawable) background).getDrawable(0);
                    }
                    return DrawableCompat.getAlpha(background);
                }

                @Override
                public void set(View view, Integer value) {
                    setBackgroundAlpha(view, value);
                }
            };

    /**
     * Sets the alpha of the top layer's drawable (of the background) only, if the background is a
     * layer drawable, to ensure that the other layers (i.e., the selectable item background, and
     * therefore the touch feedback RippleDrawable) are not affected.
     *
     * @param view  the affected view
     * @param value the alpha value (0-255)
     */
    public static void setBackgroundAlpha(View view, Integer value) {
        Drawable background = view.getBackground();
        if (background instanceof LayerDrawable
                && ((LayerDrawable) background).getNumberOfLayers() > 0) {
            background = ((LayerDrawable) background).getDrawable(0);
        }
        background.setAlpha(value);
    }

    public static final Property<View, Integer> VIEW_LEFT =
            new Property<View, Integer>(Integer.class, "left") {
                @Override
                public Integer get(View view) {
                    return view.getLeft();
                }

                @Override
                public void set(View view, Integer left) {
                    view.setLeft(left);
                }
            };

    public static final Property<View, Integer> VIEW_TOP =
            new Property<View, Integer>(Integer.class, "top") {
                @Override
                public Integer get(View view) {
                    return view.getTop();
                }

                @Override
                public void set(View view, Integer top) {
                    view.setTop(top);
                }
            };

    public static final Property<View, Integer> VIEW_BOTTOM =
            new Property<View, Integer>(Integer.class, "bottom") {
                @Override
                public Integer get(View view) {
                    return view.getBottom();
                }

                @Override
                public void set(View view, Integer bottom) {
                    view.setBottom(bottom);
                }
            };

    public static final Property<View, Integer> VIEW_RIGHT =
            new Property<View, Integer>(Integer.class, "right") {
                @Override
                public Integer get(View view) {
                    return view.getRight();
                }

                @Override
                public void set(View view, Integer right) {
                    view.setRight(right);
                }
            };

    /**
     * @param target the view to be morphed
     * @param from   the bounds of the {@code target} before animating
     * @param to     the bounds of the {@code target} after animating
     * @return an animator that morphs the {@code target} between the {@code from} bounds and the
     * {@code to} bounds. Note that it is the *content* bounds that matter here, so padding
     * insets contributed by the background are subtracted from the views when computing the
     * {@code target} bounds.
     */
    public static Animator getBoundsAnimator(View target, View from, View to) {
        // Fetch the content insets for the views. Content bounds are what matter, not total bounds.
        final Rect targetInsets = new Rect();
        target.getBackground().getPadding(targetInsets);
        final Rect fromInsets = new Rect();
        from.getBackground().getPadding(fromInsets);
        final Rect toInsets = new Rect();
        to.getBackground().getPadding(toInsets);

        // Before animating, the content bounds of target must match the content bounds of from.
        final int startLeft = from.getLeft() - fromInsets.left + targetInsets.left;
        final int startTop = from.getTop() - fromInsets.top + targetInsets.top;
        final int startRight = from.getRight() - fromInsets.right + targetInsets.right;
        final int startBottom = from.getBottom() - fromInsets.bottom + targetInsets.bottom;

        // After animating, the content bounds of target must match the content bounds of to.
        final int endLeft = to.getLeft() - toInsets.left + targetInsets.left;
        final int endTop = to.getTop() - toInsets.top + targetInsets.top;
        final int endRight = to.getRight() - toInsets.right + targetInsets.right;
        final int endBottom = to.getBottom() - toInsets.bottom + targetInsets.bottom;

        return getBoundsAnimator(target, startLeft, startTop, startRight, startBottom, endLeft,
                endTop, endRight, endBottom);
    }

    /**
     * Returns an animator that animates the bounds of a single view.
     */
    public static Animator getBoundsAnimator(View view, int fromLeft, int fromTop, int fromRight,
                                             int fromBottom, int toLeft, int toTop, int toRight, int toBottom) {
        view.setLeft(fromLeft);
        view.setTop(fromTop);
        view.setRight(fromRight);
        view.setBottom(fromBottom);

        return ObjectAnimator.ofPropertyValuesHolder(view,
                PropertyValuesHolder.ofInt(VIEW_LEFT, toLeft),
                PropertyValuesHolder.ofInt(VIEW_TOP, toTop),
                PropertyValuesHolder.ofInt(VIEW_RIGHT, toRight),
                PropertyValuesHolder.ofInt(VIEW_BOTTOM, toBottom));
    }

    public static void startDrawableAnimation(ImageView view) {
        final Drawable d = view.getDrawable();
        if (d instanceof Animatable) {
            ((Animatable) d).start();
        }
    }
}
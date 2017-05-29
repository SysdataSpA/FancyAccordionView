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

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.view.ViewGroup;

import com.sysdata.widget.accordion.utils.AnimatorUtils;
import com.sysdata.widget.accordion.utils.SystemUtils;
import com.sysdata.widget.accordion.utils.ThemeUtils;

import java.util.List;

import static android.view.View.TRANSLATION_Y;

/**
 * A ViewHolder containing views for an item in expanded state.
 */
public abstract class ExpandedViewHolder extends ArrowItemViewHolder {

    protected ExpandedViewHolder(View itemView) {
        super(itemView);

        final Context context = itemView.getContext();
        itemView.setBackground(new LayerDrawable(new Drawable[]{
                ContextCompat.getDrawable(context, R.drawable.alarm_background_expanded),
                ThemeUtils.resolveDrawable(context, R.attr.selectableItemBackground)
        }));

        // Collapse handler
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getItemHolder().collapse();
                notifyItemClicked(ItemAdapter.OnItemClickedListener.ACTION_ID_EXPANDED_VIEW);
            }
        });

        if (arrow != null) {
            arrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getItemHolder().collapse();
                    notifyItemClicked(ItemAdapter.OnItemClickedListener.ACTION_ID_EXPANDED_VIEW);
                }
            });

            // Override arrow drawable if running Lollipop
            if (SystemUtils.isLMR1OrLater()) {
                arrow.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_caret_up_animation));
            }
        }

        itemView.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
    }

    @Override
    public Animator onAnimateChange(List<Object> payloads, int fromLeft, int fromTop, int fromRight,
                                    int fromBottom, long duration) {
        if (payloads == null || payloads.isEmpty()) {
            return null;
        }

        final AnimatorSet animatorSet = new AnimatorSet();
        if (arrow != null) {
            animatorSet.playTogether(AnimatorUtils.getBoundsAnimator(itemView,
                    fromLeft, fromTop, fromRight, fromBottom,
                    itemView.getLeft(), itemView.getTop(), itemView.getRight(), itemView.getBottom()),
                    ObjectAnimator.ofFloat(arrow, TRANSLATION_Y, 0f));
        } else {
            animatorSet.playTogether(AnimatorUtils.getBoundsAnimator(itemView,
                    fromLeft, fromTop, fromRight, fromBottom,
                    itemView.getLeft(), itemView.getTop(), itemView.getRight(), itemView.getBottom()));
        }
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                setTranslationY(0f);
                itemView.requestLayout();
            }
        });
        animatorSet.setDuration(duration);
        animatorSet.setInterpolator(AnimatorUtils.INTERPOLATOR_FAST_OUT_SLOW_IN);

        return animatorSet;
    }

    private void setTranslationY(float translationY) {
        if (arrow != null) {
            arrow.setTranslationY(translationY);
        }
    }

    @Override
    public Animator onAnimateChange(final ViewHolder oldHolder, ViewHolder newHolder,
                                    long duration) {
        if (!(oldHolder instanceof ArrowItemViewHolder)
                || !(newHolder instanceof ArrowItemViewHolder)) {
            return null;
        }

        final boolean isExpanding = this == newHolder;
        AnimatorUtils.setBackgroundAlpha(itemView, isExpanding ? 0 : 255);
        setChangingViewsAlpha(isExpanding ? 0f : 1f);

        final Animator changeAnimatorSet = isExpanding
                ? createExpandingAnimator((ArrowItemViewHolder) oldHolder, duration)
                : createCollapsingAnimator((ArrowItemViewHolder) newHolder, duration);
        changeAnimatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                AnimatorUtils.setBackgroundAlpha(itemView, 255);
                if (arrow != null) {
                    arrow.setVisibility(View.VISIBLE);
                    arrow.setTranslationY(0f);
                    arrow.jumpDrawablesToCurrentState();
                }
                setChangingViewsAlpha(1f);
            }
        });
        return changeAnimatorSet;
    }

    private Animator createCollapsingAnimator(ArrowItemViewHolder newHolder, long duration) {
        if (arrow != null) {
            arrow.setVisibility(View.INVISIBLE);
        }

        final View oldView = itemView;
        final View newView = newHolder.itemView;

        final Animator backgroundAnimator = ObjectAnimator.ofPropertyValuesHolder(oldView,
                PropertyValuesHolder.ofInt(AnimatorUtils.BACKGROUND_ALPHA, 255, 0));
        backgroundAnimator.setDuration(duration);

        final Animator boundsAnimator = AnimatorUtils.getBoundsAnimator(oldView, oldView, newView);
        boundsAnimator.setDuration(duration);
        boundsAnimator.setInterpolator(AnimatorUtils.INTERPOLATOR_FAST_OUT_SLOW_IN);

        final AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(backgroundAnimator, boundsAnimator);
        return animatorSet;
    }

    private Animator createExpandingAnimator(ArrowItemViewHolder oldHolder, long duration) {
        final View oldView = oldHolder.itemView;
        final View newView = itemView;
        final Animator boundsAnimator = AnimatorUtils.getBoundsAnimator(newView, oldView, newView);
        boundsAnimator.setDuration(duration);
        boundsAnimator.setInterpolator(AnimatorUtils.INTERPOLATOR_FAST_OUT_SLOW_IN);

        final Animator backgroundAnimator = ObjectAnimator.ofPropertyValuesHolder(newView,
                PropertyValuesHolder.ofInt(AnimatorUtils.BACKGROUND_ALPHA, 0, 255));
        backgroundAnimator.setDuration(duration);

        final AnimatorSet animatorSet;
        if (arrow != null) {
            final View oldArrow = oldHolder.arrow;
            final Rect oldArrowRect = new Rect(0, 0, oldArrow.getWidth(), oldArrow.getHeight());
            final Rect newArrowRect = new Rect(0, 0, arrow.getWidth(), arrow.getHeight());
            ((ViewGroup) newView).offsetDescendantRectToMyCoords(arrow, newArrowRect);
            ((ViewGroup) oldView).offsetDescendantRectToMyCoords(oldArrow, oldArrowRect);
            final float arrowTranslationY = oldArrowRect.bottom - newArrowRect.bottom;

            arrow.setTranslationY(arrowTranslationY);
            arrow.setVisibility(View.VISIBLE);

            final Animator arrowAnimation = ObjectAnimator.ofFloat(arrow, View.TRANSLATION_Y, 0f)
                    .setDuration(duration);
            arrowAnimation.setInterpolator(AnimatorUtils.INTERPOLATOR_FAST_OUT_SLOW_IN);

            animatorSet = new AnimatorSet();
            animatorSet.playTogether(backgroundAnimator, boundsAnimator, arrowAnimation);
            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animator) {
                    AnimatorUtils.startDrawableAnimation(arrow);
                }
            });
        } else {
            animatorSet = new AnimatorSet();
            animatorSet.playTogether(backgroundAnimator, boundsAnimator);
        }

        return animatorSet;
    }

    private void setChangingViewsAlpha(float alpha) {
        //
    }
}

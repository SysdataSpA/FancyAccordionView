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
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.view.ViewGroup;

import com.sysdata.widget.accordion.utils.AnimatorUtils;
import com.sysdata.widget.accordion.utils.SystemUtils;

import java.util.List;

/**
 * A ViewHolder containing views for an item in collapsed stated.
 */
public abstract class CollapsedViewHolder extends ArrowItemViewHolder {

    protected CollapsedViewHolder(View itemView) {
        super(itemView);

        // Expand handler
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getItemHolder().expand();
                notifyItemClicked(ItemAdapter.OnItemClickedListener.ACTION_ID_COLLAPSED_VIEW);
            }
        });

        if (arrow != null) {
            arrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getItemHolder().expand();
                    notifyItemClicked(ItemAdapter.OnItemClickedListener.ACTION_ID_COLLAPSED_VIEW);
                }
            });

            // Override arrow drawable if running Lollipop
            if (SystemUtils.isLMR1OrLater()) {
                arrow.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_caret_down_animation));
            }
        }

        itemView.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
    }

    @Override
    public Animator onAnimateChange(List<Object> payloads, int fromLeft, int fromTop, int fromRight,
                                    int fromBottom, long duration) {
        /* There are no possible partial animations for collapsed view holders. */
        return null;
    }

    @Override
    public Animator onAnimateChange(final ViewHolder oldHolder, ViewHolder newHolder,
                                    long duration) {
        if (!(oldHolder instanceof ArrowItemViewHolder)
                || !(newHolder instanceof ArrowItemViewHolder)) {
            return null;
        }

        final boolean isCollapsing = this == newHolder;
        setChangingViewsAlpha(isCollapsing ? 0f : 1f);

        final Animator changeAnimatorSet = isCollapsing
                ? createCollapsingAnimator((ArrowItemViewHolder) oldHolder, duration)
                : createExpandingAnimator((ArrowItemViewHolder) newHolder, duration);
        changeAnimatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
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

    private Animator createExpandingAnimator(ArrowItemViewHolder newHolder, long duration) {
        if (arrow != null) {
            arrow.setVisibility(View.INVISIBLE);
        }

        final View oldView = itemView;
        final View newView = newHolder.itemView;
        final Animator boundsAnimator = AnimatorUtils.getBoundsAnimator(oldView, oldView, newView)
                .setDuration(duration);
        boundsAnimator.setInterpolator(AnimatorUtils.INTERPOLATOR_FAST_OUT_SLOW_IN);

        final AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(boundsAnimator);
        return animatorSet;
    }

    private Animator createCollapsingAnimator(ArrowItemViewHolder oldHolder, long duration) {
        final View oldView = oldHolder.itemView;
        final View newView = itemView;
        final Animator boundsAnimator = AnimatorUtils.getBoundsAnimator(newView, oldView, newView)
                .setDuration(duration);
        boundsAnimator.setInterpolator(AnimatorUtils.INTERPOLATOR_FAST_OUT_SLOW_IN);

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
            animatorSet.playTogether(boundsAnimator, arrowAnimation);
            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animator) {
                    AnimatorUtils.startDrawableAnimation(arrow);
                }
            });
        } else {
            animatorSet = new AnimatorSet();
            animatorSet.playTogether(boundsAnimator);
        }

        return animatorSet;
    }

    private void setChangingViewsAlpha(float alpha) {
        //
    }
}
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
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import java.util.List;

/**
 * Wrapper around RecyclerView to prevent simultaneous layout passes,
 * particularly during animations and present data by UI accordion pattern rules.
 */
public class FancyAccordionView extends RecyclerView {

    private long mScrollToItemId = Item.INVALID_ID;
    private long mExpandedItemId = Item.INVALID_ID;

    private boolean mIgnoreRequestLayout;
    private long mCurrentUpdateToken;

    private static ItemAdapter.ItemViewHolder.Factory sCollapsedViewHolderFactory;
    private static ItemAdapter.ItemViewHolder.Factory sExpandedViewHolderFactory;

    private ItemAdapter<ExpandableItemHolder> mItemAdapter;

    public FancyAccordionView(Context context) {
        this(context, null);
    }

    public FancyAccordionView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FancyAccordionView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        addOnItemTouchListener(new RecyclerView.SimpleOnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                // Disable scrolling/user action to prevent choppy animations.
                return rv.getItemAnimator().isRunning();
            }
        });

        setLayoutManager(new AccordionLinearLayoutManager(context));

        ItemAnimator itemAnimator = new com.sysdata.widget.accordion.ItemAnimator();
        itemAnimator.setChangeDuration(300L);
        itemAnimator.setMoveDuration(300L);
        setItemAnimator(itemAnimator);

        mItemAdapter = new ItemAdapter<>();
        mItemAdapter.setHasStableIds();
        mItemAdapter.setOnItemChangedListener(new ItemAdapter.OnItemChangedListener() {
            @Override
            public void onItemChanged(ItemAdapter.ItemHolder<?> holder) {
                if (((ExpandableItemHolder) holder).isExpanded()) {
                    if (mExpandedItemId != holder.itemId) {
                        // Collapse the prior expanded item.
                        final ExpandableItemHolder aih = mItemAdapter.findItemById(mExpandedItemId);
                        if (aih != null) {
                            aih.collapse();
                        }
                        // Record the freshly expanded item.
                        mExpandedItemId = holder.itemId;
                        final RecyclerView.ViewHolder viewHolder = findViewHolderForItemId(mExpandedItemId);
                        if (viewHolder != null) {
                            smoothScrollTo(viewHolder.getAdapterPosition());
                        }
                    }
                } else if (mExpandedItemId == holder.itemId) {
                    // The expanded item is now collapsed so update the tracking id.
                    mExpandedItemId = Item.INVALID_ID;
                }
            }

            @Override
            public void onItemChanged(ItemAdapter.ItemHolder<?> holder, Object payload) {
                /* No additional work to do */
            }
        });
        setAdapter(mItemAdapter);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        mIgnoreRequestLayout = true;
        super.onLayout(changed, left, top, right, bottom);
        mIgnoreRequestLayout = false;
    }

    @Override
    public void requestLayout() {
        if (!mIgnoreRequestLayout && (getItemAnimator() == null || !getItemAnimator().isRunning())) {
            super.requestLayout();
        }
    }

    /**
     * Scroll the RecyclerView to make the position visible.
     *
     * @param position Scroll to this adapter position
     */
    public void smoothScrollTo(int position) {
        ((LinearLayoutManager) getLayoutManager()).scrollToPositionWithOffset(position, 0);
    }

    /**
     * @param alarmId identifies the alarm to be displayed
     */
    private void scrollTo(long alarmId) {
        final int alarmCount = mItemAdapter.getItemCount();
        int alarmPosition = -1;
        for (int i = 0; i < alarmCount; i++) {
            long id = mItemAdapter.getItemId(i);
            if (id == alarmId) {
                alarmPosition = i;
                break;
            }
        }

        if (alarmPosition >= 0) {
            mItemAdapter.findItemById(alarmId).expand();
            smoothScrollTo(alarmPosition);
        }
    }

    /**
     * Sets the {@link ItemAdapter.ItemViewHolder.Factory} used to create new item view holders for collapsed state.
     *
     * @param factory the {@link ItemAdapter.ItemViewHolder.Factory} used to create new item view holders
     */
    public void setCollapsedViewHolderFactory(ItemAdapter.ItemViewHolder.Factory factory) {
        this.setCollapsedViewHolderFactory(factory, null);
    }

    /**
     * Sets the {@link ItemAdapter.ItemViewHolder.Factory} and {@link ItemAdapter.OnItemClickedListener}
     * used to create new item view holders for expanded state.
     *
     * @param factory the {@link ItemAdapter.ItemViewHolder.Factory} used to create new item view holders
     */
    public void setExpandedViewHolderFactory(ItemAdapter.ItemViewHolder.Factory factory) {
        this.setExpandedViewHolderFactory(factory, null);
    }

    /**
     * Sets the {@link ItemAdapter.ItemViewHolder.Factory} and {@link ItemAdapter.OnItemClickedListener}
     * used to create new item view holders for collapsed state.
     *
     * @param factory  the {@link ItemAdapter.ItemViewHolder.Factory} used to create new item view holders
     * @param listener the {@link ItemAdapter.OnItemClickedListener}
     */
    public void setCollapsedViewHolderFactory(ItemAdapter.ItemViewHolder.Factory factory, ItemAdapter.OnItemClickedListener listener) {
        sCollapsedViewHolderFactory = factory;
        mItemAdapter.withViewTypes(factory, listener, factory.getItemViewLayoutId());
    }

    /**
     * Sets the {@link ItemAdapter.ItemViewHolder.Factory} and {@link ItemAdapter.OnItemClickedListener}
     * used to create new item view holders for expanded state.
     *
     * @param factory  the {@link ItemAdapter.ItemViewHolder.Factory} used to create new item view holders
     * @param listener the {@link ItemAdapter.OnItemClickedListener}
     */
    public void setExpandedViewHolderFactory(ItemAdapter.ItemViewHolder.Factory factory, ItemAdapter.OnItemClickedListener listener) {
        sExpandedViewHolderFactory = factory;
        mItemAdapter.withViewTypes(factory, listener, factory.getItemViewLayoutId());
    }

    /**
     * Updates the adapters items, deferring the update until the current animation is finished or
     * if no animation is running then the listener will be automatically be invoked immediately.
     *
     * @param items the new list of {@link ExpandableItemHolder} to use
     */
    public void setAdapterItems(final List<ExpandableItemHolder> items) {
        this.setAdapterItems(items, SystemClock.elapsedRealtime());
    }

    /**
     * Updates the adapters items, deferring the update until the current animation is finished or
     * if no animation is running then the listener will be automatically be invoked immediately.
     *
     * @param items       the new list of {@link ExpandableItemHolder} to use
     * @param updateToken a monotonically increasing value used to preserve ordering of deferred
     *                    updates
     */
    private void setAdapterItems(final List<ExpandableItemHolder> items, final long updateToken) {
        if (updateToken < mCurrentUpdateToken) {
            Log.v("SampleActivity", String.format("Ignoring adapter update: %d < %d", updateToken, mCurrentUpdateToken));
            return;
        }

        if (getItemAnimator().isRunning()) {
            // RecyclerView is currently animating -> defer update.
            getItemAnimator().isRunning(
                    new RecyclerView.ItemAnimator.ItemAnimatorFinishedListener() {
                        @Override
                        public void onAnimationsFinished() {
                            setAdapterItems(items, updateToken);
                        }
                    });
        } else if (isComputingLayout()) {
            // RecyclerView is currently computing a layout -> defer update.
            post(new Runnable() {
                @Override
                public void run() {
                    setAdapterItems(items, updateToken);
                }
            });
        } else {
            mCurrentUpdateToken = updateToken;
            mItemAdapter.setItems(items);

            // Expand the correct alarm.
            if (mExpandedItemId != Item.INVALID_ID) {
                final ExpandableItemHolder aih = mItemAdapter.findItemById(mExpandedItemId);
                if (aih != null) {
                    aih.expand();
                } else {
                    mExpandedItemId = Item.INVALID_ID;
                }
            }

            // Scroll to the selected alarm.
            if (mScrollToItemId != Item.INVALID_ID) {
                scrollTo(mScrollToItemId);
                setSmoothScrollStableId(Item.INVALID_ID);
            }
        }
    }

    private void setSmoothScrollStableId(long stableId) {
        mScrollToItemId = stableId;
    }

    /**
     * @return the item identifier of the current expanded item or -1 if no item is expanded.
     */
    public long getExpandedItemId() {
        return mExpandedItemId;
    }

    /**
     * Sets the identifier of the item to expand. Use this method before call {@link #setAdapterItems(List, long)}.
     *
     * @param expandedItemId the item identifier to expand the view related to it.
     */
    public void setExpandedItemId(long expandedItemId) {
        mExpandedItemId = expandedItemId;
    }

    /**
     * @return the {@link ItemAdapter.ItemViewHolder.Factory} used to create new item view holders for collapsed state.
     */
    static ItemAdapter.ItemViewHolder.Factory getCollapsedViewHolderFactory() {
        return sCollapsedViewHolderFactory;
    }

    /**
     * @return the {@link ItemAdapter.ItemViewHolder.Factory} used to create new item view holders for expanded state.
     */
    static ItemAdapter.ItemViewHolder.Factory getExpandedViewHolderFactory() {
        return sExpandedViewHolderFactory;
    }
}
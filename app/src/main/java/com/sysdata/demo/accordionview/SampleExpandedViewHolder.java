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

package com.sysdata.demo.accordionview;

import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sysdata.widget.accordionview.ExpandableItemHolder;
import com.sysdata.widget.accordionview.ExpandedViewHolder;
import com.sysdata.widget.accordionview.ItemAdapter;

/**
 * Created on 06/04/17.
 *
 * @author Umberto Marini
 */
public final class SampleExpandedViewHolder extends ExpandedViewHolder {

    private TextView mTitleTextView;
    private TextView mDescriptionTextView;

    private SampleExpandedViewHolder(View itemView) {
        super(itemView);

        mTitleTextView = (TextView) itemView.findViewById(R.id.sample_layout_expanded_title);
        mDescriptionTextView = (TextView) itemView.findViewById(R.id.sample_layout_expanded_description);
    }

    @Override
    protected void onBindItemView(ExpandableItemHolder itemHolder) {
        mTitleTextView.setText(((SampleItem) itemHolder.item).getTitle());
        mDescriptionTextView.setText(((SampleItem) itemHolder.item).getDescription());
    }

    @Override
    protected void onRecycleItemView() {
        // do nothing
    }

    @Override
    protected ItemAdapter.ItemViewHolder.Factory getViewHolderFactory() {
        return null;
    }

    public static class Factory implements ItemAdapter.ItemViewHolder.Factory {

        public static SampleExpandedViewHolder.Factory create(@LayoutRes int itemViewLayoutId) {
            return new Factory(itemViewLayoutId);
        }

        @LayoutRes
        private final int mItemViewLayoutId;

        public Factory(@LayoutRes int itemViewLayoutId) {
            mItemViewLayoutId = itemViewLayoutId;
        }

        @Override
        public ItemAdapter.ItemViewHolder<?> createViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false /* attachToRoot */);
            return new SampleExpandedViewHolder(itemView);
        }

        @Override
        public int getItemViewLayoutId() {
            return mItemViewLayoutId;
        }
    }
}

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

import android.os.Bundle;

public class ExpandableItemHolder<T extends Item> extends ItemAdapter.ItemHolder<T> {

    private static final String EXPANDED_KEY = "expanded";
    private boolean mExpanded;

    public ExpandableItemHolder(T item) {
        super(item, item.getUniqueId());
    }

    @Override
    public int getItemViewType() {
        return isExpanded() ? FancyAccordionView.getExpandedViewHolderFactory().getItemViewLayoutId()
                : FancyAccordionView.getCollapsedViewHolderFactory().getItemViewLayoutId();
    }

    public void expand() {
        if (!isExpanded()) {
            mExpanded = true;
            notifyItemChanged();
        }
    }

    public void collapse() {
        if (isExpanded()) {
            mExpanded = false;
            notifyItemChanged();
        }
    }

    public boolean isExpanded() {
        return mExpanded;
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putBoolean(EXPANDED_KEY, mExpanded);
    }

    @Override
    public void onRestoreInstanceState(Bundle bundle) {
        super.onRestoreInstanceState(bundle);
        mExpanded = bundle.getBoolean(EXPANDED_KEY);
    }
}
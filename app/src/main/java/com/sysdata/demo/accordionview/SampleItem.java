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

import com.sysdata.widget.accordionview.Item;

/**
 * Created on 05/04/17.
 *
 * @author Umberto Marini
 */
public class SampleItem extends Item {

    private String mTitle;
    private String mDescription;

    public static SampleItem create(String title, String description) {
        return new SampleItem(title, description);
    }

    SampleItem(String title, String description) {
        mTitle = title;
        mDescription = description;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getDescription() {
        return mDescription;
    }

    @Override
    public int getUniqueId() {
        return hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        SampleItem that = (SampleItem) o;

        if (!mTitle.equals(that.mTitle))
            return false;
        return mDescription != null ? mDescription.equals(that.mDescription) : that.mDescription == null;
    }

    @Override
    public int hashCode() {
        int result = mTitle.hashCode();
        result = 31 * result + (mDescription != null ? mDescription.hashCode() : 0);
        return result;
    }
}

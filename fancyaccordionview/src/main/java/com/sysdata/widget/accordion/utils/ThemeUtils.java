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

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.AttrRes;

public final class ThemeUtils {

    /**
     * Temporary array used internally to resolve attributes.
     */
    private static final int[] TEMP_ATTR = new int[1];

    private ThemeUtils() {
        // Prevent instantiation.
    }

    /**
     * Convenience method for retrieving a themed drawable.
     *
     * @param context the {@link Context} to resolve the theme attribute against
     * @param attr    the attribute corresponding to the drawable to resolve
     * @return the drawable of the resolved attribute
     */
    public static Drawable resolveDrawable(Context context, @AttrRes int attr) {
        final TypedArray a;
        synchronized (TEMP_ATTR) {
            TEMP_ATTR[0] = attr;
            a = context.obtainStyledAttributes(TEMP_ATTR);
        }

        try {
            return a.getDrawable(0);
        } finally {
            a.recycle();
        }
    }
}


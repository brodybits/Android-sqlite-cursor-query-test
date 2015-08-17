/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.test.db;

/**
 * A base class for Cursors that store their data in {@link CursorWindow}s.
 * <p>
 * The cursor owns the cursor window it uses.  When the cursor is closed,
 * its window is also closed.  Likewise, when the window used by the cursor is
 * changed, its old window is closed.  This policy of strict ownership ensures
 * that cursor windows are not leaked.
 * </p><p>
 * Subclasses are responsible for filling the cursor window with data during
 * {@link #onMove(int, int)}, allocating a new cursor window if necessary.
 * During {@link #requery()}, the existing cursor window should be cleared and
 * filled with new data.
 * </p><p>
 * If the contents of the cursor change or become invalid, the old window must be closed
 * (because it is owned by the cursor) and set to null.
 * </p>
 */
public abstract class AbstractWindowedCursor extends android.database.AbstractWindowedCursor {
    /* @Override */
    public int getType(int columnIndex) {
        checkPosition();
        // FUTURE TBD fix to support old API versions:
        return mWindow.getType(mPos, columnIndex);
    }

    /**
     * If there is a window, clear it.
     * Otherwise, creates a new window.
     *
     * @param name The window name.
     * @hide
     */
    /* @Override */
    protected void clearOrCreateWindow(String name) {
        if (mWindow == null) {
            // XXX TBD TODO ???:
            //mWindow = new CursorWindow(name);
            mWindow = new CursorWindow(true);
        } else {
            mWindow.clear();
        }
    }
}

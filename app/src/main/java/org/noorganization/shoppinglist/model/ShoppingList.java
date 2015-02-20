/*
 * Copyright 2015 Michael Wodniok
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 *  This file is part of Einkaufsliste.
 */

package org.noorganization.shoppinglist.model;

import android.util.SparseArray;

public class ShoppingList extends IdentificableModelObject {
    public String             Title;
    public SparseArray<Float> ListEntries;

    public ShoppingList() {
        Id          = ModelManager.INVALID_ID;
        Title       = "";
        ListEntries = new SparseArray<>();
    }

    public ShoppingList(ShoppingList _toCopy) {
        Id          = _toCopy.Id;
        Title       = _toCopy.Title;
        ListEntries = new SparseArray<>(_toCopy.ListEntries.size());

        for (int index = 0; index < _toCopy.ListEntries.size(); index++) {
            ListEntries.append(_toCopy.ListEntries.keyAt(index), _toCopy.ListEntries.valueAt(index).floatValue());
        }
    }
}

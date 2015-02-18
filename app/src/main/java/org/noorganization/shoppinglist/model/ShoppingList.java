package org.noorganization.shoppinglist.model;

import android.util.SparseArray;

/**
 * For license information, look into LICENSE-file located a project's root.
 * This file is part of Einkaufsliste.
 */
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

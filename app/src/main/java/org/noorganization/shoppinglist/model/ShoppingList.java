package org.noorganization.shoppinglist.model;

import android.util.SparseArray;

/**
 * For license information, look into LICENSE-file located a project's root.
 * This file is part of Einkaufsliste.
 */
public class ShoppingList extends IdentificableModelObject {
    public String             Title;
    public SparseArray<Float> ListEntries;
}

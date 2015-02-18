package org.noorganization.shoppinglist.model;

/**
 * For license information, look into LICENSE-file located a project's root.
 * This file is part of Einkaufsliste.
 */
public class Product extends IdentificableModelObject{

    public String Title;
    public float  DefaultValue;
    public int    UnitId;

    public Product() {
        Id           = ModelManager.INVALID_ID;
        Title        = "";
        DefaultValue = 1.0f;
        UnitId       = ModelManager.INVALID_ID;
    }

    public Product(Product _toCopy) {
        Id           = _toCopy.Id;
        Title        = _toCopy.Title;
        DefaultValue = _toCopy.DefaultValue;
        UnitId       = _toCopy.UnitId;
    }
}

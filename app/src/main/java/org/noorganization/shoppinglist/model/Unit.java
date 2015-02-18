package org.noorganization.shoppinglist.model;

public class Unit extends IdentificableModelObject {
    public String UnitText;

    public Unit() {
        Id       = ModelManager.INVALID_ID;
        UnitText = "";
    }

    public Unit(Unit _toCopy) {
        Id       = _toCopy.Id;
        UnitText = _toCopy.UnitText;
    }
}

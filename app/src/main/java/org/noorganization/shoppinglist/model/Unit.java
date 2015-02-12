package org.noorganization.shoppinglist.model;


import java.util.ArrayList;
import java.util.List;

public class Unit extends SaveableModelObject {

    private int m_Id;
    private String m_UnitText;

    private static final String ASSERT_ERROR_UNITTEXT_NULL = "UnitText must contain a non-null string.";

    Unit(){
    }

    @Override
    public int getId() {
        return m_Id;
    }

    @Override
    void setId(int _newId) {
        m_Id = _newId;
    }

    public String getUnitText() {
        return m_UnitText;
    }

    @Override
    public void invalidate() {
        m_Id = INVALID_ID;
    }

    /**
     * @param _NewUnitText Something like "kg" or "l". null is not a valid value.
     */
    public void setUnitText(String _NewUnitText) {
        assert (_NewUnitText == null) : ASSERT_ERROR_UNITTEXT_NULL;
        m_UnitText = _NewUnitText;
    }
}

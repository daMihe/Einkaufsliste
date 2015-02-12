package org.noorganization.shoppinglist.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by michi on 31.01.15.
 */
public class Product extends SaveableModelObject {

    private int m_Id;
    private String m_Title;
    private float m_DefaultValue;
    private int m_UnitId;

    private static final String ASSERT_ERROR_TITLE_NULL = "Product without Title is not allowed.";
    private static final String ERROR_UNIT_NOT_FOUND = "The Unit-id could not be resolved: %08x";

    Product() {
    }

    @Override
    public int getId() {
        return m_Id;
    }

    @Override
    void setId(int _newId) {
        m_Id = _newId;
    }

    @Override
    public void invalidate() {
        m_Id = INVALID_ID;
    }

    public int getUnitId() {
        return m_UnitId;
    }

    public void setUnitId(int _unitId) {
        m_UnitId = _unitId;
    }

    public float getDefaultValue() {
        return m_DefaultValue;
    }

    public void setDefaultValue(float _defaultValue) {
        m_DefaultValue = _defaultValue;
    }

    public String getTitle() {
        return m_Title;
    }

    public void setTitle(String _title) {
        assert (_title != null) : ASSERT_ERROR_TITLE_NULL;
        m_Title = _title;
    }
}

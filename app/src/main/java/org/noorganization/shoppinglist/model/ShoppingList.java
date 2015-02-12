package org.noorganization.shoppinglist.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by michi on 11.02.15.
 */
public class ShoppingList extends SaveableModelObject {

    private int m_Id;
    private String m_Title;
    private HashMap<Integer,Float> m_ListEntries;

    private static final String ASSERT_ERROR_TITLE_NULL = "A ShoppingList needs a title. null is not allowed.";
    private static final String ERROR_PRODUCT_NOT_FOUND = "The Product-id could not be resolved: %08x";

    ShoppingList() {
        m_ListEntries = new HashMap<Integer, Float>();
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

    public String getTitle() {
        return m_Title;
    }

    public void setTitle(String _title) {
        assert (_title != null) : ASSERT_ERROR_TITLE_NULL;
        m_Title = _title;
    }

    /**
     * @return A list of product-id's and their amount.
     */
    public HashMap<Integer, Float> getListEntries() {
        return m_ListEntries;
    }
}

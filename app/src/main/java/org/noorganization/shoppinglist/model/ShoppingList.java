package org.noorganization.shoppinglist.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by michi on 11.02.15.
 */
public class ShoppingList implements Identificable {

    public static final int INVALID_ID = 0xFFFFFFFF;

    private static List<ShoppingList> m_sAllLists;

    private int m_Id;
    private String m_Title;
    private HashMap<Integer,Float> m_ListEntries;

    private static final String ASSERT_ERROR_TITLE_NULL = "A ShoppingList needs a title. null is not allowed.";
    private static final String ERROR_PRODUCT_NOT_FOUND = "The Product-id could not be resolved: %08x";

    private ShoppingList() {
    }

    /**
     * Creates a ShoppingList and registers it automatically to the List of ShoppingList's.
     * @param _title Title for the new List, simply not null.
     * @return The constructed and registered ShoppingList,
     */
    public static ShoppingList Create(String _title) {
        assert (_title != null) : ASSERT_ERROR_TITLE_NULL;
        if (m_sAllLists == null) {
            m_sAllLists = new ArrayList<ShoppingList>();
        }

        ShoppingList newList = new ShoppingList();
        newList.m_Title = _title;
        newList.m_ListEntries = new HashMap<Integer, Float>();
        newList.m_Id = HelperFunctions.generateId(m_sAllLists.toArray(new ShoppingList[m_sAllLists.size()]), INVALID_ID);
        m_sAllLists.add(newList);

        return newList;
    }

    @Override
    public int getId() {
        return m_Id;
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

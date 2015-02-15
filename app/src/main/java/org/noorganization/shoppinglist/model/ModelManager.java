package org.noorganization.shoppinglist.model;

import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by michi on 12.02.15.
 */
public class ModelManager {

    public static final int INVALID_ID = 0xFFFFFFFF;

    private static List<Product> m_sAllProducts;
    private static List<ShoppingList> m_sAllLists;
    private static List<Unit> m_sAllUnits;

    /**
     * Creates a new Product and registers it.
     * @param _title A name for the Product. null is not allowed (asserted).
     * @param _defaultValue
     * @param _unitId The id of the referencing Unit returned by {@link Unit#Id}. {@link #INVALID_ID} is also allowed
     *                and means something like "this product should have no unit".
     * @return The created Product.
     */
    public static Product CreateProduct(String _title, float _defaultValue, int _unitId) {
        if (m_sAllProducts == null) {
            m_sAllProducts = new ArrayList<>();
        }

        Product newProduct = new Product();
        newProduct.Title        = _title;
        newProduct.DefaultValue = _defaultValue;
        newProduct.UnitId       = _unitId;
        newProduct.Id           = HelperFunctions.generateId(m_sAllProducts.toArray(new Product[m_sAllProducts.size()]),
                INVALID_ID);
        m_sAllProducts.add(newProduct);

        return newProduct;
    }

    /**
     * Creates a ShoppingList and registers it automatically to the List of ShoppingList's.
     * @param _title Title for the new List, simply not null.
     * @return The constructed and registered ShoppingList,
     */
    public static ShoppingList CreateShoppingList(String _title) {
        if (m_sAllLists == null) {
            m_sAllLists = new ArrayList<ShoppingList>();
        }

        ShoppingList newList = new ShoppingList();
        newList.Title       = _title;
        newList.Id          = HelperFunctions.generateId(m_sAllLists.toArray(new ShoppingList[m_sAllLists.size()]),
                INVALID_ID);
        newList.ListEntries = new SparseArray<>();
        m_sAllLists.add(newList);

        return newList;
    }

    /**
     * Creates a Unit and registers it automatically in the list of all Units.
     * @param _unitText The "name" of the unit e.g. "kg" (kilogram) or "l" (liter). null is not valid.
     */
    public static Unit CreateUnit(String _unitText) {
        if (m_sAllUnits == null) {
            m_sAllUnits = new ArrayList<Unit>();
        }

        Unit newUnit = new Unit();
        newUnit.UnitText = _unitText;
        newUnit.Id       = HelperFunctions.generateId(
                m_sAllUnits.toArray(new IdentificableModelObject[m_sAllUnits.size()]),
                INVALID_ID);
        m_sAllUnits.add(newUnit);

        return newUnit;
    }

    /**
     * Searches a Unit by it's id.
     * @param _id The internal id of the unit. If {@link org.noorganization.shoppinglist.model.Unit#INVALID_ID} is
     *            provided, no object will be found.
     * @return A Unit if found or null if no Unit was found.
     */
    /*public static Unit findUnitById(int _id) {
        if (_id == IdentificableModelObject.INVALID_ID || m_sAllUnits == null) {
            return null;
        }
        for (Unit CurrentUnit : m_sAllUnits) {
            if (CurrentUnit.getId() == _id) {
                return CurrentUnit;
            }
        }
        return null;
    }*/
}

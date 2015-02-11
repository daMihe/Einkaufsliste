package org.noorganization.shoppinglist.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by michi on 31.01.15.
 */
public class Product implements Identificable {
    public static final int INVALID_ID = 0xFFFFFFFF;

    private static List<Product> m_AllProducts;

    private int m_Id;
    private String m_Title;
    private float m_DefaultValue;
    private int m_UnitId;

    private static final String ASSERT_ERROR_TITLE_NULL = "Product without Title is not allowed.";
    private static final String ERROR_UNIT_NOT_FOUND = "The Unit-id could not be resolved: %08x";

    private Product() {
    }

    /**
     * Creates a new Product and registers it.
     * @param _Title A name for the Product. null is not allowed (asserted).
     * @param _DefaultValue
     * @param _UnitId The id of the referencing Unit returned by {@link Unit#getId()}. {@link Unit#INVALID_ID} is also
     *                allowed and means something like "this product should have no unit".
     * @return The created Product.
     */
    public static Product Create(String _Title, float _DefaultValue, int _UnitId) {
        assert (_Title != null) : ASSERT_ERROR_TITLE_NULL;

        if (m_AllProducts == null) {
            m_AllProducts = new ArrayList<Product>();
        }

        Product newProduct = new Product();
        newProduct.m_Title = _Title;
        newProduct.m_DefaultValue = _DefaultValue;
        newProduct.m_UnitId = _UnitId;
        newProduct.m_Id = HelperFunctions.generateId(m_AllProducts.toArray(new Product[m_AllProducts.size()]), INVALID_ID);
        m_AllProducts.add(newProduct);

        return newProduct;
    }

    @Override
    public int getId() {
        return m_Id;
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

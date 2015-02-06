package org.noorganization.shoppinglist.model;

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

    public Product(String _Title, float _DefaultValue, int _UnitId) {
        if (_Title == null || _DefaultValue == 0.0f) {
            throw new IllegalArgumentException("One of the parameters is not usable, either Product's name is null " +
                    "or the default value is 0.0f.");
        }

    }

    @Override
    public int getId() {
        return m_Id;
    }
}

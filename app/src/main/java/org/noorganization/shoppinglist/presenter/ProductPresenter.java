/*
 * Copyright 2015 Michael Wodniok
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 *   This file is part of Einkaufsliste.
 */

package org.noorganization.shoppinglist.presenter;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.noorganization.shoppinglist.model.ModelManager;
import org.noorganization.shoppinglist.model.Product;

import java.util.SortedMap;
import java.util.TreeMap;

public class ProductPresenter {

    private static ProductPresenter m_presenter;

    private ModelManager   m_model;
    private SQLiteDatabase m_db;

    private ProductPresenter(Context _context, String _dbName) {
        m_model = ModelManager.getInstance();
        m_db = m_model.openAndReadDatabase(_context, _dbName);
    }

    public static ProductPresenter getInstance(Context _context) {
        return getInstance(_context, Constants.DATABASE_NAME, false);
    }

    static ProductPresenter getInstance(Context _context, String _dbName, boolean _forceNew) {
        if (m_presenter == null || _forceNew) {
            m_presenter = new ProductPresenter(_context, _dbName);
        }
        return m_presenter;
    }

    public SortedMap<String, Integer> getProducts() {
        SortedMap<String, Integer> allProducts = new TreeMap<>();

        for (Product currentProduct : m_model.getAllProducts()) {
            allProducts.put(currentProduct.Title, currentProduct.Id);
        }

        return allProducts;
    }

    public ProductDetails getProductDetails(int _ProductId) {
        Product neededProduct = m_model.getProductById(_ProductId);

        if (neededProduct == null) {
            return null;
        }

        ProductDetails rtn = new ProductDetails();
        rtn.Title        = neededProduct.Title;
        rtn.DefaultValue = neededProduct.DefaultValue;
        rtn.UnitId       = neededProduct.UnitId;

        return rtn;
    }

    public void editProduct(int _id, String _title, float _defValue, int _unitId) {
        Product productToEdit = m_model.getProductById(_id);
        if (productToEdit == null) {
            return;
        }

        productToEdit.Title        = _title;
        productToEdit.UnitId       = _unitId;
        productToEdit.DefaultValue = _defValue;

        m_model.updateProduct(productToEdit, m_db);
    }

    public void deleteProduct(int _id) {
        Product productToDelete = m_model.getProductById(_id);
        if (productToDelete == null) {
            return;
        }
        m_model.deleteProduct(productToDelete, m_db);
    }
}

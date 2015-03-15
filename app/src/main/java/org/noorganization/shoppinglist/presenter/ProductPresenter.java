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

import org.noorganization.shoppinglist.model.ModelManager;

import java.util.SortedMap;

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
        return null;
    }

    public ProductDetails getProductDetails(int _ProductId) {
        return null;
    }

    public void editProduct(int _id, String _title, float _defValue, int _unitId) {

    }

    public void deleteProduct(int _id) {

    }
}

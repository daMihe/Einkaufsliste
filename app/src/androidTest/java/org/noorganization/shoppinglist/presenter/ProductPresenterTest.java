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

import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import org.noorganization.shoppinglist.model.ModelManager;
import org.noorganization.shoppinglist.model.Product;

import java.util.HashMap;
import java.util.Iterator;
import java.util.SortedMap;

public class ProductPresenterTest extends AndroidTestCase {

    public static final String DB_NAME = "nonOrganizationalTestBase.db";
    //public static final String SP_NAME = "nonOrganizationalTestPrefs";

    private ModelManager     m_model;
    private SQLiteDatabase   m_db;
    private ProductPresenter m_presenter;

    public void setUp() throws Exception {
        m_model = ModelManager.getInstance();
        m_db = m_model.openAndReadDatabase(getContext(), DB_NAME);

        m_presenter = ProductPresenter.getInstance(getContext());
    }

    public void tearDown() throws Exception {
        m_db.close();
        getContext().deleteDatabase(DB_NAME);
    }

    public void testGetProducts() throws Exception {
        SortedMap<String, Integer> emptyMap = m_presenter.getProducts();
        assertEquals(0, emptyMap.size());

        Product p1 = m_model.createProduct("Product 1", 1.0f, Constants.NO_ID, m_db);
        Product p2 = m_model.createProduct("Product 2", 1.0f, Constants.NO_ID, m_db);
        m_presenter = ProductPresenter.getInstance(getContext(), true);

        SortedMap<String, Integer> filledMap = m_presenter.getProducts();
        Iterator<String> key = filledMap.keySet().iterator();
        assertEquals(2, filledMap.size());
        assertEquals("Product 1", key.next());
        assertEquals("Product 2", key.next());
        assertEquals(p1.Id, filledMap.get("Product 1").intValue());
        assertEquals(p2.Id, filledMap.get("Product 2").intValue());
    }

}

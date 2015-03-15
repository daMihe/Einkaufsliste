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
import org.noorganization.shoppinglist.model.Unit;

import java.util.Iterator;
import java.util.SortedMap;

public class ProductPresenterTest extends AndroidTestCase {

    public static final String DB_NAME = "nonOrganizationalTestBase.db";

    private ModelManager     m_model;
    private SQLiteDatabase   m_db;
    private ProductPresenter m_presenter;

    public void setUp() throws Exception {
        m_model = ModelManager.getInstance();
        m_db = m_model.openAndReadDatabase(getContext(), DB_NAME);

        m_presenter = ProductPresenter.getInstance(getContext(), DB_NAME, false);
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

        SortedMap<String, Integer> filledMap = m_presenter.getProducts();
        Iterator<String> key = filledMap.keySet().iterator();
        assertEquals(2, filledMap.size());
        assertEquals("Product 1", key.next());
        assertEquals("Product 2", key.next());
        assertEquals(p1.Id, filledMap.get("Product 1").intValue());
        assertEquals(p2.Id, filledMap.get("Product 2").intValue());
    }

    public void testGetProductDetails() throws Exception{
        assertNull(m_presenter.getProductDetails(1));

        Product p1 = m_model.createProduct("Product 1", 5.0f, Constants.NO_ID, m_db);

        ProductDetails pd1 = m_presenter.getProductDetails(p1.Id);
        assertNotNull(pd1);
        assertEquals("Product 1", pd1.Title);
        assertEquals(5.0f, pd1.DefaultValue, 0.001f);
        assertEquals(Constants.NO_ID, pd1.UnitId);

        Unit u2 = m_model.createUnit("kg", m_db);
        Product p2 = m_model.createProduct("Product 2", 12.0f, u2.Id, m_db);

        ProductDetails pd2 = m_presenter.getProductDetails(p2.Id);
        assertNotNull(pd2);
        assertEquals("Product 2", pd2.Title);
        assertEquals(12.0f, pd2.DefaultValue, 0.001f);
        assertEquals(u2.Id, pd2.UnitId);
    }

    public void testEditProduct() throws Exception {
        Product pb1 = m_model.createProduct("Product 1", 5.0f, Constants.NO_ID, m_db);
        Product pb2 = m_model.createProduct("Product 2", 12.0f, Constants.NO_ID, m_db);

        m_presenter.editProduct(Constants.NO_ID, "", 1.0f, Constants.NO_ID);
        Product pa1 = m_model.getProductById(pb1.Id);
        Product pa2 = m_model.getProductById(pb2.Id);
        assertEquals("Product 1", pa1.Title);
        assertEquals("Product 2", pa2.Title);
        assertEquals(5.00f, pa1.DefaultValue, 0.001f);
        assertEquals(12.00f, pa2.DefaultValue, 0.001f);

        m_presenter.editProduct(pb1.Id, "Product edited", 2.0f, Constants.NO_ID);
        pa1 = m_model.getProductById(pb1.Id);
        pa2 = m_model.getProductById(pb2.Id);
        assertEquals("Product edited", pa1.Title);
        assertEquals("Product 2", pa2.Title);
        assertEquals(2.00f, pa1.DefaultValue, 0.001f);
        assertEquals(12.00f, pa2.DefaultValue, 0.001f);
    }

    public void testDeleteProduct() throws Exception {
        m_presenter.deleteProduct(1);

        Product pb1 = m_model.createProduct("Product 1", 5.0f, Constants.NO_ID, m_db);
        Product pb2 = m_model.createProduct("Product 2", 12.0f, Constants.NO_ID, m_db);

        m_presenter.deleteProduct(pb1.Id);
        assertNull(m_model.getProductById(pb1.Id));
        assertNotNull(m_model.getProductById(pb2.Id));
    }

}

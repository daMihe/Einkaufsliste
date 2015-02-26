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
 *  This file is part of Einkaufsliste.
 */

package org.noorganization.shoppinglist.model;

import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

public class ModelManagerTest extends AndroidTestCase {

    public static final String DB_NAME = "nonOrganizationalTestBase.db";

    SQLiteDatabase m_currentConnection;

    public void setUp() throws Exception {
        super.setUp();

        ModelManager modelManager = ModelManager.getInstance();

        ModelManager.DBOpenHelper openHelper = new ModelManager.DBOpenHelper(getContext(), DB_NAME, null,
                ModelManager.DBOpenHelper.CURRENT_DATABASE_VERSION);
        SQLiteDatabase testDb = openHelper.getWritableDatabase();

        testDb.execSQL("INSERT INTO Units VALUES (1, 'kg')");
        testDb.execSQL("INSERT INTO Products VALUES (1, 'Reis', 1.0, 1)");
        testDb.execSQL("INSERT INTO ShoppingLists VALUES (1, 'Meine Einkaufsliste')");
        testDb.execSQL("INSERT INTO ProductsInShoppingLists VALUES (1, 1, 2.0)");

        testDb.close();

        m_currentConnection = modelManager.openAndReadDatabase(getContext(), DB_NAME);
    }

    public void tearDown() throws Exception {
        m_currentConnection.close();

        getContext().deleteDatabase(DB_NAME);
    }

    public void testOpenAndReadDatabase() throws Exception {

        ModelManager modelManager = ModelManager.getInstance();

        assertEquals(1, modelManager.m_allLists.size());
        assertEquals(1, modelManager.m_allProducts.size());
        assertEquals(1, modelManager.m_allUnits.size());

        Product testProduct = modelManager.m_allProducts.get(0);
        assertEquals("Reis", testProduct.Title);
        assertEquals(1.0f, testProduct.DefaultValue, 0.01f);
        assertEquals(1, testProduct.UnitId);
        assertEquals(1, testProduct.Id);

        ShoppingList testList = modelManager.m_allLists.get(0);
        assertEquals(1, testList.Id);
        assertEquals("Meine Einkaufsliste", testList.Title);
        assertEquals(1, testList.ListEntries.size());
        assertEquals(2.0f, testList.ListEntries.get(1), 0.01f);

        Unit testUnit = modelManager.m_allUnits.get(0);
        assertEquals("kg", testUnit.UnitText);
        assertEquals(1, testUnit.Id);

        m_currentConnection.close();
        m_currentConnection = modelManager.openAndReadDatabase(getContext(), DB_NAME);

        assertEquals(1, modelManager.m_allLists.size());
        assertEquals(1, modelManager.m_allProducts.size());
        assertEquals(1, modelManager.m_allUnits.size());
    }

    public void testCreateUnit() throws Exception {
        ModelManager modelManager = ModelManager.getInstance();

        Unit testUnitForId = modelManager.createUnit("l", m_currentConnection);

        Unit testUnit = modelManager.getUnitById(testUnitForId.Id);
        assertNotNull(testUnit);

        assertEquals("l", testUnit.UnitText);
        assertTrue(1 != testUnit.Id);
        assertTrue(ModelManager.INVALID_ID != testUnit.Id);
        assertNotSame(testUnitForId, testUnit);

        assertEquals(2, modelManager.m_allUnits.size());
        m_currentConnection.close();
        m_currentConnection = modelManager.openAndReadDatabase(getContext(), DB_NAME);
        assertEquals(2, modelManager.m_allUnits.size());
    }

    public void testGetUnitById() throws Exception {
        ModelManager modelManager = ModelManager.getInstance();

        Unit positiveUnit = modelManager.getUnitById(1);
        assertEquals("kg", positiveUnit.UnitText);
        assertNotSame(positiveUnit, modelManager.m_allUnits.get(0));

        assertNull(modelManager.getUnitById(ModelManager.INVALID_ID));
        assertNull(modelManager.getUnitById(725));
    }

    public void testGetAllUnits() throws Exception {
        ModelManager modelManager = ModelManager.getInstance();

        Unit allUnits[] = modelManager.getAllUnits();

        assertNotNull(allUnits);
        assertEquals(1, allUnits.length);
        assertNotSame(modelManager.m_allUnits.get(0), allUnits[0]);

        assertEquals(1, allUnits[0].Id);
    }

    public void testCreateProduct() throws Exception {
        ModelManager modelManager = ModelManager.getInstance();

        Product testProductForId = modelManager.createProduct("Product 2", 5.0f, ModelManager.INVALID_ID,
                m_currentConnection);

        Product testProduct = modelManager.getProductById(testProductForId.Id);
        assertNotNull(testProduct);

        assertTrue(1 != testProduct.Id);
        assertTrue(ModelManager.INVALID_ID != testProduct.Id);

        assertEquals("Product 2", testProduct.Title);
        assertEquals(testProductForId.Id, testProduct.Id);
        assertEquals(5.0f, testProduct.DefaultValue, 0.001f);
        assertEquals(ModelManager.INVALID_ID, testProduct.UnitId);

        assertNotSame(testProduct, testProductForId);

        assertEquals(2, modelManager.m_allProducts.size());
        m_currentConnection.close();
        m_currentConnection = modelManager.openAndReadDatabase(getContext(), DB_NAME);
        assertEquals(2, modelManager.m_allProducts.size());
    }

    public void testGetProductById() throws Exception {
        ModelManager modelManager = ModelManager.getInstance();

        Product positiveProduct = modelManager.getProductById(1);
        assertEquals("Reis", positiveProduct.Title);
        assertEquals(1.0f, positiveProduct.DefaultValue, 0.001f);
        assertEquals(1, positiveProduct.UnitId);
        assertEquals(1, positiveProduct.Id);
        assertNotSame(positiveProduct, modelManager.m_allProducts.get(0));

        assertNull(modelManager.getUnitById(ModelManager.INVALID_ID));
        assertNull(modelManager.getUnitById(725));
    }

    public void testGetAllProducts() throws Exception {
        ModelManager modelManager = ModelManager.getInstance();

        Product allProducts[] = modelManager.getAllProducts();

        assertNotNull(allProducts);
        assertEquals(1, allProducts.length);
        assertNotSame(modelManager.m_allProducts.get(0), allProducts[0]);

        assertEquals(1, allProducts[0].Id);
    }

    public void testCreateShoppingList() throws Exception {
        ModelManager modelManager = ModelManager.getInstance();

        ShoppingList testShoppingListForId = modelManager.createShoppingList("Arbeitsliste", m_currentConnection);

        ShoppingList testShoppingList = modelManager.getShoppingListById(testShoppingListForId.Id);
        assertNotNull(testShoppingList);

        assertTrue(1 != testShoppingList.Id);
        assertTrue(ModelManager.INVALID_ID != testShoppingList.Id);

        assertEquals("Arbeitsliste", testShoppingList.Title);
        assertEquals(testShoppingListForId.Id, testShoppingList.Id);
        assertNotNull(testShoppingList.ListEntries);
        assertEquals(0, testShoppingList.ListEntries.size());

        assertEquals(2, modelManager.m_allLists.size());
        m_currentConnection.close();
        m_currentConnection = modelManager.openAndReadDatabase(getContext(), DB_NAME);
        assertEquals(2, modelManager.m_allLists.size());
    }

    public void testGetShoppingListById() throws Exception {
        ModelManager modelManager = ModelManager.getInstance();

        ShoppingList positiveShoppingList = modelManager.getShoppingListById(1);
        assertEquals("Meine Einkaufsliste", positiveShoppingList.Title);
        assertEquals(1, positiveShoppingList.Id);
        assertNotNull(positiveShoppingList.ListEntries);
        assertEquals(1, positiveShoppingList.ListEntries.keyAt(0));
        assertEquals(2.0f, positiveShoppingList.ListEntries.valueAt(0).floatValue(), 0.001f);
        assertNotSame(positiveShoppingList, modelManager.m_allLists.get(0));

        assertNull(modelManager.getUnitById(ModelManager.INVALID_ID));
        assertNull(modelManager.getUnitById(725));
    }

    public void testGetAllShoppingLists() throws Exception {
        ModelManager modelManager = ModelManager.getInstance();

        ShoppingList allShoppingLists[] = modelManager.getAllShoppingLists();

        assertNotNull(allShoppingLists);
        assertEquals(1, allShoppingLists.length);
        assertNotSame(modelManager.m_allLists.get(0), allShoppingLists[0]);

        assertEquals(1, allShoppingLists[0].Id);
    }

    public void testUpdateUnit() throws Exception {
        ModelManager modelManager = ModelManager.getInstance();

        Unit unitOfChange = modelManager.getUnitById(1);

        unitOfChange.UnitText = "blubbla";
        assertTrue(modelManager.updateUnit(unitOfChange, m_currentConnection));
        assertEquals("blubbla", modelManager.getUnitById(1).UnitText);

        unitOfChange.Id = 2;
        assertFalse(modelManager.updateUnit(unitOfChange, m_currentConnection));
        assertNotNull(modelManager.getUnitById(1));

        m_currentConnection.close();
        m_currentConnection = modelManager.openAndReadDatabase(getContext(), DB_NAME);
        assertNotNull(modelManager.getUnitById(1));
        assertEquals("blubbla", modelManager.getUnitById(1).UnitText);
    }

    public void testUpdateProduct() throws Exception {
        ModelManager modelManager = ModelManager.getInstance();

        Product productOfChange = modelManager.getProductById(1);

        productOfChange.Title = "blubbla";
        assertTrue(modelManager.updateProduct(productOfChange, m_currentConnection));
        assertEquals("blubbla", modelManager.getProductById(1).Title);

        productOfChange.Id = 2;
        assertFalse(modelManager.updateProduct(productOfChange, m_currentConnection));
        assertNotNull(modelManager.getProductById(1));

        m_currentConnection.close();
        m_currentConnection = modelManager.openAndReadDatabase(getContext(), DB_NAME);
        assertNotNull(modelManager.getProductById(1));
        assertEquals("blubbla", modelManager.getProductById(1).Title);
    }

    public void testUpdateShoppingList() throws Exception {
        ModelManager modelManager = ModelManager.getInstance();

        ShoppingList shoppingListOfChange = modelManager.getShoppingListById(1);

        shoppingListOfChange.Title = "blubbla";
        assertTrue(modelManager.updateShoppingList(shoppingListOfChange, m_currentConnection));
        assertEquals("blubbla", modelManager.getShoppingListById(1).Title);

        shoppingListOfChange.Id = 2;
        assertFalse(modelManager.updateShoppingList(shoppingListOfChange, m_currentConnection));
        assertNotNull(modelManager.getShoppingListById(1));

        m_currentConnection.close();
        m_currentConnection = modelManager.openAndReadDatabase(getContext(), DB_NAME);
        assertNotNull(modelManager.getShoppingListById(1));
        assertEquals("blubbla", modelManager.getShoppingListById(1).Title);

        assertEquals(2.0f, modelManager.getShoppingListById(1).ListEntries.get(1, Float.NaN), 0.001f);
    }

    public void testDeleteUnit() throws Exception {
        ModelManager modelManager = ModelManager.getInstance();

        modelManager.deleteUnit(modelManager.getUnitById(1), m_currentConnection);

        assertNull(modelManager.getUnitById(1));
        assertNull(modelManager.getProductById(1));
        assertEquals(0, modelManager.getShoppingListById(1).ListEntries.size());

        m_currentConnection.close();
        m_currentConnection = modelManager.openAndReadDatabase(getContext(), DB_NAME);

        assertNull(modelManager.getUnitById(1));
        assertNull(modelManager.getProductById(1));
        assertEquals(0, modelManager.getShoppingListById(1).ListEntries.size());
    }

    public void testDeleteProduct() throws Exception {
        ModelManager modelManager = ModelManager.getInstance();

        modelManager.deleteProduct(modelManager.getProductById(1), m_currentConnection);

        assertNotNull(modelManager.getUnitById(1));
        assertNull(modelManager.getProductById(1));
        assertEquals(0, modelManager.getShoppingListById(1).ListEntries.size());

        m_currentConnection.close();
        m_currentConnection = modelManager.openAndReadDatabase(getContext(), DB_NAME);

        assertNotNull(modelManager.getUnitById(1));
        assertNull(modelManager.getProductById(1));
        assertEquals(0, modelManager.getShoppingListById(1).ListEntries.size());
    }

    public void testDeleteShoppingList() throws Exception {
        ModelManager modelManager = ModelManager.getInstance();

        modelManager.deleteShoppingList(modelManager.getShoppingListById(1), m_currentConnection);

        assertNotNull(modelManager.getUnitById(1));
        assertNotNull(modelManager.getProductById(1));
        assertNull(modelManager.getShoppingListById(1));

        m_currentConnection.close();
        m_currentConnection = modelManager.openAndReadDatabase(getContext(), DB_NAME);

        assertNotNull(modelManager.getUnitById(1));
        assertNotNull(modelManager.getProductById(1));
        assertNull(modelManager.getShoppingListById(1));
    }
}
package org.noorganization.shoppinglist.model;

import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

public class ModelManagerTest extends AndroidTestCase {

    public static final String DB_NAME = "nonOrganizationalTestBase.db";

    public void setUp() throws Exception {
        super.setUp();
        ModelManager.DBOpenHelper openHelper = new ModelManager.DBOpenHelper(getContext(), DB_NAME, null,
                ModelManager.DBOpenHelper.CURRENT_DATABASE_VERSION);
        SQLiteDatabase testDb = openHelper.getWritableDatabase();

        testDb.execSQL("INSERT INTO Units VALUES (1, 'kg')");
        testDb.execSQL("INSERT INTO Products VALUES (1, 'Reis', 1.0, 1)");
        testDb.execSQL("INSERT INTO ShoppingLists VALUES (1, 'Meine Einkaufsliste')");
        testDb.execSQL("INSERT INTO ProductsInShoppingLists VALUES (1, 1, 2.0)");

        testDb.close();
    }

    public void tearDown() throws Exception {
        getContext().deleteDatabase(DB_NAME);
    }

    public void testOpenAndReadDatabase() throws Exception {
        SQLiteDatabase testDb = ModelManager.openAndReadDatabase(getContext(), DB_NAME);

        assertEquals(1, ModelManager.m_sAllLists.size());
        assertEquals(1, ModelManager.m_sAllProducts.size());
        assertEquals(1, ModelManager.m_sAllUnits.size());

        Product testProduct = ModelManager.m_sAllProducts.get(0);
        assertEquals("Reis", testProduct.Title);
        assertEquals(1.0f, testProduct.DefaultValue, 0.01f);
        assertEquals(1, testProduct.UnitId);
        assertEquals(1, testProduct.Id);

        ShoppingList testList = ModelManager.m_sAllLists.get(0);
        assertEquals(1, testList.Id);
        assertEquals("Meine Einkaufsliste", testList.Title);
        assertEquals(1, testList.ListEntries.size());
        assertEquals(2.0f, testList.ListEntries.get(1), 0.01f);

        Unit testUnit = ModelManager.m_sAllUnits.get(0);
        assertEquals("kg", testUnit.UnitText);
        assertEquals(1, testUnit.Id);

        testDb.close();
    }
}
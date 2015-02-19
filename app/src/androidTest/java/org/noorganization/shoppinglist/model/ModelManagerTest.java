package org.noorganization.shoppinglist.model;

import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

public class ModelManagerTest extends AndroidTestCase {

    public static final String DB_NAME = "nonOrganizationalTestBase.db";

    SQLiteDatabase m_currentConnection;

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

        m_currentConnection = ModelManager.openAndReadDatabase(getContext(), DB_NAME);
    }

    public void tearDown() throws Exception {
        m_currentConnection.close();

        getContext().deleteDatabase(DB_NAME);
    }

    public void testOpenAndReadDatabase() throws Exception {

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
    }

    public void testCreateUnit() throws Exception {
        Unit testUnitForId = ModelManager.createUnit("l");

        Unit testUnit = ModelManager.getUnitById(testUnitForId.Id);
        assertNotNull(testUnit);

        assertEquals("l", testUnit.UnitText);
        assertTrue(1 != testUnit.Id);
        assertTrue(ModelManager.INVALID_ID != testUnit.Id);
        assertNotSame(testUnitForId, testUnit);
    }

    public void testGetUnitById() throws Exception {
        Unit positiveUnit = ModelManager.getUnitById(1);
        assertEquals("kg", positiveUnit.UnitText);
        assertNotSame(positiveUnit, ModelManager.m_sAllUnits.get(0));

        assertNull(ModelManager.getUnitById(ModelManager.INVALID_ID));
        assertNull(ModelManager.getUnitById(725));
    }

    public void testGetAllUnits() throws Exception {
        Unit allUnits[] = ModelManager.getAllUnits();

        assertNotNull(allUnits);
        assertEquals(1, allUnits.length);
        assertNotSame(ModelManager.m_sAllUnits.get(0), allUnits[0]);

        assertEquals(1, allUnits[0].Id);
    }

    public void testCreateProduct() throws Exception {
        Product testProductForId = ModelManager.createProduct("Product 2", 5.0f, ModelManager.INVALID_ID);

        Product testProduct = ModelManager.getProductById(testProductForId.Id);
        assertNotNull(testProduct);

        assertTrue(1 != testProduct.Id);
        assertTrue(ModelManager.INVALID_ID != testProduct.Id);

        assertEquals("Product 2", testProduct.Title);
        assertEquals(testProductForId.Id, testProduct.Id);
        assertEquals(5.0f, testProduct.DefaultValue, 0.001f);
        assertEquals(ModelManager.INVALID_ID, testProduct.UnitId);

        assertNotSame(testProduct, testProductForId);
    }

    public void testGetProductById() throws Exception {
        Product positiveProduct = ModelManager.getProductById(1);
        assertEquals("Reis", positiveProduct.Title);
        assertEquals(1.0f, positiveProduct.DefaultValue, 0.001f);
        assertEquals(1, positiveProduct.UnitId);
        assertEquals(1, positiveProduct.Id);
        assertNotSame(positiveProduct, ModelManager.m_sAllProducts.get(0));

        assertNull(ModelManager.getUnitById(ModelManager.INVALID_ID));
        assertNull(ModelManager.getUnitById(725));
    }

    public void testGetAllProducts() throws Exception {
        Product allProducts[] = ModelManager.getAllProducts();

        assertNotNull(allProducts);
        assertEquals(1, allProducts.length);
        assertNotSame(ModelManager.m_sAllProducts.get(0), allProducts[0]);

        assertEquals(1, allProducts[0].Id);
    }

    public void testCreateShoppingList() throws Exception {
        ShoppingList testShoppingListForId = ModelManager.createShoppingList("Arbeitsliste");

        ShoppingList testShoppingList = ModelManager.getShoppingListById(testShoppingListForId.Id);
        assertNotNull(testShoppingList);

        assertTrue(1 != testShoppingList.Id);
        assertTrue(ModelManager.INVALID_ID != testShoppingList.Id);

        assertEquals("Arbeitsliste", testShoppingList.Title);
        assertEquals(testShoppingListForId.Id, testShoppingList.Id);
        assertNotNull(testShoppingList.ListEntries);
        assertEquals(0, testShoppingList.ListEntries.size());
    }

    public void testGetShoppingListById() throws Exception {
        ShoppingList positiveShoppingList = ModelManager.getShoppingListById(1);
        assertEquals("Meine Einkaufsliste", positiveShoppingList.Title);
        assertEquals(1, positiveShoppingList.Id);
        assertNotNull(positiveShoppingList.ListEntries);
        assertEquals(1, positiveShoppingList.ListEntries.keyAt(0));
        assertEquals(2.0f, positiveShoppingList.ListEntries.valueAt(0).floatValue(), 0.001f);
        assertNotSame(positiveShoppingList, ModelManager.m_sAllLists.get(0));

        assertNull(ModelManager.getUnitById(ModelManager.INVALID_ID));
        assertNull(ModelManager.getUnitById(725));
    }

    public void testGetAllShoppingLists() throws Exception {
        ShoppingList allShoppingLists[] = ModelManager.getAllShoppingLists();

        assertNotNull(allShoppingLists);
        assertEquals(1, allShoppingLists.length);
        assertNotSame(ModelManager.m_sAllLists.get(0), allShoppingLists[0]);

        assertEquals(1, allShoppingLists[0].Id);
    }

    // TODO Create asserts for test if db is updated
}
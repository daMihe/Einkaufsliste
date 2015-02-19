package org.noorganization.shoppinglist.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.SystemClock;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * For license information, look into LICENSE-file located a project's root.
 * This file is part of Einkaufsliste.
 */
public class ModelManager {

    public static final int INVALID_ID = 0xFFFFFFFF;

    static List<Product> m_sAllProducts;
    static List<ShoppingList> m_sAllLists;
    static List<Unit> m_sAllUnits;

    /**
     * Creates a new Product and registers it. This method is does not throw anything if saving to database fails (see
     * return value).
     * @param _title A name for the Product. null is not allowed
     * @param _defaultValue The default value when adding to a shopping list.
     * @param _unitId The id of the referencing Unit returned by {@link Unit#Id}. {@link #INVALID_ID} is also allowed
     *                and means something like "this product should have no unit".
     * @param _db Open connection to a writable database.
     * @return The created Product. Null if saving to database failed.
     */
    public static Product createProduct(String _title, float _defaultValue, int _unitId, SQLiteDatabase _db) {
        if (m_sAllProducts == null) {
            m_sAllProducts = new ArrayList<>();
        }

        Product newProduct = new Product();
        newProduct.Title        = _title;
        newProduct.DefaultValue = _defaultValue;
        newProduct.UnitId       = _unitId;
        newProduct.Id           = generateId(m_sAllProducts.toArray(new Product[m_sAllProducts.size()]));

        ContentValues insertionValues = new ContentValues();
        insertionValues.put("title", _title);
        insertionValues.put("defaultvalue", _defaultValue);
        insertionValues.put("unit_id", (_unitId == INVALID_ID ? null : _unitId));
        insertionValues.put("id", newProduct.Id);
        if(_db.insert("Products", null, insertionValues) == -1) {
            return null;
        }

        m_sAllProducts.add(newProduct);

        return newProduct;
    }

    /**
     * Creates a ShoppingList and registers it automatically to the List of ShoppingList's. No exception is thrown when
     * saving fails. Check the returned object.
     * @param _title Title for the new List, simply not null.
     * @param _db Open connection to a writable database.
     * @return The constructed and registered ShoppingList, Or null if saving to database failed.
     */
    public static ShoppingList createShoppingList(String _title, SQLiteDatabase _db) {
        if (m_sAllLists == null) {
            m_sAllLists = new ArrayList<>();
        }

        ShoppingList newList = new ShoppingList();
        newList.Title       = _title;
        newList.Id          = generateId(m_sAllLists.toArray(new ShoppingList[m_sAllLists.size()]));
        newList.ListEntries = new SparseArray<>();


        ContentValues insertionValues = new ContentValues();
        insertionValues.put("title", _title);
        insertionValues.put("id", newList.Id);
        if(_db.insert("ShoppingLists", null, insertionValues) == -1) {
            return null;
        }

        m_sAllLists.add(newList);

        return newList;
    }

    /**
     * Creates a Unit and registers it automatically in the list of all Units. No exception will be thrown if saving
     * fails.
     * @param _unitText The "name" of the unit e.g. "kg" (kilogram) or "l" (liter). null is not valid.
     * @param _db Open connection to a writable database.
     * @return The created and saved object or null if saving did not work.
     */
    public static Unit createUnit(String _unitText, SQLiteDatabase _db) {
        if (m_sAllUnits == null) {
            m_sAllUnits = new ArrayList<>();
        }

        Unit newUnit = new Unit();
        newUnit.UnitText = _unitText;
        newUnit.Id       = generateId(m_sAllUnits.toArray(new IdentificableModelObject[m_sAllUnits.size()]));


        ContentValues insertionValues = new ContentValues();
        insertionValues.put("title", _unitText);
        insertionValues.put("id", newUnit.Id);
        if(_db.insert("Units", null, insertionValues) == -1) {
            return null;
        }

        m_sAllUnits.add(newUnit);

        return newUnit;
    }

    /**
     * Generates a unique id for an IdentificableModelObject object.
     * @return A collision-free, random id
     */
    static int generateId(IdentificableModelObject _existingObjects[]) {
        Random randomGenerator = new Random();
        randomGenerator.setSeed(SystemClock.uptimeMillis());
        while (true) {
            int newId = randomGenerator.nextInt();
            if (newId == INVALID_ID) {
                continue;
            }
            boolean unique = true;
            for (IdentificableModelObject currentObject : _existingObjects) {
                if (currentObject.Id == newId) {
                    unique = false;
                    break;
                }
            }
            if (unique) {
                return newId;
            }
        }
    }

    /**
     * Opens or creates a database in this context with the specified name.
     * @param _context Null is not allowed.
     * @param _name Null is not allowed.
     * @return The open database. It's writable and can be used for all model-modifying methods.
     */
    public static SQLiteDatabase openAndReadDatabase(Context _context, String _name) {

        String realFilename = _name;
        if (!realFilename.endsWith(".db")) {
            realFilename = realFilename.concat(".db");
        }

        DBOpenHelper databaseHelper = new DBOpenHelper(_context, realFilename, null, DBOpenHelper.CURRENT_DATABASE_VERSION);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        if (m_sAllUnits == null) {
            m_sAllUnits = new ArrayList<>();
        } else {
            m_sAllUnits.clear();
        }

        Cursor allUnits = db.query("Units",
                new String[]{ "id", "title" },
                null, new String[0], null, null, null);
        allUnits.moveToFirst();
        while (!allUnits.isAfterLast()) {
            Unit existingUnit = new Unit();
            existingUnit.UnitText = allUnits.getString(allUnits.getColumnIndex("title"));
            existingUnit.Id       = allUnits.getInt(allUnits.getColumnIndex("id"));
            m_sAllUnits.add(existingUnit);
            allUnits.moveToNext();
        }
        allUnits.close();

        if (m_sAllProducts == null) {
            m_sAllProducts = new ArrayList<>();
        } else {
            m_sAllProducts.clear();
        }

        Cursor allProducts = db.query("Products",
                new String[]{ "id", "title", "defaultvalue", "unit_id" },
                null, new String[0], null, null, null);
        allProducts.moveToFirst();
        int indexOfUnitId = allProducts.getColumnIndex("unit_id");
        while (!allProducts.isAfterLast()) {
            Product existingProduct = new Product();
            existingProduct.DefaultValue = allProducts.getFloat(allProducts.getColumnIndex("defaultvalue"));
            existingProduct.Title        = allProducts.getString(allProducts.getColumnIndex("title"));
            existingProduct.Id           = allProducts.getInt(allProducts.getColumnIndex("id"));
            existingProduct.UnitId       = (allProducts.isNull(indexOfUnitId) ?
                    INVALID_ID : allProducts.getInt(indexOfUnitId));
            m_sAllProducts.add(existingProduct);
            allProducts.moveToNext();
        }
        allProducts.close();

        if (m_sAllLists == null) {
            m_sAllLists = new ArrayList<>();
        } else {
            m_sAllLists.clear();
        }

        Cursor allLists = db.query("ShoppingLists",
                new String[]{ "id", "title" },
                null, new String[0], null, null, null);
        allLists.moveToFirst();
        while (!allLists.isAfterLast()) {
            ShoppingList existingList = new ShoppingList();
            existingList.Title       = allLists.getString(allLists.getColumnIndex("title"));
            existingList.Id          = allLists.getInt(allLists.getColumnIndex("id"));
            existingList.ListEntries = new SparseArray<>();
            Cursor allItemsInList = db.query("ProductsInShoppingLists",
                    new String[] { "product_id", "value" },
                    "shoppinglist_id = ?",
                    new String[] { existingList.Id+"" },
                    null, null,
                    "product_id ASC"); // product-id's and -values where shoppinglist_id is the current id, ordered.
            for (allItemsInList.moveToFirst(); !allItemsInList.isAfterLast(); allItemsInList.moveToNext()) {
                existingList.ListEntries.append(allItemsInList.getInt(allItemsInList.getColumnIndex("product_id")),
                        allItemsInList.getFloat(allItemsInList.getColumnIndex("value")));
            }
            allItemsInList.close();

            m_sAllLists.add(existingList);
            allLists.moveToNext();
        }
        allLists.close();

        return db;
    }

    /**
     * @return A list with copies of all Products. The array is not sorted. null wont be returned even if no Products
     * are loaded.
     */
    public static Product[] getAllProducts() {
        if (m_sAllProducts == null || m_sAllProducts.size() == 0) {
            return new Product[0];
        }

        Product rtn[] = new Product[m_sAllProducts.size()];
        int index = 0;
        for (Product currentProduct : m_sAllProducts) {
            rtn[index] = new Product(currentProduct);
            index++;
        }
        return rtn;
    }

    /**
     * @return A list with copies of all Units. The array is not sorted. null wont be returned even if no Units are
     * loaded.
     */
    public static Unit[] getAllUnits() {
        if (m_sAllUnits == null || m_sAllUnits.size() == 0) {
            return new Unit[0];
        }

        Unit rtn[] = new Unit[m_sAllUnits.size()];
        int index = 0;
        for (Unit currentUnit : m_sAllUnits) {
            rtn[index] = new Unit(currentUnit);
            index++;
        }
        return rtn;
    }

    /**
     * @return A list of all ShoppingLists (as copies). Never null. Not sorted.
     */
    public static ShoppingList[] getAllShoppingLists() {
        if (m_sAllLists == null || m_sAllLists.size() == 0) {
            return new ShoppingList[0];
        }

        ShoppingList rtn[] = new ShoppingList[m_sAllLists.size()];
        int index = 0;
        for (ShoppingList currentList : m_sAllLists) {
            rtn[index] = new ShoppingList(currentList);
            index++;
        }
        return rtn;
    }

    /**
     * @param _id Internal id of Product. If {@link #INVALID_ID}, none will be found.
     * @return A copy of the Product or null, if nothing found.
     */
    public static Product getProductById(int _id) {
        if (_id == INVALID_ID || m_sAllProducts == null) {
            return null;
        }
        for (Product currentProduct : m_sAllProducts) {
            if (currentProduct.Id == _id) {
                return new Product(currentProduct);
            }
        }
        return null;
    }

    /**
     * Searches a Unit by it's id.
     * @param _id The internal id of the unit. If {@link #INVALID_ID} is provided, no object will be found.
     * @return A Unit if found or null if no Unit was found.
     */
    public static Unit getUnitById(int _id) {
        if (_id == INVALID_ID || m_sAllUnits == null) {
            return null;
        }
        for (Unit CurrentUnit : m_sAllUnits) {
            if (CurrentUnit.Id == _id) {
                return new Unit(CurrentUnit);
            }
        }
        return null;
    }

    /**
     * @param _id Internal id of the list. If {@link #INVALID_ID}, none will be found.
     * @return A copy of the ShoppingList or null, if nothing found.
     */
    public static ShoppingList getShoppingListById(int _id) {
        if (_id == INVALID_ID || m_sAllLists == null) {
            return null;
        }
        for (ShoppingList currentList : m_sAllLists) {
            if (currentList.Id == _id) {
                return new ShoppingList(currentList);
            }
        }
        return null;
    }

    static class DBOpenHelper extends SQLiteOpenHelper {
        public static final int CURRENT_DATABASE_VERSION = 1;

        public DBOpenHelper(Context _context, String _name, SQLiteDatabase.CursorFactory _cursorFactory, int _version) {
            super(_context, _name, _cursorFactory, _version);
        }

        @Override
        public void onCreate(SQLiteDatabase _db) {
            // rowid's are omitted because it's not possible to refer to them, so just wasting space in this case.
            _db.execSQL("CREATE TABLE Units (" +
                    "id INTEGER NOT NULL, " +
                    "title STRING NOT NULL, " +
                    "PRIMARY KEY (id)" +
                    ") WITHOUT ROWID");
            _db.execSQL("CREATE TABLE Products (" +
                    "id INTEGER NOT NULL, " +
                    "title STRING NOT NULL, " +
                    "defaultvalue REAL NOT NULL, " +
                    "unit_id INTEGER, " +
                    "PRIMARY KEY (id), " +
                    "FOREIGN KEY (unit_id) REFERENCES Units(id) ON UPDATE RESTRICT ON DELETE CASCADE " +
                    ") WITHOUT ROWID");
            _db.execSQL("CREATE TABLE ShoppingLists (" +
                    "id INTEGER NOT NULL," +
                    "title STRING NOT NULL, " +
                    "PRIMARY KEY (id)" +
                    ") WITHOUT ROWID");
            _db.execSQL("CREATE TABLE ProductsInShoppingLists (" +
                    "shoppinglist_id INTEGER NOT NULL, " +
                    "product_id INTEGER NOT NULL, " +
                    "value REAL NOT NULL, " +
                    "PRIMARY KEY (shoppinglist_id, product_id), " +
                    "FOREIGN KEY (shoppinglist_id) REFERENCES ShoppingLists(id) ON UPDATE RESTRICT ON DELETE CASCADE, " +
                    "FOREIGN KEY (product_id) REFERENCES Products(id) ON UPDATE RESTRICT ON DELETE CASCADE " +
                    ") WITHOUT ROWID");
        }

        @Override
        public void onUpgrade(SQLiteDatabase _db, int _oldVersion, int _newVersion) {
            // Currently there is only db version 1.
        }

        @Override
        public void onConfigure(SQLiteDatabase _db) {
            _db.rawQuery("PRAGMA foreign_keys = ON", new String[0]);
        }
    }
}

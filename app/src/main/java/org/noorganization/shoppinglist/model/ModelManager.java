package org.noorganization.shoppinglist.model;

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
 * Created by michi on 12.02.15.
 */
public class ModelManager {

    public static final int INVALID_ID = 0xFFFFFFFF;

    static List<Product> m_sAllProducts;
    static List<ShoppingList> m_sAllLists;
    static List<Unit> m_sAllUnits;

    /**
     * Creates a new Product and registers it.
     * @param _title A name for the Product. null is not allowed (asserted).
     * @param _defaultValue
     * @param _unitId The id of the referencing Unit returned by {@link Unit#Id}. {@link #INVALID_ID} is also allowed
     *                and means something like "this product should have no unit".
     * @return The created Product.
     */
    public static Product createProduct(String _title, float _defaultValue, int _unitId) {
        if (m_sAllProducts == null) {
            m_sAllProducts = new ArrayList<>();
        }

        Product newProduct = new Product();
        newProduct.Title        = _title;
        newProduct.DefaultValue = _defaultValue;
        newProduct.UnitId       = _unitId;
        newProduct.Id           = generateId(m_sAllProducts.toArray(new Product[m_sAllProducts.size()]));
        m_sAllProducts.add(newProduct);

        return newProduct;
    }

    /**
     * Creates a ShoppingList and registers it automatically to the List of ShoppingList's.
     * @param _title Title for the new List, simply not null.
     * @return The constructed and registered ShoppingList,
     */
    public static ShoppingList createShoppingList(String _title) {
        if (m_sAllLists == null) {
            m_sAllLists = new ArrayList<ShoppingList>();
        }

        ShoppingList newList = new ShoppingList();
        newList.Title       = _title;
        newList.Id          = generateId(m_sAllLists.toArray(new ShoppingList[m_sAllLists.size()]));
        newList.ListEntries = new SparseArray<>();
        m_sAllLists.add(newList);

        return newList;
    }

    /**
     * Creates a Unit and registers it automatically in the list of all Units.
     * @param _unitText The "name" of the unit e.g. "kg" (kilogram) or "l" (liter). null is not valid.
     */
    public static Unit createUnit(String _unitText) {
        if (m_sAllUnits == null) {
            m_sAllUnits = new ArrayList<Unit>();
        }

        Unit newUnit = new Unit();
        newUnit.UnitText = _unitText;
        newUnit.Id       = generateId(m_sAllUnits.toArray(new IdentificableModelObject[m_sAllUnits.size()]));
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

    public static SQLiteDatabase openAndReadDatabase(Context _context, String _name) {
        DBOpenHelper databaseHelper = new DBOpenHelper(_context, _name, null, DBOpenHelper.CURRENT_DATABASE_VERSION);
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

    /**
     * Searches a Unit by it's id.
     * @param _id The internal id of the unit. If {@link org.noorganization.shoppinglist.model.Unit#INVALID_ID} is
     *            provided, no object will be found.
     * @return A Unit if found or null if no Unit was found.
     */
    public static Unit getUnitById(int _id) {
        if (_id == INVALID_ID || m_sAllUnits == null) {
            return null;
        }
        for (Unit CurrentUnit : m_sAllUnits) {
            if (CurrentUnit.Id == _id) {
                return CurrentUnit;
            }
        }
        return null;
    }
}

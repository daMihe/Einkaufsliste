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
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;

import org.noorganization.shoppinglist.model.ModelManager;
import org.noorganization.shoppinglist.model.Product;
import org.noorganization.shoppinglist.model.ShoppingList;
import org.noorganization.shoppinglist.model.Unit;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Locale;

public class ShoppingListPresenter {
    private ShoppingList      m_activeList;
    private SharedPreferences m_prefs;
    private Context           m_context;
    private ModelManager      m_model;
    private SQLiteDatabase    m_db;

    private static ShoppingListPresenter s_presenter;

    private ShoppingListPresenter(Context _context, String _sharedPrefName, String _dbName) {
        m_context = _context;
        m_prefs = m_context.getSharedPreferences(_sharedPrefName, Context.MODE_PRIVATE);
        m_model = ModelManager.getInstance();
        m_activeList = null;

        m_db = m_model.openAndReadDatabase(m_context, _dbName);

        if (m_prefs.contains(Constants.SP_CURRENT_LIST_ID)) {
            m_activeList = m_model.getShoppingListById(m_prefs.getInt(Constants.SP_CURRENT_LIST_ID, ModelManager.INVALID_ID));
        }
        if (m_activeList == null && m_model.getCountOfShoppingLists() != 0) {
            m_activeList = new ShoppingList(m_model.getAllShoppingLists()[0]);
            SharedPreferences.Editor editorForActiveList = m_prefs.edit();
            editorForActiveList.putInt(Constants.SP_CURRENT_LIST_ID, m_activeList.Id);
            editorForActiveList.commit();
        }
    }

    public boolean needsToCreateAList() {
        return (m_activeList == null);
    }

    public String getCurrentListTitle() {
        if (m_activeList == null) {
            return "";
        }
        return m_activeList.Title;
    }

    public static ShoppingListPresenter getInstance(Context _context) {
        return getInstance(_context, Constants.SHARED_PREFERENCES_NAME, Constants.DATABASE_NAME);
    }

    static ShoppingListPresenter getInstance(Context _context, String _sharedPrefName, String _dbName) {
        if (s_presenter == null) {
            s_presenter = new ShoppingListPresenter(_context, _sharedPrefName, _dbName);
        }

        return s_presenter;
    }

    static ShoppingListPresenter resetSingleton(Context _context, String _sharedPrefName, String _dbName) {
        s_presenter = null;
        return getInstance(_context, _sharedPrefName, _dbName);
    }

    /**
     * Creates a list and selects it (for usability reasons).
     * @param _newListTitle May not be null.
     * @return Whether creating was successful.
     */
    public boolean createList(String _newListTitle) {
        if (_newListTitle == null) {
            return false;
        }
        for (ShoppingList listToCheck : m_model.getAllShoppingLists()) {
            if (listToCheck.Title == _newListTitle) {
                return false;
            }
        }

        ShoppingList newList = m_model.createShoppingList(_newListTitle, m_db);
        selectList(newList.Id);

        return true;
    }

    /**
     * Creates a Map of list-titles to internal id's, that have to be used for writing operations.
     * @return the created map. Never null.
     */
    public HashMap<String, Integer> getLists() {
        HashMap<String, Integer> listMap = new HashMap<>();

        for (ShoppingList currentListToMap : m_model.getAllShoppingLists()) {
            listMap.put(currentListToMap.Title, currentListToMap.Id);
        }

        return listMap;
    }

    public void selectList(int _newList) {
        ShoppingList selectedList = m_model.getShoppingListById(_newList);
        if (selectedList == null) {
            return;
        }

        m_activeList = selectedList;
        SharedPreferences.Editor prefEditor = m_prefs.edit();
        prefEditor.putInt(Constants.SP_CURRENT_LIST_ID, m_activeList.Id);
        prefEditor.apply();
    }

    public HashMap<String, Integer> getActiveListEntries() {
        if (m_activeList == null) {
            return new HashMap<>();
        }

        HashMap<String, Integer> activeEntries = new HashMap<>();

        for (int currentPosition = 0; currentPosition < m_activeList.ListEntries.size(); currentPosition++) {
            Product currentProduct = m_model.getProductById(m_activeList.ListEntries.keyAt(currentPosition));
            Unit currentUnit = m_model.getUnitById(currentProduct.UnitId);

            String unitString = (currentUnit == null ? "" : currentUnit.UnitText);
            float value = m_activeList.ListEntries.valueAt(currentPosition);
            String entryString = currentProduct.Title;
            if (value > 1.001f || value < 0.999f || !unitString.isEmpty()) {
                entryString = new DecimalFormat("#.###").format(value) + unitString + " " + entryString;
            }

            activeEntries.put(entryString, currentProduct.Id);
        }

        return activeEntries;
    }

    public HashMap<String, Integer> getInactiveListEntries() {

        HashMap<String, Integer> inactiveEntries = new HashMap<>();
        for (Product currentProduct : m_model.getAllProducts()) {
            if (m_activeList == null || m_activeList.ListEntries.indexOfKey(currentProduct.Id) < 0) {
                inactiveEntries.put(currentProduct.Title, currentProduct.Id);
            }
        }

        return inactiveEntries;
    }

    public void deactivateListEntry(int _productToDeactivate) {
        if (m_activeList != null) {
            m_activeList.ListEntries.remove(_productToDeactivate);
            if (!m_model.updateShoppingList(m_activeList, m_db)) {
                m_activeList = m_model.getShoppingListById(m_activeList.Id);
            }
        }
    }

    public void activateListEntry(int _productToActivate, float _value) {
        if (m_model.getProductById(_productToActivate) != null && _value > 0.0f && m_activeList != null) {
            m_activeList.ListEntries.put(_productToActivate, _value);
            if (!m_model.updateShoppingList(m_activeList, m_db)) {
                m_activeList = m_model.getShoppingListById(m_activeList.Id);
            }
        }
    }

    public boolean deleteList(int _listToDelete) {
        if (m_activeList != null && m_activeList.Id == _listToDelete) {
            if (m_model.getCountOfShoppingLists() == 1) {
                return false;
            }

            m_model.deleteShoppingList(m_activeList, m_db);
            selectList(m_model.getAllShoppingLists()[0].Id);
        } else {
            ShoppingList toDelete = m_model.getShoppingListById(_listToDelete);
            if (toDelete != null) {
                m_model.deleteShoppingList(toDelete, m_db);
            }
        }

        return true;
    }
}

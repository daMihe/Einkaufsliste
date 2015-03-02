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
import android.util.Log;

import org.noorganization.shoppinglist.model.ModelManager;
import org.noorganization.shoppinglist.model.ShoppingList;

import java.util.HashMap;

public class ShoppingListPresenter {
    private ShoppingList      m_activeList;
    private SharedPreferences m_prefs;
    private Context           m_context;
    private ModelManager      m_model;

    private static ShoppingListPresenter s_presenter;

    private ShoppingListPresenter(Context _context, String _sharedPrefName, String _dbName) {
        m_context = _context;
        m_prefs = m_context.getSharedPreferences(_sharedPrefName, Context.MODE_PRIVATE);
        m_model = ModelManager.getInstance();
        m_activeList = null;

        if (!m_model.loaded()) {
            m_model.openAndReadDatabase(m_context, _dbName);
        }

        if (m_prefs.contains(Constants.SP_CURRENT_LIST_ID)) {
            m_activeList = m_model.getShoppingListById(m_prefs.getInt(Constants.SP_CURRENT_LIST_ID, ModelManager.INVALID_ID));
            if (m_activeList == null) {
                Log.d("TAG", String.format("Got null for %08x", m_prefs.getInt(Constants.SP_CURRENT_LIST_ID, ModelManager.INVALID_ID)));
            } else {
                Log.d("TAG", String.format("Got a List for %08x: %s", m_activeList.Id, m_activeList.Title));
            }
        }
        if (m_activeList == null && m_model.getCountOfShoppingLists() != 0) {
            Log.d("TAG", "updated currentlist");
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

    public boolean createList(String _newListTitle) {
        // TODO implement stub
        return false;
    }

    public HashMap<String, Integer> getLists() {
        // TODO implement stub
        return null;
    }

    public void selectList(int _newList) {
        // TODO implement stub
    }

    public HashMap<String, Integer> getActiveListEntries() {
        // TODO implement stub
        return null;
    }

    public HashMap<String, Integer> getInactiveListEntries() {
        // TODO implement stub
        return null;
    }

    public void deactivateListEntry(int _productToDeactivate) {
        // TODO implement stub
    }

    public void activateListEntry(int _productToActivate, float _value) {
        // TODO implement stub
    }

    public boolean deleteList(int _listToDelete) {
        // TODO implement stub
        return false;
    }
}

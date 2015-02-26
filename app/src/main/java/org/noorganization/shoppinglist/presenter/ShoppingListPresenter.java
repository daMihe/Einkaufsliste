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
import android.graphics.AvoidXfermode;

import org.noorganization.shoppinglist.model.ModelManager;
import org.noorganization.shoppinglist.model.ShoppingList;

public class ShoppingListPresenter {
    private ShoppingList      m_activeList;
    private SharedPreferences m_prefs;
    private Context           m_context;
    private ModelManager      m_model;

    private static ShoppingListPresenter s_presenter;

    private ShoppingListPresenter(Context _context) {
        m_context = _context;
        m_prefs = m_context.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        m_model = ModelManager.getInstance();

        if (!m_model.loaded()) {
            m_model.openAndReadDatabase(m_context, Constants.DATABASE_NAME);
        }

        if (m_prefs.contains(Constants.SP_CURRENT_LIST_ID)) {
            m_activeList = m_model.getShoppingListById(m_prefs.getInt(Constants.SP_CURRENT_LIST_ID, 0));
        }
        if (m_activeList == null) {
            if (m_model.getCountOfShoppingLists() == 0) {
                // TODO create list and load it then. maybe change activity?
            } else {
                m_activeList = m_model.getAllShoppingLists()[0];
                SharedPreferences.Editor editorForActiveList = m_prefs.edit();
                editorForActiveList.putInt(Constants.SP_CURRENT_LIST_ID, m_activeList.Id);
            }
        }
    }

    public String getCurrentListTitle() {
        if (m_activeList == null) {
            return "";
        }
        return m_activeList.Title;
    }

    public static ShoppingListPresenter getInstance(Context _context) {
        if (s_presenter == null) {
            s_presenter = new ShoppingListPresenter(_context);
        }

        return s_presenter;
    }
}

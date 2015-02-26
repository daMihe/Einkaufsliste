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
import android.graphics.AvoidXfermode;
import android.test.AndroidTestCase;
import android.util.Log;

import org.noorganization.shoppinglist.model.ModelManager;
import org.noorganization.shoppinglist.model.ShoppingList;

public class ShoppingListPresenterTest extends AndroidTestCase {

    public static final String DB_NAME = "nonOrganizationalTestBase.db";
    public static final String SP_NAME = "nonOrganizationalTestPrefs";

    private ShoppingListPresenter m_presenter;
    private ModelManager          m_model;
    private SQLiteDatabase        m_modelConnection;
    private SharedPreferences     m_prefs;

    public void setUp() throws Exception {
        super.setUp();

        m_model = ModelManager.getInstance();
        m_modelConnection = m_model.openAndReadDatabase(getContext(), DB_NAME);
        m_prefs = getContext().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);

        m_presenter = ShoppingListPresenter.resetSingleton(getContext(), SP_NAME, DB_NAME);
    }

    public void tearDown() throws Exception {
        m_modelConnection.close();
        getContext().deleteDatabase(DB_NAME);
        m_prefs.edit().clear().apply();
    }

    public void testNeedsToCreateAList() throws Exception {
        assertTrue(m_presenter.needsToCreateAList());

        m_model.createShoppingList("A shopping list", m_modelConnection);
        m_presenter = ShoppingListPresenter.resetSingleton(getContext(), SP_NAME, DB_NAME);

        assertFalse(m_presenter.needsToCreateAList());
    }

    public void testGetCurrentListTitle() throws Exception {
        assertEquals("", m_presenter.getCurrentListTitle());

        m_model.createShoppingList("A shopping list 2", m_modelConnection);
        ShoppingList createdList = m_model.createShoppingList("A shopping list 1", m_modelConnection);
        m_prefs.edit().putInt(Constants.SP_CURRENT_LIST_ID, createdList.Id).commit();
        m_presenter = ShoppingListPresenter.resetSingleton(getContext(), SP_NAME, DB_NAME);

        assertEquals("A shopping list 1", m_presenter.getCurrentListTitle());
    }

    public void testCreateList() throws Exception {
        fail("Test implementation missing.");
    }

    public void testSelectList() throws Exception {
        fail("Test implementation missing.");
    }

    public void testGetLists() throws Exception {
        fail("Test implementation missing.");
    }

    public void testGetActiveListEntries() throws Exception {
        fail("Test implementation missing.");
    }

    public void testGetInactiveListEntries() throws Exception {
        fail("Test implementation missing.");
    }

    public void testActivateListEntry() throws Exception {
        fail("Test implementation missing.");
    }

    public void testDeactivateListEntry() throws Exception {
        fail("Test implementation missing.");
    }

    public void testGetEditInformationForEntry() throws Exception {
        fail("Test implementation missing.");
    }

    public void testEditListEntry() throws Exception {
        fail("Test implementation missing.");
    }

    public void testDeleteList() throws Exception {
        fail("Test implementation missing.");
    }
}
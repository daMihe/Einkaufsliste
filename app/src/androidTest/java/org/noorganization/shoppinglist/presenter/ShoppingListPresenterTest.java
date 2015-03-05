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
import android.test.AndroidTestCase;

import org.noorganization.shoppinglist.model.ModelManager;
import org.noorganization.shoppinglist.model.Product;
import org.noorganization.shoppinglist.model.ShoppingList;

import java.util.HashMap;

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
        assertTrue(m_presenter.createList("New List title"));
        ShoppingList[] allLists = m_model.getAllShoppingLists();
        assertEquals(1, allLists.length);
        assertEquals("New List title", allLists[0].Title);
        assertEquals(allLists[0].Id, m_prefs.getInt(Constants.SP_CURRENT_LIST_ID, ModelManager.INVALID_ID));

        assertFalse(m_presenter.createList(null));
        allLists = m_model.getAllShoppingLists();
        assertEquals(1, allLists.length);
    }

    public void testSelectList() throws Exception {
        ShoppingList primaryList = m_model.createShoppingList("Primary List", m_modelConnection);
        ShoppingList secondaryList = m_model.createShoppingList("Secondary List", m_modelConnection);

        m_presenter.selectList(primaryList.Id);
        assertEquals(primaryList.Id, m_prefs.getInt(Constants.SP_CURRENT_LIST_ID, ModelManager.INVALID_ID));
        m_presenter.selectList(secondaryList.Id);
        assertEquals(secondaryList.Id, m_prefs.getInt(Constants.SP_CURRENT_LIST_ID, ModelManager.INVALID_ID));
    }

    public void testGetLists() throws Exception {
        assertEquals(0, m_presenter.getLists().size());

        ShoppingList resultingList = m_model.createShoppingList("List 1", m_modelConnection);
        HashMap<String, Integer> allLists = m_presenter.getLists();
        assertEquals(1, allLists.size());
        assertEquals(resultingList.Id, allLists.get("List 1").intValue());

        ShoppingList secResultingList = m_model.createShoppingList("List 2", m_modelConnection);
        allLists = m_presenter.getLists();
        assertEquals(2, allLists.size());
        assertEquals(secResultingList.Id, allLists.get("List 2").intValue());
    }

    public void testGetActiveListEntries() throws Exception {
        assertEquals(0, m_presenter.getActiveListEntries().size());

        Product testProductActive = m_model.createProduct("Active Product", 1.0f, ModelManager.INVALID_ID,
                m_modelConnection);
        m_model.createProduct("Inactive Product", 1.0f, ModelManager.INVALID_ID, m_modelConnection);
        ShoppingList testList = m_model.createShoppingList("List 1", m_modelConnection);
        testList.ListEntries.put(testProductActive.Id, 5.0f);
        m_model.updateShoppingList(testList, m_modelConnection);
        m_presenter.selectList(testList.Id);

        HashMap<String, Integer> activeEntries = m_presenter.getActiveListEntries();
        assertEquals(1, activeEntries.size());
        assertTrue(activeEntries.containsKey("5 Active Product"));
        assertEquals(testProductActive.Id, activeEntries.get("5 Active Product").intValue());

    }

    public void testGetInactiveListEntries() throws Exception {
        // TODO add tests and impl for more variants of text (plurals, number mangling etc.)
        assertEquals(0, m_presenter.getInactiveListEntries().size());

        Product testProductActive = m_model.createProduct("Active Product", 1.0f, ModelManager.INVALID_ID,
                m_modelConnection);
        Product testProductInactive = m_model.createProduct("Inactive Product", 1.0f, ModelManager.INVALID_ID,
                m_modelConnection);
        ShoppingList testList = m_model.createShoppingList("List 1", m_modelConnection);
        testList.ListEntries.put(testProductActive.Id, 5.0f);
        m_model.updateShoppingList(testList, m_modelConnection);
        m_presenter.selectList(testList.Id);

        HashMap<String, Integer> inactiveEntries = m_presenter.getInactiveListEntries();
        assertEquals(1, inactiveEntries.size());
        assertEquals(testProductInactive.Id, inactiveEntries.get("Inactive Product").intValue());
    }

    public void testActivateListEntry() throws Exception {
        Product testProductActive = m_model.createProduct("Activated Product", 1.0f, ModelManager.INVALID_ID,
                m_modelConnection);
        ShoppingList testList = m_model.createShoppingList("List 1", m_modelConnection);

        m_presenter = ShoppingListPresenter.resetSingleton(getContext(), SP_NAME, DB_NAME);
        m_presenter.activateListEntry(testProductActive.Id, 2.0f);

        assertEquals(2.0f, m_model.getShoppingListById(testList.Id).ListEntries.get(testProductActive.Id, Float.NaN));
    }

    public void testDeactivateListEntry() throws Exception {
        Product testProductActive = m_model.createProduct("Activate Product", 1.0f, ModelManager.INVALID_ID,
                m_modelConnection);
        Product testProductInactive = m_model.createProduct("Deactivated Product", 1.0f, ModelManager.INVALID_ID,
                m_modelConnection);
        ShoppingList testList = m_model.createShoppingList("List 1", m_modelConnection);
        testList.ListEntries.put(testProductActive.Id, 2.0f);
        testList.ListEntries.put(testProductInactive.Id, 2.0f);
        m_model.updateShoppingList(testList, m_modelConnection);
        m_presenter.selectList(testList.Id);

        m_presenter.deactivateListEntry(testProductInactive.Id);

        testList = m_model.getShoppingListById(testList.Id);
        assertEquals(1, testList.ListEntries.size());
        assertTrue(testList.ListEntries.indexOfKey(testProductInactive.Id) < 0);
    }

    public void testGetEditInformationForEntry() throws Exception {
        fail("Test implementation missing.");
    }

    public void testEditListEntry() throws Exception {
        fail("Test implementation missing.");
    }

    public void testDeleteList() throws Exception {
        ShoppingList testList = m_model.createShoppingList("List 1", m_modelConnection);
        m_presenter.selectList(testList.Id);

        assertFalse(m_presenter.deleteList(testList.Id));

        ShoppingList testList2 = m_model.createShoppingList("List 2", m_modelConnection);
        m_presenter.selectList(testList2.Id);

        assertTrue(m_presenter.deleteList(testList2.Id));
        assertEquals(testList.Id, m_prefs.getInt(Constants.SP_CURRENT_LIST_ID, ModelManager.INVALID_ID));

    }
}
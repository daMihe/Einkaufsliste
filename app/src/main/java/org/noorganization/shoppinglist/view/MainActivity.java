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

package org.noorganization.shoppinglist.view;

import android.app.ActionBar;
import android.app.FragmentManager;
import android.content.Context;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import org.noorganization.shoppinglist.R;
import org.noorganization.shoppinglist.presenter.ShoppingListPresenter;

import java.util.LinkedList;
import java.util.List;
import java.util.SortedMap;

public class MainActivity extends FragmentActivity {

    private ShoppingListPresenter m_presenter;
    private Spinner               m_listSelector;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        m_presenter = ShoppingListPresenter.getInstance(this);
        if (m_presenter.needsToCreateAList()) {
            m_presenter.createList("Test List");
        }

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shoppinglist);

        m_listSelector = new Spinner(this);
        m_listSelector.setAdapter(new ListSpinnerAdapter(m_presenter.getLists()));
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(m_listSelector);

        updateListDropDown();

        m_listSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> _parent, View _view, int _position, long _selectedId) {
                m_presenter.selectList((int) _selectedId);
                // TODO refresh listfragments
            }

            @Override
            public void onNothingSelected(AdapterView<?> _parent) {
            }
        });

    }

    public void updateListDropDown() {
        int selectedId = m_presenter.getCurrentListId();
        ((ListSpinnerAdapter) m_listSelector.getAdapter()).updateLists(m_presenter.getLists());
        for (int currentIndex = 0; currentIndex < m_listSelector.getCount(); currentIndex++) {
            if (m_listSelector.getItemIdAtPosition(currentIndex) == selectedId) {
                m_listSelector.setSelection(currentIndex);
                break;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu _menu) {
        getMenuInflater().inflate(R.menu.menu_shoppinglist, _menu);

        ShoppingListPresenter presenter = ShoppingListPresenter.getInstance(this);
        _menu.findItem(R.id.action_delete_list).setEnabled(presenter.getLists().size() > 1);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        FragmentManager fragMan = getFragmentManager();

        switch (item.getItemId()) {
            case R.id.action_create_list:
                fragMan.beginTransaction().add(new CreateListFragment(), null).commit();
                return true;
            case R.id.action_delete_list:
                fragMan.beginTransaction().add(new DeleteListFragment(), null).commit();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class ListSpinnerAdapter implements SpinnerAdapter {

        private SortedMap<String, Integer>  m_elements;
        private List<DataSetObserver>       m_observers;

        public ListSpinnerAdapter(SortedMap<String, Integer> _elements) {
            m_elements  = _elements;
            m_observers = new LinkedList<>();
        }

        public void updateLists(SortedMap<String, Integer> _elements) {
            m_elements = _elements;
            for (DataSetObserver currentObserver : m_observers) {
                currentObserver.onChanged();
            }
        }

        @Override
        public View getDropDownView(int _position, View _recycleView, ViewGroup _parent) {
            return getView(_position, _recycleView, _parent);
        }

        @Override
        public void registerDataSetObserver(DataSetObserver _dataSetObserver) {
            m_observers.add(_dataSetObserver);
        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver _dataSetObserver) {
            m_observers.remove(_dataSetObserver);
        }

        @Override
        public int getCount() {
            return m_elements.size();
        }

        @Override
        public Object getItem(int _position) {
            return m_elements.keySet().toArray(new String[m_elements.size()])[_position];
        }

        @Override
        public long getItemId(int _position) {
            return m_elements.values().toArray(new Integer[m_elements.size()])[_position];
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getView(int _position, View _recycleView, ViewGroup _parent) {
            TextView rtn;

            if (_recycleView != null) {
                rtn = (TextView) _recycleView;
            } else {
                LayoutInflater inflater = (LayoutInflater) _parent.getContext().
                        getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                rtn = (TextView) inflater.inflate(android.R.layout.simple_dropdown_item_1line, null);
            }

            rtn.setText((String) getItem(_position));
            rtn.setTag((int) getItemId(_position));

            return rtn;
        }

        @Override
        public int getItemViewType(int i) {
            return 1;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public boolean isEmpty() {
            return m_elements.isEmpty();
        }
    }


}

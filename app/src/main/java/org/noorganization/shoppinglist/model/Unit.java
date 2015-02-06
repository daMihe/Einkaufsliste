package org.noorganization.shoppinglist.model;


import android.os.SystemClock;
import android.renderscript.Int4;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

public class Unit implements Identificable {

    public static final int INVALID_ID = 0xFFFFFFFF;

    private static List<Unit> m_sAllUnits;

    private int m_Id;
    private String m_UnitText;

    /**
     * Searches a Unit by it's id.
     * @param _Id
     * @return A Unit if found or null if no Unit was found.
     */
    public static Unit findUnitById(int _Id) {
        for (Unit CurrentUnit : m_sAllUnits) {
            if (CurrentUnit.m_Id == _Id) {
                return CurrentUnit;
            }
        }
        return null;
    }

    /**
     * Creates a Unit and registers automatically itself in the list of all Units.
     * @param _UnitText The "name" of the unit e.g. "kg" (kilogram) or "l" (liter). null will cause an
     *                  IllegalArgumentException.
     * @throws java.lang.IllegalArgumentException
     */
    public Unit(String _UnitText) {
        if (_UnitText == null) {
            throw new IllegalArgumentException("_UnitText must contain a non-null string.");
        }

        m_UnitText = _UnitText;
        if (m_sAllUnits == null) {
            m_sAllUnits = new ArrayList<Unit>();
        }
        m_Id = HelperFunctions.generateId(m_sAllUnits, INVALID_ID);
        m_sAllUnits.add(this);
    }

    public int getId() {
        return m_Id;
    }

    public String getUnitText() {
        return m_UnitText;
    }

    public void invalidate() {
        m_Id = INVALID_ID;
    }

    /**
     * @param _NewUnitText Something like "kg" or "l". null is not a valid value.
     * @throws java.lang.IllegalArgumentException
     */
    public void setUnitText(String _NewUnitText) {
        if (_NewUnitText == null) {
            throw new IllegalArgumentException("_NewUnitText must contain a non-null string.");
        }
        m_UnitText = _NewUnitText;
    }
}

package org.noorganization.shoppinglist.model;


import java.util.ArrayList;
import java.util.List;

public class Unit implements Identificable {

    public static final int INVALID_ID = 0xFFFFFFFF;

    private static List<Unit> m_sAllUnits;

    private int m_Id;
    private String m_UnitText;

    private static final String ASSERT_ERROR_UNITTEXT_NULL = "UnitText must contain a non-null string.";

    /**
     * Searches a Unit by it's id.
     * @param _Id The internal id of the unit. If {@link org.noorganization.shoppinglist.model.Unit#INVALID_ID} is
     *            provided, no object will be found.
     * @return A Unit if found or null if no Unit was found.
     */
    public static Unit findById(int _Id) {
        if (_Id == INVALID_ID || m_sAllUnits == null) {
            return null;
        }
        for (Unit CurrentUnit : m_sAllUnits) {
            if (CurrentUnit.m_Id == _Id) {
                return CurrentUnit;
            }
        }
        return null;
    }

    private Unit(){
    }

    /**
     * Creates a Unit and registers it automatically in the list of all Units.
     * @param _UnitText The "name" of the unit e.g. "kg" (kilogram) or "l" (liter). null is not valid.
     */
    public static Unit Create(String _UnitText) {
        assert (_UnitText == null) : ASSERT_ERROR_UNITTEXT_NULL;
        if (m_sAllUnits == null) {
            m_sAllUnits = new ArrayList<Unit>();
        }

        Unit newUnit = new Unit();
        newUnit.m_UnitText = _UnitText;
        newUnit.m_Id = HelperFunctions.generateId(m_sAllUnits.toArray(new Identificable[m_sAllUnits.size()]), INVALID_ID);
        m_sAllUnits.add(newUnit);

        return newUnit;
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
     */
    public void setUnitText(String _NewUnitText) {
        assert (_NewUnitText == null) : ASSERT_ERROR_UNITTEXT_NULL;
        m_UnitText = _NewUnitText;
    }
}

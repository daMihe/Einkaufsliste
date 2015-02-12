package org.noorganization.shoppinglist.model;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by michi on 05.02.15.
 */
public interface SaveableModelObject {

    /**
     * Invalid id for a object. Objects having this id wont be saved to or deleted from the database.
     */
    public static final int INVALID_ID = 0xFFFFFFFF;

    /**
     * @return A unique id for this object. (unique does not mean globally only once but only once per database and
     * implementing type). May be also {@link #INVALID_ID}, which is the only value that is allowed multiple times.
     */
    public int getId();

    /**
     * Marks the object for deletion.
     */
    public void invalidate();
}

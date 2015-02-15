package org.noorganization.shoppinglist.model;

import android.os.SystemClock;

import java.util.Random;

/**
 * Created by michi on 05.02.15.
 */
class HelperFunctions {
    /**
     * Generates a unique id for an IdentificableModelObject object.
     * @return A collision-free, random id
     */
    static int generateId(IdentificableModelObject _ExistingObjects[], int _InvalidId) {
        Random randomGenerator = new Random();
        randomGenerator.setSeed(SystemClock.uptimeMillis());
        while (true) {
            int newId = randomGenerator.nextInt();
            if (newId == _InvalidId) {
                continue;
            }
            boolean unique = true;
            for (IdentificableModelObject currentObject : _ExistingObjects) {
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
}

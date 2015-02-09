package org.noorganization.shoppinglist.model;

import android.os.SystemClock;

import java.util.List;
import java.util.Random;

/**
 * Created by michi on 05.02.15.
 */
class HelperFunctions {
    /**
     * Generates a unique id for an Identificable object.
     * @return A collision-free, random id
     */
    static int generateId(Identificable _ExistingObjects[], int _InvalidId) {
        Random randomGenerator = new Random();
        randomGenerator.setSeed(SystemClock.uptimeMillis());
        while (true) {
            int newId = randomGenerator.nextInt();
            if (newId == _InvalidId) {
                continue;
            }
            boolean unique = true;
            for (Identificable currentObject : _ExistingObjects) {
                if (currentObject.getId() == newId) {
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

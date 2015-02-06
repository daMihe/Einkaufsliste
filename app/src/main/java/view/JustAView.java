package view;

import org.noorganization.shoppinglist.model.Unit;

/**
 * Created by michi on 30.01.15.
 */
public class JustAView {
    int func() {
        new Unit(5, "bla");

        return Unit.findUnitById(7).getId();
    }
}

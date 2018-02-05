package de.dfki.lt.hfc.operators;

import de.dfki.lt.hfc.FunctionalOperator;
import de.dfki.lt.hfc.types.XsdMonetary;
/**
 * Computes the increment of the single argument given to apply();
 * argument is assumed to be an xsd:monetary;
 * returns a representation of the new monetary
 *
 * @see FunctionalOperator
 *
 * Created by christian on 14/06/17.
 */
public class MonetaryIncrement extends FunctionalOperator {

    @Override
    public int apply(int[] args) {
        XsdMonetary monetary = ((XsdMonetary) getObject(args[0]));
        double d = Math.nextUp(monetary.amount);
        XsdMonetary M = new XsdMonetary(d, monetary.currency);
        return registerObject(M.toString(this.tupleStore.namespace.shortIsDefault), M);
    }

}

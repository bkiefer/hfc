package de.dfki.lt.hfc.operators;

import de.dfki.lt.hfc.FunctionalOperator;
import de.dfki.lt.hfc.types.XsdLong;

import static java.lang.Math.abs;

/**
 * computes the absolute difference of the two arguments given to apply();
 * arguments are assumed to be of type xsd:long;
 * returns a representation of the new long
 *
 * @see FunctionalOperator
 *
 * @author (C) Christian Willms
 * @since JDK 1.5
 * @version Fri Aug 04 16:29:19 CEST 2017
 */
public final class LAbsoluteDifference extends FunctionalOperator {

    /**
     * note that apply() does NOT check at the moment whether the int args
     * represent in fact XSD longs;
     * note that apply() does NOT check whether it is given exactly two arguments
     */
    public int apply(int[] args) {
        long l = ((XsdLong)getObject(args[0])).value - ((XsdLong)getObject(args[1])).value;
        XsdLong L = new XsdLong(abs(l));
        return registerObject(L.toString(this.tupleStore.namespace.shortIsDefault), L);
    }

}

package de.dfki.lt.hfc.operators;

import de.dfki.lt.hfc.FunctionalOperator;
import de.dfki.lt.hfc.types.XsdDateTime;

public class DTGreater extends FunctionalOperator {


    @Override
    public int apply(int[] args) {
        XsdDateTime date = ((XsdDateTime) getObject(args[0]));
        return (date.compareTo(args[1]) > 0) ? FunctionalOperator.TRUE : FunctionalOperator.FALSE;
    }
}

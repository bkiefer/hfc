package de.dfki.lt.hfc.types;

import java.util.ArrayList;

/**
 * Created by chwi02 on 14.03.17.
 */
public abstract class XsdPoint extends XsdAnySimpleType {

    protected long zOrder;

    protected boolean neg = false;

    protected abstract void computeZOrder();

    public long getZOrder(){
        return this.zOrder;
    }




}

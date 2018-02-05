package de.dfki.lt.hfc.qrelations;


import de.dfki.lt.hfc.types.*;

import java.util.HashMap;

/**
 * This class is used as a super class for all of RCC8 relations. It was introduced to provide
 * access to the different operators necessary to model these relations as Indexlookups
 * or when rewriting the relation as filter clauses.
 *
 * Created by christian on 25/05/17.
 */
public abstract class QRelationRCC8 extends QRelation {

    /**
     * Mapping form classes of xsd:types to the String representation of the corresponding LESS operator.
     */
    private static final HashMap<Class, String> typeToLess;

    /**
     * Mapping form classes of xsd:types to the String representation of the corresponding LESSEqual operator.
     */
    private static final HashMap<Class, String> typeToLessEqual;

    /**
     * Mapping form classes of xsd:types to the String representation of the corresponding Equal operator.
     */
    private static final HashMap<Class, String> typeToEqual;

    /**
     * Mapping form classes of xsd:types to the String representation of the corresponding Greater operator.
     */
    private  static final HashMap<Class, String> typeToGreater;

    static {
        typeToLess = new HashMap<>();
        typeToLess.put(Xsd2DPoint.class, "Point2DLess");
        typeToLess.put(Xsd3DPoint.class, "Point3DLess");
        typeToLessEqual = new HashMap<>();
        typeToLessEqual.put(Xsd3DPoint.class, "Point3DLessEqual");
        typeToLessEqual.put(Xsd2DPoint.class, "Point2DLessEqual");
        typeToEqual = new HashMap<>();
        typeToEqual.put(Xsd2DPoint.class, "Point2DEqual");
        typeToEqual.put(Xsd3DPoint.class, "Point3DEqual");
        typeToGreater = new HashMap<>();
        typeToGreater.put(Xsd2DPoint.class, "Point2DGreater");
        typeToGreater.put(Xsd3DPoint.class, "Point3DGreater");
    }

    public boolean isValid(){
        return false;
    }

    @Override
    public boolean isAllenRelation() {
        return false;
    }

    @Override
    public boolean isInterval() {
        return false;
    }
}

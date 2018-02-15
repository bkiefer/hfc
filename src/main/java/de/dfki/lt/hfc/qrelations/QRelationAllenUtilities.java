package de.dfki.lt.hfc.qrelations;


import de.dfki.lt.hfc.FunctionalOperator;
import de.dfki.lt.hfc.OperatorRegistry;
import de.dfki.lt.hfc.TupleStore;
import de.dfki.lt.hfc.types.*;

import java.util.HashMap;

/**
 * @author Christian Willms - Date: 08.09.17 18:03.
 * @version 08.09.17
 */
public class QRelationAllenUtilities {

    /**
     *
     */
    private static final HashMap<Class, AnyType> typeToMinValue ;

    /**
     *
     */
    private static final HashMap<Class, AnyType> typeToMaxValue;

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
    private   static final HashMap<Class, String> typeToGreater;

    /**
     * Mapping form classes of xsd:types to the String representation of the corresponding GreaterEqual operator.
     */
    private   static final HashMap<Class, String> typeToGreaterEqual;

    /**
     * Mapping form classes of xsd:types to the String representation of the corresponding Increment operator.
     */
    private static final HashMap<Class, String> typeToIncrement;

    /**
     * Mapping form classes of xsd:types to the String representation of the corresponding Decrement operator.
     */
    private static final HashMap<Class, String> typeToDecrement;

    private static TupleStore tupleStore;

    static {
        typeToMaxValue = new HashMap<>();
        typeToMaxValue.put(XsdDateTime.class, new XsdDateTime(Integer.MAX_VALUE,Integer.MAX_VALUE,Integer.MAX_VALUE,Integer.MAX_VALUE,Integer.MAX_VALUE,Float.MAX_VALUE));
        typeToMaxValue.put(XsdFloat.class, new XsdFloat(Float.MAX_VALUE));
        typeToMaxValue.put(XsdInt.class, new XsdInt(Integer.MAX_VALUE));
        typeToMaxValue.put(XsdLong.class, new XsdLong(Long.MAX_VALUE));
        typeToMaxValue.put(XsdDate.class, new XsdDate(9999,12,31));
        typeToMinValue = new HashMap<>();
        typeToMinValue.put(XsdDateTime.class, new XsdDateTime(Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE,Float.MIN_VALUE));
        typeToMinValue.put(XsdFloat.class, new XsdFloat(Float.MIN_VALUE));
        typeToMinValue.put(XsdInt.class, new XsdInt(Integer.MIN_VALUE));
        typeToMinValue.put(XsdLong.class, new XsdLong(Long.MIN_VALUE));
        typeToMinValue.put(XsdDate.class, new XsdDate(-9999,1,1));
        typeToLess = new HashMap<>();
        typeToLess.put(XsdDateTime.class, "DTLess");
        typeToLess.put(XsdFloat.class, "FLess");
        typeToLess.put(XsdInt.class, "ILess");
        typeToLess.put(XsdLong.class, "LLess");
        typeToLess.put(XsdDate.class, "DLess");
        typeToLessEqual = new HashMap<>();
        typeToLessEqual.put(XsdDateTime.class, "DTLessEqual");
        typeToLessEqual.put(XsdFloat.class, "FLessEqual");
        typeToLessEqual.put(XsdInt.class, "ILessEqual");
        typeToLessEqual.put(XsdLong.class, "LLessEqual");
        typeToEqual = new HashMap<>();
        typeToEqual.put(XsdDateTime.class, "DTEqual");
        typeToEqual.put(XsdFloat.class, "FEqual");
        typeToEqual.put(XsdInt.class, "IEqual");
        typeToEqual.put(XsdLong.class, "LEqual");
        typeToGreater = new HashMap<>();
        typeToGreater.put(XsdDateTime.class, "DTGreater");
        typeToGreater.put(XsdFloat.class, "FGreater");
        typeToGreater.put(XsdInt.class, "IGreater");
        typeToGreater.put(XsdLong.class, "LGreater");
        typeToGreaterEqual = new HashMap<>();
        typeToGreaterEqual.put(XsdDateTime.class, "DTGreaterEqual");
        typeToGreaterEqual.put(XsdFloat.class, "FGreaterEqual");
        typeToGreaterEqual.put(XsdInt.class, "IGreaterEqual");
        typeToGreaterEqual.put(XsdLong.class, "LGreaterEqual");
        typeToIncrement = new HashMap<>();
        typeToIncrement.put(XsdInt.class, "IIncrement");
        typeToIncrement.put(XsdFloat.class, "FIncrement");
        typeToIncrement.put(XsdLong.class, "LIncrement");
        typeToIncrement.put(XsdDateTime.class, "DTIncrement");
        typeToDecrement = new HashMap<>();
        typeToDecrement.put(XsdInt.class, "IDecrement");
        typeToDecrement.put(XsdFloat.class, "FDecrement");
        typeToDecrement.put(XsdLong.class, "LDecrement");
        typeToDecrement.put(XsdDateTime.class, "DTDecrement");
    }


    static XsdAnySimpleType increment(int id){
        XsdAnySimpleType type = (XsdAnySimpleType) tupleStore.getJavaObject(id);
        FunctionalOperator op = (FunctionalOperator) tupleStore.getOperator(OperatorRegistry.OPERATOR_PATH+typeToIncrement.get(type.getClass()));
        int incrementedObject_ID = op.apply(new int[]{id});
        return (XsdAnySimpleType) tupleStore.getJavaObject(incrementedObject_ID);
    }

    static   XsdAnySimpleType decrement(int id){
        XsdAnySimpleType type = (XsdAnySimpleType) tupleStore.getJavaObject(id);
        FunctionalOperator op = (FunctionalOperator) tupleStore.getOperator(OperatorRegistry.OPERATOR_PATH+typeToDecrement.get(type.getClass()));
        int decrementedObject_ID = op.apply(new int[]{id});
        return (XsdAnySimpleType) tupleStore.getJavaObject(decrementedObject_ID);
    }

    public static void setTupleStore(TupleStore tupleStore){
        QRelationAllenUtilities.tupleStore = tupleStore;
    }


    public static String getGreater(Class<? extends XsdAnySimpleType> aClass) {
        return typeToGreater.get(aClass);
    }

    public static String getGreaterEqual(Class<? extends XsdAnySimpleType> aClass) {
        return typeToGreaterEqual.get(aClass);
    }

    public static String getLessEqual(Class<? extends XsdAnySimpleType> aClass) {
        return typeToLessEqual.get(aClass);
    }

    public static String getLess(Class<? extends XsdAnySimpleType> aClass) {
        return typeToLess.get(aClass);
    }

    public static AnyType getMinValue(Class<? extends XsdAnySimpleType> aClass) {
        return typeToMinValue.get(aClass);
    }

    public static AnyType getMaxValue(Class<? extends XsdAnySimpleType> aClass) {
        return typeToMaxValue.get(aClass);
    }

    public static String getEqual(Class<? extends XsdAnySimpleType> aClass) {
        return typeToEqual.get(aClass);
    }
}

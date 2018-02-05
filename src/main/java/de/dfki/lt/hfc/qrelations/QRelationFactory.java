package de.dfki.lt.hfc.qrelations;

import de.dfki.lt.hfc.QueryParseException;
import de.dfki.lt.hfc.TupleStore;
import de.dfki.lt.hfc.WrongFormatException;
import de.dfki.lt.hfc.types.XsdAnySimpleType;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * @author Christian Willms - Date: 08.09.17 16:40.
 * @version 08.09.17
 */
public class QRelationFactory {

    /**
     * A basic LOGGER.
     */
    private static final Logger LOGGER = Logger.getLogger(QRelationFactory.class.getName());
    private static boolean verbose;

    /**
     * the association between a relation class and its constructor
     */
    private static final HashMap<String, Constructor<QRelation>> nameToRe;

    /**
     * The String representations of all possible Intervals
     */
    private static final Set<String> INTERVALS = new HashSet<>(Arrays.asList("[]", "[)", "()", "(]"));

    /**
     * The String representation of all possible interval relations ( Allen´s interval relations are implemented)
     */
    private static final Set<String> INTERVAL_RELATIONS = new HashSet<>(Arrays.asList("S", "Si", "F", "Fi", "D", "Di", "Bf", "Af", "M", "Mi", "EA", "O", "Oi"));

    /**
     * The String representation of all possible geo spatial relations ( RCC8 relations are implemented)
     */
    private static final Set<String> RCC8_RELATIONS = new HashSet<>(Arrays.asList("DC", "EC", "EQ", "PO", "TPP", "TPPi", "NTPP", "NTPPi"));

    /**
     * The {@link TupleStore} which is active at runtime
     */
    private static TupleStore tupleStore;

    /**
     * This counter is used to create new variables, used as a internalized representation of the relation.
     */
    private static int counter = 1;

    static {
        LOGGER.setLevel(Level.ERROR);
        nameToRe = new HashMap<String, Constructor<QRelation>>();
        loadRelations();
    }

    public static void initFactory(TupleStore tupleStore){
        QRelationAllenUtilities.setTupleStore(tupleStore);
        counter = 1;
        QRelationFactory.tupleStore =tupleStore;
        verbose = tupleStore.verbose;
    }

    /**
     * all (custom) operators should be put into package de.dfki.lt.hfc.operators
     */
    public static final String RELATION_PATH = "de.dfki.lt.hfc.qrelations.";  // last '.' char is needed

    /**
     * if true, register() exits with error code 1 in case registration fails;
     * if false, register() returns null in case registration fails
     */
    public static final boolean EXIT_WHEN_REGISTRATION_FAILS = true;


    private static void loadRelations() {
        String[] relations = {
                "Interval", // [ or (
                "AllenAfter", // >
                "AllenBefore", // <
                "AllenDuring", // D or Di
                "AllenMeet", // M or Mi
                "AllenOverlaps", // O or Oi
                "AllenStart", // S or Si
                "AllenFinish", // F or Fi
                "AllenEqual", // =
                "RCC8Equal", // Eq
        };
        for (String relation : relations) {
            if(verbose)
                LOGGER.info("Class for name " + RELATION_PATH + relation);
            try {
                Class.forName(RELATION_PATH + relation);
            } catch (ClassNotFoundException e) {
                // Should never happen
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Register a constructor for a query relation here, so that it can be used
     * by the factory method getRelation
     *
     * @param clazz The class object of some XsdAnySimpleType type
     * @param forms The short and long form of the type tag
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    protected static void registerConstructor(Class clazz, String... forms) {
        try {
            Constructor<QRelation> constructor =
                    clazz.getConstructor(String.class, int.class, int.class, int.class);
            for (String s : forms) {
                nameToRe.put(s, constructor);
            }
        } catch (Exception e) {  // should never happen
            throw new RuntimeException(e);
        }
    }

    /**
     * @param isExclusive boolean flag indicating whether the a [ or ( was already processed.
     * @param st          the string tokenizer providing the input of the parsing process.
     * @param tuple       the result of the already processed part of the clause.
     * @return String representation of the parsed interval.
     * @throws QueryParseException
     */
    public static String parseInterval(boolean isExclusive, StringTokenizer st, ArrayList<String> tuple) throws QueryParseException {
        // the leading  "[", "(" character has already been consumed, so consume tokens until we
        // find the closing character "]", ")"
        String token, end = "";
        StringBuilder sb = new StringBuilder();
        sb.append((isExclusive) ? "[" : "(");
        String[] values = {"", ""};
        int counter = 0;
        while (st.hasMoreTokens()) {
            token = st.nextToken();
            //System.out.println(token);
            if (token.equals("]")) {
                if (values[0].equals("") || values[1].equals(""))
                    throw new QueryParseException("An interval must consist of exactly two values! At least one is missing");
                end = "]";
                break;
            } else if (token.equals(")")) {
                if (values[0].equals("") || values[1].equals(""))
                    throw new QueryParseException("An interval must consist of exactly two values! At least one is missing");
                end = ")";
                break;
            } else if (token.equals("\"")) {
                values[counter] = tupleStore.parseAtom(st, tuple);
                tupleStore.putObject(values[counter]);
            } else if (token.equals(",")) {
                if (values[0].equals("") || counter == 1)
                    throw new QueryParseException("An interval must consist of exactly two values! ");
                counter++;
            } else if (token.equals(" "))  // keep on parsing ...
                continue;
        }
        if (end.equals(""))
            throw new QueryParseException("An interval must end with an ] or )!");
        sb.append(end);
        tuple.add(sb.toString());
        return sb.toString();
    }

    /**
     * @param token The String that triggered the calling of this method.
     * @param st    the string tokenizer providing the input of the parsing process.
     * @param tuple the result of the already processed part of the clause.
     * @return String representation of the parsed interval.
     * @throws QueryParseException
     */
    public static String parseAllenRelation(String token, StringTokenizer st, ArrayList<String> tuple) throws QueryParseException {
        StringBuilder sb = new StringBuilder(token);
        int elementCounter = 0;
        while (st.hasMoreTokens() && elementCounter < 2) {
            token = st.nextToken();
            if (token.equals("\"")) {
                tupleStore.putObject(tupleStore.parseAtom(st, tuple));
                elementCounter++;
            } else if (token.equals(" "))  // keep on parsing ...
                continue;
            else
                throw new QueryParseException("  incorrect relation clause");
        }
        if (elementCounter != 2)
            throw new QueryParseException("An relation not enough arguments for the relation!");
        tuple.add(sb.toString());
        return sb.toString();
    }


    /**
     * @param token The String that triggered the calling of this method.
     * @param st    the string tokenizer providing the input of the parsing process.
     * @param tuple the result of the already processed part of the clause.
     * @return String representation of the parsed interval.
     * @throws QueryParseException
     */
    public static String parseRCC8Relation(String token, StringTokenizer st, ArrayList<String> tuple) throws QueryParseException {
        String end = "";
        String t = token;
        StringBuilder sb = new StringBuilder();
        return sb.toString();
    }


    /**
     * A factory method to generate the correct AnyType subclass from the string
     * representation
     *
     * @throws WrongFormatException in case the type is not known, or the
     *                              constructor<String> does not exist.
     */
    public static QRelation getRelation(String literal, int firstArgument_ID, int secondArgument_ID, int i) throws QueryParseException {
        try {
            QRelation relation = nameToRe.get(literal).newInstance(literal, firstArgument_ID, secondArgument_ID, i);
            relation.firstArgumentObject = (XsdAnySimpleType) tupleStore.getJavaObject(firstArgument_ID);
            relation.secondArgumentObject = (XsdAnySimpleType) tupleStore.getJavaObject(secondArgument_ID);
            relation.setType(getType(firstArgument_ID));
            if (relation.isValid())
                return relation;
            else
                throw new QueryParseException("Invalid QRelation definition: " + relation.toString());
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isAllenRelation(String token) {
        return QRelationFactory.INTERVAL_RELATIONS.contains(token);
    }

    public static boolean isRCC8Relation(String token) {
        return QRelationFactory.RCC8_RELATIONS.contains(token);
    }

    /**
     * Checks whether the given element is a QRelation
     *
     * @param elem The String to validate.
     * @return true, if the string is a representaton of a Qrelation,
     * false otherwise.
     */
    public static boolean isRelation(String elem) {
        return INTERVALS.contains(elem) || INTERVAL_RELATIONS.contains(elem) || RCC8_RELATIONS.contains(elem);
    }

    public static String createNewVariable() {
        return "?rel" + counter++;
    }

    public static Class getType(int id) {
        return tupleStore.getJavaObject(id).getClass();
    }
}

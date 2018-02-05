package de.dfki.lt.hfc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.StringTokenizer;

/**
 * TODO
 * Created by christian on 18/05/17.
 */
public class IntervalRelation {

    public  static HashSet<String> identifier =  new HashSet<String>(Arrays.asList("[", "(")); // TODO add additional values

//    public static String parseInterval(TupleStore ts, StringTokenizer st , ArrayList<String> tuple, boolean closed) throws QueryParseException {
//        // the leading  "[", "(" character has already been consumed, so consume tokens until we
//        // find the closing character "]", ")"
//        StringBuilder sb;
//        if(closed) {
//             sb = new StringBuilder("[");
//        } else {
//            sb = new StringBuilder("(");
//        }
//        String token, end= "";
//
//        while (st.hasMoreTokens()) {
//            token = st.nextToken();
//            if (token.equals("]")) {
//                end = "]";
//                break;
//            } else if (token.equals(")")) {
//                end = "]";
//                break;
//            } else {
//                // normalize the namespace
//                sb.append(ts.parseAtom(st, tuple));
//            }
//        }
//        if (end.equals(""))
//            throw new QueryParseException("An interval must end with an ] or )!");
//        token = sb.append(end).toString();
//        tuple.add(token);
//        return token;
//    }



}

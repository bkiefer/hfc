package de.dfki.lt.hfc.operators;

import de.dfki.lt.hfc.FunctionalOperator;
import de.dfki.lt.hfc.types.XsdDateTime;

/**
 * Computes the increment of the single argument given to apply();
 * argument is assumed to be an xsd:dateTime;
 * returns a representation of the new dateTime
 *
 * @see FunctionalOperator
 *
 * Created by christian on 14/06/17.
 */
public class DTIncrement extends FunctionalOperator {

    /**
     * note that apply() does NOT check at the moment whether the int arg
     * represent in fact an XSD int or float;
     * note that apply() does NOT check whether it is given exactly one argument
     */
    @Override
    public int apply(int[] args) {
        XsdDateTime date = ((XsdDateTime) getObject(args[0]));
        XsdDateTime newDate;
            if (date.second < 60)
                newDate = new XsdDateTime(date.year, date.month, date.day, date.hour, date.minute, date.second + 1);
            else if(date.minute <60)
                newDate = new XsdDateTime(date.year, date.month, date.day, date.hour, date.minute +1,  1);
            else if(date.hour < 24)
                newDate = new XsdDateTime(date.year, date.month, date.day, date.hour + 1 , 1,  1);
            else if(date.day < 31)
                newDate = new XsdDateTime(date.year, date.month, date.day +1 ,  1 , 1,  1);
            else if(date.month < 12)
                newDate = new XsdDateTime(date.year, date.month +1 , 1 ,  1 , 1,  1);
            else
            newDate = new XsdDateTime(date.year +1,1 , 1 ,  1 , 1,  1);
        return registerObject(newDate.toString(this.tupleStore.namespace.shortIsDefault), newDate);
    }
}

package de.dfki.lt.hfc.indexingStructures;

import de.dfki.lt.hfc.types.AnyType;

/**
 * @author Christian Willms - Date: 30.10.17 17:28.
 * @version 30.10.17
 */
public class IntervalTest implements Comparable {

    public AnyType start, end;

    IntervalTest(AnyType start,AnyType end){
        this.start = start;
        this.end = end;
    }

    @Override
    public int compareTo(Object o) {
        if (this.start.compareTo(((IntervalTest)o).start)< 0) {
            return -1;
        }
        else if (this.start.compareTo(((IntervalTest)o).start) == 0) {
            if (this.end.compareTo(((IntervalTest)o).end) == 0)
                return 0;
            else
                return this.end.compareTo(((IntervalTest)o).end) < 0 ? -1 : 1;
        }
        else {
            return 1;
        }
    }

    @Override
    public String toString(){
        return "["+ start + " | "+end +"]";
    }
}

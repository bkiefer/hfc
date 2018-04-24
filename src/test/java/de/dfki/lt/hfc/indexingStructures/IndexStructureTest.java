package de.dfki.lt.hfc.indexingStructures;

import de.dfki.lt.hfc.indices.IndexingException;
import de.dfki.lt.hfc.types.AnyType;
import de.dfki.lt.hfc.types.XsdInt;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;


/**
 * @author Christian Willms - Date: 29.10.17 15:04.
 * @version 29.10.17
 */
public abstract class IndexStructureTest {

    protected abstract void add(AnyType key, Set<int[]> value);

    protected abstract void clearData();

    protected abstract void validateData() throws IndexingException;

    protected abstract void validateSearch(AnyType key )  throws IndexingException ;

    public void validateAll() throws IndexingException {
        //btree.printTree();
        validateData();
//        validateSize();

    }

    public void addRandomKeys(int min, int max, int iterations) {
        int minNum = min;
        int maxNum = max;
        int nVal;
        for (int i = 0; i < iterations; ++i) {
            nVal = randInt(minNum, maxNum);
            add(new XsdInt(nVal), new HashSet<>(Arrays.asList(new int[]{randInt(min,max),randInt(min,max),randInt(min,max) })));
        }
    }

    //
    // Randomly generate integer within the specified range
    //
    protected int randInt(int min, int max) {
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }

    @Test
    public void validateTestCase1() throws IndexingException {
        clearData();
        addRandomKeys(-10, 10, 10);
        for (int i = -10; i < 10; ++i) {
            validateSearch(new XsdInt(i));
        }
        validateAll();
    }

    /**
     * @throws IndexingException
     */
    @Test
    public void validateTestCase2() throws IndexingException {
        clearData();
        addRandomKeys(0,400, 100);
        for (int i = -10; i < 100; ++i) {
            validateSearch(new XsdInt(i));
        }
        validateAll();
    }

    //@Test
    /**
     @Tag
    public void validateTestCase3() throws IndexingException {
        clearData();
        addRandomKeys(0,1000, 4000);
        for (int i = -10; i < 1000; ++i) {
            validateSearch(new XsdInt(i));
        }
        validateAll();
    }

    @Test
    @Tag("slow")
    public void validateTestCase4() throws IndexingException {
        clearData();
        addRandomKeys(-1000,10000, 40000);
        for (int i = 666; i < 1222; ++i) {
            validateSearch(new XsdInt(i));
        }
        validateAll();
    }

    @Test
    @Tag("slow")
    public void validateTestCase5() throws IndexingException {
        clearData();
        addRandomKeys(-10000,100000, 400000);
        for (int i = 666; i < 12220; ++i) {
            validateSearch(new XsdInt(i));
        }
        validateAll();
    }
    **/

    public void addKey(int i) {
        add(new XsdInt(i), new HashSet<>(Arrays.asList(new int[]{i, i, i, i})));
    }
}

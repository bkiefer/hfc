package de.dfki.lt.hfc.operators;
import static org.junit.Assert.*;

import de.dfki.lt.hfc.FunctionalOperator;
import de.dfki.lt.hfc.NamespaceManager;
import de.dfki.lt.hfc.TupleStore;
import de.dfki.lt.hfc.WrongFormatException;
import de.dfki.lt.hfc.types.Uri;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;

import static de.dfki.lt.hfc.TestingUtils.*;

public class NoSubClassOfTest {

  @Test
  public void testNoSubClassOf() throws FileNotFoundException,
          WrongFormatException, IOException {

    // load NamespaceManager
    NamespaceManager namespace = NamespaceManager.getInstance();

    // create TupleStore
    TupleStore store =
            new TupleStore(false, true, true, 2, 5,0,1,2, 4, 2, namespace,
                    getTestResource("default.nt"));

    // store values in TupleStore, save integer-key in database
    int[] validArgs = new int[3];

    //<rdf:type> <rdf:type> <rdf:Property> .
    validArgs[0] = store.putObject((new Uri("<rdf:type>", NamespaceManager.RDF)).toString());
    validArgs[1] = store.putObject((new Uri("<rdf:type>", NamespaceManager.RDF)).toString());
    validArgs[2] = store.putObject((new Uri("<rdf:Property>", NamespaceManager.RDF)).toString());

    // TODO test also invalid args

    // create FunctionalOperator
    FunctionalOperator fop =
            (FunctionalOperator)store.checkAndRegisterOperator("de.dfki.lt.hfc.operators.NoSubClassOf");


    // do operation
    assertEquals("Valid Tuple: <rdf:type> <rdf:type> <rdf:Property>", FunctionalOperator.TRUE, fop.apply(validArgs));
    //assertEquals("Valid Tuple: <rdf:type> <rdf:type> <rdf:Property>", FunctionalOperator.FALSE, fop.apply(invalidArgs));
  }

}








package de.dfki.lt.hfc;


import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HTCTest {

  @Test
  public void test() {
    Hfc hfc = new Hfc();

    String[] in = {
        "<http://www.dfki.de/lt/onto/pal/rifca.owl>",
        "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>",
        "<http://www.w3.org/2002/07/owl#Ontology>",
        "\"blabla\"^^<http://www.w3.org/2001/XMLSchema#string>",
        "\"blabla\"@en",
        "<rdf:type>",
    };
    String[] exp = {
        "<http://www.dfki.de/lt/onto/pal/rifca.owl>",
        "<rdf:type>",
        "<owl:Ontology>",
        "\"blabla\"^^<xsd:string>",
        "\"blabla\"@en",
        "<rdf:type>",
    };
    for (int i = 0; i < in.length; ++i) {
      assertEquals(exp[i], exp[i], hfc.myNormalizeNamespaces(in[i]));
    }
  }

}

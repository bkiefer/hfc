package de.dfki.lt.hfc;
import static de.dfki.lt.hfc.TestingUtils.*;
import static org.junit.Assert.*;

import org.junit.*;


public class LGetLatestTest {
  static Hfc fc;

  private static String getResource(String name) {
    return TestingUtils.getTestResource("LGetLatest", name);
  }

  @Before
  public  void init() throws Exception {

    fc =  new Hfc(Config.getInstance(getResource("test.yml")));

    // compute deductive closure

    fc.computeClosure();

  }

  @Test
  public void test() throws QueryParseException  {

    String[][] expected = {

        { "<owl:sameAs>", "<rdf:type>", "<owl:SymmetricProperty>" },
        { "<owl:equivalentClass>", "<owl:inverseOf>", "<owl:equivalentClass>" },
        { "<owl:SymmetricProperty>", "<rdfs:subClassOf>", "<rdf:Property>" },
        { "<xsd:gMonthDay>", "<rdf:type>", "<rdfs:Datatype>" },
        { "<xsd:double>", "<rdf:type>", "<rdfs:Datatype>" },
        { "<rdfs:subPropertyOf>", "<rdfs:subPropertyOf>", "<rdfs:subPropertyOf>" },
        { "<owl:differentFrom>", "<rdf:type>", "<owl:SymmetricProperty>" },
        { "<owl:FunctionalProperty>", "<rdfs:subClassOf>", "<rdf:Property>" },
        { "<owl:ObjectProperty>", "<rdfs:subClassOf>", "<rdf:Property>" },
        { "<rdf:nil>", "<rdf:type>", "<rdf:List>" },
        { "<owl:equivalentProperty>", "<owl:inverseOf>", "<owl:equivalentProperty>" },
        { "<owl:Thing>", "<rdfs:subClassOf>", "<owl:Thing>" },
        { "<rdfs:subClassOf>", "<rdf:type>", "<rdf:Property>" },
        { "<owl:equivalentProperty>", "<rdfs:subPropertyOf>", "<owl:equivalentProperty>" },
        { "<rdf:subject>", "<rdf:type>", "<rdf:Property>" },
        { "<xsd:long>", "<rdf:type>", "<rdfs:Datatype>" },
        { "<owl:equivalentProperty>", "<rdf:type>", "<rdf:Property>" },
        { "<xsd:monetary>", "<rdf:type>", "<rdfs:Datatype>" },
        { "<rdfs:subClassOf>", "<rdfs:subPropertyOf>", "<rdfs:subClassOf>" },
        { "<owl:disjointWith>", "<rdf:type>", "<owl:SymmetricProperty>" },
        { "<xsd:anyURI>", "<rdf:type>", "<rdfs:Datatype>" },
        { "<owl:equivalentProperty>", "<rdf:type>", "<owl:TransitiveProperty>" },
        { "<owl:equivalentProperty>", "<rdf:type>", "<owl:SymmetricProperty>" },
        { "<owl:equivalentClass>", "<rdf:type>", "<rdf:Property>" },
        { "<xsd:string>", "<rdf:type>", "<rdfs:Datatype>" },
        { "<owl:differentFrom>", "<rdf:type>", "<rdf:Property>" },
        { "<owl:equivalentClass>", "<rdfs:subPropertyOf>", "<rdfs:subClassOf>" },
        { "<owl:Thing>", "<rdf:type>", "<owl:Class>" },
        { "<owl:sameAs>", "<rdf:type>", "<rdf:Property>" },
        { "<rdf:object>", "<rdfs:subPropertyOf>", "<rdf:object>" },
        { "<rdf:subject>", "<rdfs:subPropertyOf>", "<rdf:subject>" },
        { "<owl:inverseOf>", "<rdfs:subPropertyOf>", "<owl:inverseOf>" },
        { "<rdf:type>", "<rdfs:subPropertyOf>", "<rdf:type>" },
        { "<owl:AnnotationProperty>", "<rdfs:subClassOf>", "<rdf:Property>" },
        { "<xsd:uDateTime>", "<rdf:type>", "<rdfs:Datatype>" },
        { "<xsd:float>", "<rdf:type>", "<rdfs:Datatype>" },
        { "<owl:Thing>", "<owl:disjointWith>", "<owl:Nothing>" },
        { "<xsd:gMonth>", "<rdf:type>", "<rdfs:Datatype>" },
        { "<xsd:dateTime>", "<rdf:type>", "<rdfs:Datatype>" },
        { "<owl:disjointWith>", "<rdf:type>", "<rdf:Property>" },
        { "<owl:differentFrom>", "<rdfs:subPropertyOf>", "<owl:differentFrom>" },
        { "<xsd:date>", "<rdf:type>", "<rdfs:Datatype>" },
        { "<owl:sameAs>", "<rdf:type>", "<owl:TransitiveProperty>" },
        { "<rdfs:subPropertyOf>", "<rdf:type>", "<owl:TransitiveProperty>" },
        { "<owl:disjointWith>", "<owl:inverseOf>", "<owl:disjointWith>" },
        { "<owl:equivalentClass>", "<rdf:type>", "<owl:TransitiveProperty>" },
        { "<owl:Nothing>", "<owl:disjointWith>", "<owl:Thing>" },
        { "<rdfs:subClassOf>", "<rdf:type>", "<owl:TransitiveProperty>" },
        { "<owl:inverseOf>", "<rdf:type>", "<rdf:Property>" },
        { "<rdf:first>", "<rdf:type>", "<rdf:Property>" },
        { "<rdf:predicate>", "<rdf:type>", "<rdf:Property>" },
        { "<owl:TransitiveProperty>", "<rdfs:subClassOf>", "<rdf:Property>" },
        { "<xsd:gDay>", "<rdf:type>", "<rdfs:Datatype>" },
        { "<owl:Nothing>", "<rdfs:subClassOf>", "<owl:Thing>" },
        { "<owl:InverseFunctionalProperty>", "<rdfs:subClassOf>", "<rdf:Property>" },
        { "<xsd:gYearMonth>", "<rdf:type>", "<rdfs:Datatype>" },
        { "<rdfs:subPropertyOf>", "<rdf:type>", "<rdf:Property>" },
        { "<owl:DatatypeProperty>", "<rdfs:subClassOf>", "<rdf:Property>" },
        { "<owl:differentFrom>", "<owl:inverseOf>", "<owl:differentFrom>" },
        { "<xsd:duration>", "<rdf:type>", "<rdfs:Datatype>" },
        { "<owl:equivalentProperty>", "<rdfs:subPropertyOf>", "<rdfs:subPropertyOf>" },
        { "<xsd:boolean>", "<rdf:type>", "<rdfs:Datatype>" },
        { "<owl:disjointWith>", "<rdfs:subPropertyOf>", "<owl:disjointWith>" },
        { "<owl:equivalentClass>", "<rdf:type>", "<owl:SymmetricProperty>" },
        { "<rdf:type>", "<rdf:type>", "<rdf:Property>" },
        { "<owl:Nothing>", "<rdf:type>", "<owl:Class>" },
        { "<xsd:int>", "<rdf:type>", "<rdfs:Datatype>" },
        { "<owl:sameAs>", "<owl:inverseOf>", "<owl:sameAs>" },
        { "<rdf:object>", "<rdf:type>", "<rdf:Property>" },
        { "<owl:Nothing>", "<rdfs:subClassOf>", "<owl:Nothing>" },
        { "<rdf:first>", "<rdfs:subPropertyOf>", "<rdf:first>" },
        { "<owl:inverseOf>", "<rdf:type>", "<owl:SymmetricProperty>" },
        { "<xsd:gYear>", "<rdf:type>", "<rdfs:Datatype>" },
        { "<owl:sameAs>", "<rdfs:subPropertyOf>", "<owl:sameAs>" },
        { "<rdf:rest>", "<rdf:type>", "<rdf:Property>" },
        { "<rdf:rest>", "<rdfs:subPropertyOf>", "<rdf:rest>" },
        { "<owl:equivalentClass>", "<rdfs:subPropertyOf>", "<owl:equivalentClass>" },
        { "<rdf:predicate>", "<rdfs:subPropertyOf>", "<rdf:predicate>" },
        { "<owl:inverseOf>", "<owl:inverseOf>", "<owl:inverseOf>" },

    };
    Query q = new Query(fc._tupleStore);
    BindingTable bt = q.query("SELECT ?s ?p ?o WHERE ?s ?p ?o");
    checkResult(expected, bt, bt.getVars());
  }

  @After
  public  void finish() {
    fc.shutdownNoExit();
  }

}

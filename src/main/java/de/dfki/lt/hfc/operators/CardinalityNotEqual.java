package de.dfki.lt.hfc.operators;

import de.dfki.lt.hfc.RelationalOperator;
import de.dfki.lt.hfc.BindingTable;
import de.dfki.lt.hfc.types.XsdInt;
import java.util.*;

/**
 * CardinalityNotEqual returns only those subject-predicate-object triples which consist of a
 * predicate that violates its cardinality description (if there is any; see example below);
 * note that the open-world assumption together with the (non-)existence of owl:sameAs and
 * owl:differentFrom axioms MUST be taken into consideration when using the below implementation
 * of this predicate
 *
 * EXAMPLE
 * + assumes a closed-world (total knowledge)
 * + only datatype properties are taken into account
 * + object properties must also employ sameAs & differentFrom
 *
 *   ONTOLOGY (RBox)
 *     q rdf:type owl:DatatypeProperty
 *     _:b1 rdf:type owl:Restriction
 *     _:b1 owl:onProperty q
 *     _:b1 owl:cardinality "1"^^xsd:int
 *     r rdf:type owl:DatatypeProperty
 *     _:b2 rdf:type owl:Restriction
 *     _:b2 owl:onProperty r
 *     _:b2 owl:cardinality "1"^^xsd:int
 *   ONTOLOGY (ABox)
 *     foo q 10
 *     foo q 20
 *     foo r 30
 *     bar q 40
 *     bar r 50
 *   ENTAILMENT RULE
 *     ?p rdf:type owl:DatatypeProperty
 *     ?r owl:onProperty ?p
 *     ?r owl:cardinality ?c
 *     ?s ?p ?o
 *     ->
 *     ?s rdf:type owl:Nothing
 *     @test
 *     CardinalityNotEqual ??(s p o) ??(p c)
 *   BINDING TABLE B (after LHS matching)
 *      ?s  ?p  ?o   ?r  ?c 
 *     foo   q   1  _:b1  1  \ cardinality restriction
 *     foo   q   2  _:b1  1  / violated for q on foo
 *     foo   r   1  _:b2  1  - but OK for r on foo
 *     bar   q   1  _:b1  1
 *     bar   r   1  _:b2  1
 *   MODIFIED ?s-?p-?o RETURN TABLE T (= args[0]), no need to return table for ??(p c) as it does not change
 *     foo   q   1
 *     foo   q   2
 *   JOINED RESULT: B |X| T
 *     foo   q   1  _:b1  1
 *     foo   q   2  _:b1  1
 *   RHS INSTANTIATION TABLE AFTER ?s PROJECTION
 *     foo
 *   LEADING TO
 *     foo rdf:type owl:Nothing
 *
 * @return args[0], the modified input binding table, corresponding to subject predicate object
 *
 * @see RelationalOperator
 *
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Wed Mar 20 14:36:50 CET 2013
 */
public final class CardinalityNotEqual extends RelationalOperator {
	
	/**
	 * this implementation of apply() assumes that args consists of exactly TWO (2)
	 * binding tables, the first one of length 3 (subject predicate object), and the
	 * second one of length 2 (predicate cardinality);
	 * I will destructively modify the first binding table, and will only return that
	 * table (args[0]), since the second table is only used for lookup
	 */
	public BindingTable[] apply(BindingTable[] args) {
		// obtain the right positions from mapping internalized in the binding table arguments;
		// obtain the first relational variable and map it to the list of functional variables;
		// first argument is something like this: ??(s p o) -- variable names might change
		int[] positions = obtainPositions(args[0], 0);
		int pos10 = positions[0];
		int pos11 = positions[1];
		// build lookup structure (1): subject-predicate-numberOfObjects
		Map<Integer, Map<Integer, Integer>> index = new HashMap<Integer, Map<Integer, Integer>>();
		Map<Integer, Integer> posize;
		Integer card;
		for (int[] spo : args[0].table) {
			if (index.containsKey(spo[pos10])) {
				posize = index.get(spo[pos10]);
				if (posize.containsKey(spo[pos11])) {
					card = posize.get(spo[pos11]);
					card = new Integer(card + 1);  // more than one <s, p, *> instantiation
					posize.put(spo[pos11], card);
				}
				else {
					card = new Integer(1);
					posize.put(spo[pos11], card);
				}
			}
			else {
				posize = new HashMap<Integer, Integer>();
				card = new Integer(1);  // first occurrence
				posize.put(spo[pos11], card);
				index.put(spo[pos10], posize);
			}
		}
		// build lookup structure (2): predicate-cardinality
		positions = obtainPositions(args[1], 1);
		int pos20 = positions[0];
		int pos21 = positions[1];
		Map<Integer, Integer> cardmap = new HashMap<Integer, Integer>();
		for (int[] pcard : args[1].table)
			// note that we must transform the XSD ints from the ontology to Java ints for comparison below
			cardmap.put(pcard[pos20], ((XsdInt)getObject(pcard[pos21])).value);
		// now iterate over args[0] and KEEP only those triple <s, p, o> where
		//   index.get(s).get(p) != cardmap.get(p)
		Iterator<int[]> tableit = args[0].table.iterator();
		int[] spo;
		while (tableit.hasNext()) {
			spo = tableit.next();
			// note: comparing the Integer objects via == leads of course to wrong results: use equals()
			if (index.get(spo[pos10]).get(spo[pos11]).equals(cardmap.get(spo[pos11])))
				tableit.remove();
		}
		// only return the _modified_ table (the second one will not change the original LHS binding table)
		return new BindingTable[]{args[0]};
	}
	
}

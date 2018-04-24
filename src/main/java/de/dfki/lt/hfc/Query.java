package de.dfki.lt.hfc;

import java.util.*;

import de.dfki.lt.hfc.qrelations.QRelation;
import de.dfki.lt.hfc.qrelations.QRelationFactory;
import org.apache.commons.lang3.ArrayUtils;


// TO DO --- IMPLEMENTATION NOTE
//
// in order to implement SPARQL's
//   ORDER BY
// option in HFC's QDL, the standard BindingTable implementation will not work, as the
// BindingTable.table implementation is of type Set<int[]> and the output of a query,
// such as
//   SELECT ?da
//   WHERE ?da <rdf:type> <dafn:DialogueAct> &
//   ?da <dafn:sender> <hst:i_myself> &
//   ?da <dafn:time> ?time
//   ORDER BY DESC(?time)
// no longer has access to ?time in the SELECT;
// solution: make sure that BindingTable.table is a SortedSet before TROVE's projection
//           takes place -- not sure whether this will work



/**
 *
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Tue Jan  5 11:45:27 CET 2016
 */
public class Query {

	/**
	 * do we want proxy expansion in the resulting binding table if equivalence reduction
	 * has been turned on ?
	 * this field is set every time query() is called, depending on whether either SELECT
	 * (expandProxy = false) or SELECTALL (expandProxy = true) is used
	 */
	private boolean expandProxy = false;

  /**
   * a constant that controls whether a warning is printed in case "unexpected" things
   * happen; similar variables exists in class TupleStore, RuleStore, and ForwardChainer
   */
  private boolean verbose = false;

	/**
	 * in order to query, we need a tuple store
	 */
	protected TupleStore tupleStore;

	/**
   * simple flag indicating whether an indexstore is used.
   */
  private boolean activeIndex;

	/**
	 * the unary constructor internalizes the tuple store
	 */
	public Query(TupleStore tupleStore) {
		this.tupleStore = tupleStore;
		QRelationFactory.initFactory(tupleStore);
	}

	/**
	 * the unary constructor internalizes the tuple store that `sits'
	 * inside the forward chainer
	 */
	public Query(ForwardChainer fc) {
		this.tupleStore = fc.tupleStore;
		QRelationFactory.initFactory(fc.tupleStore);
	}

	/**
	 * reads in a QDL query and returns a binding table, encoding the result of the query;
	 * at the moment (= v2.3), QDL is described by the following EBNF:
	 *
	 *   <query>     ::= <select> <where> [<filter>] [<aggregate>] | ASK <groundtuple>
	 *   <select>    ::= {"SELECT" | "SELECTALL"} ["DISTINCT"] {"*" | <var>^+}
	 *   <var>       ::= "?"{a-zA-Z0-9}^+ | "?_"
	 *   <nwchar>    ::= any NON-whitespace character
	 *   <where>     ::= "WHERE" <tuple> {"&" <tuple>}^*
	 *   <tuple>     ::= <literal>^+
	 *   <gtuple>    ::= <constant>^+
	 *   <literal>   ::= <var> | <constant>
	 *   <constant>  ::= <uri> | <atom>
	 *   <uri>       ::= "<" <nwchar>^+ ">"
	 *   <atom>      ::= "\""  <char>^* "\"" [ "@" <langtag> | "^^" <xsdtype> ]
	 *   <char>      ::= any character, incl. whitespaces, numbers, even '\"'
	 *   <langtag>   ::= "de" | "en" | ...
	 *   <xsdtype>   ::= "<xsd:int>" | "<xsd:long>" | "<xsd:float>" | "<xsd:double>" | "<xsd:dateTime>" |
	 *                   "<xsd:string>" | "<xsd:boolean>" | "<xsd:date>" | "<xsd:gYear>" | "<xsd:gMonthDay>" |
   *                   "<xsd:gDay>" | "<xsd:gMonth>" | "<xsd:gYearMonth>" | "<xsd:duration>" | "<xsd:anyURI>" | ...
	 *   <filter>    ::= "FILTER" <constr> {"&" <constr>}^*
	 *   <constr>    ::= <ineq> | <predcall>
	 *   <ineq>      ::= <var> "!=" <literal>
	 *   <predcall>  ::= <predicate> <literal>^*
	 *   <predicate> ::= <nwchar>^+
	 *   <aggregate> ::= "AGGREGATE" <funcall> {"&" <funcall>}^*
	 *   <funcall>   ::= <var>^+ "=" <function> <literal>^*
	 *   <function>  ::= <nwchar>^+
	 *
	 * NOTE: the reserved keywords ASK, SELECT, SELECTALL, DISTINCT, WHERE, FILTER, and AGGREGATE need
	 *       _not_ be written uppercase
   *
	 * NOTE: it is required that neither filter predicates nor aggregate functions have the same name
	 *       as the above reserved keywords
   *
   * NOTE: _don't-care_ variables should be marked _explicitly_ by using exactly the identifier "?_";
   *       this is especially important when using "*" in a SELECT;
   *       example:
   *         SELECT DISTINCT * WHERE ?s <rdf:type> ?_
   *         SELECT * WHERE ?s <rdf:type> ?o ?_ ?_
   *       when restricting the object position without projecting it, we explicitly write down theselcted vars:
   *         SELECT ?s WHERE ?s <rdf:type> ?o ?_ ?_ FILTER ?o != <foo-class>
	 *
	 * @return a (possibly empty) binding table, otherwise
	 * @throws a query parse exception iff query is syntactically incorrect
	 */
	public BindingTable query(String query) throws QueryParseException {
		// do we want to proxy expansion
		this.expandProxy = false;
		this.activeIndex = this.tupleStore.indexStore != null;
		// remove unnecessary whitespaces
		query = query.trim();
		if (query.contains(" ?rel")) {
      throw new QueryParseException("The variable name ?rel is reserved!");
    }
    // use set to get rid of duplicates
		HashSet<String> projectedVars = new LinkedHashSet<String>();
		// parse SELECT section
		boolean makeDistinct = parseSelect(new StringTokenizer(query), projectedVars);
		// parse WHERE section
		ArrayList<ArrayList<String>> whereClauses = new ArrayList<ArrayList<String>>();
		// look for " WHERE": "WHERE" won't work due to a selected variable "?where"
		String where = query.substring(query.toUpperCase().indexOf(" WHERE"));
		where = where.trim();  // get rid of leading space
		HashSet<String> foundVars = new HashSet<String>();
		// Changed the regex to cover the intervals - CW
    parseWhere(new StringTokenizer(where, " [](),?<>_\"\\", true), whereClauses, foundVars);
		// some further checks on the projected vars, independent of optional filters
		if (projectedVars.contains("*")) {
			// a "*" should not come up with further vars
			if (projectedVars.size() > 1)
				throw new QueryParseException("\"*\" and further variables can not be mixed");
		}
		else {
			// projected vars should only consist of found vars
			HashSet<String> pv = new HashSet<String>(projectedVars);
			pv.removeAll(foundVars);
			if (! pv.isEmpty())
				throw new QueryParseException("SELECT contains variables not found in WHERE: " + pv);
		}
		// parse optional FILTER section
		ArrayList<ArrayList<String>> filterClauses = new ArrayList<ArrayList<String>>();
		int hasFilter = query.toUpperCase().indexOf(" FILTER");
		if (hasFilter != -1) {
			String filter = query.substring(hasFilter);
			filter = filter.trim();  // get rid of leading space
			parseFilter(new StringTokenizer(filter, " ?<>\"\\", true), filterClauses, foundVars);
		}
		// parse optional AGGREGATE section
		ArrayList<ArrayList<String>> aggregateClauses = null;
		int hasAggregate = query.toUpperCase().indexOf(" AGGREGATE");
		if (hasAggregate != -1) {
			String aggregate = query.substring(hasAggregate);
			aggregate = aggregate.trim();  // get rid of leading space
			aggregateClauses = new ArrayList<ArrayList<String>>();
			parseAggregate(new StringTokenizer(aggregate, " ?<>\"\\", true), aggregateClauses);
		}
		// so far, query syntactically correct; namespaces are also correctly treated, using the
		// cannonical form (either short or long), as specified in the namespace object;
		// now map surface WHERE clauses to internal representation and record id and number of
		// occurrences for each variable
		HashMap<String, Integer> nameToId = new HashMap<String, Integer>();
		HashMap<Integer, String> idToName = new HashMap<Integer, String>();
		HashSet<IndexLookup> lkps = new HashSet<>();
		ArrayList<int[]> patterns = new ArrayList<int[]>();
		// we might return the null value in case a constant in the WHERE clause is not known to the
    // tuple store; given null, we can distinguish this case from an empty binding table, arising
    // from a query with known constants;
    //if (internalizeWhere(whereClauses, patterns, nameToId, idToName) == null)
    //	return null;
    // note that we _no_ longer make this distinction for easier external API use !
		// TODO: THIS CHANGE RESULTED IN A NULLPOINTEREXCEPTION AT MAKEDISTINCTANDPROJECT LATER
		// IN CASE THIS CALL RETURNS NULL
		if (internalizeWhere(whereClauses, patterns, lkps, nameToId, idToName,
        filterClauses, foundVars).isEmpty() && lkps.isEmpty())
		  return new BindingTable();
		if (!filterClauses.isEmpty()) {
      hasFilter = 1;
    }
    // now that mappings have been established, internalize FILTER conditions;
		// note: all filter vars are definitely contained in found vars at this point
		ArrayList<Integer> varvarIneqs = null;
		ArrayList<Integer> varconstIneqs = null;
		ArrayList<Predicate> predicates = null;
		if (hasFilter != -1) {
			varvarIneqs = new ArrayList<Integer>();
			varconstIneqs = new ArrayList<Integer>();
			predicates = new ArrayList<Predicate>();
			internalizeFilter(filterClauses, varvarIneqs, varconstIneqs, predicates, nameToId);
		}
		// lastly, internalize AGGREGATE information; note: aggregate variables will only refer
		// to selected variables
		ArrayList<Aggregate> aggregates = null;
		if (hasAggregate != -1) {
			aggregates = new ArrayList<Aggregate>();
			internalizeAggregate(aggregateClauses, aggregates, nameToId, idToName, projectedVars, foundVars);
		}
		// process the clauses to obtain the corresponding parameters to queryIndex()
		ArrayList<Table> tables = new ArrayList<Table>();
		prepareQueryIndex(patterns, lkps, tables, nameToId.get("?_"));
		// call queryIndex for each pattern/table combination in order to construct binding tables
		// and perform successive joins on the return values
		BindingTable bt = queryAndJoin(patterns, tables,lkps);
		// bt now refers to the joined tables, so potential filter conditions can be applied
		if (hasFilter != -1) {
			Calc.restrict(bt, varvarIneqs, varconstIneqs);
			Calc.restrict(bt, predicates);
		}
		// equip bt with the idToName mapping for later output;
		// note: idToName even contains LHS aggregate variables
		bt.nameToExternalName = idToName;
		bt.selectVars = new String[projectedVars.size()];
		projectedVars.toArray(bt.selectVars);
		// finally consider projected vars and DISTINCT keyword
		makeDistinctAndProject(bt, projectedVars, foundVars, nameToId, makeDistinct);
		// bt is now destructively changed; finally consider potential aggregation;
		// note that there might be several aggregate clauses, working on the same/overlapping
		// projection window; if so, do not change the table any longer and combine the result
		// using table join
		if (hasAggregate != -1)
			bt = aggregateAndJoin(bt, aggregates);
		bt.tupleStore = this.tupleStore;
		// in case a SELECTALL query has been posted, expand the binding table
		if (this.expandProxy) {
			bt.expandBindingTable();
		}
    return bt;
	}



	/**
 * parses the SELECT section of a QDL query
 */
private boolean parseSelect(StringTokenizer st, HashSet<String> projectedVars)
		throws QueryParseException {
	boolean distinct = false;
	if (!st.hasMoreTokens()) {
		throw new QueryParseException("empty query");
	}
	String token = st.nextToken();
	if (!(token.toUpperCase().equals("SELECT") || token.toUpperCase().equals("SELECTALL"))) {
		throw new QueryParseException("SELECT/SELECTALL missing");
	}
	// if SELECTALL, record that binding table needs to be expanded afterwards
	if (token.toUpperCase().equals("SELECTALL")) {
		this.expandProxy = true;
	}
	if (!st.hasMoreTokens()) {
		throw new QueryParseException("query too short");
	}
	token = st.nextToken();
	if (token.toUpperCase().equals("WHERE")){
		throw new QueryParseException("projected Vars missing");
	}
	if (token.toUpperCase().equals("DISTINCT")) {
		distinct = true;
		if (!st.hasMoreTokens()) {
			throw new QueryParseException("query too short");
		}
		token = st.nextToken();
	}
	if (token.equals("*")) {
		projectedVars.add(token);
		if (!st.hasMoreTokens()) {
			throw new QueryParseException("query too short");
		}
		token = st.nextToken();
		if (token.toUpperCase().equals("WHERE")) {
			return distinct;
		} else {
			throw new QueryParseException("projected Vars missing");
		}
	}
	while (!token.toUpperCase().equals("WHERE")) {
		if (!RuleStore.isVariable(token)) {
			throw new QueryParseException("token not a variable");
		} else {
			projectedVars.add(token);
		}
		if (!st.hasMoreTokens()) {
			throw new QueryParseException("WHERE missing");
		}
		token = st.nextToken();
	}
	return distinct;
}

/**
 * parses the WHERE section of a QDL query
 */
private void parseWhere(StringTokenizer st,
		ArrayList<ArrayList<String>> whereClauses,
		HashSet<String> foundVars) throws QueryParseException {
	// first token is guaranteed to be "WHERE"--get rid of it
	st.nextToken();
	String token;
	ArrayList<String> tuple = new ArrayList<String>();
	while (st.hasMoreTokens()) {
		token = st.nextToken();
		if (token.equals("&")) {
			whereClauses.add(tuple);
			tuple = new ArrayList<String>();
		} else if (token.equals("?")) {
			foundVars.add(TupleStore.parseVariable(st, tuple));
		} else if (token.equals("<")) {
			this.tupleStore.parseURI(st, tuple);
		} else if (token.equals("\"")) {
			this.tupleStore.parseAtom(st, tuple);
		} else if (token.equals("_")) {
			this.tupleStore.parseBlankNode(st, tuple);
		} else if (token.equals("[")) { // check for exclusive interval
			QRelationFactory.parseInterval(true, st, tuple);
		} else if (token.equals("(")) {// check for inclusive interval
			QRelationFactory.parseInterval(false, st, tuple);
		} else if (QRelationFactory.isAllenRelation(token)) {
			QRelationFactory.parseAllenRelation(token, st, tuple);
		} else if (QRelationFactory.isRCC8Relation(token)) {
			QRelationFactory.parseRCC8Relation(token, st, tuple);
		} else if (token.equals(" "))  // keep on parsing ...
		{
			continue;
		}
		// break if (optional) FILTER or AGGREGATE is found
		else if (token.toUpperCase().equals("FILTER") || token.toUpperCase().equals("AGGREGATE")) {
			break;
		}
		// something has gone wrong when reading the tuple
		else {
			throw new QueryParseException("  incorrect WHERE clause " + token);
		}
	}
	// since last clause needs not be finished by the '.' character, we
	// can detect this by checking whether tuple is not empty
	if (!tuple.isEmpty()) {
		whereClauses.add(tuple);
	}
}

	/**
	 * parses the optional FILTER section of a QDL query
	 */
	private void parseFilter(StringTokenizer st,
													 ArrayList<ArrayList<String>> filterClauses,
													 HashSet<String> foundVars) throws QueryParseException {
		// first token is guaranteed to be "FILTER"--get rid of it
		st.nextToken();
		String token, var;
		ArrayList<String> constraint = new ArrayList<String>();
		while (st.hasMoreTokens()) {
			token = st.nextToken();
			if (token.equals("&")) {
				filterClauses.add(constraint);
				constraint = new ArrayList<String>();
			}
			else if (token.equals("?")) {
				var = TupleStore.parseVariable(st, constraint);
				// check whether filter vars are definitely used in where vars
				if (! foundVars.contains(var))
					throw new QueryParseException("  filter variable not contained in WHERE: " + var);
			}
			else if (token.equals("<"))
				this.tupleStore.parseURI(st, constraint);
			else if (token.equals("\""))
				this.tupleStore.parseAtom(st, constraint);
			else if (token.equals(" ") || token.equals("!="))  // keep on parsing ...
				continue;
			// break if (optional) AGGREGATE is found
			else if (token.toUpperCase().equals("AGGREGATE"))
				break;
			else { // a predicate symbol
				// check whether this is a legal Java class name
				try {
					Class.forName("de.dfki.lt.hfc.operators." + token);
				} catch (ClassNotFoundException var8) {
					throw new QueryParseException(token + " is not a valid constraint.");
				}
				constraint.add(token);
			}
		}
		// since last clause is usually not followed by the '&' character,
		// we need to check this by testing whether constraint is not empty
		if (! constraint.isEmpty())
			filterClauses.add(constraint);
	}

	/**
	 * parses the optional AGGREGATE section of a QDL query
	 */
	private void parseAggregate(StringTokenizer st,
															ArrayList<ArrayList<String>> aggregateClauses) throws QueryParseException {
		// first token is guaranteed to be "AGGREGATE"--get rid of it
		st.nextToken();
		String token, var;
		ArrayList<String> aggregate = new ArrayList<String>();
		while (st.hasMoreTokens()) {
			token = st.nextToken();
			if (token.equals("?"))
				var = TupleStore.parseVariable(st, aggregate);
			else if (token.equals("<"))
				this.tupleStore.parseURI(st, aggregate);
			else if (token.equals("\""))
				this.tupleStore.parseAtom(st, aggregate);
			else if (token.equals(" "))  // keep on parsing ...
				continue;
			else if (token.equals("="))  // the separator
				aggregate.add(token);
			else if (token.equals("&")) {  // a new aggregate is going to start
				aggregateClauses.add(aggregate);
				aggregate = new ArrayList<String>();
			}
			else { // an aggregate symbol
				//I do not check whether this is a legal Java class name
				try {
					Class.forName("de.dfki.lt.hfc.aggregates." + token);
				} catch (ClassNotFoundException var7) {
					throw new QueryParseException(token + " is not a valid aggregate operator.");
				}
				aggregate.add(token);
			}
		}
		if (! aggregate.isEmpty())
			aggregateClauses.add(aggregate);
	}

	/**
   * maps surface WHERE form to internal representation. Further handles the internalization of
   * relations and intervals, In case there are relations or intervals, two cases have to be
   * consideres 1) There is an active IndexStore and the relation matches the indices. Then a
   * corresponding {@link IndexLookup} is created and added to the given
   * lkps Set. 2) If (1) does not hold, the relations are rewritten into new filter clauses. These
   * clauses are added to the given {@link ArrayList} of filterclauses.
   *
   * @param whereClauses the clauses to be internalized. Each {@link ArrayList} represents one
   * clause.
   * @param patterns the internalized clauses. This Lst will be populated in the course of this
   * method.
   * @param lkps a set of {@link IndexLookup}s to be populated in the
   * course of this method.
   * @param idToName a mapping from internal id to the string representation.
   * @param filterClauses a {@link ArrayList} of filterclauses. This might be extended in the course
   * of the method, in case there are relations which must be rewritten.
   * @param foundVars
   * @return null iff constants in the WHERE clause are NOT known to the tuple store
   */
  private List<int[]> internalizeWhere(ArrayList<ArrayList<String>> whereClauses,
                                       ArrayList<int[]> patterns, HashSet<IndexLookup> lkps,
                                       HashMap<String, Integer> nameToId, HashMap<Integer, String> idToName,
                                       ArrayList<ArrayList<String>> filterClauses, HashSet<String> foundVars)
      throws QueryParseException {
    List<Integer> clause;
    HashMap<Integer, QRelation> idToRelation = new HashMap<>();
    int varcount = 0;
    int id1, id2;
    ArrayList<String> wc;
    String elem;
    for (int j = 0; j < whereClauses.size(); j++) {
      wc = whereClauses.get(j);
      clause = new ArrayList<>();
      for (int i = 0; i < wc.size(); i++) {
        elem = wc.get(i);
        if (RuleStore.isVariable(elem)) {
          // a variable: check if it is old or new
          if (nameToId.containsKey(elem)) {
            clause.add(nameToId.get(elem));
          } else {
            nameToId.put(elem, --varcount);
            idToName.put(varcount, elem);
            clause.add(varcount);
          }
        } else {
          // a constant: check whether elem is known to the tuple store;
          // also handle uri-to-proxy mapping here
          if (this.tupleStore.isConstant(elem)) {
            clause.add(this.tupleStore.objectToId.get(elem));
            if (this.tupleStore.equivalenceClassReduction) {
              clause
                  .set(clause.size() - 1,
                      this.tupleStore.getProxy(clause.get(clause.size() - 1)));
            }
          } else if (QRelationFactory.isRelation(elem)) {
            //collapse relation
            // We have to use the actual size of the ArrayList (clause) here, as the index i used while
            // iteration on wc might get somewhat of compared to the clause due to the collapsing of relation statements.
            id1 = clause.get(clause.size() - 2);
            id2 = clause.get(clause.size() - 1);
            QRelation r;
            if (null == (r = QRelationFactory.getRelation(elem, id1, id2, clause.size()))) {
              return Collections.EMPTY_LIST;
            } else {
              varcount = r.prepareMappings(nameToId, idToRelation, idToName, varcount, clause, foundVars);
              //numberOfFoundRelations++;
            }
          } else {
            // not known: return null to indicate this special empty table;
            // other options: (i) throw a special exception  or (ii) an empty binding table
            // we _now_ opt for the empty binding table in method query()
            if (tupleStore.isAtom(wc.get(i))){
              tupleStore.putObject(elem);
              clause.add(this.tupleStore.objectToId.get(elem));
            } else {
              return Collections.EMPTY_LIST;
            }
          }
        }
      }
      if (clause.size() > tupleStore.maxNoOfArgs) {
        throw new QueryParseException(
            "Invalid Where clause. To many arguments: " + clause.size() + " but only "
                + tupleStore.maxNoOfArgs + " are supported.");
      }
      Set<QRelation> relationsToBeRewritten = new HashSet<>();
      boolean createPattern = false;
      // Check whether an active indexStore exists
      if (activeIndex) {
        // Check whether the indexing conditions are matched by the clause
        createPattern = tupleStore.indexStore.prepareLookup(clause, idToRelation, lkps,relationsToBeRewritten);
      }
      if (!activeIndex || !relationsToBeRewritten.isEmpty() || createPattern) {
        patterns.add(ArrayUtils.toPrimitive(clause.toArray(new Integer[clause.size()])));
      }
    }
    for (QRelation relation : new HashSet<>(idToRelation.values())) {
      filterClauses.addAll(relation.rewrite(idToName));
    }
    // return internalized where clause
    return patterns;
  }

	/**
	 * a predicate symbol is neither a URI, a blank node, a variable, nor an XSD atom
	 */
	public static boolean isPredicate(String literal) {
		return !RuleStore.isVariable(literal) &&
		!TupleStore.isAtom(literal) &&
		!TupleStore.isUri(literal) &&
		!TupleStore.isBlankNode(literal) &&
		!QRelationFactory.isRelation(literal);
	}

	/**
	 * maps surface FILTER form to internal representation, stored in varvarIneqs and
	 * varconstIneqs (as used by Calc.restrict) and predicates;
	 *
	 * NOTE: code does NOT work for NoValue in queries (FILTER), but DOES work for @test in rules
	 * SOLUTION: replace QueryParseException by a warning
	 * @see RuleStore.evaluatePredicates()
	 */
	private void internalizeFilter(ArrayList<ArrayList<String>> filterClauses,
																 ArrayList<Integer> varvarIneqs,
																 ArrayList<Integer> varconstIneqs,
																 ArrayList<Predicate> predicates,
																 HashMap<String, Integer> nameToId) throws QueryParseException {
		String first, next;
		Predicate predicate;
		ArrayList<Integer> args;
		int id;
		for (ArrayList<String> filter : filterClauses) {
			first = filter.get(0);
			// a predicate
			if (Query.isPredicate(first)) {
				// at least we have a predicate name (not necessarily further arguments)
				// construct the fully-qualified name, using the directory path where the operators live
				first = OperatorRegistry.OPERATOR_PATH + first;
				// check whether there is such a functional operator/Java class with this name
				Operator op = this.tupleStore.operatorRegistry.checkAndRegister(first);
				// continue with the args
				args = new ArrayList<Integer>();
				for (int i = 1; i < filter.size(); i++) {
					next = filter.get(i);  // reuse next
					if (RuleStore.isVariable(next))
						args.add(nameToId.get(next));
					else {
						if (this.tupleStore.isConstant(next)) {
							id = this.tupleStore.objectToId.get(next);
							// uri-to-proxy mapping, if necessary
							if (this.tupleStore.equivalenceClassReduction)
								id = this.tupleStore.getProxy(id);
							args.add(id);
						}
            else {
              // constant _not_ known to tuple store: we no longer will throw an exception:
              //throw new QueryParseException("  unknown constant in predicate: " + next);
              if (verbose)
                System.out.println("  unknown constant in FILTER predicate: " + next + "\n");
              this.tupleStore.putObject(next);
              id = this.tupleStore.objectToId.get(next);
              args.add(id);
            }
					}
				}
				predicate = new Predicate(first, op, args);
				predicates.add(predicate);
			}
			// an in-eq constraint; make distinction here: either var-var or var-const
			else {
				// no error-checking here
				next = filter.get(1);
				if (RuleStore.isVariable(next)) {
					varvarIneqs.add(nameToId.get(first));
					varvarIneqs.add(nameToId.get(next));
				}
				else {
					varconstIneqs.add(nameToId.get(first));
					// next might not be known to the tuple store
					if (this.tupleStore.isConstant(next)) {
						id = this.tupleStore.objectToId.get(next);
						// uri-to-proxy mapping, if necessary
						if (this.tupleStore.equivalenceClassReduction)
							id = this.tupleStore.getProxy(id);
						varconstIneqs.add(id);
					}
          else {
            // constant _not_ known to tuple store: we no longer will throw an exception:
            //throw new QueryParseException("  unknown constant in in-eq constraint: " + next);
            if (verbose)
              System.out.println("  unknown constant in FILTER in-eq constraint: " + next + "\n");
            this.tupleStore.putObject(next);
            id = this.tupleStore.objectToId.get(next);
            varconstIneqs.add(id);
          }
				}
			}
		}
	}

	/**
	 * maps surface AGGREGATE form to internal representation, stored in aggregates;
	 * aggregate list is of the following form:
	 *   <var1> ... <varN> = <aggregate> <token1> ... <tokenM>,
	 * where
	 *   N >= 1 (at least _one_ var) and M >= 0 (potentially _no_ args)
	 */
	private void internalizeAggregate(ArrayList<ArrayList<String>> aggregateClauses,
																		ArrayList<Aggregate> aggregates,
																		HashMap<String, Integer> nameToId,
																		HashMap<Integer, String> idToName,
																		HashSet<String> projectedVars,
																		HashSet<String> foundVars) throws QueryParseException {
		String name, elem;
		String[] vars;
		int[] args;
		int eqpos;
		int id;
		for (ArrayList<String> clause : aggregateClauses) {
			eqpos = -1;
			// look for the "=" sign
			for (int i = 0; i < clause.size(); i++) {
				if (clause.get(i).equals("=")) {
					eqpos = i;
					break;
				}
			}
			// no equal sign ?
			if (eqpos == -1)
				throw new QueryParseException("  equal sign missing in aggregate: " + clause);
			// read LHS vars and check whether LHS vars are brand new
			vars = new String[eqpos];
			for (int i = 0; i < eqpos; i++) {
				elem = clause.get(i);
				// check that LHS aggregate variables are brand new (= _not_ contained in foundVars) and
				// assign new ids (= negative ints); note: LHS aggregate vars might appear more than once
				if (foundVars.contains(elem))
					throw new QueryParseException("  aggregate LHS variable not brand new: " + elem);
				// do NOT translate LHS var to a negative int -- not necessary for the aggregate
				vars[i] = elem;
			}
			// read aggregate name
			name = clause.get(eqpos + 1);
			// read remaining args (vars, URIs, XSD atoms) and store internal id
			args = new int[clause.size() - eqpos - 2];
			for (int i = eqpos + 2; i < clause.size(); i++) {
				elem = clause.get(i);
				// a variable
				if (RuleStore.isVariable(elem)) {
					// check whether projectedVars contains "*"; if so, aggregate vars must be contained
					// in foundVars; otherwise, aggregate vars must be contained in projectedVars
					if (projectedVars.contains("*")) {
						if (! foundVars.contains(elem))
							throw new QueryParseException("  aggregate RHS variable not contained in SELECT: " + elem);
					}
					else {
						if (! projectedVars.contains(elem))
							throw new QueryParseException("  aggregate RHS variable not contained in SELECT: " + elem);
					}
					args[i - eqpos - 2] = nameToId.get(elem);
				}
				// a constant
				else {
					if (this.tupleStore.isConstant(elem)) {
						id = this.tupleStore.objectToId.get(elem);
						// uri-to-proxy mapping, if necessary
						if (this.tupleStore.equivalenceClassReduction)
							id = this.tupleStore.getProxy(id);
						args[i - eqpos - 2] = id;
					}
          else {
            // constant _not_ known to tuple store: we no longer will throw an exception:
            //throw new QueryParseException("  unknown constant in aggregate: " + elem);
            if (verbose)
              System.out.println("  unknown constant in AGGREGATE function: " + elem + "\n");
            this.tupleStore.putObject(elem);
            id = this.tupleStore.objectToId.get(elem);
            args[i - eqpos - 2] = id;
          }
				}
			}
			aggregates.add(new Aggregate(name, vars, args));
		}
	}

	/**
   * preparation phase, before querying the index for each clause; determines the proper
   * indexToVariable, don't care indexToVariable, relevant positions, equal positions, and the
   * name-to-position mapping for patterns, as well as {@link IndexLookup}s
   */
  private void prepareQueryIndex(ArrayList<int[]> patterns, HashSet<IndexLookup> lkps,
      ArrayList<Table> tables,
      Integer dcid) {
    for (int i = 0; i < patterns.size(); i++) {
      tables.add(getTable(patterns.get(i), dcid));
    }
    for (IndexLookup lkp : lkps) {
      lkp.table = getTable(lkp.clause, dcid);
    }
  }

  /**
   * determines the proper indexToVariable, don't care indexToVariable, relevant positions, equal
   * positions, and the name-to-position mapping for patterns, as well as {@link
   * IndexLookup}s
   *
   * @param clause the clause to be processed
   */
  private Table getTable(int[] clause, Integer dcid) {

    int elem;
    HashSet<Integer> pvars = new HashSet<Integer>();                  // all the indexToVariable found in the i-th WHERE clause
    ArrayList<Integer> relpos = new ArrayList<Integer>();
    ArrayList<ArrayList<Integer>> eqpos = new ArrayList<ArrayList<Integer>>();
    HashMap<Integer, ArrayList<Integer>> nameToPos = new HashMap<Integer, ArrayList<Integer>>();
    for (int j = 0; j < clause.length; j++) {
      elem = clause[j];
      if (RuleStore.isVariable(elem)) {
        // make sure that elem does _not_ refer to the generic don't-care var "?_"
        if ((dcid == null) || (elem != dcid)) {
          pvars.add(elem);
          if (!nameToPos.containsKey(elem)) {
            nameToPos.put(elem, new ArrayList<Integer>());
          }
          nameToPos.get(elem).add(j);
        }
      }
    }
    // given nameToPos, determine relpos and eqpos; note: pos is always sorted
    for (ArrayList<Integer> pos : nameToPos.values()) {
      // always take the first element from pos for the relevant positions
      relpos.add(pos.get(0));
      // if length of pos > 1, pos needs to be added to eqpos
      if (pos.size() > 1) {
        eqpos.add(pos);
      }
    }
    // note: relpos needs to be *sorted*
    Collections.sort(relpos);
    // now table can be constructed
    return new Table(pvars, new HashSet<>(), relpos, eqpos, nameToPos);
  }

	/**
	 * queries the index for each clause and successively joins the results
	 */
	private BindingTable queryAndJoin(ArrayList<int[]> patterns,
																		ArrayList<Table> tables, Set<IndexLookup> lkps) {
		// call queryIndex for each pattern/table combination in order to construct binding tables
		ArrayList<BindingTable> localTables = new ArrayList<BindingTable>();
		BindingTable bt;
		// perform index lookups
    for (IndexLookup lkp : lkps) {
      bt = new BindingTable();
      lkp.apply(bt);
      localTables.add(bt);
    }
		// perform pattern lookups
		for (int i = 0; i < patterns.size(); i++) {
			bt = new BindingTable();
			bt.table = this.tupleStore.queryIndex(patterns.get(i), tables.get(i));
			bt.nameToPos = tables.get(i).nameToPosProper;
			localTables.add(bt);
		}
		// perform successive joins on the return values
		bt = localTables.get(0);
		for (int i = 1; i < localTables.size(); i++)
			bt = Calc.join(bt, localTables.get(i));
		return bt;
	}

	/**
	 * potentially modifies the binding table, due to the use of DISTINCT and
	 * the set of projected variables
	 */
	private void makeDistinctAndProject(BindingTable bt,
																			HashSet<String> projectedVars,
																			HashSet<String> foundVars,
																			HashMap<String, Integer> nameToId,
																			boolean makeDistinct) {
    // look for the generic don't-care variable marker "?_"
    if (projectedVars.contains("*") && foundVars.contains("?_")) {
      // do _not_ destructively modify original foundVars set and keep outer projectedVars container
      projectedVars.remove("*");
      for (String elem : foundVars)
        if (! elem.equals("?_"))
          projectedVars.add(elem);
    }
    // now distinguish between "*" and list of vars in SELECT
		if (projectedVars.contains("*")) {
			if (makeDistinct)
				// nothing to do for the positive case, since bt's table is already of type THashSet
				return;
			else
				// even though bt does not contain duplicates, we make its table a hash set, since
				// structurally-equivalent int arrays are counted as different objects
				bt.table = new HashSet(bt.table);
		}
		else {
			// note: non-projected vars need to be removed from bt's nameToPos and nameToExternalName fields
			foundVars.removeAll(projectedVars);
			for (String var : foundVars) {
				bt.nameToPos.remove(nameToId.get(var));
				bt.nameToExternalName.remove(nameToId.get(var));
			}
			if (makeDistinct) {
				// determine the positions that are going to be projected
				int[] pos = new int[projectedVars.size()];
				int i = 0;
				for (String var : projectedVars)
					pos[i++] = bt.nameToPos.get(nameToId.get(var));
				// pos needs to be SORTED
				Arrays.sort(pos);
				// pos is needed to construct the right hashing strategy
				bt.table = Calc.project(bt.table, pos);
			}
			else
				// seems that we only need to have a HashSet instead of a THashSet object
				bt.table = new HashSet(bt.table);
		}
	}

	/**
	 * applies the aggregates as specified in the AGGREGATE section and joins the resulting
	 * tables (if there is more than one aggregate)
	 * @see de.dfki.lt.hfc.Calc.join()
	 */
	private BindingTable aggregateAndJoin(BindingTable bt, ArrayList<Aggregate> aggregates) {
		// NOTE: each aggregate is required to provide the right nameToPos and nameToExternalName mappings
		BindingTable result = applyAggregate(aggregates.get(0), bt);  // one aggregate is guaranteed to exist
		BindingTable next;
		Map<Integer, String> nameToExternalName;
		for (int i = 1; i < aggregates.size(); i++) {
			// reuse and extend result's nameToExternalName mapping (not needed by Calc.join)
			// nameToPos is automatically set by join()
			nameToExternalName = result.nameToExternalName;
			next = applyAggregate(aggregates.get(i), bt);
			renameVariables(result, next);
			nameToExternalName.putAll(next.nameToExternalName);
			result = Calc.join(result, next);
			// nameToPos of new result is constructed by join(), but nameToExternalName is missing
			result.nameToExternalName = nameToExternalName;
		}
		return result;
	}

	/**
	 * potentially renames internal variable names (negative ints) in next, given result;
	 * this requires changing next.nameToPos and next.nameToExternalName
	 */
	private void renameVariables(BindingTable result, BindingTable next) {
		// map over the key-value pairs of both internal-to-external mappings
		Set<Map.Entry<Integer, String>> nextEntries = next.nameToExternalName.entrySet();
		Set<Map.Entry<Integer, String>> resultEntries = result.nameToExternalName.entrySet();
		// construct brand new maps for binding table next
		TreeMap<Integer, Integer> newNameToPos = new TreeMap<Integer, Integer>();
		HashMap<Integer, String> newNameToExternalName = new HashMap<Integer, String>();
		int maxVarId = -resultEntries.size();
		Integer inameNext, inameResult;
		String enameNext, enameResult;
		boolean found;
		for (Map.Entry<Integer, String> i2enameNext : nextEntries) {
			inameNext = i2enameNext.getKey();
			enameNext = i2enameNext.getValue();
			found = false;
			for (Map.Entry<Integer, String> i2enameResult : resultEntries) {
				inameResult = i2enameResult.getKey();
				enameResult = i2enameResult.getValue();
				if (enameNext.equals(enameResult)) {
					// same external var in both tables
					newNameToPos.put(inameResult, next.nameToPos.get(inameNext));
					newNameToExternalName.put(inameResult, enameResult);
					found = true;
					break;
				}
			}
			if (! found) {
				// external var from next not present in result; generate new variable
				newNameToPos.put(--maxVarId, next.nameToPos.get(inameNext));
				newNameToExternalName.put(maxVarId, enameNext);
			}
		}
		next.nameToPos = newNameToPos;
		next.nameToExternalName = newNameToExternalName;
	}

	/**
	 * applies an aggregate given a binding table and returns a new BindingTable
	 * @see de.dfki.lt.hfc.Calc.map()
	 */
	private BindingTable applyAggregate(Aggregate aggregate, BindingTable oldTable) {
		// create a new binding table from parameter oldTable that contains the variable
		// bindings and the reoccurring constants in the right order (code is relocate to
		// static method Calc.map())
		BindingTable newTable = Calc.map(oldTable, aggregate.args);
		// apply aggregate to the "projected" input table and return result table
		return this.tupleStore.aggregateRegistry.evaluate(aggregate.name,
																											AggregateRegistry.AGGREGATE_PATH,
																											newTable,
																											aggregate.nameToPos,
																											aggregate.nameToExternalName);
	}

	/**
	 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	 * !!! FOR TEST PURPOSES ONLY !!!
	 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	 *
	 * goto HFC's bin directory
	 * krieger$ java -cp .:../lib/trove-2.1.0.jar -Xms800m -Xmx1200m de/dfki/lt/hfc/Interactive
	 * > new ../resources/default.ns ../resources/default.nt ../resources/default.rdl
	 * > tuples ../resources/ltworld.jena.nt
	 * > closure
	 * > select ...
	 */

	public static void main(String[] args) throws Exception {
		Namespace ns = new Namespace("/Users/krieger/Desktop/Java/HFC/hfc/src/resources/default.ns");
		TupleStore ts = new TupleStore(100000, 250000, ns,
																	 "/Users/krieger/Desktop/Java/HFC/hfc/src/resources/default.nt");
		//ts.readTuples("/Users/krieger/Desktop/Java/HFC/hfc/src/resources/test.child.labvalues.nt");
		ts.readTuples("/Users/krieger/Desktop/Java/HFC/hfc/src/resources/ltworld.jena.nt");
		Query q = new Query(ts);
		// different binder vars in aggregates
		BindingTable bt = q.query("SELECT DISTINCT ?p WHERE ?s ?p ?o AGGREGATE ?number = Count ?p & ?subject = Identity ?p");
		//BindingTable bt = q.query("SELECT ?child ?prop ?val ?t WHERE ?child <rdf:type> <dom:Child> ?t1 & ?child <dom:hasLabValue> ?lv ?t2 & ?lv ?prop ?val ?t & AGGREGATE ?measurement ?result ?patient ?time = LGetLatestValues ?prop ?val ?child ?t ?t");
		// same binder vars in aggregates
		//q.query("SELECT ?o1 ?o2 WHERE ?s1 <value> ?o1 & ?s2 <value> ?o2 FILTER ?s1 != ?s2 & ILess ?o1 ?o2 AGGREGATE ?minmax = Min ?o1 & ?minmax = Max ?o2");
		System.out.println(bt.toString());
		//long start = System.currentTimeMillis();
		//ts.query("select distinct ?x ?y where ?x <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <owl:Class> & ?y <rdf:type> ?x");
		//ts.query("select * where ?s <rdf:type> <http://www.lt-world.org/ltw.owl#Active_Person>");
		//ts.query("select * where ?s <rdfs:subClassOf> ?t");
		//q.query("select distinct * where ?s <rdf:type> <owl:Class>");
		//q.query("select distinct ?s where ?s ?p ?o");
		//q.query("select ?s where ?s ?p ?o . ?o ?q ?s");  // -> 271
		//q.query("select distinct ?s where ?s ?p ?o & ?o ?p ?s");  // -> 206
		//q.query("select distinct * where ?s ?p ?o");
		//q.query("select * where ?s ?p ?o");
		//BindingTable bt = q.query("select distinct ?s where ?s ?p ?o & ?o ?p ?s");
		//bt.tupleStore = ts;
		//System.out.println(bt.toString(30));
		//System.out.println(bt.toString());
		//System.out.println((System.currentTimeMillis() - start)/1000.0);
	}


}

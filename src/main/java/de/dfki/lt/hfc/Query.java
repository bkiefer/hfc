package de.dfki.lt.hfc;

import java.io.*;
import java.util.*;
import gnu.trove.*;

/**
 * 
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Thu Sep 24 15:22:56 CEST 2015
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
	 * in order to query, we need a tuple store
	 */
	protected TupleStore tupleStore;
	
	/**
	 * the unary constructor internalizes the tuple store
	 */
	public Query(TupleStore tupleStore) {
		this.tupleStore = tupleStore;
	}
	
	/**
	 * the unary constructor internalizes the tuple store that `sits'
	 * inside the forward chainer
	 */
	public Query(ForwardChainer fc) {
		this.tupleStore = fc.tupleStore;
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
	 * @return null iff a constant in the query is not known to the tuple store (represents an empty table)
	 * @return a (possibly empty) binding table, otherwise
	 * @throws a query parse exception iff query is syntactically incorrect
	 */
	public BindingTable query(String query) throws QueryParseException {
		// do we want to proxy expansion
		this.expandProxy = false;
		// remove unnecessary whitespaces
		query = query.trim();
    // use set to get rid of duplicates
		HashSet<String> projectedVars = new HashSet<String>();
		// parse SELECT section
		boolean makeDistinct = parseSelect(new StringTokenizer(query), projectedVars);
		// parse WHERE section
		ArrayList<ArrayList<String>> whereClauses = new ArrayList<ArrayList<String>>();
		// look for " WHERE": "WHERE" won't work due to a selected variable "?where"
		String where = query.substring(query.toUpperCase().indexOf(" WHERE"));
		where = where.trim();  // get rid of leading space
		HashSet<String> foundVars = new HashSet<String>();
		parseWhere(new StringTokenizer(where, " ?<>_\"\\", true), whereClauses, foundVars);
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
		ArrayList<ArrayList<String>> filterClauses = null;
		int hasFilter = query.toUpperCase().indexOf(" FILTER");
		if (hasFilter != -1) {
			String filter = query.substring(hasFilter);
			filter = filter.trim();  // get rid of leading space
			filterClauses = new ArrayList<ArrayList<String>>();
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
		ArrayList<int[]> patterns = new ArrayList<int[]>();
		// we return the null value in case a constant in the WHERE clause is not known to the tuple
		// store; given null, we can distinguish this case from an empty binding table, arising from
		// a query with known constants
		if (internalizeWhere(whereClauses, patterns, nameToId, idToName) == null)
			return null;
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
		prepareQueryIndex(patterns, tables, nameToId.get("?_"));
		// call queryIndex for each pattern/table combination in order to construct binding tables
		// and perform successive joins on the return values
		BindingTable bt = queryAndJoin(patterns, tables);
		// bt now refers to the joined tables, so potential filter conditions can be applied
		if (hasFilter != -1) {
			Calc.restrict(bt, varvarIneqs, varconstIneqs);
			Calc.restrict(bt, predicates);
		}
		// equip bt with the idToName mapping for later output;
		// note: idToName even contains LHS aggregate variables
		bt.nameToExternalName = idToName;
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
	private boolean parseSelect(StringTokenizer st, HashSet<String> projectedVars) throws QueryParseException {
		boolean distinct = false;
		if (! st.hasMoreTokens())
			throw new QueryParseException("empty query");
		String token = st.nextToken();
		if (! (token.toUpperCase().equals("SELECT") || token.toUpperCase().equals("SELECTALL")))
			throw new QueryParseException("SELECT/SELECTALL missing");
		// if SELECTALL, record that binding table needs to be expanded afterwards
		if (token.toUpperCase().equals("SELECTALL"))
			this.expandProxy = true;
		if (! st.hasMoreTokens())
			throw new QueryParseException("query too short");
		token = st.nextToken();
		if (token.toUpperCase().equals("DISTINCT")) {
			distinct = true;
			if (! st.hasMoreTokens())
				throw new QueryParseException("query too short");
			token = st.nextToken();
		}
		if (token.equals("*")) {
			projectedVars.add(token);
			if (! st.hasMoreTokens())
				throw new QueryParseException("query too short");
			token = st.nextToken();
			if (token.toUpperCase().equals("WHERE"))
				return distinct;
			else
				throw new QueryParseException("WHERE missing");
		}
		while (! token.toUpperCase().equals("WHERE")) {
			if (! RuleStore.isVariable(token))
				throw new QueryParseException("token not a variable");
			else
				projectedVars.add(token);
			if (! st.hasMoreTokens())
				throw new QueryParseException("WHERE missing");
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
			}
			else if (token.equals("?"))
				foundVars.add(TupleStore.parseVariable(st, tuple));
			else if (token.equals("<"))
				this.tupleStore.parseURI(st, tuple);
			else if (token.equals("\""))
				this.tupleStore.parseAtom(st, tuple);
			else if (token.equals("_"))
				TupleStore.parseBlankNode(st, tuple);
			else if (token.equals(" "))  // keep on parsing ...
				continue;
			// break if (optional) FILTER or AGGREGATE is found
			else if (token.toUpperCase().equals("FILTER") || token.toUpperCase().equals("AGGREGATE"))
				break;
			// something has gone wrong when reading the tuple
			else
				throw new QueryParseException("  incorrect WHERE clause");
		}
		// since last clause needs not be finished by the '.' character, we
		// can detect this by checking whether tuple is not empty
		if (! tuple.isEmpty())
			whereClauses.add(tuple);
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
			else  // a predicate symbol--I do not check whether this is a legal Java class name
				constraint.add(token);
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
			// an aggregate symbol--I do not check whether this is a legal Java class name
			else
				aggregate.add(token);
		}
		if (! aggregate.isEmpty())
			aggregateClauses.add(aggregate);
	}
	
	/**
	 * maps surface WHERE form to internal representation
	 * @return null iff constants in the WHERE clause are NOT known to the tuple store
	 */
	private ArrayList<int[]> internalizeWhere(ArrayList<ArrayList<String>> whereClauses,
																						ArrayList<int[]> patterns,
																						HashMap<String, Integer> nameToId,
																						HashMap<Integer, String> idToName) {
		int[] clause;
		int varcount = 0;
		ArrayList<String> wc;
		String elem;
		// process WHERE section
		for (int j = 0; j < whereClauses.size(); j++) {
			wc = whereClauses.get(j);
			clause = new int[wc.size()];
			patterns.add(clause);
			for (int i = 0; i < wc.size(); i++) {
				elem = wc.get(i);
				// a variable: check if it is old or new
				if (RuleStore.isVariable(elem)) {
					if (nameToId.containsKey(elem))
						clause[i] = nameToId.get(elem);
					else {
						nameToId.put(elem, --varcount);
						idToName.put(varcount, elem);
						clause[i] = varcount;
					}
				}
				// a constant: check whether elem is known to the tuple store;
				// also handle uri-to-proxy mapping here
				else {
					if (this.tupleStore.isConstant(elem)) {
						clause[i] = this.tupleStore.objectToId.get(elem);
						if (this.tupleStore.equivalenceClassReduction)
							clause[i] = this.tupleStore.getProxy(clause[i]);
					}
					else
						// not known: return null to indicate this special empty table;
						// other options: (i) throw a special exception  or (ii) an empty binding table
						return null;
				}
			}
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
		!TupleStore.isBlankNode(literal);
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
						else
							throw new QueryParseException("  unknown constant in predicate: " + next);
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
					else
						throw new QueryParseException("  unknown constant in in-eq constraint: " + next);
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
					else
						throw new QueryParseException("  unknown constant in in-eq constraint: " + elem);
				}
			}
			aggregates.add(new Aggregate(name, vars, args));
		}
	}
	
	/**
	 * preparation phase, before querying the index for each clause;
	 * determines the proper vars, don't care vars, relevant positions,
	 * equal positions, and the name-to-position mapping
	 */
	private void prepareQueryIndex(ArrayList<int[]> patterns,
																 ArrayList<Table> tables,
                                 Integer dcid) {
		Table table;
		int[] clause;
		int elem;
		HashSet<Integer> pvars;
		HashSet<Integer> dcvars = new HashSet<Integer>();  // always empty for queries due to _not_ using DISTINCT
		ArrayList<Integer> relpos;
		ArrayList<ArrayList<Integer>> eqpos;
		HashMap<Integer, ArrayList<Integer>> nameToPos;
		for (int i = 0; i < patterns.size(); i++) {
			pvars = new HashSet<Integer>();                  // all the vars found in the i-th WHERE clause
			relpos = new ArrayList<Integer>();
			eqpos = new ArrayList<ArrayList<Integer>>();
			nameToPos = new HashMap<Integer, ArrayList<Integer>>();
			clause = patterns.get(i);
			for (int j = 0; j < clause.length; j++) {
				elem = clause[j];
				if (RuleStore.isVariable(elem)) {
          // make sure that elem does _not_ refer to the generic don't-care var "?_"
          if ((dcid == null) || (elem != dcid)) {
            pvars.add(elem);
            if (! nameToPos.containsKey(elem))
              nameToPos.put(elem, new ArrayList<Integer>());
            nameToPos.get(elem).add(j);
          }
				}
			}
			// given nameToPos, determine relpos and eqpos; note: pos is always sorted
			for (ArrayList<Integer> pos : nameToPos.values()) {
				// always take the first element from pos for the relevant positions
        relpos.add(pos.get(0));
				// if length of pos > 1, pos needs to be added to eqpos
				if (pos.size() > 1)
					eqpos.add(pos);
			}
			// note: relpos needs to be *sorted*
			Collections.sort(relpos);
			// now table can be constructed
			table = new Table(pvars, dcvars, relpos, eqpos, nameToPos);
			tables.add(table);
		}
	}
	
	/**
	 * queries the index for each clause and successively joins the results
	 */
	private BindingTable queryAndJoin(ArrayList<int[]> patterns,
																		ArrayList<Table> tables) {
		// call queryIndex for each pattern/table combination in order to construct binding tables
		ArrayList<BindingTable> localTables = new ArrayList<BindingTable>();
		BindingTable bt;
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
		Namespace ns = new Namespace("/Users/krieger/Desktop/Java/HFC/hfc/resources/default.ns");
		TupleStore ts = new TupleStore(100000, 250000, ns,
																	 "/Users/krieger/Desktop/Java/HFC/hfc/resources/default.nt");
		//ts.readTuples("/Users/krieger/Desktop/Java/HFC/hfc/resources/ltworld.nt");
		Query q = new Query(ts);
		// different binder vars in aggregates
		BindingTable bt = q.query("SELECT ?p WHERE ?s ?p ?o AGGREGATE ?number = CountDistinct ?p & ?subject = Identity ?p");
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
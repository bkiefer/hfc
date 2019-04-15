package de.dfki.lt.hfc;

import de.dfki.lt.hfc.qrelations.QRelation;
import de.dfki.lt.hfc.qrelations.QRelationFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;


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
 * @author (C) Hans-Ulrich Krieger
 * @version Tue Jan  5 11:45:27 CET 2016
 * @since JDK 1.5
 */
public class Query {

  private static final Logger logger = LoggerFactory.getLogger(Query.class);

  /**
   * do we want proxy expansion in the resulting binding table if equivalence reduction
   * has been turned on ?
   * this field is set every time query() is called, depending on whether either SELECT
   * (expandProxy = false) or SELECTALL (expandProxy = true) is used
   */
  //private boolean expandProxy = false;
  /**
   * in order to query, we need a tuple store
   */
  protected TupleStore tupleStore;
  /**
   * a constant that controls whether a warning is printed in case "unexpected" things
   * happen; similar variables exists in class TupleStore, RuleStore, and ForwardChainer
   */
  private boolean verbose = false;
  private QueryParser queryParser;
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
   * reads in a QDL query and returns a binding table, encoding the result of the query;
   * at the moment (= v2.3), QDL is described by the following EBNF:
   * <p>
   * <query>     ::= <select> <where> [<filter>] [<aggregate>] | ASK <groundtuple>
   * <select>    ::= {"SELECT" | "SELECTALL"} ["DISTINCT"] {"*" | <var>^+}
   * <var>       ::= "?"{a-zA-Z0-9}^+ | "?_"
   * <nwchar>    ::= any NON-whitespace character
   * <where>     ::= "WHERE" <tuple> {"&" <tuple>}^*
   * <tuple>     ::= <literal>^+
   * <gtuple>    ::= <constant>^+
   * <literal>   ::= <var> | <constant>
   * <constant>  ::= <uri> | <atom>
   * <uri>       ::= "<" <nwchar>^+ ">"
   * <atom>      ::= "\""  <char>^* "\"" [ "@" <langtag> | "^^" <xsdtype> ]
   * <char>      ::= any character, incl. whitespaces, numbers, even '\"'
   * <langtag>   ::= "de" | "en" | ...
   * <xsdtype>   ::= "<xsd:int>" | "<xsd:long>" | "<xsd:float>" | "<xsd:double>" | "<xsd:dateTime>" |
   * "<xsd:string>" | "<xsd:boolean>" | "<xsd:date>" | "<xsd:gYear>" | "<xsd:gMonthDay>" |
   * "<xsd:gDay>" | "<xsd:gMonth>" | "<xsd:gYearMonth>" | "<xsd:duration>" | "<xsd:anyURI>" | ...
   * <filter>    ::= "FILTER" <constr> {"&" <constr>}^*
   * <constr>    ::= <ineq> | <predcall>
   * <ineq>      ::= <var> "!=" <literal>
   * <predcall>  ::= <predicate> <literal>^*
   * <predicate> ::= <nwchar>^+
   * <aggregate> ::= "AGGREGATE" <funcall> {"&" <funcall>}^*
   * <funcall>   ::= <var>^+ "=" <function> <literal>^*
   * <function>  ::= <nwchar>^+
   * <p>
   * NOTE: the reserved keywords ASK, SELECT, SELECTALL, DISTINCT, WHERE, FILTER, and AGGREGATE need
   * _not_ be written uppercase
   * <p>
   * NOTE: it is required that neither filter predicates nor aggregate functions have the same name
   * as the above reserved keywords
   * <p>
   * NOTE: _don't-care_ variables should be marked _explicitly_ by using exactly the identifier "?_";
   * this is especially important when using "*" in a SELECT;
   * example:
   * SELECT DISTINCT * WHERE ?s <rdf:type> ?_
   * SELECT * WHERE ?s <rdf:type> ?o ?_ ?_
   * when restricting the object position without projecting it, we explicitly write down theselcted vars:
   * SELECT ?s WHERE ?s <rdf:type> ?o ?_ ?_ FILTER ?o != <foo-class>
   *
   * @return a (possibly empty) binding table, otherwise
   * @throws a query parse exception iff query is syntactically incorrect
   */
  public BindingTable query(String query) throws QueryParseException {
    // do we want to proxy expansion
    //this.expandProxy = false;
    this.activeIndex = this.tupleStore.indexStore != null;
    // remove unnecessary whitespaces
    query = query.trim();
    if (query.contains(" ?rel")) {
      throw new QueryParseException("The variable name ?rel is reserved!");
    }
    StringReader stringReader = new StringReader(query);
    queryParser = new QueryParser(stringReader, tupleStore);

    try {
      queryParser.parse();
    } catch (IOException e) {
      e.printStackTrace();
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
    if (internalizeWhere(queryParser.whereClauses, patterns, lkps, nameToId, idToName,
            queryParser.filterClauses, queryParser.foundVars).isEmpty() && lkps.isEmpty())
      return new BindingTable();
    if (!queryParser.filterClauses.isEmpty()) {
      queryParser.hasFilter = true;
    }
    // now that mappings have been established, internalize FILTER conditions;
    // note: all filter vars are definitely contained in found vars at this point
    ArrayList<Integer> varvarIneqs = null;
    ArrayList<Integer> varconstIneqs = null;
    ArrayList<Predicate> predicates = null;
    if (queryParser.hasFilter) {
      varvarIneqs = new ArrayList<Integer>();
      varconstIneqs = new ArrayList<Integer>();
      predicates = new ArrayList<Predicate>();
      internalizeFilter(queryParser.filterClauses, varvarIneqs, varconstIneqs, predicates, nameToId);
    }
    // lastly, internalize AGGREGATE information; note: aggregate variables will only refer
    // to selected variables
    ArrayList<Aggregate> aggregates = null;
    if (queryParser.hasAggregate) {
      aggregates = new ArrayList<Aggregate>();
      internalizeAggregate(queryParser.aggregateClauses, aggregates, nameToId, idToName, queryParser.projectedVars, queryParser.foundVars);
    }
    // process the clauses to obtain the corresponding parameters to queryIndex()
    ArrayList<Table> tables = new ArrayList<Table>();
    prepareQueryIndex(patterns, lkps, tables, nameToId.get("?_"));
    // call queryIndex for each pattern/table combination in order to construct binding tables
    // and perform successive joins on the return values
    BindingTable bt = queryAndJoin(patterns, tables, lkps);
    // bt now refers to the joined tables, so potential filter conditions can be applied
    if (queryParser.hasFilter) {
      logger.info("Has Filter " + queryParser.hasFilter + "\n varvarIneqs: " + varvarIneqs + "\n varconstIneqs " + varconstIneqs + " \n predicates " + predicates);
      //TODO for testing only
      bt.tupleStore = tupleStore;
      //TODO end
      Calc.restrict(bt, varvarIneqs, varconstIneqs);
      Calc.restrict(bt, predicates);
    }
    // equip bt with the idToName mapping for later output;
    // note: idToName even contains LHS aggregate variables
    bt.nameToExternalName = idToName;
    bt.selectVars = new String[queryParser.projectedVars.size()];
    queryParser.projectedVars.toArray(bt.selectVars);
    // finally consider projected vars and DISTINCT keyword
    makeDistinctAndProject(bt, queryParser.projectedVars, queryParser.foundVars, nameToId, queryParser.isDistinct());
    // bt is now destructively changed; finally consider potential aggregation;
    // note that there might be several aggregate clauses, working on the same/overlapping
    // projection window; if so, do not change the table any longer and combine the result
    // using table join
    if (queryParser.hasAggregate)
      bt = aggregateAndJoin(bt, aggregates);
    bt.tupleStore = this.tupleStore;
    // in case a SELECTALL query has been posted, expand the binding table
    if (queryParser.isExpandProxy()) {
      bt.expandBindingTable();
    }
    return bt;
  }


  /**
   * maps surface WHERE form to internal representation. Further handles the internalization of
   * relations and intervals, In case there are relations or intervals, two cases have to be
   * consideres 1) There is an active IndexStore and the relation matches the indices. Then a
   * corresponding {@link IndexLookup} is created and added to the given
   * lkps Set. 2) If (1) does not hold, the relations are rewritten into new filter clauses. These
   * clauses are added to the given {@link ArrayList} of filterclauses.
   *
   * @param whereClauses  the clauses to be internalized. Each {@link ArrayList} represents one
   *                      clause.
   * @param patterns      the internalized clauses. This Lst will be populated in the course of this
   *                      method.
   * @param lkps          a set of {@link IndexLookup}s to be populated in the
   *                      course of this method.
   * @param idToName      a mapping from internal id to the string representation.
   * @param filterClauses a {@link ArrayList} of filterclauses. This might be extended in the course
   *                      of the method, in case there are relations which must be rewritten.
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
            clause.add(this.tupleStore.putObject(elem));
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
            if (tupleStore.isAtom(wc.get(i))) {
              //tupleStore.putObject(elem);
              clause.add(this.tupleStore.putObject(elem));
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
        createPattern = tupleStore.indexStore.prepareLookup(clause, idToRelation, lkps, relationsToBeRewritten);
      }
      if (!activeIndex || !relationsToBeRewritten.isEmpty() || createPattern) {
        patterns.add(Utils.toPrimitive(clause));
      }
    }
    for (QRelation relation : new HashSet<>(idToRelation.values())) {
      filterClauses.addAll(relation.rewrite(idToName));
    }
    // return internalized where clause
    return patterns;
  }

  /**
   * maps surface FILTER form to internal representation, stored in varvarIneqs and
   * varconstIneqs (as used by Calc.restrict) and predicates;
   * <p>
   * NOTE: code does NOT work for NoValue in queries (FILTER), but DOES work for @test in rules
   * SOLUTION: replace QueryParseException by a warning
   *
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
              id = this.tupleStore.putObject(next);
              // uri-to-proxy mapping, if necessary
              if (this.tupleStore.equivalenceClassReduction)
                id = this.tupleStore.getProxy(id);
              args.add(id);
            } else {
              // constant _not_ known to tuple store: we no longer will throw an exception:
              //throw new QueryParseException("  unknown constant in predicate: " + next);
              if (verbose)
                logger.info("  unknown constant in FILTER predicate: " + next + "\n");
              this.tupleStore.putObject(next);
              id = this.tupleStore.putObject(next);
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
        } else {
          varconstIneqs.add(nameToId.get(first));
          // next might not be known to the tuple store
          if (this.tupleStore.isConstant(next)) {
            id = this.tupleStore.putObject(next);
            // uri-to-proxy mapping, if necessary
            if (this.tupleStore.equivalenceClassReduction)
              id = this.tupleStore.getProxy(id);
            varconstIneqs.add(id);
          } else {
            // constant _not_ known to tuple store: we no longer will throw an exception:
            //throw new QueryParseException("  unknown constant in in-eq constraint: " + next);
            if (verbose)
              logger.info("  unknown constant in FILTER in-eq constraint: " + next + "\n");
//            this.tupleStore.putObject(next);
            id = this.tupleStore.putObject(next);
            varconstIneqs.add(id);
          }
        }
      }
    }
  }

  /**
   * maps surface AGGREGATE form to internal representation, stored in aggregates;
   * aggregate list is of the following form:
   * <var1> ... <varN> = <aggregate> <token1> ... <tokenM>,
   * where
   * N >= 1 (at least _one_ var) and M >= 0 (potentially _no_ args)
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
            if (!foundVars.contains(elem))
              throw new QueryParseException("  aggregate RHS variable not contained in SELECT: " + elem);
          } else {
            if (!projectedVars.contains(elem))
              throw new QueryParseException("  aggregate RHS variable not contained in SELECT: " + elem);
          }
          args[i - eqpos - 2] = nameToId.get(elem);
        }
        // a constant
        else {
          if (this.tupleStore.isConstant(elem)) {
            id = this.tupleStore.putObject(elem);
            // uri-to-proxy mapping, if necessary
            if (this.tupleStore.equivalenceClassReduction)
              id = this.tupleStore.getProxy(id);
            args[i - eqpos - 2] = id;
          } else {
            // constant _not_ known to tuple store: we no longer will throw an exception:
            //throw new QueryParseException("  unknown constant in aggregate: " + elem);
            if (verbose)
              logger.info("  unknown constant in AGGREGATE function: " + elem + "\n");
            //this.tupleStore.putObject(elem);
            id = this.tupleStore.putObject(elem);
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
        if (!elem.equals("?_"))
          projectedVars.add(elem);
    }
    // now distinguish between "*" and list of vars in SELECT
    if (projectedVars.contains("*")) {
      if (makeDistinct)
        // nothing to do for the positive case, since bt's table is already of type THashSet
      {
      }
      else
        // even though bt does not contain duplicates, we make its table a hash set, since
        // structurally-equivalent int arrays are counted as different objects
        bt.table = new HashSet(bt.table);
    } else {
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
      } else
        // seems that we only need to have a HashSet instead of a THashSet object
        bt.table = new HashSet(bt.table);
    }
  }

  /**
   * applies the aggregates as specified in the AGGREGATE section and joins the resulting
   * tables (if there is more than one aggregate)
   *
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
      if (!found) {
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
   *
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


}

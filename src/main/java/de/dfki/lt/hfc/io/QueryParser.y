/* -*- Mode: Java -*- */

%code imports {
import java.io.Reader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.dfki.lt.hfc.RuleStore;

@SuppressWarnings({"unchecked", "unused"})
}

%language "Java"

%type <LinkedList<String>> svars vars gtuples
%type <LinkedList<List<String>>> tuples
%type <LinkedList<String>>         tuple exttuple
%type <String>       literal

%type <List<List<String>>> constraints
%type <List<String>> constraint interval
%type <LinkedList<List<String>>> funcalls

%locations

%define package "de.dfki.lt.hfc.io"

%define api.parser.public

%define api.parser.class {QueryParser}

%define parse.error verbose

%define lex_throws {java.io.IOException, QueryParseException}

%code {
  private static final Logger logger = LoggerFactory.getLogger(QueryParser.class);

  public static class Position {
    private int line;
    private int column;
    private int charPos;

    public Position(int l, int col, int c) {
      line = l;
      column = col;
      charPos = c;
    }

    public boolean equals(Position l) {
      return l.line == line && l.column == column && l.charPos == charPos;
    }

    public int hashCode(Position l) {
      return line + 23 * (column + 23 * charPos);
    }

    public String toString() {
      return "" + line + ":" + column + ":" + charPos;
    }

    public int lineno() {
      return line;
    }

    public int column() {
      return column;
    }

    public int charPos() {
      return charPos;
    }
  }

  private void forAllVars(List<String> list, Consumer<String> action) {
    for (String s : list) {
      if (RuleStore.isVariable(s))
        action.accept(s);
    }
  }

  private StringBuffer string = new StringBuffer();

  // Fields for the Query class: return values
  public HashSet<String> projectedVars = new LinkedHashSet<String>();
  public List<List<String>> whereClauses;
  public List<List<String>> filterClauses;
  public List<List<String>> aggregateClauses;
  public HashSet<String> aggregateVars = new HashSet<>();
  public HashSet<String> foundVars = new HashSet<String>();
  public HashSet<String> filterVars = new HashSet<String>();

  public boolean expandProxy = false;
  public boolean distinct = false;

  public QueryParser(Reader in) {
    this(new QueryLexer(in));
  }

  public void sanityChecks() throws QueryParseException {
    if (projectedVars.contains("*")) {
      // a "*" should not come up with further vars
      if (projectedVars.size() > 1)
        throw new QueryParseException("\"*\" and further variables can not be mixed");
    } else {
      // projected vars should only consist of found vars
      HashSet<String> pv = new HashSet<String>(projectedVars);
      pv.removeAll(foundVars);
      if (! pv.isEmpty())
        throw new QueryParseException("SELECT contains variables not found in WHERE: " + pv);
      HashSet<String> fv = new HashSet<String>(filterVars);
      fv.removeAll(foundVars);
      if (! pv.isEmpty())
        throw new QueryParseException("FILTER contains variables not found in WHERE: " + pv);
    }
  }
}

// keywords
%token ASK
%token SELECT
%token SELECTALL
%token DISTINCT
%token WHERE
%token FILTER
%token AGGREGATE
%token INTERVALSTART
%token INTERVALEND

%token <String> NOTEQ
%token <String> EQ

// real tokens
%token < String > URI
%token < String > BLANK
%token < String > VAR
%token < String > ATOM
%token < String > ID
%token < String > ALLENRELATION

%%

// start rule
query:          select WHERE exttuple tuples filter aggregate {
                  $4.addFirst($3);
                  for (List<String> l: $4)
                    forAllVars(l, (s) -> foundVars.add(s));
                  whereClauses = $4;
                }
       |       ASK gtuples {
                  // TODO
                }
                ;

select:         SELECT svars
        |       SELECT DISTINCT svars { distinct = true; }
        |       SELECTALL svars { expandProxy = true; }
        |       SELECTALL DISTINCT svars { distinct = expandProxy = true;}
                ;

exttuple:       tuple { $$ = $1; }
        |       interval tuple { $1.addAll($2); $$ = $1; }
        |       tuple ALLENRELATION tuple {
                  $1.addAll($3);
                  $1.add($2);
                  $$ = $1;
                }
        |       interval tuple ALLENRELATION tuple {
                  $1.addAll($2);
                  $1.addAll($4);
                  $1.add($3);
                  $$ = $1;
                }
                ;

svars:          '*' {
                  LinkedList<String> arr = new LinkedList<>();
                  arr.add("*");
                  projectedVars.add("*");
                  $$ = arr;
                }
        |       vars { projectedVars.addAll($1); $$ = $1; }
                ;

vars:           vars VAR { $1.add($2); $$ = $1; }
        |       %empty { $$ = new LinkedList<>(); }
                ;

// ONLY USE IN SELECT!!
tuples:         '&' tuple tuples { $3.addFirst($2); $$ = $3; }
        |       %empty { $$ = new LinkedList<>(); }
                ;

tuple:          literal tuple { $2.addFirst($1); $$ = $2; }
        |       literal {
                  LinkedList<String>arr = new LinkedList<>();
                  arr.add($1);
                  $$ = arr;
                }
                ;

literal:        URI { $$ = $1; }
        |       VAR { $$ = $1; }
        |       BLANK { $$ = $1; }
        |       ATOM { $$ = $1; }
                ;

interval:       INTERVALSTART ATOM ATOM INTERVALEND {
                  List<String> res = new LinkedList<>();
                  res.add($2); res.add($3);
                  res.add("[]"); //the "relation"
                  $$ = res;
                }
        |       INTERVALSTART ATOM ',' ATOM INTERVALEND {
                  List<String> res = new LinkedList<>();
                  res.add($2); res.add($4);
                  res.add("[]"); //the "relation"
                  $$ = res;
                }
                ;

gtuples:        URI gtuples { $2.addFirst($1); $$ = $2; }
        |       ATOM gtuples { $2.addFirst($1); $$ = $2; }
        |       %empty { $$ = new LinkedList<>(); }
                ;

filter:         FILTER constraints { filterClauses = $2; }
        |       %empty { filterClauses = new ArrayList<List<String>>();; }
                ;

constraints:    constraints '&' constraint { $1.add($3); $$ = $1; }
        |       constraint {
                  List<List<String>> arr = new ArrayList<>();
                  arr.add($1);
                  $$ = arr;
                }
                ;

constraint:     VAR NOTEQ literal {
                  filterVars.add($1);
                  if (RuleStore.isVariable($3)) {
                    filterVars.add($3);
                  }
                  List<String> c = new LinkedList<String>();
                  c.add($1); //c.add($2); // hmm, how do i distinguish from ==?
                  c.add($3);
                  $$ = c;
                }
        |       VAR EQ literal {
                  filterVars.add($1);
                  if (RuleStore.isVariable($3)) {
                    filterVars.add($3);
                  }
                  List<String> c = new LinkedList<String>();
                  c.add($1); c.add($2); c.add($3);
                  $$ = c;
                }
        |       ID tuple {
                  forAllVars($2, (s) -> filterVars.add(s));
                  $2.addFirst($1);
                  $$ = $2;
                }
        |       '!' ID tuple {
                  forAllVars($3, (s) -> filterVars.add(s));
                  $3.addFirst($2); $3.addFirst("NOT");
                  $$ = $3;
                }
        |       VAR ID tuple {
                  forAllVars($3, (s) -> filterVars.add(s));
                  filterVars.add($1);
                  $3.addFirst($2);
                  $3.addFirst($1);
                  $$ = $3;
                }
        |       '!' VAR ID tuple {
                  forAllVars($4, (s) -> filterVars.add(s));
                  filterVars.add($2);
                  $4.addFirst($3); $4.addFirst($2); $4.addFirst("NOT");
                  $$ = $4;
                }
                ;

aggregate:      AGGREGATE funcalls { aggregateClauses = $2; }
        |       %empty { aggregateClauses = Collections.emptyList(); }
                ;

funcalls:       vars '=' ID tuple '&' funcalls {
                  forAllVars($4, (s) -> foundVars.add(s));
                  aggregateVars.addAll($1);
                  LinkedList<String> agg = new LinkedList<>();
                  agg.addAll($1);
                  agg.add("=");
                  agg.add($3);
                  agg.addAll($4);
                  $6.addFirst(agg);
                  $$ = $6;
                }
        |       vars '=' ID tuple {
                  forAllVars($4, (s) -> foundVars.add(s));
                  aggregateVars.addAll($1);
                  LinkedList<String> agg = new LinkedList<>();
                  agg.addAll($1);
                  agg.add("=");
                  agg.add($3);
                  agg.addAll($4);
                  LinkedList<List<String>> res = new LinkedList<>();
                  res.add(agg);
                  $$ = res;
                }
                ;


/*
 * LEXER
 */
/*
/// keywords + true, false, null:
IMPORT: 'import';
IF: 'if';
ELSE: 'else';
WHILE: 'while';
DO: 'do';
SWITCH: 'switch';
CASE: 'case';
DEFAULT: 'default';
CONTINUE: 'continue';
BREAK: 'break';
CANCEL: 'cancel';
CANCEL_ALL: 'cancel_all';
FOR: 'for';
NULL: 'null';
TRUE: 'true';
FALSE: 'false';
RETURN: 'return';
PUBLIC: 'public';
PROTECTED: 'protected';
PRIVATE: 'private';
NEW: 'new';
FINAL: 'final';
DEC_VAR: 'var';

/// character literal (starting with ' ):
CHARACTER: '\''.'\'';

/// string (starting with " ):
STRING: '\"'(~('\\'|'"')|'\\"')*'\"';

// TODO: WHAT IS THIS GOOD FOR ?
ANNOTATION: '@'('0'..'9'|'A'..'z'|'_'|'('|')')+;

/// special for dialogue grammar:
WILDCARD: '_';
PROPOSE: 'propose';
TIMEOUT: 'timeout';
*/
/// comments (starting with /* or //):
//JAVA_CODE: '/*@'.*?'@*/' -> channel(HIDDEN);
//ONE_L_COMMENT: '//'.*?'\n' -> channel(HIDDEN);
//MULTI_L_COMMENT: '/*'.*?'*/' -> channel(HIDDEN);
/*
/// whitespace
WS: [ \t\u000C]+ -> channel(HIDDEN);
NLWS: [\n\r]+ -> channel(HIDDEN);

// LETTER: ('A'..'Z'|'a'..'z');
/// identifiers (starting with "java letter"):
IDENTIFIER: ('A'..'Z'|'a'..'z'|'_')('0'..'9'|'A'..'Z'|'a'..'z'|'_'|'$')*;

/// numeric literal (starting with - or number):
INT: ('-')?('1'..'9')?('0'..'9')+;
FLOAT: ('-')?('0'('.'('0'..'9')+) | ('1'..'9')('0'..'9')*('.'('0'..'9')+));
*/

%%

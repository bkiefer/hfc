package de.dfki.lt.hfc;

import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import de.dfki.lt.hfc.types.XsdString;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

%%
%class QueryParser
%line
%column
%unicode
%ctorarg TupleStore tuplestore
%int

%yylexthrow{
  QueryParseException
%yylexthrow}

%eofthrow{
    QueryParseException
%eofthrow}

%init{
  ts = tuplestore;
%init}

%{
   private static final Logger logger = LoggerFactory.getLogger(QueryParser.class);
   private TupleStore ts;
   private StringBuffer string = new StringBuffer();
   public HashSet<String> projectedVars = new LinkedHashSet<String>();
   public ArrayList<ArrayList<String>> whereClauses = new ArrayList<ArrayList<String>>();
   public ArrayList<ArrayList<String>> filterClauses = new ArrayList<ArrayList<String>>();
   public ArrayList<ArrayList<String>> aggregateClauses = new ArrayList<ArrayList<String>>();
   public HashSet<String> aggregateVars = new HashSet<>();
   public HashSet<String> foundVars = new HashSet<String>();
   private boolean distinct = false;
   private boolean expandProxy = false;
   public boolean hasAggregate = false;
   public boolean hasFilter = false;
   private ArrayList<String> clause = new ArrayList<>();
   private int state;
   private boolean rhs = false;
   private boolean interval = false;
   private String start;
   private int c = 0;
   private boolean isRelation = false;

   public boolean isDistinct(){
       return this.distinct;
   }

   public boolean isExpandProxy(){
       return this.expandProxy;
   }

   private void handleVar(String var) throws QueryParseException {
       if(this.state == FILTER){
           logger.info(" foundVars " + foundVars);
               logger.info(" Var " + var);
               if(!this.foundVars.contains(var))
                   throw new QueryParseException("unknown variable " + var + " used in FILTER");
       }
        //add to clause
        this.clause.add(var);
        //add to foundVars
        this.foundVars.add(var);
   }

   private void closeWhereClauses() throws QueryParseException {
       this.whereClauses.add(clause);
       if (whereClauses.isEmpty()) throw new QueryParseException("Missing where clauses");
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
       clause = new ArrayList<>();
   }

   private void closeFilterClauses(){ this.filterClauses.add(clause);
   clause = new ArrayList<>();}

   private void closeAggregateClauses(){this.aggregateClauses.add(clause); clause = new ArrayList<>();}

   private void handleEOF() throws QueryParseException {
        switch (state){
            case(WHERE):{
                closeWhereClauses();
                break;
            }
            case (FILTER):{
                closeFilterClauses();
                break;
            }
            case (AGGREGATE): {
                closeAggregateClauses();
                break;
            }
            default:{
                throw new QueryParseException("Invalid query");
            }
        }
     }

     public void parse() throws IOException, QueryParseException{
       while ( !zzAtEOF ){
               yylex();
             }
     }

     private String handleUnicode(String match){
              Pattern p = Pattern.compile("\\\\u(\\p{XDigit}{4})");
              Matcher m = p.matcher(match);
              StringBuffer buf = new StringBuffer(match.length());
              while (m.find()) {
              String ch = String.valueOf((char) Integer.parseInt(m.group(1), 16));
                 m.appendReplacement(buf, Matcher.quoteReplacement(ch));
              }
              m.appendTail(buf);
              return buf.toString();

       }

       private void closeRelation(String end){
        c = 0;
        interval = false;
        isRelation = false;
        this.clause.add(start + end);
        yybegin(state);
       }

%}

%eof{
    handleEOF();
%eof}


NEWLINE = \r|\n|\r\n|\u2028|\u2029|\u000B|\u000C|\u0085
WhiteSpace = [ \t\f]
NONWHITESPACE = [^ \r\n\t\f]
URI = <{NONWHITESPACE}+:{NONWHITESPACE}+> | <{NONWHITESPACE}+#{NONWHITESPACE}+>
BLANK = _:{NONWHITESPACE}+
COMMENT = #.*
StringCharacter = [^\r\n\"\\]
OctDigit          = [0-7]
VAR = \?{NONWHITESPACE}+
SELECT = select | Select | SELECT
SELECTALL = selectall | SelectAll | SELECTALL
DISTINCT = distinct | Distinct | DISTINCT
WHERE = where | Where | WHERE
FILTER = filter | Filter | FILTER
PRED = {NONWHITESPACE}+
AGGREGATE = aggregate | Aggregate | AGGREGATE
INTERVALSTART = [\[\(]
INTERVALEND = [\]\)]
ALLENRELATION = "S"| "Si"| "F"| "Fi"| "D"| "Di"| "Bf"| "Af"| "M"| "Mi"| "EA"| "O"| "Oi"


%state STRING, SELECT, WHERE, FILTER, AGGREGATE, INTERVAL

%%

<YYINITIAL> {

{SELECT} {
           yybegin(SELECT);}

{SELECTALL} { this.expandProxy = true;
            yybegin(SELECT);
            }
}

<STRING> {
  \"                             {
   if (interval == true)yybegin(INTERVAL); else yybegin(state);
                                 }
  // add handling for short/long and langtag
  {StringCharacter}+             |

  /* escape sequences */
  "\\b"                          |
  "\\t"                          |
  "\\n"                          |
  "\\f"                          |
  "\\r"                          |
  "\\\""                         |
  "\\'"                          |
  "\\u"                          |
  "\\\\"                         |
  \\[0-3]?{OctDigit}?{OctDigit}  { string.append( yytext() ); }

  /* error cases */
  \\.                            { throw new RuntimeException("Illegal escape sequence \""+yytext()+"\""); }
  {NEWLINE}               { throw new RuntimeException("Unterminated string at end of line"); }

}

<INTERVAL> {

{VAR} {c++; handleVar(yytext());
if(c==2)closeRelation("");}

\^\^<xsd:{NONWHITESPACE}+> {
                            c++;
                           clause.add("\""+handleUnicode(string.toString())+"\"" + ts.namespace.normalizeNamespace(yytext()));
                           string.setLength(0);if(c==2)if(isRelation)closeRelation("");}

\^\^<{NONWHITESPACE}+#{NONWHITESPACE}+> {
                            c++;
                            clause.add("\""+handleUnicode(string.toString())+"\"" + ts.namespace.normalizeNamespace(yytext()));
                            string.setLength(0);if(c==2)if(isRelation)closeRelation("");}

\" {  interval = true; yybegin(STRING);}

{WhiteSpace} |
"," {}

{INTERVALEND} {closeRelation( yytext()); }

. {throw new QueryParseException();}

}
<SELECT> {

{DISTINCT} {distinct = true;}

{VAR} {this.projectedVars.add(yytext());}

"*" {projectedVars.add("*");}

{WhiteSpace} {}

{WHERE} { state = WHERE;  yybegin(WHERE);}

. {throw new QueryParseException("Invalid select statement " + yytext());}

}

<WHERE> {
"&" {this.whereClauses.add(clause);
clause = new ArrayList<>();}

{VAR} {
   handleVar(yytext());
}

{INTERVALSTART} {start = yytext(); state = WHERE; yybegin(INTERVAL);}
{ALLENRELATION} {isRelation = true; start = yytext(); state = WHERE; yybegin(INTERVAL);}

{URI}            {this.clause.add(yytext());}

\^\^<xsd:{NONWHITESPACE}+> {

                           clause.add("\""+handleUnicode(string.toString())+"\"" + ts.namespace.normalizeNamespace(yytext()));
                           string.setLength(0);}

\^\^<{NONWHITESPACE}+#{NONWHITESPACE}+> {clause.add("\""+handleUnicode(string.toString())+"\"" + ts.namespace.normalizeNamespace(yytext()));

                            string.setLength(0);}


{WhiteSpace} {}

\" {  yybegin(STRING);}

{AGGREGATE} {hasAggregate = true; state = AGGREGATE; closeWhereClauses();
        yybegin(AGGREGATE);}

{FILTER} {hasFilter = true; state = FILTER; closeWhereClauses();
        yybegin(FILTER);}

. {throw new QueryParseException("Invalid WHERE statement " + yytext());}
}

<FILTER> {

{AGGREGATE} {hasAggregate = true;  state = AGGREGATE; closeFilterClauses();
                     yybegin(AGGREGATE);}
"&" {this.filterClauses.add(clause);
   clause = new ArrayList<>();
    }


{VAR} {
        handleVar(yytext());
 }

"NOT" {clause.add(yytext());}

"!=" {}

{URI} {
    clause.add(yytext());
 }

 {WhiteSpace} {}

\" {yybegin(STRING); }

\^\^<xsd:{NONWHITESPACE}+> {

                           clause.add("\""+handleUnicode(string.toString())+"\"" + ts.namespace.normalizeNamespace(yytext()));
                           string.setLength(0);}

\^\^<{NONWHITESPACE}+#{NONWHITESPACE}+> {clause.add("\""+handleUnicode(string.toString())+"\"" + ts.namespace.normalizeNamespace(yytext()));

                            string.setLength(0);}

{PRED} { this.clause.add(yytext());}

    . {throw new QueryParseException("Invalid FILTER statement " + yytext());}
}

<AGGREGATE> {

{VAR} {
    if(rhs)
        handleVar(yytext());
    else {
        clause.add(yytext());
        aggregateVars.add(yytext());
    }
}

{URI} {clause.add(yytext());}
"&" {this.aggregateClauses.add(clause);
      clause = new ArrayList<>();
      rhs = false;
       }

{PRED} {this.clause.add(yytext()); rhs= true; }

\" { yybegin(STRING); }

\^\^<xsd:{NONWHITESPACE}+> {

                           clause.add("\""+handleUnicode(string.toString())+"\"" + ts.namespace.normalizeNamespace(yytext()));
                           string.setLength(0);}

\^\^<{NONWHITESPACE}+#{NONWHITESPACE}+> {clause.add("\""+handleUnicode(string.toString())+"\"" + ts.namespace.normalizeNamespace(yytext()));

                            string.setLength(0);}

{WhiteSpace} {}

= {clause.add(yytext());}


. {throw new QueryParseException("Invalid AGGREGATE statement " + yytext());}
}


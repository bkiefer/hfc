package de.dfki.lt.hfc.io;

import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import de.dfki.lt.hfc.types.XsdString;
import de.dfki.lt.hfc.io.QueryParser.Position;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

%%
%class QueryLexer
%implements QueryParser.Lexer
%function yylex_internal

%unicode

%char
%line
%column

%byaccj

%int

%yylexthrow{
  QueryParseException
%yylexthrow}

%{
  private static final Logger logger = LoggerFactory.getLogger(QueryParser.class);

  private StringBuffer string = new StringBuffer();
  private Object yylval;

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

  /**
   * Method to retrieve the beginning position of the last scanned token.
   * @return the position at which the last scanned token starts.
   */
  public Position getStartPos() {
    return new Position(yyline, yycolumn, yychar);
  }

  /**
   * Method to retrieve the ending position of the last scanned token.
   * @return the first position beyond the last scanned token.
   */
  public Position getEndPos() {
    int len = yylength();
    return new Position(yyline, yycolumn + len, yychar + len);
  }

  /**
   * Method to retrieve the semantic value of the last scanned token.
   * @return the semantic value of the last scanned token.
   */
  public Object getLVal() {
    Object result = yylval;
    yylval = null;
    return result;
  }

  /**
   * Entry point for the scanner.  Returns the token identifier corresponding
   * to the next token and prepares to return the semantic value
   * and beginning/ending positions of the token.
   *
   * This is a wrapper around the internal yylex method to collect tokens such
   * as comments, whitespace, etc. to use them later on in the compiler's
   * output. Also, other necessary functionality can be put her (extracting
   * the full input text?)
   *
   * @return the token identifier corresponding to the next token.
   */
  public int yylex() throws java.io.IOException, QueryParseException {
    int result = yylex_internal();
    return result;
  }

  /**
   * Entry point for error reporting.  Emits an error
   * referring to the given location in a user-defined way.
   *
   * @param loc The location of the element to which the
   *                error message is related
   * @param msg The string for the error message.
   */
  public void yyerror(QueryParser.Location loc, String msg) {
    logger.error("{} at {}", msg, loc);
    throw new RuntimeException(new QueryParseException(msg + " at " + loc));
  }
%}


NEWLINE = \r|\n|\r\n|\u2028|\u2029|\u000B|\u000C|\u0085
WhiteSpace = [ \t\f]
NONWHITESPACE = [^ \r\n\t\f]
URI = <{NONWHITESPACE}+:{NONWHITESPACE}+> | <{NONWHITESPACE}+#{NONWHITESPACE}+>
BLANK = _:{NONWHITESPACE}+
COMMENT = #.*
StringCharacter = [^\r\n\"\\]
OctDigit          = [0-7]
VAR = \?{NONWHITESPACE}+
PRED = {NONWHITESPACE}+
INTERVALSTART = [\[\(]
INTERVALEND = [\]\)]
ALLENRELATION = "S"| "Si"| "F"| "Fi"| "D"| "Di"| "Bf"| "Af"| "M"| "Mi"| "EA"| "O"| "Oi"

// Keywords
ASK = ask | Ask | ASK
SELECT = select | Select | SELECT
SELECTALL = selectall | SelectAll | SELECTALL
DISTINCT = distinct | Distinct | DISTINCT
WHERE = where | Where | WHERE
FILTER = filter | Filter | FILTER
AGGREGATE = aggregate | Aggregate | AGGREGATE
NOT = NOT | not

/* identifiers */
Identifier = [:jletter:][:jletterdigit:]*

%state STRING

%%

<YYINITIAL> {
// Keywords
{ASK}       { return QueryParser.Lexer.ASK;}
{SELECT}    { return QueryParser.Lexer.SELECT;}
{SELECTALL} { return QueryParser.Lexer.SELECTALL;}
{DISTINCT}  { return QueryParser.Lexer.DISTINCT; }
{WHERE}     { return QueryParser.Lexer.WHERE; }
{FILTER}    { return QueryParser.Lexer.FILTER; }
{AGGREGATE} { return QueryParser.Lexer.AGGREGATE; }

{INTERVALEND}    { return QueryParser.Lexer.INTERVALEND; }
{INTERVALSTART}  { return QueryParser.Lexer.INTERVALSTART; }
{ALLENRELATION}  { yylval = yytext();
                   return QueryParser.Lexer.ALLENRELATION; }
{NOT} { return '!'; }

/* identifiers */
{Identifier} { yylval = yytext(); return QueryParser.Lexer.ID; }
{VAR}        { yylval = yytext(); return QueryParser.Lexer.VAR; }
{URI}        { yylval = yytext(); return QueryParser.Lexer.URI; }
{BLANK}      { yylval = yytext(); return QueryParser.Lexer.BLANK; }

"!="  { yylval = yytext(); return QueryParser.Lexer.NOTEQ; }
"=="  { yylval = yytext(); return QueryParser.Lexer.EQ; }
"&"   { return (int) yytext().charAt(0); }
"="   { return (int) yytext().charAt(0); }
"*"   { return (int) yytext().charAt(0); }
","   { return (int) yytext().charAt(0); }


/* string literal */
\"    { yybegin(STRING); }

} // end YYINITIAL

                       // /[^\^@]
<STRING> {
  \"           {
                                  yybegin(YYINITIAL);
                                  String s = string.toString();
                                  string.setLength(0);
                                  yylval = '"' + s + "\"^^<xsd:string>";
                                  return QueryParser.Lexer.ATOM;
                                 }

  \"\^\^<xsd:{NONWHITESPACE}+>   {
                                yybegin(YYINITIAL);
                                String s = string.toString();
                                string.setLength(0);
                                yylval = '"' + s + yytext();
                                return QueryParser.Lexer.ATOM;
  }

  \"\^\^<{NONWHITESPACE}+#{NONWHITESPACE}+> {
                                           yybegin(YYINITIAL);
                                           String s = string.toString();
                                           string.setLength(0);
                                           yylval = '"' + s + yytext();
                                           return QueryParser.Lexer.ATOM;
  }

  \"@[a-z]+ {
                                           yybegin(YYINITIAL);
                                           String s = string.toString();
                                           string.setLength(0);
                                           yylval = '"' + s + yytext();
                                           return QueryParser.Lexer.ATOM;
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
  \\.                            { throw new QueryParseException("Illegal escape sequence \""+yytext()+"\""); }
  {NEWLINE}                      { throw new QueryParseException("Unterminated string at end of line"); }
}

{COMMENT} {}

{WhiteSpace} {}

. { throw new QueryParseException("Unexpected Character: '" + yytext() + "'");}

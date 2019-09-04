package de.dfki.lt.hfc;

import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import de.dfki.lt.hfc.types.XsdString;
import de.dfki.lt.hfc.types.Uri;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

%%
%class TupleParser
%line
%column
%unicode
%ctorarg TupleStore tuplestore
%int

%yylexthrow{
  WrongFormatException
%yylexthrow}

%eofthrow{
    WrongFormatException
%eofthrow}

%init{
  ts = tuplestore;
%init}

%{
    private TupleStore ts;
    private StringBuffer string = new StringBuffer();
    private List<List<String>> tuples = new ArrayList();
	private List<String> t = new ArrayList();
	private String match = "";
    private static final Logger logger = LoggerFactory.getLogger(TupleParser.class);
    private String front;
    private String[] backs;

	public void setTupleStore(TupleStore tuplestore){
		ts = tuplestore;
	}

  public void parse(String front, String... backs) throws IOException, WrongFormatException{
      this.front = front;
      this.backs = backs;
      while ( !zzAtEOF ){
        yylex();
      }
    }

  private void massageXSD(){
     if (string.length() != 0){
        // complete type in order to recognize duplicates (perhaps output a message?)
        match = "\"" + string.toString() + "\"";
        if (ts.namespace.isShortIsDefault())
            match += "^^" + XsdString.SHORT_NAME;
        else
            match += "^^" + XsdString.LONG_NAME;
        t.add(handleUnicode(match));
        string.setLength(0);
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

  private void handleNewLine() throws WrongFormatException {
        if(!t.isEmpty()){
    	    massageXSD();
            // now add one potential front element and further potential back elements;
            // but check whether (front == null) & (backs.length == 0) in order to avoid a
            // useless copy of 'tuple'
            if ((front != null) || (backs.length != 0))
                t = ts.extendTupleExternally(t, front, backs);
                // external tuple representation might be misspelled or the tuple is already contained
            }
            ts.addTuple(t, yyline);
    		t = new ArrayList();
  }


  private void handleEOF() throws WrongFormatException {
          handleNewLine();
          if (ts.verbose) {
            logger.info("\n  read " + ts.noOfTuples + " proper tuples");
            logger.info("  overall " + ts.allTuples.size() + " unique tuples");
                 // some further statistics
            int noOfURIs = 0, noOfBlanks = 0, noOfAtoms = 0;
            for (int i = 0; i < ts.idToJavaObject.size(); i++) {
                if (ts.idToJavaObject.get(i) instanceof Uri) //.startsWith("<"))
                         ++noOfURIs;
                else if (ts.idToJavaObject.get(i) instanceof Uri) //.startsWith("_"))
                         ++noOfBlanks;
                else
                         ++noOfAtoms;
                }
                logger.info("  found " + noOfURIs + " URIs");
                logger.info("  found " + noOfBlanks + " blank nodes");
                logger.info("  found " + noOfAtoms + " XSD atoms");
             }
             // finally cleanup
            if (ts.equivalenceClassReduction) {
                 if (ts.verbose)
                     logger.info("\n  applying equivalence class reduction ... ");
                 final int all = ts.allTuples.size();
                 final int no = ts.cleanUpTupleStore();
                 if (ts.verbose) {
                     logger.info("  removing " + no + " equivalence relation instances");
                     logger.info("  removing " + (all - ts.allTuples.size()) + " resulting duplicates");
                     logger.info("  number of all tuples: " + ts.allTuples.size() + "\n");
            }
          }
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
STRING = \"([^\\\"]|\\.)*~\"
COMMENT = #.*
StringCharacter = [^\r\n\"\\]
OctDigit          = [0-7]

%state STRING, CHARLITERAL

%%

<YYINITIAL> {

{URI} {match = yytext();
        t.add(match);
      }

\^\^<xsd:{NONWHITESPACE}+> {match = "\""+handleUnicode(string.toString())+"\"" + yytext();
                           t.add(match);
                           string.setLength(0);}

\^\^<{NONWHITESPACE}+#{NONWHITESPACE}+> {match = "\""+handleUnicode(string.toString())+"\"" + yytext();
                            t.add(match);
                            string.setLength(0);}

@{NONWHITESPACE}+ {match = yytext();
                   t.add("\""+ handleUnicode(string.toString()) + "\"" + match);
                   string.setLength(0);}

{BLANK} {match = yytext();
				if (ts.blankNodeSuffix != null) match +='X'+ ts.blankNodeSuffix;
				t.add(match);}

{WhiteSpace} {massageXSD();}

{COMMENT} {				}
{NEWLINE} {
			handleNewLine();
		   }

  /* string literal */
  \"                             {  yybegin(STRING);  }

  . {}
}

<STRING> {
  \"                             {
  yybegin(YYINITIAL);
  String s = string.toString();
                                 }

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




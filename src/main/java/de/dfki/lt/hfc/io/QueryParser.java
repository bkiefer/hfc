/* A Bison parser, made by GNU Bison 3.8.2.  */

/* Skeleton implementation for Bison LALR(1) parsers in Java

   Copyright (C) 2007-2015, 2018-2021 Free Software Foundation, Inc.

   This program is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with this program.  If not, see <https://www.gnu.org/licenses/>.  */

/* As a special exception, you may create a larger work that contains
   part or all of the Bison parser skeleton and distribute that work
   under terms of your choice, so long as that work isn't itself a
   parser generator using the skeleton or a modified version thereof
   as a parser skeleton.  Alternatively, if you modify or redistribute
   the parser skeleton itself, you may (at your option) remove this
   special exception, which will cause the skeleton and the resulting
   Bison output files to be licensed under the GNU General Public
   License without this special exception.

   This special exception was added by the Free Software Foundation in
   version 2.2 of Bison.  */

/* DO NOT RELY ON FEATURES THAT ARE NOT DOCUMENTED in the manual,
   especially those whose name start with YY_ or yy_.  They are
   private implementation details that can be changed or removed.  */

package de.dfki.lt.hfc.io;



import java.text.MessageFormat;
import java.util.ArrayList;
/* "%code imports" blocks.  */
/* "QueryParser.y":3  */

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

/* "QueryParser.java":64  */

/**
 * A Bison parser, automatically generated from <tt>QueryParser.y</tt>.
 *
 * @author LALR (1) parser skeleton written by Paolo Bonzini.
 */
public class QueryParser
{
  /** Version number for the Bison executable that generated this parser.  */
  public static final String bisonVersion = "3.8.2";

  /** Name of the skeleton that generated this parser.  */
  public static final String bisonSkeleton = "lalr1.java";



  /**
   * True if verbose error messages are enabled.
   */
  private boolean yyErrorVerbose = true;

  /**
   * Whether verbose error messages are enabled.
   */
  public final boolean getErrorVerbose() { return yyErrorVerbose; }

  /**
   * Set the verbosity of error messages.
   * @param verbose True to request verbose error messages.
   */
  public final void setErrorVerbose(boolean verbose)
  { yyErrorVerbose = verbose; }



  /**
   * A class defining a pair of positions.  Positions, defined by the
   * <code>Position</code> class, denote a point in the input.
   * Locations represent a part of the input through the beginning
   * and ending positions.
   */
  public static class Location {
    /**
     * The first, inclusive, position in the range.
     */
    public Position begin;

    /**
     * The first position beyond the range.
     */
    public Position end;

    /**
     * Create a <code>Location</code> denoting an empty range located at
     * a given point.
     * @param loc The position at which the range is anchored.
     */
    public Location (Position loc) {
      this.begin = this.end = loc;
    }

    /**
     * Create a <code>Location</code> from the endpoints of the range.
     * @param begin The first position included in the range.
     * @param end   The first position beyond the range.
     */
    public Location (Position begin, Position end) {
      this.begin = begin;
      this.end = end;
    }

    /**
     * Print a representation of the location.  For this to be correct,
     * <code>Position</code> should override the <code>equals</code>
     * method.
     */
    public String toString() {
      if (begin.equals (end))
        return begin.toString();
      else
        return begin.toString() + "-" + end.toString();
    }
  }

  private Location yylloc(YYStack rhs, int n)
  {
    if (0 < n)
      return new Location(rhs.locationAt(n-1).begin, rhs.locationAt(0).end);
    else
      return new Location(rhs.locationAt(0).end);
  }

  public enum SymbolKind
  {
    S_YYEOF(0),                    /* "end of file"  */
    S_YYerror(1),                  /* error  */
    S_YYUNDEF(2),                  /* "invalid token"  */
    S_ASK(3),                      /* ASK  */
    S_SELECT(4),                   /* SELECT  */
    S_SELECTALL(5),                /* SELECTALL  */
    S_DISTINCT(6),                 /* DISTINCT  */
    S_WHERE(7),                    /* WHERE  */
    S_FILTER(8),                   /* FILTER  */
    S_AGGREGATE(9),                /* AGGREGATE  */
    S_INTERVALSTART(10),           /* INTERVALSTART  */
    S_INTERVALEND(11),             /* INTERVALEND  */
    S_NOTEQ(12),                   /* NOTEQ  */
    S_EQ(13),                      /* EQ  */
    S_URI(14),                     /* URI  */
    S_BLANK(15),                   /* BLANK  */
    S_VAR(16),                     /* VAR  */
    S_ATOM(17),                    /* ATOM  */
    S_ID(18),                      /* ID  */
    S_ALLENRELATION(19),           /* ALLENRELATION  */
    S_20_(20),                     /* '*'  */
    S_21_(21),                     /* '&'  */
    S_22_(22),                     /* ','  */
    S_23_(23),                     /* '!'  */
    S_24_(24),                     /* '='  */
    S_YYACCEPT(25),                /* $accept  */
    S_query(26),                   /* query  */
    S_select(27),                  /* select  */
    S_exttuple(28),                /* exttuple  */
    S_svars(29),                   /* svars  */
    S_vars(30),                    /* vars  */
    S_tuples(31),                  /* tuples  */
    S_tuple(32),                   /* tuple  */
    S_literal(33),                 /* literal  */
    S_interval(34),                /* interval  */
    S_gtuples(35),                 /* gtuples  */
    S_filter(36),                  /* filter  */
    S_constraints(37),             /* constraints  */
    S_constraint(38),              /* constraint  */
    S_aggregate(39),               /* aggregate  */
    S_funcalls(40);                /* funcalls  */


    private final int yycode_;

    SymbolKind (int n) {
      this.yycode_ = n;
    }

    private static final SymbolKind[] values_ = {
      SymbolKind.S_YYEOF,
      SymbolKind.S_YYerror,
      SymbolKind.S_YYUNDEF,
      SymbolKind.S_ASK,
      SymbolKind.S_SELECT,
      SymbolKind.S_SELECTALL,
      SymbolKind.S_DISTINCT,
      SymbolKind.S_WHERE,
      SymbolKind.S_FILTER,
      SymbolKind.S_AGGREGATE,
      SymbolKind.S_INTERVALSTART,
      SymbolKind.S_INTERVALEND,
      SymbolKind.S_NOTEQ,
      SymbolKind.S_EQ,
      SymbolKind.S_URI,
      SymbolKind.S_BLANK,
      SymbolKind.S_VAR,
      SymbolKind.S_ATOM,
      SymbolKind.S_ID,
      SymbolKind.S_ALLENRELATION,
      SymbolKind.S_20_,
      SymbolKind.S_21_,
      SymbolKind.S_22_,
      SymbolKind.S_23_,
      SymbolKind.S_24_,
      SymbolKind.S_YYACCEPT,
      SymbolKind.S_query,
      SymbolKind.S_select,
      SymbolKind.S_exttuple,
      SymbolKind.S_svars,
      SymbolKind.S_vars,
      SymbolKind.S_tuples,
      SymbolKind.S_tuple,
      SymbolKind.S_literal,
      SymbolKind.S_interval,
      SymbolKind.S_gtuples,
      SymbolKind.S_filter,
      SymbolKind.S_constraints,
      SymbolKind.S_constraint,
      SymbolKind.S_aggregate,
      SymbolKind.S_funcalls
    };

    static final SymbolKind get(int code) {
      return values_[code];
    }

    public final int getCode() {
      return this.yycode_;
    }

    /* Return YYSTR after stripping away unnecessary quotes and
       backslashes, so that it's suitable for yyerror.  The heuristic is
       that double-quoting is unnecessary unless the string contains an
       apostrophe, a comma, or backslash (other than backslash-backslash).
       YYSTR is taken from yytname.  */
    private static String yytnamerr_(String yystr)
    {
      if (yystr.charAt (0) == '"')
        {
          StringBuffer yyr = new StringBuffer();
          strip_quotes: for (int i = 1; i < yystr.length(); i++)
            switch (yystr.charAt(i))
              {
              case '\'':
              case ',':
                break strip_quotes;

              case '\\':
                if (yystr.charAt(++i) != '\\')
                  break strip_quotes;
                /* Fall through.  */
              default:
                yyr.append(yystr.charAt(i));
                break;

              case '"':
                return yyr.toString();
              }
        }
      return yystr;
    }

    /* YYTNAME[SYMBOL-NUM] -- String name of the symbol SYMBOL-NUM.
       First, the terminals, then, starting at \a YYNTOKENS_, nonterminals.  */
    private static final String[] yytname_ = yytname_init();
  private static final String[] yytname_init()
  {
    return new String[]
    {
  "\"end of file\"", "error", "\"invalid token\"", "ASK", "SELECT",
  "SELECTALL", "DISTINCT", "WHERE", "FILTER", "AGGREGATE", "INTERVALSTART",
  "INTERVALEND", "NOTEQ", "EQ", "URI", "BLANK", "VAR", "ATOM", "ID",
  "ALLENRELATION", "'*'", "'&'", "','", "'!'", "'='", "$accept", "query",
  "select", "exttuple", "svars", "vars", "tuples", "tuple", "literal",
  "interval", "gtuples", "filter", "constraints", "constraint",
  "aggregate", "funcalls", null
    };
  }

    /* The user-facing name of this symbol.  */
    public final String getName() {
      return yytnamerr_(yytname_[yycode_]);
    }

  };


  /**
   * Communication interface between the scanner and the Bison-generated
   * parser <tt>QueryParser</tt>.
   */
  public interface Lexer {
    /* Token kinds.  */
    /** Token "end of file", to be returned by the scanner.  */
    static final int YYEOF = 0;
    /** Token error, to be returned by the scanner.  */
    static final int YYerror = 256;
    /** Token "invalid token", to be returned by the scanner.  */
    static final int YYUNDEF = 257;
    /** Token ASK, to be returned by the scanner.  */
    static final int ASK = 258;
    /** Token SELECT, to be returned by the scanner.  */
    static final int SELECT = 259;
    /** Token SELECTALL, to be returned by the scanner.  */
    static final int SELECTALL = 260;
    /** Token DISTINCT, to be returned by the scanner.  */
    static final int DISTINCT = 261;
    /** Token WHERE, to be returned by the scanner.  */
    static final int WHERE = 262;
    /** Token FILTER, to be returned by the scanner.  */
    static final int FILTER = 263;
    /** Token AGGREGATE, to be returned by the scanner.  */
    static final int AGGREGATE = 264;
    /** Token INTERVALSTART, to be returned by the scanner.  */
    static final int INTERVALSTART = 265;
    /** Token INTERVALEND, to be returned by the scanner.  */
    static final int INTERVALEND = 266;
    /** Token NOTEQ, to be returned by the scanner.  */
    static final int NOTEQ = 267;
    /** Token EQ, to be returned by the scanner.  */
    static final int EQ = 268;
    /** Token URI, to be returned by the scanner.  */
    static final int URI = 269;
    /** Token BLANK, to be returned by the scanner.  */
    static final int BLANK = 270;
    /** Token VAR, to be returned by the scanner.  */
    static final int VAR = 271;
    /** Token ATOM, to be returned by the scanner.  */
    static final int ATOM = 272;
    /** Token ID, to be returned by the scanner.  */
    static final int ID = 273;
    /** Token ALLENRELATION, to be returned by the scanner.  */
    static final int ALLENRELATION = 274;

    /** Deprecated, use YYEOF instead.  */
    public static final int EOF = YYEOF;

    /**
     * Method to retrieve the beginning position of the last scanned token.
     * @return the position at which the last scanned token starts.
     */
    Position getStartPos();

    /**
     * Method to retrieve the ending position of the last scanned token.
     * @return the first position beyond the last scanned token.
     */
    Position getEndPos();

    /**
     * Method to retrieve the semantic value of the last scanned token.
     * @return the semantic value of the last scanned token.
     */
    Object getLVal();

    /**
     * Entry point for the scanner.  Returns the token identifier corresponding
     * to the next token and prepares to return the semantic value
     * and beginning/ending positions of the token.
     * @return the token identifier corresponding to the next token.
     */
    int yylex() throws java.io.IOException, QueryParseException;

    /**
     * Emit an error referring to the given locationin a user-defined way.
     *
     * @param loc The location of the element to which the
     *                error message is related.
     * @param msg The string for the error message.
     */
     void yyerror(Location loc, String msg);


  }


  /**
   * The object doing lexical analysis for us.
   */
  private Lexer yylexer;





  /**
   * Instantiates the Bison-generated parser.
   * @param yylexer The scanner that will supply tokens to the parser.
   */
  public QueryParser(Lexer yylexer)
  {

    this.yylexer = yylexer;

  }



  private int yynerrs = 0;

  /**
   * The number of syntax errors so far.
   */
  public final int getNumberOfErrors() { return yynerrs; }

  /**
   * Print an error message via the lexer.
   * Use a <code>null</code> location.
   * @param msg The error message.
   */
  public final void yyerror(String msg) {
      yylexer.yyerror((Location)null, msg);
  }

  /**
   * Print an error message via the lexer.
   * @param loc The location associated with the message.
   * @param msg The error message.
   */
  public final void yyerror(Location loc, String msg) {
      yylexer.yyerror(loc, msg);
  }

  /**
   * Print an error message via the lexer.
   * @param pos The position associated with the message.
   * @param msg The error message.
   */
  public final void yyerror(Position pos, String msg) {
      yylexer.yyerror(new Location (pos), msg);
  }


  private final class YYStack {
    private int[] stateStack = new int[16];
    private Location[] locStack = new Location[16];
    private Object[] valueStack = new Object[16];

    public int size = 16;
    public int height = -1;

    public final void push(int state, Object value, Location loc) {
      height++;
      if (size == height) {
        int[] newStateStack = new int[size * 2];
        System.arraycopy(stateStack, 0, newStateStack, 0, height);
        stateStack = newStateStack;
        Location[] newLocStack = new Location[size * 2];
        System.arraycopy(locStack, 0, newLocStack, 0, height);
        locStack = newLocStack;

        Object[] newValueStack = new Object[size * 2];
        System.arraycopy(valueStack, 0, newValueStack, 0, height);
        valueStack = newValueStack;

        size *= 2;
      }

      stateStack[height] = state;
      locStack[height] = loc;
      valueStack[height] = value;
    }

    public final void pop() {
      pop(1);
    }

    public final void pop(int num) {
      // Avoid memory leaks... garbage collection is a white lie!
      if (0 < num) {
        java.util.Arrays.fill(valueStack, height - num + 1, height + 1, null);
        java.util.Arrays.fill(locStack, height - num + 1, height + 1, null);
      }
      height -= num;
    }

    public final int stateAt(int i) {
      return stateStack[height - i];
    }


    public final Location locationAt(int i) {
      return locStack[height - i];
    }

    public final Object valueAt(int i) {
      return valueStack[height - i];
    }

    // Print the state stack on the debug stream.
    public void print(java.io.PrintStream out) {
      out.print ("Stack now");

      for (int i = 0; i <= height; i++) {
        out.print(' ');
        out.print(stateStack[i]);
      }
      out.println();
    }
  }

  /**
   * Returned by a Bison action in order to stop the parsing process and
   * return success (<tt>true</tt>).
   */
  public static final int YYACCEPT = 0;

  /**
   * Returned by a Bison action in order to stop the parsing process and
   * return failure (<tt>false</tt>).
   */
  public static final int YYABORT = 1;



  /**
   * Returned by a Bison action in order to start error recovery without
   * printing an error message.
   */
  public static final int YYERROR = 2;

  /**
   * Internal return codes that are not supported for user semantic
   * actions.
   */
  private static final int YYERRLAB = 3;
  private static final int YYNEWSTATE = 4;
  private static final int YYDEFAULT = 5;
  private static final int YYREDUCE = 6;
  private static final int YYERRLAB1 = 7;
  private static final int YYRETURN = 8;


  private int yyerrstatus_ = 0;


  /**
   * Whether error recovery is being done.  In this state, the parser
   * reads token until it reaches a known state, and then restarts normal
   * operation.
   */
  public final boolean recovering ()
  {
    return yyerrstatus_ == 0;
  }

  /** Compute post-reduction state.
   * @param yystate   the current state
   * @param yysym     the nonterminal to push on the stack
   */
  private int yyLRGotoState(int yystate, int yysym) {
    int yyr = yypgoto_[yysym - YYNTOKENS_] + yystate;
    if (0 <= yyr && yyr <= YYLAST_ && yycheck_[yyr] == yystate)
      return yytable_[yyr];
    else
      return yydefgoto_[yysym - YYNTOKENS_];
  }

  private int yyaction(int yyn, YYStack yystack, int yylen)
  {
    /* If YYLEN is nonzero, implement the default value of the action:
       '$$ = $1'.  Otherwise, use the top of the stack.

       Otherwise, the following line sets YYVAL to garbage.
       This behavior is undocumented and Bison
       users should not rely upon it.  */
    Object yyval = (0 < yylen) ? yystack.valueAt(yylen - 1) : yystack.valueAt(0);
    Location yyloc = yylloc(yystack, yylen);

    switch (yyn)
      {
          case 2: /* query: select WHERE exttuple tuples filter aggregate  */
  if (yyn == 2)
    /* "QueryParser.y":153  */
                                                              {
                  ((LinkedList<List<String>>)(yystack.valueAt (2))).addFirst(((LinkedList<String>)(yystack.valueAt (3))));
                  for (List<String> l: ((LinkedList<List<String>>)(yystack.valueAt (2))))
                    forAllVars(l, (s) -> foundVars.add(s));
                  whereClauses = ((LinkedList<List<String>>)(yystack.valueAt (2)));
                };
  break;


  case 3: /* query: ASK gtuples  */
  if (yyn == 3)
    /* "QueryParser.y":159  */
                           {
                  // TODO
                };
  break;


  case 5: /* select: SELECT DISTINCT svars  */
  if (yyn == 5)
    /* "QueryParser.y":165  */
                                      { distinct = true; };
  break;


  case 6: /* select: SELECTALL svars  */
  if (yyn == 6)
    /* "QueryParser.y":166  */
                                { expandProxy = true; };
  break;


  case 7: /* select: SELECTALL DISTINCT svars  */
  if (yyn == 7)
    /* "QueryParser.y":167  */
                                         { distinct = expandProxy = true;};
  break;


  case 8: /* exttuple: tuple  */
  if (yyn == 8)
    /* "QueryParser.y":170  */
                      { yyval = ((LinkedList<String>)(yystack.valueAt (0))); };
  break;


  case 9: /* exttuple: interval tuple  */
  if (yyn == 9)
    /* "QueryParser.y":171  */
                               { ((List<String>)(yystack.valueAt (1))).addAll(((LinkedList<String>)(yystack.valueAt (0)))); yyval = ((List<String>)(yystack.valueAt (1))); };
  break;


  case 10: /* exttuple: tuple ALLENRELATION tuple  */
  if (yyn == 10)
    /* "QueryParser.y":172  */
                                          {
                  ((LinkedList<String>)(yystack.valueAt (2))).addAll(((LinkedList<String>)(yystack.valueAt (0))));
                  ((LinkedList<String>)(yystack.valueAt (2))).add((( String )(yystack.valueAt (1))));
                  yyval = ((LinkedList<String>)(yystack.valueAt (2)));
                };
  break;


  case 11: /* exttuple: interval tuple ALLENRELATION tuple  */
  if (yyn == 11)
    /* "QueryParser.y":177  */
                                                   {
                  ((List<String>)(yystack.valueAt (3))).addAll(((LinkedList<String>)(yystack.valueAt (2))));
                  ((List<String>)(yystack.valueAt (3))).addAll(((LinkedList<String>)(yystack.valueAt (0))));
                  ((List<String>)(yystack.valueAt (3))).add((( String )(yystack.valueAt (1))));
                  yyval = ((List<String>)(yystack.valueAt (3)));
                };
  break;


  case 12: /* svars: '*'  */
  if (yyn == 12)
    /* "QueryParser.y":185  */
                    {
                  LinkedList<String> arr = new LinkedList<>();
                  arr.add("*");
                  projectedVars.add("*");
                  yyval = arr;
                };
  break;


  case 13: /* svars: vars  */
  if (yyn == 13)
    /* "QueryParser.y":191  */
                     { projectedVars.addAll(((LinkedList<String>)(yystack.valueAt (0)))); yyval = ((LinkedList<String>)(yystack.valueAt (0))); };
  break;


  case 14: /* vars: vars VAR  */
  if (yyn == 14)
    /* "QueryParser.y":194  */
                         { ((LinkedList<String>)(yystack.valueAt (1))).add((( String )(yystack.valueAt (0)))); yyval = ((LinkedList<String>)(yystack.valueAt (1))); };
  break;


  case 15: /* vars: %empty  */
  if (yyn == 15)
    /* "QueryParser.y":195  */
                       { yyval = new LinkedList<>(); };
  break;


  case 16: /* tuples: '&' tuple tuples  */
  if (yyn == 16)
    /* "QueryParser.y":199  */
                                 { ((LinkedList<List<String>>)(yystack.valueAt (0))).addFirst(((LinkedList<String>)(yystack.valueAt (1)))); yyval = ((LinkedList<List<String>>)(yystack.valueAt (0))); };
  break;


  case 17: /* tuples: %empty  */
  if (yyn == 17)
    /* "QueryParser.y":200  */
                       { yyval = new LinkedList<>(); };
  break;


  case 18: /* tuple: literal tuple  */
  if (yyn == 18)
    /* "QueryParser.y":203  */
                              { ((LinkedList<String>)(yystack.valueAt (0))).addFirst(((String)(yystack.valueAt (1)))); yyval = ((LinkedList<String>)(yystack.valueAt (0))); };
  break;


  case 19: /* tuple: literal  */
  if (yyn == 19)
    /* "QueryParser.y":204  */
                        {
                  LinkedList<String>arr = new LinkedList<>();
                  arr.add(((String)(yystack.valueAt (0))));
                  yyval = arr;
                };
  break;


  case 20: /* literal: URI  */
  if (yyn == 20)
    /* "QueryParser.y":211  */
                    { yyval = (( String )(yystack.valueAt (0))); };
  break;


  case 21: /* literal: VAR  */
  if (yyn == 21)
    /* "QueryParser.y":212  */
                    { yyval = (( String )(yystack.valueAt (0))); };
  break;


  case 22: /* literal: BLANK  */
  if (yyn == 22)
    /* "QueryParser.y":213  */
                      { yyval = (( String )(yystack.valueAt (0))); };
  break;


  case 23: /* literal: ATOM  */
  if (yyn == 23)
    /* "QueryParser.y":214  */
                     { yyval = (( String )(yystack.valueAt (0))); };
  break;


  case 24: /* interval: INTERVALSTART ATOM ATOM INTERVALEND  */
  if (yyn == 24)
    /* "QueryParser.y":217  */
                                                    {
                  List<String> res = new LinkedList<>();
                  res.add((( String )(yystack.valueAt (2)))); res.add((( String )(yystack.valueAt (1))));
                  res.add("[]"); //the "relation"
                  yyval = res;
                };
  break;


  case 25: /* interval: INTERVALSTART ATOM ',' ATOM INTERVALEND  */
  if (yyn == 25)
    /* "QueryParser.y":223  */
                                                        {
                  List<String> res = new LinkedList<>();
                  res.add((( String )(yystack.valueAt (3)))); res.add((( String )(yystack.valueAt (1))));
                  res.add("[]"); //the "relation"
                  yyval = res;
                };
  break;


  case 26: /* gtuples: URI gtuples  */
  if (yyn == 26)
    /* "QueryParser.y":231  */
                            { ((LinkedList<String>)(yystack.valueAt (0))).addFirst((( String )(yystack.valueAt (1)))); yyval = ((LinkedList<String>)(yystack.valueAt (0))); };
  break;


  case 27: /* gtuples: ATOM gtuples  */
  if (yyn == 27)
    /* "QueryParser.y":232  */
                             { ((LinkedList<String>)(yystack.valueAt (0))).addFirst((( String )(yystack.valueAt (1)))); yyval = ((LinkedList<String>)(yystack.valueAt (0))); };
  break;


  case 28: /* gtuples: %empty  */
  if (yyn == 28)
    /* "QueryParser.y":233  */
                       { yyval = new LinkedList<>(); };
  break;


  case 29: /* filter: FILTER constraints  */
  if (yyn == 29)
    /* "QueryParser.y":236  */
                                   { filterClauses = ((List<List<String>>)(yystack.valueAt (0))); };
  break;


  case 30: /* filter: %empty  */
  if (yyn == 30)
    /* "QueryParser.y":237  */
                       { filterClauses = new ArrayList<List<String>>();; };
  break;


  case 31: /* constraints: constraints '&' constraint  */
  if (yyn == 31)
    /* "QueryParser.y":240  */
                                           { ((List<List<String>>)(yystack.valueAt (2))).add(((List<String>)(yystack.valueAt (0)))); yyval = ((List<List<String>>)(yystack.valueAt (2))); };
  break;


  case 32: /* constraints: constraint  */
  if (yyn == 32)
    /* "QueryParser.y":241  */
                           {
                  List<List<String>> arr = new ArrayList<>();
                  arr.add(((List<String>)(yystack.valueAt (0))));
                  yyval = arr;
                };
  break;


  case 33: /* constraint: VAR NOTEQ literal  */
  if (yyn == 33)
    /* "QueryParser.y":248  */
                                  {
                  filterVars.add((( String )(yystack.valueAt (2))));
                  if (RuleStore.isVariable(((String)(yystack.valueAt (0))))) {
                    filterVars.add(((String)(yystack.valueAt (0))));
                  }
                  List<String> c = new LinkedList<String>();
                  c.add((( String )(yystack.valueAt (2)))); //c.add($2); // hmm, how do i distinguish from ==?
                  c.add(((String)(yystack.valueAt (0))));
                  yyval = c;
                };
  break;


  case 34: /* constraint: VAR EQ literal  */
  if (yyn == 34)
    /* "QueryParser.y":258  */
                               {
                  filterVars.add((( String )(yystack.valueAt (2))));
                  if (RuleStore.isVariable(((String)(yystack.valueAt (0))))) {
                    filterVars.add(((String)(yystack.valueAt (0))));
                  }
                  List<String> c = new LinkedList<String>();
                  c.add((( String )(yystack.valueAt (2)))); c.add(((String)(yystack.valueAt (1)))); c.add(((String)(yystack.valueAt (0))));
                  yyval = c;
                };
  break;


  case 35: /* constraint: ID tuple  */
  if (yyn == 35)
    /* "QueryParser.y":267  */
                         {
                  forAllVars(((LinkedList<String>)(yystack.valueAt (0))), (s) -> filterVars.add(s));
                  ((LinkedList<String>)(yystack.valueAt (0))).addFirst((( String )(yystack.valueAt (1))));
                  yyval = ((LinkedList<String>)(yystack.valueAt (0)));
                };
  break;


  case 36: /* constraint: '!' ID tuple  */
  if (yyn == 36)
    /* "QueryParser.y":272  */
                             {
                  forAllVars(((LinkedList<String>)(yystack.valueAt (0))), (s) -> filterVars.add(s));
                  ((LinkedList<String>)(yystack.valueAt (0))).addFirst((( String )(yystack.valueAt (1)))); ((LinkedList<String>)(yystack.valueAt (0))).addFirst("NOT");
                  yyval = ((LinkedList<String>)(yystack.valueAt (0)));
                };
  break;


  case 37: /* constraint: VAR ID tuple  */
  if (yyn == 37)
    /* "QueryParser.y":277  */
                             {
                  forAllVars(((LinkedList<String>)(yystack.valueAt (0))), (s) -> filterVars.add(s));
                  filterVars.add((( String )(yystack.valueAt (2))));
                  ((LinkedList<String>)(yystack.valueAt (0))).addFirst((( String )(yystack.valueAt (1))));
                  ((LinkedList<String>)(yystack.valueAt (0))).addFirst((( String )(yystack.valueAt (2))));
                  yyval = ((LinkedList<String>)(yystack.valueAt (0)));
                };
  break;


  case 38: /* constraint: '!' VAR ID tuple  */
  if (yyn == 38)
    /* "QueryParser.y":284  */
                                 {
                  forAllVars(((LinkedList<String>)(yystack.valueAt (0))), (s) -> filterVars.add(s));
                  filterVars.add((( String )(yystack.valueAt (2))));
                  ((LinkedList<String>)(yystack.valueAt (0))).addFirst((( String )(yystack.valueAt (1)))); ((LinkedList<String>)(yystack.valueAt (0))).addFirst((( String )(yystack.valueAt (2)))); ((LinkedList<String>)(yystack.valueAt (0))).addFirst("NOT");
                  yyval = ((LinkedList<String>)(yystack.valueAt (0)));
                };
  break;


  case 39: /* aggregate: AGGREGATE funcalls  */
  if (yyn == 39)
    /* "QueryParser.y":292  */
                                   { aggregateClauses = ((LinkedList<List<String>>)(yystack.valueAt (0))); };
  break;


  case 40: /* aggregate: %empty  */
  if (yyn == 40)
    /* "QueryParser.y":293  */
                       { aggregateClauses = Collections.emptyList(); };
  break;


  case 41: /* funcalls: vars '=' ID tuple '&' funcalls  */
  if (yyn == 41)
    /* "QueryParser.y":296  */
                                               {
                  forAllVars(((LinkedList<String>)(yystack.valueAt (2))), (s) -> foundVars.add(s));
                  aggregateVars.addAll(((LinkedList<String>)(yystack.valueAt (5))));
                  LinkedList<String> agg = new LinkedList<>();
                  agg.addAll(((LinkedList<String>)(yystack.valueAt (5))));
                  agg.add("=");
                  agg.add((( String )(yystack.valueAt (3))));
                  agg.addAll(((LinkedList<String>)(yystack.valueAt (2))));
                  ((LinkedList<List<String>>)(yystack.valueAt (0))).addFirst(agg);
                  yyval = ((LinkedList<List<String>>)(yystack.valueAt (0)));
                };
  break;


  case 42: /* funcalls: vars '=' ID tuple  */
  if (yyn == 42)
    /* "QueryParser.y":307  */
                                  {
                  forAllVars(((LinkedList<String>)(yystack.valueAt (0))), (s) -> foundVars.add(s));
                  aggregateVars.addAll(((LinkedList<String>)(yystack.valueAt (3))));
                  LinkedList<String> agg = new LinkedList<>();
                  agg.addAll(((LinkedList<String>)(yystack.valueAt (3))));
                  agg.add("=");
                  agg.add((( String )(yystack.valueAt (1))));
                  agg.addAll(((LinkedList<String>)(yystack.valueAt (0))));
                  LinkedList<List<String>> res = new LinkedList<>();
                  res.add(agg);
                  yyval = res;
                };
  break;



/* "QueryParser.java":978  */

        default: break;
      }

    yystack.pop(yylen);
    yylen = 0;
    /* Shift the result of the reduction.  */
    int yystate = yyLRGotoState(yystack.stateAt(0), yyr1_[yyn]);
    yystack.push(yystate, yyval, yyloc);
    return YYNEWSTATE;
  }




  /**
   * Parse input from the scanner that was specified at object construction
   * time.  Return whether the end of the input was reached successfully.
   *
   * @return <tt>true</tt> if the parsing succeeds.  Note that this does not
   *          imply that there were no syntax errors.
   */
  public boolean parse() throws java.io.IOException, QueryParseException

  {
    /* @$.  */
    Location yyloc;


    /* Lookahead token kind.  */
    int yychar = YYEMPTY_;
    /* Lookahead symbol kind.  */
    SymbolKind yytoken = null;

    /* State.  */
    int yyn = 0;
    int yylen = 0;
    int yystate = 0;
    YYStack yystack = new YYStack ();
    int label = YYNEWSTATE;


    /* The location where the error started.  */
    Location yyerrloc = null;

    /* Location. */
    Location yylloc = new Location (null, null);

    /* Semantic value of the lookahead.  */
    Object yylval = null;



    yyerrstatus_ = 0;
    yynerrs = 0;

    /* Initialize the stack.  */
    yystack.push (yystate, yylval, yylloc);



    for (;;)
      switch (label)
      {
        /* New state.  Unlike in the C/C++ skeletons, the state is already
           pushed when we come here.  */
      case YYNEWSTATE:

        /* Accept?  */
        if (yystate == YYFINAL_)
          return true;

        /* Take a decision.  First try without lookahead.  */
        yyn = yypact_[yystate];
        if (yyPactValueIsDefault (yyn))
          {
            label = YYDEFAULT;
            break;
          }

        /* Read a lookahead token.  */
        if (yychar == YYEMPTY_)
          {

            yychar = yylexer.yylex ();
            yylval = yylexer.getLVal();
            yylloc = new Location(yylexer.getStartPos(),
                                          yylexer.getEndPos());

          }

        /* Convert token to internal form.  */
        yytoken = yytranslate_ (yychar);

        if (yytoken == SymbolKind.S_YYerror)
          {
            // The scanner already issued an error message, process directly
            // to error recovery.  But do not keep the error token as
            // lookahead, it is too special and may lead us to an endless
            // loop in error recovery. */
            yychar = Lexer.YYUNDEF;
            yytoken = SymbolKind.S_YYUNDEF;
            yyerrloc = yylloc;
            label = YYERRLAB1;
          }
        else
          {
            /* If the proper action on seeing token YYTOKEN is to reduce or to
               detect an error, take that action.  */
            yyn += yytoken.getCode();
            if (yyn < 0 || YYLAST_ < yyn || yycheck_[yyn] != yytoken.getCode()) {
              label = YYDEFAULT;
            }

            /* <= 0 means reduce or error.  */
            else if ((yyn = yytable_[yyn]) <= 0)
              {
                if (yyTableValueIsError(yyn)) {
                  label = YYERRLAB;
                } else {
                  yyn = -yyn;
                  label = YYREDUCE;
                }
              }

            else
              {
                /* Shift the lookahead token.  */
                /* Discard the token being shifted.  */
                yychar = YYEMPTY_;

                /* Count tokens shifted since error; after three, turn off error
                   status.  */
                if (yyerrstatus_ > 0)
                  --yyerrstatus_;

                yystate = yyn;
                yystack.push(yystate, yylval, yylloc);
                label = YYNEWSTATE;
              }
          }
        break;

      /*-----------------------------------------------------------.
      | yydefault -- do the default action for the current state.  |
      `-----------------------------------------------------------*/
      case YYDEFAULT:
        yyn = yydefact_[yystate];
        if (yyn == 0)
          label = YYERRLAB;
        else
          label = YYREDUCE;
        break;

      /*-----------------------------.
      | yyreduce -- Do a reduction.  |
      `-----------------------------*/
      case YYREDUCE:
        yylen = yyr2_[yyn];
        label = yyaction(yyn, yystack, yylen);
        yystate = yystack.stateAt(0);
        break;

      /*------------------------------------.
      | yyerrlab -- here on detecting error |
      `------------------------------------*/
      case YYERRLAB:
        /* If not already recovering from an error, report this error.  */
        if (yyerrstatus_ == 0)
          {
            ++yynerrs;
            if (yychar == YYEMPTY_)
              yytoken = null;
            yyreportSyntaxError(new Context(this, yystack, yytoken, yylloc));
          }

        yyerrloc = yylloc;
        if (yyerrstatus_ == 3)
          {
            /* If just tried and failed to reuse lookahead token after an
               error, discard it.  */

            if (yychar <= Lexer.YYEOF)
              {
                /* Return failure if at end of input.  */
                if (yychar == Lexer.YYEOF)
                  return false;
              }
            else
              yychar = YYEMPTY_;
          }

        /* Else will try to reuse lookahead token after shifting the error
           token.  */
        label = YYERRLAB1;
        break;

      /*-------------------------------------------------.
      | errorlab -- error raised explicitly by YYERROR.  |
      `-------------------------------------------------*/
      case YYERROR:
        yyerrloc = yystack.locationAt (yylen - 1);
        /* Do not reclaim the symbols of the rule which action triggered
           this YYERROR.  */
        yystack.pop (yylen);
        yylen = 0;
        yystate = yystack.stateAt(0);
        label = YYERRLAB1;
        break;

      /*-------------------------------------------------------------.
      | yyerrlab1 -- common code for both syntax error and YYERROR.  |
      `-------------------------------------------------------------*/
      case YYERRLAB1:
        yyerrstatus_ = 3;       /* Each real token shifted decrements this.  */

        // Pop stack until we find a state that shifts the error token.
        for (;;)
          {
            yyn = yypact_[yystate];
            if (!yyPactValueIsDefault (yyn))
              {
                yyn += SymbolKind.S_YYerror.getCode();
                if (0 <= yyn && yyn <= YYLAST_
                    && yycheck_[yyn] == SymbolKind.S_YYerror.getCode())
                  {
                    yyn = yytable_[yyn];
                    if (0 < yyn)
                      break;
                  }
              }

            /* Pop the current state because it cannot handle the
             * error token.  */
            if (yystack.height == 0)
              return false;


            yyerrloc = yystack.locationAt (0);
            yystack.pop ();
            yystate = yystack.stateAt(0);
          }

        if (label == YYABORT)
          /* Leave the switch.  */
          break;


        /* Muck with the stack to setup for yylloc.  */
        yystack.push (0, null, yylloc);
        yystack.push (0, null, yyerrloc);
        yyloc = yylloc (yystack, 2);
        yystack.pop (2);

        /* Shift the error token.  */

        yystate = yyn;
        yystack.push (yyn, yylval, yyloc);
        label = YYNEWSTATE;
        break;

        /* Accept.  */
      case YYACCEPT:
        return true;

        /* Abort.  */
      case YYABORT:
        return false;
      }
}




  /**
   * Information needed to get the list of expected tokens and to forge
   * a syntax error diagnostic.
   */
  public static final class Context {
    Context(QueryParser parser, YYStack stack, SymbolKind token, Location loc) {
      yyparser = parser;
      yystack = stack;
      yytoken = token;
      yylocation = loc;
    }

    private QueryParser yyparser;
    private YYStack yystack;


    /**
     * The symbol kind of the lookahead token.
     */
    public final SymbolKind getToken() {
      return yytoken;
    }

    private SymbolKind yytoken;

    /**
     * The location of the lookahead.
     */
    public final Location getLocation() {
      return yylocation;
    }

    private Location yylocation;
    static final int NTOKENS = QueryParser.YYNTOKENS_;

    /**
     * Put in YYARG at most YYARGN of the expected tokens given the
     * current YYCTX, and return the number of tokens stored in YYARG.  If
     * YYARG is null, return the number of expected tokens (guaranteed to
     * be less than YYNTOKENS).
     */
    int getExpectedTokens(SymbolKind yyarg[], int yyargn) {
      return getExpectedTokens (yyarg, 0, yyargn);
    }

    int getExpectedTokens(SymbolKind yyarg[], int yyoffset, int yyargn) {
      int yycount = yyoffset;
      int yyn = yypact_[this.yystack.stateAt(0)];
      if (!yyPactValueIsDefault(yyn))
        {
          /* Start YYX at -YYN if negative to avoid negative
             indexes in YYCHECK.  In other words, skip the first
             -YYN actions for this state because they are default
             actions.  */
          int yyxbegin = yyn < 0 ? -yyn : 0;
          /* Stay within bounds of both yycheck and yytname.  */
          int yychecklim = YYLAST_ - yyn + 1;
          int yyxend = yychecklim < NTOKENS ? yychecklim : NTOKENS;
          for (int yyx = yyxbegin; yyx < yyxend; ++yyx)
            if (yycheck_[yyx + yyn] == yyx && yyx != SymbolKind.S_YYerror.getCode()
                && !yyTableValueIsError(yytable_[yyx + yyn]))
              {
                if (yyarg == null)
                  yycount += 1;
                else if (yycount == yyargn)
                  return 0; // FIXME: this is incorrect.
                else
                  yyarg[yycount++] = SymbolKind.get(yyx);
              }
        }
      if (yyarg != null && yycount == yyoffset && yyoffset < yyargn)
        yyarg[yycount] = null;
      return yycount - yyoffset;
    }
  }




  private int yysyntaxErrorArguments(Context yyctx, SymbolKind[] yyarg, int yyargn) {
    /* There are many possibilities here to consider:
       - If this state is a consistent state with a default action,
         then the only way this function was invoked is if the
         default action is an error action.  In that case, don't
         check for expected tokens because there are none.
       - The only way there can be no lookahead present (in tok) is
         if this state is a consistent state with a default action.
         Thus, detecting the absence of a lookahead is sufficient to
         determine that there is no unexpected or expected token to
         report.  In that case, just report a simple "syntax error".
       - Don't assume there isn't a lookahead just because this
         state is a consistent state with a default action.  There
         might have been a previous inconsistent state, consistent
         state with a non-default action, or user semantic action
         that manipulated yychar.  (However, yychar is currently out
         of scope during semantic actions.)
       - Of course, the expected token list depends on states to
         have correct lookahead information, and it depends on the
         parser not to perform extra reductions after fetching a
         lookahead from the scanner and before detecting a syntax
         error.  Thus, state merging (from LALR or IELR) and default
         reductions corrupt the expected token list.  However, the
         list is correct for canonical LR with one exception: it
         will still contain any token that will not be accepted due
         to an error action in a later state.
    */
    int yycount = 0;
    if (yyctx.getToken() != null)
      {
        if (yyarg != null)
          yyarg[yycount] = yyctx.getToken();
        yycount += 1;
        yycount += yyctx.getExpectedTokens(yyarg, 1, yyargn);
      }
    return yycount;
  }


  /**
   * Build and emit a "syntax error" message in a user-defined way.
   *
   * @param ctx  The context of the error.
   */
  private void yyreportSyntaxError(Context yyctx) {
      if (yyErrorVerbose) {
          final int argmax = 5;
          SymbolKind[] yyarg = new SymbolKind[argmax];
          int yycount = yysyntaxErrorArguments(yyctx, yyarg, argmax);
          String[] yystr = new String[yycount];
          for (int yyi = 0; yyi < yycount; ++yyi) {
              yystr[yyi] = yyarg[yyi].getName();
          }
          String yyformat;
          switch (yycount) {
              default:
              case 0: yyformat = "syntax error"; break;
              case 1: yyformat = "syntax error, unexpected {0}"; break;
              case 2: yyformat = "syntax error, unexpected {0}, expecting {1}"; break;
              case 3: yyformat = "syntax error, unexpected {0}, expecting {1} or {2}"; break;
              case 4: yyformat = "syntax error, unexpected {0}, expecting {1} or {2} or {3}"; break;
              case 5: yyformat = "syntax error, unexpected {0}, expecting {1} or {2} or {3} or {4}"; break;
          }
          yyerror(yyctx.yylocation, new MessageFormat(yyformat).format(yystr));
      } else {
          yyerror(yyctx.yylocation, "syntax error");
      }
  }

  /**
   * Whether the given <code>yypact_</code> value indicates a defaulted state.
   * @param yyvalue   the value to check
   */
  private static boolean yyPactValueIsDefault(int yyvalue) {
    return yyvalue == yypact_ninf_;
  }

  /**
   * Whether the given <code>yytable_</code>
   * value indicates a syntax error.
   * @param yyvalue the value to check
   */
  private static boolean yyTableValueIsError(int yyvalue) {
    return yyvalue == yytable_ninf_;
  }

  private static final byte yypact_ninf_ = -51;
  private static final byte yytable_ninf_ = -1;

/* YYPACT[STATE-NUM] -- Index in YYTABLE of the portion describing
   STATE-NUM.  */
  private static final byte[] yypact_ = yypact_init();
  private static final byte[] yypact_init()
  {
    return new byte[]
    {
      42,    10,    -2,     0,    22,    31,    10,    10,   -51,    13,
     -51,   -51,    33,    13,   -51,   -51,    20,   -51,   -51,   -51,
     -51,   -51,    36,   -51,   -51,   -51,   -51,    34,    35,    26,
      26,     9,    26,    48,    26,   -51,    38,    47,    43,    34,
       5,    50,   -51,    26,   -51,    51,   -51,    -3,    26,    32,
      40,   -51,   -51,   -51,   -51,   -51,    26,    26,    26,   -51,
      46,    26,     5,    -8,   -51,   -51,   -51,   -51,    26,   -51,
     -51,    49,   -51,    26,    44,   -51,   -51
    };
  }

/* YYDEFACT[STATE-NUM] -- Default reduction number in state STATE-NUM.
   Performed when YYTABLE does not specify something else to do.  Zero
   means the default is an error.  */
  private static final byte[] yydefact_ = yydefact_init();
  private static final byte[] yydefact_init()
  {
    return new byte[]
    {
       0,    28,    15,    15,     0,     0,    28,    28,     3,    15,
      12,     4,    13,    15,     6,     1,     0,    26,    27,     5,
      14,     7,     0,    20,    22,    21,    23,    17,     8,    19,
       0,     0,     0,    30,     0,    18,     9,     0,     0,    17,
       0,    40,    10,     0,    24,     0,    16,     0,     0,     0,
      29,    32,    15,     2,    11,    25,     0,     0,     0,    35,
       0,     0,     0,     0,    39,    33,    34,    37,     0,    36,
      31,     0,    38,     0,    42,    15,    41
    };
  }

/* YYPGOTO[NTERM-NUM].  */
  private static final byte[] yypgoto_ = yypgoto_init();
  private static final byte[] yypgoto_init()
  {
    return new byte[]
    {
     -51,   -51,   -51,   -51,     4,   -50,    24,   -29,   -45,   -51,
      45,   -51,   -51,     6,   -51,    -9
    };
  }

/* YYDEFGOTO[NTERM-NUM].  */
  private static final byte[] yydefgoto_ = yydefgoto_init();
  private static final byte[] yydefgoto_init()
  {
    return new byte[]
    {
       0,     4,     5,    27,    11,    12,    33,    28,    29,    30,
       8,    41,    50,    51,    53,    64
    };
  }

/* YYTABLE[YYPACT[STATE-NUM]] -- What to do in state STATE-NUM.  If
   positive, shift that token.  If negative, reduce the rule whose
   number is the opposite.  If YYTABLE_NINF, syntax error.  */
  private static final byte[] yytable_ = yytable_init();
  private static final byte[] yytable_init()
  {
    return new byte[]
    {
      35,    36,    63,    39,     9,    42,    13,    14,    20,    56,
      57,    65,    66,    19,    54,    58,    71,    21,    10,    59,
      10,    47,    15,    48,     6,    63,    37,     7,    49,    67,
      22,    38,    69,    10,    23,    24,    25,    26,    16,    72,
      23,    24,    25,    26,    74,     1,     2,     3,    60,    20,
      61,    17,    18,    31,    34,    32,    40,    43,    44,    52,
      45,    62,    55,    46,    68,    75,    76,    73,    70
    };
  }

private static final byte[] yycheck_ = yycheck_init();
  private static final byte[] yycheck_init()
  {
    return new byte[]
    {
      29,    30,    52,    32,     6,    34,     6,     3,    16,    12,
      13,    56,    57,     9,    43,    18,    24,    13,    20,    48,
      20,    16,     0,    18,    14,    75,    17,    17,    23,    58,
      10,    22,    61,    20,    14,    15,    16,    17,     7,    68,
      14,    15,    16,    17,    73,     3,     4,     5,    16,    16,
      18,     6,     7,    17,    19,    21,     8,    19,    11,     9,
      17,    21,    11,    39,    18,    21,    75,    18,    62
    };
  }

/* YYSTOS[STATE-NUM] -- The symbol kind of the accessing symbol of
   state STATE-NUM.  */
  private static final byte[] yystos_ = yystos_init();
  private static final byte[] yystos_init()
  {
    return new byte[]
    {
       0,     3,     4,     5,    26,    27,    14,    17,    35,     6,
      20,    29,    30,     6,    29,     0,     7,    35,    35,    29,
      16,    29,    10,    14,    15,    16,    17,    28,    32,    33,
      34,    17,    21,    31,    19,    32,    32,    17,    22,    32,
       8,    36,    32,    19,    11,    17,    31,    16,    18,    23,
      37,    38,     9,    39,    32,    11,    12,    13,    18,    32,
      16,    18,    21,    30,    40,    33,    33,    32,    18,    32,
      38,    24,    32,    18,    32,    21,    40
    };
  }

/* YYR1[RULE-NUM] -- Symbol kind of the left-hand side of rule RULE-NUM.  */
  private static final byte[] yyr1_ = yyr1_init();
  private static final byte[] yyr1_init()
  {
    return new byte[]
    {
       0,    25,    26,    26,    27,    27,    27,    27,    28,    28,
      28,    28,    29,    29,    30,    30,    31,    31,    32,    32,
      33,    33,    33,    33,    34,    34,    35,    35,    35,    36,
      36,    37,    37,    38,    38,    38,    38,    38,    38,    39,
      39,    40,    40
    };
  }

/* YYR2[RULE-NUM] -- Number of symbols on the right-hand side of rule RULE-NUM.  */
  private static final byte[] yyr2_ = yyr2_init();
  private static final byte[] yyr2_init()
  {
    return new byte[]
    {
       0,     2,     6,     2,     2,     3,     2,     3,     1,     2,
       3,     4,     1,     1,     2,     0,     3,     0,     2,     1,
       1,     1,     1,     1,     4,     5,     2,     2,     0,     2,
       0,     3,     1,     3,     3,     2,     3,     3,     4,     2,
       0,     6,     4
    };
  }




  /* YYTRANSLATE_(TOKEN-NUM) -- Symbol number corresponding to TOKEN-NUM
     as returned by yylex, with out-of-bounds checking.  */
  private static final SymbolKind yytranslate_(int t)
  {
    // Last valid token kind.
    int code_max = 274;
    if (t <= 0)
      return SymbolKind.S_YYEOF;
    else if (t <= code_max)
      return SymbolKind.get(yytranslate_table_[t]);
    else
      return SymbolKind.S_YYUNDEF;
  }
  private static final byte[] yytranslate_table_ = yytranslate_table_init();
  private static final byte[] yytranslate_table_init()
  {
    return new byte[]
    {
       0,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,    23,     2,     2,     2,     2,    21,     2,
       2,     2,    20,     2,    22,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,    24,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     1,     2,     3,     4,
       5,     6,     7,     8,     9,    10,    11,    12,    13,    14,
      15,    16,    17,    18,    19
    };
  }


  private static final int YYLAST_ = 68;
  private static final int YYEMPTY_ = -2;
  private static final int YYFINAL_ = 15;
  private static final int YYNTOKENS_ = 25;

/* Unqualified %code blocks.  */
/* "QueryParser.y":45  */

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

/* "QueryParser.java":1701  */

}
/* "QueryParser.y":383  */


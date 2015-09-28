package de.dfki.lt.hfc;

/**
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Tue Sep 15 15:17:29 CEST 2015
 */


/**
 * Version Roadmap HFC (curent version: 6.0.8)
 * ===========================================
 *
 * v0.0 implement building blocks
 * v0.1 tuple store implementation
 * v0.2 local LHS matching
 * v0.3 global LHS matching
 *
 * v1.0 first running completed prototype
 * v1.1 RHS instantiation: minimal RDL rules with existential variables ("?" prefix) implemented
 * v1.2 inequality constraints added
 * v1.3 rule reordering, mega clustering
 *
 * v2.0 extension, performance issues, etc.
 * v2.1 parallel execution of rules
 * v2.2 additional tuple/rule uploading, further functionality
 *
 * v3.0 QDLv1.0 language (QDL--)
 * v3.1 first prototype
 * v3.1 interactive mode
 * v3.2 LHS predicates (@test)
 * 
 * v4.0 further functionality & improvements 1
 * v4.1 RHS functions (@action)
 * v4.2 _concrete_ predicates & functions implemented under de.dfki.lt.hfc.operators;
 * v4.3 QDLv2.0 language (QDL)
 * v4.4 QDLv2.1 language
 * v4.5 QDLv2.2 language
 * v4.6 additional API & config functionality
 * v4.7 QDLv2.3 language
 * v4.8 rule priorities added
 * v4.9 choice points/copies of FC implemented
 *
 * v5.0 further functionality & improvements 2
 * v5.1 equivalence class reduction (sameAs, equivalentClass, equivalentProperty)
 * v5.2 functionality implemented, axiomatic triples, rule files
 * v5.3 QDLv2.4: SELECTALL
 * v5.4 Java XMLRPC-server, Java client
 * v5.5 temporal entailment rules added
 * v5.6 tuple deletion added                          
 * v5.7 all 8 XSD temporal data types implemented
 * v5.8 add/remove transaction functionality
 *
 * v6.0 RDL relational variables ("??" prefix) implemented for @test section   <<<======== WE ARE HERE AT THE MOMENT + minor improvements
 * v6.1 RDL relational variables ("??" prefix) implemented for @action section
 * v6.2 additional entended OWL rule sets, including cardinality restrictions,
 *      negation, and temporal entailment, using the new RDL descriptive means
 *
 *
 * *** version numbering from here on might change ***
 *
 * v7.0 QDL v3.0 language (QDL++)
 *
 * v8.0 efficient native support for transitive, symmetric and inverse properties
 *
 * v9.0 listener architecture w/ anytime behavior, SDL support, streaming
 * ...
 * v?.x database implementation (new strand)
 */



/**
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
 * note: the reserved keywords ASK, SELECT, SELECTALL, DISTINCT, WHERE, FILTER, and AGGREGATE need
 *       _not_ be written uppercase
 * note: it is required that neither filter predicates nor aggregate functions have the same name
 *       as the above reserved keywords
 */

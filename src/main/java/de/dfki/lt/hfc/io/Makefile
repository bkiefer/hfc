ROOT       = $(HOME)/src/external/jflex
#JFLEX      = $(ROOT)/jflex/bin/jflex
JFLEX	   = jflex
JAVA       = java
JAVAC      = javac
BISON	   = bison


# --------------------------------------------------

#%.java : %.y
#	$(BISON) -Wall -rall --language=java --debug $<
%.java : %.y
	$(BISON) -Wall -rall --language=java $<

%.java: %.flex
	$(JFLEX) $<

all: QueryParser.java QueryLexer.java QueryFlexParser.java ../TupleParser.java

clean:
	rm -f *.class
	rm -f *~
	rm -f QueryParser.java QueryLexer.java QueryFlexParser.java ../TupleParser.java
	rm -f QueryParser.output

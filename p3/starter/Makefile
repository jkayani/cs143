
ASSN = 4J
CLASS= cs143
CLASSDIR= ../..

SRC= cool-tree.java CoolAnalysis.java 
CSRC = \
	ASTConstants.java \
	ASTLexer.java \
	ASTParser.java \
	AbstractSymbol.java \
	AbstractTable.java \
	Flags.java \
	IdSymbol.java \
	IdTable.java \
	IntTable.java \
	ListNode.java \
	StringTable.java \
	SymbolTable.java \
	SymtabExample.java \
	TokenConstants.java \
	TreeNode.java \
	Utilities.java

TSRC= mycoolc cool-tree.aps
CGEN= 
LIBS= lexer parser cgen
CFIL= ClassTable.java TreeConstants.java IntSymbol.java StringSymbol.java BoolConst.java Semant.java
HFIL= 
LSRC= Makefile
CLS= ${CFIL:.java=.class}
OUTPUT= example.output

JAVAC := javac

# rt.jar yet again
CLASSPATH := ${CLASSDIR}/lib:.:/usr/java/lib/rt.jar:/usr/class/lib/java_cup/runtime/

source: ${SRC} ${TSRC} ${LSRC} ${LIBS} ${CSRC}

${OUTPUT}: semant
	@rm -f ${OUTPUT}
	./mycoolc  example.cl >example.output 2>&1 

semant: Makefile java-source
	@rm -f semant
	echo '#!/bin/sh' >> semant
	echo 'java -classpath ${CLASSPATH} Semant $$*' >> semant
	chmod 755 semant

symtab-example: Makefile ${CLS}
	@rm -f symtab-example
	echo '#!/bin/sh' >> symtab-example
	echo 'java -classpath ${CLASSPATH} SymtabExample $$*' >> symtab-example
	chmod 755 symtab-example

dotest:	cgen example.cl
	@echo "\nRunning code generator on example.cl\n"
	-./mycoolc example.cl

## cool-tree.class is not a real class file, but we need to have it
## for dependency tracking
java-source: 
	${JAVAC} -g -nowarn -classpath ${CLASSPATH} ${CFIL}
	${JAVAC} -g -nowarn -classpath ${CLASSPATH} ${SRC}
	touch cool-tree.class

${LIBS}:
	${CLASSDIR}/etc/link-object ${ASSN} $@

# These dependencies allow you to get the starting files for
# the assignment.  They will not overwrite a file you already have.

${LSRC} :
	-${CLASSDIR}/etc/link-shared ${ASSN} ${LSRC}

${TSRC} ${CSRC}:
	-ln -s ${CLASSDIR}/src/PA${ASSN}/$@ $@

${HSRC}:
	-ln -s ${CLASSDIR}/include/PA${ASSN}/$@ $@

submit-clean: ${OUTPUT}
	-rm -f *.s core ${CLS} *~ ${CGEN} parser cgen semant lexer symtab-example

clean :
	-rm -f ${OUTPUT} *.s core ${CLS} ${CGEN} parser cgen semant lexer symtab-example *~ 





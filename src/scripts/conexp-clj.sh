#!/bin/bash

CONEXP_CLJ_HOME=$(dirname $0)/../
CONEXP_CLJ_JARS=.
for jar in clojure.jar clojure-contrib.jar G.jar jline.jar conexp-clj.jar
do
  CONEXP_CLJ_JARS=${CONEXP_CLJ_JARS}:${CONEXP_CLJ_HOME}/lib/${jar}
done

CONEXP_CLJ_CONSOLE_INIT=${CONEXP_CLJ_HOME}/lib/conexp-clj-console-init.clj

java -cp ${CONEXP_CLJ_JARS} jline.ConsoleRunner clojure.lang.Repl ${CONEXP_CLJ_CONSOLE_INIT} $*

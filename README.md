# GATE Framework

This project contains a number of modules for exposing GATE processing resources as services that produces LIF output.

![Java CI with Maven](https://github.com/lappsgrid-incubator/gate-framework/workflows/Java%20CI%20with%20Maven/badge.svg)

## Modules

All of the programs expect [GATE Embedded]() to be installed on the local system and most modules provide the GATE system and required plugins.  The required GATE plugins are also available from the [Lappsgrid download page](https://downloads.lappsgrid.org).
 
Snapshot versions for the library modules, `gate-core`, `gate-abner`, `gate-timeml`, et al are deployed to the Sonatype Snapshot repository and will eventuall be deployed to Maven Central.

### gate-core
Provides abstract base classes that perform all the heavy lifting of initializing the GATE subsystem and running the processing resources.

### gate-abner
The GATE AbnerTagger.

### gate-abner-cli
A command line wrapper for the GATE Abner Tagger. The CLI program can process plain text, LIF, or GATE/XML documents and produces LIF or GATE/XML.

### gate-timeml
TBD

### gate-heideltime
TBD

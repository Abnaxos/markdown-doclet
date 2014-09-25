Pegdown Doclet for IDEA
=======================

This is a plugin for IDEA that enables *Ctrl-Q* in projects using Pegdown for JavaDocs.


Installing the Plugin
---------------------

Simply install it from the Plugin manager.


Setting up the Development Environment
--------------------------------------

 *  Setup a plugin development environment as described [here](http://www.jetbrains.org/display/IJOS/Writing+Plug-ins).
 
 *  Select "File -- Import Project..." and import the top-level `build.gradle` file
 
 *  Create a IDEA plugin module wherever you like. You can leave that unchanged, we just need an IDEA plugin module to be able to create a working run configuration. Also create the run configuration for that pseudo plugin.
 
 *  *(Optional)* Create a Gradle run configuration for the project `pegdown-doclet:integrations:idea-plagin`, task `jar` and add that to "Before launch" of the IDEA run configuration. Like that, the plugin JAR will be built automatically using gradle before launching IDEA.
 
 *  Symlink `path/to/pegdown-doclet/integrations/idea-plugin/target/libs/idea-plugin-1.1-SNAPSHOT.jar` to the plugins directory of your IDEA SDK's sandbox.

 * Have fun tinkering with the plugin. ;)

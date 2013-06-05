Pegdown Doclet for IDEA
=======================

This is a plugin for IDEA that enables *Ctrl-Q* in projects using Pegdown for JavaDocs.


Installing the Plugin
---------------------

Simply install it from the Plugin manager.


Setting up the Development Environment
--------------------------------------

 *  Setup a plugin development environment as described [here](http://www.jetbrains.org/display/IJOS/Writing+Plug-ins).

 *  Select "File -- Import Project..." and import the `pom.xml` file.

 *  Select "File -- New Module..." to create a "IntelliJ Platform Plugin", choose `integrations/idea-plugin` as content root and don't create a source root.

 *  Open the project structure dialog, add a module dependency to pegdown-doclet and set `src/main/java` as source directory.

    In the "Plugin Deployment" tab, select `src/main/resources` as the directory containing `META-INF/plugin.xml`.

 *  Also in the project structure, set the export flag for all *Compile* dependencies, change the scope of com.sun.tools:tools to *Provided*

    **Important:** You'll need to repeat this last step after every Maven synchronisation. It's therefore recommended to disable auto import from Maven.

 * Have fun tinkering with the plugin. ;)

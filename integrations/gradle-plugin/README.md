Pegdown Doclet Plugin for Gradle
================================

This is a plugin for Gradle that enables the Pegdown Doclet for all JavaDoc tasks.


Using the Plugin
----------------

```groovy
buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath 'ch.raffael.pegdown-doclet:gradle-plugin:1.0'
    }
}

apply plugin: 'ch.raffael.doclets.pegdown'
```

or compile this plugin from source (`./gradlew :integrations:gradle-plugin:jar`) 
and replace `'ch.raffael.pegdown-doclet:gradle-plugin:1.0'` with 
`files('/path/to/gradle-plugin.jar')`.

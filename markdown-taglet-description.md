# Markdown Taglet Description

## What is a Markdown Taglet?

1. A Markdown Taglet is a taglet is a class which is quite similar to the javadoc taglet,
but it could use *markdown* or *pegdown* syntax.

2. The markdown taglet is always an inline taglet.

3. It's possible to define your own options.

4. The syntax itself is a bit different. You have three possibilities:
    1. {{tagname tag-content}}
    2. {%tagname tag-content%}
    3. {$tagname tag-content$}
    

## How to create your own custom MarkdownTaglet?

This is quite simple:

1. You must extend `ch.raffael.doclets.pegdown.mdtaglet.MarkdownTagletBase`

2. Provide a **_public_** default constructor `public MyTaglet() {...}` 

3. Define a tag name by implementing `getName()` 

4. and implement one of the render methods:

    - either `render(List<String> arguments)` 
    - or `renderRaw(String tagContent)` and `useArgumentValidator()` should return `false`.
 
Actually these are the mandatory steps. The rest is optional.

+ `createNewInstance()` : The markdown taglet framework creates a prototype instance, 
   and tries to create for each tag within the javadoc a new instance. 

    - the default implementation returns `this.` 
    - If you don't override the default implementation, you are actually working
      on a **singleton** :-(.

+ `afterOptionsSet()`: After all options (see below) has been set the 
   prototype instance could do some initialization. _This method will be called only once and before any `createNewInstance()`._

+ `getWhiteSpacePreserver()`: A `WhiteSpacePreserver` is responsible what happens to 
   the leading and trailing whitespaces around the actually tag.
        
      - This is useful, if you want to control the rendering. See the `GistMarkdownTaglet` as an example.
      - The default implementation returns `PredefinedWhiteSpacePreserver.KEEP_ALL`

+ `getArgumentValidator()`: if `useArgumentValidator()` returns `true`, the framework splits the content of
the tag into a argument list and uses the `ArgumentValidator` to check, if arguments are valid.

      - If the argument's not valid, the taglet will not applied, but marked with an appropriate error message.  
      - There are a lot of predefined `ArgumentValidator`:  `ch.raffael.doclets.pegdown.mdtaglet.argval.PredefinedArgumentValidators`. 
      - The default implementation returns `PredefinedArgumentValidators.ZERO_OR_MORE`. 
 
+ _custom taglet options_: The framework supports the possibility to customize your taglet, by using the annotation `@Option("my-option")`. 

      - Every option could set by executing the pegdown doclet with `-mdt-my-option value`
      - The method must be public and have a single String parameter: `@Option("my-option1") public void setOption1(String option1) {...}`

## FAQ

##### **I've implemented my own taglet. How make it available to the pegdown doclet? **

Two steps must be done:

i. Add your taglet to the classpath (otherwise it won't be found)
ii. Register it by calling the javadoc tool with `-mdtaglet the.full.path.to.your.Taglet` 

##### **How do I make an option value available to my taglet?

First step. Add an option method to your taglet. Example:

```java
public class HelloTaglet {
    private String language="EN";
       
    @Option("hello-lang")
    // or this also possible 
    // @Option("-mdt-hello-lang")
    public void setDefaultLanguage(String language) {
        this.language = language;
    }
    
    // rest ommitted for brevity
}    
```

Second step. Call the javadoc tool (for example) with `-mdt-hello-lang DE`.

##### **I like to have access to the entire content of the tag. What should I do?**

i. Override `useArgumentValidator()` returning `false`.
ii. Override `renderRaw(String tagContent)` and do what you ever want.



##### **How can I define arguments with whitespaces?**

This is already implemented by the framework. For example:

```java
/**
 * This is my tag {{mytag hello world}} 
 */
```

This will be split into an argument list with _hello_ and _world_.


```java
/**
 * This is my tag {{mytag 'hello world'}} or {{mytag "hello world"}}  
 */
```

In both cases this will be only one argument with of course _hello world_. _Even the number of whitespace characters will be
kept._

##### Can I use MarkdownTaglet with the standard javadoc doclet?
 
No. It's part of the pegdown doclet.
 
 
#### Can a markdown taglet arguments span over multiple line?
 
Yes, that's possible.   example:
 
```java
/**
 * This is my tag {{highlight Say hi to
 *      - Uncle Bob
 *      - my wife Mary
 *      ...
 * }}  
 */
```

You need to use the `renderRaw(String tagContent)`. 
Then you will get everything inside it as a single string (including whitespace, linefeeds etc).


#### How can I make my markdown taglet a standard taglet?

_First_ you have to fork the [pegdown doclet](https://github.com/Abnaxos/pegdown-doclet) and
_second_ add your taglet to `ch.raffael.doclets.pegdown.mdtaglet.StandardTaglets`.
 
If you like to have it in the official version, you need to make a pull request. 


## Examples


Study the [Hello World example](https://gist.github.com/loddar/05db6aa22972adf81babaad59a6b6136).
 
 
Study the `ch.raffael.doclets.pegdown.mdt.gist.GistMarkdownTaglet` and
 it's test class `mdtaglets.GistMarkdownTagletSpec`.
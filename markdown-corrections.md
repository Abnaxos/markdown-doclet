## Markdown Corrections

Markdown and Javadoc do not always work smoothly together. They where not meant to work together.


Look at this typical issue: The @ Symbol is a normal character, but often used in code examples:

> ```
>     @Option("hello-lang")
>     public void setDefaultLanguage(String language) {
>         this.language = language;
>     }
> ```

Javadoc complains about an unknown tag `Option`. If you like me, you try to fix the _warning_:

###### First attempt. 

Use the {@literal @}.

> ```
>     {@literal @}Option("hello-lang")
>     public void setDefaultLanguage(String language) {
>         this.language = language;
>     }
> ```


But this not nice, (you have to it on every place) and second the output look something like this:


> ```
>      @Option("hello-lang")
>     public void setDefaultLanguage(String language) {
>         this.language = language;
>     }
> ```


Of course, you can fix this be remove one space - I did this all the time. But if the your code block needs to be
intended all the time. Otherwise you can't start the {@literal @} one character before the rest - ugly. 

> ```
>    {@literal @}Option("hello-lang")
>     public void setDefaultLanguage(String language) {
>         this.language = language;
>     }
> ```


Do you like this, do you?! I don't!



###### Next attempt. 

You are smart, so use html entity code of @ (`&#64;`). 


> ```
>     &#64;Option("hello-lang")
>     public void setDefaultLanguage(String language) {
>         this.language = language;
>     }
> ```

Again, you have to do it everywhere :-(. 

_But_ even this is not a solution because the pegdown parser, change any
html entity within a code block into `&#64;` - actually it turns the & symbol into `&amp;`. 

This is not an error, it's specified this way: **_It's not a bug, it's a feature._**

## The Solution

So we have a problem with the @ symbol and (if you've noticed) and html entities within code blocks.

### The @ Symbol
  
So even with {@literal @} there is a solution. You have to type it all the time, it takes time and worst of all: the
 code is not readable and errorprone. And what about copying code fragments?!

*__It's done.__* You don't have to anything. Just type @ everywhere you like. Within or outside a code block.
Just type @. 


### Html entities within code blocks

If you want to have print the real symbol inside a code block you can use now something like this:


> // If you wan't to have rendered a right arrow (&rarr;) inside a code block 
> // just use this: `{&rarr;}`.

This is specific to the _pegdown doclet_ and is not part of _pegdown_ markdown parser.


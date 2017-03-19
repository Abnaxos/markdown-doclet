### Description

The gist markdown taglet resolve the content of a [gist](https://help.github.com/articles/about-gists/) from [Github](https://github.com)
and render this to a highlighted code block. A gist could contain at least one gist file.


### Syntax


    {{**gist** _option(s)_ **_gist-id_** _file(s)_}}
    {%**gist** _option(s)_ **_gist-id_** _file(s)_%}
    {$**gist** _option(s)_ **_gist-id_** _file(s)_$}
    

+ **_gist-id_**: This is the hexadecimal identification of a *public* gist.

+ _files(s)_: select the gist file(s) to be rendered. If your gist has more then one gist file, you can select one or more of them. If you do not provide a selector, all files of your gist will be rendered.

+ _option(s)_: the rendering options. You can set up to **_2 optional_** options which overwrites the general settings.  
    
    - `+indent/-indent`: enable/disable indent (_default is **enabled**_).
    - `+desc/-desc`: enable/disable rendering the gist's description (_default is **enabled**_)


### Examples

    {{gist feafcf888d949627001948b8346e0da7}} 
        - renders all gist files
    
    
    {{gist feafcf888d949627001948b8346e0da7 GistTest.java}}   
        - renders only GistTest.java of the gist
        
    {{gist feafcf888d949627001948b8346e0da7 'GistTest.java'}} 
        - same result (useful if your filename contains whitespace characters)
    
    
    {{gist -desc feafcf888d949627001948b8346e0da7}} 
        - do not render the gist's description



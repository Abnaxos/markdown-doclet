/*
 * Copyright 2013-2016 Raffael Herzog, Marko Umek
 *
 * This file is part of pegdown-doclet.
 *
 * pegdown-doclet is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * pegdown-doclet is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with pegdown-doclet.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

/**
 *  # Description of the gist markdown taglet.
 *
 * ### Description
 *
 * The gist markdown taglet resolve the content of a [gist](https://help.github.com/articles/about-gists/) from [Github](https://github.com)
 * and render this to an highlighted code block. A single gist contains at least one gist file.
 *
 *
 * #### Benefits
 *
 * + So using this markdown taglet it's easier to share code examples.
 * + When somebody find an issue with your code example, he can make a comment, directly to your gist.
 * + Fixing issue is easier.
 * + You can write full examples including javadoc comments.
 * + Annotations is no issue anymore.
 * + The raw code is reachable with one click and <ctrl-C>.
 *
 *
 * #### Remarks
 *
 * + This taglet not a javadoc taglet. It's a **markdown taglet**.
 * + The gist markdown taglet is a **_standard_ markdown taglet**. So you don't need add it to the doclet.
 *
 *
 *
 * + A markdown taglet is extension to the old pegdown-doclet.
 * + It's possible to write your own markdown taglet and adding it by `-mdtaglet <my markdown taglet>`.
 *
 *
 * ### Syntax
 *
 *
 * {{**gist** _option(s)_ **_gist-id_** _file(s)_}}
 *
 * {%**gist** _option(s)_ **_gist-id_** _file(s)_%}
 *
 * {$**gist** _option(s)_ **_gist-id_** _file(s)_$}
 *
 *
 * + **_gist-id_**: This is the hexadecimal identification of a *public* gist.
 *
 * + _files(s)_: select the gist file(s) to be rendered. If your gist has more then one gist file, you can select one
 *   or more of them. If you do not provide a selector, all files of your gist will be rendered.
 *
 * + _option(s)_: the rendering options. You can set up to **_2 optional_** options which overwrites the general settings.
 *
 *      - `+indent/-indent`: enable/disable indent (_default is **enabled**_).
 *      - `+desc/-desc`: enable/disable rendering the gist's description (_default is **enabled**_)
 *
 *
 * ### Markdown Taglet Options (-mdt-)
 *
 * The markdown taglet options are parameters to the javadoc tool. If any of the options
 * produce an error the default value will be used.
 *
 * #### Rendering options
 *
 * + `-mdt-gist-indent`: Enable or disable the indent rendering of the gist.
 *      - Possible values are `true`/`false`
 *      - Default is `true`
 *
 * + `-mdt-gist-description`: Enable or disable the rendering of the gist's description.
 *      - Possible values are `true`/`false`
 *      - Default is `true`
 *
 *
 * #### Github API options
 *
 * The gist markdown taglet make heavy use of the Github API using [GitHub API for Java](http://github-api.kohsuke.org/).
 * So this taglet does not work without access to Github.
 *
 * + `-mdt-gist-github-properties`: Provide a property file with credentials for the [Github API](http://github-api.kohsuke.org/).
 *
 *      - If you do not provide your own property file, the taglet tries to find `~/.github`
 *      - or if none is available, use anonymous mode.
 *      - **Remarks**:
 *          1. If you don't provide your own credentials, you can run in [Github rate limits](https://developer.github.com/v3/#rate-limiting):
 *          Currently 60 request per hour.
 *          2. I recommend using `oauth=<any token>`.
 *          3. Visit [Github - Creating an access token](https://help.github.com/articles/creating-an-access-token-for-command-line-use/)
 *
 * + `-mdt-gist-github-use-cache`: Enable or disable the use of an cache
 *
 *      - Possible values are `true`/`false`
 *      - Default is `true`
 *      - Only of the cache is enabled the other cache related options have an effect.
 *
 * + `-mdt-gist-github-cache-size`: Set the cache size in MB
 *
 *      - The default value is 10 MB
 *      - Only values > 0 are valid
 *
 * + `-mdt-gist-github-cache-dir`: Set a cache directory.
 *
 *      - This is only the parent directory. The actually cache directory will be `<directory>/mdt-gist-cache`
 *      - The default is either `./target` or `./build`, if neither could be found
 *        `./cache` will be created.
 *      - So using the default cache settings with Maven/Gradle the cache will also dropped,
 *        if you call for example `gradle clean`.
 *      - Even using the cache does not prevent from calling the Github API, but there should be no issue with
 *      the [Github rate limits](https://developer.github.com/v3/#rate-limiting).
 *
 *
 * ### Examples
 *
 * <pre><code>
 *      {{gist feafcf888d949627001948b8346e0da7}}
 *      - renders all gist files
 *
 *
 *      {{gist feafcf888d949627001948b8346e0da7 GistTest.java}}
 *      - renders only GistTest.java of the gist
 *
 *      {{gist feafcf888d949627001948b8346e0da7 'GistTest.java'}}
 *      - same result (useful if your filename contains whitespace characters)
 *
 *
 *      {{gist -desc feafcf888d949627001948b8346e0da7}}
 *      - do not render the gist's description
 * </code></pre>
 *
 *
 */
package ch.raffael.doclets.pegdown.mdt.gist;
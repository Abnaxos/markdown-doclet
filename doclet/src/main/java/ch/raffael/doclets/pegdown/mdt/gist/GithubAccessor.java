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
package ch.raffael.doclets.pegdown.mdt.gist;

import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.OkUrlFactory;
import org.kohsuke.github.GHGist;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.kohsuke.github.RateLimitHandler;
import org.kohsuke.github.extras.OkHttpConnector;

import java.io.File;
import java.io.IOException;

/**
 * # GithubAccessor is responsible for accessing Github's API.
 *
 * GithubAccessor provides/is responsible
 *
 * + creating a Github API instance with credentials
 * + a cache implementation (enable/disable)
 *      - to provide the cache directory
 * + a wrapper around {@link GitHub}
 */
final class GithubAccessor {
    private static final long CACHE_SIZE_10_MB = 10 * 1024 * 1024;
    private static final String MDT_GIST_CACHE = "mdt-gist-cache";

    private GitHub github;
    private String gitHubPropertyFileName;
    private boolean useCache = true;
    private long cacheSize = CACHE_SIZE_10_MB;
    private String cacheDirectoryName;
    private File cacheDirectory;


    void setGitHubPropertyFileName(String gitHubPropertyFileName) {
        this.gitHubPropertyFileName = gitHubPropertyFileName;
    }

    void setUseCache(boolean useCache) {
        this.useCache = useCache;
    }

    void setCacheDirectoryName(String cacheDirectoryName) {
        this.cacheDirectoryName = cacheDirectoryName;
    }

    /**
     * Set the cache size in MB.
     *
     * @param cacheSize the cache size
     */
    void setCacheSize(int cacheSize) {
        assert cacheSize>0 : "Cache size must be > 0";
        this.cacheSize = cacheSize * 1024 * 1024;
    }

    final void init() throws IOException {
        setupCacheDirectory();
        this.github = createGitHubInstance();
    }

    private void setupCacheDirectory() {
        if( useCache ) {
            if (cacheDirectoryName == null)
                this.cacheDirectory = createDefaultCacheDirectory();
            else
                this.cacheDirectory = createCacheDirectory(new File(cacheDirectoryName));
        }
    }

    final GHGist getGist(String gistId) throws IOException {
        return github.getGist(gistId);
    }

    private GitHub createGitHubInstance() throws IOException {
        final GitHubBuilder gitHubBuilder;

        if (this.gitHubPropertyFileName != null) {
            gitHubBuilder = GitHubBuilder.fromPropertyFile(gitHubPropertyFileName);
        } else if (existsStandardGithubConfigFile()) {
            gitHubBuilder = GitHubBuilder.fromPropertyFile();
        } else {
            gitHubBuilder = new GitHubBuilder();
        }

        if (useCache) {
            gitHubBuilder.withConnector(createCachedHttpConnector());
        }

        return gitHubBuilder
                .withRateLimitHandler(RateLimitHandler.FAIL)
                .build();
    }

    private File findBuildDir() {
        final File target = new File("./target");
        if (target.isDirectory()) {
            return target;
        }

        final File build = new File("./build");
        if (build.isDirectory()) {
            return build;
        }

        return new File("./cache");
    }

    private OkHttpConnector createCachedHttpConnector() {
        final File cacheDirectory = getCacheDirectory();
        final Cache cache = new Cache(cacheDirectory, this.cacheSize);
        return new OkHttpConnector(new OkUrlFactory(new OkHttpClient().setCache(cache)));
    }

    private File getCacheDirectory() {
        return this.cacheDirectory;
    }

    private File createDefaultCacheDirectory() {
        return createCacheDirectory(findBuildDir());
    }

    private File createCacheDirectory(File parentDir) {
        File cacheDirectory = new File(parentDir, MDT_GIST_CACHE);
        if( ! cacheDirectory.exists() ) {
            cacheDirectory.mkdirs();
        }

        if( ! ( cacheDirectory.isDirectory() && cacheDirectory.canWrite() ) ) {
            System.err.println("Can't create cache directory " + cacheDirectory + "! No cache will be used!");
            cacheDirectory = null;
            useCache = false;
        }
        return cacheDirectory;
    }

    private boolean existsStandardGithubConfigFile() {
        final File homeDir = new File(System.getProperty("user.home"));
        final File propertyFile = new File(homeDir, ".github");

        return propertyFile.isFile() && propertyFile.canRead();
    }

}

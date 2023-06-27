# AndroSea
A tool for identifying Android Silently-evolved APIs
### Tool running
* First download Android framework code base from [GitHub](https://github.com/aosp-mirror/platform_frameworks_base) or [AOSP](https://android.googlesource.com/platform/frameworks/base/). Please be sure there are two copies available on your local environment.
* Be sure Java 8 environment is available.
* Running the tool with the shell script under directory tool with three parameters, Android repo1, Android repo2, a txt file contain the pairwise tag names seperated by a colon such the file androidPlatforms.txt.
`sh runandrosea.h /path/to/framework/code/copy1 /path/to/framework/code/copy2 androidPlatforms.txt.`

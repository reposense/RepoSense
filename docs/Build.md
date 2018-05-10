# Build from Source

This guide explains how to compile the executable Jar.

1. Download our source code by
   * using Git clone <br>
     e.g. `git clone https://github.com/reposense/RepoSense.git` <br>
   * or download and extract our [zip file](https://github.com/reposense/RepoSense/archive/master.zip).
2. In the `RepoSense` directory, execute the below command in the terminal <br>
   `gradlew shadowJar`
3. The executable Jar file will be generated in the folder `build` > `jar` with the name `RepoSense.jar` upon successful build.

# RepoSense
## Dependency
1. Git
2. Gradle
## How to Use
1. GUI
```sh
$ gradle clean run
```
2. CLI
```sh
$ gradle clean build
```
Unzip the file in build/distributions/
```
$ cd build/distributions/GitGrader/bin/
$ ./GitGrader -config CSV_path.csv -output output_path/ -since 01/10/2017 -until 01/11/2017
```
Argument List:
- config: Mandatory.The path to the CSV config file.
- output: Optional. The path to the report generated. If not provided, it will be generated in the current directory.
- since : Optional. start date of analysis. Format: dd/MM/yyyy
- until : Optional. end date of analysis. Format: dd/MM/yyyy

# VGit

Simple git server written with java

## Building and running

1. Download & Install **Java Development Kit 11** and the last version of [NodeJS](https://nodejs.org/en/download/) with npm.
   - Java for Mac - https://download.bell-sw.com/java/11.0.5+11/bellsoft-jdk11.0.5+11-macos-amd64.zip
   - Java for Linux - https://download.bell-sw.com/java/11/bellsoft-jdk11-linux-amd64.tar.gz
2. Build the full distribution of VGit using command:
```bash
./gradlew fullBuild
```
3. The compiled distribution is located in the folder ``backend/build/libs``
4. Install mongodb and provide connection uri (with collection) to MONGODB_URI envirement variable. Defualt: ``mongodb://localhost:27017/vgit``
5. Create new folder for your git repositories and provide to GIT_BASE_DIRECTORY envirement variable
6. Run compiled distribution using ``java -jar path/to/file.jar`` command. Then open ``localhost:8080``

# NavigationDrawerPlus

[![Build Status](https://travis-ci.org/tlemansec/NavigationDrawerPlus.svg?branch=master)](https://travis-ci.org/tlemansec/NavigationDrawerPlus)
![Maven Central](https://img.shields.io/maven-central/v/com.github.tlemansec/navigationdrawerplus.svg?style=flat)

NavigationDrawerPlus is an Android library to use the drawer layout with 2 possible menus and a content view.

min sdk version = 14

JavaDoc is available at: http://tlemansec.github.io/NavigationDrawerPlus/

Contents
--------

- [Usage](#usage)  
- [Examples](#examples)
- [Download](#download)
- [Tests](#tests)
- [Code style](#code-style)
- [Static code analysis](#static-code-analysis)
- [License](#license)

Usage
-----

Library has...:

```java

```

Examples
--------

Exemplary application is located in `app` directory of this repository.

Download
--------

You can depend on the library through Maven:

```xml
<dependency>
    <groupId>com.github.tlemansec</groupId>
    <artifactId>navigationdrawerplus</artifactId>
    <version>0.0.1</version>
</dependency>
```

or through Gradle:

```groovy
dependencies {
  compile 'com.github.tlemansec:navigationdrawerplus:0.0.1'
}
```

Tests
-----

Tests are available in `library/src/androidTest/java/` directory and can be executed on emulator or Android device from Android Studio or CLI with the following command:

```
./gradlew connectedCheck
```

Code style
----------

Code style used in the project is called `SquareAndroid` from Java Code Styles repository by Square available at: https://github.com/square/java-code-styles.

Static code analysis
--------------------

Static code analysis runs Checkstyle, FindBugs, PMD and Lint. It can be executed with command:

 ```
 ./gradlew check
 ```

Reports from analysis are generated in `library/build/reports/` directory.

License
-------

    Copyright 2015 Thibault Le Mansec

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

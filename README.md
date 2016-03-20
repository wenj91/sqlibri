![SQLite Logo](logo.svg)
``` Bash
███████╗ ██████╗ ██╗     ██╗██████╗ ██████╗ ██╗
██╔════╝██╔═══██╗██║     ██║██╔══██╗██╔══██╗██║
███████╗██║   ██║██║     ██║██████╔╝██████╔╝██║
╚════██║██║▄▄ ██║██║     ██║██╔══██╗██╔══██╗██║
███████║╚██████╔╝███████╗██║██████╔╝██║  ██║██║
╚══════╝ ╚══▀▀═╝ ╚══════╝╚═╝╚═════╝ ╚═╝  ╚═╝╚═╝

```
# SQLibri - the lightweight sqlite GUI client

## Project structure
``` Bash
src/
└── com
    └── sqlibri
        ├── App.java - [Class which launch application]
        ├── controller - [Controller classes. Application behavior]
        │   └── MainController.java - [Controller for main View]
        │   └── ... - [Other controllers]
        ├── model - [Model classes. Application business logic]
        │   ├── Database.java - [Class for working with database]
        │   └── QueryResult.java - [Class for storing query results]
        │   └── ... - [Other models]
        ├── resources - [Resources folder]
        │   ├── image - [Images folder]
        │   │   ├── database.png
        │   │   ├── ... - [Other images]
        │   ├── layout - [View folder. Application GUI]
        │   │   └── main-layout.fxml - [Main view]
        │   │   ├── ... - [Other views]
        │   └── style - [Styles folder. Application appearance]
        │       └── main-style.css - [Style for Main view]
        │       ├── ... - [Other styles]
        └── util - [Utility classes]
           └── ...
```

## Build and Run
To build this application you need gradle build tool
[Download Gradle](http://gradle.org/gradle-download/)
To run this application you need Java SE 1.8+
[Download Java](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)

Navigate to project parent folder and run: 
``` Bash 
gradle build
```
To build the project. And
``` Bash
 java -jar build/libs/sqlibri-0.0.1-SNAPSHOT.jar
```
To run it.
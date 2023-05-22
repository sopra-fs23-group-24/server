# "Who said that?" - party game  
We have created a party game, which makes getting to know people easier and more fun. 

## Introduction:
Instead of just asking each other the same lame questions over and over again, we wanted to create a fun
and easy way to getting to know people. To achieve this we created a quiz game, revolving around the participants
themselves.   

roughly explained:
- All players answer the questions that are posed initially.  
- Next the quiz asks: "Who said that?" - and everybody takes a guess. 
- The goal is to be as correct and as fast as possible.  

## Technologies used

- JPA, hibernate database, REST, Google Cloud, GitHub, Gradle


## High-level components:
- Our main file, running the application: [Application.java](src%2Fmain%2Fjava%2Fch%2Fuzh%2Fifi%2Fhase%2Fsoprafs23%2FApplication.java)    
  - but not really that important though, at least no to us in this course, since we left it as it was 


- Our main components are all of our services and controllers.   
(For each entity one of each, and an additional service for generating the quiz questions.)
  - the package with all our services: [service](src%2Fmain%2Fjava%2Fch%2Fuzh%2Fifi%2Fhase%2Fsoprafs23%2Fservice)  
    - the service (or class) for generating quiz questions: 
    [QuizQuestionGenerator.java](src%2Fmain%2Fjava%2Fch%2Fuzh%2Fifi%2Fhase%2Fsoprafs23%2Fservice%2Fquiz%2FQuizQuestionGenerator.java)
  - the package with all our controllers: [controller](src%2Fmain%2Fjava%2Fch%2Fuzh%2Fifi%2Fhase%2Fsoprafs23%2Fcontroller)


## Launch & Deployment:
Write down the steps a new developer joining your team would
have to take to get started with your application. What commands are required to build and
run your project locally? How can they run the tests? Do you have external dependencies
or a database that needs to be running? How can they do releases?

- #### Build  

  - ```bash
      ./gradlew build 
    ```
  - we build this project with gradle: (https://gradle.org/)

  
- #### Run

  - ```bash
      ./gradlew bootRun
      ```
  
  - the running server will be "stuck" at: "80% EXECUTING [time]" - this is normal. 
  - The last log in the terminal should 
    say: "Started Application", indicating that the application (the server) is running.
  - Additionally, this can be checked by visiting the `localhost:8080`, which will show the text: "The application is running."

- #### Test
  - ```bash
    ./gradlew test
    ```
  - and with the built-in test functionality of your IDE of choice.

- #### Dependencies and databases 
  - The JPA / hibernate database does not need to be run separately, neither to any dependencies.  


## Deployment 
On each push to the main branch, the project is automatically deployed to google cloud.  


## Roadmap:
The top 2-3 features that new developers who want to contribute to your project
could add.

- adding new "game modes" respectively question types, like "rate this on a scale of 1-10", or other.
- changing the calculation of points to include "answer-streaks". 
- making the game even more customisable by letting the users choose how many questions the want to be generated, per prompt. 
- adding progress indicators:
  - on how far the other players are with answering their prompts
  - in the quiz: 
    - on each question - how many players have already answered 
    - in general - showing how many questions out of the total amount have been answered
- adding a "go back" option to allow "undo" and the re-writing of already answered prompts  


## Authors
SoPra Group 24 
- Tim Kinget
- Linda Steiner
- Lara Gianinni
- Mike Röllin
- Jan Eggli


## License

This project is licensed under the Apache License - see the LICENSE file for details


## Acknowledgments

initially some inspiration on this README from [PurpleBooth](https://github.com/PurpleBooth)



---

---
End of our README 

---

---
# DISCLAIMER: 
    The following information was provided to us by our course / TA's / Prof, 
    we found it quite helpful, that is why took it over into our own README. 

# SoPra RESTful Service Template FS23

## Getting started with Spring Boot
-   Documentation: https://docs.spring.io/spring-boot/docs/current/reference/html/index.html
-   Guides: http://spring.io/guides
    -   Building a RESTful Web Service: http://spring.io/guides/gs/rest-service/
    -   Building REST services with Spring: https://spring.io/guides/tutorials/rest/

## Setup this Template with your IDE of choice
Download your IDE of choice (e.g., [IntelliJ](https://www.jetbrains.com/idea/download/), [Visual Studio Code](https://code.visualstudio.com/), or [Eclipse](http://www.eclipse.org/downloads/)). Make sure Java 17 is installed on your system (for Windows, please make sure your `JAVA_HOME` environment variable is set to the correct version of Java).

### IntelliJ
1. File -> Open... -> SoPra server template
2. Accept to import the project as a `gradle project`
3. To build right click the `build.gradle` file and choose `Run Build`

### VS Code
The following extensions can help you get started more easily:
-   `vmware.vscode-spring-boot`
-   `vscjava.vscode-spring-initializr`
-   `vscjava.vscode-spring-boot-dashboard`
-   `vscjava.vscode-java-pack`

**Note:** You'll need to build the project first with Gradle, just click on the `build` command in the _Gradle Tasks_ extension. Then check the _Spring Boot Dashboard_ extension if it already shows `soprafs23` and hit the play button to start the server. If it doesn't show up, restart VS Code and check again.

## Building with Gradle
You can use the local Gradle Wrapper to build the application.
-   macOS: `./gradlew`
-   Linux: `./gradlew`
-   Windows: `./gradlew.bat`

More Information about [Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html) and [Gradle](https://gradle.org/docs/).

### Build

```bash
./gradlew build
```

### Run

```bash
./gradlew bootRun
```

You can verify that the server is running by visiting `localhost:8080` in your browser.

### Test

```bash
./gradlew test
```

### Development Mode
You can start the backend in development mode, this will automatically trigger a new build and reload the application
once the content of a file has been changed.

Start two terminal windows and run:

`./gradlew build --continuous`

and in the other one:

`./gradlew bootRun`

If you want to avoid running all tests with every change, use the following command instead:

`./gradlew build --continuous -xtest` - this can be quite good!  

## API Endpoint Testing with Postman
We recommend using [Postman](https://www.getpostman.com) to test your API Endpoints.

## Debugging
If something is not working and/or you don't know what is going on. We recommend using a debugger and step-through the process step-by-step.

To configure a debugger for SpringBoot's Tomcat servlet (i.e. the process you start with `./gradlew bootRun` command), do the following:

1. Open Tab: **Run**/Edit Configurations
2. Add a new Remote Configuration and name it properly
3. Start the Server in Debug mode: `./gradlew bootRun --debug-jvm`
4. Press `Shift + F9` or the use **Run**/Debug "Name of your task"
5. Set breakpoints in the application where you need it
6. Step through the process one step at a time

## Testing
Have a look here: https://www.baeldung.com/spring-boot-testing

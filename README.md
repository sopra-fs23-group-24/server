# "Who said that?" - party game  
We have created a party game, which makes getting to know people easier and more fun. 

## Introduction
Instead of just asking each other the same lame questions over and over again, we wanted to create a fun
and easy way to getting to know people. To achieve this we created a quiz game, revolving around the participants
themselves.   

- roughly explained:
  - All players answer the questions that are posed initially.
  - Each typ of question requires a different answer: a drawing, some text or telling a true/false story.
  - Next the quiz asks: "Who said that?" - and everybody takes a guess. 
  - The goal is to be as correct and as fast as possible.  

## Technologies used

- JPA, hibernate database, REST, Google Cloud, GitHub, Gradle, Spring Boot


## High-level components

- Our main components are all of our services and controllers.   
(For each entity one of each, and an additional service for generating the quiz questions.)
  - the package with all our services: [service](src%2Fmain%2Fjava%2Fch%2Fuzh%2Fifi%2Fhase%2Fsoprafs23%2Fservice)  
    - the service (or class) for generating quiz questions: 
    [QuizQuestionGenerator.java](src%2Fmain%2Fjava%2Fch%2Fuzh%2Fifi%2Fhase%2Fsoprafs23%2Fservice%2Fquiz%2FQuizQuestionGenerator.java)
  - the package with all our controllers: [controller](src%2Fmain%2Fjava%2Fch%2Fuzh%2Fifi%2Fhase%2Fsoprafs23%2Fcontroller)


## Launch & Deployment

- #### Build  

  - ```bash
      ./gradlew build 
    ```
  - we build this project with gradle: (https://gradle.org/)

  
- #### Run

  - ```bash
      ./gradlew bootRun
      ```
  
  - The running server will be "stuck" at: "80% EXECUTING [time]" - this is normal. 
  - The last log in the terminal should 
    say: "Started Application", indicating that the application (the server) is running.
  - Additionally, this can be checked by visiting the `localhost:8080`, which will show the text: "The application is running."

- #### Test
  - ```bash
    ./gradlew test
    ```
  - And with the built-in test functionality of your IDE of choice.
  - We also recommend using [Postman](https://www.getpostman.com) to test your API Endpoints.

- #### Dependencies and databases 
  - The JPA / hibernate database does not need to be run separately, neither to any dependencies.  


- #### Development
  - To make development faster and easier, you can use the "development mode":
    - using `./gradlew build --continuous`, to re-build with each change to the code
  - If you want to avoid running all tests with every change, use the following command instead:
      - `./gradlew build --continuous -xtest`
  - While running one of the two commands above, run the server in another terminal:
    - `./gradlew bootRun`


- #### Deployment 
  - On each push to the main branch, the project is automatically deployed to google cloud.  

    
## Roadmap

- Adding new "game modes" respectively question types, like "rate this on a scale of 1-10", or other.
- Changing the calculation of points to include "answer-streaks". 
- Making the game even more customisable by letting the users choose how many questions the want to be generated, per prompt. 
- Adding progress indicators:
  - on how far the other players are with answering their prompts
  - in the quiz: 
    - on each question - how many players have already answered 
    - in general - showing how many questions out of the total amount have been answered
- Adding a "go back" option to allow "undo" and the re-writing of already answered prompts  

(This is the same as in the client README, because the two parts of our application are closely connected, in achieving these features.)


## Authors
SoPra Group 24:
- Tim Kinget
- Linda Steiner
- Lara Gianinni
- Mike Röllin
- Jan Eggli


## License

This project is licensed under the Apache License - see the [LICENSE](LICENSE) file for details.


## Acknowledgments

Initially, we got some inspiration on this README from [PurpleBooth](https://github.com/PurpleBooth).  
The [SOPRA_README.md](SOPRA_README.md) provided us with helpful information as well as inspiration. Especially on the 
topic of _Launch & Deployment_ in our README. 
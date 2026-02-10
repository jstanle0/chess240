# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```

## Link to Server Diagram

[Link](https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=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-aB2fq49WqoPGRy-t4wBXyHarOHL8km8gAXsOEZwoMShQkoE2Yk1JZ8bn1FiUdOsX46zZKkZMtQ6KU6Yr7D0nIAAeTZ2Zyh4oEq3PphY4AFHkGmWg1CgXCWZaoBYDRxjMqRNIWKftwjBECCCTY8RKwoDLJGVaIJkigHhPq1eo8QTMqFoWKenRp6QrsXU16yxZWxQVWMJVKr6hqo1VqnVoF5oj2+MakAprA2GpdYWa1cwp4wDtTvTawZ95QAcpA-+rKDFCXBTUoKd8wrOucZ9F+biNYf1UKlKWkDMrZRVHlBOhUG0gNKhwAA7G4JwKAnAxBZMEOANUABs8BsLCrmDAQZPthkD2aG0LovRmXTPHpQiUVrCz2tuKQ3uizF3yWXVG1dCbjaXV2liHEeICREgNFKzFF0XYcGYWAbNXtc32NFI-KKRb4Vh3+jATUQN60FRWYJPsBdwmbIuYIgVATc6ARgEcgYJyIBnMoeB9AFLrlGFuWme5WZSY6DzM84hryj2Yk+fo8+WL04fi-AK6VztTZgrupWnN8A80wqcXC76CL6hIuOiiuOaKyM-Ihji4AkGfQHMAiBx0YGRNrE2dG7GZGqU0XyYWWRl5dDcAzeR80vLG78w3WxWonqe5sUzc8N6nrVXqsCPWp9LH7H1KcVZ71Nn61qKTdbVNNaT6CevZfRjntbG1I0g-WFwdP1vzLRW9K1b-51sacA5ppVLDcKhpsKBSAEhgFS1+CAGWABSEA0wjsMP4EN8Jx3mEnSgpowZZ09HnQQ5JO7RjYAQMAVLUBRVQygGsT1a7VLzM3bUJZaAJTtc65QHr0B+uFmVZZXeIK9qnsOsdS9CzzN1GW-ex9wXn3ClfeFlxnGv3qh-YDbUaLAOoXqAAK2K2gMDY25tzGVaNDrXWZu5TE4E2DGN4OnPOWNtD7kqWEywxnbMTzKEHro+80j0HM19io3zNh5GJzLYY0ua+9m56ikcU-CLp33HNa8fx-9Sckc6bfCJ370HjNHTA0qjgoPYJUppcwZllt95qgMJiAol54iGF2tpoTtQ9O4rURQbg4A46wbU2eRsGnZkGf8-UKcM5LBznknth1IXfbbmOx+knGslcXivDeSnfj0dQYk7UbAeEpyOjaZL4ANo3ds6rhh2owZsBaEdAr0dVtoC21x-thzwonMjEW4m5N3n01+fOgFnHNj9cHdCwWjjocovJXLV-Stv8fMAOt425LYCoCdcy9lrwVfEBwlgMAbA7XCB5EF1Vso6vGhoIwVg3qxgF0COITLYbRnpFD6oZt8XsIDBgGzHoAwKG0C7Io-iyu6zjlA+QyD7T4POeayMI3JPt2oeNw8wMS2CZtZ1g4DMErpQLtgB8O8BMopbOQAyjoGy-QQDhKF4YfUbAR7LANXZPWoMxYucnKxQLcPB1DPEKN-RtfxcTAlAMGfI6effQFAG0L3fGWoTJHhQwdEBAQ-R5ErSpUg3QLAuZSPDSaPWPayLzCBYvMXdXPXddA3fNN9QtVWV+UtPPGLH+OLHKBLBtAqIAA)
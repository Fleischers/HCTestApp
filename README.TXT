This project is a simple http server created with Netty framework. 
Created as @HamsterCoders assignment.
Author: @Fleischers.

# Basic features:
	- Asynchronous I/O
	- About 700 queries per second

# How to build:
	## With Maven
	- Apache Maven is needed (working properly with 3.1.0)
	- Run "mvn clean compile assembly:single" in the directory with "pom.xml" file.
		or simply use "mvn-compile.bat" if using OS Windows.
	## No Maven
	- Install Netty 4-0-9-Final
	- Start compile from main class HCServer.java and add Netty to your classpath

# How to run:
	## Server side use
		Run in command line 
"java -jar target\HCServer-1.0-SNAPSHOT-jar-with-dependencies.jar" 
		and server will be launched at http://localhost:8080/ 
		or simply use "launchHCServer.bat" if using OS Windows.
		For normal server work port 8080 is needed to be free.
		*if you want to specify another port you can add it as parameter e.g. "java -jar target\HCServer.jar 8081"
	## Client side use
		Go to browser at http://localhost:8080/ and follow the instructions
		

# Notes: 
	- You can simply download already compiled jar file from "target" directory
	- See screenshots in "Screenshots" folder
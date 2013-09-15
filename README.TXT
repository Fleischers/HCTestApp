This project is a simple http server implemented with Netty framework. 
Created as @HamsterCoders assignment.
Author: @Fleischers.

#Basic features:
	- Asynchronous I/O
	- About 10000? queries per second

#How to build:
	- Apache Maven is needed (working poperly with 3.1.0)
	- Run "mvn clean compile assembly:single" in the directory with "pom.xml" file.
		or simply use "mvn-compile.bat" if using OS Windows.

#How to run:
	##Server side use
		Run in command line "java -jar target\HCServer-1.0-SNAPSHOT-jar-with-dependencies.jar" and server will be launched at http://localhost:8080/ 
		or simply use "launchHCServer.bat" if using OS Windows.
		For normal server work port 8080 is needed to be free.
		*if you want to specify another port you can add it as parameter e.g. "java -jar target\HCServer-1.0-SNAPSHOT-jar-with-dependencies.jar 8081"
	##Client side use
		Go to browser at http://localhost:8080/ and follow the instructions
		


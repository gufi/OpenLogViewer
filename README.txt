OpenLogViewer is an open source project created out of bordom.
with that being said I have no intention on making the program unusable or even hard to use. 
I would eventually love for the program to become mainstream in the Automotive Datalog area as a tool.


On 3/16/2011 the project was moved to Maven!

To run the application using maven, simply run "mvn" and wait.

The first time you run maven, it downloads itself, hence the wait.

It can however still be built by Ant:

step 1:  ant
this will build and package the source into a executable jar file in the folder
/path/to/OpenLogViewer/target/OpenLogViewer-0.0.1.jar

step 2: cd target

step 3: java -jar OpenLogViewer-0.0.1.jar

and thats about all there is to it!


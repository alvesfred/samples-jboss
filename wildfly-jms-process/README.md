# wildfly-jms-process
An application process to control all MDBs dispatch and messages over a lot of EAR/WAR Application

Author: Frederico Alves (alvesfred)    
Level: Intermediate   
Technologies: EJB, JAR, EAR, MDB, JMS     
Summary: The simple engine application topic control for handling all messages from a producer and received by a specific consumer.

## What is it?

This simple engine sample project demonstrates the deployment process of an EAR that controls all messages from producer and listening them by each consumer application.
Each consumer EAR application is an independent implementation. So, it is possible to create a lot of consumers EAR to handle and processing a specific message.

The wildfly-jms-process is composed of five Maven projects. The projects are as follow:

1. 'core': This project contains base classes and it will be used by all other projects.

2. 'engine': This project is an EAR application that controls all messages. This project is used to delivery responsabilities to other projects.

3. 'consumer-1': This project is an EAR to demonstrate a little control over a specific text message delivery by a jms topic.

4. 'consumer-2': The second application sample to demonstrate how to receive and process another type of message.

5. 'producer': Message producer sample.

## System Requirements

Java SDK version 8

Java Enterprise Edition version 7

The applications this project produces are designed to be run on Wildfly 11 or later.

## Start the Server

1. Open a command prompt and navigate to the root of the $WILDFLY_HOME directory.
2. The following shows the command line to start the server:

        For Linux:   ${WILDFLY_HOME}/bin/standalone.sh -c standalone-full-ha.xml

## Build and Deploy the Quickstart

1. Make sure you have started the WILDFLY server as described above and execute the maven command over the project:
2. Open a command prompt and navigate to the root directory of this project. Type this command to validate and generate EAR files:

	mvn clean install	

3. Type this command to deploy the archive (EAR files) into Wildfly:

        mvn wildfly:deploy

This command will deploy `ear/target/${project.artifactId}.ear` to the running instance of the server.

## Access the Console Admin

All applications will be deploying at the following URL <http://localhost:9990/console>.

## Undeploy the Archive

1. Make sure you have started the WILDFLY server as described above.
2. Open a command prompt and navigate to the root directory of this project.
3. When you are finished testing, type this command to undeploy the archive:

        mvn wildfly:undeploy



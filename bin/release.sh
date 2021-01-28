#!/bin/bash

#
# The MAIL POLL COMPONENT provides the core functionality to read from IMAP servers
# and to push mail messages into a Kafka topic, using the AVRO format.
#
cd ../mpc
mvn clean generate-sources compile package install

#
# The API project offers the a SpringBoot application to test and simulate interval polls.
# The connector project wraps all things up into a single deployable component for Kafka Connect.
#
cd ..
./gradlew clean compileJava shadowJar build docker
cp connector/build/libs/connector-0.0.1-all.jar connector/runtime/imap-connector-0.0.1-all.jar

#
# Run the connector in local mode.
#
cd connector
$CONFLUENT_HOME/bin/connect-standalone config/imap-mail-connect-standalone.properties config/imap-mail-connector.properties


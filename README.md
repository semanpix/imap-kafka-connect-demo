# IMAP Kafka Source Connector
This project contains the source code for a custom Source Connector for Kafka Connect. 
The Connector reads email messages from IMAP mailboxes, e.g., from Google Mail and writes the message metadata into a Kafka topic using the Avro format. 
 
The Source Connector periodically polls new emails from the server to produce the new metadata records to a Kafka topic. 

The purpose of this process is creation of corpus of communication meta data for analysis of communication patterns and cantitative statistics on message flows.

## Getting Started
Clone repo:
```bash
git clone https://github.com/semanpix/imap-kafka-connect-demo
```

Compile and package docker image:
```bash
$ cd kafka-connect
$ ./gradlew clean build docker
```

Create Source Connector uber-jar:
```bash
$ cd kafka-connect
$ ./gradlew clean shadowJar
```

* [Tutorial Blog Post](https://medium.com/enfuse-io/collecting-email-metadata-for-statistical-analysis-of-business-communication) - WORK IN PROGRESS
* [Use Google Kubernetes Engine](docs/gcloud-setup.md)
* [Use Minikube On Local Machine](docs/minikube-setup.md)
* [How to Install and Run a Custom Connector](docs/install-connector.md)
* [Helpful Links](docs/links.md)
* [Helpful Kafka Commands](docs/kafka.md)
* [Push API Image to Google Registry and Deploy on GKE](docs/deploy-api-to-google-registry.md)

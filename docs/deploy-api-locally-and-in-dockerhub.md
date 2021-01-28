# Deploy API Application To Google Cloud Registry

Build The App:
--
```bash
$ ./gradlew build docker
$ ./gradlew clean compileJava shadowJar build docker --refresh-dependencies
```

Execute the connector in local mode:  
--
```bash
$ cd connector
$ $CONFLUENT_HOME/bin/connect-standalone config/imap-mail-connect-standalone.properties config/imap-mail-connector.properties
```

Login to public Image Repository:
--
```bash
$ docker login --username=kamir
```

Tag The Docker Image:
--
```bash
# Grab the image id from this list
$ docker images --all
$ docker tag 39b8419fb274 kamir/imap-kafka-connector-api
```

Push To Registry:
--
```bash
$ docker push kamir/imap-kafka-connect_random-mail-api
```

Run the image locally:
--
```bash
$ docker run -d -p 8080:8080 org.semanpix/imap-kafka-connector-api
```
Open a browser:
```bash
$ open http://127.0.0.1:8080/random/mail
```
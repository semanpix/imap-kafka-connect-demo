# Deploy API Application To Google Cloud Registry

Build The App:
--
```bash
$ ./gradlew build docker
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
$ docker tag <image id> imap-kafka-connect_random-mail-api>
```

docker tag bb1e62d8190f kamir/imap-kafka-connect_random-mail-api


Push To Registry:
--
```bash
$ docker push kamir/imap-kafka-connect_random-mail-api
```

Run the image locally:
--
```bash
$ docker run -d -p 8080:8080 kamir/imap-kafka-connect_random-mail-api
```
Open a browser:
```bash
$ open http://127.0.0.1:8080/random/mail
```
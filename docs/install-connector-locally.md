# How to Install and Run a Custom Connector

The Confluent Kafka Connect Server comes with binaries that allow you to start a connector in [standalone or distributed mode](https://docs.confluent.io/current/connect/userguide.html#standalone-vs-distributed). 
For testing and development purposes, you can install a standalone connector, but in production it is recommended to run in distributed mode. 

These instructions assume you already have Kafka and Kafka Connect clusters running on your local machine.

## Standalone mode

1. Under this project's `/config` directory, you will find sample [imap-mail-connect-standalone.properties](../connector/config/imap-mail-connect-standalone.properties) and [imap-mail-connect.properties](../connector/config/imap-mail-connect.properties) files. 
First `cd` into the root of this project. 

    In `imap-mail-connect-standalone.properties`, modify the address for your kafka brokers: 
    
    ```properties
    bootstrap.servers=<replace with your kafka ip address>:9092
    ```
    
    In `imap-mail-connect.properties`, modify the address for the endpoint you want to hit:
    
    ```properties
    api.url=<replace with your api's ip address>:8080
    ```

2. Copy the two `.properties` files into the `/etc/kafka` directory in your connect server with the following commands:
	
	```bash
	$ kubectl get pods // to see the name of your kafka connect pod
	$ kubectl cp /config/randomlong-connect-standalone.properties <kafka-connect-pod-name>:/etc/kafka -c cp-kafka-connect-server
	$ kubectl cp /config/randomlong-connector.properties <kafka-connect-pod-name>:/etc/kafka -c cp-kafka-connect-server
	```

2. Build an uber-jar with `$ ./gradlew clean shadowJar` 

3. In another terminal window, SSH into the kafka connect server container to create a new directory:
	
	```bash
	$ kubectl exec -ti <kafka-connect-pod-name> -c cp-kafka-connect-server bash
	$ cd /user/share/java
	$ mkdir kafka-connect-randomlong
	```

4. Copy the uber-jar from `/connector/build/libs` into the `/usr/share/java/kafka-connect-randomlong-connector` directory in the kafka connect container:  

	```bash
	$ kubectl cp connector/build/libs/connector-all.jar <kafka-connect-pod-name>:/usr/share/java/kafka-connect-randomlong/ -c cp-kafka-connect-server
	```

5. Go back to the terminal window where you're ssh-ed into the container and run:
	
	```bash
	/usr/bin/connect-standalone /etc/kafka/randomlong-connect-standalone.properties /etc/kafka/randomlong-connector.properties
	```

## Distributed mode

To run the connector in distributed mode:

1. In `randomlong-connect-distributed.properties`, modify the address for your kafka brokers: 
       
       ```properties
       bootstrap.servers=<replace with your kafka brokers ip address>:9092
       ```
 
2. Copy the [randomlong-connect-distributed.properties](../connector/config/randomlong-connect-distributed.properties) file 
into the `etc/kafka` directory in your kafka connect server with the following command: 

	```bash
	$ kubectl get pods // to see the name of your kafka connect pod
	$ kubectl cp /config/randomlong-connect-distributed.properties <kafka-connect-pod-name>:/etc/kafka -c cp-kafka-connect-server
	```

3. As before, you will need your connector uber-jar in the `/user/share/java/kafka-connect-randomlong` directory of the kafka connect container. 
See above step #4 from previous section.

4. `kubectl exec` into the kafka connect container and run:
   	
   	```bash
   	/usr/bin/connect-distributed /etc/kafka/randomlong-connect-distributed.properties
   	```

    > Note that `/usr/bin/connect-distributed` only takes the configuration properties for the connect workers. 
    Configuration for your custom connector will be passed through the Kafka Connect REST API, which we'll do in the next step.
    
5. Set up port-forwarding to the rest port for your custom connector:

    ```bash
    $ kubectl port-forward <kafka-connect-pod-name> 8085:8085
    ```
   
   > See the `rest.port` property in `randomlong-connect-distributed.properties` to see which port to use. 

6. Submit a POST request to the Kafka Connect REST API to create your new connector, passing in the required configuration properties through the request body: 

    ```bash
    curl -X POST \
      http://localhost:8086/connectors \
      -H 'Accept: */*' \
      -H 'Content-Type: application/json' \
      -d '{
        "name": "randomlong_source_connector",
        "config": {
            "connector.class": "io.enfuse.kafka.connect.connector.RandomLongSourceConnector",
            "api.url": "<host>:8080",
            "topic": "randomlong_topic",
            "sleep.seconds": 5
        }
    }'
    
    ```
    
    > Don't forget to modify the value for `api.url` in your request body!
    
## Docker Container with Custom Connector Pre-Installed

You can deploy a connect server with your custom connector pre-installed. Under `/connector`, you can find a sample [`Dockerfile`](../connector/Dockerfile)

1. Modify the env value for `CONNECT_BOOTSTRAP_SERVERS` in the Dockerfile. You can use the IP Address & port of your kafka brokers or the name of your headless kafka service.

2. `cd` into the `/connector` directory.

3. Build the docker image: 

	```bash
	$ docker build . -t randomlong-connector
	```
	
4. Tag the docker image in preparation for pushing it to Google Container Registry: 

	```bash
	$ docker tag randomlong-connector us.gcr.io/enfuse-gke/randomlong-connector
	```
	
5. Make sure your docker is authenticated to push to GCR:

	```bash
	$ gcloud auth configure-docker
	```
	
6. Push the docker image to GCR: 

	```bash
	$ docker push us.gcr.io/enfuse-gke/randomlong-connector
	```
	
7. Run the container:

	```bash
	$ kubectl run randomlong-connector --image=us.gcr.io/enfuse-gke/randomlong-connector --port=8083
	```
	
8. Expose a service for the connector:

	```bash
	$ kubectl expose deployment randomlong-connector --type=ClusterIP --name=randomlong-connector-service
	```
	
9. Port-forward to the randomlong connector container:

	```bash
	$ kubectl get pods // to get the name of your randomlong-connector pod
	$ kubectl port-forward <randomlong-connector-pod-name> 8083:8083
	```

10. Follow step (6) from the above "Distributed Mode" section to submit a POST request that will start the connector.

## Using Mounted Volume

1. Modify the `CONNECT_BOOTSTRAP_SERVERS` env variable in [`randomlong-connect-pod.yaml`](../connector/k8s/randomlong-connect-pod.yaml).

2. To deploy a pod that runs the kafka connect base image with the randomlong connector pre-installed in an ephemeral volume:

    ```bash
    $ kubectl apply -f connector/k8s/randomlong-connect-pod.yaml
    ```
    
    > Note: When the pod is removed, so are the ephemeral volumes it's mounted to.
    
3. As before, submit a POST request to start a randomlong connector worker in distributed mode (see previous sections).
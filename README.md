* Kafka Connect is a framework for connecting Kafka with external systems such as databases, key-value stores, search indexes, and file systems, using so-called Connectors.

* Tasks – the implementation of how data is copied to or from Kafka

* Workers – the running processes that execute connectors and tasks

* A topic is a table in KSQL world.

* Build a custom postgres docker image using the "dockerfile" included: "docker build -t postgres ."

* docker-compose file:

    * Has 3 kafka brokers(broker, broker_1, broker_2) to form a Kafka cluster
    * One Zookeeper(zookeeper) to manage the cluster
    * schema-registry container which stores all the schemas for the data transferred thrugh the Kafka
    * It has one source File connect(file-connect) which pushes the files like XML, CSV files to the kafka topics as per the config file
        * Connectors running: http://localhost:8083/connectors
        * A specific connector status: http://localhost:8083/connectors/{connector-name}/status
        * http://localhost:8083/connectors/orders-source-connector/status  
      
    * It has one source JDBC connect(jdbc-connect) which pulls the data from a DB configured in the config file to the corresponding/prefixed kafka topics
        * Connectors running: http://localhost:8084/connectors
        * A specific connector status: http://localhost:8084/connectors/{connector-name}/status
        * http://localhost:8084/connectors/user-recs-jdbc-connector/status
        * http://localhost:8084/connectors/VALID_EMAILS_STREAM-sink-connector/status
        * http://localhost:8084/connectors/DB_EMPTY_EMAILS_STREAM-sink-connector/status
        * http://localhost:8084/connectors/XML_SPACE_EMAILS_STREAM-sink-connector/status

    * primary-ksqldb-server, and an additional-ksqldb-server containers to run KSql queries
    * ksqldb-cli: CLI just like any DB CLI to interact with topics on Kafka cluster
    * kafdrop: A simple GUI service to see brokers, topics and data being produced and consumed in the topics with offsets and partitions for topics. Accessible through: http://localhost:9000/
      ![alt text](https://github.com/sravantatikonda1893/kafka-connect-poc/blob/master/kafdrop-dash-ui.png?raw=true)

    * postgres: A DB container for Postgres out of 13 version to be used by the JDBC connect container to pull data from DB and push it to corresponding topic/s.
    * elasticsearch: A container running the search engine which stores data as documents in different indices(index -> table in RDBMS)
    * kibana: A container running a GUI based service which acts as a visualization dashboard for Elasticsearch
    * kafka-connect-poc: A container running a springboot based service to create generate users in postgres DB and orders in XML files````
    * Volumes: With this option, even after the containers are scrapped off, the data is still persisted outside the container as this will mount from the local machine to the container

**Steps to try it out this POC project on Kafka**:

1. Go to https://github.com/-OCS/kafka-connect-poc/tree/main/src/main/resources/docker-compose.yml file
2. Change the volume paths to your local file system paths
3. Docker commands:
    * To run all containers, run this command "docker-compose up -d"
    * To run a specific container, run this command "docker-compose up -d {service-name}"
      Ex: docker-compose up -d cp-zookeeper
    * Check all the current containers' status: "docker ps"
      ![alt text](https://github.com/sravantatikonda1893/kafka-connect-poc/blob/master/docker%20container%20running.png?raw=true)

    * Check for specific container logs: "docker logs -f {service-name}"
    * To login to a container: "docker exec -it {container-name} bash"
    * To kill and remove all containers: "docker-compose down --remove-orphans"

4. To create a connector instance for ether source or sink connector, use CURL command with config file.
   
 *Command to run Source Files Connector*:

  curl -sX POST http://localhost:8083/connectors -d @connect-file-pulse-xml.json --header "Content-Type: application/json"

 *Command to run Source JDBC connector*: 
  
  curl -sX POST http://localhost:8084/connectors -d @users-sync-source.json --header "Content-Type: application/json"


 *Check connector specific config: http://localhost:8083/connectors/{connector-name}/config*:


5. Go to Kafdrop UI to verify if the messages are in the topic which is configured in the JSON config file under the "topic" attribute.

**Start the POC service**:

    - Modify the settings.xml file under .m2 folder**:
    
    - Use the sample in the repo if not existing in your local and modify the below tags with your username and password for the docker hub

     <server>
        <id>docker.io</id>
        <username>sravankf244</username>
        <password>{ur docker pwd}</password>
    </server>

    - Build the jar and docker image: mvn clean package docker:build 
      
    - Push the docker image to docker hub: mvn clean package docker:push

    - docker run -t -d --name kafka-connect-poc sravankf244/kafka-connect-poc:latest

    - To access the container: docker exec -it kafka-connect-poc sh

    - Once started open the Swagger UI at: http://localhost:9999/swagger-ui.html, use this API to generate sample order records in XML files. Populate fileds(filesCount: for number of files to be generated, recordCount: Number of records in each file, usersCount: Number of user records to be loaded into the database which acts as a source DB) and click try it out, XML files would be generated. Make sure this path is mounted to the path to which the files-connect is mounted on under volumes section. So that, it will scan and push them to corresponding topic.

![alt text](https://github.com/sravantatikonda1893/kafka-connect-poc/blob/master/Swagger-API-UI.png?raw=true)


**KSQL DB**:

1. *Open KSQL CLI*:
   
   * docker exec -it ksqldb-cli ksql http://primary-ksqldb-server:8088

2. *Some commands*:
    
    * list topics;
    * list streams;

3. *Create stream out of both the topics*:

    * create stream orders_stream_topic WITH(KAFKA_TOPIC='orders',VALUE_FORMAT='AVRO');
    * select * from orders_stream_topic;
    * create stream user_records_stream_topics WITH(KAFKA_TOPIC='user_records',VALUE_FORMAT='AVRO');
    * select * from user_records_stream_topics;

4. *Create a valid_emails topic out of valid_emails_stream by joining both the tables*:VALID_EMAILS_STREAM

    * create stream valid_emails_stream WITH (PARTITIONS=5) AS select os.email  
      from orders_stream_topic os inner join user_records_stream_topics urs within 1 HOURS ON os.Email = urs.email;
    * Create a sink connector for sinking the valid emails, this will create a table "SELECT * FROM "VALID_EMAILS_STREAM";" mentioned in the config file:

            curl -sX POST http://localhost:8084/connectors -d @valid_emails-sink.json --header "Content-Type: application/json"
   
    * Go to Kafdrop UI, a new topic named: "VALID_EMAILS_STREAM", as the new messages to the user_records and orders topics flows, the VALID_EMAILS_STREAM will be updated based on the join condition we used.
    
    * Create an aggregate of order count:

   CREATE TABLE user_orders_count_stream AS
   Select os.Email, urs.user_id, urs.first_name, urs.last_name, count(os.email) as orders_count
   from orders_stream_topic os inner join user_records_stream_topics urs within 1 HOURS ON os.Email = urs.email
   Group By (os.Email,URS.LAST_NAME, URS.USER_ID, URS.FIRST_NAME);

5. *Create a stream with "empty"  in the email column in the user records table*:
   
    * create stream db_empty_emails_stream WITH (PARTITIONS=5) AS select urst.user_id from USER_RECORDS_STREAM_TOPICS urst where urst.Email = '';

6. *Create a stream with "space" in the email field in the order files*:
   
    * create stream xml_space_emails_stream WITH (PARTITIONS=5) AS select os.ID from orders_stream_topic os where os.Email = ' ';

7. *Create a sink connector for sinking the userIds' with empty email, this will create a table "SELECT * FROM "DB_EMPTY_EMAILS_STREAM";" mentioned in the config file*:
   
   curl -sX POST http://localhost:8084/connectors -d @db_empty_emails-sink.json --header "Content-Type: application/json"

6. *Create a sink connector for sinking the Ids' with whitespace email, this will create a table "select * from "XML_SPACE_EMAILS_STREAM";" mentioned in the config file*:

   curl -sX POST http://localhost:8084/connectors -d @xml_space_emails-sink.json --header "Content-Type: application/json"

**PSQL in Postgres container**:

    * Login to the container:    docker exec -it postgres  bash
    * Go to PSQL:                psql -h localhost -p 5432 -U postgres

**Get all tables in Postgres**:

    SELECT * FROM pg_catalog.pg_tables where schemaname='poc_schm';

**Go to tables created SINK**:

    SELECT * FROM poc_schm.valid_emails;
    SELECT count(*) FROM poc_schm.valid_emails;

    SELECT * FROM poc_schm.db_empty_emails;
    SELECT count(*) FROM poc_schm.db_empty_emails;

    SELECT * FROM poc_schm.xml_space_emails;
    SELECT count(*) FROM poc_schm.xml_space_emails;

**Sinking topic stream data to ElasticSearch index**:

    - Make sure the plugins are loaded: http://localhost:8085/connector-plugins

    - curl -sX POST http://localhost:8085/connectors -d @elasticsearch-orders-sink.json --header "Content-Type: application/json"

    - curl -sX POST http://localhost:8085/connectors -d @elasticsearch-users-sink.json --header "Content-Type: application/json"

    - Go to Kibana Dashboard and check the index: 

![alt text](https://github.com/sravantatikonda1893/kafka-connect-poc/blob/master/KibanaIndex.png?raw=true)

**Delete Connector instances**:

    - curl -X DELETE localhost:8084/connectors/user-recs-jdbc-connector

    - curl -X DELETE localhost:8083/connectors/orders-source-connector

    - curl -X DELETE localhost:8084/connectors/DB_EMPTY_EMAILS_STREAM-sink-connector

    - curl -X DELETE localhost:8084/connectors/XML_SPACE_EMAILS_STREAM-sink-connector

    - curl -X DELETE localhost:8084/connectors/VALID_EMAILS_STREAM-sink-connector

    - curl -X DELETE localhost:8085/connectors/elastic-search-users-sink-connector

    - curl -X DELETE localhost:8085/connectors/elastic-search-orders-sink-connector

**Flow Diagram**:
    
![alt text](https://github.com/sravantatikonda1893/kafka-connect-poc/blob/master/Flow%20Diagram.png?raw=true)


**Start an individual container**: 

    - docker-compose up -d elastic-search-connect
    - docker-compose up -d ksqldb-cli
    - docker-compose up -d primary-ksqldb-server
    - docker-compose up -d jdbc-connect
    - docker-compose up -d file-connect
    - docker-compose up -d kafdrop-ui
    - docker-compose up -d schema-registry
    - docker-compose up -d broker_2
    - docker-compose up -d broker_1
    - docker-compose up -d broker
    - docker-compose up -d kibana
    - docker-compose up -d zookeeper
    - docker-compose up -d postgres
    - docker-compose up -d elasticsearch


**Setup**:

    - Clone this repo: https://github.com/confluentinc/kafka-connect-elasticsearch
    - mvn clean package
    - Copy the contents of the target folder to the plugins path for elasticsearch connect

**Access the ES Index**:

    - curl -s http://localhost:9200/orders/_search -H 'content-type: application/json' -d '{ "size":42 }'

    - curl -s http://localhost:9200/user_records/_search -H 'content-type: application/json' -d '{ "size":42 }'

**MISC Docker**:

    - Stop all containers: docker stop (docker ps -a -q)
    - Remove all stopped containers: docker rm (docker ps -a -q)

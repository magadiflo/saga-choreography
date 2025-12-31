# SAGA Choreography | Distributed Transaction

#### Referencias tomadas para este proyecto:

- Java Tech Solutions
    - Tutorial:
      [Microservices Architecture Patterns | SAGA Design Pattern| Project Creation| Distributed Transaction](https://www.youtube.com/watch?v=WGI_ciUa3FE)
    - Repositorio: [distributed-transaction](https://github.com/JavaaTechSolutions/distributed-transaction)

- Java Techie:
    - Tutorial:
      [Saga Choreography in Microservices 🔥 Movie Ticket Booking System Explained](https://www.youtube.com/watch?v=q38plWv6h5U)
    - Repositorio: [saga-choreography](https://github.com/Java-Techie-jt/saga-choreography)

---

## Docker Compose

````yml
services:
  s-kafka-saga:
    image: apache/kafka:4.1.0
    container_name: c-kafka-saga
    restart: unless-stopped
    ports:
      - '9092:9092'
    environment:
      # Settings required for KRaft mode
      KAFKA_NODE_ID: 1
      KAFKA_PROCESS_ROLES: broker,controller
      KAFKA_CONTROLLER_LISTENER_NAMES: CONTROLLER
      KAFKA_CONTROLLER_QUORUM_VOTERS: 1@localhost:9091
      # Configure listeners for both docker and host communication
      KAFKA_LISTENERS: CONTROLLER://localhost:9091,HOST://0.0.0.0:9092,DOCKER://0.0.0.0:9093
      KAFKA_ADVERTISED_LISTENERS: HOST://localhost:9092,DOCKER://s-kafka:9093
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: CONTROLLER:PLAINTEXT,DOCKER:PLAINTEXT,HOST:PLAINTEXT
      # Listener to use for broker-to-broker communication
      KAFKA_INTER_BROKER_LISTENER_NAME: DOCKER
      # Required for a single node cluster
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
    networks:
      - saga-net

  s-postgres-order:
    image: postgres:17-alpine
    container_name: c-postgres-order
    restart: unless-stopped
    ports:
      - '5433:5432'
    environment:
      POSTGRES_DB: db_order_service
      POSTGRES_USER: order_service_user
      POSTGRES_PASSWORD: order_service_pass
    volumes:
      - postgres-order-data:/var/lib/postgresql/data
    networks:
      - saga-net

  s-postgres-payment:
    image: postgres:17-alpine
    container_name: c-postgres-payment
    restart: unless-stopped
    ports:
      - '5434:5432'
    environment:
      POSTGRES_DB: db_payment_service
      POSTGRES_USER: payment_service_user
      POSTGRES_PASSWORD: payment_service_pass
    volumes:
      - postgres-payment-data:/var/lib/postgresql/data
    networks:
      - saga-net

  s-mysql-inventory:
    image: mysql:8.0.41-debian
    container_name: c-mysql-inventory
    restart: unless-stopped
    ports:
      - '3307:3306'
    environment:
      MYSQL_DATABASE: db_inventory_service
      MYSQL_ROOT_PASSWORD: root_pass
      MYSQL_USER: inventory_service_user
      MYSQL_PASSWORD: inventory_service_pass
    volumes:
      - mysql-inventory-data:/var/lib/mysql
    networks:
      - saga-net

volumes:
  postgres-order-data:
    name: postgres-order-data
  postgres-payment-data:
    name: postgres-payment-data
  mysql-inventory-data:
    name: mysql-inventory-data

networks:
  saga-net:
    name: saga-net
````

Levantamos contenedores

````bash
D:\programming\spring\02.youtube\17.java_tech_solutions\saga-choreography (main -> origin)
$ docker compose -f ./docker/compose.yml up -d                                            
[+] Running 8/8                                                                           
 ✔ Network saga-net              Created                                                  
 ✔ Volume mysql-inventory-data   Created                                                  
 ✔ Volume postgres-order-data    Created                                                  
 ✔ Volume postgres-payment-data  Created                                                  
 ✔ Container c-kafka-saga        Started                                                  
 ✔ Container c-postgres-order    Started                                                  
 ✔ Container c-postgres-payment  Started                                                  
 ✔ Container c-mysql-inventory   Started                                                   
````

Verificamos ejecución de contenedores

````bash
$ docker container ls -a
CONTAINER ID   IMAGE                 COMMAND                  CREATED         STATUS         PORTS                                         NAMES
de41be881f1c   postgres:17-alpine    "docker-entrypoint.s…"   4 minutes ago   Up 4 minutes   0.0.0.0:5434->5432/tcp, [::]:5434->5432/tcp   c-postgres-payment
dc0cca1378d5   mysql:8.0.41-debian   "docker-entrypoint.s…"   4 minutes ago   Up 4 minutes   0.0.0.0:3307->3306/tcp, [::]:3307->3306/tcp   c-mysql-inventory
30f40521aa9a   apache/kafka:4.1.0    "/__cacert_entrypoin…"   4 minutes ago   Up 4 minutes   0.0.0.0:9092->9092/tcp, [::]:9092->9092/tcp   c-kafka-saga
a7e7858d3f75   postgres:17-alpine    "docker-entrypoint.s…"   4 minutes ago   Up 4 minutes   0.0.0.0:5433->5432/tcp, [::]:5433->5432/tcp   c-postgres-order 
````


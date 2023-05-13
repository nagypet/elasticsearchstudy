# ElasticSearch sample with Spring boot 3.0.4

## Building the project

1. start the elasticseach database in docker. Please check out the docker-compose folder.

```
CONTAINER ID   IMAGE                         COMMAND                  CREATED         STATUS       PORTS                              NAMES        
7cdc774a7c88   elasticsearch:8.5.3           "/bin/tini -- /usr/l…"   8 hours ago     Up 8 hours   0.0.0.0:9200->9200/tcp, 9300/tcp   elasticsearch
```
2. Start Spring Boot Application
3. Use Postman to test the application. Please see the postman folder.

- POST localhost:8700/api/elasticsearch/user
- GET localhost:8700/api/elasticsearch/user
- POST localhost:8700/api/elasticsearch/user/search

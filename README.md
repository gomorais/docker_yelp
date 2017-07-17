# docker_yelp

This project takes yelp dataset in json files and stores them in cassandra. The tables can then be queried used an api.
It starts 3 docker containers, using docker-compose, one with Spark, another with cassandra and another one with a python Api.
The Api container will wait for cassandra to be available and create a keyspace, while the Spark container will wait for the Api to start in order to start running the jobs.

As the dataset is quite large, the Spark job is limiting the input to 1000 rows for each file. And the Api is simply returning 10 rows from the respective database table. Each of the yelp files will create a different table.

## Running the project

The start-script.sh starts the pipeline. It expectes the path to the .tar file containing the yelp dataset. 
After that it will start the docker-compose. Just run:

          ./start-script.sh {file_path}

As soon as the process is finished the data can be queried using the api. The 5 tables are "business", "review", "user", "checkin" and "tip", named according to the file. 

To query the Api simply do:
          
          localhost:8888/api/{table_name}

Just replace {table_name} with one of the 5 names mentioned above. It will return 10 lines from each table.

## TODO

- Add Kafka and Spark streaming to process the data and store it in cassandra.
- Process the data in different ways to provide interesting queries, such as highest rated businesses per city.
- Fix sbt build

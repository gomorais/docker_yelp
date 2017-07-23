package com.morais.spark

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.storage.StorageLevel
import org.apache.spark.sql.functions._
import org.apache.spark.sql.SQLContext

import java.util.regex.Pattern
import java.util.regex.Matcher

import org.apache.spark.streaming.kafka._
import kafka.serializer.StringDecoder
import org.apache.spark.api.java.function.ForeachPartitionFunction

import com.datastax.spark.connector._
import com.twitter.jsr166e
import org.apache.spark.sql.cassandra._

object KafkaStreaming {
  
  def main(args: Array[String]) {

    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("KafkaExample")
    sparkConf.set("spark.cassandra.connection.host", "127.0.0.1")
    sparkConf.set("packages","datastax:spark-cassandra-connector:2.0.3-s_2.11")
    
    val sc = new SparkContext(sparkConf)
    // Create the context with a 1 second batch size
    //val ssc = new StreamingContext("local[*]", "KafkaExample", Seconds(1))
    val ssc = new StreamingContext(sc, Seconds(1))
    
    // hostname:port for Kafka brokers, not Zookeeper
    val kafkaParams = Map("metadata.broker.list" -> "localhost:9092")
    // List of topics you want to listen for from Kafka
    val topics = List("test").toSet
    // Create our Kafka stream, which will contain (topic,message) pairs. 
    val messages = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](
      ssc, kafkaParams, topics)
      
    val sqlContext = new SQLContext(sc)
    //df.createCassandraTable("users", "test", Some(Seq("address")), Some(Seq("city")),writeConf=)
    
    // map(_._2) at the end in order to only get the messages, which contain individual
    // lines of data.
    val lines = messages.map(_._2)
    lines.foreachRDD(row => {
      
      var df = sqlContext.read.json(row)
      //df.show()
      if (!df.rdd.isEmpty){
        
        df.write.mode("append").cassandraFormat("test", "users").save()
      }
      
      //var t = row.flatMap(x => x.split(','))
      //println(t.collect().foreach(println))
    })
   
    ssc.checkpoint("/home/goncalo/projects/spark/checkpoint/")
    ssc.start()
    ssc.awaitTermination()
  }
  
}
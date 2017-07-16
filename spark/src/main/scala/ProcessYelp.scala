package com.morais.spark

import org.apache.spark._
import org.apache.spark.SparkContext._
import org.apache.spark.sql._
import org.apache.log4j._
import com.datastax.spark.connector._
import com.twitter.jsr166e
import org.apache.spark.sql.cassandra._

object ProcessYelp {
  
  val fileMap = Map("business" -> ("yelp_academic_dataset_business.json", "state", "city"),
      "review" -> ("yelp_academic_dataset_review.json", "review_id", "user_id"),
      "tip" -> ("yelp_academic_dataset_tip.json", "text", "date"),
      "checkin" -> ("yelp_academic_dataset_checkin.json", "business_id", "type"),
      "user" -> ("yelp_academic_dataset_user.json", "user_id", "name"))
  
  /** Our main function where the action happens */
  def main(args: Array[String]) {
    
    // Set the log level to only print errors
    Logger.getLogger("org").setLevel(Level.ERROR)
    
    // Use new SparkSession interface in Spark 2.0
    val spark = SparkSession
      .builder
      .appName("MyTest")
      .config("packages","datastax:spark-cassandra-connector:2.0.3-s_2.11")
      .config("spark.cassandra.connection.host", "127.0.0.1")
      .master("local[*]")
      .getOrCreate()
    
    for ((key, value) <- fileMap) {
      println(key + "-->" + value._1)
      try {
        var df = spark.read.json("../" + value._1).limit(1000)
        df.show(2, false)
        df.createCassandraTable("users",key, Some(Seq(value._2)), Some(Seq(value._3)))
        df.write.cassandraFormat(key, "users").save()
      } catch {
        case e: Exception => e.printStackTrace
      }
    }
      
    
    //val filtered_df = df.filter("address != '' AND name != ''").limit(20)
    //val t = spark.sql("select * from users.my_table")
    //filtered_df.show(20, false)
    //df.createCassandraTable("users","business", Some(Seq("name")), Some(Seq("address")))
    //filtered_df.write.format("org.apache.spark.sql.cassandra").options(Map("table" -> "My_table", "keyspace" -> "new_one")).save()
    //df.write.cassandraFormat("business", "users").save()
  }
}
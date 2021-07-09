package com.example

import org.testcontainers.containers.KafkaContainer
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.{ StringDeserializer, StringSerializer }

trait Kafka {

  val kafkaBroker: KafkaContainer = new KafkaContainer()
  kafkaBroker.start()

  private val bootstrapServers: String = kafkaBroker.getBootstrapServers()

  val kafkaProducerSettings = ProducerSettings(actorSystem.toClassic, new StringSerializer, new StringSerializer)
    .withBootstrapServers(bootstrapServers)

}

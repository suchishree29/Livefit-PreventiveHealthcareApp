package com.springboot.kafka.project295.controller;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.kafka.project295.Config;

@RestController
public class KafkaProducerController {
	//Config myConf;
	Producer<String, byte[]> producer;
	String topic, bootstrapServers;
	Path path;
	ByteArrayOutputStream out;

	@RequestMapping(value = "/upload", method = RequestMethod.PUT)
	public String fileUpload(@RequestBody byte[] fileData) throws InterruptedException, FileNotFoundException, IOException {

		Thread.sleep(500);
		//myConf = new Config("../resources/ProducerConf.conf");

		// setting the kafka producer stuff
		Properties props = new Properties();
		props.put("bootstrap.servers", "localhost:9092");
		props.put("acks", "1");
		props.put("compression.type", "snappy");
		props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		props.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");
		producer = new KafkaProducer<>(props);
		
		topic = "exchangetopic";
		ArrayList<byte[]> allChunks;
		allChunks = splitFile("dummyFile", fileData);
		for (int i = 0; i < allChunks.size(); i++) {
			publishMessage("dummyFile", (allChunks.get(i)));
		}
		return "Upload Successful!!";
	}

	private ArrayList splitFile(String name, byte[] datum) {
		int i, l = datum.length;
		int block = 10240;
		int numblocks = l / block;
		int counter = 0, totalSize = 0;
		int marker = 0;
		byte[] chunk;
		ArrayList<byte[]> data = new ArrayList();
		for (i = 0; i < numblocks; i++) {
			counter++;
			chunk = Arrays.copyOfRange(datum, marker, marker + block);
			data.add(chunk);
			totalSize += chunk.length;
			marker += block;
		}
		chunk = Arrays.copyOfRange(datum, marker, l);
		data.add(chunk);
		// the null value is a flag to the consumer, specifying that it has reached the
		// end of the file
		data.add(null);
		return data;
	}

	private void publishMessage(String key, byte[] bytes) {
		ProducerRecord<String, byte[]> data = new ProducerRecord<>(topic, key, bytes);
		producer.send(data);

	}

}

package com.springboot.kafka.project295.controller;

import java.io.ByteArrayOutputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.config.Config;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class KafkaProducerController {
	Config myConf;
	Producer<String, byte[]> producer;
	String topic, bootstrapServers;
	Path path;
	ByteArrayOutputStream out;

	@RequestMapping(value = "/upload", method = RequestMethod.PUT)
	public String fileUpload(@RequestBody byte[] fileData, String fileName) throws InterruptedException {

		Thread.sleep(500);
		ArrayList<byte[]> allChunks;
		allChunks = splitFile(fileName, fileData);
		for (int i = 0; i < allChunks.size(); i++) {
			publishMessage(fileName, (allChunks.get(i)));
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

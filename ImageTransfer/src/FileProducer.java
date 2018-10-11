
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

/**
 *
 * @author guys
 */
public class FileProducer {
	Config myConf;
	Producer<String, byte[]> producer;
	String topic, bootstrapServers, watchDir;
	Path path;
	ByteArrayOutputStream out;

	public FileProducer(String configPath) throws IOException {
		// Read initial configuration
		myConf = new Config(configPath);

		// setting the kafka producer stuff
		Properties props = new Properties();
		props.put("bootstrap.servers", myConf.get("bootstrap.servers"));
		props.put("acks", "1");
		props.put("compression.type", "snappy");
		props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		props.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");
		producer = new KafkaProducer<>(props);

		topic = myConf.get("topic");
		watchDir = myConf.get("watchdir");
		path = FileSystems.getDefault().getPath(watchDir);

	}

	// Takes a whole binary file content and splits it into 10k chunks
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

	public void fileRead(String fileName, byte[] fileData) throws IOException, InterruptedException {
		// We need this little delay to make sure the file is closed before we read it
		Thread.sleep(500);
		ArrayList<byte[]> allChunks;
		allChunks = splitFile(fileName, fileData);
		for (int i = 0; i < allChunks.size(); i++) {
			publishMessage(fileName, (allChunks.get(i)));
		}
		System.out.println("Published file " + fileName);

	}

	private void publishMessage(String key, byte[] bytes) {
		ProducerRecord<String, byte[]> data = new ProducerRecord<>(topic, key, bytes);
		producer.send(data);

	}

	public static void main(String args[]) {
		FileProducer abp;
		try {
			abp = new FileProducer(args[0]);
			try {
				abp.start();
			} catch (InterruptedException ex) {
				Logger.getLogger(FileProducer.class.getName()).log(Level.SEVERE, null, ex);
			}
		} catch (IOException ex) {
			Logger.getLogger(FileProducer.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}
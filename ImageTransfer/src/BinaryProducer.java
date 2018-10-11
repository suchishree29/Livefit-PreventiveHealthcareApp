
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
public class BinaryProducer {
	Config myConf;
	Producer<String, byte[]> producer;
	String topic, bootstrapServers, watchDir;
	Path path;
	ByteArrayOutputStream out;

	public BinaryProducer(String configPath) throws IOException {
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
	// post(file)
	private void start() throws IOException, InterruptedException {
		String fileName;
		byte[] fileData;
		ArrayList<byte[]> allChunks;
		// the watcher watches for filesystem changes
		WatchService watcher = FileSystems.getDefault().newWatchService();
		WatchKey key;
		path.register(watcher, ENTRY_CREATE);

		while (true) {
			key = watcher.take();
			// The code gets beyond this point only when a filesystem event occurs

			for (WatchEvent<?> event : key.pollEvents()) {
				WatchEvent.Kind<?> kind = event.kind();
				// We act only if a new file was added
				if (kind == ENTRY_CREATE) {
					WatchEvent<Path> ev = (WatchEvent<Path>) event;
					Path filename = ev.context();
					fileName = filename.toString();
					// We need this little delay to make sure the file is closed before we read it
					Thread.sleep(500);

					fileData = Files
							.readAllBytes(FileSystems.getDefault().getPath(watchDir + File.separator + fileName));
					allChunks = splitFile(fileName, fileData);
					for (int i = 0; i < allChunks.size(); i++) {
						publishMessage(fileName, (allChunks.get(i)));
					}
					System.out.println("Published file " + fileName);

				}
			}
			key.reset();
		}
	}

	private void publishMessage(String key, byte[] bytes) {
		ProducerRecord<String, byte[]> data = new ProducerRecord<>(topic, key, bytes);
		producer.send(data);

	}

	public static void main(String args[]) {
		BinaryProducer abp;
		try {
			abp = new BinaryProducer(args[0]);
			try {
				abp.start();
			} catch (InterruptedException ex) {
				Logger.getLogger(BinaryProducer.class.getName()).log(Level.SEVERE, null, ex);
			}
		} catch (IOException ex) {
			Logger.getLogger(BinaryProducer.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}
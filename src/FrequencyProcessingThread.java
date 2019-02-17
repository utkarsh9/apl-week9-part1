import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

class FrequencyProcessingThread extends Thread {
	ConcurrentLinkedQueue<ConcurrentHashMap<String, Integer>> frequencySpace;
	ConcurrentHashMap<String, Integer> wordFrequencies;
	boolean stopFrequencyProcessing;

	public FrequencyProcessingThread(ConcurrentLinkedQueue<ConcurrentHashMap<String, Integer>> frequencySpace,
			ConcurrentHashMap<String, Integer> wordFrequencies) {
		this.frequencySpace = frequencySpace;
		this.wordFrequencies = wordFrequencies;
		this.stopFrequencyProcessing = false;
		this.start();
	}

	public void run() {
		mergeWordFrequencies();
	}

	public void mergeWordFrequencies() {
		ConcurrentHashMap<String, Integer> frequencies = new ConcurrentHashMap<String, Integer>();
		while (!this.stopFrequencyProcessing) {
			if (frequencySpace.isEmpty()) {
				this.stopFrequencyProcessing = true;
				break;
			}
			frequencies = frequencySpace.poll();
			for (Map.Entry<String, Integer> entry : frequencies.entrySet()) {
				int count = 0;
				if (entry.getKey().length() > 1 && wordFrequencies.containsKey(entry.getKey())) {
					count = entry.getValue() + wordFrequencies.get(entry.getKey());
				} else {
					count = entry.getValue();
				}
				wordFrequencies.put(entry.getKey(), count);
			}
		}
	}
}
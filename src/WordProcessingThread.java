import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

class WordProcessingThread extends Thread {

	HashSet<String> stopWords;
	ConcurrentLinkedQueue<String> wordSpace;
	ConcurrentLinkedQueue<ConcurrentHashMap<String, Integer>> frequencySpace;
	boolean stopWordProcessing;

	WordProcessingThread(HashSet<String> stopWords, ConcurrentLinkedQueue<String> wordSpace,
			ConcurrentLinkedQueue<ConcurrentHashMap<String, Integer>> frequencySpace) {
		this.stopWords = stopWords;
		this.wordSpace = wordSpace;
		this.frequencySpace = frequencySpace;
		this.stopWordProcessing = false;
		this.start();
	}

	public void run() {
		processWords();
	}

	public void processWords() {
		ConcurrentHashMap<String, Integer> wordFrequencies = new ConcurrentHashMap<String, Integer>();
		while (!this.stopWordProcessing) {
			if (wordSpace.isEmpty()) {
				this.stopWordProcessing = true;
				break;
			}
			String word = wordSpace.poll();
			if (word != null && !stopWords.contains(word)) {
				wordFrequencies.put(word, wordFrequencies.getOrDefault(word, 0) + 1);
			}
		}
		frequencySpace.add(wordFrequencies);
	}
}
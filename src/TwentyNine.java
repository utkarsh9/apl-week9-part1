import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class TwentyNine {

	private HashSet<String> stopWords;
	private ConcurrentLinkedQueue<String> wordSpace;
	private ConcurrentLinkedQueue<ConcurrentHashMap<String, Integer>> frequencySpace;
	private ConcurrentHashMap<String, Integer> wordFrequencies;

	TwentyNine() {
		stopWords = new HashSet<>();
		wordSpace = new ConcurrentLinkedQueue<String>();
		frequencySpace = new ConcurrentLinkedQueue<ConcurrentHashMap<String, Integer>>();
		wordFrequencies = new ConcurrentHashMap<String, Integer>();
	}

	public static void main(String[] args) throws InterruptedException {
		TwentyNine twentyNine = new TwentyNine();

		List<String> stopWords = new ArrayList<String>();
		List<String> bookWords = new ArrayList<String>();
		try {
			stopWords = Files.lines(Paths.get("stop-words.txt")).map(line -> line.split(","))
					.flatMap(Arrays::stream).collect(Collectors.toList());
			bookWords = Files.lines(Paths.get(args[0]))
					.flatMap(line -> Arrays.stream(line.split("[\\s,;:?._!--]+"))).map(s -> s.toLowerCase())
					.collect(Collectors.toList());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		twentyNine.stopWords.addAll(stopWords);

		for (int i = 0; i < bookWords.size(); i++) {
			if (!"".equals(bookWords.get(i)) && !"s".equals(bookWords.get(i)))
				twentyNine.wordSpace.add(bookWords.get(i));
		}

		CopyOnWriteArrayList<WordProcessingThread> wordWorkers = new CopyOnWriteArrayList<>();
		for (int i = 0; i < 5; i++) {
			wordWorkers.add(
					new WordProcessingThread(twentyNine.stopWords, twentyNine.wordSpace, twentyNine.frequencySpace));
		}

		for (WordProcessingThread thread : wordWorkers) {
			thread.join();
		}

		CopyOnWriteArrayList<FrequencyProcessingThread> frequencyWorkers = new CopyOnWriteArrayList<>();
		for (int i = 0; i < 5; i++) {
			frequencyWorkers.add(new FrequencyProcessingThread(twentyNine.frequencySpace, twentyNine.wordFrequencies));
		}

		for (FrequencyProcessingThread thread : frequencyWorkers) {
			thread.join();
		}

		List<Map.Entry<String, Integer>> list = new LinkedList<Map.Entry<String, Integer>>(
				twentyNine.wordFrequencies.entrySet());

		Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
			public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
				return (o2.getValue()).compareTo(o1.getValue());
			}
		});
		int count = 0;
		Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
		for (Map.Entry<String, Integer> entry : list) {
			count++;
			if (count >= 26)
				break;
			sortedMap.put(entry.getKey(), entry.getValue());
		}

		for (Map.Entry<String, Integer> reEntry : sortedMap.entrySet()) {
			System.out.println(reEntry.getKey() + "  " + "-  " + reEntry.getValue());
		}
	}
}
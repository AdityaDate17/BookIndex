import java.io.*;
import java.util.*;

class BookIndexer {
    private Set<String> excludeWords;
    private Map<String, Set<Integer>> wordIndex;

    public BookIndexer() {
        excludeWords = new HashSet<>();
        wordIndex = new TreeMap<>();
    }

    public void readExcludeWords(String filename) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                excludeWords.add(line.trim().toLowerCase());
            }
        }
    }

    public void processPages(String[] filenames) throws IOException {
        for (int i = 0; i < filenames.length; i++) {
            String filename = filenames[i];
            try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] words = line.trim().toLowerCase().split("\\s+");
                    for (String word : words) {
                        if (!excludeWords.contains(word)) {
                            Set<Integer> pages = wordIndex.getOrDefault(word, new HashSet<>());
                            pages.add(i + 1);
                            wordIndex.put(word, pages);
                        }
                    }
                }
            }
        }
    }

    public void generateIndex(String filename) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (Map.Entry<String, Set<Integer>> entry : wordIndex.entrySet()) {
                String word = entry.getKey();
                Set<Integer> pages = entry.getValue();
                writer.write(word + " : " + formatPages(pages));
                writer.newLine();
            }
        }
    }

    private String formatPages(Set<Integer> pages) {
        StringBuilder sb = new StringBuilder();
        for (int page : pages) {
            sb.append(page).append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }
}

public class Book {
    public static void main(String[] args) {
        String[] pageFilenames = { "Page1.txt", "Page2.txt", "Page3.txt" };
        String excludeWordsFilename = "exclude-words.txt";
        String indexFilename = "index.txt";

        BookIndexer bookIndexer = new BookIndexer();

        try {
            bookIndexer.readExcludeWords(excludeWordsFilename);
            bookIndexer.processPages(pageFilenames);
            bookIndexer.generateIndex(indexFilename);
            System.out.println("Index generated!");
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}

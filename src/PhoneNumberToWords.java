import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PhoneNumberToWords {
	
	private static final String SEVEN_DIGITS = "4448786";
	private static final String HIGUSTO = "higusto";
	
	private static final String TEN_DIGITS = "4355648786";
	private static final String HELLOGUSTO = "hellogusto";
	
	private static final int NUMBER_OF_RUNS = 500;
	
	private static final String DICTIONARY_FILE_NAME = "english-words.txt";
	
	private static Set<String> dictionary;
	private Map<String, List<String>> results = new HashMap<String, List<String>>();
	private int count = 0;
	
	private static final Map<Integer, String> phoneLetters;
    static {
       Map<Integer, String> map = new HashMap<Integer, String>();
        map.put(2, "abc");
        map.put(3, "def");
        map.put(4, "ghi");
        map.put(5, "jkl");
        map.put(6, "mno");
        map.put(7, "pqrs");
        map.put(8, "tuv");
        map.put(9, "wxyz");
        map.put(0, "");
        phoneLetters = Collections.unmodifiableMap(map);
    }

    public PhoneNumberToWords() throws IOException {
        populateDictionary();
    }
    
    private static void populateDictionary() throws IOException {
    	Path path = Paths.get(DICTIONARY_FILE_NAME);
        byte[] readBytes = Files.readAllBytes(path);
        String wordListContents = new String(readBytes, "UTF-8");
        String[] words = wordListContents.split("\n");
        dictionary = new HashSet<>();
        Collections.addAll(dictionary, words);
    }
    
    private void initialize() {
		count = 0;
		results = new HashMap<String, List<String>>();
	}
    
    public boolean dictionaryContains(String word) {
    	count++;
        return dictionary.contains(word);
    }
    
	/**
	 * We will find all the combination and for each of them we will find the 
	 * possible words.
	 */
	public void findWords1(String digits) {
		initialize();
		List<String> wordChoices = getLetterCombinations(digits);
		for (String word:wordChoices) {
			findWords(word, word, new ArrayList<String>());
		}
	}

    /**
     * Get all the letter combinations from a number using the phone letters,
     * ie 2 can be a,b or c, 3 can be d,e or f...
     * @param digits the number
     * @return a List of all the generated strings
     */
	public List<String> getLetterCombinations(String digits) {
	    List<String> result = new ArrayList<String>();
	    if(digits == null || digits.length() == 0) {
	        return result;
	    } 
	    getString(digits, "", result);
	 
	    return result;
	}
	 
	private void getString(String digits, String wordInProgress, List<String> result){
	    if(digits.length() == 0){
	        result.add(wordInProgress);
	        return;
	    }
	 
	    Integer curr = Integer.valueOf(digits.substring(0,1));
	    String letters = phoneLetters.get(curr);
	    for(int i=0; i<letters.length(); i++){
	        getString(digits.substring(1), wordInProgress+letters.charAt(i), result);
	    }
	}
	
	private void findWords(String input, String suffix, List<String> currentResults) {
        if (suffix.length() ==  0) {
        	addResult(input, currentResults);
            return;
        }

        for (int i = 0; i < suffix.length(); i++) {
        	String word = suffix.substring(0,i+1);
        	if (dictionaryContains(word)) {
        		currentResults.add(word);
            	findWords(input, suffix.substring(i+1), currentResults);
            	currentResults.remove(currentResults.size()-1);
        	}
        }
    }
	
	private void addResult(String key, List<String> result) {
		List<String> currentResults = results.get(key);
    	if (currentResults == null) {
    		currentResults = new ArrayList<String>();
    	}
    	currentResults.add(result.toString());
    	results.put(key, currentResults);
	}
	
	/**
	 * This will be a dynamic search, we will look at the first
	 * digit and explore each letter. If the letter is a word we will 
	 * look for words in the remaining digits. Then we will look for
	 * a word starting with that letter and the next digit
	 */
	public void findWords2(String digits) {
		initialize();
		findWords2(digits, digits, "",  "", new ArrayList<String>());
	}
	
	/**
	 * 
	 * @param digits is the original phone number
	 * @param suffix is the phone number getting smaller while we search
	 * @param currentWord is the word we are trying to find in the dictionary, if we find one it will be added to the result and become empty for the next word
	 * @param key is the string representation of the digits we are building, it will end up being the key in our results map i.e. “hellogusto” 
	 * @param currentResults is a list of words getting filled i.e. “hello”,”gusto”
	 */
	private void findWords2(String digits, String suffix, String currentWord, String key, List<String> currentResults) {
        if (suffix.length() ==  0) {
        	if (currentResults == null || currentResults.size() == 0){ 
        		return;
        	}
        	if (key.length() != digits.length()) {
        		return;
        	}
        	addResult(key, currentResults);
            return;
        }
        
        Integer digit = Integer.valueOf(suffix.charAt(0)+"");
    	String letters = phoneLetters.get(digit);
    	for (int letterCount = 0; letterCount<letters.length(); letterCount++) {
    		String word =  currentWord + letters.charAt(letterCount);
    		if (dictionaryContains(word)) {
    			currentResults.add(word);
    			findWords2(digits, suffix.substring(1), "", key+word, currentResults);
    			currentResults.remove(currentResults.size()-1);
    		} 
    		findWords2(digits, suffix.substring(1), word, key, currentResults);
    	}
    }
	
	public static void main(String[] args) {
		try {
			PhoneNumberToWords dictionary = new PhoneNumberToWords();
			
			long startTime = System.currentTimeMillis();
			for (int i=0;i<NUMBER_OF_RUNS;i++) {
				dictionary.findWords1(SEVEN_DIGITS);
			}
			long endTime = System.currentTimeMillis();
			System.out.println(NUMBER_OF_RUNS+" runs for "+SEVEN_DIGITS+" using the first search took "+ (endTime-startTime)+"ms");
			dictionary.displayResults(HIGUSTO);
			
			
			Map<String, List<String>> firstResults = dictionary.results;
			
			startTime = System.currentTimeMillis();
			for (int i=0;i<NUMBER_OF_RUNS;i++) {
				dictionary.findWords2(SEVEN_DIGITS);
			}
			endTime = System.currentTimeMillis();
			System.out.println(NUMBER_OF_RUNS+" runs for "+SEVEN_DIGITS+" using the second search took "+ (endTime-startTime)+"ms");
			dictionary.displayResults(HIGUSTO);
			
			Map<String, List<String>> secondResults = dictionary.results;
			
			testResults(firstResults, secondResults);
			
			startTime = System.currentTimeMillis();
			for (int i=0;i<NUMBER_OF_RUNS;i++) {
				dictionary.findWords1(TEN_DIGITS);
			}
			endTime = System.currentTimeMillis();
			System.out.println(NUMBER_OF_RUNS+" runs for "+TEN_DIGITS+" using the first search took "+ (endTime-startTime)+"ms");
			dictionary.displayResults(HELLOGUSTO);
			
			firstResults = dictionary.results;
			
			startTime = System.currentTimeMillis();
			for (int i=0;i<NUMBER_OF_RUNS;i++) {
				dictionary.findWords2(TEN_DIGITS);
			}
			endTime = System.currentTimeMillis();
			System.out.println(NUMBER_OF_RUNS+" runs for "+TEN_DIGITS+" using the second search took "+ (endTime-startTime)+"ms");
			dictionary.displayResults(HELLOGUSTO);
			
			secondResults = dictionary.results;
			
			testResults(firstResults, secondResults);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
    }
	
	
	private void displayResults(String key) {
		Map<String, List<String>> firstResults = results;
		System.out.println("Number of results = "+ firstResults.size());
		System.out.println("Number of searches = "+ count);
		System.out.println("For "+key+" the results are " + firstResults.get(key));
		//System.out.println("Results = "+ dictionary.results);
	}
	
	private static void testResults(Map<String, List<String>> firstResults,Map<String, List<String>> secondResults) {
		if (firstResults.size() != secondResults.size()) {
			System.out.println("The results are not the same size, first results="+firstResults.size()+", second results="+secondResults.size());
		}
		for (String word : firstResults.keySet()) {
			if (!secondResults.containsKey(word)) {
				System.out.println("Missing the key "+word+" in the second results");
			} else {
				if (!firstResults.get(word).equals(secondResults.get(word))) {
					System.out.println("For the key "+word+" the results are not the same, first results="+firstResults.get(word)+", second results="+secondResults.get(word));
				}
			}
		}
		System.out.println("The results are matching.");
	}
	
}

import java.util.*;
import java.io.*;

import org.jfree.ui.RefineryUtilities;
import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.opencsv.CSVWriter;


public class WebCrawler {
	
	String firstUrl = "";
	int MAXURLS;
	Queue<String> urlQ;
	Hashtable<String, Elements> urlHT;

	Hashtable<String, LinkedList<String>> wordHT;
	
	static String stopWordLink = "http://ir.dcs.gla.ac.uk/resources/linguistic_utils/stop_words";
	
	Hashtable<String, String> stopWords;
	
	public WebCrawler(String u, int maxurls) {
		
		//Initialize queue
		urlQ = new LinkedList<String>();
		
		//Initialize HT
		urlHT = new Hashtable<String, Elements>();
		
		//Initialize wordHT
		wordHT = new Hashtable<String, LinkedList<String>>();
		
		stopWords = new Hashtable<String, String>();
		
		this.firstUrl = u;
		this.MAXURLS = maxurls;
		
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		//Validate arguments
		if (args.length != 1) {
			System.out.println("usage: WebCrawler [url]");
			return;
		}
        String url = args[0];
        System.out.println("Starting with: " + url);
        
        WebCrawler myCrawler = new WebCrawler(url, 250);
		
        myCrawler.startCrawl();
        
	}
	
	//Breadth First Search
	private void bfs(HTMLParser myParser, String currentURL) {
		
		int urlCnt = 0;
		int duplicateUrlCnt = 0;
		int depth = 0;
		
		int uniqueWordsInPage = 0;
		
		ArrayList<Integer> uniqueWordCnt = new ArrayList<Integer>();
 		
		//Enqueue URL
		urlQ.add(currentURL);
		
		//Clear HashTable
		urlHT.clear();
		
			
		while (urlQ.size() != 0 && urlCnt < this.MAXURLS) {
				
			try {
				
				uniqueWordsInPage = 0;
				
				//Get next url
				currentURL = urlQ.poll();
				
				//Parse doc
				myParser.parse(currentURL);
				//Get Links and Text
				Elements currentLinks = new Elements();
				currentLinks = myParser.getLinkElements();
				
				String text = myParser.getPageText();
				
				//From Stack Overflow: http://stackoverflow.com/questions/18830813/how-can-i-remove-punctuation-from-input-text-in-java
				String[] words = text.replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+");
				
				//Add words to words hashtable
				for (int i = 0; i < words.length; i++) {
					
					System.out.println(words[i] + " ");
					String word = words[i];
					if(this.stopWords.containsKey(word)) {
						//Bunk word
					}
					else {
						if (wordHT.containsKey(word)) {
							
							LinkedList<String> wordUrlList = wordHT.get(word);
							
							//Add currentUrl
							wordUrlList.add(currentURL);
							
							wordHT.put(word, wordUrlList);
						}
						else {
							
							//Fresh word add
							LinkedList<String> wordUrlList = new LinkedList<String>();
							
							wordUrlList.add(currentURL);
							
							wordHT.put(word, wordUrlList);
							
							uniqueWordsInPage++;
							
									
						}
					}
					
					
					
				}
				
				//Add to visited links
				this.urlHT.put(currentURL, currentLinks);
				
				//Visited Url Count
				urlCnt++;
				
				//Add unique words track
				uniqueWordCnt.add(uniqueWordsInPage);
				uniqueWordsInPage = 0;
				
				
				for (int i = 0; i < currentLinks.size(); i++) {
					
					Element nextLink = currentLinks.get(i);
					String linkString = nextLink.attr("abs:href");
					
					//Check if key exists (if url has been visited
					if (this.urlHT.containsKey(linkString)) {
						//Testing
						System.out.println("\nKey exists. Hash Table isEmpty: " + this.urlHT.isEmpty());
						
						//Count number of duplicate urls
						duplicateUrlCnt++;
					}
					
					else {
						//Testing
						System.out.println("\nKey didn't exist. Current Link: " + currentURL + " \nAdding to queue: " + linkString +
								"\nCurrent Visited URL Count: " + urlCnt);
						
						urlQ.add(linkString);
						
					}
					
				}
				
			}
			
			catch (IOException e) {
				System.out.println("There was an IO exception: " + e.getMessage());
			}
		}
		
		System.out.println("Queue Size: " + urlQ.size());
		System.out.println("Number of Duplicate Links: " + duplicateUrlCnt);
		System.out.println("Url Count: " + urlCnt);
		
		try {
			printWordStatistics(uniqueWordCnt);
		}
		
		catch (IOException e) {
			
		}
		
		
	}
	
	private void printWordStatistics(ArrayList<Integer> uniqueWordCnt) throws IOException {
		
		//Average Unique Word Count
		int sum = 0;
		for (int i = 0; i < uniqueWordCnt.size(); i++) {
			sum += uniqueWordCnt.get(i);
		}
		
		System.out.println("Average Unique Words Per Page: " + ((int)sum/uniqueWordCnt.size()));
		
		//100 most frequent words
		Hashtable<String, LinkedList<String>> tempHT = new Hashtable<String, LinkedList<String>>(this.wordHT);
		
		String[] mostFreqWords = new String[100];
		int[] wordFreq = new int[100];
		
		//Testing
		for (String key : tempHT.keySet()) {
			LinkedList<String> tempWordList = tempHT.get(key);
		    System.out.println(key + ": " + tempWordList.size());
		}
		
		for (int i = 0; i < 100 && !tempHT.isEmpty(); i++) {
			
			int high = 0;
			String highWord = "";
			for (String word: tempHT.keySet()) {
				
				LinkedList<String> tempList = tempHT.get(word);
				int wordCnt = 0;
				if (tempList != null) {
					wordCnt = tempList.size();
					
					if (high < wordCnt) {
						high = wordCnt;
						highWord = word;
					}
				}
				
			}
			//System.out.println("Rank " + (i+1) + ": " + highWord);
			
			mostFreqWords[i] = new String(highWord);
			wordFreq[i] = high;
			
			//Remove from hashTable
			tempHT.remove(highWord);
			
		}
		
		//Testing
		System.out.println(Arrays.toString(wordFreq));
		System.out.println(Arrays.toString(mostFreqWords));
		
		//Write to CSV
		CSVWriter writer = new CSVWriter(new FileWriter("histogramData.csv"), ',');
		// feed in your array (or convert your data to an array)
		for (int i = 0; i < mostFreqWords.length; i++) {
			String[] entries = {mostFreqWords[i], "" + wordFreq[i]};
			writer.writeNext(entries);
			
		}
		
		writer.close();
		
	}
	
	
	//Depth First Search
	private void dfs(HTMLParser myParser, String currentURL) {
		
		int urlCnt = 0;
		int duplicateUrlCnt = 0;
		int depth = 0;
		
		//Clear Hashtable
		urlHT.clear();
		
		//Start dfs
		boolean flag = dfsUtil(currentURL, myParser, urlCnt, duplicateUrlCnt, depth);
		
		System.out.println("Recursion Stopped: " + flag);
		
	}
	
	private boolean dfsUtil(String currentURL, HTMLParser myParser, int urlCnt, int duplicateUrlCnt, int depth) {
		
		//Base Case
		if (urlCnt >= this.MAXURLS) {
			System.out.println("Report: \n" + "Url Cnt: " + urlCnt + " \nDuplicate Url Cnt: " + duplicateUrlCnt + "\nDepth: " + depth);
			return true;
		}
		
		try {
			
			//Parse doc
			myParser.parse(currentURL);
			//Get Links and Text
			Elements currentLinks = new Elements();
			currentLinks = myParser.getLinkElements();
			
			//Add to visited links
			this.urlHT.put(currentURL, currentLinks);
			
			//Visited Url Cnt
			urlCnt++;
			
			
			for (int i = 0; i < currentLinks.size(); i++) {
				
				Element nextLink = currentLinks.get(i);
				String linkString = nextLink.attr("abs:href");
				
				//Check if key exists (if url has been visited)
				if (this.urlHT.containsKey(linkString)) {
					//Testing
					System.out.println("\nKey exists. Hash Table isEmpty: " + this.urlHT.isEmpty());
					
					//Count number of duplicate urls
					duplicateUrlCnt++;
				}
				
				else {
					//Testing
					System.out.println("\nKey didn't exist. Current Link: " + currentURL + " \nAdding to queue: " + linkString +
							"\nCurrent Visited URL Count: " + urlCnt);
					
					//No need of Queue
					//urlQ.add(linkString);
					
					if (dfsUtil(linkString, myParser, urlCnt, duplicateUrlCnt, depth+1)) return true;
					
				}
				
			}

		}
		catch (IOException e) {
			System.out.println("There was an IO exception: " + e.getMessage());
		}
		
		return false;
	}
	
	private void constructStopWordTable(HTMLParser myParser, String currentURL) {
		
		try {
			//Parse doc
			myParser.parse(currentURL);
			//Get Links and Text
			Elements currentLinks = new Elements();
			currentLinks = myParser.getLinkElements();
			
			String text = myParser.getPageText();
			
			//From Stack Overflow: http://stackoverflow.com/questions/18830813/how-can-i-remove-punctuation-from-input-text-in-java
			String[] words = text.replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+");
			
			//Add words to words hashtable
			for (int i = 0; i < words.length; i++) {
				
				System.out.println(words[i]);
				this.stopWords.put(words[i], words[i]);
				
			}			
			
		}
		catch (IOException e) {
			System.out.println("There was an IO exception: " + e.getMessage());
		}
		
		
	}
	
	private void startCrawl() {
		
		//Initialize Parser
		HTMLParser myParser = new HTMLParser();
		String currentURL = this.firstUrl;
		
		//Construct the stop words
		constructStopWordTable(myParser, stopWordLink);
		
		//Breadth First Search
		bfs(myParser, currentURL);
		
		//Depth First Search
		//dfs(myParser, currentURL);
	}
	
}

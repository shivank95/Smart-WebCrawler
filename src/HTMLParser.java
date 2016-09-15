import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;

public class HTMLParser {
	
	Document doc;
	
	public HTMLParser() {
		
	}
	public void parse (String url) throws IOException {
			
		this.doc = Jsoup.connect(url).get();  
        
	}
	
	public Elements getLinkElements() {
		
		Elements links = doc.select("a[href]");
		return links;
	}
	
	public String getPageText() {
		
		//Get the text to tokenize
		return this.doc.body().text();
		
		
	}
	
}


/*Elements links = doc.select("a[href]");

//Elements media = doc.select("[src]");
//Elements imports = doc.select("link[href]");

System.out.println("LINKS: ");

for (int i= 0; i < links.size(); i++) {
	
	Element link = links.get(i);
	
	System.out.println("Link " + (i+1) + ": " + link.attr("abs:href") + " TEXT: " + link.text());
}

String title = doc.title();  
System.out.println("title is: " + title);  

System.out.println("\n\nBody Text: " + doc.body().text());
String linkText = doc.body().text();*/

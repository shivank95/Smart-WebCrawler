import java.io.IOException;  
import org.jsoup.Jsoup;  
import org.jsoup.nodes.Document;  

public class HTMLParser {
	
	public static void main( String[] args ) throws IOException{  
		
        Document doc = Jsoup.connect("http://www.facebook.com").get();  
        String title = doc.title();  
        System.out.println("title is: " + title);  
	}  
	
}

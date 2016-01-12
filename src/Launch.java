import java.io.IOException;

import fr.sulivan.breadsoup.BreadcrumbsDetector;

public class Launch {

	public static final int DEFAULT_TIME_OUT = 20000; //milliseconds
	
	public static void main(String[] args) {
		BreadcrumbsDetector detector = new BreadcrumbsDetector();
		try {
			detector.load("http://www.nintendo-master.com/news/le-logo-du-25eme-anniversaire-de-sonic", DEFAULT_TIME_OUT);
			System.out.println(detector.getResult());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}

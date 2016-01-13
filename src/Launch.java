import java.io.IOException;

import fr.sulivan.breadsoup.BreadCrumbsTree;
import fr.sulivan.breadsoup.BreadcrumbsDetector;

public class Launch {

	public static final int DEFAULT_TIME_OUT = 20000; //milliseconds
	
	public static void main(String[] args) {
		BreadcrumbsDetector detector1 = new BreadcrumbsDetector();
		BreadcrumbsDetector detector2 = new BreadcrumbsDetector();
		try {
			detector1.load("http://www.marieclaire.fr/,maiwenn-denonce-les-maladies-cardio-vasculaires-chez-les-femmes-dans-un-clip-glacant,798137.asp", DEFAULT_TIME_OUT);
			BreadCrumbsTree tree1 = detector1.getResult().getTree();
			
			detector2.load("http://www.marieclaire.fr/,le-cours-sabre-laser-une-nouvelle-tendance-minceur-qui-fait-le-buzz,806950.asp", DEFAULT_TIME_OUT);
			BreadCrumbsTree tree2 = detector2.getResult().getTree();
			
			
			
			
			System.out.println(tree1);
			System.out.println(tree2);
			
			tree1.mergeTree(tree2);
			
			System.out.println(tree1);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}

//http://www.marieclaire.fr/,maiwenn-denonce-les-maladies-cardio-vasculaires-chez-les-femmes-dans-un-clip-glacant,798137.asp
//http://www.marieclaire.fr/,le-cours-sabre-laser-une-nouvelle-tendance-minceur-qui-fait-le-buzz,806950.asp
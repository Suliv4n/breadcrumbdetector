package fr.sulivan.breadsoup;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class BreadcrumbsDetector {


	private Document document;
	private ArrayList<BreadcrumbsCandidate> candidates;
	private URL url;
	
	public BreadcrumbsDetector(){
		candidates = new ArrayList<BreadcrumbsCandidate>();
	}
	
	
	/**
	 * Charge le document est cherche les éléments qui peuvent être des anchors.
	 * 
	 * @param url
	 * 	URL de la page à tester.
	 * @param timeout
	 * 	Timeout en millisecondes
	 * @throws IOException
	 */
	public void load(String url, int timeout) throws IOException{
		this.url = new URL(url);
		document = Jsoup.parse(this.url, timeout);

		Elements elements = document.select("body *:not(a)");
		
		for(Element element : elements){
			getAnchorsFromElement(element);
		}
		
	}
	
	

	
	/**
	 * Retourne les éléments a qui sont dans l'éléments passés en paramètre.
	 *  
	 * @param element
	 *  Un élément du Dom. 
	 * @return
	 * 	 Les éléments a dans l'élément passé en paramètre.
	 */
	private Elements getAnchorsFromElement(Element element){
		Elements anchors = element.select("a");
		
		if(anchors.size() > 0){
			BreadcrumbsCandidate candidate = new BreadcrumbsCandidate(element, this);
			if(candidate.analyze()){
				candidates.add(candidate);
			}
		}
		
		return null;
	}
	
	/*
	public void printDebug(){
		for(BreadcrumbsCandidate candidate : candidates){
			candidate.printDebug();
		}
	}*/
	
	
	/**
	 * Retourne l'élément candidat qui a le poids le plus élevé.
	 * @return
	 * 	l'élément candidat qui a le poids le plus élevé ou null si aucun candidat.
	 */
	public BreadcrumbsCandidate getResult() {
		if(candidates.size() > 0){
			Collections.sort(candidates, (c1, c2) -> {
				return ((Integer)c2.getWeight()).compareTo(c1.getWeight());
			});
			
			return candidates.get(0);
		}
		return null;
	}

	/**
	 * Retourne le document. 
	 * @return
	 * 	Le document.
	 */
	protected Element getDocument() {
		return document;
	}

	/**
	 * Retourne l'URL de la page à tester.
	 * @return
	 */
	public URL getURL() {
		return url;
	}
	
}

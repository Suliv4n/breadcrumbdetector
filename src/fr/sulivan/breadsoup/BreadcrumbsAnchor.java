package fr.sulivan.breadsoup;

import java.util.ArrayList;
import java.util.Collections;

import org.jsoup.nodes.Element;

/**
 * Représente un élément a qui se trouve dans un élément considéré comme 
 * possiblement un bread crumbs.
 *  
 * @author sbochant
 *
 */
public class BreadcrumbsAnchor {

	//element "a"
	private Element element;
	
	//Vrai si c'est un lien vers la page d'accueil
	private boolean home = false;
	
	/*
	 * Liste des éléments parents de l'anchor jusqu'à l'élément detecté comme potentiel breadcrumbs.
	 * Par exemple pour :
	 * <ul class="potentielBreadCrumbs" >
	 * 	<li><span><a href="example">Element courant</a>
	 * 
	 * path contiendra dans l'ordre : <ul>, <li>, <span>
	 */
	private ArrayList<Element> path;
	
	/**
	 * Anchor d'un potentiel breadcrumbs.
	 * 
	 * @param element
	 * 	L'élément a courrant.
	 * @param from
	 * 	Le BreadcrumbsCandidate dont l'élément "element" appartient.
	 */
	public BreadcrumbsAnchor(Element element, BreadcrumbsCandidate from) {
		this.element = element;
		//Vérifie si l'attribut href renvoie à la page d'accueil du site
		if(element.attr("abs:href").matches("https?://" + from.getContext().getURL().getHost() + "(/?index\\..{1,4}])?")){
			home = true;
		}
		
		//Initialisation path 
		path = new ArrayList<Element>();
		Element parent = element.parent(); 
		while(parent != from.getElement()){
			path.add(parent);
			parent = parent.parent();
		}
		if(path.isEmpty()){
			path.add(from.getElement());
		}
		
		Collections.reverse(path);
		
	}

	/**
	 * Serialize le path
	 * 
	 * @return
	 * 	Chaine de caractère contenant les tagnames des éléments qui constituent le path du lien depuis le potentiel breadcrumbs spérarés par un espace.
	 */
	public String getStringPath() {
		String str = "";
		for(Element element : path){
			str += element.tagName()+" ";
		}

		return str.substring(0, str.length() - 1);
	}

	/**
	 * Retourne l'élément a.
	 * 
	 * @return
	 * 	L'élément a
	 */
	public Element getElement() {
		return element;
	}

	/**
	 * Retourne vrai si le lien renvoie vers la page d'accueil du site testé.
	 * @return
	 * 	Vrai si le lien renvoie à la page d'acueil du site faux sinon.
	 */
	public boolean isHome() {
		return home;
	}
	
}

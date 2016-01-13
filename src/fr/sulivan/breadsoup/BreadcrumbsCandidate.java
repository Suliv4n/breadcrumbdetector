package fr.sulivan.breadsoup;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import fr.sulivan.breadsoup.configuration.Configurations;

/**
 * Représente un élément considéré comme eventuelle breadcrumbs.
 * 
 * @author sbochant
 *
 */
public class BreadcrumbsCandidate{
	
	/* Element ciblé */
	private Element element;
	
	/* Liens "a" enfant du BreadcrumbsCandidate */
	private ArrayList<BreadcrumbsAnchor> anchors;
	
	/* Chaîne de caractère entre chaque lien */
	private String separator = null;
	
	/* Nombre de caractères totales dans les élements anchors. */
	private int anchorsCharsNumber = 0;
	
	/* Vrai si tous les liens ont le même host que le site ciblé */
	private boolean sameHost = true;
	
	/* BreadcrumbsDetector dont le BreadcrumbsCandidate appartient */
	private BreadcrumbsDetector context;
	
	
	//Plus cette valeur est grande plus il y a de chance que ce soit peut être, quasiment probablement 
	//un éventuel breadcrumbs mais c'est pas sûr.
	private int weight = 0;
	
	/* Nombre de caractères après l'élément "element" */
	private int numberCharBefore = 0;
	/* Nombre de caractères avant l'elément "element" */
	private int numberCharsAfter = 0;
	
	/*
	 * Vrai si les liens sont du genres :
	 * www.site.fr/rub
	 * www.site.fr/rub/sousrub
	 * www.site.fr/rub/sousrub/soussousrub
	 */
	private boolean logicalLinksOrder = false;

	/*
	 * Texte du dernier lien ou du dernier élément qui n'est pas un lien.
	 * (dernier élément du breadCrumb en soit)
	 */
	private String potentialCurrentText;
	
	/**
	 * Élément considéré comme éventuel breadcrumbs
	 * 
	 * @param element
	 * 	Element testé.
	 * @param context
	 *  BreadcrumbsDetector dont le BreadcrumbsCandidate appartient
	 * 	
	 */
	public BreadcrumbsCandidate(Element element, BreadcrumbsDetector context){
		this.element = element;
		this.context = context;
		
		anchors = new ArrayList<BreadcrumbsAnchor>();
	}
	
	/**
	 * Analyse le candidat et retourne vrai s'il pourrait être un 
	 * breadcrumb, sinon false.
	 * @return
	 * 	Vrai s'il peut être considéré comme breadcrumbs, faux sinon.
	 */
	public boolean analyze(){
		
		setDeepestElement();
		
		BreadcrumbsAnchor previous = null;
		for(Element anchor : element.select("a")){
			BreadcrumbsAnchor bca = new BreadcrumbsAnchor(anchor, this);
			
			/* On considère que  si les liens qui constituent le candidat n'ont pas le même path alors c'est mort !*/

			if(previous != null && !previous.getStringPath().equals(bca.getStringPath())){
				return false;
			}
			
			if(previous != null){
				
				String previousHref = previous.getElement().attr("abs:href");
				String currentHref = bca.getElement().attr("abs:href");
				
				try {
					
					URL previousUrl = new URL(previousHref);
					URL currentUrl = new URL(currentHref);
					if(!previousUrl.getHost().equals(context.getURL().getHost())){
						sameHost = false;
					};
					
					String[] currentPath = currentUrl.getPath().split("/");
					String[] previousPath = previousUrl.getPath().split("/");

					if(currentPath.length == previousPath.length+1){
						logicalLinksOrder = true;
						for(int i = 0; i<previousPath.length;i++){
							if(!previousPath[i].equals(currentPath[i])){
								logicalLinksOrder = false;
								break;
							}
						}
					}
					
				} catch (MalformedURLException e) {
					//e.printStackTrace();
				}


			}
			
			previous = bca;
					
			anchors.add(bca);
		}
		
		/* Si c'est un sous menu ou qu'il contient des tags interdits c'est mort aussi */
		if(isSubmenu() || hasForbidenTags()){
			return false;
		}
		
		/* On tente de détecter un séparateur */
		separator = detectSeparator();
		
		/* calcule caractères */
		anchorsCharsNumber = getNumberCharsAnchor();
		
		textAfterAndBefore();
		
		giveWeight();
		
		return true;
	}
	
	/**
	 * Retourne vrai si l'élément contient des tags configurés dans 
	 * Configurations.FORBIDEN_BREAD_CRUMBS_TAGS
	 * 
	 * @return
	 *  vrai si l'élément contient des tags configurés dans Configurations.FORBIDEN_BREAD_CRUMBS_TAGS, sinon retourne faux.
	 */
	private boolean hasForbidenTags() {
		
		String cssSelector = "";
		
		for(String tag : Configurations.FORBIDEN_BREAD_CRUMBS_TAGS){
			cssSelector += tag + ",";
		}
		
		return element.select(cssSelector.substring(0, cssSelector.length() - 1)).size() > 0;
	}
	
	/**
	 * Calcul le nombre de caractères avant et après l'élement testé.
	 */
	private void textAfterAndBefore(){
		String bodyContent = context.getDocument().select("body").get(0).text().replaceAll("\n|\r", "");
		String elementContent = element.text().replaceAll("\n|\r", "");
		
		
		Pattern pattern = Pattern.compile("(.*)" + Pattern.quote(elementContent) + "(.*)");
		Matcher matcher = pattern.matcher(bodyContent);
		
		//avant
		if(matcher.matches()){
			numberCharBefore = matcher.group(1).length();
			numberCharsAfter = matcher.group(2).length();
		}
	}

	/**
	 * Retourne vrai si l'élément ressemble à un sous menu. Sinon retourne faux.
	 * 
	 * @return
	 * 	vrai si l'élément ressemble à un sous menu. Sinon retourne faux.
	 */
	private boolean isSubmenu() {
		/*
		 On essaye de détecter cette configuration :
		 <ul class="menu2level" >
		 	<li><a>item1</a></li>
		 	<li><a>item2</a></li>
		 	<li><a>item3</a></li>
		 	<li>
		 		<ul class="submenu breadcrumbslike" >
		 			<li><a>subitem1</a></li>
		 			<li><a>subitem2</a></li>
		 		</ul>
		 	</li>
		 <ul>
		 */
		
		Element parentLevel1 = element.parent();
		Element parentLevel2 = parentLevel1 != null ? parentLevel1.parent() : null;
		
		if(
				parentLevel1 != null 																	&&
				parentLevel2 != null 																	&&
				((parentLevel1.tagName().equals("li") && parentLevel2.tagName().equals("ul") )			||
				(parentLevel1.tagName().equals("ul") && parentLevel2.getElementsByTag("a").size() > 0 ))
		)
		{
			return true;
		}
		
		return false;
	}

	/**
	 * Detecte l'élément le plus profond pouvant être considéré comme breadcrumbs.
	 * Example :
	 * <div>
	 * <nav>
	 * 	<ul>
	 * 		<li><a href="http://">link 1</a></li>
	 * 		<li><a href="http://">link 2</a></li>
	 * 		<li><a href="http://">link 3</a></li>
	 * 	</ul>
	 * </nav>
	 * </div>
	 * 
	 * will set the element to ul element.
	 */
	private void setDeepestElement(){
		Elements children = element.children();
		Element deepest = element;
		
		while(children.size() == 1 && children.get(0).tagName() != "a"){
			deepest = children.get(0);
			children = deepest.children();
		}
		
		element = deepest;
	}
	
	/**
	 * Tente de trouver un séparateur entre chaque lien (typiquement un ">").
	 * Ne fonctionne que si le séparateur est du texte (pas d'image, ni de pseudo élément)
	 * 
	 * @return
	 * 	le séparateur
	 * 
	 */
	private String detectSeparator(){
		String regex = "";
		//Détections de séparateur seulement si on a plus de un lien
		if(anchors.size() > 1){
			
			for(int i=0; i<anchors.size(); i++){
				regex += Pattern.quote(anchors.get(i).getElement().text().replaceAll("\\n|\\s", ""));
				if(i<anchors.size() - 1){
					regex += "(.{1,3})";
				}
			}
			
			
			Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
			Matcher matcher = pattern.matcher(element.text().replaceAll("\\n|\\s", ""));
			
			if(matcher.find()){
				return matcher.group(1);
			}
		}
		
		return null;
	}
	
	
	/**
	 * Calcul le nombre de caractères dans les les tags "a" du candidat. Et prend en compte le dernier
	 * élément qui n'est pas un "a" (ça pourrait être l'élément actif)
	 * 
	 */
	private int getNumberCharsAnchor(){
		int totalAnchorsChar = 0;
		
		for(BreadcrumbsAnchor anchor : anchors){
			totalAnchorsChar += anchor.getElement().text().replaceAll("\\s|\\n", "").length();
		}
		
		
		//Ajouter le dernier element qui n'est pas un a
		Elements notLinks = element.select("*:not(a)");
		if(notLinks.size() > 0){
			if(notLinks.get(notLinks.size() - 1).select("a").size() == 0){
				String lastElementNotLink = notLinks.get(notLinks.size() - 1).text();
				this.potentialCurrentText = lastElementNotLink;
				totalAnchorsChar += lastElementNotLink.replaceAll("\\s|\\n", "").length();
			}
		}
		
		//Ajouter les séprateurs
		if(separator != null && anchors.size() > 1){
			totalAnchorsChar += separator.replaceAll("\\s|\\n", "").length() * (anchors.size() - 1);
		}
		
		return totalAnchorsChar;
	}

	/**
	 * Retourne l'élément testé.
	 * 
	 * @return
	 * L'élément testé
	 */
	public Element getElement() {
		return element;
	}
	
	/*
	public void printDebug(){
		System.out.println(element.cssSelector() + " | " + separator + " | " + weight + " | " + getCharRate() + " | " + numberCharBefore + " ; " + numberCharsAfter);	
	}
	*/
	
	/**
	 * Retourne le pourcentage de caractères des liens par rapport au nombre de caractères total de l'élément.
	 * 
	 * @return
	 * 	le pourcentage de caractères des liens par rapport au nombre de caractères total de l'élément.
	 */
	protected float getCharRate(){
		return (float) (float)anchorsCharsNumber / element.text().replaceAll("\\s|\\n", "").length() * 100;
	}
	
	public boolean linksHaveSameHost(){
		return sameHost;
	}
	
	/**
	 * Test une regex sur l'attribut passé en paramètre de l'élément testé et des ses parents qui 
	 * ne contiennent qu'un enfant direct.
	 * 
	 * @param attr
	 * 	L'attribut à tester.
	 * @param regex
	 * 	La regex à tester.
	 * @return
	 * 	Vrai si l'attribut match la regex. Faux sinon.
	 */
	public boolean checkContainerAttr(String attr, String regex){
		Element container = this.element;
		do{
			
			Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(container.attr(attr));
			
			
			if(matcher.matches()){
				return true;
			}
			
			if(container.parent() == null || container.parent().children().size() != 1 ){
				return false;
			}
			
			container = container.parent();
			
		}while(container != null);
		
		return false;
	}
	
	/**
	 * Assigne un poids à l'élément testé. Plus le poids est élevé, plus on peut le considérer comme breadcrumbs.
	 * Voir WeightConfigurations pour configurer les poids.
	 * 
	 * @see fr.sulivan.breadsoup.configuration.WeightConfigurations
	 * 
	 */
	protected void giveWeight(){
		weight = context.getWeigtEngine().process(this);
	}
	
	public boolean isInTopContent(){
		return numberCharsAfter > numberCharBefore;
	}
	
	/**
	 * Retourne vrai si le tagname de l'élément testé se trouve dans Configurations.LEGIT_BREADCRUMBS_TAGS.
	 * 
	 * @return
	 * 	vrai si le tagname de l'élément testé se trouve dans Configurations.LEGIT_BREADCRUMBS_TAGS, sinon faux.
	 * 
	 * @see fr.sulivan.breadsoup.configuration.Configurations
	 * 
	 */
	public boolean isLegitTag() {
		return Arrays.asList(Configurations.LEGIT_BREADCRUMBS_TAGS).contains(element.tagName());
	}

	/**
	 * Retourne le poids de l'élément. Plus il est élevé, plus il peut être considéré comme breadcrumbs.
	 * 
	 * @return
	 * 	Le poids de l'élément.
	 */
	public int getWeight(){
		return weight;
	}

	/**
	 * Retourne le BreadcrumbsDetector dans le BreadcrumbsCandidate appartient.
	 * 
	 * @return
	 * 	Le BreadcrumbsDetector dans le BreadcrumbsCandidate appartient
	 */
	protected BreadcrumbsDetector getContext() {
		return context;
	}
	
	/**
	 * Affiche l'arbo les href des anchors sous forme d'arborescence.
	 */
	public String getStringTree(){
		
		ArrayList<String> links = getUniqAnchorsAbsHref();
		String tree = "";
		
		for(int i = 0; i<links.size(); i++){
			String href = links.get(i);
			String line = "";
			for(int j = 0; j<=i;j++){
				line += i == j ? "" : " ";
			}
			tree += line + href + "\n";
		}
		
		return tree;
	}
	
	public BreadCrumbsTree getTree(){
		
		BreadCrumbsTree root = new BreadCrumbsTree(anchors.get(0).getElement().attr("abs:href"), null);
		BreadCrumbsTree previousNode = root;
		
		for(int i=1; i< anchors.size(); i++){
			BreadCrumbsTree tree = new BreadCrumbsTree(anchors.get(i).getElement().attr("abs:href"), previousNode);
			previousNode = tree;
		}
		
		return root;
	}
	
	/**
	 * Retourne vrai si :
	 * - Le texte dans le dernier élément "a" est dans le titre de la page (balise "title")
	 * - Le texte du dernier élement non "a" est dans le titre de la page
	 * 
	 * @return
	 * 	Vrai si le dernier élément "a" ou non "a" est dans le titre de la page.	
	 */
	public boolean titleMatchesCurrent(){
		
		Elements res = getContext().getDocument().select("head title");
		String actifText = potentialCurrentText == null ? anchors.get(anchors.size() - 1).getElement().text() : potentialCurrentText;
		

		
		if(res.size() > 0 && actifText.length() > 1){
			Element title = res.get(0);
			String titleText = title.text();

			return titleText.toLowerCase().matches(".*" + (Pattern.quote(actifText.toLowerCase())) + ".*");
		}
		return false;
	}

	/**
	 * Retourne le séparateur entre chaque lien ou null si aucun séparateur n'a été detecté.
	 * analyze() doit être appelé au préalable.
	 * 
	 * @return
	 * 	le séparateur entre chaque lien ou null si aucun séparateur n'a été detecté.
	 */
	public String getSeparator() {
		return separator;
	}

	/**
	 * Retourne l'élément "a" dans le breadcrumbscandidate à l'index passé en paramètre.
	 * 
	 * @param i
	 * 	Index de l'élément "a" à retourner.
	 * @return
	 * 	l'élément "a" dans le breadcrumbscandidate à l'index passé en paramètre.
	 */
	public BreadcrumbsAnchor getAnchors(int i) {
		
		return anchors.get(i);
	}
	
	/**
	 * Retourne les attributs "href" des liens qui composent le breadcrumbs en ne prenant pas comp^te des doublons.
	 * Les urls sont converties en URL absolues
	 * 
	 * @return
	 * 	Les attributs href (en absolue) des liens du BreadCrumbsCandidate 
	 */
	public ArrayList<String> getUniqAnchorsAbsHref(){
		ArrayList<String> links = new ArrayList<String>();		
		
		for(BreadcrumbsAnchor anchor : anchors){
			String link = anchor.getElement().attr("abs:href");
			if(!links.contains(link)){
				links.add(link);
			}
		}
		
		return links;
	}
	
	public boolean isOrderLogic(){
		return logicalLinksOrder;
	}
	
	@Override
	public String toString(){
		/*String string = "";
		for(String href : getUniqAnchorsAbsHref()){
			string += href + "\n";
		}
		
		return string;
		*/
		return getStringTree();
	}
}

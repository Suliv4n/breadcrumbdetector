package fr.sulivan.breadsoup.configuration;

/**
 * Configuration pour le DefaultWeightEngine
 *
 */
public class WeightConfigurations {
	/**
	 * Poids attribué lorque il y a un séparateur entre chaque lien.
	 */
	public static final int WEIGHT_HAS_SEPARATOR = 70;
	
	/**
	 * Poids attribué lorsque il n'y a pas de texte autour des liens à part les éventuels séparateurs. 
	 */
	public static final int WEIGHT_NOT_IN_TEXT = 130;
	
	/**
	 * Poids attribué lorsque l'élement est un tag de Configurations.LEGIT_BREADCRUMBS_TAGS
	 */
	public static final int WEIGHT_LEGIT_TAGS = 70;
	
	/**
	 * Poids attribué lorsque le premier lien renvoie vers la page d'accueil.
	 */
	public static final int WEIGHT_FIRST_LINK_HOME = 60;
	
	/**
	 *	Poids attribué losque l'id du parent match la regex Configurations.ID_REGEX
	 */
	public static final int WEIGHT_KEY_WORDS_MATCH_ID = 70;
	
	/**
	 *	Poids attribué losque l'id du parent match la regex Configurations.CLASS_REGEX
	 */
	public static final int WEIGHT_KEY_WORDS_MATCH_CLASS = 70;
	
	/**
	 * Poids si les liens ont le même host
	 */
	public static final int WEIGHT_SAME_HOST_LINK = 50;

	/**
	 * Poids attribué si les liens ont le même host.
	 */
	public static final int WEIGHT_SAME_HOST = 90;

	/**
	 * Poids attribué lorsque l'élément est plus tout avant le contenu de la page.
	 */
	public static final int WEIGHT_FEW_CONTENT_AFTER = 100;

	public static final int WEIGHT_LOGICAL_ORDER_LINKS = 50;

	/**
	 * Poids attribué lorsque le texte du dernier élémement se retrouve dans le title de la page.
	 */
	public static final int WEIGHT_TITLE_MATCH_LAST_ELEMENT = 50;
}

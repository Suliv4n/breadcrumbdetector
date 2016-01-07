package fr.sulivan.breadsoup.configuration;

public class Configurations {
	
	/**
	 * Liste des tags qu'on ne s'attend pas à voir dans un breadcrumbs.
	 */
	public static final String[] FORBIDEN_BREAD_CRUMBS_TAGS = new String[]{"form", "input", "fieldset", "textarea", "select", "script"};
	
	public static final String ID_REGEX = ".*((fil)?ariane|breadcrumbs?).*";
	public static final String CLASS_REGEX = ".*((fil)?ariane|breadcrumbs?).*";
	
	public static final String[] LEGIT_BREADCRUMBS_TAGS = new String[]{"ul", "ol"};
}

package fr.sulivan.breadsoup.engine;

import fr.sulivan.breadsoup.configuration.Configurations;
import fr.sulivan.breadsoup.configuration.WeightConfigurations;

public class DefaultWeightEngine extends ConfigurableWeightEngine{
	
	public static final int SAME_HOST_LINK = 1;
	public static final int FEW_CONTENT = 2;
	public static final int FIRST_LINK_HOME = 3;
	public static final int HAS_SEPARATOR = 4;
	public static final int KEY_WORDS_MATCH_CLASS = 5;
	public static final int KEY_WORDS_MATCH_ID = 6;
	public static final int LEGIT_TAGS = 7;
	public static final int TITLE_MATCH_LAST_ELEMENT = 8;
	public static final int NOT_IN_TEXT = 9;
	public static final int LOGICAL_ORDER_LINKS = 10;
	
	public DefaultWeightEngine(){
		add(SAME_HOST_LINK			,new WeightRule(WeightConfigurations.WEIGHT_SAME_HOST_LINK, bca -> bca.linksHaveSameHost() ));
		add(FEW_CONTENT				,new WeightRule(WeightConfigurations.WEIGHT_FEW_CONTENT_AFTER, bca -> bca.isInTopContent() ));
		add(FIRST_LINK_HOME			,new WeightRule(WeightConfigurations.WEIGHT_FIRST_LINK_HOME, bca -> bca.getAnchors(0).isHome() ));
		add(HAS_SEPARATOR			,new WeightRule(WeightConfigurations.WEIGHT_HAS_SEPARATOR, bca -> bca.getSeparator() != null ));
		add(KEY_WORDS_MATCH_CLASS	,new WeightRule(WeightConfigurations.WEIGHT_KEY_WORDS_MATCH_CLASS, bca -> bca.checkContainerAttr("class", Configurations.CLASS_REGEX) ));
		add(KEY_WORDS_MATCH_ID		,new WeightRule(WeightConfigurations.WEIGHT_KEY_WORDS_MATCH_ID, bca -> bca.checkContainerAttr("id", Configurations.ID_REGEX) ));
		add(LEGIT_TAGS				,new WeightRule(WeightConfigurations.WEIGHT_LEGIT_TAGS, bca -> bca.isLegitTag() ));
		add(TITLE_MATCH_LAST_ELEMENT,new WeightRule(WeightConfigurations.WEIGHT_TITLE_MATCH_LAST_ELEMENT, bca -> bca.titleMatchesCurrent() ));
		add(NOT_IN_TEXT				,new WeightRule(WeightConfigurations.WEIGHT_NOT_IN_TEXT, bca -> bca.titleMatchesCurrent() ));
		add(LOGICAL_ORDER_LINKS		,new WeightRule(WeightConfigurations.WEIGHT_LOGICAL_ORDER_LINKS, bca -> bca.isOrderLogic() ));
	}
}

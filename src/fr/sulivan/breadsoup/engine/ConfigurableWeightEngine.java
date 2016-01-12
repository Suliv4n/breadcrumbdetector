package fr.sulivan.breadsoup.engine;

import java.util.HashMap;

import fr.sulivan.breadsoup.BreadcrumbsCandidate;

public class ConfigurableWeightEngine {
	private HashMap<Integer, WeightRule> rules;
	
	public ConfigurableWeightEngine(){
		rules = new HashMap<Integer, WeightRule>();
	}
	
	public void add(int id, WeightRule rule){
		rules.put(id, rule);
	}
	
	public int process(BreadcrumbsCandidate candidate){
		int weight = 0;
		
		for(WeightRule rule : rules.values()){
			if(rule.process(candidate) ){
				weight += rule.getWeight();
			}
		}
		
		return weight;
	}
	
	public WeightRule getRule(int id){
		return rules.get(id);
	}
}

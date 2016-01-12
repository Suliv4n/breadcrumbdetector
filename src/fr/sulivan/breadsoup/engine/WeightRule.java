package fr.sulivan.breadsoup.engine;

import java.util.function.Function;

import fr.sulivan.breadsoup.BreadcrumbsCandidate;

public class WeightRule {

	private Function<BreadcrumbsCandidate, Boolean> process;
	private int weight;
	
	
	public WeightRule(int weight, Function<BreadcrumbsCandidate, Boolean>process){
		this.weight = weight;
		this.process = process;
	}


	public boolean process(BreadcrumbsCandidate candidate) {
		return process.apply(candidate);
	}


	public int getWeight() {
		return weight;
	}
	
	public void setWeight(int weight) {
		this.weight = weight;
	}
	
	public void setProcess(Function<BreadcrumbsCandidate, Boolean> process){
		this.process = process;
	}
	
	
}

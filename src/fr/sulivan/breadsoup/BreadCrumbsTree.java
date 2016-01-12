package fr.sulivan.breadsoup;

import java.util.ArrayList;

public class BreadCrumbsTree {
	
	private BreadCrumbsTree parent = null;
	private String link;
	private ArrayList<BreadCrumbsTree> children;
	
	public BreadCrumbsTree(String link, BreadCrumbsTree parent) {
		this.parent = parent;
		this.link = link;
		
		children = new ArrayList<BreadCrumbsTree>();
		
		if(parent != null){
			parent.addChild(this);
		}
	}

	private void addChild(BreadCrumbsTree child) {
		children.add(child);
	}
	
	public String stringTree(int indent){
		String line = isRoot() || parent.isRoot() ? "├" : "│";

		for(int i=0; i<indent-2; i++){
			line += parent.isRoot() ? "───" : "   ";
		}
		String tree = "";
		if(!isRoot()){
			tree = line + (parent.isRoot() ? "───" : "└───") + link + "\n";
		}
		for(BreadCrumbsTree child : children){
			tree += child.stringTree(indent+1);
		}
		
		return tree;
	}
	
	private boolean isRoot() {
		return parent == null;
	}

	@Override
	public String toString(){
		String string = link+"\n";
		string += stringTree(1);
		
		return string;
	}
	
	
}

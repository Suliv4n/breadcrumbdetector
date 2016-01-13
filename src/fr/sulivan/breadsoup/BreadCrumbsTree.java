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
			parent.children.add(this);
		}
	}

	public BreadCrumbsTree addChild(String childurl) {
		return new BreadCrumbsTree(childurl, this);
	}
	
	private String createLine(int indent){
		if(indent == 0){
			return "";
		}
		
		String line = "";
		if(isLastChild()){
			line = "└──";
		}
		else{
			line = "├──";
		}
		for(int i=0; i<indent; i++){

			if(getParentLevel(i+1).parent != null && !getParentLevel(i+1).isLastChild()){
				line = "│  " + line;
			}
			else{
				line = "   " + line;
			}
			
		}
		
		
		return line;
	}
	
	private BreadCrumbsTree getParentLevel(int i){
		if(i <= 0){
			return this;
		}
		else{
			int level = i;
			BreadCrumbsTree res = parent;
			while ( level > 1){
				level--;
				if(res != null){
					res = res.parent;
				}
				else{
					return null;
				}
			}
			return res;
		}
	}
	
	public String stringTree(int indent){

		String tree = "";

		tree = createLine(indent) + link + "\n";

		for(BreadCrumbsTree child : children){
			tree += child.stringTree(indent+1);
		}
		
		return tree;
	}
	
	private boolean isRoot() {
		return parent == null;
	}
	
	private boolean isLastChild(){
		return parent.children.indexOf(this) == parent.children.size() - 1;
	}
	
	public void mergeTree(BreadCrumbsTree branch){
		BreadCrumbsTree exists = getNodeByUrl(branch.link);
		if(getNodeByUrl(branch.link) != null){
			for(BreadCrumbsTree node : branch.children){
				exists.mergeTree(node);
			}
		}
		else{
			BreadCrumbsTree child = addChild(branch.link);
			for(BreadCrumbsTree node : branch.children){
				child.mergeTree(node);
			}
		}
	}
	
	public BreadCrumbsTree getNodeByUrl(String url){
		if(link.equals(url)){
			return this;
		}
		else if(children.size() > 0){
			for(BreadCrumbsTree node : children){
				BreadCrumbsTree res = node.getNodeByUrl(url);
				if( res != null){
					return res;
				}
			}
		}
		return null;
	}

	@Override
	public String toString(){
		String string = stringTree(0);
		
		return string;
	}
	
	
}

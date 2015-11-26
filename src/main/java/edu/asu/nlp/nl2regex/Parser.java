package edu.asu.nlp.nl2regex;

import java.util.List;

import nl2kr.ccgparsing.ParseTreeNode;

public class Parser {
	private List<ParseTreeNode> parseTreeList;
	public Parser(List<ParseTreeNode> functionList){
		parseTreeList = functionList;
	}	
	public String getRegEx(){
		ParseTreeNode bestParseTree = getBestParseTreeNode();
		if(bestParseTree != null){
			return parse(bestParseTree);
		}
		return "";
	}	
	private String parse(ParseTreeNode tree){
		
		return "";
	}	
	private ParseTreeNode getBestParseTreeNode(){
		for(ParseTreeNode tree : parseTreeList){
			if(!tree.toString().contains("l(")){
				return tree;
			}
		}
		return null;
	}
}

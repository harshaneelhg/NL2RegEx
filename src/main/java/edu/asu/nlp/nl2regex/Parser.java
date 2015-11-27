package edu.asu.nlp.nl2regex;

import java.util.HashMap;
import java.util.List;

import nl2kr.ccgparsing.ParseTreeNode;
import nl2kr.lambda.newImpl.FFunction;
import nl2kr.lambda.newImpl.LNumber;
import nl2kr.lambda.newImpl.LVariable;

public class Parser {
	private List<ParseTreeNode> parseTreeList;
	private HashMap<String, String> map;
	public Parser(List<ParseTreeNode> functionList){
		parseTreeList = functionList;
		map = new HashMap<>();
		map.put("contain1", ".*:x:.*");
		map.put("contain2", ".*:y:.*");
		map.put("end1", ".*:x:");
		map.put("end2", ".*:y:");
		map.put("start1", ":x:.*");
		map.put("start2", ":y:.*");
		map.put("before1", ":x:.*:y:");
		map.put("before2", ":x:.*:y:");
		map.put("after1", ":y:.*:x:");
		map.put("after2", ":y:.*:x:");
		map.put("word1", "(\\b:x:\\b)");
		map.put("word2", "(\\b:y:\\b)");
		map.put("letter1", ":x:");
		map.put("letter2", ":x:");
		map.put("not1", "~(:x:)");
		map.put("not2", "~(:x:)");
		map.put("and1", "(:x:&:y:)");
		map.put("and2", "(:x:&:y:)");
		map.put("or1", "(:x:|:y:)");
		map.put("or2", "(:x:|:y:)");
		map.put("atleast1", "(:y:){:x:}");
		map.put("atleast2", "(:y:){:x:}");
	}	
	public String getRegEx(){
		ParseTreeNode bestParseTree = getBestParseTreeNode();
		if(bestParseTree != null){
			FFunction f = getFFunction(bestParseTree);
			return parse(f);
		}
		return "";
	}	
	private String parse(Object tree){
		if(tree instanceof LVariable){
			return ((LVariable)tree).getName();
		}
		if(tree instanceof LNumber){
			return ((LNumber)tree).getName();
		}
		String fname = ((FFunction)tree).getFname().toString();
		List arglist = ((FFunction)tree).getArguments();
		int len = arglist.size();
		String ret = map.get(fname+len);
		if(len == 2){
			ret = ret.replace(":x:", parse(arglist.get(0)));
			ret = ret.replace(":y:", parse(arglist.get(1)));
		}
		if(len == 1){
			ret = ret.replace(":x:", parse(arglist.get(0)));
		}
		return ret;
	}	
	private ParseTreeNode getBestParseTreeNode(){
		for(ParseTreeNode tree : parseTreeList){
			if(!tree.toString().contains("l(")){
				return tree;
			}
		}
		return null;
	}
	private FFunction getFFunction(ParseTreeNode tree){
		return (FFunction) (tree.getNodeContent().getLambda());
	}
}

package edu.asu.nlp.nl2regex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import nl2kr.ccgparsing.ParseTreeNode;
import nl2kr.lambda.newImpl.FFunction;
import nl2kr.lambda.newImpl.LNumber;
import nl2kr.lambda.newImpl.LVariable;

public class Parser {
	private List<ParseTreeNode> parseTreeList;
	private HashMap<String, String> map;
	private HashMap<String, String> m_hmApplyParentFirst;
	private ArrayList<Object> alIsApplied = new ArrayList<Object>();
	private HashMap<String,String> hmKeyWordMap = new HashMap<String, String>();
	public Parser(List<ParseTreeNode> functionList) {
		parseTreeList = functionList;
		map = new HashMap<>();
		map.put("contain1", ".*:x:.*");
		map.put("contain2", ".*:y:.*");
		map.put("ends1", ".*:x:");
		map.put("ends2", ".*:y:");
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
		map.put("not2", "~(:y:)");
		map.put("and1", "(:x:&:y:)");
		map.put("and2", "(:x:&:y:)");
		map.put("or1", "(:x:|:y:)");
		map.put("or2", "(:x:|:y:)");
		map.put("atleast1", "(:y:){:x:}");
		map.put("atleast2", "(:y:){:x:}");

		m_hmApplyParentFirst = new HashMap<String, String>();
		m_hmApplyParentFirst.put("and", "contain");
		m_hmApplyParentFirst.put("or", "contain");
		hmKeyWordMap.put("word", "\\b[A-Z][a-z]\\b");
		hmKeyWordMap.put("vowel", "[AEIOUaeiou]");
		hmKeyWordMap.put("vowels", "[AEIOUaeiou]+");
		hmKeyWordMap.put("words", "(\\b[A-Z][a-z]\\b)+");
		hmKeyWordMap.put("number", "[0-9]");
		hmKeyWordMap.put("numbers", "[0-9]+");
		hmKeyWordMap.put("digits", "[0-9]+");
	}

	public String getRegEx() {
		ParseTreeNode bestParseTree = getBestParseTreeNode();
		if (bestParseTree != null) {
			FFunction f = getFFunction(bestParseTree);
			return parse(f, null);
		}
		return "";
	}

	private String parse(Object tree, Object immediateParent) {
		if (tree instanceof LVariable) {
			if(hmKeyWordMap.containsKey(((LVariable) tree).getName()))
			{
				return hmKeyWordMap.get(((LVariable) tree).getName());
			}
			return ((LVariable) tree).getName();
		}
		if (tree instanceof LNumber) {
			if(hmKeyWordMap.containsKey(((LNumber) tree).getName()))
			{
				return hmKeyWordMap.get(((LNumber) tree).getName());
			}
			return ((LNumber) tree).getName();
		}
		String fname = ((FFunction) tree).getFname().toString();

		List arglist = ((FFunction) tree).getArguments();
		int len = arglist.size();
		String ret = map.get(fname + len);
		if (len == 2) {
			String x = parse(arglist.get(0), tree);
			String y = parse(arglist.get(1), tree);
			String sImmediateParent = m_hmApplyParentFirst.get(fname);
			if (sImmediateParent != null
					&& immediateParent != null
					&& sImmediateParent.equals(((FFunction) immediateParent)
							.getFname().toString().trim())) {
				x = map.get(sImmediateParent + 1).replace(":x:", x);
				y = map.get(sImmediateParent + 1).replace(":x:", y);
				alIsApplied.add(immediateParent);
			}
			if (alIsApplied.contains(tree))
				return y;
			ret = ret.replace(":x:", x);
			ret = ret.replace(":y:", y);
		}
		if (len == 1) {
			String x = parse(arglist.get(0), tree);
			if (alIsApplied.contains(tree))
				return x;
			ret = ret.replace(":x:", x);
		}
		alIsApplied.add(tree);
		return ret;
	}

	private ParseTreeNode getBestParseTreeNode() {
		for (ParseTreeNode tree : parseTreeList) {
			if (!tree.toString().contains("l(")) {
				return tree;
			}
		}
		return null;
	}

	private FFunction getFFunction(ParseTreeNode tree) {
		return (FFunction) (tree.getNodeContent().getLambda());
	}
}

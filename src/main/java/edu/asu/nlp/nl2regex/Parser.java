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
	private HashMap<String, String> hmKeyWordMap = new HashMap<String, String>();

	public Parser(List<ParseTreeNode> functionList) {
		parseTreeList = functionList;
		map = new HashMap<>();
		/*
		 * this map contains function translation and the aruguments replacement
		 */
		map.put("contain1", ".*:x:.*");
		map.put("contain2", ".*:y:.*");
		map.put("ends1", ".*:x:");
		map.put("ends2", ".*:y:");
		map.put("starts1", ":x:.*");
		map.put("starts2", ":y:.*");
		map.put("before1", ":x:.*:y:");
		map.put("before2", ":x:.*:y:");
		map.put("after1", ":y:.*:x:");
		map.put("after2", ":y:.*:x:");
		map.put("word1", "(\\b:x:\\b)");
		map.put("word2", "(\\b:y:\\b)");
		map.put("words1", "(\\b:x:\\b)+");
		map.put("words2", "(\\b:y:\\b)+");
		map.put("letter1", ":x:");
		map.put("letter2", ":x:");
		map.put("number1", ":x:");
		map.put("not1", "~(:x:)");
		map.put("not2", "~(:y:)");
		map.put("and1", "(:x:&:y:)");
		map.put("and2", "(:x:&:y:)");
		map.put("or1", "(:x:|:y:)");
		map.put("or2", "(:x:|:y:)");
		map.put("atleast1", "(:y:){:x:}");
		map.put("atleast2", "(:y:){:x:}");
		map.put("len2", "(:y:){:x:}");

		/* override map for contains case */
		m_hmApplyParentFirst = new HashMap<String, String>();
		m_hmApplyParentFirst.put("and", "contain");
		m_hmApplyParentFirst.put("or", "contain");

		/* translations for key words in the functions */
		hmKeyWordMap.put("word", "\\b[A-Za-z]\\b");
		hmKeyWordMap.put("vowel", "[AEIOUaeiou]");
		hmKeyWordMap.put("vowels", "[AEIOUaeiou]+");
		hmKeyWordMap.put("words", "(\\b[A-Za-z]\\b)+");
		hmKeyWordMap.put("number", "[0-9]");
		hmKeyWordMap.put("numbers", "[0-9]+");
		hmKeyWordMap.put("digits", "[0-9]+");
		hmKeyWordMap.put("letters", "[A-Za-z]+");
		hmKeyWordMap.put("letter", "[A-Za-z]");
		hmKeyWordMap.put("digits", "[0-9]+");
		hmKeyWordMap.put("digit", "[0-9]+");
		hmKeyWordMap.put("characters", ".*");
		hmKeyWordMap.put("character", ".");
		
	}

	public String getRegEx() {
		/* get 2 best parse trees */
		ArrayList<ParseTreeNode> bestParseTree = getBestParseTreeNode();
		String sReturns = "";
		/* for each parse tree generate regular expression and output */
		if (bestParseTree != null) {
			for (ParseTreeNode pBest : bestParseTree) {
				FFunction f = getFFunction(pBest);
				try {
					sReturns += parse(f, null) + "\n";
				} catch (Exception e) {
					System.out.println("exception during conversion : " + pBest
							+ " : " + e.getMessage());
				}
			}
		}
		return sReturns;
	}

	/**
	 * This is a recursive method, goes till the bottom of the tree to resolve
	 * arguments of the child functions
	 * 
	 * @param tree
	 * @param immediateParent
	 * @return
	 */
	private String parse(Object tree, Object immediateParent) {
		if (tree instanceof LVariable) {
			/* handling for base cases numbers/values */
			if (hmKeyWordMap.containsKey(((LVariable) tree).getName())) {
				return hmKeyWordMap.get(((LVariable) tree).getName());
			}
			return ((LVariable) tree).getName();
		}
		if (tree instanceof LNumber) {
			if (hmKeyWordMap.containsKey(((LNumber) tree).getName())) {
				return hmKeyWordMap.get(((LNumber) tree).getName());
			}
			return ((LNumber) tree).getName();
		}
		/* get function name */
		String fname = ((FFunction) tree).getFname().toString();

		List arglist = ((FFunction) tree).getArguments();
		int len = arglist.size();
		String ret = map.get(fname + len);
		/* take actions according to length */
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
			if (fname.equals("atleast")) {
				ret = x.replace("}", ",}");
			} else if (fname.equals("uppercase")) {
				ret = x.toUpperCase();
			} else if (fname.equals("lowercase")) {
				ret = x.toLowerCase();
			} else {
				ret = ret.replace(":x:", x);
			}
		}
		alIsApplied.add(tree);
		return ret;
	}

	private ArrayList<ParseTreeNode> getBestParseTreeNode() {
		ArrayList<ParseTreeNode> alArrayList = new ArrayList<ParseTreeNode>();
		for (ParseTreeNode tree : parseTreeList) {
			if (!tree.toString().contains("l(") && alArrayList.size() < 2) {
				alArrayList.add(tree);
			}
		}
		return alArrayList;
	}

	private FFunction getFFunction(ParseTreeNode tree) {
		return (FFunction) (tree.getNodeContent().getLambda());
	}
}

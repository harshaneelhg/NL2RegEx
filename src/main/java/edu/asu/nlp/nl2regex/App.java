package edu.asu.nlp.nl2regex;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import nl2kr.ccgparsing.ParseTreeNode;

/**
 * This is a class to generate regular expressions from the trained output of
 * the NL2KR
 * 
 */
public class App {

	/*
	 * HashMap for preprocessing stuff convert numbers three->3 one->1
	 */
	static HashMap<String, String> hmConvertNumbers;

	public static void main(String[] args) throws IOException {
		/* initialize the map */
		createNumberMap();
		readUserInput();

		/*
		 * create an object of converter class and get the parse trees by
		 * listening to events
		 */
		Convert c = new Convert();
		List<ParseTreeNode> functionList = c.getFuctionList();
		System.out.println(functionList);
		/*
		 * now based on entire list of trees returned we parse the tress to
		 * generate reqular expression
		 */
		Parser p = new Parser(functionList);
		String regEx = p.getRegEx();
		System.out.println("RegEx: " + regEx);
	}

	private static void readUserInput() {
		Scanner sc = new Scanner(System.in);

		System.out.println("Enter a sentence to translate:");
		String input = sc.nextLine();
		if (!input.equals("")) {
			/* call preprocessor */
			String cleanedInput = preprocess(input);
			if (!cleanedInput.equals("")) {
				PrintWriter out;
				try {
					// Write to file
					out = new PrintWriter("./Data/test.txt");
					out.print(cleanedInput);
					out.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			} else {
				System.out.println("Sentence is empty!!");
			}
		}
	}

	private static String preprocess(String input) {

		// replace spaces inside the quotes with SP
		input = replaceSpaceWithSPInQuotes(input, "\"");
		input = replaceSpaceWithSPInQuotes(input, "'");

		// remove all single and double quotes
		input = input.replaceAll("'", "");
		input = input.replaceAll("\"", "");

		// remove all articles
		String[] articleList = { " the ", " a ", " an " };
		for (String article : articleList) {
			input = input.replaceAll(article, " ");
		}

		// remove all punctuation
		String[] puncList = { "\\.", "\\?", ",", "!" };
		for (String punc : puncList) {
			input = input.replaceAll(punc, " ");
		}

		// remove all which with that
		input = input.replaceAll(" which ", " that ");

		// remove all in with with
		input = input.replaceAll(" in ", " with ");

		input = input.replaceAll("at least", "atleast");
		// replace numbers
		for (int i = 0; i < 9; i++) {
			input = input.replaceAll("" + i, "ln " + i);
		}
		for (String sNumber : hmConvertNumbers.keySet()) {
			input = input.replaceAll(sNumber, hmConvertNumbers.get(sNumber));
		}
		return input;
	}

	private static String replaceSpaceWithSPInQuotes(String input,
			String pattern) {
		int startIndex = 0;
		/* this is special handling for phrases */
		while (input.indexOf(pattern, startIndex) != -1) {
			int firstIndex = input.indexOf(pattern, startIndex);
			int secondIndex = input.indexOf(pattern, firstIndex + 1);
			// replace space
			String substr = input.substring(firstIndex, secondIndex);
			substr = substr.replace(" ", "SP");

			String firstpart = input.substring(0, firstIndex);
			String lastPart = input.substring(secondIndex + 1);
			// recreate input with replaced spaces
			input = firstpart + substr + lastPart;
			startIndex = secondIndex + 1;
		}

		return input;
	}

	private static void createNumberMap() {
		hmConvertNumbers = new HashMap<String, String>();
		hmConvertNumbers.put("one", "ln 1");
		hmConvertNumbers.put("two", "ln 2");
		hmConvertNumbers.put("three", "ln 3");
		hmConvertNumbers.put("four", "ln 4");
		hmConvertNumbers.put("five", "ln 5");
		hmConvertNumbers.put("six", "ln 6");
		hmConvertNumbers.put("seven", "ln 7");
		hmConvertNumbers.put("eight", "ln 8");
		hmConvertNumbers.put("nine", "ln 9");
	}
}

package edu.asu.nlp.nl2regex;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Scanner;

import nl2kr.ccgparsing.ParseTreeNode;

public class App {
    public static void main( String[] args ) throws IOException{
    	System.out.println(System.getProperty("user.dir"));
    	
    	//getUserInput
    	readUserInput();
    	
        Convert c = new Convert();
        List<ParseTreeNode> functionList = c.getFuctionList();
        System.out.println(functionList);
        Parser p = new Parser(functionList);
        String regEx = p.getRegEx();
    }
    
    private static void readUserInput(){
    	Scanner sc = new Scanner(System.in);
    	
    	System.out.println("Enter a sentence to translate:");
    	String input = sc.nextLine();
    	if(!input.equals("")){
    		String cleanedInput = preprocess(input);
    		if(!cleanedInput.equals("")){
    			PrintWriter out;
				try {
					//Write to file
					out = new PrintWriter("./Data/test.txt");
	    			out.print(cleanedInput);
	    			out.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
    		}else{
    			System.out.println("Sentence is empty!!");
    		}
    	}
    }
    
    private static String preprocess(String input){
    	
    	//replace spaces inside the quotes with SP
    	input = replaceSpaceWithSPInQuotes(input, "\"");
    	input = replaceSpaceWithSPInQuotes(input, "'");
    	
    	//remove all single and double quotes
    	input = input.replaceAll("'", "");
    	input = input.replaceAll("\"", "");
    	
    	//remove all articles
    	String [] articleList = {" the ", " a ", " an "};
    	for(String article : articleList){
    		input = input.replaceAll(article, "");
    	}
    	
    	//remove all punctuation
    	String [] puncList = {"\\.", "\\?", "," ,"!"};
    	for(String punc : puncList){
    		input = input.replaceAll(punc, "");
    	}
    	
    	//remove all which with that
    	input = input.replaceAll(" which ", " that ");
    	
    	//remove all in with with
    	input = input.replaceAll(" in ", " with ");
    	
    	return input;
    }
    
    private static String replaceSpaceWithSPInQuotes(String input, String pattern){
    	int startIndex = 0;
    	
    	while(input.indexOf(pattern, startIndex) != -1 ){
    		int firstIndex = input.indexOf(pattern, startIndex);
    		int secondIndex = input.indexOf(pattern, firstIndex+1);
    		//replace space
    		String substr = input.substring(firstIndex, secondIndex);
    		substr = substr.replace(" ", "SP");
    		
    		String firstpart = input.substring(0, firstIndex);
    		String lastPart = input.substring(secondIndex+1);
    		//recreate input with replaced spaces
    		input = firstpart + substr + lastPart;
    		startIndex = secondIndex+1;
    	}
    	
    	return input;
    }  
}

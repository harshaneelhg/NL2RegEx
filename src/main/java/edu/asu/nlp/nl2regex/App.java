package edu.asu.nlp.nl2regex;

import java.io.IOException;
import java.util.List;

import nl2kr.ccgparsing.ParseTreeNode;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws IOException
    {
    	System.out.println(System.getProperty("user.dir"));
        Convert c = new Convert();
        List<ParseTreeNode> functionList = c.getFuctionList();
        System.out.println(functionList);
    }
}

package edu.asu.nlp.nl2regex;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.google.common.eventbus.Subscribe;

import nl2kr.events.Bus;
import nl2kr.events.TranslationEvent;
import nl2kr.lambda.newImpl.FFunction;
import nl2kr.scripts.NL2KR_TTest;


class Convert {
	private List m_functionList;
	public void convert() throws IOException {
		ArrayList<String> alInputSentences = new ArrayList<String>();
		alInputSentences.add("Data\\test.txt");
		ArrayList<String> alSyntax = new ArrayList<String>();
		alSyntax.add("Data\\syntax.txt");
		ArrayList<String> aloutput = new ArrayList<String>();
		aloutput.add("Data\\output.txt");
		Bus.INSTANCE.register(this);
		NL2KR_TTest.runTranslation(alInputSentences, aloutput, alSyntax);
	}

	public static ArrayList<String> getFileContents(InputStreamReader IOreader) throws IOException {
		BufferedReader reader = null;
		ArrayList<String> alFileContents = null;
		try {
			reader = new BufferedReader(IOreader);
			String sContent = null;
			alFileContents = new ArrayList<String>();
			while ((sContent = reader.readLine()) != null) {
				alFileContents.add(sContent);
			}
		} finally {
			if (reader != null)
				reader.close();
		}
		return alFileContents;
	}

	@Subscribe
	public void handleTranslationEvent(TranslationEvent event) {
		System.out.println("Event");
		FFunction f = (FFunction) (event.getResults().get(0).getNodeContent().getLambda());
		System.out.println(f.getFname());
		m_functionList = event.getResults();
	}
	public List getFuctionList() throws IOException{
		convert();
		return m_functionList;
	}
	
}

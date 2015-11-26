package edu.asu.nlp.nl2regex;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.google.common.eventbus.Subscribe;

import nl2kr.events.Bus;
import nl2kr.events.TranslationEvent;
import nl2kr.lambda.newImpl.FFunction;
import nl2kr.scripts.NL2KR_TTest;
class Convert {
	public void convert() throws IOException {
		ArrayList<String> alInputSentences = new ArrayList<String>();
		alInputSentences
				.add("D:\\sem3\\CSE571\\examples\\7sentences\\test1.txt");
		ArrayList<String> alSyntax = new ArrayList<String>();
		alSyntax.add("D:\\syntax1.txt");
		ArrayList<String> aloutput = new ArrayList<String>();
		aloutput.add("D:\\output.txt");
		Bus.INSTANCE.register(this);
		System.out.println(System.getProperty("user.dir"));
		NL2KR_TTest.runTranslation(alInputSentences, aloutput, alSyntax);
		System.out.println();

	}

	public static ArrayList<String> getFileContents(InputStreamReader IOreader)
			throws IOException {
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
		FFunction f =(FFunction)(event.getResults().get(0).getNodeContent().getLambda());
		System.out.println(f.getFname());
		
	}
}

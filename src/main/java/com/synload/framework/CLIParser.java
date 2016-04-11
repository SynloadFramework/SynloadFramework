package com.synload.framework;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class CLIParser {
	private CommandLineParser parser;
	private Options options;
	private CommandLine cmd;
	public CLIParser(String[] args){
		parser = new DefaultParser();
    	options = new Options();
    	options.addOption("c", true, "configuration file");
    	options.addOption("p", true, "path to program root");
    	try {
			cmd = parser.parse( options, args);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	public void addOption(String tag, boolean hasArg, String description){
		options.addOption("t", hasArg, description);
	}
	public CommandLineParser getParser() {
		return parser;
	}
	public Options getOptions() {
		return options;
	}
	public CommandLine getCmd() {
		return cmd;
	}
}

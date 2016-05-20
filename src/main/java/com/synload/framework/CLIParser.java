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
    	options.addOption("config", true, "Configuration file");
    	options.addOption("sitepath", true, "Path to program root");
		options.addOption("port", true, "Port to run the site on");
		options.addOption("cb", true, "Connect back api with stats");
		options.addOption("scb", false, "Send stats back through the connectback bridge");
		options.addOption("id", true, "identifier for this server");
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

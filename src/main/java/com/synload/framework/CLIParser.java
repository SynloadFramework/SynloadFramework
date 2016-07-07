package com.synload.framework;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

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
		int x=0;
		while(x<20) {
			try {
				cmd = parser.parse( options, args);
				break;
			} catch (Exception e) {
				e.printStackTrace();
				try {
					Pattern regex = Pattern.compile("-([a-zA-Z0-9:+-]+)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
					Matcher regexMatcher = regex.matcher(e.getMessage());
					if (regexMatcher.find()) {
						String argsString = StringUtils.join(args, " ");
						argsString.replaceAll("(?i)" + regexMatcher.group(0), "");
						System.out.println(regexMatcher.group(0));
						args = argsString.split(" ");
					}
				} catch (PatternSyntaxException ex) {
					// Syntax error in the regular expression
				}
				x++;
			}
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

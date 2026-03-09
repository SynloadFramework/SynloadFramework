package com.synload.framework;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

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
		args = filterKnownArgs(args);
		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			System.err.println("Error parsing command line arguments: " + e.getMessage());
		}
	}

	/**
	 * Pre-filters arguments to only include those recognized by the configured options.
	 * Unrecognized options and their values are silently discarded.
	 */
	private String[] filterKnownArgs(String[] args) {
		Set<String> knownOptions = new HashSet<>();
		for (Object key : options.getOptions()) {
			org.apache.commons.cli.Option opt = (org.apache.commons.cli.Option) key;
			knownOptions.add(opt.getOpt());
			if (opt.getLongOpt() != null) {
				knownOptions.add(opt.getLongOpt());
			}
		}
		List<String> filtered = new ArrayList<>();
		for (int i = 0; i < args.length; i++) {
			String arg = args[i];
			if (arg.startsWith("-")) {
				String name = arg.replaceFirst("^-+", "");
				if (knownOptions.contains(name)) {
					filtered.add(arg);
					// Include the next argument as the option's value if it takes one
					if (i + 1 < args.length && !args[i + 1].startsWith("-")) {
						filtered.add(args[++i]);
					}
				}
			}
		}
		return filtered.toArray(new String[0]);
	}
	public void addOption(String tag, boolean hasArg, String description){
		options.addOption(tag, hasArg, description);
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

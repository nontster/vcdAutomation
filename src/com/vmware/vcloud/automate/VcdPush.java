package com.vmware.vcloud.automate;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class VcdPush {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// create Options object
		Options options = new Options();
		
		Option help = new Option( "help", "print this message" );		
		Option debug = new Option( "debug", "print debugging information" );
		Option config = new Option( "config", true, "configuration file" );
		
		options.addOption(help);
		options.addOption(debug);
		options.addOption(config);
		
		CommandLineParser parser = new DefaultParser();
		
		try {
			CommandLine cmd = parser.parse( options, args);
			
			if(cmd.hasOption("help")) {	    
				// automatically generate the help statement
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp( "vcdpush", options );
			} else {
			    
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			System.err.println( "Parsing failed.  Reason: " + e.getMessage() );
		}
	}

}

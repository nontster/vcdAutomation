package com.vmware.vcloud.automate;

import java.io.Console;
import java.io.FileNotFoundException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.vmware.vcloud.api.rest.schema.ReferenceType;
import com.vmware.vcloud.model.VCloudOrganization;
import com.vmware.vcloud.sdk.Task;
import com.vmware.vcloud.sdk.VCloudException;
import com.vmware.vcloud.sdk.Vapp;
import com.vmware.vcloud.sdk.VcloudClient;
import com.vmware.vcloud.sdk.Vdc;
import com.vmware.vcloud.sdk.admin.AdminOrganization;
import com.vmware.vcloud.sdk.admin.AdminVdc;
import com.vmware.vcloud.sdk.admin.EdgeGateway;
import com.vmware.vcloud.sdk.admin.VcloudAdmin;
import com.vmware.vcloud.sdk.constants.Version;

public class VcdPush {
	private static VcloudClient client;
	private static VcloudAdmin admin;

	private static VCloudOrganization vCloudOrg;
	private static AdminVdc adminVdc;
	private static EdgeGateway edgeGateway;

	private static String vcdurl;
	private static String username;
	private static String password;
	private static String template;	


	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// create Options object
		Options options = new Options();
		HelpFormatter formatter = new HelpFormatter();
		CommandLineParser parser = new DefaultParser();
		Console cnsl = null;

		Option optHelp = new Option("help", "print this message");
		Option optDebug = new Option("debug", "print debugging information");

		Option optBlueprint = Option.builder("t").longOpt("template").desc("template file").hasArg(true)
				.required(true).argName("file").build();

		Option optVcdurl = Option.builder("l").longOpt("vcdurl").desc("vCloud Director public URL").hasArg(true)
				.required(true).argName("url").build();

		Option optUsername = Option.builder("u").longOpt("user").desc("username").hasArg(true).required(true)
				.argName("username").build();

		Option optPassword = Option.builder("p").longOpt("password").desc("password").hasArg(true).required(false)
				.argName("password").build();

		options.addOption(optBlueprint);
		options.addOption(optVcdurl);
		options.addOption(optUsername);
		options.addOption(optPassword);
		options.addOption(optHelp);
		options.addOption(optDebug);

		if (args.length < 5) {
			formatter.printHelp("vcdpush", options);
			System.exit(1);
		}

		try {
			CommandLine cmd = parser.parse(options, args);

			if (cmd.hasOption("help"))
				/* automatically generate the help statement */
				formatter.printHelp("vcdpush", options); 
			else {

				if (cmd.hasOption("vcdurl")) {
					vcdurl = cmd.getOptionValue("vcdurl");
				}

				if (cmd.hasOption("user")) {
					username = cmd.getOptionValue("user");
					
					int index = username.indexOf('@');
					
					if(index < 0)
						username += "@System"; 
				}

				if (cmd.hasOption("password")) {
					password = cmd.getOptionValue("password");
				}

				if (cmd.hasOption("template")) {
					template = cmd.getOptionValue("template");
				}

				if (password == null) {					
					// creates a console object
			         cnsl = System.console();

			         // if console is not null
			         if (cnsl != null) {
			        	// read password into the char array
			             char[] pwd = cnsl.readPassword("Enter your password: ");
			             password = new String(pwd);
			         }				
				}

				ConfigParser cParser = ConfigParser.getParser(template);
				vCloudOrg = cParser.getOrg();

				VcloudClient.setLogLevel(Level.OFF);
				System.out.println("Vcloud Login");
				client = new VcloudClient(vcdurl, Version.V5_5);

				client.login(username, password);
				System.out.println("	Login Success\n");

				System.out.println("Get Vcloud Admin");
				admin = client.getVcloudAdmin();
				System.out.println("	" + admin.getResource().getHref() + "\n");
				
				System.out.print("Add New Organization : ");
				AdminOrganization adminOrg = admin.createAdminOrg(OrgUtils.createNewAdminOrgType(vCloudOrg));
				Task task = OrgUtils.returnTask(client, adminOrg);
				if (task != null)
					task.waitForTask(0);
				System.out.print(adminOrg.getResource().getName() + " : ");
				System.out.println("	" + adminOrg.getResource().getHref() + "\n");

				// Create vDC You may end using one of the following.
				adminVdc = VdcUtils.addPayAsYouGoVdc(vCloudOrg, admin, client, adminOrg);

				// Create user on the organization
				UserUtils.addUserToOrg(vCloudOrg, admin, adminOrg);

				// Create org vdc networks on the organizaiton
				NetworkUtils.addNatRoutedOrgVdcNetwork(client, vCloudOrg, edgeGateway, adminVdc, adminOrg);
			
				// find the vdc ref
				Vdc vdc = VdcUtils.findVdc(client, vCloudOrg.getName(), vCloudOrg.getVdc().getName());

				// find the vapp template ref
				ReferenceType vappTemplateRef = VappUtils.findVappTemplateRef(client, vCloudOrg.getCloudResources().getCatalog().getName(), vCloudOrg.getvApp().getChildVms().get(0).getTemplateType()); 

				// Composed vApp. 
				System.out.println("vApp: " + vCloudOrg.getvApp().getName());
				Vapp vapp = vdc.composeVapp(VappUtils.createComposeParams(client, vCloudOrg, vappTemplateRef, vdc));
				System.out.println("	Composing vApp : " + vapp.getResource().getName());
				List<Task> tasks = vapp.getTasks();
				if (tasks.size() > 0)
					tasks.get(0).waitForTask(0);
				
				// refresh the vapp
				vapp = Vapp.getVappByReference(client, vapp.getReference());
				
				// reconfigure Vms
				VappUtils.reconfigureVms(vapp, vCloudOrg);
										
				System.out.println("---------- Completed! ----------");
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			System.err.println("Parsing failed.  Reason: " + e.getMessage());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.err.println("BOT template not found: " + e.getMessage());
		} catch (VCloudException e) {
			// TODO Auto-generated catch block
			if(e.getMessage().equalsIgnoreCase("Unauthorized"))
				System.err.println("Invalid username or password");
			else
				System.err.println("vCloud exception: \n" + e.getMessage());
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			System.err.println("Time out on connecting to: "+ vcdurl +"\n"+ e.getMessage());
		} 

	}

}

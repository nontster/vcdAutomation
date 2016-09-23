package com.vmware.vcloud.automate;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;
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
	private static String blueprint;	

	public static void main(String[] args) throws VCloudException, TimeoutException, FileNotFoundException {
		// TODO Auto-generated method stub
		// create Options object
		Options options = new Options();
		HelpFormatter formatter = new HelpFormatter();
		CommandLineParser parser = new DefaultParser();

		Option optHelp = new Option("help", "print this message");
		Option optDebug = new Option("debug", "print debugging information");

		Option optBlueprint = Option.builder("b").longOpt("blueprint").desc("blueprint file").hasArg(true)
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
				}

				if (cmd.hasOption("password")) {
					password = cmd.getOptionValue("password");
				}

				if (cmd.hasOption("blueprint")) {
					blueprint = cmd.getOptionValue("blueprint");
				}

				if (password == null) {
					System.out.println("Enter your password: ");
					Scanner scanner = new Scanner(System.in);
					password = scanner.nextLine().trim();
					scanner.close();
				}

				ConfigParser cParser = ConfigParser.getParser(blueprint);
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
				Vdc vdc = VdcUtils.findVdc(client, vCloudOrg.getName(), vCloudOrg.getVdc().getVdcParams().getName());

				// find the vapp template ref
				ReferenceType vappTemplateRef = VappUtils.findVappTemplateRef(client, vCloudOrg.getCloudResources().getCatalog().getName(), vCloudOrg.getvApp().getChildVms().get(0).getTemplateType()); 

				// Composed vApp. 				
				Vapp vapp = vdc.composeVapp(VappUtils.createComposeParams(client, vCloudOrg, vappTemplateRef, vdc));
				System.out.println("Composing vApp : " + vapp.getResource().getName());
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
		}

	}

}

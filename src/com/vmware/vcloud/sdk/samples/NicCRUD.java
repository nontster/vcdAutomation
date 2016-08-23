/*
 * *******************************************************
 * Copyright VMware, Inc. 2010-2013.  All Rights Reserved.
 * *******************************************************
 *
 * DISCLAIMER. THIS PROGRAM IS PROVIDED TO YOU "AS IS" WITHOUT
 * WARRANTIES OR CONDITIONS # OF ANY KIND, WHETHER ORAL OR WRITTEN,
 * EXPRESS OR IMPLIED. THE AUTHOR SPECIFICALLY # DISCLAIMS ANY IMPLIED
 * WARRANTIES OR CONDITIONS OF MERCHANTABILITY, SATISFACTORY # QUALITY,
 * NON-INFRINGEMENT AND FITNESS FOR A PARTICULAR PURPOSE.
 */
package com.vmware.vcloud.sdk.samples;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;

import com.vmware.vcloud.api.rest.schema.ReferenceType;
import com.vmware.vcloud.sdk.Organization;
import com.vmware.vcloud.sdk.VCloudException;
import com.vmware.vcloud.sdk.VM;
import com.vmware.vcloud.sdk.Vapp;
import com.vmware.vcloud.sdk.VcloudClient;
import com.vmware.vcloud.sdk.Vdc;
import com.vmware.vcloud.sdk.VirtualNetworkCard;
import com.vmware.vcloud.sdk.constants.IpAddressAllocationModeType;
import com.vmware.vcloud.sdk.constants.Version;
import com.vmware.vcloud.sdk.constants.NetworkAdapterType;

/**
 * This sample demonstrates nic addition and deletion.
 * 
 * @author Ecosystem Engineering
 * 
 */

public class NicCRUD {

	public static VcloudClient client;

	/**
	 * Finding the vm using the org name and the vapp name
	 * 
	 * @param orgName
	 * @param vAppName
	 * @param vmName
	 * @return
	 * @throws VCloudException
	 */
	private static VM findVM(String orgName, String vAppName, String vmName)
			throws VCloudException {
		Organization org = Organization.getOrganizationByReference(client,
				client.getOrgRefsByName().get(orgName));

		for (ReferenceType vdcRef : org.getVdcRefs()) {
			Vdc vdc = Vdc.getVdcByReference(client, vdcRef);
			if (vdc.getVappRefsByName().containsKey(vAppName)) {
				Vapp vapp = Vapp.getVappByReference(client, vdc
						.getVappRefByName(vAppName));
				for (VM vm : vapp.getChildrenVms()) {
					if (vm.getResource().getName().equals(vmName)) {
						System.out.println("VM Found: " + vmName + " - "
								+ vm.getReference().getHref());
						return vm;
					}
				}
			}
		}
		System.out.println("VM " + vmName + " not found");
		System.exit(0);
		return null;
	}

	/**
	 * Sample usage
	 */
	private static void usage() {
		System.out
				.println("java NicCRUD VcloudUrl Username@vcloud-oragnization Password OrganizationName vAppName vmName networkName CertificateKeyStorePath[optional] CertificateKeyStorePassword[optional]");
		System.out
				.println("java NicCRUD https://vcloud username@Organization password orgName vappName vmName networkName");
		System.out
				.println("java NicCRUD https://vcloud username@Organization password orgName vappName vmName networkName certificatekeystorepath certificatekeystorepassword");
		System.exit(0);
	}

	/**
	 * Starts here
	 * 
	 * @param args
	 * @throws VCloudException
	 * @throws KeyManagementException
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 * @throws KeyStoreException
	 * @throws UnrecoverableKeyException
	 * @throws TimeoutException
	 * @throws CertificateException
	 */
	public static void main(String args[]) throws VCloudException,
			KeyManagementException, NoSuchAlgorithmException, IOException,
			UnrecoverableKeyException, KeyStoreException, TimeoutException,
			CertificateException {

		if(args.length < 7)
			usage();

		VcloudClient.setLogLevel(Level.OFF);
		client = new VcloudClient(args[0], Version.V5_5);

		// Performing Certificate Validation
		if (args.length == 9) {
			System.setProperty("javax.net.ssl.trustStore", args[7]);
			System.setProperty("javax.net.ssl.trustStorePassword", args[8]);

			client.registerScheme("https", 443, CustomSSLSocketFactory
					.getInstance());
		} else if (args.length == 7) {
			System.err
					.println("Ignoring the Certificate Validation using FakeSSLSocketFactory.java - DO NOT DO THIS IN PRODUCTION");
			client.registerScheme("https", 443, FakeSSLSocketFactory
					.getInstance());
		} else {
			usage();
		}

		client.login(args[1], args[2]);

		VM vm = findVM(args[3], args[4], args[5]);

		// getting the already existing nics.
		List<VirtualNetworkCard> nics = vm.getNetworkCards();
		int nicSize = vm.getNetworkCards().size();

		// adding 4 more nics
		boolean onlyOnePrimaryNetwork = false;
		if(nicSize == 0)
			onlyOnePrimaryNetwork = true;
		for (int i = 0; i < 2; i++) {
			//For creating a Virtual Network card of default E1000 adapter type. 
			VirtualNetworkCard nic = new VirtualNetworkCard(nicSize + i, true,
					args[6], onlyOnePrimaryNetwork,
					IpAddressAllocationModeType.POOL, "");
			nics.add(nic);
			onlyOnePrimaryNetwork = false;

			//For creating a Virtual Network card other than default E1000 adapter type.
			VirtualNetworkCard nicVlance = new VirtualNetworkCard(nicSize + i + 2, true,
					args[6], onlyOnePrimaryNetwork,
					IpAddressAllocationModeType.POOL, "", NetworkAdapterType.VLANCE);
			nics.add(nicVlance);
		}
		try {
			vm.updateNetworkCards(nics).waitForTask(0);
		} catch (VCloudException e) {
			System.out.println("	Adding nics failed" + e.getLocalizedMessage());
			System.exit(0);
		}

		System.out.println("	New nics Added");

		nics = VM.getNetworkCards(client, vm.getReference());
		// deleting nic with nic-id = nicSize
		for (int i = 0; i < nics.size(); i++) {
			if (nics.get(i).getItemResource().getAddressOnParent().getValue().equalsIgnoreCase(nicSize+"")) {
				nics.remove(i);
				break;
			}
		}
		try {
			vm.updateNetworkCards(nics).waitForTask(0);
		} catch (VCloudException e) {
			System.out.println("	Deleting nic failed "
					+ e.getLocalizedMessage());
			System.exit(0);
		}
		System.out.println("	Deleted nic with nic-id - " + nicSize);
	}
}

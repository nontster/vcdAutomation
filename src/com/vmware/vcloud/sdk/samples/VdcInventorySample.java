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
import java.util.Collection;
import java.util.HashMap;
import java.util.logging.Level;

import org.apache.http.HttpException;

import com.vmware.vcloud.api.rest.schema.ReferenceType;
import com.vmware.vcloud.api.rest.schema.VdcType;
import com.vmware.vcloud.sdk.Organization;
import com.vmware.vcloud.sdk.VCloudException;
import com.vmware.vcloud.sdk.VcloudClient;
import com.vmware.vcloud.sdk.Vdc;
import com.vmware.vcloud.sdk.constants.Version;

/**
 * This sample lists the Vdc of the vCloud Inventory
 * 
 * @author Ecosystem Engineering
 * 
 */

public class VdcInventorySample {

	public VcloudClient vcloudClient;
	public HashMap<String, ReferenceType> organizationsMap;

	/**
	 * Login to the API with credentials.
	 * 
	 * @param vCloudURL
	 * @param username
	 * @param password
	 * @throws IOException
	 * @throws VCloudException
	 * @throws HttpException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 * @throws KeyStoreException
	 * @throws UnrecoverableKeyException
	 * @throws CertificateException
	 */
	public VdcInventorySample(String vCloudURL, String username,
			String password, Boolean certRequire, String certpath,
			String certpassword) throws HttpException, VCloudException,
			IOException, KeyManagementException, NoSuchAlgorithmException,
			UnrecoverableKeyException, KeyStoreException, CertificateException {
		VcloudClient.setLogLevel(Level.OFF);
		vcloudClient = new VcloudClient(vCloudURL, Version.V5_5);

		if (certRequire == true) {
			// Performing Certificate Validation
			System.setProperty("javax.net.ssl.trustStore", certpath);
			System
					.setProperty("javax.net.ssl.trustStorePassword",
							certpassword);

			vcloudClient.registerScheme("https", 443, CustomSSLSocketFactory
					.getInstance());
		} else {
			System.err
					.println("Ignoring the Certificate Validation using FakeSSLSocketFactory.java - DO NOT DO THIS IN PRODUCTION");
			vcloudClient.registerScheme("https", 443, FakeSSLSocketFactory
					.getInstance());
		}

		vcloudClient.login(username, password);
		organizationsMap = vcloudClient.getOrgRefsByName();
		if (organizationsMap.isEmpty()) {
			System.out.println("Try Logging in with valid details");
			System.exit(0);
		}
	}

	/**
	 * Lists the vCloud Inventory as
	 * 
	 * Organization Vdc vApp & vAppTemplate
	 * 
	 * @throws VCloudException
	 */
	public void listInventory() throws VCloudException {
		System.out
				.println("Organization					Vdc						vApp, vAppTemplate & Media");
		System.out
				.println("------------        				---						--------------------------");
		System.out.println();
		if (!organizationsMap.isEmpty()) {
			for (String organizationName : organizationsMap.keySet()) {
				ReferenceType organizationReference = organizationsMap
						.get(organizationName);
				System.out.print(organizationName);
				System.out.println();
				System.out.println(organizationReference.getHref());
				Organization organization = Organization
						.getOrganizationByReference(vcloudClient,
								organizationReference);
				Collection<ReferenceType> vdcLinks = organization.getVdcRefs();
				if (!vdcLinks.isEmpty()) {
					for (ReferenceType vdcLink : vdcLinks) {
						Vdc vdc = Vdc.getVdcByReference(vcloudClient, vdcLink);
						VdcType vdcParams = vdc.getResource();
						System.out.print("						" + vdcParams.getName());
						System.out.println();
						System.out.println("						" + vdcLink.getHref());

						HashMap<String, ReferenceType> vAppReferencesByName = vdc
								.getVappRefsByName();
						if (!vAppReferencesByName.isEmpty()) {
							for (String vAppName : vAppReferencesByName
									.keySet()) {
								System.out.print("												" + vAppName);
								System.out.println();
								System.out.println("												"
										+ vAppReferencesByName.get(vAppName)
												.getHref());
							}
						} else
							System.out.println("												No vApp's Found");

						Collection<ReferenceType> vAppTemplateReferences = vdc
								.getVappTemplateRefs();
						if (!vAppTemplateReferences.isEmpty()) {
							for (ReferenceType vAppTemplateRef : vAppTemplateReferences) {
								System.out.print("												"
										+ vAppTemplateRef.getName());
								System.out.println();
								System.out.println("												"
										+ vAppTemplateRef.getHref());
							}
						} else
							System.out
									.println("												No vAppTemplate's Found");

						Collection<ReferenceType> mediaReferences = vdc
								.getMediaRefs();
						if (!mediaReferences.isEmpty()) {
							for (ReferenceType mediaRef : mediaReferences) {
								System.out.print("												"
										+ mediaRef.getName());
								System.out.println();
								System.out.println("												"
										+ mediaRef.getHref());
							}
						} else
							System.out.println("												No Media's Found");

					}
					System.out.println();
				} else
					System.out.println("						No Vdc's Found");
			}
		} else {
			System.out.println("No Organizations");
			System.exit(0);
		}
	}

	/**
	 * VdcInventorySample Program Usage
	 */
	public static void getUsage() {
		System.out
				.println("java VdcInventorySample vCloudURL user@organization password CertificateKeyStorePath[optional] CertificateKeyStorePassword[optional]");
		System.out
				.println("java VdcInventorySample https://vcloud user@organization password");
		System.out
				.println("java VdcInventorySample https://vcloud user@organization password certificatekeystorepath certificatekeystorepassword");
		System.exit(0);
	}

	/**
	 * Starting method for the VdcInventorySample
	 * 
	 * @param args
	 * @throws IOException
	 * @throws HttpException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 * @throws KeyStoreException
	 * @throws UnrecoverableKeyException
	 * @throws CertificateException
	 */

	public static void main(String args[]) throws HttpException, IOException,
			KeyManagementException, NoSuchAlgorithmException,
			UnrecoverableKeyException, KeyStoreException, CertificateException {

		try {
			if (args.length == 5) {
				VdcInventorySample vdcInventorySample = new VdcInventorySample(
						args[0], args[1], args[2], true, args[3], args[4]);
				vdcInventorySample.listInventory();
			} else if (args.length == 3) {
				VdcInventorySample vdcInventorySample = new VdcInventorySample(
						args[0], args[1], args[2], false, null, null);
				vdcInventorySample.listInventory();
			} else {
				getUsage();
			}
		} catch (VCloudException e) {
			System.out.println(e.getMessage());
		}

	}

}

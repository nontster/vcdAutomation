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

import com.vmware.vcloud.api.rest.schema.CatalogType;
import com.vmware.vcloud.api.rest.schema.ReferenceType;
import com.vmware.vcloud.sdk.Catalog;
import com.vmware.vcloud.sdk.Organization;
import com.vmware.vcloud.sdk.VCloudException;
import com.vmware.vcloud.sdk.VcloudClient;
import com.vmware.vcloud.sdk.constants.Version;

/**
 * This sample lists the Catalog of the vCloud Inventory
 * 
 * @author Ecosystem Engineering
 * 
 */

public class CatalogInventorySample {

	public VcloudClient vcloudClient;
	public HashMap<String, ReferenceType> organizationsMap;

	/**
	 * Login to the API with credentials.
	 * 
	 * @param vCloudURL
	 * @param username
	 * @param password
	 * @param certRequire
	 * @param certpath
	 * @param certpassword
	 * @throws IOException
	 * @throws VCloudException
	 * @throws HttpException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 * @throws KeyStoreException
	 * @throws UnrecoverableKeyException
	 * @throws CertificateException
	 */
	public CatalogInventorySample(String vCloudURL, String username,
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
	 * Organization Catalog CatalogItem
	 * 
	 * @throws VCloudException
	 */
	public void listInventory() throws VCloudException {
		System.out.println("Organization					Catalog						CatalogItem");
		System.out.println("------------        				-------						-----------");
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
				Collection<ReferenceType> catalogLinks = organization
						.getCatalogRefs();
				if (!catalogLinks.isEmpty()) {
					for (ReferenceType catalogLink : catalogLinks) {
						Catalog catalog = Catalog.getCatalogByReference(
								vcloudClient, catalogLink);
						CatalogType catalogParams = catalog.getResource();
						System.out.print("						" + catalogParams.getName());
						System.out.println();
						System.out.println("						" + catalogLink.getHref());
						Collection<ReferenceType> catalogItemReferences = catalog
								.getCatalogItemReferences();
						if (!catalogItemReferences.isEmpty()) {
							for (ReferenceType catalogItemReference : catalogItemReferences) {
								System.out.print("												"
										+ catalogItemReference.getName());
								System.out.println();
								System.out.println("												"
										+ catalogItemReference.getHref());
							}
							System.out.println();
						} else
							System.out
									.println("												No CatalogItems Found");
					}
					System.out.println();
				} else
					System.out.println("						No Catalogs Found");
			}
		} else {
			System.out.println("No Organizations");
			System.exit(0);
		}
	}

	/**
	 * CatalogInventorySample Program Usage
	 */
	public static void getUsage() {
		System.out
				.println("java CatalogInventorySample vCloudURL user@vcloud-organization password CertificateKeyStorePath[optional] CertificateKeyStorePassword[optional]");
		System.out
				.println("java CatalogInventorySample https://vcloud user@vcloud-organization password");
		System.out
				.println("java CatalogInventorySample https://vcloud user@vcloud-organization password certificatekeystorepath certificatekeystorepassword");
		System.exit(0);
	}

	/**
	 * Starting method for the CatalogInventorySample
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
				CatalogInventorySample catalogInventorySample = new CatalogInventorySample(
						args[0], args[1], args[2], true, args[3], args[4]);
				catalogInventorySample.listInventory();
			} else if (args.length == 3) {
				CatalogInventorySample catalogInventorySample = new CatalogInventorySample(
						args[0], args[1], args[2], false, null, null);
				catalogInventorySample.listInventory();
			} else {
				getUsage();
			}
		} catch (VCloudException e) {
			System.out.println(e.getMessage());
		}
	}
}

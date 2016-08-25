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
import java.util.logging.Level;

import org.apache.http.HttpException;

import com.vmware.vcloud.api.rest.schema.ReferenceType;
import com.vmware.vcloud.sdk.Organization;
import com.vmware.vcloud.sdk.VCloudException;
import com.vmware.vcloud.sdk.VM;
import com.vmware.vcloud.sdk.Vapp;
import com.vmware.vcloud.sdk.VappTemplate;
import com.vmware.vcloud.sdk.VcloudClient;
import com.vmware.vcloud.sdk.Vdc;
import com.vmware.vcloud.sdk.admin.extensions.VcloudAdminExtension;
import com.vmware.vcloud.sdk.constants.Version;

/**
 * This sample lists the tenant and the provider resources.
 *
 * @author Ecosystem Engineering
 */

public class InventorySample {

	private static VcloudClient vcloudClient;

	/**
	 * Sample Usage
	 */
	public static void usage() {
		System.out
				.println("java InventorySample vCloudURL user@organization password CertificateKeyStorePath[optional] CertificateKeyStorePassword[optional]");
		System.out
				.println("java InventorySample https://vcloud user@organization password");
		System.out
				.println("java InventorySample https://vcloud user@organization password certificatekeystorepath certificatekeystorepassword");
		System.exit(0);
	}

	/**
	 * This method performs the initial certificate validation
	 *
	 * @param commandLineArgs
	 * @throws KeyManagementException
	 * @throws UnrecoverableKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyStoreException
	 * @throws VCloudException
	 * @throws CertificateException
	 * @throws IOException
	 */
	private static void certificateValidation(String[] commandLineArgs)
			throws KeyManagementException, UnrecoverableKeyException,
			NoSuchAlgorithmException, KeyStoreException, CertificateException,
			IOException {

		if (commandLineArgs.length < 3)
			usage();

		// Client login
		VcloudClient.setLogLevel(Level.OFF);
		vcloudClient = new VcloudClient(commandLineArgs[0], Version.V5_5);

		// Performing Certificate Validation
		if (commandLineArgs.length == 5) {
			System.setProperty("javax.net.ssl.trustStore", commandLineArgs[3]);
			System.setProperty("javax.net.ssl.trustStorePassword",
					commandLineArgs[4]);

			vcloudClient.registerScheme("https", 443, CustomSSLSocketFactory
					.getInstance());
		} else if (commandLineArgs.length == 3) {
			System.err
					.println("Ignoring the Certificate Validation using FakeSSLSocketFactory.java - DO NOT DO THIS IN PRODUCTION");
			vcloudClient.registerScheme("https", 443, FakeSSLSocketFactory
					.getInstance());
		} else {
			usage();
		}
	}

	/**
	 * Lists all the tenant resources. This method can be run by a sys admin/org
	 * admin/org user. The tenant resources are Org, vdc, vdc network, media,
	 * vapp, vm, vapptemplate, catalog etc.
	 *
	 * This method can be expensive in traversing the whole inventory. Instead
	 * you can also use the query service to list all the objects. Refer to
	 * {@link QueryAllvApps} or {@link QueryAllVms}
	 *
	 * @throws VCloudException
	 */
	private static void tenantResourcesInventory() throws VCloudException {
		System.out.println("-------------------------------");
		System.out.println("Tenant/Org Resources Inventory");
		System.out.println("-------------------------------");
		for (ReferenceType orgRef : vcloudClient.getOrgRefs()) {
			System.out.println("(Org) " + orgRef.getName());
			Organization org = Organization.getOrganizationByReference(
					vcloudClient, orgRef);
			for (ReferenceType catalogRef : org.getCatalogRefs()) {
				System.out.println("  (Catalog) " + catalogRef.getName());
			}
			for (ReferenceType vdcRef : org.getVdcRefs()) {
				System.out.println("  (Vdc) " + vdcRef.getName());
				try {
					Vdc vdc = Vdc.getVdcByReference(vcloudClient, vdcRef);
					for (ReferenceType mediaRef : vdc.getMediaRefs()) {
						System.out.println("    (Media) " + mediaRef.getName());
					}

					for (ReferenceType vappRef : vdc.getVappRefs()) {
						System.out.println("    (VApp) " + vappRef.getName());
						try {
							Vapp vapp = Vapp.getVappByReference(vcloudClient,
									vappRef);
							for (VM vm : vapp.getChildrenVms()) {
								System.out.println("      (VM) "
										+ vm.getResource().getName());
							}
						} catch (VCloudException e) {
							System.out.println(e.getMessage());
						}
					}
					for (ReferenceType vappTempRef : vdc.getVappTemplateRefs()) {
						System.out.println("    (VAppTemplate) "
								+ vappTempRef.getName());
						try {
							VappTemplate vappTemp = VappTemplate
									.getVappTemplateByReference(vcloudClient,
											vappTempRef);
							for (VappTemplate vm : vappTemp.getChildren()) {
								System.out.println("      (VM) "
										+ vm.getResource().getName());
							}
						} catch (VCloudException e) {
							System.out.println(e.getMessage());
						}
					}
					for (ReferenceType networkRef : vdc
							.getAvailableNetworkRefs()) {
						System.out.println("    (VdcNetwork) "
								+ networkRef.getName());
					}
				} catch (VCloudException e) {
					System.out.println(e.getMessage());
				}
			}
			System.out.println();
		}
	}

	/**
	 * Lists all the provider resources. This method requires sys admin
	 * privileges. The provider resources can be provider vdc, external network,
	 * network pool, host, datastore etc.
	 *
	 * @throws VCloudException
	 */
	private static void providerResourcesInventory() throws VCloudException {
		System.out.println("----------------------------");
		System.out.println("Provider Resources Inventory");
		System.out.println("----------------------------");
		VcloudAdminExtension adminExtenion = vcloudClient
				.getVcloudAdminExtension();
		for (String vmwProvVdcName : adminExtenion
				.getVMWProviderVdcRefsByName().keySet()) {
			System.out.println("  (ProviderVdc) " + vmwProvVdcName);
		}
		System.out.println();
		for (String vmwExternalNetworkName : adminExtenion
				.getVMWExternalNetworkRefsByName().keySet()) {
			System.out.println("  (ExternalNetwork) " + vmwExternalNetworkName);
		}
		System.out.println();
		for (String vmwNetworkPoolName : adminExtenion
				.getVMWNetworkPoolRefsByName().keySet()) {
			System.out.println("  (NetworkPool) " + vmwNetworkPoolName);
		}
		System.out.println();
		for (String vmwHostName : adminExtenion.getVMWHostRefsByName().keySet()) {
			System.out.println("  (Host) " + vmwHostName);
		}
		System.out.println();
		for (ReferenceType vmwDatstoreRef : adminExtenion.getVMWDatastoreRefs()) {
			System.out.println("  (Datastore) " + vmwDatstoreRef.getName());
		}
	}

	/**
	 * InventorySample main.
	 *
	 * @param args
	 * @throws HttpException
	 * @throws IOException
	 * @throws KeyManagementException
	 * @throws NoSuchAlgorithmException
	 * @throws UnrecoverableKeyException
	 * @throws KeyStoreException
	 * @throws CertificateException
	 * @throws VCloudException
	 */
	public static void main(String args[]) throws HttpException, IOException,
			KeyManagementException, NoSuchAlgorithmException,
			UnrecoverableKeyException, KeyStoreException, CertificateException,
			VCloudException {

		certificateValidation(args);

		System.out.println("Vcloud Login");
		vcloudClient.login(args[1], args[2]);
		System.out.println("	Login Success\n");

		// org admin/users - lists the tenant resources
		tenantResourcesInventory();

		// system admin - lists the provider resources
		providerResourcesInventory();
	}
}

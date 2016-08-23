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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.apache.http.HttpException;

import com.vmware.vcloud.api.rest.schema.QueryResultAdminDiskRecordType;
import com.vmware.vcloud.api.rest.schema.QueryResultAdminVAppNetworkRecordType;
import com.vmware.vcloud.api.rest.schema.QueryResultAdminVMRecordType;
import com.vmware.vcloud.api.rest.schema.QueryResultAdminVdcRecordType;
import com.vmware.vcloud.api.rest.schema.QueryResultDatastoreProviderVdcRelationRecordType;
import com.vmware.vcloud.api.rest.schema.QueryResultDatastoreRecordType;
import com.vmware.vcloud.api.rest.schema.QueryResultHostRecordType;
import com.vmware.vcloud.api.rest.schema.QueryResultNetworkPoolRecordType;
import com.vmware.vcloud.api.rest.schema.QueryResultNetworkRecordType;
import com.vmware.vcloud.api.rest.schema.QueryResultOrgVdcNetworkRecordType;
import com.vmware.vcloud.api.rest.schema.QueryResultOrgVdcResourcePoolRelationRecordType;
import com.vmware.vcloud.api.rest.schema.QueryResultPortgroupRecordType;
import com.vmware.vcloud.api.rest.schema.QueryResultProviderVdcResourcePoolRelationRecordType;
import com.vmware.vcloud.api.rest.schema.QueryResultVMWProviderVdcRecordType;
import com.vmware.vcloud.api.rest.schema.ReferenceType;
import com.vmware.vcloud.api.rest.schema.extension.FencePoolType;
import com.vmware.vcloud.api.rest.schema.extension.PortGroupPoolType;
import com.vmware.vcloud.api.rest.schema.extension.VimObjectRefType;
import com.vmware.vcloud.api.rest.schema.extension.VlanPoolType;
import com.vmware.vcloud.sdk.Expression;
import com.vmware.vcloud.sdk.Filter;
import com.vmware.vcloud.sdk.QueryParams;
import com.vmware.vcloud.sdk.QueryService;
import com.vmware.vcloud.sdk.RecordResult;
import com.vmware.vcloud.sdk.VCloudException;
import com.vmware.vcloud.sdk.VM;
import com.vmware.vcloud.sdk.VappNetwork;
import com.vmware.vcloud.sdk.VappTemplate;
import com.vmware.vcloud.sdk.VcloudClient;
import com.vmware.vcloud.sdk.admin.AdminOrgVdcNetwork;
import com.vmware.vcloud.sdk.admin.extensions.VMWNetworkPool;
import com.vmware.vcloud.sdk.admin.extensions.VMWProviderVdc;
import com.vmware.vcloud.sdk.admin.extensions.VMWVimServer;
import com.vmware.vcloud.sdk.constants.Version;
import com.vmware.vcloud.sdk.constants.query.ExpressionType;
import com.vmware.vcloud.sdk.constants.query.FilterType;
import com.vmware.vcloud.sdk.constants.query.QueryAdminDiskField;
import com.vmware.vcloud.sdk.constants.query.QueryAdminVAppNetworkField;
import com.vmware.vcloud.sdk.constants.query.QueryAdminVMField;
import com.vmware.vcloud.sdk.constants.query.QueryAdminVdcField;
import com.vmware.vcloud.sdk.constants.query.QueryDatastoreField;
import com.vmware.vcloud.sdk.constants.query.QueryDatastoreProviderVdcRelationField;
import com.vmware.vcloud.sdk.constants.query.QueryHostField;
import com.vmware.vcloud.sdk.constants.query.QueryNetworkField;
import com.vmware.vcloud.sdk.constants.query.QueryNetworkPoolField;
import com.vmware.vcloud.sdk.constants.query.QueryOrgVdcNetworkField;
import com.vmware.vcloud.sdk.constants.query.QueryOrgVdcResourcePoolRelationField;
import com.vmware.vcloud.sdk.constants.query.QueryPortgroupField;
import com.vmware.vcloud.sdk.constants.query.QueryProviderVdcResourcePoolRelationField;
import com.vmware.vcloud.sdk.constants.query.QueryRecordType;
import com.vmware.vcloud.sdk.constants.query.QueryVMWProviderVdcField;

/**
 * This sample illustrates the correlation of the objects in vCloud to objects
 * in vCenter.
 *
 * Running this sample requires System Administrator privileges.
 *
 * @author Ecosystem Engineering
 */

@SuppressWarnings( { "unchecked" })
public class CorrelationSample {

	private static VcloudClient vcloudClient;

	private static QueryService queryService;

	/**
	 * This method performs the initial certificate validation and login.
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
	public static void performLogin(String[] commandLineArgs)
			throws KeyManagementException, UnrecoverableKeyException,
			NoSuchAlgorithmException, KeyStoreException, VCloudException,
			CertificateException, IOException {

		if (commandLineArgs.length < 3)
			usage();

		// Set the log levels
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

		if (!commandLineArgs[1].toLowerCase().contains("system")) {
			System.out.println("This sample requires System admin credentials");
			System.exit(0);
		}

		vcloudClient.registerScheme("https", 443, FakeSSLSocketFactory
				.getInstance());
		// login operation
		vcloudClient.login(commandLineArgs[1], commandLineArgs[2]);
		// retrieve the query service.
		queryService = vcloudClient.getQueryService();

	}

	/**
	 * Sample Usage
	 */
	public static void usage() {
		System.out
				.println("java CorrelationSample vCloudURL user@System password CertificateKeyStorePath[optional] CertificateKeyStorePassword[optional]");
		System.out
				.println("java CorrelationSample https://vcloud user@System password");
		System.out
				.println("java CorrelationSample https://vcloud user@System password certificatekeystorepath certificatekeystorepassword");
		System.exit(0);
	}

	/**
	 * Lists the Vdc's correlation data with vCenter. a. vCenter server
	 * information. b. Resource pools information.
	 *
	 * @throws VCloudException
	 *
	 */
	private static void getVdcCorrelation() throws VCloudException {
		System.out.println("---------------");
		System.out.println("Vdc Correlation");
		System.out.println("---------------");
		// admin org vdc query
		QueryParams<QueryAdminVdcField> vdcParams = new QueryParams<QueryAdminVdcField>();
		vdcParams.setPageSize(128);
		RecordResult<QueryResultAdminVdcRecordType> vdcResult = queryService
				.queryIdRecords(QueryRecordType.ADMINORGVDC, vdcParams);
		// iterate through the admin org vdc's
		for (QueryResultAdminVdcRecordType record : vdcResult.getRecords()) {
			System.out.println("   " + record.getName());
			// org vdc resource pool relation query
			Expression expression = new Expression(
					QueryOrgVdcResourcePoolRelationField.VDC, record.getId(),
					ExpressionType.EQUALS);
			QueryParams<QueryAdminVdcField> vdcResPoolRelationParams = new QueryParams<QueryAdminVdcField>();
			vdcResPoolRelationParams.setPageSize(128);
			vdcResPoolRelationParams.setFilter(new Filter(expression));
			RecordResult<QueryResultOrgVdcResourcePoolRelationRecordType> vdcResPoolRelationRecords = queryService
					.queryIdRecords(QueryRecordType.ORGVDCRESOURCEPOOLRELATION,
							vdcResPoolRelationParams);
			// iterate through the org vdc resource pool relation result.
			for (QueryResultOrgVdcResourcePoolRelationRecordType vdcResPoolRelationRecord : vdcResPoolRelationRecords
					.getRecords()) {
				// get the vcenter information.
				try {
					VMWVimServer vCenterServer = VMWVimServer
							.getVMWVimServerById(vcloudClient,
									vdcResPoolRelationRecord.getVc());
					System.out.print("	(vCenter) "
							+ vCenterServer.getResource().getName() + "(");
					System.out.println(vCenterServer.getResource().getUrl()
							+ ")");
				} catch (VCloudException e) {
					e.printStackTrace();
				}
				System.out.print("	(Resource Pool) ");
				System.out.println(vdcResPoolRelationRecord
						.getResourcePoolMoref());
			}
		}
	}

	/**
	 * Lists the Vdc Network's correlation data with vCenter and its network
	 * resources. a. vCenter server information. b. vCenter Network information.
	 *
	 * @throws VCloudException
	 */
	private static void getOrgVdcNetworkCorrelation() throws VCloudException {
		System.out.println("-----------------------");
		System.out.println("Vdc Network Correlation");
		System.out.println("-----------------------");
		// org vdc network query
		QueryParams<QueryOrgVdcNetworkField> vdcParams = new QueryParams<QueryOrgVdcNetworkField>();
		vdcParams.setPageSize(128);
		RecordResult<QueryResultOrgVdcNetworkRecordType> vdcResult = queryService
				.queryIdRecords(QueryRecordType.ORGVDCNETWORK, vdcParams);
		// iterate through the org vdc network's
		for (QueryResultOrgVdcNetworkRecordType record : vdcResult.getRecords()) {
			System.out.println("   " + record.getName());
			String parentNetworkName = record.getName();
			try {
				AdminOrgVdcNetwork adminOrgVdcNetwork = AdminOrgVdcNetwork
						.getOrgVdcNetworkById(vcloudClient, record.getId());
				// find the parent network if any.
				if (adminOrgVdcNetwork.getResource().getConfiguration()
						.getParentNetwork() != null) {
					parentNetworkName = adminOrgVdcNetwork.getResource()
							.getConfiguration().getParentNetwork().getName();
				}
			} catch (VCloudException e) {
				e.printStackTrace();
			}
			// port group query to get the vcenter network information.
			Expression expression = new Expression(
					QueryPortgroupField.NETWORKNAME, parentNetworkName,
					ExpressionType.EQUALS);
			QueryParams<QueryPortgroupField> portGroupParams = new QueryParams<QueryPortgroupField>();
			portGroupParams.setPageSize(128);
			portGroupParams.setFilter(new Filter(expression));
			RecordResult<QueryResultPortgroupRecordType> portGroupResult = queryService
					.queryIdRecords(QueryRecordType.PORTGROUP, portGroupParams);
			// iterate through the port group query result.
			for (QueryResultPortgroupRecordType portGroupRecord : portGroupResult
					.getRecords()) {
				// get the vcenter information.
				VMWVimServer vCenterServer;
				try {
					vCenterServer = VMWVimServer.getVMWVimServerById(
							vcloudClient, portGroupRecord.getVc());
					System.out.print("	(vCenter) "
							+ vCenterServer.getResource().getName() + "(");
					System.out.println(vCenterServer.getResource().getUrl()
							+ ")");
				} catch (VCloudException e) {
					e.printStackTrace();
				}
				System.out.print("	(vCenter Network) ");
				System.out.print(portGroupRecord.getName() + "(");
				System.out.println(portGroupRecord.getMoref() + ")");
			}
		}
	}

	/**
	 * Lists the VApp Network's correlation data with vCenter and its network
	 * resources. a. vCenter server information. b. vCenter Network information.
	 *
	 * @throws VCloudException
	 *
	 */
	private static void getVappNetworkCorrelation() throws VCloudException {
		System.out.println("------------------------");
		System.out.println("VApp Network Correlation");
		System.out.println("------------------------");
		// vapp network query
		QueryParams<QueryAdminVAppNetworkField> vappNetworkParams = new QueryParams<QueryAdminVAppNetworkField>();
		vappNetworkParams.setPageSize(128);
		RecordResult<QueryResultAdminVAppNetworkRecordType> vdcResult = queryService
				.queryIdRecords(QueryRecordType.ADMINVAPPNETWORK,
						vappNetworkParams);
		// iterate through the vapp network's
		for (QueryResultAdminVAppNetworkRecordType record : vdcResult
				.getRecords()) {
			System.out.println("   " + record.getName());
			String parentNetworkName = record.getName();
			try {
				VappNetwork vappNetwork = VappNetwork.getVappNetworkById(
						vcloudClient, record.getId());
				// find the parent network if any.
				if (vappNetwork.getResource().getConfiguration()
						.getParentNetwork() != null) {
					AdminOrgVdcNetwork adminOrgVdcNetwork = AdminOrgVdcNetwork
							.getOrgVdcNetworkByReference(vcloudClient,
									vappNetwork.getResource()
											.getConfiguration()
											.getParentNetwork());
					if (adminOrgVdcNetwork.getResource().getConfiguration()
							.getParentNetwork() != null) {
						parentNetworkName = adminOrgVdcNetwork.getResource()
								.getConfiguration().getParentNetwork()
								.getName();
					}
				}
			} catch (VCloudException e) {
				e.printStackTrace();
			}

			// port group query to get the vcenter network information.
			Expression expression = new Expression(
					QueryPortgroupField.NETWORKNAME, parentNetworkName,
					ExpressionType.EQUALS);
			QueryParams<QueryPortgroupField> portGroupParams = new QueryParams<QueryPortgroupField>();
			portGroupParams.setPageSize(128);
			portGroupParams.setFilter(new Filter(expression));
			RecordResult<QueryResultPortgroupRecordType> portGroupResult = queryService
					.queryIdRecords(QueryRecordType.PORTGROUP, portGroupParams);
			// iterate through the port group query result.
			for (QueryResultPortgroupRecordType portGroupRecord : portGroupResult
					.getRecords()) {
				// get the vcenter information.
				try {
					VMWVimServer vCenterServer = VMWVimServer
							.getVMWVimServerById(vcloudClient, portGroupRecord
									.getVc());
					System.out.print("	(vCenter) "
							+ vCenterServer.getResource().getName() + "(");
					System.out.println(vCenterServer.getResource().getUrl()
							+ ")");
				} catch (VCloudException e) {
					e.printStackTrace();
				}
				System.out.print("	(vCenter Network) ");
				System.out.print(portGroupRecord.getName() + "(");
				System.out.println(portGroupRecord.getMoref() + ")");
			}
		}
	}

	/**
	 * Lists the VM's correlation data with vCenter, host, datastore etc
	 *
	 * @throws VCloudException
	 */
	private static void getVMCorrelation() throws VCloudException {
		System.out.println("--------------");
		System.out.println("VM Correlation");
		System.out.println("--------------");
		QueryParams<QueryAdminVMField> adminVMParams = new QueryParams<QueryAdminVMField>();
		adminVMParams.setPageSize(128);
		Expression unresolvedExpression = new Expression(
				QueryAdminVMField.STATUS, "UNRESOLVED",
				ExpressionType.NOT_EQUALS);
		Expression failedCreationExpression = new Expression(
				QueryAdminVMField.STATUS, "UNRESOLVED",
				ExpressionType.NOT_EQUALS);
		List<Filter> filters = new ArrayList<Filter>();
		filters.add(new Filter(unresolvedExpression));
		filters.add(new Filter(failedCreationExpression));
		adminVMParams.setFilter(new Filter(filters, FilterType.AND));
		RecordResult<QueryResultAdminVMRecordType> adminVMResult = queryService
				.queryIdRecords(QueryRecordType.ADMINVM, adminVMParams);
		while (true) {
			for (QueryResultAdminVMRecordType adminVMRecord : adminVMResult
					.getRecords()) {
				System.out
						.println("	"
								+ adminVMRecord.getName()
								+ (adminVMRecord.isIsVAppTemplate() ? "(VAppTemplate VM)"
										: "(VApp VM)"));
				// get the vcenter information.
				try {
					VMWVimServer vCenterServer = VMWVimServer
							.getVMWVimServerById(vcloudClient, adminVMRecord
									.getVc());
					System.out.print("		(vCenter) "
							+ vCenterServer.getResource().getName() + "(");
					System.out.println(vCenterServer.getResource().getUrl()
							+ ")");
				} catch (VCloudException e) {
					e.printStackTrace();
				}
				System.out.println("		(StorageProfile) "
						+ adminVMRecord.getStorageProfileName());
				if (adminVMRecord.isIsVAppTemplate()) {
					try {
						VappTemplate vappTemplateVM = VappTemplate
								.getVappTemplateById(vcloudClient,
										adminVMRecord.getId());
						System.out.println("		(VM Details) "
								+ vappTemplateVM.getVMVimRef().getMoRef());
						System.out.println("		(Host Details) "
								+ vappTemplateVM.getVMHostVimRef().getMoRef()
								+ "(" + adminVMRecord.getHostName() + ")");
						System.out.println("		(Datastore Details) "
								+ vappTemplateVM.getVMDatastoreVimRef()
										.getMoRef() + "("
								+ adminVMRecord.getDatastoreName() + ")");
					} catch (VCloudException e) {
						e.printStackTrace();
					}
				} else {
					try {
						VM vm = VM.getVMById(vcloudClient, adminVMRecord
								.getId());
						System.out.println("		(VM Details) "
								+ vm.getVMVimRef().getMoRef());
						System.out.println("		(Host Details) "
								+ vm.getVMHostVimRef().getMoRef() + "("
								+ adminVMRecord.getHostName() + ")");
						System.out.println("		(Datastore Details) "
								+ vm.getVMDatastoreVimRef().getMoRef() + "("
								+ adminVMRecord.getDatastoreName() + ")");
					} catch (VCloudException e) {
						e.printStackTrace();
					}
				}
			}
			if (!adminVMResult.hasNextPage()) {
				break;
			}
			adminVMResult = adminVMResult.getNextPage();
		}
	}

	/**
	 * Lists the disks and its correlation with the StorageProfile and
	 * datastore.
	 *
	 * @throws VCloudException
	 */
	private static void getIndependentDiskCorrelation() throws VCloudException {
		System.out.println("----------------------------");
		System.out.println("Independent Disk Correlation");
		System.out.println("----------------------------");
		QueryParams<QueryAdminDiskField> adminDiskParams = new QueryParams<QueryAdminDiskField>();
		adminDiskParams.setPageSize(128);
		RecordResult<QueryResultAdminDiskRecordType> adminDiskResult = queryService
				.queryIdRecords(QueryRecordType.ADMINDISK, adminDiskParams);
		for (QueryResultAdminDiskRecordType adminDiskRecord : adminDiskResult
				.getRecords()) {
			System.out.println("	" + adminDiskRecord.getName());

			try {
				VMWVimServer vCenterServer = VMWVimServer.getVMWVimServerById(
						vcloudClient, adminDiskRecord.getVc());
				System.out.print("		(vCenter) "
						+ vCenterServer.getResource().getName() + "(");
				System.out.println(vCenterServer.getResource().getUrl() + ")");
			} catch (VCloudException e) {
				e.printStackTrace();
			}
			System.out.println("		(StorageProfile) "
					+ adminDiskRecord.getStorageProfileName());
			Expression expression = new Expression(QueryAdminDiskField.NAME,
					adminDiskRecord.getDatastoreName(), ExpressionType.EQUALS);
			QueryParams<QueryDatastoreField> datastoreParams = new QueryParams<QueryDatastoreField>();
			datastoreParams.setPageSize(128);
			datastoreParams.setFilter(new Filter(expression));
			RecordResult<QueryResultDatastoreRecordType> datastoreResult = queryService
					.queryIdRecords(QueryRecordType.DATASTORE, datastoreParams);
			for (QueryResultDatastoreRecordType datastoreRecord : datastoreResult
					.getRecords()) {
				System.out.println("		(Datastore) "
						+ datastoreRecord.getMoref() + "("
						+ datastoreRecord.getName() + ")");
			}

		}
	}

	/**
	 * Lists the correlation between the provider vdc and the resources in
	 * vCenter(resource pool, datastore, storage profile etc)
	 *
	 * @throws VCloudException
	 */
	private static void getProviderVdcCorrelation() throws VCloudException {
		System.out.println("------------------------");
		System.out.println("Provider Vdc Correlation");
		System.out.println("------------------------");
		QueryParams<QueryVMWProviderVdcField> adminDiskParams = new QueryParams<QueryVMWProviderVdcField>();
		adminDiskParams.setPageSize(128);
		RecordResult<QueryResultVMWProviderVdcRecordType> providerVdcResult = queryService
				.queryIdRecords(QueryRecordType.PROVIDERVDC, adminDiskParams);
		for (QueryResultVMWProviderVdcRecordType providerVdcRecord : providerVdcResult
				.getRecords()) {
			System.out.println("	" + providerVdcRecord.getName());
			VMWProviderVdc providerVdc;
			try {
				providerVdc = VMWProviderVdc.getVMWProviderVdcById(
						vcloudClient, providerVdcRecord.getId());
				// get vcenter information
				for (ReferenceType vCenterRef : providerVdc
						.getVMWVimServerRefs()) {
					VMWVimServer vCenterServer = VMWVimServer
							.getVMWVimServerByReference(vcloudClient,
									vCenterRef);
					System.out.print("		(vCenter) "
							+ vCenterServer.getResource().getName() + "(");
					System.out.println(vCenterServer.getResource().getUrl()
							+ ")");
				}
				// get the host information
				for (ReferenceType hostRef : providerVdc.getVMWHostRefs()) {
					System.out.println("		(Host) " + hostRef.getName());
				}
				// get the storage profile information
				for (ReferenceType storageProfileRef : providerVdc
						.getVMWProviderVdcStorageProfileRefs()) {
					System.out.println("		(StorageProfile) "
							+ storageProfileRef.getName());
				}
			} catch (VCloudException e) {
				e.printStackTrace();
			}

			// get the datastore information
			QueryParams<QueryDatastoreProviderVdcRelationField> provVdcDatastoreParams = new QueryParams<QueryDatastoreProviderVdcRelationField>();
			provVdcDatastoreParams.setPageSize(128);
			Expression expression = new Expression(
					QueryProviderVdcResourcePoolRelationField.PROVIDERVDC,
					providerVdcRecord.getId(), ExpressionType.EQUALS);
			provVdcDatastoreParams.setFilter(new Filter(expression));
			RecordResult<QueryResultDatastoreProviderVdcRelationRecordType> provVdcDatastoreRelationResult = queryService
					.queryIdRecords(
							QueryRecordType.DATASTOREPROVIDERVDCRELATION,
							provVdcDatastoreParams);
			for (QueryResultDatastoreProviderVdcRelationRecordType provVdcDatastoreRelationRecord : provVdcDatastoreRelationResult
					.getRecords()) {
				System.out.println("		(Datastore) "
						+ provVdcDatastoreRelationRecord.getMoref() + "("
						+ provVdcDatastoreRelationRecord.getName() + ")");
			}
			// get the resource pool information
			QueryParams<QueryProviderVdcResourcePoolRelationField> provVdcResPoolParams = new QueryParams<QueryProviderVdcResourcePoolRelationField>();
			provVdcResPoolParams.setPageSize(128);
			provVdcResPoolParams.setFilter(new Filter(expression));
			RecordResult<QueryResultProviderVdcResourcePoolRelationRecordType> provVdcResPoolRelationResult = queryService
					.queryIdRecords(
							QueryRecordType.PROVIDERVDCRESOURCEPOOLRELATION,
							provVdcResPoolParams);
			for (QueryResultProviderVdcResourcePoolRelationRecordType provVdcResPoolRelationRecord : provVdcResPoolRelationResult
					.getRecords()) {
				System.out.println("		(ResourcePool) "
						+ provVdcResPoolRelationRecord.getResourcePoolMoref()
						+ "(" + provVdcResPoolRelationRecord.getName() + ")");
			}
		}
	}

	/**
	 * Lists the correlation between the external network and the vcenter
	 * network information.
	 *
	 * @throws VCloudException
	 */
	private static void getExternalNetworkCorrelation() throws VCloudException {
		System.out.println("---------------------------");
		System.out.println("External Network Correlation");
		System.out.println("---------------------------");
		// external network query
		QueryParams<QueryNetworkField> extNetworkParams = new QueryParams<QueryNetworkField>();
		extNetworkParams.setPageSize(128);
		RecordResult<QueryResultNetworkRecordType> extNetworkResult = queryService
				.queryIdRecords(QueryRecordType.EXTERNALNETWORK,
						extNetworkParams);
		// iterate through the external network's
		for (QueryResultNetworkRecordType record : extNetworkResult
				.getRecords()) {
			System.out.println("   " + record.getName());
			// port group query to get the vcenter network information.
			Expression expression = new Expression(
					QueryPortgroupField.NETWORKNAME, record.getName(),
					ExpressionType.EQUALS);
			QueryParams<QueryPortgroupField> portGroupParams = new QueryParams<QueryPortgroupField>();
			portGroupParams.setPageSize(128);
			portGroupParams.setFilter(new Filter(expression));
			RecordResult<QueryResultPortgroupRecordType> portGroupResult = queryService
					.queryIdRecords(QueryRecordType.PORTGROUP, portGroupParams);
			// iterate through the port group query result.
			for (QueryResultPortgroupRecordType portGroupRecord : portGroupResult
					.getRecords()) {
				// get the vcenter information.
				try {
					VMWVimServer vCenterServer = VMWVimServer
							.getVMWVimServerById(vcloudClient, portGroupRecord
									.getVc());
					System.out.print("	(vCenter) "
							+ vCenterServer.getResource().getName() + "(");
					System.out.println(vCenterServer.getResource().getUrl()
							+ ")");
				} catch (VCloudException e) {
					e.printStackTrace();
				}
				System.out.print("	(vCenter Network) ");
				System.out.print(portGroupRecord.getMoref() + "(");
				System.out.println(portGroupRecord.getName() + ")");
			}
		}
	}

	/**
	 * Lists the correlation between the network pools and the vcenter
	 * portgroups and switches.
	 *
	 * @throws VCloudException
	 */
	private static void getNetworkPoolCorrelation() throws VCloudException {
		System.out.println("------------------------");
		System.out.println("Network Pool Correlation");
		System.out.println("------------------------");
		// network pool query
		QueryParams<QueryNetworkPoolField> networkPoolParams = new QueryParams<QueryNetworkPoolField>();
		networkPoolParams.setPageSize(128);
		RecordResult<QueryResultNetworkPoolRecordType> networkPoolResult = queryService
				.queryIdRecords(QueryRecordType.NETWORKPOOL, networkPoolParams);
		// iterate through the network pools
		for (QueryResultNetworkPoolRecordType record : networkPoolResult
				.getRecords()) {
			ReferenceType vCenterRef = null;
			System.out.println("   " + record.getName());
			try {
				VMWNetworkPool networkPool = VMWNetworkPool
						.getVMWNetworkPoolById(vcloudClient, record.getId());
				// VXLAN type
				if (record.getNetworkPoolType() == 3) {
					// not much data to be displayed
					System.out.println();
				} // Cloud Network Isolation type
				else if (record.getNetworkPoolType() == 1) {
					FencePoolType fencePoolType = (FencePoolType) networkPool
							.getResource();
					System.out.println("		(vCenter Network) "
							+ fencePoolType.getVimSwitchRef().getMoRef());
					vCenterRef = fencePoolType.getVimSwitchRef()
							.getVimServerRef();

				} // Port Group type
				else if (record.getNetworkPoolType() == 2) {
					PortGroupPoolType portGroupPoolType = (PortGroupPoolType) networkPool
							.getResource();
					for (VimObjectRefType portGroupVimRef : portGroupPoolType
							.getPortGroupRefs().getVimObjectRef()) {
						System.out.println("		(vCenter Network) "
								+ portGroupVimRef.getMoRef());
					}
					vCenterRef = portGroupPoolType.getVimServer();
				} // VLAN type
				else if (record.getNetworkPoolType() == 0) {
					VlanPoolType vlanPoolType = (VlanPoolType) networkPool
							.getResource();
					System.out.println("		(vCenter Network) "
							+ vlanPoolType.getVimSwitchRef().getMoRef());
					vCenterRef = vlanPoolType.getVimSwitchRef()
							.getVimServerRef();
				}
			} catch (VCloudException e1) {
				e1.printStackTrace();
			}
			// get the vcenter information.
			if (vCenterRef != null) {
				try {
					VMWVimServer vCenterServer = VMWVimServer
							.getVMWVimServerByReference(vcloudClient,
									vCenterRef);
					System.out.print("		(vCenter) "
							+ vCenterServer.getResource().getName() + "(");
					System.out.println(vCenterServer.getResource().getUrl()
							+ ")");
				} catch (VCloudException e) {
					e.printStackTrace();
				}

			}
		}
	}

	/**
	 * Lists the correlation between the hosts and the vcenter.
	 *
	 * @throws VCloudException
	 */
	private static void getHostCorrelation() throws VCloudException {

		System.out.println("----------------");
		System.out.println("Host Correlation");
		System.out.println("----------------");
		// host query
		QueryParams<QueryHostField> hostParams = new QueryParams<QueryHostField>();
		hostParams.setPageSize(128);
		RecordResult<QueryResultHostRecordType> hostResult = queryService
				.queryIdRecords(QueryRecordType.HOST, hostParams);
		// iterate through the hosts
		for (QueryResultHostRecordType record : hostResult.getRecords()) {
			System.out.println("   " + record.getName() + "("
					+ record.getOsVersion() + ")");
			try {
				VMWVimServer vCenterServer = VMWVimServer.getVMWVimServerById(
						vcloudClient, record.getVc());
				System.out.print("		(vCenter) "
						+ vCenterServer.getResource().getName() + "(");
				System.out.println(vCenterServer.getResource().getUrl() + ")");
			} catch (VCloudException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Lists the correlation between the datastore and the vcenter.
	 *
	 * @throws VCloudException
	 */
	private static void getDatastoreCorrelation() throws VCloudException {
		System.out.println("---------------------");
		System.out.println("Datastore Correlation");
		System.out.println("---------------------");
		// datastore query
		QueryParams<QueryDatastoreField> datastoreParams = new QueryParams<QueryDatastoreField>();
		datastoreParams.setPageSize(128);
		RecordResult<QueryResultDatastoreRecordType> datastoreResult = queryService
				.queryIdRecords(QueryRecordType.DATASTORE, datastoreParams);
		// iterate through the datastores
		for (QueryResultDatastoreRecordType record : datastoreResult
				.getRecords()) {
			System.out.println("   " + record.getName() + "("
					+ record.getDatastoreType() + ")");
			try {
				VMWVimServer vCenterServer = VMWVimServer.getVMWVimServerById(
						vcloudClient, record.getVc());
				System.out.print("		(vCenter) "
						+ vCenterServer.getResource().getName() + "(");
				System.out.println(vCenterServer.getResource().getUrl() + ")");
			} catch (VCloudException e) {
				e.printStackTrace();
			}
			System.out.println("		(Datastore) " + record.getMoref() + "("
					+ record.getName() + ")");
		}
	}

	/**
	 * Correlation sample main.
	 *
	 * @param args
	 * @throws HttpException
	 * @throws VCloudException
	 * @throws IOException
	 * @throws KeyManagementException
	 * @throws NoSuchAlgorithmException
	 * @throws UnrecoverableKeyException
	 * @throws KeyStoreException
	 * @throws CertificateException
	 */

	public static void main(String args[]) throws HttpException,
			VCloudException, IOException, KeyManagementException,
			NoSuchAlgorithmException, UnrecoverableKeyException,
			KeyStoreException, CertificateException {

		performLogin(args);

		getVdcCorrelation();

		getOrgVdcNetworkCorrelation();

		getVappNetworkCorrelation();

		getVMCorrelation();

		getIndependentDiskCorrelation();

		getProviderVdcCorrelation();

		getExternalNetworkCorrelation();

		getNetworkPoolCorrelation();

		getHostCorrelation();

		getDatastoreCorrelation();

	}
}

package com.vmware.vcloud.automate;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;

import javax.xml.bind.JAXBElement;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.vmware.vcloud.api.rest.schema.AdminOrgType;
import com.vmware.vcloud.api.rest.schema.ComposeVAppParamsType;
import com.vmware.vcloud.api.rest.schema.FirewallRuleProtocols;
import com.vmware.vcloud.api.rest.schema.FirewallRuleType;
import com.vmware.vcloud.api.rest.schema.FirewallServiceType;
import com.vmware.vcloud.api.rest.schema.GatewayConfigurationType;
import com.vmware.vcloud.api.rest.schema.GatewayFeaturesType;
import com.vmware.vcloud.api.rest.schema.GatewayInterfaceType;
import com.vmware.vcloud.api.rest.schema.GatewayInterfacesType;
import com.vmware.vcloud.api.rest.schema.GatewayNatRuleType;
import com.vmware.vcloud.api.rest.schema.GatewayType;
import com.vmware.vcloud.api.rest.schema.GuestCustomizationSectionType;
import com.vmware.vcloud.api.rest.schema.InstantiationParamsType;
import com.vmware.vcloud.api.rest.schema.IpRangeType;
import com.vmware.vcloud.api.rest.schema.IpRangesType;
import com.vmware.vcloud.api.rest.schema.IpScopeType;
import com.vmware.vcloud.api.rest.schema.IpScopesType;
import com.vmware.vcloud.api.rest.schema.NatRuleType;
import com.vmware.vcloud.api.rest.schema.NatServiceType;
import com.vmware.vcloud.api.rest.schema.NetworkConfigSectionType;
import com.vmware.vcloud.api.rest.schema.NetworkConfigurationType;
import com.vmware.vcloud.api.rest.schema.NetworkConnectionSectionType;
import com.vmware.vcloud.api.rest.schema.NetworkConnectionType;
import com.vmware.vcloud.api.rest.schema.NetworkServiceType;
import com.vmware.vcloud.api.rest.schema.ObjectFactory;
import com.vmware.vcloud.api.rest.schema.OrgGeneralSettingsType;
import com.vmware.vcloud.api.rest.schema.OrgLeaseSettingsType;
import com.vmware.vcloud.api.rest.schema.OrgPasswordPolicySettingsType;
import com.vmware.vcloud.api.rest.schema.OrgSettingsType;
import com.vmware.vcloud.api.rest.schema.OrgVAppTemplateLeaseSettingsType;
import com.vmware.vcloud.api.rest.schema.OrgVdcNetworkType;
import com.vmware.vcloud.api.rest.schema.QueryResultAdminVAppTemplateRecordType;
import com.vmware.vcloud.api.rest.schema.ReferenceType;
import com.vmware.vcloud.api.rest.schema.SourcedCompositionItemParamType;
import com.vmware.vcloud.api.rest.schema.SubnetParticipationType;
import com.vmware.vcloud.api.rest.schema.TaskType;
import com.vmware.vcloud.api.rest.schema.TasksInProgressType;
import com.vmware.vcloud.api.rest.schema.VAppNetworkConfigurationType;
import com.vmware.vcloud.api.rest.schema.ovf.MsgType;
import com.vmware.vcloud.api.rest.schema.ovf.RASDType;
import com.vmware.vcloud.api.rest.schema.ovf.SectionType;
import com.vmware.vcloud.api.rest.schema.ovf.VirtualHardwareSectionType;
import com.vmware.vcloud.model.VCloudOrganization;
import com.vmware.vcloud.sdk.Expression;
import com.vmware.vcloud.sdk.Filter;
import com.vmware.vcloud.sdk.Organization;
import com.vmware.vcloud.sdk.QueryParams;
import com.vmware.vcloud.sdk.RecordResult;
import com.vmware.vcloud.sdk.Task;
import com.vmware.vcloud.sdk.VCloudException;
import com.vmware.vcloud.sdk.VM;
import com.vmware.vcloud.sdk.Vapp;
import com.vmware.vcloud.sdk.VappTemplate;
import com.vmware.vcloud.sdk.VcloudClient;
import com.vmware.vcloud.sdk.Vdc;
import com.vmware.vcloud.sdk.admin.AdminOrgVdcNetwork;
import com.vmware.vcloud.sdk.admin.AdminOrganization;
import com.vmware.vcloud.sdk.admin.AdminVdc;
import com.vmware.vcloud.sdk.admin.EdgeGateway;
import com.vmware.vcloud.sdk.admin.ExternalNetwork;
import com.vmware.vcloud.sdk.admin.User;
import com.vmware.vcloud.sdk.admin.VcloudAdmin;
import com.vmware.vcloud.sdk.constants.FenceModeValuesType;
import com.vmware.vcloud.sdk.constants.FirewallPolicyType;
import com.vmware.vcloud.sdk.constants.GatewayBackingConfigValuesType;
import com.vmware.vcloud.sdk.constants.GatewayEnums;
import com.vmware.vcloud.sdk.constants.IpAddressAllocationModeType;
import com.vmware.vcloud.sdk.constants.Version;
import com.vmware.vcloud.sdk.constants.query.ExpressionType;
import com.vmware.vcloud.sdk.constants.query.QueryAdminVAppTemplateField;
import com.vmware.vcloud.sdk.constants.query.QueryRecordType;

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

	
	/**
	 * Create the compose vapp params. Creating compose vapp params containing
	 * the vapp templates vms. The same vm is added 3 times with different
	 * names.
	 * 
	 * @param vappTemplateRef
	 * @param vdc
	 * @return
	 * @throws VCloudException
	 */

	public static ComposeVAppParamsType createComposeParams(ReferenceType vappTemplateRef, Vdc vdc)
			throws VCloudException {

	
		// Get the href of the OrgNetwork to which we can connect the vApp network
		NetworkConfigurationType networkConfigurationType = new NetworkConfigurationType();
		if (vdc.getResource().getAvailableNetworks().getNetwork().size() == 0) {
			System.out.println("No Networks in vdc to instantiate vapp");
			System.exit(0);
		}		
		
		// Specify the NetworkConfiguration for the vApp network
		System.out.println("ParentNetwork: "+ vdc.getResource().getAvailableNetworks().getNetwork().get(0).getName());
		networkConfigurationType.setParentNetwork(vdc.getResource().getAvailableNetworks().getNetwork().get(0));
		
		// FIXME: use NATROUTED and change BRIDGED later to solve vCloud Director bug
		networkConfigurationType.setFenceMode(FenceModeValuesType.NATROUTED.value()); 

		VAppNetworkConfigurationType vAppNetworkConfigurationType = new VAppNetworkConfigurationType();
		vAppNetworkConfigurationType.setConfiguration(networkConfigurationType); 
		vAppNetworkConfigurationType.setNetworkName(vdc.getResource().getAvailableNetworks().getNetwork().get(0).getName());
		
		NetworkConfigSectionType networkConfigSectionType = new NetworkConfigSectionType();
		MsgType networkInfo = new MsgType();
		networkConfigSectionType.setInfo(networkInfo);
		
		List<VAppNetworkConfigurationType> vAppNetworkConfigs = networkConfigSectionType.getNetworkConfig();
		vAppNetworkConfigs.add(vAppNetworkConfigurationType);

		// create vApp config
		InstantiationParamsType vappOrvAppTemplateInstantiationParamsType = new InstantiationParamsType(); 
		List<JAXBElement<? extends SectionType>> vappSections = vappOrvAppTemplateInstantiationParamsType.getSection();
		vappSections.add(new ObjectFactory().createNetworkConfigSection(networkConfigSectionType));
		//vappSections.add(new ObjectFactory().createNetworkConnectionSection(networkObject));

		ComposeVAppParamsType composeVAppParamsType = new ComposeVAppParamsType(); 
		composeVAppParamsType.setDeploy(false);
		composeVAppParamsType.setInstantiationParams(vappOrvAppTemplateInstantiationParamsType);
		composeVAppParamsType.setName(vCloudOrg.getvApp().getName());
		composeVAppParamsType.setDescription(vCloudOrg.getvApp().getDescription());
		
		List<SourcedCompositionItemParamType> items = composeVAppParamsType.getSourcedItem();

		// getting the vApp Templates first vm.
		VappTemplate vappTemplate = VappTemplate.getVappTemplateByReference(client, vappTemplateRef);
		VappTemplate vm = null; //vappTemplate.getChildren().get(0);
		
		for(VappTemplate child : vappTemplate.getChildren()){
			if(child.isVm()){
				vm = child;
			}			
		}
		
		if (vm == null) {
			System.out.println("Could not find template VM");
			System.exit(1);
		}
		
		String vmHref = vm.getReference().getHref();

		SourcedCompositionItemParamType vappTemplateItem = new SourcedCompositionItemParamType();
		ReferenceType vappTemplateVMRef = new ReferenceType();
		vappTemplateVMRef.setHref(vmHref);
		vappTemplateVMRef.setName(vCloudOrg.getvApp().getVmName());
		vappTemplateItem.setSource(vappTemplateVMRef);

		NetworkConnectionSectionType networkConnectionSectionType = new NetworkConnectionSectionType();
		networkConnectionSectionType.setInfo(networkInfo);

		NetworkConnectionType networkConnectionType = new NetworkConnectionType();
		networkConnectionType.setNetwork(vdc.getResource().getAvailableNetworks().getNetwork().get(0).getName());
		networkConnectionType.setIpAddressAllocationMode(IpAddressAllocationModeType.POOL.value());
		networkConnectionType.setIsConnected(Boolean.TRUE);
		networkConnectionSectionType.getNetworkConnection().add(networkConnectionType);

		InstantiationParamsType vmInstantiationParamsType = new InstantiationParamsType();
		List<JAXBElement<? extends SectionType>> vmSections = vmInstantiationParamsType.getSection();
		vmSections.add(new ObjectFactory().createNetworkConnectionSection(networkConnectionSectionType));
		
		vappTemplateItem.setInstantiationParams(vmInstantiationParamsType);

		items.add(vappTemplateItem);

		return composeVAppParamsType;

	}
	
	/**
	 * Finding a vdc
	 * 
	 * @param vdcName
	 * @param orgName
	 * @return {@link Vdc}
	 * @throws VCloudException
	 */
	public static Vdc findVdc(String orgName, String vdcName) throws VCloudException {
		System.out.println("Org Name: " + orgName);
		System.out.println("--------------------");
		ReferenceType orgRef = client.getOrgRefsByName().get(orgName);
		Organization org = Organization.getOrganizationByReference(client, orgRef);
		ReferenceType vdcRef = org.getVdcRefByName(vdcName);
		System.out.println("Vdc Name: " + vdcName);
		System.out.println("--------------------");
		return Vdc.getVdcByReference(client, vdcRef);
	}
	
	/**
	 * Search the vapp template reference. Since the vapptemplate is not unique
	 * under a vdc. This method returns the first occurance of the vapptemplate
	 * in that vdc.
	 * 
	 * @return
	 * @throws VCloudException
	 */
	public static ReferenceType findVappTemplateRef(String orgName, String vdcName, String vappTemplateName)
			throws VCloudException {
		ReferenceType vappTemplateRef = new ReferenceType();
		
		QueryParams<QueryAdminVAppTemplateField> queryParams = new QueryParams<QueryAdminVAppTemplateField>();
		queryParams.setFilter(new Filter(new Expression(QueryAdminVAppTemplateField.CATALOGNAME, "AIS-VM-TEMPLATES-CATALOG", ExpressionType.EQUALS)));
		RecordResult<QueryResultAdminVAppTemplateRecordType> vappTemplateResult = client.getQueryService().queryRecords(QueryRecordType.ADMINVAPPTEMPLATE, queryParams);
		for (QueryResultAdminVAppTemplateRecordType vappTemplateRecord : vappTemplateResult.getRecords()) { 
			if(vappTemplateRecord.getName().equals(vappTemplateName)){				
				vappTemplateRef.setName(vappTemplateName);
				vappTemplateRef.setHref(vappTemplateRecord.getHref());				
				return vappTemplateRef;
			}
		}		
		
		return null;	
	}
	
	private static void addFirewallRule(List<FirewallRuleType> fwRules, String name, String protocol, String srcIp,
			String dstIp, String portRange) {

		FirewallRuleType firewallRuleType = new FirewallRuleType();
		firewallRuleType.setDescription(name);
		firewallRuleType.setSourceIp(srcIp);
		firewallRuleType.setSourcePort(-1);
		firewallRuleType.setDestinationIp(dstIp);
		FirewallRuleProtocols protocols = new FirewallRuleProtocols();

		if (protocol.equalsIgnoreCase("ICMP")) {
			protocols.setIcmp(true);
			firewallRuleType.setIcmpSubType("any");
		} else if (protocol.equalsIgnoreCase("TCP")) {
			protocols.setTcp(true);
			firewallRuleType.setDestinationPortRange(portRange);			
		} else if (protocol.equalsIgnoreCase("UDP")) {
			protocols.setUdp(true);
			firewallRuleType.setDestinationPortRange(portRange);
		} else if (protocol.equalsIgnoreCase("ANY")) {
			protocols.setAny(true);
			firewallRuleType.setDestinationPortRange(portRange);
		}

		firewallRuleType.setProtocols(protocols);
		fwRules.add(firewallRuleType);
	}
	
	/**
	 * Create params for Edge Gateway
	 *
	 * @param externalNetwork
	 *            {@link ReferenceType}
	 * @return GatewayType
	 * @throws VCloudException
	 */
	private static GatewayType createEdgeGatewayParams(ReferenceType externalNetwork, String edgeGatewayName)
			throws VCloudException {
		ExternalNetwork externalNet = ExternalNetwork.getExternalNetworkByReference(client, externalNetwork);
		IpScopeType externalNetIpScope = externalNet.getResource().getConfiguration().getIpScopes().getIpScope().get(0);

		GatewayType gatewayParams = new GatewayType();
		gatewayParams.setName(edgeGatewayName);
		gatewayParams.setDescription(vCloudOrg.getEdgeGateway().getGatewayParams().getDescription());
		GatewayConfigurationType gatewayConfig = new GatewayConfigurationType();
		gatewayConfig.setGatewayBackingConfig(GatewayBackingConfigValuesType.COMPACT.value());
		GatewayInterfaceType gatewayInterface = new GatewayInterfaceType();
		gatewayInterface.setDisplayName(vCloudOrg.getEdgeGateway().getGatewayParams().getGatewayConfiguration()
				.getGatewayInterfaces().get(0).getDisplayName());
		gatewayInterface.setNetwork(externalNetwork);
		gatewayInterface.setInterfaceType(GatewayEnums.UPLINK.value());

		SubnetParticipationType subnetParticipationType = new SubnetParticipationType();
		subnetParticipationType.setGateway(externalNetIpScope.getGateway());
		subnetParticipationType.setNetmask(externalNetIpScope.getNetmask());
		
		IpRangesType ipRanges = new IpRangesType();
		IpRangeType ipRange = new IpRangeType();

		String startAddress = vCloudOrg.getEdgeGateway().getGatewayParams().getGatewayConfiguration().getGatewayInterfaces()
				.get(0).getSubnetParticipation().getIpRanges().get(0).getStartAddress();
		
		String endAddress = vCloudOrg.getEdgeGateway().getGatewayParams().getGatewayConfiguration().getGatewayInterfaces()
				.get(0).getSubnetParticipation().getIpRanges().get(0).getEndAddress();
		
		// Specify IP range
		ipRange.setStartAddress(startAddress);
		ipRange.setEndAddress(endAddress);
		
		ipRanges.getIpRange().add(ipRange);
		subnetParticipationType.setIpRanges(ipRanges);
		gatewayInterface.getSubnetParticipation().add(subnetParticipationType);
		
		// Is use for default route
		gatewayInterface.setUseForDefaultRoute(vCloudOrg.getEdgeGateway().getGatewayParams().getGatewayConfiguration()
				.getGatewayInterfaces().get(0).isUseForDefaultRoute());
		
		GatewayInterfacesType interfaces = new GatewayInterfacesType();
		interfaces.getGatewayInterface().add(gatewayInterface);
		gatewayConfig.setGatewayInterfaces(interfaces);
		

		// Edge Gateway Service Configuration Section
/*		String[] subStr = startAddress.split("\\.");
		String[] subStr2 = endAddress.split("\\.");
		int currentAddress = Integer.parseInt(subStr[3]);*/
/*		int increment = 1;
		if (subStr[2].equalsIgnoreCase(subStr2[2])) {
			increment = Math.round((Integer.parseInt(subStr2[3]) - currentAddress) / 6);
			if (increment < 1) {
				System.err.println("External Network IP Address Pool is not enough to configure " + "the edge gateway services");
				return null;
			}
		} else
			increment = 5;*/
		//----------------------------------------------------
		GatewayFeaturesType gatewayFeatures = new GatewayFeaturesType();

		ObjectFactory objectFactory = new ObjectFactory();

		// Edge Gateway NAT service configuration
		NatServiceType natService = new NatServiceType();
		natService.setExternalIp(startAddress);
		// To Enable the service using this flag
		natService.setIsEnabled(Boolean.TRUE);

        // Configuring destination NAT
        NatRuleType dnatRule = new NatRuleType();
        
        ReferenceType refd = new ReferenceType();
        refd.setHref(gatewayInterface.getNetwork().getHref());
        
        // Setting Rule type Destination Nat (DNAT)
        dnatRule.setRuleType("DNAT");
        dnatRule.setIsEnabled(Boolean.TRUE);
        dnatRule.setDescription("DNAT");
        GatewayNatRuleType dgatewayNat = new GatewayNatRuleType();
       
        // Network on which NAT rules to be applied
        dgatewayNat.setInterface(refd);

        // Setting Original IP/Port
        dgatewayNat.setOriginalIp(startAddress);
        dgatewayNat.setOriginalPort("any");
        dgatewayNat.setTranslatedIp("10.1.1.0/24");

        // To allow all ports and all protocols
		dgatewayNat.setTranslatedPort("any");
		dgatewayNat.setProtocol("any");

        // Setting Destination IP
        dnatRule.setGatewayNatRule(dgatewayNat);
        natService.getNatRule().add(dnatRule);

        // Configuring Source NAT (SNAT)
        NatRuleType snatRule = new NatRuleType();
        
        // Setting Rule type Source Nat SNAT
        snatRule.setRuleType("SNAT");
        snatRule.setDescription("SNAT");
        snatRule.setIsEnabled(Boolean.TRUE);
        GatewayNatRuleType sgatewayNat = new GatewayNatRuleType();


        // Network on which NAT rules to be applied
        sgatewayNat.setInterface(refd);

        // To allow all ports and all protocols
        sgatewayNat.setProtocol("any");
        sgatewayNat.setTranslatedPort("any");
        
        // Setting Original IP/Port
        sgatewayNat.setOriginalIp("10.1.1.0/24");
        sgatewayNat.setOriginalPort("any");
        sgatewayNat.setTranslatedIp(startAddress);
      

        snatRule.setGatewayNatRule(sgatewayNat);
        natService.getNatRule().add(snatRule);
        		
		JAXBElement<NetworkServiceType> nat = objectFactory.createNetworkService(natService);
		gatewayFeatures.getNetworkService().add(nat);

		// Edge Gateway Firewall service configuration
		FirewallServiceType firewallService = new FirewallServiceType(); 
		firewallService.setIsEnabled(true);
		firewallService.setDefaultAction(FirewallPolicyType.DROP.value());
		firewallService.setLogDefaultAction(false);
		
		List <FirewallRuleType> fwRules = firewallService.getFirewallRule();
        addFirewallRule(fwRules, "PING OUT", "ICMP", "10.1.1.0/24", "Any", "Any");
        addFirewallRule(fwRules, "DNS OUT", "UDP", "10.1.1.0/24", "Any", "53");
        addFirewallRule(fwRules, "NTP OUT", "UDP", "10.1.1.0/24", "Any", "123");
        addFirewallRule(fwRules, "HTTP OUT", "TCP", "10.1.1.0/24", "Any", "80");
        addFirewallRule(fwRules, "HTTPS OUT", "TCP", "10.1.1.0/24", "Any", "443");
        addFirewallRule(fwRules, "PING IN", "ICMP", "external", "internal", "Any");
        
		JAXBElement<FirewallServiceType> firewall = objectFactory.createFirewallService(firewallService); 
		gatewayFeatures.getNetworkService().add(firewall);

		/*// Edge Gateway DHCP service configuration
		DhcpServiceType dhcpService = new DhcpServiceType();
		dhcpService.setDefaultLeaseTime(0);
		dhcpService.setIpRange(ipRange);
		dhcpService.setIsEnabled(true);
		dhcpService.setPrimaryNameServer("r2");
		dhcpService.setSubMask(externalNetIpScope.getNetmask());
		dhcpService.setDefaultLeaseTime(3600);
		dhcpService.setMaxLeaseTime(7200);

		JAXBElement<DhcpServiceType> dhcp = objectFactory.createDhcpService(dhcpService);
		gatewayFeatures.getNetworkService().add(dhcp);

		// Edge Gateway Load Balancer service configuration
		LoadBalancerServiceType loadBalancer = new LoadBalancerServiceType();

		LoadBalancerPoolType pool = new LoadBalancerPoolType();
		pool.setDescription("Pool Desc");
		pool.setName("PoolName");
		pool.setOperational(true);

		LBPoolHealthCheckType lBPoolHealthCheck = new LBPoolHealthCheckType();
		lBPoolHealthCheck.setHealthThreshold("2");
		lBPoolHealthCheck.setUnhealthThreshold("3");
		lBPoolHealthCheck.setInterval("5");
		lBPoolHealthCheck.setMode("HTTP");
		lBPoolHealthCheck.setTimeout("15");

		LBPoolMemberType lBPoolMember = new LBPoolMemberType();
		currentAddress += increment;
		lBPoolMember.setIpAddress(subStr[0] + "." + subStr[1] + "." + subStr[2] + "." + currentAddress);
		lBPoolMember.setWeight("1");

		LBPoolServicePortType lBPoolServicePort = new LBPoolServicePortType();
		lBPoolServicePort.setIsEnabled(true);
		lBPoolServicePort.setAlgorithm("ROUND_ROBIN");
		lBPoolServicePort.setHealthCheckPort("80");
		lBPoolServicePort.getHealthCheck().add(lBPoolHealthCheck);
		lBPoolServicePort.setProtocol("HTTP");
		lBPoolServicePort.setPort("80");

		pool.getServicePort().add(lBPoolServicePort);

		pool.getMember().add(lBPoolMember);
		loadBalancer.getPool().add(pool);

		LoadBalancerVirtualServerType loadBalancerVirtualServer = new LoadBalancerVirtualServerType();
		loadBalancerVirtualServer.setDescription("desc");
		loadBalancerVirtualServer.setIsEnabled(true);
		currentAddress += increment;
		loadBalancerVirtualServer.setIpAddress(subStr[0] + "." + subStr[1] + "." + subStr[2] + "." + currentAddress);
		loadBalancerVirtualServer.setName("VirtualServerName2");
		loadBalancerVirtualServer.setPool("PoolName");
		loadBalancerVirtualServer.setLogging(true);
		loadBalancerVirtualServer.setInterface(externalNetwork);

		LBVirtualServerServiceProfileType lBVirtualServerServiceProfile = new LBVirtualServerServiceProfileType();
		lBVirtualServerServiceProfile.setProtocol("HTTP");
		lBVirtualServerServiceProfile.setPort("80");
		lBVirtualServerServiceProfile.setIsEnabled(true);

		LBPersistenceType lBPersistence = new LBPersistenceType();
		lBPersistence.setCookieMode("INSERT");
		lBPersistence.setCookieName("CookieName2");
		lBPersistence.setMethod("COOKIE");
		lBVirtualServerServiceProfile.setPersistence(lBPersistence);
		loadBalancerVirtualServer.getServiceProfile().add(lBVirtualServerServiceProfile);

		loadBalancer.getVirtualServer().add(loadBalancerVirtualServer);
		loadBalancer.setIsEnabled(true);

		JAXBElement<LoadBalancerServiceType> load = objectFactory.createLoadBalancerService(loadBalancer);
		gatewayFeatures.getNetworkService().add(load);

		// Edge Gateway Static Routing service configuration
		StaticRoutingServiceType staticRouting = new StaticRoutingServiceType();
		staticRouting.setIsEnabled(true);
		StaticRouteType staticRoute = new StaticRouteType();
		staticRoute.setName("RouteName");
		staticRoute.setNetwork(subStr[0] + "." + subStr[1] + ".2.0/24");
		currentAddress += increment;
		staticRoute.setNextHopIp(subStr[0] + "." + subStr[1] + "." + subStr[2] + "." + currentAddress);
		staticRoute.setGatewayInterface(externalNetwork);
		staticRoute.setInterface("External");
		staticRouting.getStaticRoute().add(staticRoute);

		JAXBElement<StaticRoutingServiceType> route = objectFactory.createStaticRoutingService(staticRouting);
		gatewayFeatures.getNetworkService().add(route);

		// Edge Gateway VPN service configuration
		IpsecVpnServiceType vpn = new IpsecVpnServiceType();
		vpn.setExternalIpAddress(
				subStr[0] + "." + subStr[1] + "." + subStr[2] + "." + (Integer.parseInt(subStr[3]) - 5));
		vpn.setIsEnabled(false);
		currentAddress += increment;
		vpn.setPublicIpAddress(subStr[0] + "." + subStr[1] + "." + subStr[2] + "." + currentAddress);
		IpsecVpnTunnelType ipsecVpnTunnel = new IpsecVpnTunnelType();
		ipsecVpnTunnel.setMtu(1500);
		ipsecVpnTunnel.setName("VpnName");

		JAXBElement<IpsecVpnServiceType> ipsecVpn = objectFactory.createIpsecVpnService(vpn);
		gatewayFeatures.getNetworkService().add(ipsecVpn);*/

		gatewayConfig.setEdgeGatewayServiceConfiguration(gatewayFeatures);
		// ------------------------------------------------------------------------
		
		gatewayParams.setConfiguration(gatewayConfig);

		return gatewayParams;
	}


	/**
	 * Gets External Network Reference
	 * 
	 * @param networkName
	 *            {@link String}
	 * @return {@link ReferenceType}
	 * 
	 * @throws VCloudException
	 */
	private static ReferenceType getExternalNetworkRef(String networkName) throws VCloudException {
		return client.getVcloudAdmin().getExternalNetworkRefByName(networkName);
	}

	/**
	 * Adding nat routed org vdc network to the organization
	 * 
	 * @param adminOrg
	 *            {@link AdminOrganization}
	 * @throws VCloudException
	 * @throws TimeoutException
	 */
	private static void addNatRoutedOrgVdcNetwork(AdminOrganization adminOrg) throws VCloudException, TimeoutException {
		
		OrgVdcNetworkType OrgVdcNetworkParams = new OrgVdcNetworkType();
		OrgVdcNetworkParams.setName(vCloudOrg.getOrgVdcNetwork().getName());
		OrgVdcNetworkParams.setDescription("Org vdc network of type Nat-Routed for Custom");

		// Configure Internal IP Settings
		NetworkConfigurationType netConfig = new NetworkConfigurationType();
		netConfig.setRetainNetInfoAcrossDeployments(true);

		IpScopeType ipScope = new IpScopeType();
		ipScope.setNetmask("255.255.255.0");
		ipScope.setGateway("10.1.1.1");
		ipScope.setIsEnabled(true);
		ipScope.setIsInherited(true);
		ipScope.setDns1("115.178.58.10");
		ipScope.setDns2("115.178.58.26");
		
		// IP Ranges
		IpRangesType ipRangesType = new IpRangesType();
		IpRangeType ipRangeType = new IpRangeType();
		ipRangeType.setStartAddress("10.1.1.11");
		ipRangeType.setEndAddress("10.1.1.254");

		ipRangesType.getIpRange().add(ipRangeType);

		ipScope.setIpRanges(ipRangesType);

		IpScopesType ipScopes = new IpScopesType();
		ipScopes.getIpScope().add(ipScope);
		netConfig.setIpScopes(ipScopes);
		netConfig.setFenceMode(FenceModeValuesType.NATROUTED.value());
		 
		System.out.println("External Network: " + vCloudOrg.getCloudResources().getExternalNetwork().getName());
		ReferenceType externalNetRef = getExternalNetworkRef(vCloudOrg.getCloudResources().getExternalNetwork().getName());

		System.out.println("Creating EdgeGateway: " + vCloudOrg.getEdgeGateway().getGatewayParams().getName());
		GatewayType gateway = createEdgeGatewayParams(externalNetRef,
				vCloudOrg.getEdgeGateway().getGatewayParams().getName());

		edgeGateway = adminVdc.createEdgeGateway(gateway);
		Task createTask = returnTask(edgeGateway);
		if (createTask != null)
			createTask.waitForTask(0);
		System.out.println("Edge Gateway Created");
		System.out.println("	Edge Gateway:	" + edgeGateway.getResource().getName());


		OrgVdcNetworkParams.setEdgeGateway(edgeGateway.getReference());
		OrgVdcNetworkParams.setConfiguration(netConfig);
		System.out.println("Creating Nat-Routed Org vDC Network");
		
		try {
			AdminOrgVdcNetwork orgVdcNet = adminVdc.createOrgVdcNetwork(OrgVdcNetworkParams);
			
			if (orgVdcNet.getTasks().size() > 0) {
				orgVdcNet.getTasks().get(0).waitForTask(0);
			}
			
			System.out.println("	Nat-Routed Org vDC Network : " + orgVdcNet.getResource().getName() + " created - "
					+ orgVdcNet.getResource().getHref());
		} catch (VCloudException e) {
			System.out.println("FAILED: creating org vdc network - " + e.getLocalizedMessage());
		}
		 
	}





	/**
	 * Check for tasks if any
	 *
	 * @param edgeGateway
	 *            {@link EdgeGateway}
	 * @return {@link Task}
	 * @throws VCloudException
	 */
	public static Task returnTask(EdgeGateway edgeGateway) throws VCloudException {
		TasksInProgressType tasksInProgress = edgeGateway.getResource().getTasks();
		if (tasksInProgress != null)
			for (TaskType task : tasksInProgress.getTask()) {
				return new Task(client, task);
			}
		return null;
	}

	/**
	 * Check for tasks if any
	 * 
	 * @param user
	 * @return {@link Task}
	 * @throws VCloudException
	 */
	public static Task returnTask(User user) throws VCloudException {
		TasksInProgressType tasksInProgress = user.getResource().getTasks();
		if (tasksInProgress != null)
			for (TaskType task : tasksInProgress.getTask()) {
				return new Task(client, task);
			}
		return null;
	}

	/**
	 * Creates a new admin org type object
	 * 
	 * @throws VCloudException
	 * 
	 */
	private static AdminOrgType createNewAdminOrgType() throws VCloudException {

		/*
		 * SmtpServerSettingsType smtpServerSettings = new
		 * SmtpServerSettingsType(); smtpServerSettings.setHost("custom");
		 * smtpServerSettings.setIsUseAuthentication(true);
		 * smtpServerSettings.setPassword("custom");
		 * smtpServerSettings.setUsername("custom");
		 * 
		 * OrgEmailSettingsType orgEmailSettings = new OrgEmailSettingsType();
		 * orgEmailSettings.setIsDefaultOrgEmail(true);
		 * orgEmailSettings.setIsDefaultSmtpServer(true);
		 * orgEmailSettings.setFromEmailAddress("custom@custom.com");
		 * orgEmailSettings.setDefaultSubjectPrefix("");
		 * orgEmailSettings.setSmtpServerSettings(smtpServerSettings);
		 */

		OrgLeaseSettingsType orgLeaseSettings = new OrgLeaseSettingsType();
		orgLeaseSettings.setDeleteOnStorageLeaseExpiration(
				vCloudOrg.getOrgSettings().getOrgLeaseSettings().isDeleteOnStorageLeaseExpiration());
		orgLeaseSettings
				.setDeploymentLeaseSeconds(vCloudOrg.getOrgSettings().getOrgLeaseSettings().getDeploymentLeaseSeconds());
		orgLeaseSettings.setStorageLeaseSeconds(vCloudOrg.getOrgSettings().getOrgLeaseSettings().getStorageLeaseSeconds());

		OrgGeneralSettingsType orgGeneralSettings = new OrgGeneralSettingsType();
		orgGeneralSettings.setStoredVmQuota(vCloudOrg.getOrgSettings().getOrgGeneralSettings().getStoredVmQuota());
		orgGeneralSettings.setDeployedVMQuota(vCloudOrg.getOrgSettings().getOrgGeneralSettings().getDeployedVMQuota());
		orgGeneralSettings.setCanPublishCatalogs(vCloudOrg.getOrgSettings().getOrgGeneralSettings().isCanPublishCatalogs());

		OrgVAppTemplateLeaseSettingsType orgVAppTemplateLeaseSettings = new OrgVAppTemplateLeaseSettingsType();
		orgVAppTemplateLeaseSettings.setDeleteOnStorageLeaseExpiration(
				vCloudOrg.getOrgSettings().getOrgVAppTemplateLeaseSettings().isDeleteOnStorageLeaseExpiration());
		orgVAppTemplateLeaseSettings.setStorageLeaseSeconds(
				vCloudOrg.getOrgSettings().getOrgVAppTemplateLeaseSettings().getStorageLeaseSeconds());

		OrgPasswordPolicySettingsType orgPasswordPolicySettings = new OrgPasswordPolicySettingsType();
		orgPasswordPolicySettings.setAccountLockoutEnabled(
				vCloudOrg.getOrgSettings().getOrgPasswordPolicySettings().isAccountLockoutEnabled());
		orgPasswordPolicySettings.setAccountLockoutIntervalMinutes(
				vCloudOrg.getOrgSettings().getOrgPasswordPolicySettings().getAccountLockoutIntervalMinutes());
		orgPasswordPolicySettings.setInvalidLoginsBeforeLockout(
				vCloudOrg.getOrgSettings().getOrgPasswordPolicySettings().getInvalidLoginsBeforeLockout());

		OrgSettingsType orgSettings = new OrgSettingsType();
		orgSettings.setOrgGeneralSettings(orgGeneralSettings);
		orgSettings.setVAppLeaseSettings(orgLeaseSettings);
		// orgSettings.setOrgEmailSettings(orgEmailSettings);
		orgSettings.setVAppTemplateLeaseSettings(orgVAppTemplateLeaseSettings);
		orgSettings.setOrgPasswordPolicySettings(orgPasswordPolicySettings);

		AdminOrgType adminOrgType = new AdminOrgType();
		adminOrgType.setName(vCloudOrg.getName());
		adminOrgType.setDescription(vCloudOrg.getDescription());
		adminOrgType.setFullName(vCloudOrg.getFullName());
		adminOrgType.setSettings(orgSettings);
		adminOrgType.setIsEnabled(vCloudOrg.isEnabled());

		return adminOrgType;
	}

	/**
	 * Check for tasks if any
	 * 
	 * @param adminOrg
	 * @return {@link Task}
	 * @throws VCloudException
	 */
	public static Task returnTask(AdminOrganization adminOrg) throws VCloudException {
		TasksInProgressType tasksInProgress = adminOrg.getResource().getTasks();
		if (tasksInProgress != null)
			for (TaskType task : tasksInProgress.getTask()) {
				return new Task(client, task);
			}
		return null;
	}

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
				formatter.printHelp("vcdpush", options); // automatically
															// generate the help
															// statement
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
				
				System.out.println("Add New Organization");
				AdminOrganization adminOrg = admin.createAdminOrg(createNewAdminOrgType());
				Task task = returnTask(adminOrg);
				if (task != null)
					task.waitForTask(0);
				System.out.println("	" + adminOrg.getResource().getName());
				System.out.println("	" + adminOrg.getResource().getHref() + "\n");

				// Create vDC You may end using one of the following.
				adminVdc = VcdUtils.addPayAsYouGoVdc(vCloudOrg, admin, client, adminOrg);

				// Create user on the organization
				UserUtils.addUserToOrg(vCloudOrg, admin, adminOrg);

				// Create org vdc networks on the organizaiton
				addNatRoutedOrgVdcNetwork(adminOrg);
			
				// find the vdc ref
				Vdc vdc = findVdc(vCloudOrg.getName(), vCloudOrg.getVdc().getVdcParams().getName());

				// find the vapp template ref
				ReferenceType vappTemplateRef = findVappTemplateRef(vCloudOrg.getName(), vCloudOrg.getVdc().getVdcParams().getName(), vCloudOrg.getvApp().getTemplateType()); 

				// Composed vApp. 				
				Vapp vapp = vdc.composeVapp(createComposeParams(vappTemplateRef, vdc));
				System.out.println("Composing vApp : " + vapp.getResource().getName());
				List<Task> tasks = vapp.getTasks();
				if (tasks.size() > 0)
					tasks.get(0).waitForTask(0);

				// fetch the instantiated vapp
/*				vapp = Vapp.getVappByReference(client, vapp.getReference());
				
				NetworkConfigSectionType networkConfigSection = vapp.getNetworkConfigSection();
				List<VAppNetworkConfigurationType> networkConfigs = networkConfigSection.getNetworkConfig();
				
				for(int i =0; i < networkConfigs.size(); i++){
					networkConfigs.get(i).getConfiguration().setFenceMode(FenceModeValuesType.BRIDGED.value());
				}
				
				vapp.updateSection(networkConfigSection).waitForTask(0);*/
	
				

				// change the guest customization settings of the vm inside the vapp.
				// for simplicity purposes guest customization is disabled. you can
				// enable it and set the parameters accordingly.
				VM vm1 = vapp.getChildrenVms().get(0);
				
				System.out.println("Setting the guest customization settings of the vm");
				System.out.println("--------------------------------------------------");
				GuestCustomizationSectionType guestCustomizationSection = vm1.getGuestCustomizationSection();
				
				guestCustomizationSection.setEnabled(false);
				vm1.updateSection(guestCustomizationSection).waitForTask(0);
				
				VirtualHardwareSectionType virtualHardwareSection = vm1.getVirtualHardwareSection();
				List <RASDType> rasdTypes = virtualHardwareSection.getItem();
				
				NetworkConnectionSectionType networkConnectionSection = vm1.getNetworkConnectionSection();
				NetworkConnectionType networkConnection = networkConnectionSection.getNetworkConnection().get(0);

				
				/*
				
				networkConfigSectionType.getNetworkConfig().get(0).getConfiguration().setFenceMode(FenceModeValuesType.NATROUTED.value());			
				System.out.println("Updating the NetworkConfigSection using Fence mode: NATROUTED for " + vapp.getResource().getName());
				System.out.println("--------------------");
				vapp.updateSection(networkConfigSectionType).waitForTask(0);

				networkConfigSectionType.getNetworkConfig().get(0).getConfiguration().setFenceMode(FenceModeValuesType.BRIDGED.value());			
				System.out.println("Updating the NetworkConfigSection using Fence mode: BRIDGED for " + vapp.getResource().getName());
				System.out.println("--------------------");
				vapp.updateSection(networkConfigSectionType).waitForTask(0);*/
				/*
				 * System.out.println("Update Organization to Disabled");
				 * adminOrg.getResource().setIsEnabled(false);
				 * adminOrg.updateAdminOrg(adminOrg.getResource()); task =
				 * returnTask(adminOrg); if (task != null) task.waitForTask(0);
				 * System.out.println("	Updated\n");
				 * 
				 * System.out.println("Delete Organization"); adminOrg.delete();
				 * System.out.println("	Deleted\n");
				 */
				
				System.out.println("---------- Completed! ----------");
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			System.err.println("Parsing failed.  Reason: " + e.getMessage());
		}

	}

}

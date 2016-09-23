package com.vmware.vcloud.automate;

import java.util.List;
import java.util.concurrent.TimeoutException;

import javax.xml.bind.JAXBElement;

import com.vmware.vcloud.api.rest.schema.FirewallRuleProtocols;
import com.vmware.vcloud.api.rest.schema.FirewallRuleType;
import com.vmware.vcloud.api.rest.schema.FirewallServiceType;
import com.vmware.vcloud.api.rest.schema.GatewayConfigurationType;
import com.vmware.vcloud.api.rest.schema.GatewayFeaturesType;
import com.vmware.vcloud.api.rest.schema.GatewayInterfaceType;
import com.vmware.vcloud.api.rest.schema.GatewayInterfacesType;
import com.vmware.vcloud.api.rest.schema.GatewayNatRuleType;
import com.vmware.vcloud.api.rest.schema.GatewayType;
import com.vmware.vcloud.api.rest.schema.IpRangeType;
import com.vmware.vcloud.api.rest.schema.IpRangesType;
import com.vmware.vcloud.api.rest.schema.IpScopeType;
import com.vmware.vcloud.api.rest.schema.IpScopesType;
import com.vmware.vcloud.api.rest.schema.NatRuleType;
import com.vmware.vcloud.api.rest.schema.NatServiceType;
import com.vmware.vcloud.api.rest.schema.NetworkConfigurationType;
import com.vmware.vcloud.api.rest.schema.NetworkServiceType;
import com.vmware.vcloud.api.rest.schema.ObjectFactory;
import com.vmware.vcloud.api.rest.schema.OrgVdcNetworkType;
import com.vmware.vcloud.api.rest.schema.ReferenceType;
import com.vmware.vcloud.api.rest.schema.SubnetParticipationType;
import com.vmware.vcloud.api.rest.schema.TaskType;
import com.vmware.vcloud.api.rest.schema.TasksInProgressType;
import com.vmware.vcloud.model.VCloudOrganization;
import com.vmware.vcloud.sdk.Task;
import com.vmware.vcloud.sdk.VCloudException;
import com.vmware.vcloud.sdk.VcloudClient;
import com.vmware.vcloud.sdk.admin.AdminOrgVdcNetwork;
import com.vmware.vcloud.sdk.admin.AdminOrganization;
import com.vmware.vcloud.sdk.admin.AdminVdc;
import com.vmware.vcloud.sdk.admin.EdgeGateway;
import com.vmware.vcloud.sdk.admin.ExternalNetwork;
import com.vmware.vcloud.sdk.constants.FenceModeValuesType;
import com.vmware.vcloud.sdk.constants.FirewallPolicyType;
import com.vmware.vcloud.sdk.constants.GatewayBackingConfigValuesType;
import com.vmware.vcloud.sdk.constants.GatewayEnums;

public class NetworkUtils {

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
	static GatewayType createEdgeGatewayParams(VcloudClient client, VCloudOrganization vCloudOrg, ReferenceType externalNetwork, String edgeGatewayName)
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
		
		GatewayFeaturesType gatewayFeatures = new GatewayFeaturesType();


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
        
		ObjectFactory objectFactory = new ObjectFactory();
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
	 * Adding nat routed org vdc network to the organization
	 * 
	 * @param adminOrg
	 *            {@link AdminOrganization}
	 * @throws VCloudException
	 * @throws TimeoutException
	 */
	static void addNatRoutedOrgVdcNetwork(VcloudClient client, VCloudOrganization vCloudOrg, EdgeGateway edgeGateway, AdminVdc adminVdc, AdminOrganization adminOrg) throws VCloudException, TimeoutException {
		
		OrgVdcNetworkType OrgVdcNetworkParams = new OrgVdcNetworkType();
		OrgVdcNetworkParams.setName(vCloudOrg.getOrgVdcNetwork().getName());
		OrgVdcNetworkParams.setDescription(vCloudOrg.getOrgVdcNetwork().getDescription());

		// Configure Internal IP Settings
		NetworkConfigurationType netConfig = new NetworkConfigurationType();
		netConfig.setRetainNetInfoAcrossDeployments(true);
		IpScopesType ipScopes = new IpScopesType();
		
		for (int i = 0; i < vCloudOrg.getOrgVdcNetwork().getConfiguration().getIpScopes().size(); i++) {
			IpScopeType ipScope = new IpScopeType();
			ipScope.setNetmask(vCloudOrg.getOrgVdcNetwork().getConfiguration().getIpScopes().get(i).getNetmask());
			ipScope.setGateway(vCloudOrg.getOrgVdcNetwork().getConfiguration().getIpScopes().get(i).getGateway());
			ipScope.setIsEnabled(true);
			ipScope.setIsInherited(true);
			ipScope.setDns1(vCloudOrg.getOrgVdcNetwork().getConfiguration().getIpScopes().get(i).getDns1());
			ipScope.setDns2(vCloudOrg.getOrgVdcNetwork().getConfiguration().getIpScopes().get(i).getDns2());

			// IP Ranges
			IpRangesType ipRangesType = new IpRangesType();
			IpRangeType ipRangeType = new IpRangeType();
			ipRangeType.setStartAddress(vCloudOrg.getOrgVdcNetwork().getConfiguration().getIpScopes().get(i).getIpRange().getStartAddress());
			ipRangeType.setEndAddress(vCloudOrg.getOrgVdcNetwork().getConfiguration().getIpScopes().get(i).getIpRange().getEndAddress());

			ipRangesType.getIpRange().add(ipRangeType);
			ipScope.setIpRanges(ipRangesType);
			ipScopes.getIpScope().add(ipScope);
		}
		
		netConfig.setIpScopes(ipScopes);
		
		if(vCloudOrg.getOrgVdcNetwork().getConfiguration().getFenceMode().name().equalsIgnoreCase("NATROUTED"))
			netConfig.setFenceMode(FenceModeValuesType.NATROUTED.value()); 
		else if(vCloudOrg.getOrgVdcNetwork().getConfiguration().getFenceMode().name().equalsIgnoreCase("BRIDGED"))
			netConfig.setFenceMode(FenceModeValuesType.BRIDGED.value()); 
		
			
		ReferenceType externalNetRef = NetworkUtils.getExternalNetworkRef(client, vCloudOrg.getCloudResources().getExternalNetwork().getName());
		
		System.out.println("External Network: " + vCloudOrg.getCloudResources().getExternalNetwork().getName() + " : " + externalNetRef + "\n");
		
		System.out.println("Creating EdgeGateway: " + vCloudOrg.getEdgeGateway().getGatewayParams().getName());
		GatewayType gateway = NetworkUtils.createEdgeGatewayParams(client, vCloudOrg, externalNetRef, vCloudOrg.getEdgeGateway().getGatewayParams().getName());

		edgeGateway = adminVdc.createEdgeGateway(gateway);
		Task createTask = returnTask(client, edgeGateway);
		if (createTask != null)
			createTask.waitForTask(0);
		System.out.println("	Edge Gateway: " + edgeGateway.getResource().getName() +" created - " + edgeGateway.getReference().getHref() + "\n");

		OrgVdcNetworkParams.setEdgeGateway(edgeGateway.getReference());
		OrgVdcNetworkParams.setConfiguration(netConfig);
		
		System.out.println("Creating Nat-Routed Org vDC Network");
		
		try {
			AdminOrgVdcNetwork orgVdcNet = adminVdc.createOrgVdcNetwork(OrgVdcNetworkParams);
			
			if (orgVdcNet.getTasks().size() > 0) {
				orgVdcNet.getTasks().get(0).waitForTask(0);
			}
			
			System.out.println("	Nat-Routed Org vDC Network : " + orgVdcNet.getResource().getName() + " created - "
					+ orgVdcNet.getResource().getHref() + "\n");
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
	public static Task returnTask(VcloudClient client, EdgeGateway edgeGateway) throws VCloudException {
		TasksInProgressType tasksInProgress = edgeGateway.getResource().getTasks();
		if (tasksInProgress != null)
			for (TaskType task : tasksInProgress.getTask()) {
				return new Task(client, task);
			}
		return null;
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
	static ReferenceType getExternalNetworkRef(VcloudClient client, String networkName) throws VCloudException {
		return client.getVcloudAdmin().getExternalNetworkRefByName(networkName);
	}
	
}

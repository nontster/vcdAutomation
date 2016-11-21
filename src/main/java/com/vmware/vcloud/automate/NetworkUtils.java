package com.vmware.vcloud.automate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import com.vmware.vcloud.api.rest.schema.SubAllocationType;
import com.vmware.vcloud.api.rest.schema.SubAllocationsType;
import com.vmware.vcloud.api.rest.schema.SubnetParticipationType;
import com.vmware.vcloud.api.rest.schema.TaskType;
import com.vmware.vcloud.api.rest.schema.TasksInProgressType;
import com.vmware.vcloud.exception.ExternalNetworkNotFoundException;
import com.vmware.vcloud.exception.InsufficientIPAddressesException;
import com.vmware.vcloud.model.FirewallRule;
import com.vmware.vcloud.model.FirewallService;
import com.vmware.vcloud.model.GatewayInterface;
import com.vmware.vcloud.model.IpRange;
import com.vmware.vcloud.model.OrderType;
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

	/**
	 * Convert an IP address to a hex string
	 *
	 * @param ipAddress
	 *            Input IP address
	 *
	 * @return The IP address in hex form
	 */
	public static String toHex(String ipAddress) {
		return Long.toHexString(ipToLong(ipAddress));
	}

	/**
	 * Convert an IP address to a number
	 *
	 * @param ipAddress
	 *            Input IP address
	 *
	 * @return The IP address as a number
	 */
	static Long ipToLong(String ipAddress) {
		long result = 0;
		String[] atoms = ipAddress.split("\\.");

		for (int i = 3; i >= 0; i--) {
			result |= (Long.parseLong(atoms[3 - i]) << (i * 8));
		}

		return result & 0xFFFFFFFF;
	}

	public static String longToIp(long ip) {
		StringBuilder sb = new StringBuilder(15);

		for (int i = 0; i < 4; i++) {
			sb.insert(0, Long.toString(ip & 0xff));

			if (i < 3) {
				sb.insert(0, '.');
			}

			ip >>= 8;
		}

		return sb.toString();
	}	
	
	 
	static List<String> getAvailableAddresses(VcloudClient client, ReferenceType externalNetRef, int numIP)
			throws VCloudException, InsufficientIPAddressesException {

		Comparator<String> ipComparator = new Comparator<String>() {
			@Override
			public int compare(String ip1, String ip2) {
				return ipToLong(ip1).compareTo(ipToLong(ip2));
			}
		};

		// IPs already allocated
		ArrayList<String> ips = new ArrayList<String>();
		// All IPs in scope
		ArrayList<String> ipsAll = new ArrayList<String>();

		ExternalNetwork externalNet = ExternalNetwork.getExternalNetworkByReference(client, externalNetRef);

		IpScopesType externalNetIpScopes = externalNet.getResource().getConfiguration().getIpScopes();

		for (IpScopeType ipScope : externalNetIpScopes.getIpScope()) {

			for (IpRangeType ipRangeType : ipScope.getIpRanges().getIpRange()) {

				long start = ipToLong(ipRangeType.getStartAddress());
				long end = ipToLong(ipRangeType.getEndAddress());

				for (long i = start; i <= end; i++) {
					ipsAll.add(longToIp(i));
				}
			}

			SubAllocationsType subAllocationsType = ipScope.getSubAllocations();

			for (SubAllocationType subAllocationType : subAllocationsType.getSubAllocation()) {
				for (IpRangeType ipRangeType1 : subAllocationType.getIpRanges().getIpRange()) {

					long start = ipToLong(ipRangeType1.getStartAddress());
					long end = ipToLong(ipRangeType1.getEndAddress());

					for (long i = start; i <= end; i++) {
						ips.add(longToIp(i));
					}
				}
			}

			for (String ipAddress : ipScope.getAllocatedIpAddresses().getIpAddress()) {
				ips.add(ipAddress);
			}

			Collections.sort(ips, ipComparator);
			Collections.sort(ipsAll, ipComparator);

			ipsAll.removeAll(ips);
		}

		if (ipsAll.size() < numIP) {
			StringBuffer errMsg = new StringBuffer();
			errMsg.append("You've requested ").append(numIP).append(" IP addresses but ").append(ipsAll.size())
					.append(" available");
			throw new InsufficientIPAddressesException(errMsg.toString());
		}

		int startIdx = 0;

		// validate array index
		if (ipsAll.size() - numIP < 0)
			startIdx = 0;
		else
			startIdx = ipsAll.size() - numIP;

		return ipsAll.subList(startIdx, ipsAll.size());
	}
	
	private static void addNatRule(GatewayInterfaceType gatewayInterface, NatServiceType natService, String pubIP, String privIP) {
		// Edge Gateway NAT service configuration

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
		dgatewayNat.setOriginalIp(pubIP);
		dgatewayNat.setOriginalPort("any");
		dgatewayNat.setTranslatedIp(privIP);

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
		sgatewayNat.setOriginalIp(privIP);
		sgatewayNat.setOriginalPort("any");
		sgatewayNat.setTranslatedIp(pubIP);

		snatRule.setGatewayNatRule(sgatewayNat);
		natService.getNatRule().add(snatRule);
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
	 * @throws InsufficientIPAddressesException 
	 */
	static GatewayType createEdgeGatewayParams(VcloudClient client, VCloudOrganization vCloudOrg, ReferenceType externalNetwork)
			throws VCloudException, InsufficientIPAddressesException {
		ExternalNetwork externalNet = ExternalNetwork.getExternalNetworkByReference(client, externalNetwork);
		IpScopeType externalNetIpScope = externalNet.getResource().getConfiguration().getIpScopes().getIpScope().get(0);

		// Create edge gateway
		StringBuffer edgeGatewayName = new StringBuffer();
		
		if(vCloudOrg.getEdgeGateway() != null && vCloudOrg.getEdgeGateway().getName() != null)
			edgeGatewayName.append(vCloudOrg.getEdgeGateway().getName());
		else{
			if(vCloudOrg.getOrderType() == OrderType.TRIAL)
				edgeGatewayName.append("Trial-");
			
			edgeGatewayName.append(vCloudOrg.getShortName());
			edgeGatewayName.append("-edgegw-01");
		}
			
		System.out.println("Creating EdgeGateway: " + edgeGatewayName.toString());
		
	
		GatewayType gatewayParams = new GatewayType();
		
		// Set edge gateway name
		gatewayParams.setName(edgeGatewayName.toString());
		
		// Set edge gateway description
		if (vCloudOrg.getEdgeGateway() != null && vCloudOrg.getEdgeGateway().getDescription() != null)
			gatewayParams.setDescription(vCloudOrg.getEdgeGateway().getDescription());
		else
			gatewayParams.setDescription("Edge gateway for " + vCloudOrg.getFullName());
		
		GatewayConfigurationType gatewayConfig = new GatewayConfigurationType();
		
		// Set Gateway backing config
		if (vCloudOrg.getEdgeGateway() != null 
				&& vCloudOrg.getEdgeGateway().getGatewayConfiguration() != null
				&& vCloudOrg.getEdgeGateway().getGatewayConfiguration().getGatewayBackingConfig() != null
				&& vCloudOrg.getEdgeGateway().getGatewayConfiguration().getGatewayBackingConfig().name() != null){
			
			if (vCloudOrg.getEdgeGateway().getGatewayConfiguration().getGatewayBackingConfig().name().equalsIgnoreCase("COMPACT"))
				gatewayConfig.setGatewayBackingConfig(GatewayBackingConfigValuesType.COMPACT.value());
			else if (vCloudOrg.getEdgeGateway().getGatewayConfiguration().getGatewayBackingConfig().name().equalsIgnoreCase("FULL") 
					|| vCloudOrg.getEdgeGateway().getGatewayConfiguration().getGatewayBackingConfig().name().equalsIgnoreCase("FULL4"))
				gatewayConfig.setGatewayBackingConfig(GatewayBackingConfigValuesType.FULL.value());		
		} else {
			gatewayConfig.setGatewayBackingConfig(GatewayBackingConfigValuesType.COMPACT.value());
		}
		
		// Set isHaEnable
		if (vCloudOrg.getEdgeGateway() != null 
				&& vCloudOrg.getEdgeGateway().getGatewayConfiguration() != null
				&& vCloudOrg.getEdgeGateway().getGatewayConfiguration().isHaEnabled() != null)
			gatewayConfig.setHaEnabled(vCloudOrg.getEdgeGateway().getGatewayConfiguration().isHaEnabled());
		else
			gatewayConfig.setHaEnabled(Boolean.FALSE);

		// Set useDefaultRouteForDnsRelay
		if(vCloudOrg.getEdgeGateway()!= null 
				&& vCloudOrg.getEdgeGateway().getGatewayConfiguration() != null 
				&& vCloudOrg.getEdgeGateway().getGatewayConfiguration().isUseDefaultRouteForDnsRelay() != null)
			gatewayConfig.setUseDefaultRouteForDnsRelay(vCloudOrg.getEdgeGateway().getGatewayConfiguration().isUseDefaultRouteForDnsRelay());
		else
			gatewayConfig.setUseDefaultRouteForDnsRelay(Boolean.FALSE);
		
		// Set gateway interface
		GatewayInterfacesType interfaces = new GatewayInterfacesType();
		GatewayFeaturesType gatewayFeatures = new GatewayFeaturesType();
		ObjectFactory objectFactory = new ObjectFactory();
		
		if (vCloudOrg.getEdgeGateway() != null 
				&& vCloudOrg.getEdgeGateway().getGatewayConfiguration() != null
				&& vCloudOrg.getEdgeGateway().getGatewayConfiguration().getGatewayInterfaces() != null) {

			for (GatewayInterface gwInterface : vCloudOrg.getEdgeGateway().getGatewayConfiguration().getGatewayInterfaces()) {
				
				GatewayInterfaceType gatewayInterface = new GatewayInterfaceType();
				
				gatewayInterface.setDisplayName(gwInterface.getDisplayName());
				gatewayInterface.setNetwork(externalNetwork);
				
				if (gwInterface.getInterfaceType().name().equalsIgnoreCase("UPLINK"))
					gatewayInterface.setInterfaceType(GatewayEnums.UPLINK.value());
				else if (gwInterface.getInterfaceType().name().equalsIgnoreCase("INTERNAL"))
					gatewayInterface.setInterfaceType(GatewayEnums.INTERNAL.value());

				SubnetParticipationType subnetParticipationType = new SubnetParticipationType();
				
				if (gwInterface.getSubnetParticipation() != null
						&& gwInterface.getSubnetParticipation().getGateway() != null)
					subnetParticipationType.setGateway(gwInterface.getSubnetParticipation().getGateway());

				if (gwInterface.getSubnetParticipation() != null
						&& gwInterface.getSubnetParticipation().getNetmask() != null)
					subnetParticipationType.setNetmask(gwInterface.getSubnetParticipation().getNetmask());

				// Set IP Ranges
				IpRangesType ipRangesType = new IpRangesType();
				
				NatServiceType natService = new NatServiceType();
				//natService.setExternalIp(pubIP);
				
				for(IpRange ipRange : gwInterface.getSubnetParticipation().getIpRanges()){
					String startAddress = ipRange.getStartAddress();
					String endAddress = ipRange.getEndAddress();
					
					IpRangeType ipRangeType = new IpRangeType();
					
					// Specify IP range
					ipRangeType.setStartAddress(startAddress);
					ipRangeType.setEndAddress(endAddress);
					
					ipRangesType.getIpRange().add(ipRangeType);
					
					
					long pubStart = ipToLong(ipRangeType.getStartAddress());
					long pubEnd = ipToLong(ipRangeType.getEndAddress());
					
					long priStart = ipToLong("10.1.1.11");

					if(vCloudOrg.getOrgVdcNetwork() != null 
							&& vCloudOrg.getOrgVdcNetwork().getConfiguration() != null
							&& vCloudOrg.getOrgVdcNetwork().getConfiguration().getIpScopes() != null 
							&& vCloudOrg.getOrgVdcNetwork().getConfiguration().getIpScopes().get(0) != null 
							&& vCloudOrg.getOrgVdcNetwork().getConfiguration().getIpScopes().get(0).getIpRange() != null 
							&& vCloudOrg.getOrgVdcNetwork().getConfiguration().getIpScopes().get(0).getIpRange().getStartAddress() != null)
							priStart = ipToLong(vCloudOrg.getOrgVdcNetwork().getConfiguration().getIpScopes().get(0).getIpRange().getStartAddress());
							
					for (long i = pubStart; i <= pubEnd; i++) {

						addNatRule(gatewayInterface, natService, longToIp(i), longToIp(priStart));
						priStart = priStart + 1;
						
						JAXBElement<NetworkServiceType> nat = objectFactory.createNetworkService(natService);
						gatewayFeatures.getNetworkService().add(nat);
					}				
				}
								
				subnetParticipationType.setIpRanges(ipRangesType);
				gatewayInterface.getSubnetParticipation().add(subnetParticipationType);

				// Is use for default route
				gatewayInterface.setUseForDefaultRoute(gwInterface.isUseForDefaultRoute());
				
				// Add gateway interface
				interfaces.getGatewayInterface().add(gatewayInterface);
			}
			
		} else {
			
			GatewayInterfaceType gatewayInterface = new GatewayInterfaceType();

			gatewayInterface.setDisplayName("Gateway interface for " + vCloudOrg.getFullName());
			gatewayInterface.setNetwork(externalNetwork);
			gatewayInterface.setInterfaceType(GatewayEnums.UPLINK.value());

			SubnetParticipationType subnetParticipationType = new SubnetParticipationType();

			// Use gateway/netmask from external network
			subnetParticipationType.setGateway(externalNetIpScope.getGateway());
			subnetParticipationType.setNetmask(externalNetIpScope.getNetmask());

			IpRangesType ipRanges = new IpRangesType();

			int numVM = vCloudOrg.getvApp().getChildVms().size();

			List<String> ips = getAvailableAddresses(client, externalNetwork, numVM);
			long priStart = ipToLong("10.1.1.11");

			if(vCloudOrg.getOrgVdcNetwork() != null 
					&& vCloudOrg.getOrgVdcNetwork().getConfiguration() != null
					&& vCloudOrg.getOrgVdcNetwork().getConfiguration().getIpScopes() != null 
					&& vCloudOrg.getOrgVdcNetwork().getConfiguration().getIpScopes().get(0) != null 
					&& vCloudOrg.getOrgVdcNetwork().getConfiguration().getIpScopes().get(0).getIpRange() != null 
					&& vCloudOrg.getOrgVdcNetwork().getConfiguration().getIpScopes().get(0).getIpRange().getStartAddress() != null)
					priStart = ipToLong(vCloudOrg.getOrgVdcNetwork().getConfiguration().getIpScopes().get(0).getIpRange().getStartAddress());
			
			NatServiceType natService = new NatServiceType();
			//natService.setExternalIp(pubIP);
			
			for (String ipAddr : ips) {

				IpRangeType ipRange = new IpRangeType();

				// Specify IP range
				ipRange.setStartAddress(ipAddr);
				ipRange.setEndAddress(ipAddr);

				ipRanges.getIpRange().add(ipRange);

				addNatRule(gatewayInterface, natService, ipAddr, longToIp(priStart));
				priStart = priStart + 1;

				
				JAXBElement<NetworkServiceType> nat = objectFactory.createNetworkService(natService);
				gatewayFeatures.getNetworkService().add(nat);
			}

			subnetParticipationType.setIpRanges(ipRanges);
			gatewayInterface.getSubnetParticipation().add(subnetParticipationType);

			// Is use for default route
			gatewayInterface.setUseForDefaultRoute(Boolean.TRUE);

			interfaces.getGatewayInterface().add(gatewayInterface);
		}
		
				
		gatewayConfig.setGatewayInterfaces(interfaces);
		
		// Edge Gateway Firewall service configuration
		FirewallServiceType firewallService = new FirewallServiceType(); 
		List<FirewallRuleType> fwRules = firewallService.getFirewallRule();
		FirewallService fs = null;
		
		if (vCloudOrg.getEdgeGateway() != null 
				&& vCloudOrg.getEdgeGateway().getGatewayFeatures() != null
				&& vCloudOrg.getEdgeGateway().getGatewayFeatures().getFirewallService() != null) {
			
			fs = vCloudOrg.getEdgeGateway().getGatewayFeatures().getFirewallService();

			if (fs.isEnabled() != null)
				firewallService.setIsEnabled(fs.isEnabled());
			else
				firewallService.setIsEnabled(Boolean.TRUE);

			if (fs.getDefaultAction() != null 
					&& fs.getDefaultAction().name() != null 
					&& fs.getDefaultAction().name().equalsIgnoreCase("DROP"))
				firewallService.setDefaultAction(FirewallPolicyType.DROP.value());
			else if (fs.getDefaultAction() != null 
					&& fs.getDefaultAction().name() != null 
					&& fs.getDefaultAction().name().equalsIgnoreCase("ALLOW"))
				firewallService.setDefaultAction(FirewallPolicyType.ALLOW.value());
			else
				firewallService.setDefaultAction(FirewallPolicyType.DROP.value());

			if (fs.isLogDefaultAction() != null)
				firewallService.setLogDefaultAction(fs.isLogDefaultAction());
			else
				firewallService.setLogDefaultAction(Boolean.FALSE);

			for (FirewallRule fr : fs.getFirewallRules()) {
				addFirewallRule(fwRules, fr.getDescription(), fr.getProtocol().name(), fr.getSourceIp(), fr.getDestIp(), fr.getDestPort());
			}
		} else {

			addFirewallRule(fwRules, "PING OUT", "ICMP", "10.1.1.0/24", "Any", "Any");
			addFirewallRule(fwRules, "DNS OUT", "UDP", "10.1.1.0/24", "Any", "53");
			addFirewallRule(fwRules, "NTP OUT", "UDP", "10.1.1.0/24", "Any", "123");
			addFirewallRule(fwRules, "HTTP OUT", "TCP", "10.1.1.0/24", "Any", "80");
			addFirewallRule(fwRules, "HTTP OUT", "TCP", "10.1.1.0/24", "Any", "80");
			addFirewallRule(fwRules, "HTTPS OUT", "TCP", "10.1.1.0/24", "Any", "443");
			addFirewallRule(fwRules, "PING IN", "ICMP", "Any", "internal", "Any");
		}
		
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
	 * @throws ExternalNetworkNotFoundException 
	 * @throws InsufficientIPAddressesException 
	 */
	static EdgeGateway addNatRoutedOrgVdcNetwork(VcloudClient client, VCloudOrganization vCloudOrg, AdminVdc adminVdc, AdminOrganization adminOrg) throws VCloudException, TimeoutException, ExternalNetworkNotFoundException, InsufficientIPAddressesException {
		
		OrgVdcNetworkType OrgVdcNetworkParams = new OrgVdcNetworkType();
		EdgeGateway edgeGateway = null;
		
		// If Organization VDC network name defined in template use those one, if omitted use short name combination with fixed string
		if(vCloudOrg.getOrgVdcNetwork() != null && vCloudOrg.getOrgVdcNetwork().getName() != null)
			OrgVdcNetworkParams.setName(vCloudOrg.getOrgVdcNetwork().getName());
		else
			OrgVdcNetworkParams.setName(vCloudOrg.getShortName()+"-orgnet-01");
			
		if (vCloudOrg.getOrgVdcNetwork() != null && vCloudOrg.getOrgVdcNetwork().getDescription() != null)
			OrgVdcNetworkParams.setDescription(vCloudOrg.getOrgVdcNetwork().getDescription());
		else
			OrgVdcNetworkParams.setDescription("Organization network for " + vCloudOrg.getFullName());		
		
		// Configure Internal IP Settings
		NetworkConfigurationType netConfig = new NetworkConfigurationType();
		netConfig.setRetainNetInfoAcrossDeployments(true);
		IpScopesType ipScopes = new IpScopesType();
		
		if (vCloudOrg.getOrgVdcNetwork() != null 
				&& vCloudOrg.getOrgVdcNetwork().getConfiguration() != null
				&& vCloudOrg.getOrgVdcNetwork().getConfiguration().getIpScopes() != null) {
			
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
				ipRangeType.setStartAddress(vCloudOrg.getOrgVdcNetwork().getConfiguration().getIpScopes().get(i)
						.getIpRange().getStartAddress());
				ipRangeType.setEndAddress(vCloudOrg.getOrgVdcNetwork().getConfiguration().getIpScopes().get(i)
						.getIpRange().getEndAddress());

				ipRangesType.getIpRange().add(ipRangeType);
				ipScope.setIpRanges(ipRangesType);
				ipScopes.getIpScope().add(ipScope);
			}
		} else {
			IpScopeType ipScope = new IpScopeType();
			ipScope.setNetmask("255.255.255.0");
			ipScope.setGateway("10.1.1.1");
			ipScope.setIsEnabled(Boolean.TRUE);
			ipScope.setIsInherited(Boolean.TRUE);
			ipScope.setDns1("115.178.58.10");
			ipScope.setDns2("115.178.58.26");

			// IP Ranges
			IpRangesType ipRangesType = new IpRangesType();
			IpRangeType ipRangeType = new IpRangeType();
			ipRangeType.setStartAddress("10.1.1.11");
			ipRangeType.setEndAddress("10.1.1.254");

			ipRangesType.getIpRange().add(ipRangeType);
			ipScope.setIpRanges(ipRangesType);
			ipScopes.getIpScope().add(ipScope);
		}
		
		netConfig.setIpScopes(ipScopes);
		
		// Set Fence Mode, if not specified in template then use NATROUTED
		if (vCloudOrg.getOrgVdcNetwork() != null 
				&& vCloudOrg.getOrgVdcNetwork().getConfiguration() != null
				&& vCloudOrg.getOrgVdcNetwork().getConfiguration().getFenceMode() != null) {
			if (vCloudOrg.getOrgVdcNetwork().getConfiguration().getFenceMode().name().equalsIgnoreCase("NATROUTED"))
				netConfig.setFenceMode(FenceModeValuesType.NATROUTED.value());
			else if (vCloudOrg.getOrgVdcNetwork().getConfiguration().getFenceMode().name().equalsIgnoreCase("BRIDGED"))
				netConfig.setFenceMode(FenceModeValuesType.BRIDGED.value());
		} else {
			netConfig.setFenceMode(FenceModeValuesType.NATROUTED.value());
		}
			
		// FIXME: need the better way to handle external network pool. 
		// This code imply that only one external network pool, Tenant-External-Internet02
		ReferenceType externalNetRef = null;
		String externalNetName = null;
		
		if (vCloudOrg.getCloudResources() != null 
				&& vCloudOrg.getCloudResources().getExternalNetwork() != null
				&& vCloudOrg.getCloudResources().getExternalNetwork().getName() != null)
			externalNetName = vCloudOrg.getCloudResources().getExternalNetwork().getName();
		else
			externalNetName = "Tenant-External-Internet02";
		
		if((externalNetRef = NetworkUtils.getExternalNetworkRef(client, externalNetName)) == null)
			throw new ExternalNetworkNotFoundException("External network: " + externalNetName + " not found");
				
		System.out.println("External Network: " + externalNetName + " : " + externalNetRef.getHref() + "\n");
		
		GatewayType gateway = NetworkUtils.createEdgeGatewayParams(client, vCloudOrg, externalNetRef);

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
		
		return edgeGateway;
		
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

	/**
	 * Gets NatServiceType
	 * 
	 * @param gateway
	 *            {@link EdgeGateway}
	 * @return {@link NatServiceType}
	 */
	static NatServiceType getNatService(EdgeGateway gateway) {
		for (JAXBElement<? extends NetworkServiceType> service : gateway.getResource().getConfiguration()
				.getEdgeGatewayServiceConfiguration().getNetworkService()) {
			if (service.getValue() instanceof NatServiceType) {
				return (NatServiceType) service.getValue();
			}
		}
		return null;
	}
	
	/**
	 * Gets FirewallServiceType
	 * 
	 * @param gateway
	 *            {@link EdgeGateway}
	 * @return {@link FirewallServiceType}
	 */	
	static FirewallServiceType getFirewallService(EdgeGateway gateway) {
		for (JAXBElement<? extends NetworkServiceType> service : gateway.getResource().getConfiguration()
				.getEdgeGatewayServiceConfiguration().getNetworkService()) {
			if (service.getValue() instanceof FirewallServiceType) {
				return (FirewallServiceType) service.getValue();
			}
		}
		return null;
	}
}

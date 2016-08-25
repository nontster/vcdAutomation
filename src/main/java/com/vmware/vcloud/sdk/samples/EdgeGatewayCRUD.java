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
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;

import javax.xml.bind.JAXBElement;

import com.vmware.vcloud.api.rest.schema.DhcpServiceType;
import com.vmware.vcloud.api.rest.schema.FirewallServiceType;
import com.vmware.vcloud.api.rest.schema.GatewayConfigurationType;
import com.vmware.vcloud.api.rest.schema.GatewayFeaturesType;
import com.vmware.vcloud.api.rest.schema.GatewayInterfaceType;
import com.vmware.vcloud.api.rest.schema.GatewayInterfacesType;
import com.vmware.vcloud.api.rest.schema.GatewayType;
import com.vmware.vcloud.api.rest.schema.IpRangeType;
import com.vmware.vcloud.api.rest.schema.IpRangesType;
import com.vmware.vcloud.api.rest.schema.IpScopeType;
import com.vmware.vcloud.api.rest.schema.IpsecVpnServiceType;
import com.vmware.vcloud.api.rest.schema.IpsecVpnTunnelType;
import com.vmware.vcloud.api.rest.schema.LBPersistenceType;
import com.vmware.vcloud.api.rest.schema.LBPoolHealthCheckType;
import com.vmware.vcloud.api.rest.schema.LBPoolMemberType;
import com.vmware.vcloud.api.rest.schema.LBPoolServicePortType;
import com.vmware.vcloud.api.rest.schema.LBVirtualServerServiceProfileType;
import com.vmware.vcloud.api.rest.schema.LoadBalancerPoolType;
import com.vmware.vcloud.api.rest.schema.LoadBalancerServiceType;
import com.vmware.vcloud.api.rest.schema.LoadBalancerVirtualServerType;
import com.vmware.vcloud.api.rest.schema.NatServiceType;
import com.vmware.vcloud.api.rest.schema.NetworkServiceType;
import com.vmware.vcloud.api.rest.schema.ObjectFactory;
import com.vmware.vcloud.api.rest.schema.ReferenceType;
import com.vmware.vcloud.api.rest.schema.StaticRouteType;
import com.vmware.vcloud.api.rest.schema.StaticRoutingServiceType;
import com.vmware.vcloud.api.rest.schema.SubnetParticipationType;
import com.vmware.vcloud.api.rest.schema.TaskType;
import com.vmware.vcloud.api.rest.schema.TasksInProgressType;
import com.vmware.vcloud.sdk.Task;
import com.vmware.vcloud.sdk.VCloudException;
import com.vmware.vcloud.sdk.VcloudClient;
import com.vmware.vcloud.sdk.admin.AdminOrganization;
import com.vmware.vcloud.sdk.admin.AdminVdc;
import com.vmware.vcloud.sdk.admin.EdgeGateway;
import com.vmware.vcloud.sdk.admin.ExternalNetwork;
import com.vmware.vcloud.sdk.constants.FirewallPolicyType;
import com.vmware.vcloud.sdk.constants.GatewayBackingConfigValuesType;
import com.vmware.vcloud.sdk.constants.GatewayEnums;
import com.vmware.vcloud.sdk.constants.Version;

/**
 * Creating, Updating, Getting and Deleting Edge Gateway
 *
 * Should be System Administrator. Should have Admin VDC. Should have External
 * Network.
 *
 * @author Ecosystem Engineering
 * 
 */

public class EdgeGatewayCRUD {

   private static VcloudClient client;

   /**
    * EdgeGateway Usage
    */
   public static void usage() {
      System.out
            .println("java EdgeGatewayCRUD Vcloud user@organization password OrgName AdminVdcName ExternalNetworkName EdgeGatewayName CertificateKeyStorePath[optional] CertificateKeyStorePassword[optional]");
      System.out
            .println("java EdgeGatewayCRUD https://vcloud username@Organization password orgname adminvdcname externalnetworkname edgegatewayname");
      System.out
            .println("java EdgeGatewayCRUD https://vcloud username@Organization password orgname adminvdcname externalnetworkname edgegatewayname certificatekeystorepath certificatekeystorepassword");
      System.exit(0);
   }

   /**
    * Check for tasks if any
    *
    * @param edgeGateway
    *           {@link EdgeGateway}
    * @return {@link Task}
    * @throws VCloudException
    */
   public static Task returnTask(EdgeGateway edgeGateway)
         throws VCloudException {
      TasksInProgressType tasksInProgress =
            edgeGateway.getResource().getTasks();
      if (tasksInProgress != null)
         for (TaskType task : tasksInProgress.getTask()) {
            return new Task(client, task);
         }
      return null;
   }

   /**
    * Gets AdminVdc Reference
    *
    * @param adminVdcName
    *           {@link String}
    * @param adminOrgRef
    *           {@link ReferenceType}
    * @return {@link ReferenceType}
    *
    * @throws VCloudException
    */
   private static ReferenceType getAdminVdcRef(String adminVdcName,
         ReferenceType adminOrgRef) throws VCloudException {
      AdminOrganization adminOrg;
      adminOrg = AdminOrganization.getAdminOrgByReference(client, adminOrgRef);
      return adminOrg.getAdminVdcRefByName(adminVdcName);
   }

   /**
    * Gets Org Reference
    *
    * @param orgName
    *           {@link String}
    * @return {@link ReferenceType}
    *
    * @throws VCloudException
    */
   private static ReferenceType getOrgRef(String orgName)
         throws VCloudException {
      return client.getVcloudAdmin().getAdminOrgRefByName(orgName);
   }

   /**
    * Gets External Network Reference
    *
    * @param networkName
    *           {@link String}
    * @return {@link ReferenceType}
    *
    * @throws VCloudException
    */
   private static ReferenceType getExternalNetworkRef(String networkName)
         throws VCloudException {
      return client.getVcloudAdmin().getExternalNetworkRefByName(networkName);
   }

   /**
    * Create params for Edge Gateway
    *
    * @param externalNetwork
    *           {@link ReferenceType}
    * @return GatewayType
    * @throws VCloudException
    */
   private static GatewayType createEdgeGatewayParams(
         ReferenceType externalNetwork, String edgeGatewayName)
         throws VCloudException {
      ExternalNetwork externalNet =
            ExternalNetwork.getExternalNetworkByReference(client,
                  externalNetwork);
      IpScopeType externalNetIpScope =
            externalNet.getResource().getConfiguration().getIpScopes()
                  .getIpScope().get(0);

      GatewayType gatewayParams = new GatewayType();
      gatewayParams.setName(edgeGatewayName);
      gatewayParams.setDescription("ee-gateway desc");
      GatewayConfigurationType gatewayConfig = new GatewayConfigurationType();
      gatewayConfig
            .setGatewayBackingConfig(GatewayBackingConfigValuesType.COMPACT
                  .value());
      GatewayInterfaceType gatewayInterface = new GatewayInterfaceType();
      gatewayInterface.setDisplayName("gateway interface");
      gatewayInterface.setNetwork(externalNetwork);
      gatewayInterface.setInterfaceType(GatewayEnums.UPLINK.value());

      SubnetParticipationType subnetParticipationType =
            new SubnetParticipationType();
      subnetParticipationType.setGateway(externalNetIpScope.getGateway());

      subnetParticipationType.setNetmask(externalNetIpScope.getNetmask());
      IpRangesType ipRanges = new IpRangesType();
      IpRangeType ipRange = new IpRangeType();
      String startAddress =
            externalNetIpScope.getIpRanges().getIpRange().get(0)
                  .getStartAddress();
      String endAddress =
            externalNetIpScope.getIpRanges().getIpRange().get(0)
                  .getEndAddress();
      ipRange.setStartAddress(startAddress);
      ipRange.setEndAddress(endAddress);
      ipRanges.getIpRange().add(ipRange);
      subnetParticipationType.setIpRanges(ipRanges);
      gatewayInterface.getSubnetParticipation().add(subnetParticipationType);
      gatewayInterface.setUseForDefaultRoute(true);
      GatewayInterfacesType interfaces = new GatewayInterfacesType();
      interfaces.getGatewayInterface().add(gatewayInterface);
      gatewayConfig.setGatewayInterfaces(interfaces);

      //Edge Gateway Service Configuration Section
      String[] subStr = startAddress.split("\\.");
      String[] subStr2 = endAddress.split("\\.");
      int currentAddress = Integer.parseInt(subStr[3]);
      int increment = 1;
      if (subStr[2].equalsIgnoreCase(subStr2[2])) {
         increment =
               Math.round((Integer.parseInt(subStr2[3]) - currentAddress) / 6);
         if (increment < 1) {
            System.err
                  .println("External Network IP Address Pool is not enough to configure "
                        + "the edge gateway services");
            return null;
         }
      } else
         increment = 5;

      GatewayFeaturesType gatewayFeatures = new GatewayFeaturesType();

      ObjectFactory objectFactory = new ObjectFactory();

      //Edge Gateway NAT service configuration
      NatServiceType natService = new NatServiceType();
      currentAddress += increment;
      natService.setExternalIp(subStr[0] + "." + subStr[1] + "." + subStr[2]
            + "." + currentAddress);
      natService.setIsEnabled(true);

      JAXBElement<NetworkServiceType> serviceType =
            objectFactory.createNetworkService(natService);
      gatewayFeatures.getNetworkService().add(serviceType);

      //Edge Gateway Firewall service configuration
      FirewallServiceType firewallService = new FirewallServiceType();
      firewallService.setIsEnabled(true);
      firewallService.setDefaultAction(FirewallPolicyType.DROP.value());
      firewallService.setLogDefaultAction(false);

      JAXBElement<FirewallServiceType> firewall =
            objectFactory.createFirewallService(firewallService);
      gatewayFeatures.getNetworkService().add(firewall);

      //Edge Gateway DHCP service configuration
      DhcpServiceType dhcpService = new DhcpServiceType();
      dhcpService.setDefaultLeaseTime(0);
      dhcpService.setIpRange(ipRange);
      dhcpService.setIsEnabled(true);
      dhcpService.setPrimaryNameServer("r2");
      dhcpService.setSubMask(externalNetIpScope.getNetmask());
      dhcpService.setDefaultLeaseTime(3600);
      dhcpService.setMaxLeaseTime(7200);

      JAXBElement<DhcpServiceType> dhcp =
            objectFactory.createDhcpService(dhcpService);
      gatewayFeatures.getNetworkService().add(dhcp);

      //Edge Gateway Load Balancer service configuration
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
      lBPoolMember.setIpAddress(subStr[0] + "." + subStr[1] + "." + subStr[2]
            + "." + currentAddress);
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

      LoadBalancerVirtualServerType loadBalancerVirtualServer =
            new LoadBalancerVirtualServerType();
      loadBalancerVirtualServer.setDescription("desc");
      loadBalancerVirtualServer.setIsEnabled(true);
      currentAddress += increment;
      loadBalancerVirtualServer.setIpAddress(subStr[0] + "." + subStr[1] + "."
            + subStr[2] + "." + currentAddress);
      loadBalancerVirtualServer.setName("VirtualServerName2");
      loadBalancerVirtualServer.setPool("PoolName");
      loadBalancerVirtualServer.setLogging(true);
      loadBalancerVirtualServer.setInterface(externalNetwork);

      LBVirtualServerServiceProfileType lBVirtualServerServiceProfile =
            new LBVirtualServerServiceProfileType();
      lBVirtualServerServiceProfile.setProtocol("HTTP");
      lBVirtualServerServiceProfile.setPort("80");
      lBVirtualServerServiceProfile.setIsEnabled(true);

      LBPersistenceType lBPersistence = new LBPersistenceType();
      lBPersistence.setCookieMode("INSERT");
      lBPersistence.setCookieName("CookieName2");
      lBPersistence.setMethod("COOKIE");
      lBVirtualServerServiceProfile.setPersistence(lBPersistence);
      loadBalancerVirtualServer.getServiceProfile().add(
            lBVirtualServerServiceProfile);

      loadBalancer.getVirtualServer().add(loadBalancerVirtualServer);
      loadBalancer.setIsEnabled(true);

      JAXBElement<LoadBalancerServiceType> load =
            objectFactory.createLoadBalancerService(loadBalancer);
      gatewayFeatures.getNetworkService().add(load);

      //Edge Gateway Static Routing service configuration
      StaticRoutingServiceType staticRouting = new StaticRoutingServiceType();
      staticRouting.setIsEnabled(true);
      StaticRouteType staticRoute = new StaticRouteType();
      staticRoute.setName("RouteName");
      staticRoute.setNetwork(subStr[0] + "." + subStr[1] + ".2.0/24");
      currentAddress += increment;
      staticRoute.setNextHopIp(subStr[0] + "." + subStr[1] + "." + subStr[2]
            + "." + currentAddress);
      staticRoute.setGatewayInterface(externalNetwork);
      staticRoute.setInterface("External");
      staticRouting.getStaticRoute().add(staticRoute);

      JAXBElement<StaticRoutingServiceType> route =
            objectFactory.createStaticRoutingService(staticRouting);
      gatewayFeatures.getNetworkService().add(route);

      //Edge Gateway VPN service configuration
      IpsecVpnServiceType vpn = new IpsecVpnServiceType();
      vpn.setExternalIpAddress(subStr[0] + "." + subStr[1] + "." + subStr[2]
            + "." + (Integer.parseInt(subStr[3]) - 5));
      vpn.setIsEnabled(false);
      currentAddress += increment;
      vpn.setPublicIpAddress(subStr[0] + "." + subStr[1] + "." + subStr[2]
            + "." + currentAddress);
      IpsecVpnTunnelType ipsecVpnTunnel = new IpsecVpnTunnelType();
      ipsecVpnTunnel.setMtu(1500);
      ipsecVpnTunnel.setName("VpnName");

      JAXBElement<IpsecVpnServiceType> ipsecVpn =
            objectFactory.createIpsecVpnService(vpn);
      gatewayFeatures.getNetworkService().add(ipsecVpn);

      gatewayConfig.setEdgeGatewayServiceConfiguration(gatewayFeatures);
      gatewayParams.setConfiguration(gatewayConfig);

      return gatewayParams;
   }

   /**
    * Updates Edge Gateway
    *
    * @param externalNetwork
    *           {@link ReferenceType}
    * @param edgeGateway
    *           {@link EdgeGateway}
    *
    * @return GatewayType
    * @throws VCloudException
    */
   private static GatewayType updateEdgeGatewayParams(
         ReferenceType externalNetwork, EdgeGateway edgeGateway)
         throws VCloudException {
      GatewayType gatewayParams = new GatewayType();
      gatewayParams.setName(edgeGateway.getResource().getName() + "_Updated");
      gatewayParams.setDescription("updated desc");
      gatewayParams.setConfiguration(edgeGateway.getResource()
            .getConfiguration());

      return gatewayParams;
   }

   /**
    * Main method, which does Creating and Updating Edge Gateway
    *
    * @param args
    *
    * @throws VCloudException
    * @throws KeyStoreException
    * @throws NoSuchAlgorithmException
    * @throws UnrecoverableKeyException
    * @throws KeyManagementException
    * @throws TimeoutException
    * @throws IOException
    * @throws CertificateException
    */
   public static void main(String args[]) throws VCloudException,
         KeyManagementException, UnrecoverableKeyException,
         NoSuchAlgorithmException, KeyStoreException, TimeoutException,
         CertificateException, IOException {

      if (args.length < 7)
         usage();

      VcloudClient.setLogLevel(Level.OFF);
      System.out.println("Vcloud Login");
      client = new VcloudClient(args[0], Version.V5_5);

      // Performing Certificate Validation
      if (args.length == 9) {
         System.out
               .println("	Validating Certificate using CustomSSLSocketFactory.java");

         System.setProperty("javax.net.ssl.trustStore", args[7]);
         System.setProperty("javax.net.ssl.trustStorePassword", args[8]);

         client.registerScheme("https", 443, CustomSSLSocketFactory
               .getInstance());
      } else if (args.length == 7) {
         System.err
               .println("Ignoring the Certificate Validation using FakeSSLSocketFactory.java - DO NOT DO THIS IN PRODUCTION");
         client
               .registerScheme("https", 443, FakeSSLSocketFactory.getInstance());
      } else {
         usage();
      }

      client.login(args[1], args[2]);
      System.out.println("Login Success");

      ReferenceType orgRef = getOrgRef(args[3]);
      ReferenceType adminVdcRef = getAdminVdcRef(args[4], orgRef);
      ReferenceType externalNetRef = getExternalNetworkRef(args[5]);
      System.out.println("external network: " + externalNetRef);
      AdminVdc adminVdc = AdminVdc.getAdminVdcByReference(client, adminVdcRef);

      System.out.println("Create Edge Gateway");
      GatewayType gateway = createEdgeGatewayParams(externalNetRef, args[6]);
      EdgeGateway edgeGateway = adminVdc.createEdgeGateway(gateway);
      Task createTask = returnTask(edgeGateway);
      if (createTask != null)
         createTask.waitForTask(0);
      System.out.println("Edge Gateway Created");
      System.out.println("	Edge Gateway:	"
            + edgeGateway.getResource().getName());

      edgeGateway =
            EdgeGateway.getEdgeGatewayByReference(client, edgeGateway
                  .getReference());
      System.out.println("Update Edge Gateway");
      gateway = updateEdgeGatewayParams(externalNetRef, edgeGateway);
      edgeGateway.updateEdgeGateway(gateway).waitForTask(0);
      System.out.println("Edge Gateway Updated");
      System.out.println("	Updated Edge Gateway:	"
            + edgeGateway.getResource().getName());

      System.out.println("Get Edge Gateway");
      System.out.println("	"
            + EdgeGateway.getEdgeGatewayByReference(client,
                  edgeGateway.getReference()).getResource().getName());

      System.out.println("Delete Edge Gateway");
      edgeGateway.delete().waitForTask(0);
      System.out.println("Edge Gateway deleted");
   }
}

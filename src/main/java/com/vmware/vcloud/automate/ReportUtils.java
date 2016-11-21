package com.vmware.vcloud.automate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.vmware.vcloud.api.rest.schema.ExternalNetworkType;
import com.vmware.vcloud.api.rest.schema.FirewallRuleType;
import com.vmware.vcloud.api.rest.schema.FirewallServiceType;
import com.vmware.vcloud.api.rest.schema.GatewayNatRuleType;
import com.vmware.vcloud.api.rest.schema.NatRuleType;
import com.vmware.vcloud.api.rest.schema.NatServiceType;
import com.vmware.vcloud.api.rest.schema.NetworkConnectionType;
import com.vmware.vcloud.model.Credential;
import com.vmware.vcloud.model.VCloudOrganization;
import com.vmware.vcloud.sdk.VCloudException;
import com.vmware.vcloud.sdk.VM;
import com.vmware.vcloud.sdk.Vapp;
import com.vmware.vcloud.sdk.VcloudClient;
import com.vmware.vcloud.sdk.VirtualDisk;
import com.vmware.vcloud.sdk.admin.EdgeGateway;
import com.vmware.vcloud.sdk.admin.ExternalNetwork;

public class ReportUtils {
	private static XSSFWorkbook workbook;
	private static FileOutputStream out;
	
	static void generateReport(VcloudClient client, Vapp vapp, EdgeGateway edgeGateway, VCloudOrganization vCloudOrg, String orgVdcName ,String fileName) throws VCloudException, IOException {
		
		System.out.println("Generating report for " + vCloudOrg.getFullName());
		
		workbook = new XSSFWorkbook();
		// Create a blank sheet
		XSSFSheet spreadsheet = workbook.createSheet(" Customer Info ");

		
		// This data needs to be written (Object[])
		Map<Integer, Object[]> cusinfo = new TreeMap<Integer, Object[]>();
		
		cusinfo.put(1, new Object[] { "Organization information","" });
		cusinfo.put(2, new Object[] { "Customer Name: ", vCloudOrg.getFullName()});
		cusinfo.put(3, new Object[] { "Organization VDC Name: ", orgVdcName});
		cusinfo.put(4, new Object[] { "URL: ", vCloudOrg.getUrl()});
		cusinfo.put(5, new Object[] { "Username: ", vCloudOrg.getUser().getName()});
		cusinfo.put(6, new Object[] { "Password: ", vCloudOrg.getUser().getPassword()});
		cusinfo.put(7, new Object[] { "",""});
		cusinfo.put(8, new Object[] { "VM information",""});
		
		int i = 9;
		for(VM vm: vapp.getChildrenVms()){
			cusinfo.put(i++, new Object[] { "VM Name: ", vm.getResource().getName()});
			cusinfo.put(i++, new Object[] { "vCPU: ", Integer.toString(vm.getCpu().getCoresPerSocket())});
			
			BigInteger memSize = vm.getMemory().getMemorySize();
			memSize = memSize.divide(BigInteger.valueOf(1024));
			
			cusinfo.put(i++, new Object[] { "vMemory: ", memSize.toString() +" GB"});
			
			List <VirtualDisk> disks = vm.getDisks();
			for (VirtualDisk disk : disks) {
				if (disk.isHardDisk()) {
					BigInteger diskSize = disk.getHardDiskSize();
					diskSize = diskSize.divide(BigInteger.valueOf(1024));
					cusinfo.put(i++, new Object[] { "Disk: ", diskSize.toString() +" GB" });
				}
			}
			
			Credential cred = getCredential(vm);
			cusinfo.put(i++, new Object[] { "OS: ", vm.getOperatingSystemSection().getDescription().getValue()});
			cusinfo.put(i++, new Object[] { "Username: ", cred.getUser()});
			cusinfo.put(i++, new Object[] { "Password: ", cred.getPassword()});
			
			for(NetworkConnectionType networkConnectionType : vm.getNetworkConnections())
				cusinfo.put(i++, new Object[] { "IP: ", networkConnectionType.getIpAddress()});
			
			cusinfo.put(i++, new Object[] { "",""});	
		}
		
		//Create style
		CellStyle style = workbook.createCellStyle();	
		CellStyle styleHead = workbook.createCellStyle();
		
		//Create font
	    Font font = workbook.createFont();	    
	    //Make font bold
	    font.setBold(true);	    
	    //set it to bold
	    style.setFont(font);
	    style.setAlignment(HorizontalAlignment.RIGHT);
	    
	    // Set style for header
	    styleHead.setFont(font);
	    styleHead.setAlignment(HorizontalAlignment.CENTER);
	    
		// Iterate over data and write to sheet
		Set<Integer> keyid = cusinfo.keySet();

		// Create row object
		XSSFRow row;
		int rowid = 0;
		for (Integer key : keyid) {
			row = spreadsheet.createRow(rowid++);
			Object[] objectArr = cusinfo.get(key);
			int cellid = 0;
			for (Object obj : objectArr) {
				Cell cell = row.createCell(cellid++);
				cell.setCellValue((String) obj);
				
				if (cellid == 1) {
					cell.setCellStyle(style);
					if (rowid == 1 || rowid == 8)
						cell.setCellStyle(styleHead);
				}
			}
		}			
		
		// Merge cell
		spreadsheet.addMergedRegion(new CellRangeAddress(0,0,0,1));
		spreadsheet.addMergedRegion(new CellRangeAddress(7,7,0,1));

		// Auto size on column 1 and 2 
		spreadsheet.autoSizeColumn(0);
		spreadsheet.autoSizeColumn(1);
		
		
		FirewallServiceType firewallService = NetworkUtils.getFirewallService(edgeGateway);
		
		XSSFSheet networkSheet = workbook.createSheet(" Network Info ");
		
		// This data needs to be written (Object[])
		Map<Integer, Object[]> netinfo = new TreeMap<Integer, Object[]>();
				

		netinfo.put(1, new Object[] { "Firewall information" });
		netinfo.put(2, new Object[] { "Rule", "Source IP", "Source Port", "Destination IP", "Destination Port", "Protocol" });
		
		int j = 3;
		for (FirewallRuleType fwRule : firewallService.getFirewallRule()) {
			
			String protocol = null;
			String sourcePort = ((fwRule.getSourcePort() == -1)? "Any" : fwRule.getSourcePort().toString());
						
			if(fwRule.getProtocols().isAny() != null && fwRule.getProtocols().isAny())
				protocol = "Any";
			
			if(fwRule.getProtocols().isIcmp() != null && fwRule.getProtocols().isIcmp())
				protocol = "ICMP";
			
			if(fwRule.getProtocols().isTcp() != null && fwRule.getProtocols().isTcp()){
				protocol = "TCP";
			}
			
			if(fwRule.getProtocols().isUdp() != null && fwRule.getProtocols().isUdp()){
				protocol = ((protocol != null && protocol.equalsIgnoreCase("TCP"))? "TCP & UDP" : "UDP");
			}
			
			netinfo.put(j++, new Object[] { 
							fwRule.getDescription(), 							
							fwRule.getSourceIp(),
							sourcePort, 
							fwRule.getDestinationIp(),
							fwRule.getDestinationPortRange(),
							protocol,  });
		}
		
		netinfo.put(j++, new Object[] { "" });
		
		int natHeaderIdx = j-1;
		netinfo.put(j++, new Object[] { "NAT" });
		netinfo.put(j++, new Object[] { "Applied On", "Description", "Type", "Original IP", "Original Port",  "Translated IP", "Translated Port", "Protocol" });
		
		// NAT report section
		NatServiceType natService = NetworkUtils.getNatService(edgeGateway);
						
		for(NatRuleType natRule : natService.getNatRule()){
		
			String natDescription = (natRule.getDescription() != null)? natRule.getDescription() : "";
			String natRuleType = (natRule.getRuleType() != null)? natRule.getRuleType() : "";
			String interfaceName = null;
			String originalIp = null;
			String translatedIp = null;
			String originalPort = null;
			String translatedPort = null;
			String protocol = null; 
							
			if (natRule.getGatewayNatRule() != null){
				
				GatewayNatRuleType gwNatRule = natRule.getGatewayNatRule();

				ExternalNetwork externalNet = ExternalNetwork.getExternalNetworkByReference(client, gwNatRule.getInterface());				
				interfaceName = (externalNet.getResource() != null) ? externalNet.getResource().getName() : "";
						
				originalIp = (gwNatRule.getOriginalIp() != null)? gwNatRule.getOriginalIp() : "";
				translatedIp = (gwNatRule.getTranslatedIp() != null)? gwNatRule.getTranslatedIp() : "";				
				originalPort = (gwNatRule.getOriginalPort() != null)? gwNatRule.getOriginalPort() : "any";
				translatedPort = (gwNatRule.getTranslatedPort() != null)? gwNatRule.getTranslatedPort() : "any";				
				protocol = (gwNatRule.getProtocol() != null)? gwNatRule.getProtocol() : "any";
				
				netinfo.put(j++, new Object[] { interfaceName, natDescription, natRuleType, 
												originalIp, originalPort,
												translatedIp, translatedPort, protocol });			
			}
			
			if (natRule.getOneToOneBasicRule() != null)
				System.out.println("OneToOneBasicRule");
			if (natRule.getOneToOneVmRule() != null)
				System.out.println("OneToOneVmRule");
			if (natRule.getPortForwardingRule() != null)
				System.out.println("PortForwardingRule");
			if (natRule.getVmRule() != null)
				System.out.println("tVmRule");
		}		
		
		
		// Iterate over data and write to sheet
		keyid = netinfo.keySet();
		
		// Create row object
		XSSFRow nrow;
		rowid = 0;
		for (Integer key : keyid) {
			nrow = networkSheet.createRow(rowid++);
			Object[] objectArr = netinfo.get(key);
			int cellid = 0;
			for (Object obj : objectArr) {
				Cell cell = nrow.createCell(cellid++);
				cell.setCellValue((String) obj);
				
				if (rowid == 1 || rowid == 2 || rowid == natHeaderIdx+1 || rowid == natHeaderIdx+2)
					cell.setCellStyle(styleHead);
			}
		}	
		
		// Merge cell
		networkSheet.addMergedRegion(new CellRangeAddress(0,0,0,5));
		networkSheet.addMergedRegion(new CellRangeAddress(natHeaderIdx,natHeaderIdx,0,7));

		// Auto size on column 1 - 9 
		networkSheet.autoSizeColumn(0);
		networkSheet.autoSizeColumn(1);
		networkSheet.autoSizeColumn(2);
		networkSheet.autoSizeColumn(3);
		networkSheet.autoSizeColumn(4);
		networkSheet.autoSizeColumn(5);
		networkSheet.autoSizeColumn(6);
		networkSheet.autoSizeColumn(7);
		networkSheet.autoSizeColumn(8);
		
		out = new FileOutputStream(new File(fileName));
		workbook.write(out);
		out.close();
		
	}
	
	  static Credential getCredential(VM vm) throws VCloudException {
	      String user = "root";
	      if (vm.getOperatingSystemSection() != null && vm.getOperatingSystemSection().getDescription() != null
	               && vm.getOperatingSystemSection().getDescription().getValue().indexOf("Windows") >= 0)
	         user = "Administrator";
	      String password = null;
	      if (vm.getGuestCustomizationSection() != null)
	         password = vm.getGuestCustomizationSection().getAdminPassword();
	      return new Credential(user, password);
	   }
}

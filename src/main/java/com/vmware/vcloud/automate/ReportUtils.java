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

import com.vmware.vcloud.api.rest.schema.NetworkConnectionType;
import com.vmware.vcloud.model.Credential;
import com.vmware.vcloud.model.VCloudOrganization;
import com.vmware.vcloud.sdk.VCloudException;
import com.vmware.vcloud.sdk.VM;
import com.vmware.vcloud.sdk.Vapp;
import com.vmware.vcloud.sdk.VirtualDisk;

public class ReportUtils {
	private static XSSFWorkbook workbook;
	private static FileOutputStream out;
	
	static void generateReport(Vapp vapp, VCloudOrganization vCloudOrg, String orgVdcName ,String fileName) throws VCloudException, IOException {
		
		System.out.println("Generating report for " + vCloudOrg.getFullName());
		
		workbook = new XSSFWorkbook();
		// Create a blank sheet
		XSSFSheet spreadsheet = workbook.createSheet(" Customer Info ");
		// Create row object
		XSSFRow row;
		
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
			//cusinfo.put(i++, new Object[] { "",""});			
			//cusinfo.put(i++, new Object[] { "Network Section"});
			
			for(NetworkConnectionType networkConnectionType : vm.getNetworkConnections()){
				cusinfo.put(i++, new Object[] { "IP: ", networkConnectionType.getIpAddress()});
				//cusinfo.put(i++, new Object[] { "NAT", networkConnectionType.getIpAddress(), networkConnectionType.getExternalIpAddress()});
			}
			
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

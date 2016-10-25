package com.vmware.vcloud.automate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import com.vmware.vcloud.exception.InvalidTemplateException;
import com.vmware.vcloud.model.VCloudOrganization;


public class ConfigParser {
	
	private VCloudOrganization org;
	
	public ConfigParser(String conf) throws FileNotFoundException {

		InputStream input;
		
		Constructor constructor = new Constructor();
        constructor.addTypeDescription(new TypeDescription(VCloudOrganization.class, "!organization"));
        Yaml yaml = new Yaml(constructor);

        if(conf == null || conf.isEmpty())
        	input = getClass().getResourceAsStream("/customer.yaml");
        else
        	input = new FileInputStream(new File(conf));	
		
        org = (VCloudOrganization) yaml.load(input);	
	}
	
	public static ConfigParser getParser(String conf) throws FileNotFoundException{
		return new ConfigParser(conf);
	}

	public VCloudOrganization getOrg() {
		return org;
	}
	
	boolean validate() throws InvalidTemplateException {
		
		VCloudOrganization vcdOrg = this.org;
		
		if(vcdOrg.getName() == null || vcdOrg.getName().length() == 0)
			throw new InvalidTemplateException("Missing organization name");
		
		return true;
	}
}

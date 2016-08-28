package com.vmware.vcloud.automate;

import java.io.InputStream;

import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import com.vmware.vcloud.model.Organization;


public class ConfigParser {
	
	private Organization org;
	
	public ConfigParser(String conf) {

		Constructor constructor = new Constructor();
        constructor.addTypeDescription(new TypeDescription(Organization.class, "!organization"));
        Yaml yaml = new Yaml(constructor);

		InputStream input = getClass().getResourceAsStream(conf);
		org = (Organization) yaml.load(input);	
	}
	
	public static ConfigParser getParser(String conf){
		return new ConfigParser(conf);
	}

	public Organization getOrg() {
		return org;
	}
	
}

package com.vmware.vcloud.automate;

import java.io.InputStream;

import org.yaml.snakeyaml.Yaml;

public class ConfigParser {

	public ConfigParser(String conf) {
		InputStream input = getClass().getResourceAsStream(conf);

		Yaml yaml = new Yaml();
		int counter = 0;
		for (Object data : yaml.loadAll(input)) {
			System.out.println(data);
			counter++;
		}
	}

}

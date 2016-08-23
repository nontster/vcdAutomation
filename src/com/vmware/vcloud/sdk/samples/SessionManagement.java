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
import java.util.logging.Level;

import org.apache.http.HttpException;

import com.vmware.vcloud.sdk.VCloudException;
import com.vmware.vcloud.sdk.VcloudClient;
import com.vmware.vcloud.sdk.constants.Version;

/**
 * This sample illustrates the session management capabilities using the
 * VcloudClient.
 *
 * 1. Creating multiple instances of VcloudClient. Every VcloudClient instance
 * has a seperate token.
 *
 * 2. Logging out a VcloudClient instance does not affect other VcloudClient
 * instance
 *
 * 3. Reusing the token across VcloudClient instances.
 *
 * 4. Extending the current session before it times out. Default session timeout
 * is 30 mins.
 *
 * 5. Shared tokens get void, if one the VcloudClient instance using it logs
 * out.
 *
 * @author Ecosystem Engineering
 *
 */

public class SessionManagement {

    private static VcloudClient vcloudClient1;

    private static VcloudClient vcloudClient2;

    /**
     * Sample Usage
     */
    public static void usage() {
	System.out
		.println("java SessionManagement vCloudURL user@organization password CertificateKeyStorePath[optional] CertificateKeyStorePassword[optional]");
	System.out
		.println("java SessionManagement https://vcloud user@System password");
	System.out
		.println("java SessionManagement https://vcloud user@System password certificatekeystorepath certificatekeystorepassword");
	System.exit(0);
    }

    /*
     * Validating certificate
     *
     * @param vcloudClient
     * @param commandLineArgs
     * @return {@link VcloudClient}
     * @throws KeyManagementException
     * @throws NoSuchAlgorithmException
     * @throws UnrecoverableKeyException
     * @throws KeyStoreException
     * @throws CertificateException
     * @throws IOException
     */
    private static VcloudClient certificateValidation(
	    VcloudClient vcloudClient, String[] commandLineArgs)
	    throws KeyManagementException, UnrecoverableKeyException,
	    NoSuchAlgorithmException, KeyStoreException, CertificateException,
	    IOException {

	if (commandLineArgs.length < 3)
	    usage();

	// Client login
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

	if (!commandLineArgs[1].contains("System")) {
	    System.out.println("This sample requires System admin credentials");
	    System.exit(0);
	}

	return vcloudClient;
    }

    /**
     * SessionManagement sample main.
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

    public static void main(String args[]) throws HttpException, IOException,
	    KeyManagementException, NoSuchAlgorithmException,
	    UnrecoverableKeyException, KeyStoreException, CertificateException {

	vcloudClient1 = certificateValidation(vcloudClient1, args);

	vcloudClient2 = certificateValidation(vcloudClient2, args);

	System.out
		.println("Note 1 : Every Authenticated VcloudClient instance gets a seperate token");
	// client 1 login
	try {
	    vcloudClient1.login(args[1], args[2]);
	    System.out.println("VcloudClient 1");
	    System.out.println("	Login Successful");
	    System.out.println("	Token - " + vcloudClient1.getVcloudToken());
	    System.out.println("	"
		    + vcloudClient1.getVcloudAdmin().getResource()
			    .getDescription());
	} catch (VCloudException e) {
	    System.out.println("	" + e.getMessage());
	}

	// client 2 login
	try {
	    vcloudClient2.login(args[1], args[2]);
	    System.out.println("VcloudClient 2");
	    System.out.println("	Login Successful");
	    System.out.println("	Token - " + vcloudClient2.getVcloudToken());
	    System.out.println("	"
		    + vcloudClient1.getVcloudAdmin().getResource()
			    .getDescription());
	} catch (VCloudException e) {
	    System.out.println("	" + e.getMessage());
	}

	System.out.println();
	System.out
		.println("Note 2 : Logging out a VcloudClient instance does not affect other VcloudClient instance");
	// client 1 logout
	try {
	    vcloudClient1.logout();
	    System.out.println("VcloudClient 1");
	    System.out.println("	Logout Successful");
	} catch (VCloudException e) {
	    System.out.println("	" + e.getMessage());
	}

	// client 2 performing some operation
	try {
	    System.out.println("VcloudClient 2");
	    System.out.println("	Still valid");
	    System.out.println("	Token - " + vcloudClient2.getVcloudToken());
	    System.out.println("	"
		    + vcloudClient2.getVcloudAdmin().getResource()
			    .getDescription());
	} catch (VCloudException e) {
	    System.out.println("	" + e.getMessage());
	}

	System.out.println();
	System.out
		.println("Note 3: Reusing the token from VcloudClient 2 to VcloudClient 1");
	try {
	    vcloudClient1.setVcloudToken(vcloudClient2.getVcloudToken());
	    System.out.println("VcloudClient 1");
	    System.out.println("	"
		    + vcloudClient1.getVcloudAdmin().getResource()
			    .getDescription());
	    System.out.println("	Token - " + vcloudClient1.getVcloudToken());
	    System.out.println("VcloudClient 2");
	    System.out.println("	Token - " + vcloudClient2.getVcloudToken());
	    System.out.println("	"
		    + vcloudClient2.getVcloudAdmin().getResource()
			    .getDescription());
	} catch (VCloudException e) {
	    System.out.println("	" + e.getMessage());
	}

	System.out.println();
	System.out
		.println("Note 4: Extending the current session before it timesout. Default session timeout is 30 mins");
	System.out
		.println(vcloudClient1.extendSession() ? "VcloudClient 1 session extended for another 30 mins"
			: "	VcloudClient 1 session extension failed. Login and try to extend the session");
	System.out
		.println(vcloudClient2.extendSession() ? "VcloudClient 2 session extended for another 30 mins"
			: "	VcloudClient 2 session extension failed. Login and try to extend the session");

	System.out.println();
	System.out
		.println("Note 5: Since the same token has been shared across VcloudClient 1 and VcloudClient 2. Logging out VcloudClint 1 or VcloudClient 2 will void the other session.");
	// client 1 logout
	try {
	    vcloudClient1.logout();
	    System.out.println("VcloudClient 1");
	    System.out.println("	Logout Successful");
	} catch (VCloudException e) {
	    System.out.println("	" + e.getMessage());
	}

	// client 1 and client 2 performing operation after logout.
	try {
	    System.out.println("VcloudClient 1");
	    System.out.println("	"
		    + vcloudClient1.getVcloudAdmin().getResource()
			    .getDescription());
	} catch (VCloudException e) {
	    System.out.println("	" + e.getMessage());
	}
	try {
	    System.out.println("VcloudClient 2");
	    System.out.println("	"
		    + vcloudClient2.getVcloudAdmin().getResource()
			    .getDescription());
	} catch (VCloudException e) {
	    System.out.println("	" + e.getMessage());
	}

    }
}

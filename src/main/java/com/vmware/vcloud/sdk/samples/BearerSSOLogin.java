/*
 * *******************************************************
 * Copyright VMware, Inc. 2013.  All Rights Reserved.
 * *******************************************************
 *
 * DISCLAIMER. THIS PROGRAM IS PROVIDED TO YOU "AS IS" WITHOUT
 * WARRANTIES OR CONDITIONS # OF ANY KIND, WHETHER ORAL OR WRITTEN,
 * EXPRESS OR IMPLIED. THE AUTHOR SPECIFICALLY # DISCLAIMS ANY IMPLIED
 * WARRANTIES OR CONDITIONS OF MERCHANTABILITY, SATISFACTORY # QUALITY,
 * NON-INFRINGEMENT AND FITNESS FOR A PARTICULAR PURPOSE.
 */
package com.vmware.vcloud.sdk.samples;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.http.HttpException;
import org.w3c.dom.Element;

//import com.vmware.sso.client.samples.AcquireBearerTokenByUserCredentialSample;
//import com.vmware.sso.client.utils.Utils;
import com.vmware.vcloud.sdk.VCloudException;
import com.vmware.vcloud.sdk.VcloudClient;
import com.vmware.vcloud.sdk.constants.Version;

/**
 * Login to vCD using the Bearer Token
 *
 * @author Ecosystem Engineering
 *
 */
public class BearerSSOLogin {

   private static VcloudClient client;

   /**
    * Sample Usage
    */
   public static void usage() {
      System.out
            .println("java BearerSSOLogin VcloudUrl OrgName SSOUrl SSO_user@domain SSO_password CertificateKeyStorePath[optional] CertificateKeyStorePassword[optional]");
      System.out
            .println("java BearerSSOLogin https://vcloud orgName ssourl sso_user@domain sso_password");
      System.out
            .println("java BearerSSOLogin https://vcloud orgName ssoUrl sso_user@domain sso_password certificatekeystorepath certificatekeystorepassword");
      System.exit(0);
   }

   /**
    * Main method - Login to the vCloud using the SAML Assertion XML
    *
    * @param args
    * @throws HttpException
    * @throws SecurityException
    * @throws FileNotFoundException
    * @throws VCloudException
    * @throws IOException
    * @throws NoSuchAlgorithmException
    * @throws KeyManagementException
    * @throws KeyStoreException
    * @throws UnrecoverableKeyException
    * @throws TimeoutException
    * @throws DatatypeConfigurationException
    * @throws TransformerException
    * @throws CertificateException
    */
   public static void main(String args[]) throws HttpException,
         SecurityException, FileNotFoundException, VCloudException,
         IOException, KeyManagementException, NoSuchAlgorithmException,
         UnrecoverableKeyException, KeyStoreException, TimeoutException,
         TransformerException, DatatypeConfigurationException,
         CertificateException {

      if (args.length < 5)
         usage();

      VcloudClient.setLogLevel(Level.OFF);
      System.out.println("Vcloud SSO Login");
      System.out.println("----------------");
      client = new VcloudClient(args[0], Version.V5_5);

      // Performing Certificate Validation
      if (args.length == 7) {
         System.out
               .println("	Validating Certificate using CustomSSLSocketFactory.java");

         System.setProperty("javax.net.ssl.trustStore", args[5]);
         System.setProperty("javax.net.ssl.trustStorePassword", args[6]);

         client.registerScheme("https", 443, CustomSSLSocketFactory
               .getInstance());
      } else if (args.length == 5) {
         System.err
               .println("Ignoring the Certificate Validation using FakeSSLSocketFactory.java - DO NOT DO THIS IN PRODUCTION");
         client
               .registerScheme("https", 443, FakeSSLSocketFactory.getInstance());
      } else {
         usage();
      }

      Element tokenElement = null;
      String org = args[1];
      String vcdAudience = args[0] + "/cloud" + (org.equalsIgnoreCase("System") ? "" : "/org/" + org) + "/saml/metadata/alias/vcd";
      String[] ssoArgs = { args[2], args[3], args[4], vcdAudience };

      // This code snippet needs the vSphere Management SDK's - SSO Client and
      // samples jar (ssoclient.jar & samples.jar)
      /*Utils.trustAllHttpsCertificates();
      tokenElement = AcquireBearerTokenByUserCredentialSample.getToken(ssoArgs);

      //Prints the generated sso token
      Utils.printToken(tokenElement);*/

      client.ssoLogin(tokenElement, args[1]);
      System.out.println("	Logged in using the SAML Assertion");

      client.getVcloudAdmin();
      System.out.println("	Get Vcloud Admin");

      client.logout();
      System.out.println("	Logout");

   }
}

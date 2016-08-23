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

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.X509Certificate;
import java.util.logging.Level;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.codec.binary.Base64;
import org.w3c.dom.Element;

//import com.vmware.sso.client.samples.AcquireHoKTokenByUserCredentialSample;
//import com.vmware.sso.client.utils.SecurityUtil;
//import com.vmware.sso.client.utils.Utils;
import com.vmware.vcloud.sdk.VcloudClient;
import com.vmware.vcloud.sdk.constants.Version;

/**
 * Login to vCD using the HOK Token
 *
 * @author Ecosystem Engineering
 *
 */
public class HOKSSOLogin {

   private static VcloudClient client;

   /**
    * Sample Usage
    */
   public static void usage() {
      System.out
            .println("java HOKSSOLogin VcloudUrl OrgName SSOUrl SSO_user@domain SSO_password keystore_path keystore_password keystore_alias signature_algo CertificateKeyStorePath[optional] CertificateKeyStorePassword[optional]");
      System.out
            .println("java HOKSSOLogin https://vcloud orgName ssourl sso_user@domain sso_password keystore_path keystore_password keystore_alias signature_algo");
      System.out
            .println("java HOKSSOLogin https://vcloud orgName ssoUrl sso_user@domain sso_password keystore_path keystore_password keystore_alias signature_algo certificatekeystorepath certificatekeystorepassword");
      System.exit(0);
   }

   /*
    * Generates the signature
    *
    * @param cert {@link X509Certificate}
    * @param key {@link PrivateKey}
    * @param alg {@link String}
    * @param token {@link Element}
    *
    * @return {@link String}
    *
    * @throws NoSuchAlgorithmException
    * @throws InvalidKeyException
    * @throws SignatureException
    */
   public static String generateSignature(X509Certificate cert, PrivateKey key,
         String alg, Element token) throws NoSuchAlgorithmException,
         InvalidKeyException, SignatureException {
      Signature dsa = Signature.getInstance(alg);
      dsa.initSign(key);
      String tokenXml = getToken(token);
      dsa.update(tokenXml.getBytes());
      String output = new Base64(0).encodeToString(dsa.sign());
      output = output.replaceAll(System.getProperty("line.separator"), "");
      return output;
   }

   /*
    * Gets the token
    *
    * @param token {@link Element}
    *
    * @return {@link String}
    */
   public static String getToken(Element token) {
      try {
         ByteArrayOutputStream out = new ByteArrayOutputStream();
         token.normalize();

         TransformerFactory tFactory = TransformerFactory.newInstance();
         Transformer tf = tFactory.newTransformer();
         tf.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
         tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
         tf.setOutputProperty(OutputKeys.METHOD, "xml");
         StreamResult result =
               new StreamResult(new OutputStreamWriter(out, "UTF-8"));
         tf.transform(new DOMSource(token), result);
         return out.toString().replaceAll("\r", "");
      } catch (Exception e) {
         throw new IllegalStateException(e);
      }
   }

   /**
    * Main method - Login to the vCloud using the SAML Assertion XML and
    * signature
    *
    * @param args
    * @throws Exception
    */
   public static void main(String args[]) throws Exception {

      if (args.length < 9)
         usage();

      VcloudClient.setLogLevel(Level.OFF);
      System.out.println("Vcloud SSO Login");
      System.out.println("----------------");
      client = new VcloudClient(args[0], Version.V5_5);

      // Performing Certificate Validation
      if (args.length == 11) {
         System.out
               .println("      Validating Certificate using CustomSSLSocketFactory.java");

         System.setProperty("javax.net.ssl.trustStore", args[9]);
         System.setProperty("javax.net.ssl.trustStorePassword", args[10]);

         client.registerScheme("https", 443, CustomSSLSocketFactory
               .getInstance());
      } else if (args.length == 9) {
         System.err
               .println("Ignoring the Certificate Validation using FakeSSLSocketFactory.java - DO NOT DO THIS IN PRODUCTION");
         client
               .registerScheme("https", 443, FakeSSLSocketFactory.getInstance());
      } else {
         usage();
      }

      Element tokenElement = null;
      String org = args[1];
      String vcdAudience =
            args[0] + "/cloud"
                  + (org.equalsIgnoreCase("System") ? "" : "/org/" + org)
                  + "/saml/metadata/alias/vcd";
      X509Certificate certificate = null;
      PrivateKey privateKey = null;

      // This code snippet needs the vSphere Management SDK's - SSO Client and
      // samples jar (ssoclient.jar & samples.jar)

      /*Utils.trustAllHttpsCertificates();
      String[] ssoArgs =
            { args[2], args[3], args[4], args[5], args[6], args[7] };
      SecurityUtil securityUtil =
            SecurityUtil.loadFromKeystore(ssoArgs[3], ssoArgs[4], ssoArgs[5]);
      certificate = securityUtil.getUserCert();
      privateKey = securityUtil.getPrivateKey();
      tokenElement =
            AcquireHoKTokenByUserCredentialSample.getToken(ssoArgs, privateKey,
                  certificate, vcdAudience);

      // Prints the generated sso token
      Utils.printToken(tokenElement);*/

      //Verifying signature
      String signAlgo = args[8];
      String signature = null;

      if (!signAlgo.isEmpty()) {
         System.out.println("\n\tSignature Algorithm:      " + signAlgo);
         signature =
               generateSignature(certificate, privateKey, signAlgo,
                     tokenElement);
         System.out.println("\n\tSignature generated:      " + signature);
      } else {
         // i.e. when 'signature_algo' is passed as "" in user arguments
         // Valid only for VCVA setup
         System.out.println("   Login without signature...");
         signAlgo = null;
      }

      client.ssoLogin(tokenElement, args[1], signature, signAlgo);
      System.out.println("    Logged in using the HOK Token");

      client.getVcloudAdmin();
      System.out.println("    Get Vcloud Admin");

      client.logout();
      System.out.println("    Logout");

   }
}

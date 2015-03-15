/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ssi_aula1;
import java.io.*;
import java.security.cert.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Gomes
 */
public class ValidateCertPath {

    public static void main(String[] args) throws Exception {
        System.out.println(validate("certs/cacert.pem","certs/client_cert.pem"));
        
        
    }
    
    public static int validate(String trustAnchor, String targetCert){
        try {
            PKIXParameters params = createParams(trustAnchor);
            CertPath cp = null;
            
            cp = createPath(targetCert);
            
            CertPathValidator cpv = CertPathValidator.getInstance("PKIX");
            
            CertPathValidatorResult cpvr = cpv.validate(cp, params);
            return 1;
        } catch (Exception ex) {
            return 0;
        }
        
    }

    public static PKIXParameters createParams(String anchorFile) throws Exception {
        TrustAnchor anchor = new TrustAnchor(getCertFromFile(anchorFile), null);
        Set anchors = Collections.singleton(anchor);
        PKIXParameters params = new PKIXParameters(anchors);
        params.setRevocationEnabled(false);
        return params;
    }

    public static CertPath createPath(String certPath) throws Exception {
        File certPathFile = new File(certPath);
        FileInputStream certPathInputStream = new FileInputStream(certPathFile);
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        try {
            return cf.generateCertPath(certPathInputStream, "PKCS7");
        } catch (CertificateException ce) {
            // try generateCertificates
            Collection c = cf.generateCertificates(certPathInputStream);
            return cf.generateCertPath(new ArrayList(c));
        }
    }

    public static CertPath createPath(String[] certs) throws Exception {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        List list = new ArrayList();
        for (int i = 1; i < certs.length; i++) {
            list.add(getCertFromFile(certs[i]));
        }
        CertPath cp = cf.generateCertPath(list);
        return cp;
    }

    /**
     * Get a DER or BASE64-encoded X.509 certificate from a file.
     *
     * @param certFilePath path to file containing DER or BASE64-encoded
     * certificate
     * @return X509Certificate
     * @throws Exception on error
     */
    public static X509Certificate getCertFromFile(String certFilePath)
            throws Exception {
        X509Certificate cert = null;
        File certFile = new File(certFilePath);
        FileInputStream certFileInputStream = new FileInputStream(certFile);
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        cert = (X509Certificate) cf.generateCertificate(certFileInputStream);
        return cert;
    }
}

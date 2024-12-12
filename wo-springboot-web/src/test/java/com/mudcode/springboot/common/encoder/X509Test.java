package com.mudcode.springboot.common.encoder;

import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

class X509Test {

    @Test
    void x509() throws Exception {
        try (InputStream is = this.getClass().getResourceAsStream("/cert/ctlcode.crt")) {
            CertificateFactory factory = CertificateFactory.getInstance("X.509");
            X509Certificate certificate = (X509Certificate) factory.generateCertificate(is);
            certificate.checkValidity();
            System.out.println(certificate);
            System.out.println(certificate.getPublicKey().toString());
        }
    }

    @Test
    void p12() throws Exception {
        try (InputStream is = this.getClass().getResourceAsStream("/cert/ctlcode.p12")) {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            char[] pwd = new char[0];
            keyStore.load(is, pwd);
            Enumeration<String> aliases = keyStore.aliases();
            while (aliases.hasMoreElements()) {
                String alias = aliases.nextElement();
                System.out.println(alias);
                X509Certificate certificate = (X509Certificate) keyStore.getCertificate(alias);
                certificate.checkValidity();
                System.out.println(certificate);
                Key key = keyStore.getKey(alias, pwd);
                System.out.println(key.toString());
            }
        }
    }

}

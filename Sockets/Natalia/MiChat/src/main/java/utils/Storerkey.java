/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import sun.security.tools.keytool.CertAndKeyGen;
import sun.security.x509.X500Name;
import sun.security.x509.X509CertImpl;
import sun.security.x509.X509CertInfo;


/**
 *
 * @author dirrospace
 */
public class Storerkey {
    private CertAndKeyGen certGen;
    private X509Certificate certLoad;
    private RSAPrivateKey keyLoad;

    public Storerkey() {
        try {
            // Para nuestro KEYSTORE
            certGen = new CertAndKeyGen("RSA", "SHA256WithRSA", null);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Storerkey.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchProviderException ex) {
            Logger.getLogger(Storerkey.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String generar(String llaves) {
        Security.addProvider(new BouncyCastleProvider());
        String mandarBase64 = null;
        try {
            // Carga clave privada >>>>> PRUEBA PFX
            InputStream inp = this.getClass().getResourceAsStream("/claves/LoginProject.privada");
            Seguridad asimetr = new Seguridad();
            PrivateKey miprivada = asimetr.prk_byteTOPRK(
                    asimetr.obtenerPrivadaBytes_Fichero(null, null, inp)
            );
            // CLAVE PUBLICA y su PROCESO DE FIRMA
            certGen.generate(2048); // TE DA ERROR INVALIDKEYEXCEPTION
            // prepare the validity of the certificate Y A partir del certificado generar info
            //long validSecs = (long) 365 * 24 * 60 * 60;
            X509Certificate cert = certGen.getSelfCertificate(
                    new X500Name(
                            "CN=TestingLogin,O=My Organisation,L=My City,C=DE"
                    ),
                    (long) 365 * 24 * 60 * 60
            );
            X509CertInfo info = new X509CertInfo(cert.getTBSCertificate());
            info.set(X509CertInfo.ISSUER, new X500Name("CN=SERVIDOR,O=My Organisation,L=My City,C=DE"));
            // Clave publica del cliente a firmar. Como esta firmada se usa X509CertImpl class
            X509CertImpl certificadoCliente = new X509CertImpl(info);
            // Firma por defecto: su algoritmo >
            certificadoCliente.sign(miprivada, cert.getSigAlgName());

            // GENERAR CLAVE PRIVADA DE CLIENTE
            PrivateKey pk = certGen.getPrivateKey();

            // ALMACENADO
            KeyStore ks = KeyStore.getInstance("PKCS12", "BC");
            ks.load(null, null); // ** necesario para funcionar
            ks.setCertificateEntry("publica", certificadoCliente);
            ks.setKeyEntry("privada", pk, null, new Certificate[]{certificadoCliente});
            ByteArrayOutputStream destinoLectura = new ByteArrayOutputStream();
            // metemos la clave de acceso
            ks.store(destinoLectura, llaves.toCharArray());
            mandarBase64 = Base64.encodeBase64String(destinoLectura.toByteArray());

        } catch (Exception ex) {
            Logger.getLogger(Storerkey.class.getName()).log(Level.SEVERE, null, ex);
        }
        return mandarBase64;
    }

// ALMACENAR Y OBTENER DE FICHERO
    public boolean almacenarStore(String user, byte[] recibidoStore) {
        boolean almacenado = false;
        File f = new File(user.concat(".pfx"));
        try {

            if (!f.exists()) {
                FileOutputStream fos = new FileOutputStream(new File(user.concat(".pfx")));
                if (recibidoStore != null) {
                    // Strings recibidpos de cliente: aqui ya son bytes la clave publica
                    fos.write(recibidoStore);
                    fos.close();
                    almacenado = true;
                }
            }

        } catch (Exception ex) {
            Logger.getLogger(Storerkey.class.getName()).log(Level.SEVERE, null, ex);
        }
        return almacenado;
    }

    public byte[] tomarStore(String user, String llaves) {
        byte[] keyStore = null;
        try {
            byte[] bufferPriv = new byte[5000];
            FileInputStream in = new FileInputStream(user.concat(".pfx"));
            int chars = in.read(bufferPriv, 0, 5000);
                in.close();

                byte[] bufferPriv2 = new byte[chars];
                System.arraycopy(bufferPriv, 0, bufferPriv2, 0, chars);
                keyStore = bufferPriv2;
            
        } catch (Exception ex) {
            Logger.getLogger(Storerkey.class.getName()).log(Level.SEVERE, null, ex);
        }
        return keyStore;
    }

// GUARDAR LAS CLAVES EN FICHEROS
    public byte[] getCertificate(String user, String llaves, byte[] keystore) {
        X509Certificate certificado = null;
        byte[] certificadoBytes = null;
        try {
            char[] llave = llaves.toCharArray();
            ByteArrayInputStream input = null;
            if (keystore != null) {
                // Strings recibidpos de cliente: aqui ya son bytes la clave publica
                input = new ByteArrayInputStream(keystore);
            }
            Security.addProvider(new BouncyCastleProvider()); // no such provider
            KeyStore ksLoad = KeyStore.getInstance("PKCS12", "BC");
            
            ksLoad.load(input, llave);

            certificado = (X509Certificate) ksLoad.getCertificate("publica");
            KeyStore.PasswordProtection pt = new KeyStore.PasswordProtection(llave);

            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) ksLoad.getEntry("privada", pt);
            keyLoad = (RSAPrivateKey) privateKeyEntry.getPrivateKey();

            certificadoBytes = certificado.getEncoded();

        } catch (Exception ex) {
//            Logger.getLogger(Storerkey.class
//                    .getName()).log(Level.SEVERE, null, ex);
        }
        return certificadoBytes;
    }

    public boolean verifica(String certBase64) {
        boolean correcto = false;

        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            Certificate cert2 = (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(Base64.decodeBase64(certBase64)));

            Seguridad a = new Seguridad();
            PublicKey servPublica = a.puk_byteTOPUK(
                    a.obtenerPublicaBytes_Fichero(null, null, this.getClass().getResourceAsStream("/claves/LoginProject.publica")
                    ));
            try {
                cert2.verify(servPublica);
                correcto = true;
            } catch (Exception e) {
                System.out.println(e);

            } finally {

            }

        } catch (CertificateException ex) {
            Logger.getLogger(Storerkey.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

        return correcto;
    }

}

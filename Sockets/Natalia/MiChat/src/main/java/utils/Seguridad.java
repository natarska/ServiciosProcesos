/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import org.apache.commons.codec.binary.Base64;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 *
 * @author dirrospace
 */
public class Seguridad {
    private KeyFactory keyFactoryRSA;
    private Cipher cifrador;

    public Seguridad() {
        try {
            Security.addProvider(new BouncyCastleProvider());
            keyFactoryRSA = KeyFactory.getInstance("RSA", "BC");
            this.cifrador = Cipher.getInstance("RSA/ECB/PKCS1Padding", "BC");
        } catch (Exception ex) {
            Logger.getLogger(Seguridad.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public byte[] obtenerPublicaBytes_Fichero(File file, String sin, InputStream ist) {
        byte[] bufferPub = new byte[5000];
        InputStream in = ist;

        byte[] bufferPub2 = null;
        try {
            int charsPub = in.read(bufferPub, 0, 5000);
            in.close();

            bufferPub2 = new byte[charsPub];
            System.arraycopy(bufferPub, 0, bufferPub2, 0, charsPub);
            in.close();
        } catch (Exception ex) {
            Logger.getLogger(Seguridad.class.getName()).log(Level.SEVERE, null, ex);
        }
        return bufferPub2;
    }

    public byte[] obtenerPrivadaBytes_Fichero(File file, String sin, InputStream ist) {
        byte[] bufferPriv2 = null;
        try {
            byte[] bufferPriv = new byte[5000];
            int chars;
            if ((file != null) || (sin != null)) {
                FileInputStream in = null;
                if (file != null) {
                    in = new FileInputStream(file);
                } else {
                    in = new FileInputStream(sin);
                    DataInputStream localDataInputStream = new DataInputStream(in);
                }
                chars = in.read(bufferPriv, 0, 5000);
                in.close();
            } else {
                InputStream input = ist;
                chars = input.read(bufferPriv, 0, 5000);
                input.close();
            }
            bufferPriv2 = new byte[chars];
            System.arraycopy(bufferPriv, 0, bufferPriv2, 0, chars);
        } catch (Exception ex) {
            Logger.getLogger(Seguridad.class.getName()).log(Level.SEVERE, null, ex);
        }
        return bufferPriv2;
    }
    
    public byte[] eNCODEFirmaClavePrivada(byte[] puk, byte[] clavePrivada64) {
        PrivateKey clavePrivada = prk_byteTOPRK(Base64.decodeBase64(clavePrivada64));
        byte[] ClavecifraConPuk64 = null;
        try {
            this.cifrador.init(Cipher.ENCRYPT_MODE, clavePrivada);
            byte[] bufferCifrado = this.cifrador.doFinal(puk);
            ClavecifraConPuk64 = bufferCifrado;
            // Porque no hace falta mandarlo
//            ClavecifraConPuk64 = Base64.encodeBase64(bufferCifrado);
        } catch (Exception ex) {
            Logger.getLogger(Seguridad.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ClavecifraConPuk64;
    }
    
    public String decode_PRK(PrivateKey clavePrivada, byte[] contenido) {
        String clave = null ;
        try{
            this.cifrador.init(Cipher.DECRYPT_MODE, clavePrivada);
            byte[] bufferCifrado = this.cifrador.doFinal(contenido);
            clave = byteToString(bufferCifrado);
        } catch (Exception ex) {
            Logger.getLogger(Seguridad.class.getName()).log(Level.SEVERE, null, ex);
        }
        return clave;
    }
    public byte[] encode_PUK(String contenido, PublicKey clavePublica) {
        byte[] bufferCifrado = null;
        try {
            this.cifrador.init(Cipher.ENCRYPT_MODE, clavePublica);
            bufferCifrado = this.cifrador.doFinal(contenido.getBytes("UTF-8"));
        } catch (Exception ex) {
            Logger.getLogger(Seguridad.class.getName()).log(Level.SEVERE, null, ex);
        }
        return bufferCifrado;
    }

    public PublicKey puk_byteTOPUK(byte[] publickeyBytes) {
        PublicKey keyPub = null;
        try {
            X509EncodedKeySpec manejadorPublica = new X509EncodedKeySpec(publickeyBytes);
            keyPub = this.keyFactoryRSA.generatePublic(manejadorPublica);
        } catch (Exception ex) {
            Logger.getLogger(Seguridad.class.getName()).log(Level.SEVERE, null, ex);
        }
        return keyPub;
    }
    public PrivateKey prk_byteTOPRK(byte[] privatekeyBytes) {
        PrivateKey keyPriv = null;
        try {
            PKCS8EncodedKeySpec manejadorPrivada = new PKCS8EncodedKeySpec(privatekeyBytes);
            keyPriv = this.keyFactoryRSA.generatePrivate(manejadorPrivada);
        } catch (Exception ex) {
            Logger.getLogger(Seguridad.class.getName()).log(Level.SEVERE, null, ex);
        }
        return keyPriv;
    }  

    public String byteToString(byte[] _bytes) {
        String file_string = "";
        for (int i = 0; i < _bytes.length; i++) {
            file_string = file_string + (char) _bytes[i];
        }
        return file_string;
    }

}

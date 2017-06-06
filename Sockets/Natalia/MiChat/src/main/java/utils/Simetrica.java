/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.security.MessageDigest;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import static org.apache.commons.codec.binary.Base64.decodeBase64;
import static org.apache.commons.codec.binary.Base64.encodeBase64;

/**
 *
 * @author dirrospace
 */
public class Simetrica {
    public String cifrar(String key,String contenido){
        String cifradoB64 = null;
        try {
            // String to array bytes
            final byte[] bytes = contenido.getBytes("UTF-8");
            final Cipher aes = obtieneCipher(key,true);
            final byte[] cifrado = aes.doFinal(bytes);
            // este da un nuevo array bytes > coger org apache commom dependencia
            cifradoB64 = new String(encodeBase64(cifrado));
            
        }  catch (Exception ex) {
            Logger.getLogger(Simetrica.class.getName()).log(Level.SEVERE, null, ex);
        }
        return cifradoB64;
    }

    // CIFRADO Y DOFINAL SE CONSIGUE ARRAY DE BYTE, clave con la que la quiero cifrar
        public static String descifra(String key,String cifrado) throws Exception {
        final Cipher aes = obtieneCipher(key,false);
        final byte[] bytes = aes.doFinal(decodeBase64(cifrado));
        final String sinCifrar = new String(bytes, "UTF-8");
        return sinCifrar;
    }
        
    private static Cipher obtieneCipher(String clave,boolean paraCifrar) throws Exception {

        final MessageDigest digest = 
        MessageDigest.getInstance("SHA-1");
        digest.update(clave.getBytes("UTF-8"));
        // con el metodo digest hashea y te coge solo esos16bites
        final SecretKeySpec key = new SecretKeySpec(
                digest.digest(), 0, 16, "AES");
        // coges instancia q viene por defecto
        final Cipher aes = Cipher.getInstance(
                "AES/ECB/PKCS5Padding");
        if (paraCifrar) {
            aes.init(Cipher.ENCRYPT_MODE, key);
        } else {
            aes.init(Cipher.DECRYPT_MODE, key);
        }

        return aes;
    }
 
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ssi_aula1;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 *
 * @author Gomes
 */

public class RSA {


static String kPublic = "";
static String kPrivate = "";

public RSA()
{

}




public static KeyPair Save2File(String text) throws NoSuchAlgorithmException,
    NoSuchPaddingException, InvalidKeyException,
    IllegalBlockSizeException, BadPaddingException, FileNotFoundException, IOException, InvalidKeySpecException {

    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
    RSAPublicKeySpec pubKeySpec = new    RSAPublicKeySpec(new BigInteger("d46f473a2d746537de2056ae3092c451", 16),new BigInteger("11", 16));
    RSAPrivateKeySpec privKeySpec = new RSAPrivateKeySpec(new BigInteger("d46f473a2d746537de2056ae3092c451", 16),new BigInteger("57791d5430d593164082036ad8b29fb1", 16));
    
    //keyFactory.generatePublic(pubKeySpec);
    //keyFactory.generatePrivate(privKeySpec)
    
    KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
    kpg.initialize(1024);
    KeyPair kp = kpg.genKeyPair();
    PublicKey publicKey = kp.getPublic();
    PrivateKey privateKey = kp.getPrivate();

    FileOutputStream fos = new FileOutputStream(text+"publicK");
    fos.write(publicKey.getEncoded());
    fos.close();

    fos = new FileOutputStream(text+"privateK");
    fos.write(privateKey.getEncoded());
    fos.close();
    return kp;
}

public static byte[] encrypt(String text, PublicKey key) {
    byte[] cipherText = null;
    try {
      // get an RSA cipher object and print the provider
      final Cipher cipher = Cipher.getInstance("RSA");
      // encrypt the plain text using the public key
      cipher.init(Cipher.ENCRYPT_MODE, key);
      cipherText = cipher.doFinal(text.getBytes());
    } catch (Exception e) {
      e.printStackTrace();
    }
    return cipherText;
  }

public static String decrypt(byte[] text, PrivateKey key) {
    byte[] dectyptedText = null;
    try {
      // get an RSA cipher object and print the provider
      final Cipher cipher = Cipher.getInstance("RSA");

      // decrypt the text using the private key
      cipher.init(Cipher.DECRYPT_MODE, key);
      dectyptedText = cipher.doFinal(text);

    } catch (Exception ex) {
      ex.printStackTrace();
    }

    return new String(dectyptedText);
  }

public static RSAPrivateKey readPrivateKey(String file) throws IOException{
    FileInputStream fis = null;
    try {
        File f = new File(file);
        fis = new FileInputStream(f);
        DataInputStream dis = new DataInputStream(fis);
        byte[] keyBytes = new byte[(int)f.length()];
        dis.readFully(keyBytes);
        dis.close();
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        RSAPrivateKey privKey = (RSAPrivateKey) kf.generatePrivate(spec);
        
        return privKey;
        
    } catch (FileNotFoundException ex) {
        Logger.getLogger(RSA.class.getName()).log(Level.SEVERE, null, ex);
    } catch (NoSuchAlgorithmException ex) {
        Logger.getLogger(RSA.class.getName()).log(Level.SEVERE, null, ex);
    } catch (InvalidKeySpecException ex) {
        Logger.getLogger(RSA.class.getName()).log(Level.SEVERE, null, ex);
    } finally {
        try {
            fis.close();
        } catch (IOException ex) {
            Logger.getLogger(RSA.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    return null;
    
}

public static void main(String[] args){
    try {
        
        KeyPair kp1 = Save2File("test1");
        KeyPair kp2 = Save2File("test2");
        
        //RSAPrivateKey ds=readPrivateKey("test2privateK");
       
        
        //RSAPrivateKey ds2=readPrivateKey("certs/server_key.pk8");
        
        
    } catch (NoSuchAlgorithmException ex) {
        Logger.getLogger(RSA.class.getName()).log(Level.SEVERE, null, ex);
    } catch (NoSuchPaddingException ex) {
        Logger.getLogger(RSA.class.getName()).log(Level.SEVERE, null, ex);
    } catch (InvalidKeyException ex) {
        Logger.getLogger(RSA.class.getName()).log(Level.SEVERE, null, ex);
    } catch (IllegalBlockSizeException ex) {
        Logger.getLogger(RSA.class.getName()).log(Level.SEVERE, null, ex);
    } catch (BadPaddingException ex) {
        Logger.getLogger(RSA.class.getName()).log(Level.SEVERE, null, ex);
    } catch (IOException ex) {
        Logger.getLogger(RSA.class.getName()).log(Level.SEVERE, null, ex);
    } catch (InvalidKeySpecException ex) {
        Logger.getLogger(RSA.class.getName()).log(Level.SEVERE, null, ex);
    }
}

}

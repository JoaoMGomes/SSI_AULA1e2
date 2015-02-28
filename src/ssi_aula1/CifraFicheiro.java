/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ssi_aula1;

import com.sun.org.apache.xerces.internal.impl.io.ASCIIReader;
import static com.sun.org.apache.xml.internal.serialize.OutputFormat.Defaults.Encoding;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import static sun.font.LayoutPathImpl.EndType.EXTENDED;
import sun.misc.BASE64Encoder;
/**
 *
 * @author Gomes
 */
public class CifraFicheiro {
    
    public static byte[] cifrarTexto(String cifra, SecretKeySpec mykey, String data) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
        
        Cipher myCipher = Cipher.getInstance(cifra);
        
        myCipher.init(Cipher.ENCRYPT_MODE,mykey);
        
        byte[] byteDataToEncrypt = data.getBytes();

        byte[] byteCipherText = myCipher.doFinal(byteDataToEncrypt); 

        String strCipherText = new BASE64Encoder().encode(byteCipherText);

        System.out.println("Cipher Text generated using RC4 is " +strCipherText);
        
        return byteCipherText;
    }
    
    public static void cifrarFicheiro(String cifra, SecretKeySpec mykey,String inputFile,String outputFile){
        
        try {
            Cipher myCipher = Cipher.getInstance(cifra);
            
            myCipher.init(Cipher.ENCRYPT_MODE,mykey);

            
            FileInputStream fis = new FileInputStream(inputFile);
            FileOutputStream fos = new FileOutputStream(outputFile);
            CipherOutputStream cos = new CipherOutputStream(fos, myCipher);
            
            byte[] block = new byte[8];
            int i;
            while ((i = fis.read(block)) != -1) {
                cos.write(block, 0, i);
            }
            cos.close();
            
        } catch (Exception ex) {   
            System.out.println("erro a cifrar para o ficheiro");
        }
        
    }
    
    
    public static String decifrarTexto(String cifra, SecretKeySpec mykey,  byte[] TextoCifrado) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException{
        
        Cipher myCipher = Cipher.getInstance(cifra);
        
        String textoDecifrado = new String();
        
        myCipher.init(Cipher.DECRYPT_MODE,mykey,myCipher.getParameters());
        byte[] byteDecryptedText = myCipher.doFinal(TextoCifrado);
        textoDecifrado = new String(byteDecryptedText);
        //System.out.println(" Decrypted Text message is " +textoDecifrado);
        return textoDecifrado;
        
    }
    
    public static void decifrarFicheiro(String cifra, SecretKeySpec mykey,String inputFile,String outputFile){
        
        try {
            Cipher myCipher = Cipher.getInstance(cifra);
            
            myCipher.init(Cipher.DECRYPT_MODE,mykey);
            
            
            
            FileInputStream fis = new FileInputStream(inputFile);
            CipherInputStream cis = new CipherInputStream(fis, myCipher);
            FileOutputStream fos = new FileOutputStream(outputFile);
            
            byte[] block = new byte[8];
            int i;
            while ((i = cis.read(block)) != -1) {
                fos.write(block, 0, i);
            }
            fos.close();
        } catch (Exception ex) {
            System.out.println("erro a decifrar para o ficheiro");
        }
    }
    
    
    public static SecretKeySpec gerarChave(String cifra, String chave){
        
        try
        { 

            SecretKeySpec mykey = new SecretKeySpec(chave.getBytes(), cifra);

            return mykey;
            //System.out.println(secretKey.toString());
        }
        catch(Exception e){
            System.out.println("Erro\n"+e.toString());
        }
        return null;
    }
    
    public static int[] manualRC4(byte[] key,byte[] plaintext){
        
        int[] S = new int[256];
        int[] T = new int[256];;
        int keylen;
        
        if (key.length < 1 || key.length > 256) {
                          System.out.println("Erro na key");
                 } else {
                         keylen = key.length;
                         for (int i = 0; i < 256; i++) {
                                 S[i] = i;
                                 T[i] = key[i % keylen];
                         }
                         int j = 0;
                         for (int i = 0; i < 256; i++) {
                                 j = (j + S[i] + T[i]) % 256;
                                 S[i] ^= S[j];
                                 S[j] ^= S[i];
                                 S[i] ^= S[j];
                         }
                 }
        
            
            
             int[] ciphertext = new int[plaintext.length];
                int i = 0, j = 0, k, t;
                for (int counter = 0; counter < plaintext.length; counter++) {
                        i = (i + 1) % 256;
                        j = (j + S[i]) % 256;
                        S[i] ^= S[j];
                        S[j] ^= S[i];
                        S[i] ^= S[j];
                        t = (S[i] + S[j]) % 256;
                        k = S[t];
                        ciphertext[counter] = plaintext[counter] ^ k;
                }
                return ciphertext;
        }
          
        

    
    
    public static void main(String[] args) throws IOException, InterruptedException {   
     
    //gerar chave RC4
    SecretKeySpec mykey = gerarChave("RC4","segredo");
    
    //cifrar ficheiro original.txt em cifrado.txt
    cifrarFicheiro("RC4",mykey,"files/original.txt","files/cifrado.txt");
    
    //decifrar ficheiro cifrado.txt para decifrado.txt
    decifrarFicheiro("RC4",mykey,"files/cifrado.txt","files/decifrado.txt");
    
    
    
    
    //Setup do RC4 MANUAL (pode haver algo erro visto existir diferença nos caracteres ASCII do java)
    /*
    byte[] key = "segredo".getBytes();
    byte[] texto = "Hello World of Encryption using RC4.\nTrata-se de um teste.\nA reprodução e venda deste ficheiro para fins comerciais não está autorizada.".getBytes();
    int [] aux = manualRC4(key,texto);
    
    byte[] cifrado = new byte[aux.length];
    
    //imprimir ficheiro encriptado
    for(int a=0;a<aux.length;a++){
        System.out.print(new Character((char)aux[a]).toString());
        cifrado[a] = (byte) (aux[a]);
    }
    
    System.out.println("\n\n\n");
    
    //imprimir ficheiro decifrado
    int [] aux2 = manualRC4(key,cifrado);
    for(int a=0;a<aux2.length;a++){
        System.out.print(new Character((char)aux2[a]).toString());
    }*/
}

   
   }
    


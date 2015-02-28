/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ssi_aula1;

import java.io.*;
import java.net.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


/**
 *
 * @author Gomes
 */
public class Client {
    
    public static void main(String [] args){
        
        int port = 12345;
        
        try
        {
          Socket client = new Socket("localhost", 12345);
          OutputStream outToServer = client.getOutputStream();
          
          //BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
          
          PrintWriter out = new PrintWriter(client.getOutputStream(), true);                   
          BufferedReader inFromServer = new BufferedReader(new InputStreamReader(client.getInputStream()));
          
          
          //////     
          //            RC4
          //
          /*CifraFicheiro cf = new CifraFicheiro();
          Cipher myCipher = Cipher.getInstance("RC4");
          SecretKeySpec mykey = cf.gerarChave("RC4", "segredo");
          myCipher.init(Cipher.ENCRYPT_MODE,mykey);
          
          FileOutputStream fos = new FileOutputStream("files/codigo");
          */
          
          //
          //            AES
          //
          SecretKeySpec keyspec = new SecretKeySpec("0123456789abcdef".getBytes("UTF-8"), "AES");
          SecureRandom random = new SecureRandom();
          
          //Enviar IV para servidor
          byte ivEnviado[] = new byte[16];
          random.nextBytes(ivEnviado);
          //System.out.println(ivEnviado);
          IvParameterSpec ivspec = new IvParameterSpec(ivEnviado);
          client.getOutputStream().write(ivspec.getIV());
          

          Cipher myCipher = Cipher.getInstance("AES/CBC/NoPadding");
          myCipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
          //teste conexão
          
          String aux= inFromServer.readLine();
          
          
          while(!aux.startsWith("Connected")){
              System.out.println(aux);
              aux= inFromServer.readLine();
          }
          System.out.println(aux);
          
          
          //conexão estabelecidade
          
          
          CipherOutputStream cos = new CipherOutputStream(client.getOutputStream(), myCipher);
          
          int test;
        while((test=System.in.read())!=-1) {
            //System.out.println("enviei");
            cos.write((byte)test);
            cos.flush();
           
        }
        
//          while(true){
//          String sentence = inFromUser.readLine();
//          out.println(sentence);
//          System.out.println(sentence);
//          }
          //client.close();
        }
        catch(IOException e)
        {
            System.out.println("fehcouud");
          e.printStackTrace();
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchPaddingException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeyException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidAlgorithmParameterException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}


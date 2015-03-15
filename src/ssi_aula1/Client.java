/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ssi_aula1;

import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyAgreement;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author Gomes
 */
public class Client {

    public static void main(String[] args) throws InvalidKeySpecException {
        BigInteger p = new BigInteger("99494096650139337106186933977618513974146274831566768179581759037259788798151499814653951492724365471316253651463342255785311748602922458795201382445323499931625451272600173180136123245441204133515800495917242011863558721723303661523372572477211620144038809673692512025566673746993593384600667047373692203583");
        BigInteger g = new BigInteger("44157404837960328768872680677686802650999163226766694797650810379076416463147265401084491113667624054557335394761604876882446924929840681990106974314935015501571333024773172440352475358750668213444607353872754650805031912866692119819377041901642732455911509867728218394542745330014071040326856846990119719675");
        DHParameterSpec dhspec = new DHParameterSpec(p, g, 1024);
        try {
            Socket client = new Socket("localhost", 12348);
            OutputStream outToServer = client.getOutputStream();
//BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
            PrintWriter out = new PrintWriter(client.getOutputStream(), true);
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(client.getInputStream()));
//teste conexão
            String aux = inFromServer.readLine();
            while (!aux.startsWith("Connected")) {
                System.out.println(aux);
                aux = inFromServer.readLine();
            }
            System.out.println(aux);
//conexão estabelecida
//DIFFIE HELLMAN
  //          ObjectOutputStream outToServerObj = new ObjectOutputStream(client.getOutputStream());
//            ObjectInputStream inFromServerObj = new ObjectInputStream(client.getInputStream());
            //outToServerObj.flush();
            //outToServerObj.reset();
//cliente escolhe numero random
            KeyPairGenerator clienteKeyGen = KeyPairGenerator.getInstance("DH");
            clienteKeyGen.initialize(dhspec, new SecureRandom());
//computacao
            KeyAgreement clienteKeyAgree = KeyAgreement.getInstance("DH");
            KeyPair clientePair = clienteKeyGen.genKeyPair();
//
            clienteKeyAgree.init(clientePair.getPrivate());

//            System.out.println("prontos");
//            outToServerObj.writeObject(clientePair.getPublic());
//            System.out.println("enviei");
//            PublicKey serverpk = (PublicKey) inFromServerObj.readObject();
//            System.out.println("recebi");
//            Key clienteKey = clienteKeyAgree.doPhase(serverpk, true);
//            outToServerObj.flush();
             //enviar cenix
            
            
            
          byte[] keyBytes = new byte[425];
          client.getOutputStream().write(clientePair.getPublic().getEncoded());
          client.getOutputStream().flush();
          //System.out.println("tamanho"+clientePair.getPublic().getEncoded().length);
         // System.out.println(Arrays.toString(clientePair.getPublic().getEncoded()));
          //receber cenix
          client.getInputStream().read(keyBytes,0,425);
          //transformar cenix
          PublicKey serverpk = KeyFactory.getInstance("DH").generatePublic(new X509EncodedKeySpec(keyBytes));
          Key clienteKey = clienteKeyAgree.doPhase(serverpk, true);
//outToServerObj.close();
          
          
          
          
          
          
          
            MessageDigest hash = MessageDigest.getInstance("SHA1");
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] baux = clienteKeyAgree.generateSecret();
            outputStream.write(baux);
            outputStream.write('1');
            byte[] k1 = outputStream.toByteArray();
            outputStream.reset();
            outputStream.write(baux);
            outputStream.write('2');
            byte[] k2 = outputStream.toByteArray();
            byte[] k1h = hash.digest(k1);
            byte[] k2h = hash.digest(k2);
            System.out.println("chavegeradak1 : " + Arrays.toString(k1h));
            System.out.println("chavegeradak2 : " + Arrays.toString(k2h));
//criar mac
            SecretKeySpec signingKey = new SecretKeySpec(k2h, "HmacSHA1");
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(signingKey);
// byte[] mymac = mac.doFinal(k2h);
// String macAsString = new sun.misc.BASE64Encoder().encode(mymac);
// System.out.println("\n\n"+Arrays.toString(mymac));
// Construcao cifra
//////
// RC4
//
/*CifraFicheiro cf = new CifraFicheiro();
             Cipher myCipher = Cipher.getInstance("RC4");
             SecretKeySpec mykey = cf.gerarChave("RC4", "segredo");
             myCipher.init(Cipher.ENCRYPT_MODE,mykey);
             FileOutputStream fos = new FileOutputStream("files/codigo");
             */
//
// AES
//
            SecretKeySpec keyspec = new SecretKeySpec(Arrays.copyOfRange(k1h, 0, 16), "AES");
            SecureRandom random = new SecureRandom();
//Enviar IV para servidor
            byte ivEnviado[] = new byte[16];
            random.nextBytes(ivEnviado);
//System.out.println(ivEnviado);
            IvParameterSpec ivspec = new IvParameterSpec(ivEnviado);
            client.getOutputStream().write(ivspec.getIV());
            Cipher myCipher = Cipher.getInstance("AES/CFB8/NoPadding");
           // Cipher myCipherDec = Cipher.getInstance("AES/CFB8/NoPadding");
            myCipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
           // myCipherDec.init(Cipher.DECRYPT_MODE, keyspec, ivspec);
            
            CipherOutputStream cos = new CipherOutputStream(client.getOutputStream(), myCipher);
           // CipherInputStream cis = new CipherInputStream(client.getInputStream(), myCipherDec);
            
          //  Scanner snin = new Scanner(cis);
            //System.out.println(snin.nextLine());
            
            int nSeq=1;
            byte[] r = new byte[1024];
            Scanner sc = new Scanner(System.in);
            String string;
            while (true) {
                string = sc.nextLine() + "\n";
                
                
                cos.write(string.getBytes());
                
                cos.flush();
                client.getOutputStream().flush();
                
                byte[] mymac = mac.doFinal((string+nSeq).getBytes());
                nSeq++;
                String mymacS = Arrays.toString(mymac)+"\n";
                cos.write(mymacS.getBytes());
                System.out.print(mymacS);

                cos.flush();
                client.getOutputStream().flush();
                
                //client.getOutputStream().write(mymac);
                //client.getOutputStream().flush();

//cos.write(mymac);
            }
// while((System.in.read(r))!=-1) {
// System.out.println("tamanho :"+r.length);
// System.out.println( "bgin : " + new String(r, "UTF-8"));
//// for(int i=0;i<r.length;i++){
//// if(r[i]=='\n')
//// r[i]='\0';
//// }
// cos.write(r);
// //System.out.println( "end : "+new String(r, "UTF-8"));
// //cos.write(mymac);
// cos.flush();
//
//
// }
// while(true){
// String sentence = inFromUser.readLine();
// out.println(sentence);
// System.out.println(sentence);
// }
//client.close();
        } catch (IOException e) {
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

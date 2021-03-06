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
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.*;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author Gomes
 */
public class Server {

    static int NClients = 0;
    static int MAXClients = 30;

    final static ReentrantLock lock = new ReentrantLock();
    static String qql = "";

    static int orderNumber = 1;
    static ServerSocket serverSocket;

    public Server(int portNumber) throws IOException {
        this.serverSocket = new ServerSocket(portNumber);
    }

    public static class Reader extends Thread {

        int mynumber;
        CipherInputStream cis;
        Socket clientSocket;

        private Reader(int orderNumber, Socket clientSocket) {
            this.mynumber = orderNumber;
            this.clientSocket = clientSocket;
        }

        @SuppressWarnings("empty-statement")
        public void run() {
            {
                String inputLine = "";
                try {
                    BigInteger p = new BigInteger("99494096650139337106186933977618513974146274831566768179581759037259788798151499814653951492724365471316253651463342255785311748602922458795201382445323499931625451272600173180136123245441204133515800495917242011863558721723303661523372572477211620144038809673692512025566673746993593384600667047373692203583");
                    BigInteger g = new BigInteger("44157404837960328768872680677686802650999163226766694797650810379076416463147265401084491113667624054557335394761604876882446924929840681990106974314935015501571333024773172440352475358750668213444607353872754650805031912866692119819377041901642732455911509867728218394542745330014071040326856846990119719675");
                    DHParameterSpec dhspec = new DHParameterSpec(p, g, 1024);

                    //
                    //
                    //
                    //      DIFFIE - HELLMAN
                    //
                    //
                    KeyPairGenerator serverKeyGen = KeyPairGenerator.getInstance("DH");
                    serverKeyGen.initialize(dhspec, new SecureRandom());
                    
                    KeyAgreement serverKeyAgree = KeyAgreement.getInstance("DH");
                    KeyPair serverPair = serverKeyGen.genKeyPair();
                    serverKeyAgree.init(serverPair.getPrivate());
                    
                    byte[] keyBytes = new byte[425];
                    clientSocket.getInputStream().read(keyBytes, 0, 425);

                    clientSocket.getOutputStream().write(serverPair.getPublic().getEncoded());
                    clientSocket.getOutputStream().flush();
                    
                    
                              
                    //System.out.println(Arrays.toString(keyBytes));
                    ByteArrayOutputStream ops = new ByteArrayOutputStream();
                    ops.write(keyBytes);
                    PublicKey serverpk = KeyFactory.getInstance("DH").generatePublic(new X509EncodedKeySpec(ops.toByteArray()));

                    Key svKey = serverKeyAgree.doPhase(serverpk, true);

//                  //
                    //
                    //
                    //      Autentica canal 
                    //      gera k1 -> para parametrizar chave simetrica
                    //      gera k2 -> para hmac
                    //
                    //
                    MessageDigest hash = MessageDigest.getInstance("SHA1");

                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    byte[] baux = serverKeyAgree.generateSecret();
                    outputStream.write(baux);
                    outputStream.write('1');
                    byte[] k1 = outputStream.toByteArray();

                    outputStream.reset();
                    outputStream.write(baux);
                    outputStream.write('2');
                    byte[] k2 = outputStream.toByteArray();

                    byte[] k1h = hash.digest(k1);
                    byte[] k2h = hash.digest(k2);
                    //System.out.println("chavegeradak1 : " + Arrays.toString(k1h));
                    //System.out.println("chavegeradak2 : " + Arrays.toString(k2h));

                    SecretKeySpec signingKey = new SecretKeySpec(k2h, "HmacSHA1");
                    Mac mac = Mac.getInstance("HmacSHA1");
                    mac.init(signingKey);
                    
                    
                    //
                    //
                    //
                    //      AES e anterior recepcao de IV
                    //
                    //
                    SecretKeySpec keyspec = new SecretKeySpec(Arrays.copyOfRange(k1h, 0, 16), "AES");

                    ///Receber IV do cliente
                    byte ivRecebido[] = new byte[16];
                    clientSocket.getInputStream().read(ivRecebido);
                   // System.out.println(ivRecebido);

                    IvParameterSpec ivspec = new IvParameterSpec(ivRecebido);

                    Cipher myCipher = Cipher.getInstance("AES/CFB8/NoPadding");
                    myCipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);
                   
                    cis = new CipherInputStream(clientSocket.getInputStream(), myCipher);

                    int nSeq = 1;
                    int aux = 0;
                    byte[] r = new byte[25];
                    Scanner sc = new Scanner(cis);

                    while (true) {

                        String string = sc.nextLine() + "\n";
                        
                        String clientMacS = sc.nextLine();
                        //System.out.println(clientMacS);

                        byte[] clientmac = mac.doFinal((string + nSeq).getBytes());

                        if (clientMacS.equals(Arrays.toString(clientmac))) {
                            System.out.print(this.mynumber + " : " + string);
                            nSeq++;
                        } else {
                            System.out.println(this.mynumber + " ->chegou mensagem que foi ignorada");
                        }


                    }


                } catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                } catch (NoSuchElementException ex) {
                    System.out.println("[ " + this.mynumber + " ]");
                    decrementClients();
                    synchronized (qql) {
                        qql.notify();
                    }
                } catch (NoSuchAlgorithmException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                } catch (NoSuchPaddingException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InvalidKeyException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InvalidAlgorithmParameterException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InvalidKeySpecException ex) {
                    //Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                    System.out.println("Aconteceu um erro a ligar o utlizador "+ this.mynumber + " por isso foi desconectado");
                    System.out.println("[ " + this.mynumber + " ]");
                    decrementClients();
                    try {
                        clientSocket.close();
                    } catch (IOException ex1) {
                       // Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex1);
                    }
                    synchronized (qql) {
                        qql.notify();
                    }
                }
            }
        }
    }

    public static synchronized void incrementClients() {
        NClients++;
    }

    public static synchronized void decrementClients() {
        NClients--;
    }

    public static synchronized boolean CheckClientsOk() {
        if (NClients >= MAXClients) {
            return false;
        } else {
            return true;
        }

    }

    public static void main(String[] args) throws IOException, InterruptedException {

        serverSocket = new ServerSocket(12348);

        while (true) {
            try {
                
                //
                //
                //
                //      ESTABELECER LIGACAO
                //
                //
                Socket clientSocket = serverSocket.accept();
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                String inputLine;

                if (!CheckClientsOk()) {
                    out.println("Too much clients...waiting for your turn");
                    synchronized (qql) {
                        qql.wait();
                    }
                    out.println("Found a place for you !");
                }

                incrementClients();

                out.println("Connected ! Your order number is " + orderNumber);
                System.out.println("O utilizador " + orderNumber+ " acabou de se juntar ao chat");

                Thread t = new Reader(orderNumber, clientSocket);
                t.start();
                //espera antes de novo cliente
               // Thread.sleep(5000);
                orderNumber++;

//                    while ((inputLine = in.readLine()) != null) 
//                        {
//                        System.out.println(inputLine);
//                        }
            } catch (IOException e) {
                System.out.println("Exception caught when trying to listen on port or listening for a connection");
                System.out.println(e.getMessage());
            }
        }
    }
}

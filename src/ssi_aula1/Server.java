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
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
/**
 *
 * @author Gomes
 */
public class Server{
    
    static int NClients=0; 
    static int MAXClients=3;
    
    final static ReentrantLock lock = new ReentrantLock();
    static String qql = "";
    
    static int orderNumber=1;
    static ServerSocket serverSocket; 
    
    public Server(int portNumber) throws IOException{
        this.serverSocket= new ServerSocket(portNumber);        
    }
    
    public static class Reader extends Thread{
        
        BufferedReader in;
        int mynumber;
        InputStream is;
        CipherInputStream cis;
        Socket clientSocket;
        
        public Reader(BufferedReader bf, int number){
            this.in=bf;
            this.mynumber=number;
        }
        
        public Reader(BufferedReader bf, int number, Socket clientSocket){
            this.in=bf;
            this.mynumber=number;
            this.clientSocket=clientSocket;
        }
        
        public void run(){
            {
                String inputLine="";
                try 
                {
//                    while ((inputLine = in.readLine()) != null)
//                    {
//                        if(inputLine.equals("null"))
//                            throw new ExitException();
//                        else
//                            System.out.println(this.mynumber+" : "+inputLine+"#");
//                    }
                    
                    //                          
                    //                  RC4
                    //
                    /*Cipher myCipher = Cipher.getInstance("RC4");
                    CifraFicheiro cf = new CifraFicheiro();

                    SecretKeySpec mykey = cf.gerarChave("RC4", "segredo");
                    myCipher.init(Cipher.DECRYPT_MODE,mykey);
                    */
                    //
                    //                  AES
                    //
                    SecretKeySpec keyspec = new SecretKeySpec("0123456789abcdef".getBytes("UTF-8"), "AES");
                    
                    ///Receber IV do cliente
                    byte ivRecebido[] = new byte[16];
                    System.out.println(clientSocket.getInputStream().read(ivRecebido));
                    System.out.println(ivRecebido);
                    
                    IvParameterSpec ivspec = new IvParameterSpec(ivRecebido);

                    Cipher myCipher = Cipher.getInstance("AES/CBC/NoPadding");
                    myCipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);
                    System.out.println("block size is "+myCipher.getBlockSize());
                    
                    
                    
                    cis = new CipherInputStream(clientSocket.getInputStream(), myCipher);
                    
                    
                    int test;
                    int aux=0;
                    while ((test=cis.read()) != -1) {
                        
                        if(aux==0){
                            System.out.print(this.mynumber+ " : ");aux++;}
                    if((byte)test == (byte)'\n')
                         aux=0;
                    System.out.print((char) test);
                    
                    
                    }
                    
                    
                } 
                catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                     
                } catch (NoSuchAlgorithmException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                } catch (NoSuchPaddingException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InvalidKeyException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InvalidAlgorithmParameterException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
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
    
    public static synchronized boolean CheckClientsOk(){
         if(NClients>=MAXClients)
             return false;
         else
             return true;
         
    }
    
    
    public static void main(String[] args) throws IOException, InterruptedException {
          
        serverSocket= new ServerSocket(12345);
        
        
        while(true)
            {
                try {
                    Socket clientSocket = serverSocket.accept(); 
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);                   
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    String inputLine;
                    
                    if(!CheckClientsOk()){
                        out.println("Too much clients...waiting for your turn");
                        synchronized(qql){ qql.wait(); }
                        out.println("Found a place for you !");
                    }
                    
                    
                    
                    
                    incrementClients();
                    
                    out.println("Connected ! Your order number is "+orderNumber);
                   
                    
                    Thread t = new Reader(in,orderNumber,clientSocket);
                    t.start();
                    
                    orderNumber++;
                    
//                    while ((inputLine = in.readLine()) != null) 
//                        {
//                        System.out.println(inputLine);
//                        }
                    }
                catch (IOException e) {
                    System.out.println("Exception caught when trying to listen on port or listening for a connection");
                    System.out.println(e.getMessage());
                }
            }          
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ssi_aula1;

import java.math.BigInteger;
import java.security.AlgorithmParameterGenerator;
import java.security.AlgorithmParameters;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import javax.crypto.KeyAgreement;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.DHPublicKeySpec;

/**
 *
 * @author Gomes
 */
public class Diffie_Hellman {
  
  public final static int XaValue = 9;

  public final static int XbValue = 14;

  public static void main(String[] args) throws Exception {
      
    //Parametros fixos
    BigInteger p = new BigInteger("99494096650139337106186933977618513974146274831566768179581759037259788798151499814653951492724365471316253651463342255785311748602922458795201382445323499931625451272600173180136123245441204133515800495917242011863558721723303661523372572477211620144038809673692512025566673746993593384600667047373692203583");
    BigInteger g = new BigInteger("44157404837960328768872680677686802650999163226766694797650810379076416463147265401084491113667624054557335394761604876882446924929840681990106974314935015501571333024773172440352475358750668213444607353872754650805031912866692119819377041901642732455911509867728218394542745330014071040326856846990119719675");
    BigInteger Xa = new BigInteger(Integer.toString(XaValue));
    BigInteger Xb = new BigInteger(Integer.toString(XbValue));
    DHParameterSpec dhspec = new DHParameterSpec(p, g, 1024);
    
    ///       Parametros aleatorios
    AlgorithmParameterGenerator pgen=AlgorithmParameterGenerator.getInstance("DH");
    pgen.init(1024);
    AlgorithmParameters params=pgen.generateParameters();
    //DHParameterSpec dhspec=(DHParameterSpec)params.getParameterSpec(DHParameterSpec.class);
    
    
    System.out.println("P :"+dhspec.getP());
    System.out.println("G :"+dhspec.getG());
    System.out.println("L :"+dhspec.getL());
    
    
    //Alice Choose random number
    KeyPairGenerator aliceKeyGen = KeyPairGenerator.getInstance("DH");
    aliceKeyGen.initialize(dhspec,new SecureRandom());
    
    //bob choose random number
    KeyPairGenerator bobKeyGen = KeyPairGenerator.getInstance("DH");
    bobKeyGen.initialize(dhspec,new SecureRandom());
    
    
    //alice computed
    KeyAgreement aliceKeyAgree = KeyAgreement.getInstance("DH");
    KeyPair alicePair = aliceKeyGen.genKeyPair();
    
    //bob computed
    KeyAgreement bobKeyAgree = KeyAgreement.getInstance("DH");
    KeyPair bobPair = bobKeyGen.genKeyPair();
    
    aliceKeyAgree.init(alicePair.getPrivate());
    Key aliceKey = aliceKeyAgree.doPhase(bobPair.getPublic(), true);
//    System.out.println(alicePair.getPublic());
//    System.out.println(bobPair.getPublic());

    bobKeyAgree.init(bobPair.getPrivate());
    Key bobKey = bobKeyAgree.doPhase(alicePair.getPublic(), true);
    
    
    MessageDigest hash = MessageDigest.getInstance("SHA1");
    byte[] aliceSharedSecret = hash.digest(aliceKeyAgree.generateSecret());
    byte[] bobSharedSecret = hash.digest(bobKeyAgree.generateSecret());    

    System.out.println("alicesharedsecret : "+Arrays.toString(aliceSharedSecret));
    System.out.println("bobsharedsecret : "+Arrays.toString(bobSharedSecret));
    
  }
}

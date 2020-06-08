/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package security;

/**
 *
 * @author huixin_zhan
 */
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.Cipher;
import java.io.*;

import java.security.MessageDigest;

import java.util.Queue;
import java.util.LinkedList;




class MBRC {
	private int maxCount;
	private int messageCount;
        private int replyCount;
        private int maxReplyCount;
        private Boolean messageBufferFull = false;
        private Boolean responseBufferFull = false;

	Queue<String> buffer = new LinkedList<>();
        Queue<String> resbuffer = new LinkedList<>();

	MBRC() {
		this.maxCount = 1; // queue size is 3.
		this.messageCount = 0;
                this.replyCount = 0;
                this.maxReplyCount = 1;
	}

	public synchronized String send(String text, int message) {
	
		String str = text + " " + message;
		buffer.add(str); // place message in buffer
		messageCount++; // Increment messageCount
		System.out.println("(\""+text+"\","+message+")"+" is placed in queue buffer.");
                if (messageCount == maxCount){
                        messageBufferFull = true;
                }
		if (messageCount == maxCount) {
			notifyAll(); // send signal
		}
                
                while (responseBufferFull == false) {
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}
		String restr = resbuffer.remove(); // remove reply from buffer
		replyCount--; // Decrement replyCount
                responseBufferFull = false;
                return restr;
	}
        
        public synchronized String sendEn(String text, String message) {
	
		String str = text + " " + message;
		buffer.add(str); // place message in buffer
		messageCount++; // Increment messageCount
		System.out.println("(\""+text+"\","+message+")"+" is placed in queue buffer.");
                if (messageCount == maxCount){
                        messageBufferFull = true;
                }
		if (messageCount == maxCount) {
			notifyAll(); // send signal
		}
                
                while (responseBufferFull == false) {
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}
		String restr = resbuffer.remove(); // remove reply from buffer
		replyCount--; // Decrement replyCount
                responseBufferFull = false;
                return restr;
	}
        
	public synchronized String receive() {
		//while (messageCount == 0) {
                while (messageBufferFull == false) {
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}
		String message = buffer.remove(); // remove message from buffer
		messageCount--; // Decrement messageCount
                messageBufferFull = false;
		
		return message;
	}
        
        public synchronized int reply(String text, int res) {
            String rstr = text + " " + res;
            resbuffer.add(rstr); // place reply in buffer
            replyCount++;
            //System.out.println("Received Response is " + res);
            if (replyCount == maxReplyCount){
                        responseBufferFull = true;
                }
	    if (replyCount == maxReplyCount) {
			notifyAll(); // send signal
		}
            return res;
        }
}

class Sender extends Thread {
	private MBRC MessageQueue;

	public Sender(MBRC q) {
		MessageQueue = q;
	}
        
        static byte[] encrypt(byte s[], Cipher c, SecretKey sk) throws Exception
	{
	    c.init(Cipher.ENCRYPT_MODE, sk);
	    return c.doFinal(s);
	}
        
        public static Boolean verify(byte[] hashValue, String msg) throws Exception
        {
	   MessageDigest md = MessageDigest.getInstance("SHA-1");
	   byte[] msgBytes = msg.getBytes();
	   md.update(msgBytes);
	   byte[] mdBytes = md.digest();
	  
	   if (MessageDigest.isEqual(hashValue, mdBytes))
		   return true;
	   else
		   return false;
	}

        
	public void run() {
            
            try{
            
		//System.out.println("Producer is sending (\"add\",4).");
                String recres = "response 000";
                
               
                //byte[] cleartext;
                
                KeyGenerator keygen = KeyGenerator.getInstance("DES");
		SecretKey desKey = keygen.generateKey();
		Cipher desCipher;	
		desCipher = Cipher.getInstance("DES/ECB/PKCS5Padding"); //Electronic Code Book mode
                
         
                        
                //recres = MessageQueue.send("add", 4);
                
                String str = "4";
		byte[] cleartext = str.getBytes();
	   	String str1 = new String(cleartext);
	   	System.out.println("The string you entered is: " + str1);
                // The sender encrpt the data	    
	   	byte[] ciphertext = encrypt(cleartext, desCipher, desKey);
                String str2 = new String(ciphertext);
	   	System.out.println("The encrypted data is: " + str2);
                
                MBRC q = new MBRC();
                Receiver h = new Receiver(q);
		byte hashValue[]= h.generate(str1);
		System.out.println("---------------------------------The message digest of your input is : " + hashValue);
                if (verify(hashValue, str1)){
		   System.out.println("Message is valid and not corrupted");
                    recres = MessageQueue.sendEn("add", str2);
                    System.out.println(recres);}
                else{
                    System.out.println("Message has been corrupted");}
                
                //recres = MessageQueue.sendEn("add", str2);
                //System.out.println(recres);

		//System.out.println("Producer is sending (\"multiply\",1).");
                str = "1";
		cleartext = str.getBytes();
	   	str1 = new String(cleartext);
	   	System.out.println("The string you entered is: " + str1);
                // The sender encrpt the data	    
	   	ciphertext = encrypt(cleartext, desCipher, desKey);
                str2 = new String(ciphertext);
	   	System.out.println("The encrypted data is: " + str2);
		//recres = MessageQueue.sendEn("multiply", str2);
                //System.out.println(recres);
                
     
                hashValue = h.generate(str1);
		System.out.println("---------------------------------The message digest of your input is : " + hashValue);
                if (verify(hashValue, str1)){
		   System.out.println("Message is valid and not corrupted");
                    recres = MessageQueue.sendEn("multiply", str2);
                    System.out.println(recres);}
                else{
                    System.out.println("Message has been corrupted");}
                

		//System.out.println("Producer is sending (\"multiply\",8).");
                str = "8";
		cleartext = str.getBytes();
	   	str1 = new String(cleartext);
	   	System.out.println("The string you entered is: " + str1);
                // The sender encrpt the data	    
	   	ciphertext = encrypt(cleartext, desCipher, desKey);
                str2 = new String(ciphertext);
	   	System.out.println("The encrypted data is: " + str2);
		//recres = MessageQueue.sendEn("multiply", str2);
                //System.out.println(recres);
                hashValue = h.generate(str1);
		System.out.println("---------------------------------The message digest of your input is : " + hashValue);
                if (verify(hashValue, str1)){
		   System.out.println("Message is valid and not corrupted");
                    recres = MessageQueue.sendEn("multiply", str2);
                    System.out.println(recres);}
                else{
                    System.out.println("Message has been corrupted");}
                
                
                str = "2";
		cleartext = str.getBytes();
	   	str1 = new String(cleartext);
	   	System.out.println("The string you entered is: " + str1);
                // The sender encrpt the data	    
	   	ciphertext = encrypt(cleartext, desCipher, desKey);
                str2 = new String(ciphertext);
	   	System.out.println("The encrypted data is: " + str2);
		//System.out.println("Producer is sending (\"add\",2).");
		//recres = MessageQueue.sendEn("add", str2);
                //System.out.println(recres);
                hashValue = h.generate(str1);
		System.out.println("---------------------------------The message digest of your input is : " + hashValue);
                if (verify(hashValue, str1)){
		   System.out.println("Message is valid and not corrupted");
                    recres = MessageQueue.sendEn("add", str2);
                    System.out.println(recres);}
                else{
                    System.out.println("Message has been corrupted");}

                
                str = "3";
		cleartext = str.getBytes();
	   	str1 = new String(cleartext);
	   	System.out.println("The string you entered is: " + str1);
                // The sender encrpt the data	    
	   	ciphertext = encrypt(cleartext, desCipher, desKey);
                str2 = new String(ciphertext);
	   	System.out.println("The encrypted data is: " + str2);
		//System.out.println("Producer is sending (\"add\",3).");
		//recres = MessageQueue.sendEn("add", str2);
                //System.out.println(recres);
                hashValue = h.generate(str1);
		System.out.println("---------------------------------The message digest of your input is : " + hashValue);
                if (verify(hashValue, str1)){
		   System.out.println("Message is valid and not corrupted");
                    recres = MessageQueue.sendEn("add", str2);
                    System.out.println(recres);}
                else{
                    System.out.println("Message has been corrupted");}
                
                
                str = "99";
		cleartext = str.getBytes();
	   	str1 = new String(cleartext);
	   	System.out.println("The string you entered is: " + str1);
                // The sender encrpt the data	    
	   	ciphertext = encrypt(cleartext, desCipher, desKey);
                str2 = new String(ciphertext);
	   	System.out.println("The encrypted data is: " + str2);
		//System.out.println("Producer is sending (\"add\",99).");
		//recres = MessageQueue.sendEn("add", str2);
                //System.out.println(recres);
                hashValue = h.generate(str1);
		System.out.println("---------------------------------The message digest of your input is : " + hashValue);
                if (verify(hashValue, str1)){
		   System.out.println("Message is valid and not corrupted");
                    recres = MessageQueue.sendEn("add", str2);
                    System.out.println(recres);}
                else{
                    System.out.println("Message has been corrupted");}
                
                

                str = "53";
		cleartext = str.getBytes();
	   	str1 = new String(cleartext);
	   	System.out.println("The string you entered is: " + str1);
                // The sender encrpt the data	    
	   	ciphertext = encrypt(cleartext, desCipher, desKey);
                str2 = new String(ciphertext);
	   	System.out.println("The encrypted data is: " + str2);
		//System.out.println("Producer is sending (\"multiply\",53).");
		//recres = MessageQueue.sendEn("multiply", str2);
                //System.out.println(recres);
                hashValue = h.generate(str1);
		System.out.println("---------------------------------The message digest of your input is : " + hashValue);
                if (verify(hashValue, str1)){
		   System.out.println("Message is valid and not corrupted");
                    recres = MessageQueue.sendEn("multiply", str2);
                    System.out.println(recres);}
                else{
                    System.out.println("Message has been corrupted");}
                
                
                str = "0";
		cleartext = str.getBytes();
	   	str1 = new String(cleartext);
	   	System.out.println("The string you entered is: " + str1);
                // The sender encrpt the data	    
	   	ciphertext = encrypt(cleartext, desCipher, desKey);
                str2 = new String(ciphertext);
	   	System.out.println("The encrypted data is: " + str2);
		//System.out.println("Producer is sending (\"end\",0).");
		recres = MessageQueue.sendEn("end", str2);
                System.out.println(recres);
                hashValue = h.generate(str1);
		System.out.println("---------------------------------The message digest of your input is : " + hashValue);
                if (verify(hashValue, str1)){
		   System.out.println("Message is valid and not corrupted");
                    recres = MessageQueue.sendEn("add", str2);
                    System.out.println(recres);}
                else{
                    System.out.println("Message has been corrupted");}
        }

		//try {
		//	sleep((int) (Math.random() * 100));
		//} 
            catch (Exception e) {
                e.printStackTrace();
		}                       
	}
        
        public void printReceivedResult(){
                MBRC q = new MBRC();
		Sender p1 = new Sender(q);
                p1.start();
            
        }
        public void printMessage(){
                System.out.println("Sender is sending (\"add\",4).");
		//MessageQueue.send("add", 4);

		System.out.println("Sender is sending (\"multiply\",1).");
		//MessageQueue.send("multiply", 1);

		System.out.println("Sender is sending (\"multiply\",8).");
		//MessageQueue.send("multiply", 8);

		System.out.println("Sender is sending (\"add\",2).");
		//MessageQueue.send("add", 2);

		System.out.println("Sender is sending (\"add\",3).");
		//MessageQueue.send("add", 3);

		System.out.println("Sender is sending (\"add\",99).");
		//MessageQueue.send("add", 99);

		System.out.println("Sender is sending (\"multiply\",53).");
		//MessageQueue.send("multiply", 53);

		System.out.println("Sender is sending (\"end\",0).");
		//MessageQueue.send("end", 0);

		try {
			sleep((int) (Math.random() * 100));
		} catch (InterruptedException e) {
		}
        }
}

class Receiver extends Thread {
	private MBRC MessageQueue;

	public Receiver(MBRC q) {
		MessageQueue = q;
	}
        
        static byte[] decrypt(byte s[], Cipher c, SecretKey sk) throws Exception
	{
	    c.init(Cipher.DECRYPT_MODE, sk);
	    return c.doFinal(s);
	}
        
        public static byte[] generate(String msg) throws Exception
	{
	   MessageDigest md = MessageDigest.getInstance("SHA-1");
	   byte[] message = msg.getBytes();
	   md.update(message);
	   byte[] mdbytes = md.digest();
	   return(mdbytes);
	}
        
        public void printResult(){
                String str = null;
		int number;
		String[] words = null;
		int res = 0;
                
                try{
                KeyGenerator keygen = KeyGenerator.getInstance("DES");
		SecretKey desKey = keygen.generateKey();
		Cipher desCipher;	
		desCipher = Cipher.getInstance("DES/ECB/PKCS5Padding"); //Electronic Code Book mode
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Enter the data: ");
                String[] strr;
		strr = new String[]{"4","1","8","2","3","99","53","0","0","\\"};

		for (int i = 0; i < 10; i++) {
                        byte[] cleartext = strr[i].getBytes();
                        String str1 = new String(cleartext);
                        System.out.println("The string you entered is: " + str1);
                        
                        
                        
                        MBRC q = new MBRC();
                        Sender q1 = new Sender(q);
                        Receiver h = new Receiver(q);
                        byte hashValue[]= h.generate(str1);
          
                        System.out.println("The message digest of your input is : " + hashValue);
                        byte[] ciphertext = q1.encrypt(cleartext, desCipher, desKey);
                        String str2 = new String(ciphertext);
                        System.out.println("----------------------The encrypted data from the sender is:-------------------- " + ciphertext);
 
                        byte[] plaintext = decrypt(ciphertext, desCipher, desKey);
                        String str3 = new String(plaintext);
                        System.out.println("The data after decryption is: " + str3);
			str = MessageQueue.receive();
			System.out.println("Receiver received : " + str);
			words = str.split(" ");
                       
                        
			number = Integer.parseInt(str3);

			if (words[0].equalsIgnoreCase("add")) {
				AddCalculation a = new AddCalculation();
				res = a.add(number);
			} else if (words[0].equalsIgnoreCase("multiply")) {
				MultiplyCalculation m = new MultiplyCalculation();
				res = m.multiply(number);
			} else if (words[0].equalsIgnoreCase("end") && number == 0) {
				System.out.println("------Program terminates here -----------");
				System.exit(0);
			}
                        MessageQueue.reply("response", res);
			System.out.println("----> Result of " + "(\"" + words[0] + "\"," + number + ") is " + res);
		}
                }
                catch (Exception e){
	 		e.printStackTrace();
	 	}
        }
	public void run() {

	}
}

class AddCalculation {

	public int add(int i) {
		int sum = 0;
		sum = 10 + i;
		return sum;
	}
}

class MultiplyCalculation {
	public int multiply(int i) {
		int mul = 0;
		mul = 10 * i;
		return mul;
	}
}

public class Security {
	public static void main(String[] args) {
		System.out.println("Program: To perform addition and multiplication operation on integer 10.");
		System.out.println("------------------------------------------------------------------------");
		MBRC q = new MBRC();
		Sender p1 = new Sender(q);
		Receiver c1 = new Receiver(q);
                
                
                
		//DES has Electronic Code Book, Ciphertext Block Chaining, Cipher FeedBack, Output Feedback modes
                p1.start();
                p1.printMessage();
                c1.printResult();
                //p1.printReceivedResult();
                System.out.println("Program: An example about using confidentiality security from sender to the receiver.");
                System.out.println("------------------------------------------------------------------------");

	}
}

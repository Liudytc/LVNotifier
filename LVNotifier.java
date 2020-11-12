package loop;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.select.*;

import javax.mail.*;
import javax.mail.internet.*;    
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

//import com.sun.java.util.jar.pack.Attribute.Layout.Element;
import java.util.Scanner;


public class LVNotifier {

	//private Properties properties;
	
	public static void main(String[] args) throws InterruptedException
	{
		Scanner keyboard = new Scanner(System.in);

		
		System.out.println("Louis Vuitton Item Notification!");
		System.out.println("");
		System.out.println("Please enter BELOW the LV website url that you would like to track. (I.E. https://uk.louisvuitton.com/eng-gb/products/onthego-gm-nvprod2130164v#M45121) ");
		String url = keyboard.next();
		System.out.println("");
		System.out.println("Please enter a valid email address below in order for the application to inform you when the product is back in stock");
		String email = keyboard.next();

		Thread a = new Thread() {
            @Override
            public void run() {
                try {
                    while (true) {
                        checkData(url, email); //check data
                    	//checkDataTest();
                        sleep(5000); //wait secs
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        try {
            a.start(); //start thread
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            br.readLine();
            a.stop(); //stop thread
            a.join();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
	public static void sendMail(String productCode, String emailAddress)
	{
		
		
		String sender = "freesoftwareprojects@gmx.co.uk";
	    String password = ")+bKJLaQKsK~4&^-";
	    String receiver = emailAddress; //uses the email address provided by user

	    Properties properties = new Properties();

	    //gmx smtp settings
	    properties.put("mail.transport.protocol", "smtp");
	    properties.put("mail.smtp.host", "mail.gmx.net");
	    properties.put("mail.smtp.port", "587");
	    properties.put("mail.smtp.auth", "true");
	    properties.put("mail.smtp.user", sender);
	    properties.put("mail.smtp.password", password);
	    properties.put("mail.smtp.starttls.enable", "true");

	    Session mailSession = Session.getInstance(properties, new Authenticator()
	    {
	        @Override
	        protected PasswordAuthentication getPasswordAuthentication()
	        {
	            return new PasswordAuthentication(properties.getProperty("mail.smtp.user"),
	                    properties.getProperty("mail.smtp.password"));
	        }
	    });

	    try {
		    Message message = new MimeMessage(mailSession);
		    InternetAddress addressTo = new InternetAddress(receiver);
		    message.setRecipient(Message.RecipientType.TO, addressTo);
		    message.setFrom(new InternetAddress(sender));
		    message.setSubject(productCode + " IS BACK IN STOCK");
		    message.setContent("Hi, This mail is to inform you that Product Code:'" + productCode + "' IS BACK IN STOCK", "text/plain");
		    
		    Transport.send(message);
		    
		    System.out.println("Message Sent!");
		} catch (MessagingException e) { //if email cannot be sent

			//return error
			System.out.print("Invalid Email Address");
			
			//e.printStackTrace();
			
			
		}
		
	}
	
	
	public static void checkData(String url, String emailAddress)
	{
		//gets website
		Document document = null;
		Document stockLevel = null;
		String strProductCode = "";
		
		try {
			
			//go to website
			document = Jsoup.connect(url).timeout(6000).get();
			//document = Jsoup.connect("https://uk.louisvuitton.com/eng-gb/products/ultimate-monogram-square-nvprod2530001v#M76650").timeout(6000).get();
			//https://uk.louisvuitton.com/eng-gb/products/pochette-accessoires-monogram-005656
			//https://uk.louisvuitton.com/eng-gb/products/louis-vuitton-horizon-wireless-earphones-black-nvprod2090051v
			
			System.out.println(url);
			
			if(url.contains("#"))
			{
				System.out.println("extract product code");
				
				
				for(int i = url.indexOf('#')+1; i < url.length(); i++)
				{
					char temp = url.charAt(i);
					
					strProductCode += temp;
					
				}
				System.out.println(strProductCode);
				
			}
			else
			{
				System.out.println("use html product code");
				
				Elements elementCode = document.select(".lv-product__details-head");
								
				strProductCode = elementCode.text();
				
			}
			
			//gets product name
			Elements elementName = document.select(".lv-product__title");
			
			//gets product price and code
			Elements elementPrice = document.select(".lv-product__price");
			
			System.out.println("Product Code: " + strProductCode);
			
			//get whether if in stock
			stockLevel= Jsoup.connect("https://secure.louisvuitton.com/ajaxsecure/getStockLevel.jsp?storeLang=eng-gb&pageType=storelocator_section&skuIdList="+strProductCode+"&null&_=1594445745544").timeout(6000).get();

			//gets the whole text element whether if in stock
			Elements elementnotInStockMessage = stockLevel.getAllElements();
			
			//grabs the important data
			String valElement = elementnotInStockMessage.text().substring(1, 28);
			
			
			//output info
			System.out.println("Product Name: " + elementName.text());
			
			System.out.println("Product Price: " + elementPrice.text());
			
			//if the important information gathered contains certain text
			if(valElement.contains("true"))
			{
				//output result
				System.out.println("Product InStock: TRUE");
				
				//send an email notifying me its available
				sendMail(strProductCode, emailAddress);			
				//close app
				System.exit(0);
			}
			else
			{
				//output result
				System.out.println("Product InStock: FALSE");
			}
			
			System.out.println("------------------------------------");

			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			
			System.out.println("------------------------------------");
			
			System.out.println("This item is not from the LV store");
		}
	}
	
}

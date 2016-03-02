

import java.util.ArrayList;

import connection.maker.log240.setConnection_log210;

public class mainProgramTest {
	private static ArrayList<String> rssFeedURLs = new ArrayList<String>();
	
  public static void main(String[] args) throws Exception {
	  setConnection_log210 conn = new setConnection_log210();
	  //conn.connectAccount("log240", "log240");
	  //tester fonction ici ("conn.fucntion(parametre);")
	  
	  //conn.insertReservation("testUser", "testISBN"); 
	  
	  testFunc();
	  

  }
  
  public static void testFunc(){
	  
	  
	  String rssFeedURL = "http://stackoverflow.com";
	  rssFeedURLs = new ArrayList<String>();
	  rssFeedURLs.add(rssFeedURL);
	  if(rssFeedURLs.contains("http://stackoverflow.com")) {
	   System.out.println("ca marche");
	  }
  }

} 

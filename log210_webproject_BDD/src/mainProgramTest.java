

import connection.maker.log240.setConnection_log210;

public class mainProgramTest {
  public static void main(String[] args) throws Exception {
	  //setConnection_log210 conn = new setConnection_log210();
	  //conn.connectAccount("log240", "log240");
	  //tester fonction ici ("conn.fucntion(parametre);")
	  
	  AjouterLivre ajout = new AjouterLivre("log240");
	  ajout.ChercherInfo("9788804643135");
	  ajout.FinaliserAjoutLivre();
	  
  }

} 

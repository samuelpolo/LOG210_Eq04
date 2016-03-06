package connection.maker.log240;

import java.sql.*;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.http.impl.cookie.DateUtils;



public class setConnection_log210{
	  private Context context = null;
      DataSource datasource = null;
	  private Connection connect = null;
	  private Statement statement = null;
	  private PreparedStatement preparedStatement = null;
	  public ResultSet resultSet = null;
	  private PreparedStatement preparedStatement2 = null;
	  public ResultSet resultSet2 = null;
	  
	  public String[] tabLivre = new String[4];
	  public String[] tabCompte = new String[4];
	  public ArrayList<String> ISBNList = new ArrayList<String>();
	  public ArrayList<String> etatList = new ArrayList<String>();
	  
	  /** Fonction de création d'un compte dans la BDD
	   * 
	   * @param pw
	   * @param email
	   * @param type
	   * @param phone
	   * @throws Exception
	   */
	  
	  public void insertAccount(String pw,String email,String type,String phone)throws Exception{
		  try{
		      
		      // This will load the MySQL driver, each DB has its own driver
		      Class.forName("com.mysql.jdbc.Driver");
		      
		      // Setup the connection with the DB
		      connect = DriverManager
		          .getConnection("jdbc:mysql://localhost/iteration_bdd?"
		              + "user=sqluser&password=sqluserpw");

		      
		   // PreparedStatements can use variables and are more efficient
		      preparedStatement = connect
		          .prepareStatement("insert into  iteration_bdd.compte values (default, ?, ?, ?, ?)");
		      // "password, email, type, phone, from iteration_bdd.compte");
		      // Parameters start with 1
		      preparedStatement.setString(1, pw);
		      preparedStatement.setString(2, email);
		      preparedStatement.setString(3, type);
		      preparedStatement.setString(4, phone);
		      preparedStatement.executeUpdate();
		      
		  
		  } 
		  catch (Exception e) {
			 throw e;
		  } 
		  finally {
			  close();
		  }
		  
		 
	  }
	  
	  public void insertCooperative(String nom, String address)throws Exception{
		  try{
		      // This will load the MySQL driver, each DB has its own driver
		      Class.forName("com.mysql.jdbc.Driver");
		      // Setup the connection with the DB
		      connect = DriverManager
		          .getConnection("jdbc:mysql://localhost/iteration_bdd?"
		              + "user=sqluser&password=sqluserpw");
		      
		   // PreparedStatements can use variables and are more efficient
		      preparedStatement = connect
		          .prepareStatement("insert into  iteration_bdd.cooperative values (default, ?, ?)");
		      // "password, email, type, phone, from iteration_bdd.compte");
		      // Parameters start with 1
		      preparedStatement.setString(1, nom);
		      preparedStatement.setString(2, address);
		      preparedStatement.executeUpdate();
		  
		  } 
		  catch (Exception e) {
			 throw e;
		  } 
		  finally {
			  close();
		  }
		  
		 
	  }
	  
	  public void insertTransaction(String gestionnaire, String acheteur, double prix)throws Exception{
		  try{
		      // This will load the MySQL driver, each DB has its own driver
		      Class.forName("com.mysql.jdbc.Driver");
		      // Setup the connection with the DB
		      connect = DriverManager
		          .getConnection("jdbc:mysql://localhost/iteration_bdd?"
		              + "user=sqluser&password=sqluserpw");
		      
		   // PreparedStatements can use variables and are more efficient
		      preparedStatement = connect
		          .prepareStatement("insert into  iteration_bdd.transactions values (default, ?, ?, ? , ?)");
		      // "password, email, type, phone, from iteration_bdd.compte");
		      // Parameters start with 1
		      preparedStatement.setString(1, gestionnaire);
		      preparedStatement.setString(2, acheteur);
		      preparedStatement.setDouble(3,prix);
		      preparedStatement.setDate(4, new java.sql.Date(System.currentTimeMillis()));
		      preparedStatement.executeUpdate();
		  
		  } 
		  catch (Exception e) {
			 throw e;
		  } 
		  finally {
			  close();
		  }
		  
		 
	  }
	  
	  /**
	   * Méthode de connection a un compte
	   * @return false : le compte n'existe pas OU ce n'est pas le bon mot de passe
	   * @return true : le compte existe ET le mot de passe est bon
	   * @param username
	   * @param pw
	   * @return
	   * @throws Exception
	   */
	  
	  public String[] connectAccount(String username,String pw) throws Exception{
		  try{
		      // This will load the MySQL driver, each DB has its own driver
		      Class.forName("com.mysql.jdbc.Driver");
		      // Setup the connection with the DB
		      connect = DriverManager
		          .getConnection("jdbc:mysql://localhost/iteration_bdd?"
		              + "user=sqluser&password=sqluserpw");
			  
			 /* context = new InitialContext();
			  datasource = (DataSource) context.lookup("jdbc:mysql://localhost/iteration_bdd");
			  connect = datasource.getConnection();*/

		    //Selecting all books with the right ISBN in the database (there should only be one)
		      preparedStatement = connect
		          .prepareStatement("SELECT * FROM iteration_bdd.compte WHERE email= ? AND password = ? ; ");		      		   
		      
		      preparedStatement.setString(1, username);
		      preparedStatement.setString(2, pw);
		      
		      resultSet = preparedStatement.executeQuery();
		      
		      if(!resultSet.next()){

		    	  preparedStatement = connect
		          .prepareStatement("SELECT * FROM iteration_bdd.compte WHERE phone= ? AND password = ? ; ");
		    	  
			      preparedStatement.setString(1, username);
			      preparedStatement.setString(2, pw);
			      resultSet = preparedStatement.executeQuery();
			      
			      /*if(resultSet == null){
			    	  return null;
			      }*/
		      }
		      else{
		    	  resultSet.previous();
		      }

		     /*resultSet.next();
		     System.out.println(resultSet.getString(3));	
		     readCompteData(resultSet);*/
		     if(resultSet.next())	{			
			 String password = resultSet.getString(2);
		     System.out.println(password);
			 tabCompte[0] = password;
		     
		     
			 
			 
	    	 String email = resultSet.getString(3);
	    	 tabCompte[1] = email;
	    	 System.out.println(email);
			 
			 
			 String type = resultSet.getString(4);
			 tabCompte[2] = type;
			 System.out.println(type);
			 
			 
			 
			 String phone = resultSet.getString(5);

			
			 
			 tabCompte[3] = phone;
			 System.out.println(phone);
		     
			 
			 
		     }
			 

		     return tabCompte;	  
		  
		  } 
		  catch (Exception e) {
			 throw e;
		  } 
		  finally {

			  close();
		  }

		  
		  
	  }
	  
	  
	  
	  /**Fonction insertLivre ajoute un livre dans la base de données
	   * 
	   * @param titre
	   * @param auteur
	   * @param isbn
	   * @param prix
	   * @param nbPages
	   * @throws Exception
	   */
	  
	  public void insertLivre(String titre,String auteur,String isbn,String prix, int nbPages)throws Exception{
		  try{
			  System.out.println("insert livre 1");
		      // This will load the MySQL driver, each DB has its own driver
		      Class.forName("com.mysql.jdbc.Driver");
		      // Setup the connection with the DB
		      connect = DriverManager
		          .getConnection("jdbc:mysql://localhost/iteration_bdd?"
		              + "user=sqluser&password=sqluserpw");
		      
		      System.out.println("insert livre 2");
		   // PreparedStatements can use variables and are more efficient
		      preparedStatement = connect
		          .prepareStatement("insert into  iteration_bdd.livre values (default, ?, ?, ?, ?, ?, ?)");
		      // Parameters start with 1
		      preparedStatement.setString(1, titre);
		      preparedStatement.setString(2, auteur);
		      preparedStatement.setString(3, isbn);
		      preparedStatement.setBoolean(4, false);
		      preparedStatement.setString(5, prix);
		      preparedStatement.setInt(6, nbPages);
		      System.out.println("insert livre 3");
		      preparedStatement.executeUpdate();
		      System.out.println("insert livre 4");
		  } 
		  catch (Exception e) {
			 throw e;
		  } 
		  finally {
			  close();
		  }
		  
		 
	  }
	  
	  public void insertReservation(String username, int ID){
		  try{
			  
		      // This will load the MySQL driver, each DB has its own driver
		      Class.forName("com.mysql.jdbc.Driver");
		      // Setup the connection with the DB
		      connect = DriverManager
		          .getConnection("jdbc:mysql://localhost/iteration_bdd?"
		              + "user=sqluser&password=sqluserpw");
		   // PreparedStatements can use variables and are more efficient
		      preparedStatement = connect
		          .prepareStatement("insert into  iteration_bdd.reservation values (default, ?, ?, ?, ?)");
		      
		      
		      
		      // Parameters start with 1
		      preparedStatement.setString(1, username);
		      preparedStatement.setString(2, "dummy dummy");
		      preparedStatement.setDate(3, new java.sql.Date(System.currentTimeMillis()));
		      preparedStatement.setInt(4, ID);
		      preparedStatement.executeUpdate();
		      
			   // PreparedStatements can use variables and are more efficient
		      preparedStatement = connect
		          .prepareStatement("UPDATE iteration_bdd.associations SET reserve=? WHERE id=?");
		      
		      preparedStatement.setDate(1,  new java.sql.Date(System.currentTimeMillis()));
		      preparedStatement.setInt(2, ID);
		      preparedStatement.executeUpdate();
		      
		      
		  } 
		  catch (Exception e) {

		  } 
		  finally {
			  close();
		  }
		  
		 
	  }
	  
	  /** Fonction de supression d'un livre dans le système
	   * 
	   * @param isbn
	   * @throws Exception
	   */
	  
	  public void deleteLivre(String isbn)throws Exception{
		  try{
		      // This will load the MySQL driver, each DB has its own driver
		      Class.forName("com.mysql.jdbc.Driver");
		      // Setup the connection with the DB
		      connect = DriverManager
		          .getConnection("jdbc:mysql://localhost/iteration_bdd?"
		              + "user=sqluser&password=sqluserpw");
		      
		      preparedStatement = connect
		      .prepareStatement("delete from iteration_bdd.livre where isbn= ? ; ");
		      preparedStatement.setString(1, isbn);
		      preparedStatement.executeUpdate();
		  
		  } 
		  catch (Exception e) {
			 throw e;
		  } 
		  finally {
			  close();
		  }
		  
		 
	  }
	  
	  /** Fonction 
	   * @return infos sur livre String[] 0: Titre
	   * 					1: auteur
	   * 					2: nombre de page
	   * 
	   * @param ISBN
	   * @return
	   * @throws Exception
	   */
	  
	  public String[] readLivre(String ISBN) throws Exception{
		  
		  tabLivre = new String[4];
		  
		  try {
		      // This will load the MySQL driver, each DB has its own driver
		      Class.forName("com.mysql.jdbc.Driver");
		      // Setup the connection with the DB
		      connect = DriverManager
		          .getConnection("jdbc:mysql://localhost/iteration_bdd?"
		              + "user=sqluser&password=sqluserpw");

		      //Selecting all books with the right ISBN in the database (there should only be one)
		      preparedStatement = connect
		          .prepareStatement("SELECT titre,etat,isbn,auteur,prix,reserver FROM iteration_bdd.livre WHERE isbn= ? ; ");
		      preparedStatement.setString(1, ISBN);
		      resultSet = preparedStatement.executeQuery();
		      readBookData(resultSet);
		      
		    } catch (Exception e) {
		      throw e;
		    } finally {
		      close();
		    }
		  
		  
		  
		  return tabLivre;
	  }
	  
	  public void associerLivre(String username,String ISBN, int etat,String coop)throws Exception{
		  try {
		      // This will load the MySQL driver, each DB has its own driver
		      Class.forName("com.mysql.jdbc.Driver");
		      // Setup the connection with the DB
		      connect = DriverManager
		          .getConnection("jdbc:mysql://localhost/iteration_bdd?"
		              + "user=sqluser&password=sqluserpw");

		   // PreparedStatements can use variables and are more efficient
		      preparedStatement = connect
		          .prepareStatement("insert into  iteration_bdd.associations values (default, ?, ?, ?, ?, default)");
		      // "myuser, webpage, datum, summary, COMMENTS from feedback.comments");
		      // Parameters start with 1
		      preparedStatement.setString(1, username);
		      preparedStatement.setString(2, ISBN);
		      preparedStatement.setInt(3, etat);
		      preparedStatement.setString(4,coop);
		      preparedStatement.executeUpdate();
		      
		    } catch (Exception e) {
		      throw e;
		    } finally {
		      close();
		    }
	  }
	  
	  public ArrayList<String> returnTitlesContaining(String partieTitre){
		  
		  //Reset the arrayList to be empty
		  ISBNList = new ArrayList<String>();
		  
		  try{
		      // This will load the MySQL driver, each DB has its own driver
		      Class.forName("com.mysql.jdbc.Driver");
		      // Setup the connection with the DB
		      connect = DriverManager
		          .getConnection("jdbc:mysql://localhost/iteration_bdd?"
		              + "user=sqluser&password=sqluserpw");
			
		    //Selecting all books containing the partieTitre in the title column
		      preparedStatement = connect
		          .prepareStatement("SELECT * FROM iteration_bdd.livre WHERE titre LIKE \"%"+partieTitre+"%\" ");		      		   
		      //System.out.println("avantSetString");
		      //preparedStatement.setString(1, partieTitre);
		      
			  resultSet = preparedStatement.executeQuery();
			  while(resultSet.next()){
				  ISBNList.add(resultSet.getString("isbn"));
				  System.out.println(resultSet.getString("isbn") + "par titre");
			  }
		      		
		      
			  //Selecting all books containing the partieTitre in the author column
		      preparedStatement = connect
		          .prepareStatement("SELECT * FROM iteration_bdd.livre WHERE auteur LIKE \"%"+partieTitre+"%\" ");		      		   
		      
		     // preparedStatement.setString(1, partieTitre);
		      
			  resultSet = preparedStatement.executeQuery();
			      
			  while(resultSet.next()){
				  ISBNList.add(resultSet.getString("isbn"));
				  System.out.println(resultSet.getString("isbn") + "par auteur");
			  }		    			 

		    	  
		  
		  } 
		  catch (Exception e) {
			 
		  } 
		  finally {

			  close();
		  }

		  return ISBNList;
	  }
	  
	  
	  
	  public ArrayList<String> returnAssociationContaining(String ISBN){
		  
		  //Reset the arrayList to be empty
		  etatList = new ArrayList<String>();
		  
		  try{
		      // This will load the MySQL driver, each DB has its own driver
		      Class.forName("com.mysql.jdbc.Driver");
		      // Setup the connection with the DB
		      connect = DriverManager
		          .getConnection("jdbc:mysql://localhost/iteration_bdd?"
		              + "user=sqluser&password=sqluserpw");

		    //Selecting all books containing the partieTitre in the title column
		      preparedStatement = connect
		          .prepareStatement("SELECT * FROM iteration_bdd.associations WHERE isbn= ?;");		      		   
		      
		      preparedStatement.setString(1, ISBN);
		      
			  resultSet = preparedStatement.executeQuery();
			      
			  while(resultSet.next()){
				  
				  Date now = new Date();
				  Date previous = resultSet.getDate("reserve");
				  
				  String testCoop = resultSet.getString("coop");
				  
				  if((previous==null||(now.getTime() - previous.getTime() >= 48*60*60*1000)) && testCoop != null){
					  etatList.add(Integer.toString(resultSet.getInt("etat")));
					  etatList.add(resultSet.getString("username"));
					  etatList.add(Integer.toString(resultSet.getInt("ID")));
				  }
			  }
		      		    			 		  
		  } 
		  catch (Exception e) {
			 
		  } 
		  finally {

			  close();
		  }

		  return etatList;
	  }
	  
	  
	  
	  	public ArrayList<String> returnAssociationName(String username){
		  
		  //Reset the arrayList to be empty
		  etatList = new ArrayList<String>();
		  
		  try{
		      // This will load the MySQL driver, each DB has its own driver
		      Class.forName("com.mysql.jdbc.Driver");
		      // Setup the connection with the DB
		      connect = DriverManager
		          .getConnection("jdbc:mysql://localhost/iteration_bdd?"
		              + "user=sqluser&password=sqluserpw");

		    //Selecting all books containing the partieTitre in the title column
		      preparedStatement = connect
		          .prepareStatement("SELECT * FROM iteration_bdd.associations WHERE username= ?;");		      		   
		      
		      preparedStatement.setString(1, username);
		      
			  resultSet = preparedStatement.executeQuery();
			      
			  while(resultSet.next()){
				  
				  //Verify if the book's coop is the same as the gestionnaire
				  if( resultSet.getString("coop") == null){
					  etatList.add(Integer.toString(resultSet.getInt("etat")));
					  etatList.add(resultSet.getString("username"));
					  etatList.add(Integer.toString(resultSet.getInt("ID")));
					  etatList.add(resultSet.getString("ISBN"));
				  }
			  }
		      		    			 		  
		  } 
		  catch (Exception e) {
			 
		  } 
		  finally {

			  close();
		  }

		  return etatList;
	  }
	  	
	  	public ArrayList<String> returnAllReservations(){
			  
			  //Reset the arrayList to be empty
			  etatList = new ArrayList<String>();
			  
			  try{
			      // This will load the MySQL driver, each DB has its own driver
			      Class.forName("com.mysql.jdbc.Driver");
			      // Setup the connection with the DB
			      connect = DriverManager
			          .getConnection("jdbc:mysql://localhost/iteration_bdd?"
			              + "user=sqluser&password=sqluserpw");

			    //Selecting all books containing the partieTitre in the title column
			      preparedStatement = connect
			          .prepareStatement("SELECT * FROM iteration_bdd.reservation;");		      		   
			      
			     // preparedStatement.setString(1, null);
			      
				  resultSet = preparedStatement.executeQuery();
				      

					  
				  while(resultSet.next()){
					  /*etatList.add(String.valueOf(resultSet.getInt("username")));
					 */
					  System.out.println(resultSet.getInt("idAssociation") + " : this is a reservation ID");
					  
						  preparedStatement2 = null;
						  resultSet2 = null;
						  
						  preparedStatement2 = connect.
								  prepareStatement("SELECT * FROM iteration_bdd.associations WHERE ID = ?;");
						  preparedStatement2.setString(1, String.valueOf(resultSet.getInt("idAssociation")));
						  
						  resultSet2 = preparedStatement2.executeQuery();
						 
						  
						  if(resultSet2.next()){
							  
							  System.out.println(resultSet2.getInt("id") + " this is the association");
							  
							  etatList.add(resultSet.getString("username"));
							  etatList.add(resultSet.getString("idAssociation"));
							  //etatList.add(String.valueOf(resultSet2.getInt("ID")));
							  etatList.add(resultSet2.getString("isbn"));
							  etatList.add(String.valueOf(resultSet2.getInt("etat")));
						  }
						  
				  }
				  
			      		    			 		  
			  } 
			  catch (Exception e) {
				 
			  } 
			  finally {

				  close();
			  }

			  return etatList;
		  }
	  
		 public ArrayList<String> returnAssociationContainingCoopNullParameterISBN(String ISBN){
			  
			  //Reset the arrayList to be empty
			  etatList = new ArrayList<String>();
			  
			  try{
			      // This will load the MySQL driver, each DB has its own driver
			      Class.forName("com.mysql.jdbc.Driver");
			      // Setup the connection with the DB
			      connect = DriverManager
			          .getConnection("jdbc:mysql://localhost/iteration_bdd?"
			              + "user=sqluser&password=sqluserpw");

			    //Selecting all books containing the partieTitre in the title column
			      preparedStatement = connect
			          .prepareStatement("SELECT * FROM iteration_bdd.associations WHERE isbn= ?;");		      		   
			      
			      preparedStatement.setString(1, ISBN);
			      
				  resultSet = preparedStatement.executeQuery();
				  
				      
				  while(resultSet.next()){
					  String testCoop = resultSet.getString("coop");
					  
					  if(testCoop == null){
						  etatList.add(Integer.toString(resultSet.getInt("etat")));
						  etatList.add(resultSet.getString("username"));
						  etatList.add(Integer.toString(resultSet.getInt("ID")));
					  }
				  }
			      		    			 		  
			  } 
			  catch (Exception e) {
				 
			  } 
			  finally {

				  close();
			  }

			  return etatList;
		  }
	  	
	  public void readDatabase() throws Exception{
		  try {
		      // This will load the MySQL driver, each DB has its own driver
		      Class.forName("com.mysql.jdbc.Driver");
		      // Setup the connection with the DB
		      connect = DriverManager
		          .getConnection("jdbc:mysql://localhost/iteration_bdd?"
		              + "user=sqluser&password=sqluserpw");

		      // Statements allow to issue SQL queries to the database
		      statement = connect.createStatement();
		      // Result set get the result of the SQL query
		      resultSet = statement
		          .executeQuery("select * from feedback.comments");
		      writeResultSet(resultSet);

		      // PreparedStatements can use variables and are more efficient
		      preparedStatement = connect
		          .prepareStatement("insert into  feedback.comments values (default, ?, ?, ?, ? , ?, ?)");
		      // "myuser, webpage, datum, summary, COMMENTS from feedback.comments");
		      // Parameters start with 1
		      preparedStatement.setString(1, "Test");
		      preparedStatement.setString(2, "TestEmail");
		      preparedStatement.setString(3, "TestWebpage");
		      preparedStatement.setDate(4, new java.sql.Date(2009, 12, 11));
		      preparedStatement.setString(5, "TestSummary");
		      preparedStatement.setString(6, "TestComment");
		      preparedStatement.executeUpdate();

		      preparedStatement = connect
		          .prepareStatement("SELECT myuser, webpage, datum, summary, COMMENTS from feedback.comments");
		      resultSet = preparedStatement.executeQuery();
		      writeResultSet(resultSet);

		      // Remove again the insert comment
		      preparedStatement = connect
		      .prepareStatement("delete from feedback.comments where myuser= ? ; ");
		      preparedStatement.setString(1, "Test");
		      preparedStatement.executeUpdate();
		      
		      resultSet = statement
		      .executeQuery("select * from feedback.comments");
		      writeMetaData(resultSet);
		      
		    } catch (Exception e) {
		      throw e;
		    } finally {
		      close();
		    }
	  }
	  
	  private void writeMetaData(ResultSet resultSet) throws SQLException {
		    //   Now get some metadata from the database
		    // Result set get the result of the SQL query
		    
		    System.out.println("The columns in the table are: ");
		    
		    System.out.println("Table: " + resultSet.getMetaData().getTableName(1));
		    for  (int i = 1; i<= resultSet.getMetaData().getColumnCount(); i++){
		      System.out.println("Column " +i  + " "+ resultSet.getMetaData().getColumnName(i));
		    }
	 }

	 private void writeResultSet(ResultSet resultSet) throws SQLException {
		    // ResultSet is initially before the first data set
		    while (resultSet.next()) {
		      // It is possible to get the columns via name
		      // also possible to get the columns via the column number
		      // which starts at 1
		      // e.g. resultSet.getSTring(2);
		      String user = resultSet.getString("myuser");
		      String website = resultSet.getString("webpage");
		      String summary = resultSet.getString("summary");
		      Date date = resultSet.getDate("datum");
		      String comment = resultSet.getString("comments");
		      System.out.println("User: " + user);
		      System.out.println("Website: " + website);
		      System.out.println("Summary: " + summary);
		      System.out.println("Date: " + date);
		      System.out.println("Comment: " + comment);
		    }
	 }
	 
	 private void readBookData(ResultSet resultSet)  throws SQLException{
		 
		 while (resultSet.next()) {
			 String titre = resultSet.getString("titre");
			 String auteur = resultSet.getString("auteur");
			 String nbDePage = String.valueOf(resultSet.getInt("etat"));
			 String prix = resultSet.getString("prix");
			 tabLivre[0] = titre;
			 tabLivre[1] = auteur;
			 tabLivre[2] = prix;
			 tabLivre[3] = nbDePage;
		 }
		 
		 return;	
	 }
	 
	 private void readCompteData(ResultSet resultSet)  throws SQLException{
	      
	      System.out.println("avant la boucle while des getString");
	      while(resultSet.next()){
				
			 String password = resultSet.getString("password");
			 String email = resultSet.getString("email");
			 String type = resultSet.getString("type");
			 String phone = resultSet.getString("phone");
			 tabCompte[0] = password;
			 System.out.println(password);
			 tabCompte[1] = email;
			 System.out.println(email);
			 tabCompte[2] = type;
			 System.out.println(type);
			 tabCompte[3] = phone;
			 System.out.println(phone);
	      }
		 
		 
		 

		 System.out.println("yo2");	
	     
		 return;	
	 }

		  // You need to close the resultSet
		  private void close() {
		    try {
		      if (resultSet != null) {
		        resultSet.close();
		      }

		      if (statement != null) {
		        statement.close();
		      }

		      if (connect != null) {
		        connect.close();
		      }
		    } catch (Exception e) {

		    }
		  }
	
	public void changeCoop (String coop, int ID, int etat) throws Exception{
		String realID = String.valueOf(ID);
		int rs;
		
		try{
		      // This will load the MySQL driver, each DB has its own driver
		      Class.forName("com.mysql.jdbc.Driver");
		      // Setup the connection with the DB
		      connect = DriverManager
		          .getConnection("jdbc:mysql://localhost/iteration_bdd?"
		              + "user=sqluser&password=sqluserpw");

		    //Selecting all books with the right ISBN in the database (there should only be one)
		      preparedStatement = connect
		          .prepareStatement("UPDATE iteration_bdd.associations SET coop=? , etat=?  WHERE ID = ? ; ");		      		   
		      
		      preparedStatement.setString(1, coop);
		      preparedStatement.setString(3, realID);
		      preparedStatement.setInt(2, etat);
		      
		      rs = preparedStatement.executeUpdate();
		  } 
		  catch (Exception e) {
			 throw e;
		  } 
		  finally {

			  close();
		  }
	}
	
	public void supprimerAssociation(int ID) throws Exception{
		try{
		      // This will load the MySQL driver, each DB has its own driver
		      Class.forName("com.mysql.jdbc.Driver");
		      // Setup the connection with the DB
		      connect = DriverManager
		          .getConnection("jdbc:mysql://localhost/iteration_bdd?"
		              + "user=sqluser&password=sqluserpw");
		      
		      preparedStatement = connect
		      .prepareStatement("delete from iteration_bdd.associations where id= ? ; ");
		      preparedStatement.setString(1, String.valueOf(ID));
		      preparedStatement.executeUpdate();
		  
		  } 
		  catch (Exception e) {
			 throw e;
		  } 
		  finally {
			  close();
		  }
		  
		 
	}
	
	public void supprimerReservationAPartirDAssociation(int ID)throws Exception{
		try{
		      // This will load the MySQL driver, each DB has its own driver
		      Class.forName("com.mysql.jdbc.Driver");
		      // Setup the connection with the DB
		      connect = DriverManager
		          .getConnection("jdbc:mysql://localhost/iteration_bdd?"
		              + "user=sqluser&password=sqluserpw");
				  
					  preparedStatement2 = null;
					  resultSet2 = null;
					  
					   preparedStatement = connect
							      .prepareStatement("delete from iteration_bdd.reservation where idAssociation= ? ; ");
					  preparedStatement.setString(1, String.valueOf(ID));
					  
					  preparedStatement.executeUpdate();
					  			  
		      		    			 		  
		  } 
		  catch (Exception e) {
			 
		  } 
		  finally {

			  close();
		  }
	}
}

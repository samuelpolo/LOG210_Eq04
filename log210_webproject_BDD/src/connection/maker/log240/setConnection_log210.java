package connection.maker.log240;

import java.sql.*;
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


public class setConnection_log210{
	  private Context context = null;
      DataSource datasource = null;
	  private Connection connect = null;
	  private Statement statement = null;
	  private PreparedStatement preparedStatement = null;
	  public ResultSet resultSet = null;
	  
	  public String[] tabLivre = new String[4];
	  public String[] tabCompte = new String[4];
	  
	  /** Fonction de cr�ation d'un compte dans la BDD
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
	  
	  /**
	   * M�thode de connection a un compte
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
		          .prepareStatement("SELECT password,type,email,phone FROM iteration_bdd.compte WHERE email= ? ; ");		      		   
		      
		      preparedStatement.setString(1, username);
		      
		      if(resultSet == null){

		    	  preparedStatement = connect
		          .prepareStatement("SELECT * FROM iteration_bdd.compte WHERE phone= ? ; ");
		    	  
			      preparedStatement.setString(1, username);
			      resultSet = preparedStatement.executeQuery();
			      
			      /*if(resultSet == null){
			    	  return null;
			      }*/
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
	  
	  
	  
	  /**Fonction insertLivre ajoute un livre dans la base de donn�es
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
		      // This will load the MySQL driver, each DB has its own driver
		      Class.forName("com.mysql.jdbc.Driver");
		      // Setup the connection with the DB
		      connect = DriverManager
		          .getConnection("jdbc:mysql://localhost/iteration_bdd?"
		              + "user=sqluser&password=sqluserpw");
		      
		   // PreparedStatements can use variables and are more efficient
		      preparedStatement = connect
		          .prepareStatement("insert into  iteration_bdd.livre values (default, ?, ?, ?, ?, ?, ?)");
		      // "password, email, type, phone, from iteration_bdd.compte");
		      // Parameters start with 1
		      preparedStatement.setString(1, titre);
		      preparedStatement.setString(2, auteur);
		      preparedStatement.setString(3, isbn);
		      preparedStatement.setBoolean(4, false);
		      preparedStatement.setString(5, prix);
		      preparedStatement.setInt(6, nbPages);
		      preparedStatement.executeUpdate();
		  
		  } 
		  catch (Exception e) {
			 throw e;
		  } 
		  finally {
			  close();
		  }
		  
		 
	  }
	  
	  /** Fonction de supression d'un livre dans le syst�me
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
		  
		  tabLivre = new String[3];
		  
		  try {
		      // This will load the MySQL driver, each DB has its own driver
		      Class.forName("com.mysql.jdbc.Driver");
		      // Setup the connection with the DB
		      connect = DriverManager
		          .getConnection("jdbc:mysql://localhost/iteration_bdd?"
		              + "user=sqluser&password=sqluserpw");

		      //Selecting all books with the right ISBN in the database (there should only be one)
		      preparedStatement = connect
		          .prepareStatement("SELECT username,isbn,etat FROM iteration_bdd.livre WHERE isbn= ? ; ");
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
	  
	  public void associerLivre(String username,String ISBN, int etat)throws Exception{
		  try {
		      // This will load the MySQL driver, each DB has its own driver
		      Class.forName("com.mysql.jdbc.Driver");
		      // Setup the connection with the DB
		      connect = DriverManager
		          .getConnection("jdbc:mysql://localhost/iteration_bdd?"
		              + "user=sqluser&password=sqluserpw");

		   // PreparedStatements can use variables and are more efficient
		      preparedStatement = connect
		          .prepareStatement("insert into  feedback.comments values (default, ?, ?, ?)");
		      // "myuser, webpage, datum, summary, COMMENTS from feedback.comments");
		      // Parameters start with 1
		      preparedStatement.setString(1, username);
		      preparedStatement.setString(2, ISBN);
		      preparedStatement.setInt(3, etat);
		      preparedStatement.executeUpdate();
		      
		    } catch (Exception e) {
		      throw e;
		    } finally {
		      close();
		    }
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
			 String nbDePage = resultSet.getString("etat");
			 String prix = resultSet.getString("prix");
			 tabLivre[0] = titre;
			 tabLivre[1] = auteur;
			 tabLivre[2] = prix;
			 tabLivre[3] = nbDePage;
		 }
		 
		 

		 
		 System.out.println("Titre: " + tabLivre[0]);
		 System.out.println("Auteur: " + tabLivre[1]);
		 System.out.println("Nombre de Pages: " + tabLivre[2]);
		 
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
	
	
}

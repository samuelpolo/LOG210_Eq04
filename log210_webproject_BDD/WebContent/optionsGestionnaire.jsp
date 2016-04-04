<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Options du gestionnaire de coop</title>
</head>
<body>
	Recherche d'un livre en attente de remise
	    <form action="rechercheLivreARemettre" method="post">
      	ISBN, UPC ou partie du titre: <input type="text" name="info" />
      	<input type="submit" value="Rechercher" />
   		</form>
   		
   	<br/>
   	<br/>
   	<br/>
	Cliquez sur ce bouton pour qu'un étudiant récupère un livre réservé au comptoir
	
	<form action="listeDesReservation" method="post">
      	<input type="submit" value="Afficher les réservations" />
    </form>
	   		
</body>
</html>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Ajout d'un livre</title>
</head>
<body>

	Ajout d'un livre
	   <form action="ajoutLivre" method="post">
      identifiant industriel : <input type="text" name="isbn" />
      <input type="submit" value="Debuter ajout" />
   </form>
	
	
	Réservation d'un livre
	<form action="accueilReservationLivre" method="post">
	      Veuillez entrer une partie du titre ou du nom de l'auteur ou bien l'ISBN complet : <input type="text" name="recherche" />
	      <input type="submit" value="Recherche" />
	</form>
	
</body>
</html>
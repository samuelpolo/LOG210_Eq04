<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Réservation d'un livre</title>
</head>
<body>
	   <form action="accueilReservationLivre" method="post">
	      Veuillez entrer une partie du titre ou du nom de l'auteur ou bien l'ISBN complet : <input type="text" name="recherche" />
	      <input type="submit" value="Recherche" />
	   </form>
</body>
</html>
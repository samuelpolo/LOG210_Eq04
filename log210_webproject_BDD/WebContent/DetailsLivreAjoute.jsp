<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>D�tails du livre ajout�</title>
</head>
<body>

	<table>
		<tr>
			<td>Titre: </td>
			<td>${titre}</td>
		</tr>
		<tr>
			<td>Auteur: </td>
			<td>${auteur}</td>
		</tr>
		<tr>
			<td>Prix: </td>
			<td>${prix}</td>
		</tr>
		<tr>
			<td>ISBN: </td>
			<td>${isbn}</td>
		</tr>
		<tr>
			<td>1. Sp�cifier l'�tat du livre </td>
		</tr>		
		<tr>
			<td>2. Comme neuf (75% du prix neuf) </td>
		</tr>		
		<tr>
			<td>3. Quelques pages plie�s, utilisation d'un marqueur (50% du prix neuf)</td>
		</tr>		
		<tr>
			<td>Livre tr�s utilis�, pages pli�, couverture endommag�s  (25% du prix neuf)</td>
		</tr>
		<tr> 

		</tr>
	</table>
	
	<form action="finalAjoutLivre" method="post">
     �tat du livre : <input type="text" name="etat" /> 
      <input type="submit" value="Finaliser l'ajout" />
   </form>
   
</body>
</html>
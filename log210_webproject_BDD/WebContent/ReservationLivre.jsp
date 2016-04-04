<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>


    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="livrePackage.Livre" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Choisissez parmis les exemplaires suivant</title>
</head>
<body>
      
      <c:if test="${livreListe.isEmpty()}">
      	Le livre recherché n'existe pas dans la base de données ou n'a pas d'exemplaire disponible.
      	
      	<br />
      	
      	Veuillez entrer le ISBN du livre et nous vous notiefierons si le livre entre en inventaire.
      	
      	<br />
      	
      	<form action="notifierLivre" method="post">
		      identifiant industriel : <input type="text" name="isbn" />
		      <input type="submit" value="Me notifier lorsque ce livre est disponible" />
  		</form>
      	
      	<br />
      	
      	<a href="AccueilAjoutLivre.jsp">Retourner à la page précédente</a>   	
      </c:if>
      
	<table>
	
	    <c:forEach items="${livreListe}" var="livre">
	        <tr>
	        	<td> Titre :</td>
	            <td>${livre.GetTitre()}</td>
	             
	        </tr>
	        <tr>
	        	<td> État : </td>
	            <td>${livre.GetÉtat()}</td>
	        </tr>
	        <tr>
	        	<td> Prix : </td>
	            <td>${livre.GetPrixVente()}</td>	             
	        </tr>
	        <tr>
	        	<td> Coop : </td>
	            <td>${livre.GetCoop()}</td>	             
	        </tr>	 	        
	        <tr>
	        	<td><a href="reserveFinal?id=${livre.GetID()}&coop=${livre.GetCoop()}">Réserver cette copie</a></td>
	        </tr>
	    </c:forEach>

	</table>
</body>
</html>
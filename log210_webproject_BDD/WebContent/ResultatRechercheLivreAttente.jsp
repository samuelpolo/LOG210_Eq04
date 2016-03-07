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
<title>Liste des livres recherch�s</title>
</head>
<body>

      <c:if test="${livreListe2.isEmpty()}">
      	Le livre recherch� n'existe pas dans la base de donn�es ou n'a pas d'exemplaire en attente.
      	
      	<br />
      	
      	<br />
      	
      	<a href="optionsGestionnaire.jsp">Retourner � la page pr�c�dente</a>   	
      </c:if>
      
		<table>
	
	    <c:forEach items="${livreListe2}" var="livre">
	    	<tr>
	        	<td> Proprio :</td>
	            <td>${livre.GetProprio()}</td>
	             
	        </tr>
	    	
	        <tr>
	        	<td> Titre :</td>
	            <td>${livre.GetTitre()}</td>
	             
	        </tr>
	        <tr>
	        	<td> �tat :</td>
	            <td>${livre.Get�tat()}</td>
	        </tr>
	        <tr>
	        	<td> Prix : </td>
	            <td>${livre.GetPrixVente()}</td>	             
	        </tr>	        
	        <tr>
	        	<td><a href="accepterFinal?id=${livre.GetID()}&etat2=${livre.Get�tat()}&proprio=${livre.GetProprio()}&titre=${livre.GetTitre()}">Accepter cette copie</a></td>
	        </tr>
	    </c:forEach>

	</table>
</body>
</html>
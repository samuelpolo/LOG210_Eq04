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
<title>Insert title here</title>
</head>
<body>
     <c:if test="${livreListe3.isEmpty()}">
      	Le livre recherché n'existe pas dans la base de données ou n'a pas d'exemplaire en attente.
      	
      	<br />
      	
      	<br />
 	
      </c:if>
      
		<table>
	
	    <c:forEach items="${livreListe3}" var="livre">
	    	<tr>
	        	<td> Acheteur :</td>
	            <td>${livre.getAcheteur()}</td>
	             
	        </tr>
	    	
	        <tr>
	        	<td> Titre :</td>
	            <td>${livre.getLivre().GetTitre()}</td>
	             
	        </tr>
	        <tr>
	        	<td> État :</td>
	            <td>${livre.getLivre().GetÉtat()}</td>
	        </tr>
	        <tr>
	        	<td> Prix : </td>
	            <td>${livre.getLivre().GetPrixVente()}</td>	             
	        </tr>	        
	        <tr>
	        	<td><a href="RemiseFinal?id=${livre.getId()}&acheteur=${livre.getAcheteur()}&prix=${livre.getLivre().GetPrixVente()}">Remettre cette copie à l'acheteur</a></td>
	        </tr>
	    </c:forEach>
	    <p>
   			<a href="optionsGestionnaire.jsp">Annuler et retourner à la page principal </a>
   		</p>
	</table>
</body>
</html>
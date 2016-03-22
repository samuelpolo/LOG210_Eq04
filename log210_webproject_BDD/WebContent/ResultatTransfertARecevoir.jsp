<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

<%@ page import="java.util.ArrayList" %>
<%@ page import="livrePackage.Livre" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
    <c:if test="${listeTransfertAExpedier.isEmpty()}">
      	Votre coopérative n'a aucun livre à expédier pour le moment.
      	
      	<br />
      	
      	<br />
 	
      </c:if>
      
		<table>
	
	    <c:forEach items="${listeTransfertAExpedier}" var="transfert">
	    	<tr>
	        	<td> Étudiant faisant la demande :</td>
	            <td>${transfert.getAcheteur()}</td>
	             
	        </tr>
	    	
	        <tr>
	        	<td> Titre :</td>
	            <td>${transfert.getLivre().GetTitre()}</td>
	             
	        </tr>
	        <tr>
	        	<td> État :</td>
	            <td>${transfert.getLivre().GetÉtat()}</td>
	        </tr>
	        <tr>
	        	<td> Prix : </td>
	            <td>${transfert.getLivre().GetPrixVente()}</td>	             
	        </tr>
	        <tr>
	        	<td> Coop vers laquelle expédier : </td>
	            <td>CoopPrincipale</td>	             
	        </tr>		        
	        <tr>
	        	<td><a href="transfertARecevoirFinal?id=${transfert.getId()}">Recevoir cet exemplaire</a></td>
	        </tr>
	    </c:forEach>
	    <p>
   			<a href="optionsGestionnaire.jsp">Annuler et retourner à la page principal </a>
   		</p>
	</table>
</body>
</html>
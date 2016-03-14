<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="java.util.ArrayList" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>


	<h2>Vous aurez 48 heures pour récupérer votre exemplaire.</h2>

	<br />

	<form action="reserveFinalConfirmation" method="post">
      <input type="submit" value="Finaliser la reservation" />
    </form>
    
   <p>
   		<a href="AccueilAjoutLivre.jsp">Annuler et retourner à la page précédente </a>
   </p>
</body>
</html>
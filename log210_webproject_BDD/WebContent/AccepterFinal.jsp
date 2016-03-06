<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>

	<h2>Veuillez confirmer l'étatdu livre ou appuyer sur précédent pour retourner à la recherche</h2>
	
	<br />

	<form action="accepterFinalConfirmation" method="post">
		<input type="text" name="etat" value="${vieilEtat}" />
    	<input type="submit" value="Finaliser l'acceptation du livre" />
    </form>
    
    
    <h2>Un email sera envoyé pour confirmer l'acceptation de ce livre.</h2>
    	
    	
</body>
</html>
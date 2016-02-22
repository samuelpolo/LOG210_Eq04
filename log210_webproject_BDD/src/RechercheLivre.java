import connection.maker.log240.setConnection_log210;

public class RechercheLivre {
	
	private String auteur, titre, ISBN, prix, nbPage;
	
	private boolean nouveauLivre = false;
	
	public RechercheLivre(String identifiantIndustriel){
		
		setConnection_log210 connectionBD = new setConnection_log210();
		
		String[] info = new String[4];
		try{info = connectionBD.readLivre(identifiantIndustriel);}
		catch(Exception e){}
		finally{}
		
		if(info[0]==null||info[0]=="")
		{
			RechercheLivreGoogle googleQuery = new RechercheLivreGoogle(identifiantIndustriel);		
			try{info = connectionBD.readLivre(googleQuery.GetISBN());}
			catch(Exception e){}
			finally{}
			
			if(info[0]==null)
			{
				System.out.println("pas d'info2");
				auteur = googleQuery.GetAuteur();
				titre = googleQuery.GetTitre();
				ISBN = googleQuery.GetISBN();
				nbPage = googleQuery.GetNbPage();
				prix = googleQuery.GetPrix();
				nouveauLivre = true;
			}
			else
			{
				titre = info[0];
				auteur = info[1];
				nbPage = info[3];
				ISBN = googleQuery.GetISBN();
				prix = info[2];
			}
		}
		else
		{
			titre = info[0];
			auteur = info[1];
			nbPage = info[3];
			ISBN = identifiantIndustriel;
			prix = info[2];
		}
		
	}
	
	public String GetAuteur()
	{
		return auteur;
	}
	
	public String GetNbPage()
	{
		return nbPage;
	}
	
	public String GetTitre()
	{		
		return titre;
	}
	
	public String GetPrix()
	{
		return prix;
	}
	
	public String GetISBN()
	{		
		return ISBN;
	}
	public boolean EstNouveauLivre()
	{
		return nouveauLivre;
	}
}

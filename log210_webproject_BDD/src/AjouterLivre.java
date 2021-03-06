import connection.maker.log240.setConnection_log210;

public class AjouterLivre{

	private String currentUser;
	private Livre livreAAjouter;
	
	private boolean nouveauLivre;
	
	private boolean titreTrouver, auteurTrouver, nbPageTrouver, ISBNTrouver, prixTrouver;
	
	public AjouterLivre(String currentUser)
	{
		this.currentUser = currentUser;
		livreAAjouter = new Livre();
		titreTrouver = false;
		auteurTrouver = false;
		nbPageTrouver = false;
		ISBNTrouver = false;
		prixTrouver = false;
	}
	
	public void ChercherInfo(String identifiantIndustriel)
	{
		RechercheLivre queryLivre = new RechercheLivre(identifiantIndustriel);
		if(queryLivre.GetAuteur() != null)
		{
			livreAAjouter.SetAuteur(queryLivre.GetAuteur());
			auteurTrouver = true;
		}
		if(queryLivre.GetISBN() != null)
		{
			livreAAjouter.SetISBN(queryLivre.GetISBN());
			ISBNTrouver = true;
		}
		if(queryLivre.GetTitre() != null)
		{
			livreAAjouter.SetTitre(queryLivre.GetTitre());
			titreTrouver = true;
		}
		if(queryLivre.GetNbPage() != null)
		{
			livreAAjouter.SetNbPage(queryLivre.GetNbPage());
			nbPageTrouver = true;
		}
		if(queryLivre.GetPrix() != null)
		{
			livreAAjouter.SetPrix(Double.parseDouble(queryLivre.GetPrix()));
			prixTrouver = true;
		}
		nouveauLivre = queryLivre.EstNouveauLivre();
	}
	
	public void FinaliserAjoutLivre()
	{
		setConnection_log210 connectionBD = new setConnection_log210();
		if (nouveauLivre)
		{
			try{connectionBD.insertLivre(livreAAjouter.GetTitre(),livreAAjouter.GetAuteur(),
					livreAAjouter.GetISBN(),String.valueOf(livreAAjouter.GetPrixAffiché()), Integer.parseInt(livreAAjouter.GetNbPage()));
			}
			catch(Exception e){}
			finally{}
		}
		try{
		connectionBD.associerLivre(currentUser, livreAAjouter.GetISBN(), livreAAjouter.GetÉtat());
		}
		catch(Exception e){}
		finally{}
	}
	
	public void SetPrixLivre(double prix)
	{
		livreAAjouter.SetPrix(prix);
	}
	
	public void SetAuteurLivre(String auteur)
	{
		livreAAjouter.SetAuteur(auteur);
	}
	
	public void SetTitreLivre(String titre)
	{
		livreAAjouter.SetTitre(titre);
	}
	
	public void SetNbPageLivre(String nbPage)
	{
		livreAAjouter.SetNbPage(nbPage);
	}
	
	public void SetISBNLivre(String isbn)
	{
		livreAAjouter.SetISBN(isbn);
	}
	
	public Livre GetLivre()
	{
		return livreAAjouter;
	}
	
	public boolean PrixEstTrouver()
	{
		return prixTrouver;
	}
	
	public boolean ISBNEstTrouver()
	{
		return ISBNTrouver;
	}
	
	public boolean AuteurEstTrouver()
	{
		return auteurTrouver;
	}
	
	public boolean TitreEstTrouver()
	{
		return titreTrouver;
	}
	
	public boolean NbPageEstTrouver()
	{
		return nbPageTrouver;
	}
}

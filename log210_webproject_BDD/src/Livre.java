
public class Livre {
	
	private String ISBN, auteur, titre, nbPage;
	
	private …tatLivre Ètat;
	private PrixLivre prix;
	
	public Livre()
	{
		Ètat = new …tatLivre();
		prix = new PrixLivre();
	}
	
	public Livre(String isbn, String auteur, String titre, String nbPage)
	{
		Ètat = new …tatLivre();
		prix = new PrixLivre();
		
		SetAuteur(auteur);
		SetISBN(isbn);
		SetTitre(titre);
		SetNbPage(nbPage);
	}
	
	public void SetAuteur(String auteur)
	{
		this.auteur = auteur;
	}
	
	public void SetTitre(String titre)
	{
		this.titre = titre;
	}
	
	public void SetNbPage(String nbPage)
	{
		this.nbPage = nbPage;
	}
	
	public void SetISBN(String isbn)
	{
		ISBN = isbn;
	}
	
	public String GetAuteur()
	{
		return auteur;
	}
	
	public String GetTitre()
	{
		return titre;
	}
	
	public String GetNbPage()
	{
		return nbPage;
	}
	
	public String GetISBN()
	{
		return ISBN;
	}
	
	public int Get…tat()
	{
		return Ètat.Get…tat();
	}
	
	public double GetPrixAffichÈ()
	{
		return prix.GetPrix();
	}
	
	public double GetPrixVente()
	{
		return prix.GetPrix()*Ètat.GetModificateurPrix();
	}
	
	public void SetPrix(double prix)
	{
		this.prix.SetPrix(prix);
	}
	
	public void Set…tat(int Ètat)
	{
		this.Ètat.Set…tat(Ètat);
	}
}

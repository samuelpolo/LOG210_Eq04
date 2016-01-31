
public class Livre {
	
	private String ISBN, auteur, titre, nbPage;
	
	private �tatLivre �tat;
	private PrixLivre prix;
	
	public Livre()
	{
		�tat = new �tatLivre();
		prix = new PrixLivre();
	}
	
	public Livre(String isbn, String auteur, String titre, String nbPage)
	{
		�tat = new �tatLivre();
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
	
	public int Get�tat()
	{
		return �tat.Get�tat();
	}
	
	public double GetPrixAffich�()
	{
		return prix.GetPrix();
	}
	
	public double GetPrixVente()
	{
		return prix.GetPrix()*�tat.GetModificateurPrix();
	}
	
	public void SetPrix(double prix)
	{
		this.prix.SetPrix(prix);
	}
	
	public void Set�tat(int �tat)
	{
		this.�tat.Set�tat(�tat);
	}
}

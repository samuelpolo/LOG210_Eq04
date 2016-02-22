package livrePackage;

public class Livre {
	
	private String ISBN, auteur, titre, nbPage, ID;
	
	private �tatLivre �tat;
	private PrixLivre prix;
	private String proprio;
	
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
	public String GetID(){
		return ID;
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
	
	public String GetProprio(){
		return proprio;
	}
	
	public double GetPrixAffich�()
	{
		return prix.GetPrix();
	}
	
	public double GetPrixVente()
	{
		if(�tat.Get�tat()==1){
			return prix.GetPrix()*0.25;
		}
			
		if (�tat.Get�tat()==2){
			return prix.GetPrix()*0.5;
		}
		
		if(�tat.Get�tat()==3){
			return prix.GetPrix()*0.75;
		}
		return 6.66;
	}
	
	public void SetPrix(double prix)
	{
		this.prix.SetPrix(prix);
	}
	
	public void Set�tat(int �tat)
	{
		this.�tat.Set�tat(�tat);
	}	
	
	public void SetID(String id)
	{
		this.ID = id;
	}
	
	public void SetProprio(String proprio){
		this.proprio = proprio;
	}
}

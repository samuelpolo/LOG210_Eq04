import java.util.ArrayList;

import connection.maker.log240.setConnection_log210;

public class Reservation 
{
	private String currentUser;
	private ArrayList<Livre> livres;
	
	public Reservation(String currentUser)
	{
		this.currentUser = currentUser;
		
		livres = new ArrayList<Livre>();
	}
	
	public void RechercherLivres(String terme)
	{
		setConnection_log210 connectionBD = new setConnection_log210();
		
		ArrayList<String> isbn = connectionBD.returnTitlesContaining(terme);
			
		ArrayList<String> étatNom = connectionBD.returnAssociationContaining(terme);
		
		System.out.println(étatNom);
		
		RechercheLivre infoLivre;
		
		if(!étatNom.isEmpty())
		{
			for(int i = 0 ; i < étatNom.size() ; i+=3){
				livres.add(new Livre());
				livres.get(livres.size()-1).SetÉtat(Integer.parseInt(étatNom.get(i)));
				livres.get(livres.size()-1).SetProprio(étatNom.get(i+1));
				livres.get(livres.size()-1).SetID(étatNom.get(i+2));
				infoLivre = new RechercheLivre(terme);
				livres.get(livres.size()-1).SetISBN(infoLivre.GetISBN());
				livres.get(livres.size()-1).SetAuteur(infoLivre.GetAuteur());
				livres.get(livres.size()-1).SetTitre(infoLivre.GetTitre());
				livres.get(livres.size()-1).SetNbPage(infoLivre.GetNbPage());
				livres.get(livres.size()-1).SetPrix(Double.parseDouble(infoLivre.GetPrix()));
			}

		}
		
		for(int i = 0; i < isbn.size(); i++)
		{
			ArrayList<String> étatsNoms = connectionBD.returnAssociationContaining(isbn.get(i));
			
			for(int j = 0 ; j < étatsNoms.size(); j=j+3)
			{
				livres.add(new Livre());
				livres.get(livres.size()-1).SetÉtat(Integer.parseInt(étatsNoms.get(j)));
				livres.get(livres.size()-1).SetProprio(étatsNoms.get(j+1));
				livres.get(livres.size()-1).SetID(étatsNoms.get(j+2));
				infoLivre = new RechercheLivre(isbn.get(i));
				livres.get(livres.size()-1).SetISBN(infoLivre.GetISBN());
				livres.get(livres.size()-1).SetAuteur(infoLivre.GetAuteur());
				livres.get(livres.size()-1).SetTitre(infoLivre.GetTitre());
				livres.get(livres.size()-1).SetNbPage(infoLivre.GetNbPage());
				livres.get(livres.size()-1).SetPrix(Double.parseDouble(infoLivre.GetPrix()));
			}
		}
	}
	
	public ArrayList<Livre> GetLivres()
	{
		return livres;
	}
	
	public void finaliserReservation(int ID){
		
		setConnection_log210 connectionBD = new setConnection_log210();
		
		connectionBD.insertReservation(currentUser, ID);
		
	}
}

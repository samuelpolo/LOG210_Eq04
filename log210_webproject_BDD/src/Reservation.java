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
			
		ArrayList<String> �tatNom = connectionBD.returnAssociationContaining(terme);
		
		System.out.println(�tatNom);
		
		RechercheLivre infoLivre;
		
		if(!�tatNom.isEmpty())
		{
			for(int i = 0 ; i < �tatNom.size() ; i+=3){
				livres.add(new Livre());
				livres.get(livres.size()-1).Set�tat(Integer.parseInt(�tatNom.get(i)));
				livres.get(livres.size()-1).SetProprio(�tatNom.get(i+1));
				livres.get(livres.size()-1).SetID(�tatNom.get(i+2));
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
			ArrayList<String> �tatsNoms = connectionBD.returnAssociationContaining(isbn.get(i));
			
			for(int j = 0 ; j < �tatsNoms.size(); j=j+3)
			{
				livres.add(new Livre());
				livres.get(livres.size()-1).Set�tat(Integer.parseInt(�tatsNoms.get(j)));
				livres.get(livres.size()-1).SetProprio(�tatsNoms.get(j+1));
				livres.get(livres.size()-1).SetID(�tatsNoms.get(j+2));
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

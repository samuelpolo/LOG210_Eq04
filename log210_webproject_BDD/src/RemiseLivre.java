import java.util.ArrayList;

import connection.maker.log240.setConnection_log210;

public class RemiseLivre 
{
	private String currentCoop;
	private ArrayList<Livre> livres;
	
	public RemiseLivre(String currentCoop)
	{
		this.setCurrentCoop(currentCoop);
		livres = new ArrayList<Livre>();
	}
	
	public void RechercherLivres(String terme)
	{
		setConnection_log210 connectionBD = new setConnection_log210();
		
		ArrayList<String> isbn = connectionBD.returnTitlesContaining(terme);
			
		ArrayList<String> �tatNom = connectionBD.returnAssociationContainingCoopNullParameterISBN(terme);
		
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
		
		�tatNom = null;
		�tatNom = connectionBD.returnAssociationName(terme);
		
		System.out.println(�tatNom);
		
		if(!�tatNom.isEmpty())
		{
			for(int i = 0 ; i < �tatNom.size() ; i+=4){
				livres.add(new Livre());
				livres.get(livres.size()-1).Set�tat(Integer.parseInt(�tatNom.get(i)));
				livres.get(livres.size()-1).SetProprio(�tatNom.get(i+1));
				livres.get(livres.size()-1).SetID(�tatNom.get(i+2));
				infoLivre = new RechercheLivre(�tatNom.get(i+3));
				livres.get(livres.size()-1).SetISBN(infoLivre.GetISBN());
				livres.get(livres.size()-1).SetAuteur(infoLivre.GetAuteur());
				livres.get(livres.size()-1).SetTitre(infoLivre.GetTitre());
				livres.get(livres.size()-1).SetNbPage(infoLivre.GetNbPage());
				livres.get(livres.size()-1).SetPrix(Double.parseDouble(infoLivre.GetPrix()));
			}
		}
		
		for(int i = 0; i < isbn.size(); i++)
		{
			ArrayList<String> �tatsNoms = connectionBD.returnAssociationContainingCoopNullParameterISBN(isbn.get(i));
			
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
		
		
		for(int i = 0; i < livres.size();i++)
		{
			for(int j = i+1; j < livres.size(); j++)
			{
				if(livres.get(i).GetID().matches(livres.get(j).GetID()) && i != j)
				{
					livres.remove(j);
				}
						
			}
		}
		
		for(Livre livreTemp : livres){
			
			System.out.println(livreTemp.GetID());
		
		}
	}
	
	public ArrayList<Livre> getLivres() {
		return livres;
	}

	public void setLivres(ArrayList<Livre> livres) {
		this.livres = livres;
	}

	public void finaliserAcceptation(String coop , int ID,int etat) throws Exception
	{
		setConnection_log210 connectionBD = new setConnection_log210();
		
		connectionBD.changeCoop(coop, ID, etat);
	}

	public String getCurrentCoop() {
		return currentCoop;
	}

	public void setCurrentCoop(String currentCoop) {
		this.currentCoop = currentCoop;
	}
}

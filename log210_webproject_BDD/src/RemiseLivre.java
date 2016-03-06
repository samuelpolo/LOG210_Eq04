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
			
		ArrayList<String> étatNom = connectionBD.returnAssociationContainingCoopNullParameterISBN(terme);
		
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
		
		étatNom = null;
		étatNom = connectionBD.returnAssociationName(terme);
		
		System.out.println(étatNom);
		
		if(!étatNom.isEmpty())
		{
			for(int i = 0 ; i < étatNom.size() ; i+=4){
				livres.add(new Livre());
				livres.get(livres.size()-1).SetÉtat(Integer.parseInt(étatNom.get(i)));
				livres.get(livres.size()-1).SetProprio(étatNom.get(i+1));
				livres.get(livres.size()-1).SetID(étatNom.get(i+2));
				infoLivre = new RechercheLivre(étatNom.get(i+3));
				livres.get(livres.size()-1).SetISBN(infoLivre.GetISBN());
				livres.get(livres.size()-1).SetAuteur(infoLivre.GetAuteur());
				livres.get(livres.size()-1).SetTitre(infoLivre.GetTitre());
				livres.get(livres.size()-1).SetNbPage(infoLivre.GetNbPage());
				livres.get(livres.size()-1).SetPrix(Double.parseDouble(infoLivre.GetPrix()));
			}
		}
		
		for(int i = 0; i < isbn.size(); i++)
		{
			ArrayList<String> étatsNoms = connectionBD.returnAssociationContainingCoopNullParameterISBN(isbn.get(i));
			
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

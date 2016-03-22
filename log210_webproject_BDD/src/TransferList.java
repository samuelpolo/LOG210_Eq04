import java.util.ArrayList;

import connection.maker.log240.setConnection_log210;

public class TransferList 
{
	private String currentUser;
	private String currentCoop;
	private ArrayList<Livre> livres;
	private ArrayList<unTransfert> listeTransfert;
	
	public TransferList(String currentUser)
	{
		this.currentUser = currentUser;
		this.currentCoop = currentUser;
		
		livres = new ArrayList<Livre>();
		listeTransfert = new ArrayList<unTransfert>();
		
		System.out.println(currentCoop);
	}
	
	public void RechercherTransfertAExpedier()
	{
		setConnection_log210 connectionBD = new setConnection_log210();
		
		//ArrayList<String> isbn = connectionBD.returnTitlesContaining(terme);
			
		ArrayList<String> resultatRecherche = connectionBD.returnTransfersAExpedier(currentCoop);
		
		System.out.println(resultatRecherche);
		
		RechercheLivre infoLivre;
		
		if(!resultatRecherche.isEmpty())
		{
			for(int i = 0 ; i < resultatRecherche.size() ; i+=4){
				listeTransfert.add(new unTransfert(resultatRecherche.get(i+2),resultatRecherche.get(i),new Livre()));
				//listeTransfert.get(listeTransfert.size()-1).setId(resultatRecherche.get(i));
				listeTransfert.get(listeTransfert.size()-1).setCoop(resultatRecherche.get(i+1));
				//listeTransfert.get(listeTransfert.size()-1).setAcheteur(resultatRecherche.get(i+2));
				listeTransfert.get(listeTransfert.size()-1).setIdAssociation(resultatRecherche.get(i+3));
				
				infoLivre = new RechercheLivre(connectionBD.getISBNfromAssociationID(resultatRecherche.get(i+3)));
				
				listeTransfert.get(listeTransfert.size()-1).getLivre().SetISBN(infoLivre.GetISBN());
				listeTransfert.get(listeTransfert.size()-1).getLivre().SetAuteur(infoLivre.GetAuteur());
				listeTransfert.get(listeTransfert.size()-1).getLivre().SetTitre(infoLivre.GetTitre());
				listeTransfert.get(listeTransfert.size()-1).getLivre().SetNbPage(infoLivre.GetNbPage());
				listeTransfert.get(listeTransfert.size()-1).getLivre().SetPrix(Double.parseDouble(infoLivre.GetPrix()));
				
				/*livres.add(new Livre());
				livres.get(livres.size()-1).SetÉtat(Integer.parseInt(étatNom.get(i)));
				livres.get(livres.size()-1).SetProprio(étatNom.get(i+1));
				livres.get(livres.size()-1).SetID(étatNom.get(i+2));
				livres.get(livres.size()-1).setCoop(étatNom.get(i+3));
				infoLivre = new RechercheLivre(terme);
				livres.get(livres.size()-1).SetISBN(infoLivre.GetISBN());
				livres.get(livres.size()-1).SetAuteur(infoLivre.GetAuteur());
				livres.get(livres.size()-1).SetTitre(infoLivre.GetTitre());
				livres.get(livres.size()-1).SetNbPage(infoLivre.GetNbPage());
				livres.get(livres.size()-1).SetPrix(Double.parseDouble(infoLivre.GetPrix()));*/
			}

		}
		
		/*for(int i = 0; i < isbn.size(); i++)
		{
			ArrayList<String> étatsNoms = connectionBD.returnAssociationContaining(isbn.get(i));
			
			for(int j = 0 ; j < étatsNoms.size(); j=j+4)
			{
				livres.add(new Livre());
				livres.get(livres.size()-1).SetÉtat(Integer.parseInt(étatsNoms.get(j)));
				livres.get(livres.size()-1).SetProprio(étatsNoms.get(j+1));
				livres.get(livres.size()-1).SetID(étatsNoms.get(j+2));
				livres.get(livres.size()-1).setCoop(étatNom.get(i+3));
				infoLivre = new RechercheLivre(isbn.get(i));
				livres.get(livres.size()-1).SetISBN(infoLivre.GetISBN());
				livres.get(livres.size()-1).SetAuteur(infoLivre.GetAuteur());
				livres.get(livres.size()-1).SetTitre(infoLivre.GetTitre());
				livres.get(livres.size()-1).SetNbPage(infoLivre.GetNbPage());
				livres.get(livres.size()-1).SetPrix(Double.parseDouble(infoLivre.GetPrix()));
			}
		}*/
		

		
	}
	
	public void RechercherTransfertARecevoir()
	{
		setConnection_log210 connectionBD = new setConnection_log210();
		
		//ArrayList<String> isbn = connectionBD.returnTitlesContaining(terme);
			
		ArrayList<String> resultatRecherche = connectionBD.returnTransfersARecevoir(currentCoop);
		
		System.out.println(resultatRecherche);
		
		RechercheLivre infoLivre;
		
		if(!resultatRecherche.isEmpty())
		{
			for(int i = 0 ; i < resultatRecherche.size() ; i+=4){
				listeTransfert.add(new unTransfert(resultatRecherche.get(i+2),resultatRecherche.get(i),new Livre()));
				//listeTransfert.get(listeTransfert.size()-1).setId(resultatRecherche.get(i));
				listeTransfert.get(listeTransfert.size()-1).setCoop(resultatRecherche.get(i+1));
				//listeTransfert.get(listeTransfert.size()-1).setAcheteur(resultatRecherche.get(i+2));
				listeTransfert.get(listeTransfert.size()-1).setIdAssociation(resultatRecherche.get(i+3));
				
				infoLivre = new RechercheLivre(connectionBD.getISBNfromAssociationID(resultatRecherche.get(i+3)));
				
				listeTransfert.get(listeTransfert.size()-1).getLivre().SetISBN(infoLivre.GetISBN());
				listeTransfert.get(listeTransfert.size()-1).getLivre().SetAuteur(infoLivre.GetAuteur());
				listeTransfert.get(listeTransfert.size()-1).getLivre().SetTitre(infoLivre.GetTitre());
				listeTransfert.get(listeTransfert.size()-1).getLivre().SetNbPage(infoLivre.GetNbPage());
				if(infoLivre.GetPrix()==null){
					listeTransfert.get(listeTransfert.size()-1).getLivre().SetPrix(0.00);
				}
				else{
				listeTransfert.get(listeTransfert.size()-1).getLivre().SetPrix(Double.parseDouble(infoLivre.GetPrix()));
				}
				
			}
		}
		
		else{
			System.out.println("!!!!!!!!!!!!!!!!    LA LISTE DES TRANSFERTS RECHERCHÉS EST VIIIIIDE    !!!!!!!!!!!!!!!!!!!");
		}
		

		
	}
	
	
	public ArrayList<unTransfert> getlisteTransfert()
	{
		return listeTransfert;
	}
	
	public void finaliserExpedition(int ID) throws Exception{
		
		setConnection_log210 connectionBD = new setConnection_log210();
		
		connectionBD.expedierTransfert(ID);
	}
	
	public String finaliserReception(int ID) throws Exception{
		
		setConnection_log210 connectionBD = new setConnection_log210();
		
		connectionBD.recevoirTransfert(ID);
		
		return connectionBD.getAcheteurFromTransfert(ID);
	}
}

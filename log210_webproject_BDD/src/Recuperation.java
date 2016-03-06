import java.util.ArrayList;

import connection.maker.log240.setConnection_log210;

public class Recuperation 
{
	private String currentCoop;
	private ArrayList<UneReservation> reservations;
	
	public ArrayList<UneReservation> getReservations() {
		return reservations;
	}

	public void setReservations(ArrayList<UneReservation> reservations) {
		this.reservations = reservations;
	}

	public Recuperation(String currentCoop)
	{
		this.setCurrentCoop(currentCoop);
		reservations = new ArrayList<UneReservation>();
	}
	
	public Recuperation()
	{
		reservations = new ArrayList<UneReservation>();
	}
	
	public ArrayList<UneReservation> ObtenirRerservation()
	{
		setConnection_log210 connectionBD = new setConnection_log210();
		
		ArrayList<String> acheteurIdIsbnEtat = connectionBD.returnAllReservations();
		
		System.out.println(acheteurIdIsbnEtat);
		
		Livre livre;
		RechercheLivre infoLivre;
		
		for(int i = 0 ; i < acheteurIdIsbnEtat.size() ; i+=4){
			livre = new Livre();
			livre.SetÉtat(Integer.parseInt(acheteurIdIsbnEtat.get(i+3)));
			infoLivre = new RechercheLivre(acheteurIdIsbnEtat.get(i+2));
			livre.SetISBN(infoLivre.GetISBN());
			livre.SetAuteur(infoLivre.GetAuteur());
			livre.SetTitre(infoLivre.GetTitre());
			livre.SetNbPage(infoLivre.GetNbPage());
			livre.SetPrix(Double.parseDouble(infoLivre.GetPrix()));
			reservations.add(new UneReservation(acheteurIdIsbnEtat.get(i),acheteurIdIsbnEtat.get(i+1),livre));
		}
		
		return reservations;		
	}

	public String getCurrentCoop() {
		return currentCoop;
	}

	public void setCurrentCoop(String currentCoop) {
		this.currentCoop = currentCoop;
	}
	
	public void finaliserRecuperation(int ID) throws Exception{
		
		setConnection_log210 connectionBD = new setConnection_log210();
		
		connectionBD.supprimerAssociationAPartirDeReservation(ID);

		connectionBD.supprimerReservation(ID);
		
	}
	
}

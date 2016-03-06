
public class UneReservation 
{
	private String acheteur, id;
	private Livre livre;
	
	public UneReservation(String acheteur, String id, Livre livre)
	{
		this.setAcheteur(acheteur);
		this.setId(id);
		this.setLivre(livre);
	}

	public Livre getLivre() {
		return livre;
	}

	public void setLivre(Livre livre) {
		this.livre = livre;
	}

	public String getAcheteur() {
		return acheteur;
	}

	public void setAcheteur(String acheteur) {
		this.acheteur = acheteur;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
}

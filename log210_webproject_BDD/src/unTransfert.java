
public class unTransfert 
{
	private String acheteur, id, coop,idAssociation;
	private Livre livre;
	
	public unTransfert(String acheteur, String id, Livre livre)
	{
		this.setAcheteur(acheteur);
		this.setId(id);
		this.setLivre(livre);
	}
	
	public unTransfert(){
		
	}

	public Livre getLivre() {
		return livre;
	}

	public void setIdAssociation(String idAssociation) {
		this.idAssociation = idAssociation;
	}
	
	public String getIdAssociation() {
		return idAssociation;
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
	
	public String getCoop(){
		return coop;
	}
	
	public void setCoop(String coop){
		this.coop = coop;
	}
	
}

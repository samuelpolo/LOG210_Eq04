

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
//import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.books.Books;
import com.google.api.services.books.Books.Volumes.List;
import com.google.api.services.books.BooksRequestInitializer;
import com.google.api.services.books.model.Volume;
import com.google.api.services.books.model.Volumes;


public class RechercheLivreGoogle {
	
	private String NOM_APPLICATION = "Log240", 
			API_KEY = "AIzaSyDFwz00XUYSkwPwQF7yhcSbhNnCmDfkE04";
	
	private java.util.List<Volume> lesVolumes;
	
	public RechercheLivreGoogle(String demande)
	{		
		System.out.print(demande);
		JacksonFactory jsonFactory = JacksonFactory.getDefaultInstance();
		try{
			
			if(demande.length() == 13 || demande.length() == 10)
			{
				demande = "isbn:" + demande;
			}
			// Création de la demande
		    final Books books = new Books.Builder(GoogleNetHttpTransport.newTrustedTransport(), jsonFactory, null)
		        .setApplicationName(NOM_APPLICATION)
		        .setGoogleClientRequestInitializer(new BooksRequestInitializer(API_KEY))
		        .build();
		    System.out.println("Demande: [" + demande + "]");
		    List listVolumes = books.volumes().list(demande);
	
		    // Envoie de la demande
		    Volumes volumes = listVolumes.execute();
		    
		    lesVolumes = volumes.getItems();
		} catch(Exception e){}
	}
	
	public String GetAuteur()
	{
		String auteur = null;
		try
		{
			return lesVolumes.get(0).getVolumeInfo().getAuthors().get(0);
			
		} catch(Exception e){}
		
		return auteur;
	}
	
	public String GetNbPage()
	{
		String nbPage = null;
		try
		{
			nbPage = String.valueOf(lesVolumes.get(0).getVolumeInfo().getPageCount());
			
		} catch(Exception e){}
		
		return nbPage;
	}
	
	public String GetTitre()
	{
		String titre = null;
		try
		{
			titre = lesVolumes.get(0).getVolumeInfo().getTitle();
			
		} catch(Exception e){}
		
		return titre;
	}
	
	public String GetPrix()
	{
		String prix = null;
		try
		{
			prix = String.valueOf(lesVolumes.get(0).getSaleInfo().getListPrice().getAmount());
	
		} catch(Exception e){}
		
		return prix;
	}
	
	public String GetISBN()
	{
		String isbn = null;

		try
		{
			for(int i = 0; i < lesVolumes.get(0).getVolumeInfo().getIndustryIdentifiers().size(); i++)
			{
				if(lesVolumes.get(0).getVolumeInfo().getIndustryIdentifiers().get(i).getType().matches("ISBN_10"))
				{
					isbn = lesVolumes.get(0).getVolumeInfo().getIndustryIdentifiers().get(i).getIdentifier();
				}
			}
			for(int i = 0; i < lesVolumes.get(0).getVolumeInfo().getIndustryIdentifiers().size(); i++)
			{
				if(lesVolumes.get(0).getVolumeInfo().getIndustryIdentifiers().get(i).getType().matches("ISBN_13"))
				{
					isbn = lesVolumes.get(0).getVolumeInfo().getIndustryIdentifiers().get(i).getIdentifier();
				}
			}

		} catch(Exception e){}
		
		return isbn;
	}

}

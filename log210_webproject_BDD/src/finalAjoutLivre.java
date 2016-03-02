

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import connection.maker.log240.setConnection_log210;

/**
 * Servlet implementation class finalAjoutLivre
 */
@WebServlet("/finalAjoutLivre")
public class finalAjoutLivre extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public finalAjoutLivre() {
        super();
        // TODO Auto-generated constructor stub
        
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
		
		
		AjouterLivre ajoutLivre = (AjouterLivre) request.getSession().getAttribute("livreAjoute");
		
		//R�cup�rer les donn�es entr�es dans le input box
		String etatDuLivreInput = request.getParameter("etat");		
		String titreDuLivreInput = request.getParameter("titre");	
		String auteurDuLivreInput = request.getParameter("auteur");	
		String prixDuLivreInput = request.getParameter("prix");	
		String isbnDuLivreInput = request.getParameter("isbn");	
		String nbPageDuLivreInput = request.getParameter("nbPage");			
		
		ajoutLivre.SetTitreLivre(titreDuLivreInput);
		ajoutLivre.SetAuteurLivre(auteurDuLivreInput);
		ajoutLivre.SetPrixLivre(Double.parseDouble(prixDuLivreInput));
		ajoutLivre.SetISBNLivre(isbnDuLivreInput);
		ajoutLivre.SetNbPageLivre(nbPageDuLivreInput);
		ajoutLivre.SetNbPageLivre(nbPageDuLivreInput);
		
		
		ajoutLivre.FinaliserAjoutLivre(Integer.parseInt(etatDuLivreInput));
		
		request.getRequestDispatcher("AccueilAjoutLivre.jsp").forward(request, response);
		
		
		
	}

}



import java.io.IOException;
import connection.maker.log240.setConnection_log210;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class ajoutLivre
 */
@WebServlet("/notifierLivre")
public class notifierLivre extends HttpServlet {
	private static final long serialVersionUID = 1L;
	setConnection_log210 conn = new setConnection_log210();
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public notifierLivre() {
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
		
		String isbn = request.getParameter("isbn");
		
		User tempUser = (User) request.getSession().getAttribute("user");
		//String test = request.getSession().getAttribute("user");
		
		AjouterLivre ajoutLivre = new AjouterLivre(tempUser.getLogin());
		
		ajoutLivre.ChercherInfo(isbn);
		
		//Passing parameters
		String isbnPasser = ajoutLivre.GetLivre().GetISBN();
		String titrePasser = ajoutLivre.GetLivre().GetTitre();
		String auteurPasser = ajoutLivre.GetLivre().GetAuteur();
		double prixPasser = ajoutLivre.GetLivre().GetPrixAffiché();
		
		request.getSession().setAttribute("isbn", isbnPasser);
		request.getSession().setAttribute("titre", titrePasser);
		request.getSession().setAttribute("auteur", auteurPasser);
		request.getSession().setAttribute("prix", prixPasser);
		
		//ajoutLivre.FinaliserAjoutLivre();		
		
		//Passer les infos du lvire a la prochaine page
		request.getSession().setAttribute("livreAjoute", ajoutLivre);
		request.getRequestDispatcher("AfficherDetailsLivre.jsp").forward(request, response);
		
		
		//response.sendRedirect("ConnectionCompte.jsp");
		
		
	}

}

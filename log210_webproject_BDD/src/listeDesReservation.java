

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class listeDesReservation
 */
@WebServlet("/listeDesReservation")
public class listeDesReservation extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public listeDesReservation() {
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
		
		
				
		User tempUser = (User) request.getSession().getAttribute("user");
		String coop = tempUser.getCoop();
		
		Recuperation recuperation = new Recuperation(coop);
		 
		String isbnRecherche = (String) request.getParameter("info");
		//String nomRecherche = (String) request.getParameter("utilisateurInfos");
		
		
			
				
					
		//Passer les infos du lvire a la prochaine page
		request.getSession().setAttribute("reservationListe3", recuperation);
		request.getSession().setAttribute("livreListe3", recuperation.ObtenirRerservation());
		request.getRequestDispatcher("ResultatReservationListe.jsp").forward(request, response);
	}

}

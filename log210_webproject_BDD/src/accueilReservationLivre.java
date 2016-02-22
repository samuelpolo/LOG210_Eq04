

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class accueilReservationLivre
 */
@WebServlet("/accueilReservationLivre")
public class accueilReservationLivre extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public accueilReservationLivre() {
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
		
		String termeRecherche = (String) request.getParameter("recherche");
		
		User tempUser = (User) request.getSession().getAttribute("user");
		String username = tempUser.getLogin();
		
		Reservation reservation = new Reservation(username);
		
		reservation.RechercherLivres(termeRecherche);
		
				
		//Passer les infos du lvire a la prochaine page
		request.getSession().setAttribute("reservationListe", reservation);
		request.getSession().setAttribute("livreListe", reservation.GetLivres());
		request.getRequestDispatcher("ReservationLivre.jsp").forward(request, response);

		
		
	}

}

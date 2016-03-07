

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class accepterFinalConfirmation
 */
@WebServlet("/accepterFinalConfirmation")
public class accepterFinalConfirmation extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public accepterFinalConfirmation() {
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
		
		int IdURL = (int) request.getSession().getAttribute("idURL");
		int etat =  Integer.parseInt((String) request.getParameter("etat"));
		String proprio =  (String) request.getSession().getAttribute("proprio");
		System.out.println("le email envoyer dans le paramettre proprio: " + proprio);
		String titre =  (String) request.getSession().getAttribute("titre");
		
		RemiseLivre remiseLivre = (RemiseLivre) request.getSession().getAttribute("reservationListe2");		
		
		try {
			
			remiseLivre.finaliserAcceptation(coop,IdURL,etat);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		MailClient envoiEmail = new MailClient(proprio, "Votre livre a bien été remis" ,
				"Votre livre a bien été remis à la Coop" + "Titre : " + titre);
		
		request.getRequestDispatcher("optionsGestionnaire.jsp").forward(request, response);
		
		
	}

}

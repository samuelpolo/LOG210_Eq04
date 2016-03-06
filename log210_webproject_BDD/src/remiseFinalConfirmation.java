

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class remiseFinalConfirmation
 */
@WebServlet("/remiseFinalConfirmation")
public class remiseFinalConfirmation extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public remiseFinalConfirmation() {
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
		
		int IdURL = (int) request.getSession().getAttribute("idURL");
		User tempUser = (User) request.getSession().getAttribute("user");
		String gestionnaire = tempUser.getLogin();
		
		String acheteur = (String) request.getSession().getAttribute("acheteur");
		double prix = (double) request.getSession().getAttribute("prix");
		
		Recuperation recuperation = (Recuperation) request.getSession().getAttribute("reservationListe3");
		
		try {
			recuperation.finaliserRecuperation(IdURL,gestionnaire,acheteur,prix);
			System.out.println("Association Id = " + IdURL + " deleted");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		request.getRequestDispatcher("optionsGestionnaire.jsp").forward(request, response);
		
		
	}

}



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
		RemiseLivre remiseLivre = (RemiseLivre) request.getSession().getAttribute("reservationListe2");		
		
		try {
			
			remiseLivre.finaliserAcceptation(coop,IdURL);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		request.getRequestDispatcher("optionsGestionnaire.jsp").forward(request, response);
		
		
	}

}



import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class transfertARecevoirFinalConfirmation
 */
@WebServlet("/transfertARecevoirFinalConfirmation")
public class transfertARecevoirFinalConfirmation extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public transfertARecevoirFinalConfirmation() {
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
		String acheteur = "";
		
		TransferList transferList = (TransferList) request.getSession().getAttribute("transferAExpedier");
		
		try {
			acheteur = transferList.finaliserReception(IdURL);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		request.getRequestDispatcher("optionsGestionnaire.jsp").forward(request, response);
		
		MailClient envoiEmail = new MailClient(acheteur, "Votre livre est arrivé à la Coop Principale", "Votre livre est arrivé à la Coop Principale THX BRO");
	}
	}



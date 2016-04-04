

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class listeDesTransfertsAExpedier
 */
@WebServlet("/listeDesTransfertsAExpedier")
public class listeDesTransfertsAExpedier extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public listeDesTransfertsAExpedier() {
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
		String coop = tempUser.getPhone();
		
		TransferList transferList = new TransferList(coop);
		transferList.RechercherTransfertAExpedier();
					
		//Passer les infos du lvire a la prochaine page
		request.getSession().setAttribute("transferAExpedier", transferList);
		request.getSession().setAttribute("listeTransfertAExpedier", transferList.getlisteTransfert());
		request.getRequestDispatcher("ResultatTransfertAExpedier.jsp").forward(request, response);
	}

}

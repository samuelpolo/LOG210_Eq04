

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
@WebServlet("/ajoutLivre")
public class ajoutLivre extends HttpServlet {
	private static final long serialVersionUID = 1L;
	setConnection_log210 conn = new setConnection_log210();
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ajoutLivre() {
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
		ajoutLivre.FinaliserAjoutLivre();
		          
		//request.getSession().setAttribute("ajoutLivre", ajoutLivre);
		//request.getRequestDispatcher("AccueilAjoutLivre.jsp").forward(request, response);

		
		
	}

}

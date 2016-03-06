

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class RemiseFinal
 */
@WebServlet("/RemiseFinal")
public class RemiseFinal extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RemiseFinal() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
				response.getWriter().append("Served at: ").append(request.getContextPath());
				
				System.out.println(request.getParameter("id"));
				
				int IdURL = Integer.parseInt(request.getParameter("id"));
				String acheteur = request.getParameter("acheteur");
				double prix = Double.parseDouble(request.getParameter("prix"));
				
				request.getSession().setAttribute("idURL", IdURL);
				request.getSession().setAttribute("acheteur", acheteur);
				request.getSession().setAttribute("prix", prix);
				//request.setAttribute("product", product); // Will be available as ${product} in JSP
		        request.getRequestDispatcher("remiseFinal.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}

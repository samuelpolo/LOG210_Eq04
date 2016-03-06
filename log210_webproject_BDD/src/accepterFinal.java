

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class accepterFinal
 */
@WebServlet("/accepterFinal")
public class accepterFinal extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public accepterFinal() {
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
		System.out.println(request.getParameter("etat2"));

		
		int IdURL = Integer.parseInt(request.getParameter("id"));
		int vieilEtat = Integer.parseInt(request.getParameter("etat2"));				
		
		request.getSession().setAttribute("idURL", IdURL);
		request.getSession().setAttribute("vieilEtat", vieilEtat);
		
		//request.setAttribute("product", product); // Will be available as ${product} in JSP
        request.getRequestDispatcher("AccepterFinal.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}

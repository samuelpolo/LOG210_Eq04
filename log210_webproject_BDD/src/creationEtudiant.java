

import java.io.IOException;
import connection.maker.log240.setConnection_log210;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class creationEtudiant
 */
@WebServlet("/creationEtudiant")
public class creationEtudiant extends HttpServlet {
	private static final long serialVersionUID = 1L;
	setConnection_log210 conn = new setConnection_log210();
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public creationEtudiant() {
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
		
		String login = request.getParameter("login");
		String password = request.getParameter("password");
		String phone = request.getParameter("phone");
		       
		if(!login.isEmpty() && !password.isEmpty()){
			try{
			conn.insertAccount(password,login,"etudiant",phone);
			}
			catch(Exception e){
				
			}
			finally{}

			response.sendRedirect("accueil.jsp");
	}
		else{
			response.sendRedirect("creationEtudiant.jsp");
		}

}
}

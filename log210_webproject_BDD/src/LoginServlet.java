

import java.io.IOException;
import connection.maker.log240.setConnection_log210;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;


/**
 * Servlet implementation class LoginServlet
 */
@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	setConnection_log210 conn = new setConnection_log210();
    /**
     * Default constructor. 
     */
    public LoginServlet() {
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
		
		
		String login = request.getParameter("login");
		String password = request.getParameter("password");
		String nomCoop = request.getParameter("nomCoop");
		String addCoop = request.getParameter("addressCoop");
		       
		if(!login.isEmpty() && !password.isEmpty()){
			try{
			conn.insertAccount(password,login,"gestionnaire",nomCoop);
			}
			catch(Exception e){
				
			}
			finally{}
			
			try{
			conn.insertCooperative(nomCoop,addCoop);
			}
			catch(Exception e){
				
			}
			finally{}
			
			
			//A reviser les trucs de User
		   User currentUser = new User();
		   currentUser.setLogin(login);
		   currentUser.setPassword(password);
		          
		   /*request.getSession().setAttribute("user", currentUser);
		   request.getRequestDispatcher("content.jsp").forward(request, response);*/
		   
		   response.sendRedirect("accueil.jsp");
		          
		} else {
		   response.sendRedirect("LoginPage.jsp");
		}
		
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}

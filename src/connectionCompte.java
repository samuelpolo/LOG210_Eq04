

import java.io.IOException;
import connection.maker.log240.setConnection_log210;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class connectionCompte
 */
@WebServlet("/connectionCompte")
public class connectionCompte extends HttpServlet {
	  
	  public String[] tabLivre = null;
	  public String[] tabCompte = null;
	  
	private static final long serialVersionUID = 1L;
	setConnection_log210 conn = new setConnection_log210();
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public connectionCompte() {
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
		
		String[] loginPass = new String[4];
		       
		if(!login.isEmpty() && !password.isEmpty()){
			try{
			loginPass = conn.connectAccount(login,password);
			}
			catch(Throwable e){
				e.printStackTrace();
			}
			finally{}
			
			if(loginPass[1]!=null){
							
			   User currentUser = new User();
			   currentUser.setLogin(loginPass[1]);
			   currentUser.setPassword(loginPass[0]);
			   currentUser.setPhone(loginPass[3]);
			          
			   request.getSession().setAttribute("user", currentUser);
			   request.getRequestDispatcher("AccueilAjoutLivre.jsp").forward(request, response);
				
			   
			}
			else{
				 System.out.println("tabCompte (returned from the connection class) is empty");
				response.sendRedirect("ConnectionCompte.jsp");
			}
		          
		} else {
		   response.sendRedirect("ConnectionCompte.jsp");
		}
		

	}


}

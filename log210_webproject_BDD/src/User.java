public class User {
 
   private String login, password,phone;
    
   public User(){
      this.login = "anonyme";
      this.password = "default";
      this.phone = "5145555555";
   }
 
   public String getLogin() {
      return login;
   }
 
   public void setLogin(String login) {
      this.login = login;
   }
 
   public String getPassword() {
      return password;
   }
 
   public void setPassword(String password) {
      this.password = password;
   }
   
   public String getPhone(){
	   return phone;
   }
   
   public void setPhone(String phone){
	   this.phone = phone;
   }
}
public class User {
 
   private String login, password,phone,coop,type;
    
   public User(){
      this.login = "anonyme";
      this.password = "default";
      this.phone = null;
      this.coop = null;
      this.type = null;
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
   
   public String getType(){
	   return this.type;
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
   
   public String getCoop(){
	   return coop;
   }
   
   public void setCoop(String coop){
	   this.coop = coop;
   }
   
   public void setType(String type){
	   this.type = type;
   }
}
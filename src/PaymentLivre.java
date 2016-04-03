import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.paypal.api.payments.Amount;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payer;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.RedirectUrls;
import com.paypal.api.payments.Transaction;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.OAuthTokenCredential;
import com.paypal.base.rest.PayPalRESTException;

public class PaymentLivre {

	String lienPaypal;
	String accessToken;
	
	public PaymentLivre(Livre livre)
	{
		Map<String, String> sdkConfig = new HashMap<String, String>();
		sdkConfig.put("mode", "sandbox");

		try {
			accessToken = new OAuthTokenCredential("Ae8B0N2WtQN40TYYPocYsS8XLHkOW0Rsj85HSp_M5tAv7UtihN9FIbLFg_ifhUm00zk9pQ4lYNMMnNkq", "EMi-xDZ9zqbgYjIvzb5-j2B71oAiYRehbfQYBjwdUy17ZIgQpMf024CZ5okA97V7HnH21pjPXVlsfgcN", sdkConfig).getAccessToken();
		} catch (PayPalRESTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Map<String, String> sdkConfig1 = new HashMap<String, String>();
		sdkConfig1.put("mode", "sandbox");

		APIContext apiContext = new APIContext(accessToken);
		apiContext.setConfigurationMap(sdkConfig1);

		Amount amount = new Amount();
		amount.setCurrency("CAD");
		amount.setTotal(String.valueOf(livre.GetPrixVente()));

		Transaction transaction = new Transaction();
		transaction.setDescription("Payment du livre : " + livre.GetTitre());
		transaction.setAmount(amount);

		List<Transaction> transactions = new ArrayList<Transaction>();
		transactions.add(transaction);

		Payer payer = new Payer();
		payer.setPaymentMethod("paypal");

		Payment payment = new Payment();
		payment.setIntent("sale");
		payment.setPayer(payer);
		payment.setTransactions(transactions);
		RedirectUrls redirectUrls = new RedirectUrls();
		redirectUrls.setCancelUrl("https://devtools-paypal.com/guide/pay_paypal?cancel=true");
		redirectUrls.setReturnUrl("https://devtools-paypal.com/guide/pay_paypal?success=true");
		payment.setRedirectUrls(redirectUrls);
		
		Payment createdPayment;
		
		try {
			createdPayment = payment.create(apiContext);
			
			Iterator<Links> links = createdPayment.getLinks().iterator();
			while (links.hasNext()) {
				Links link = links.next();
				if (link.getRel().equalsIgnoreCase("approval_url")) {
					System.out.println(link.getHref());
					lienPaypal = link.getHref();
				}
			}

		} catch (PayPalRESTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public String getLienPaypal() {
		return lienPaypal;
	}	

}

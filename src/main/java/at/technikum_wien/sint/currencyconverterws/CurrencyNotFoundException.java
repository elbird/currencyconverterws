package at.technikum_wien.sint.currencyconverterws;

/**
 * Exception is thrown the currency provided by the user was not found on
 * eurofex-daily
 * 
 * @author Shveta Sohal
 * @author Sebastian Vogel
 * @author Sonja Vollnhofer
 * @author Alexander Wagner
 */
public class CurrencyNotFoundException extends Exception {

	public CurrencyNotFoundException(String message) {
		super(message);
	}

}

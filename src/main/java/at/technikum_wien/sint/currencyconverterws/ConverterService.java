package at.technikum_wien.sint.currencyconverterws;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * Converter service to convert from different currencies to euro and to convert
 * crossrates
 * 
 * @author Shveta Sohal
 * @author Sebastian Vogel
 * @author Sonja Vollnhofer
 * @author Alexander Wagner
 */
@WebService(name = "CurrenyConverter")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public class ConverterService {

	private static String ECB_CURRENCIES_URL = "http://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml";

	/**
	 * Convert from EUR to another currency shortcut method for convert("EUR",
	 * to, value)
	 * 
	 * @param to
	 *            the currency code to which convert
	 * @param value
	 *            the amount to convert
	 * @return the converted values
	 * @throws MalformedURLException
	 *             when eurofxref-daily could not be reached
	 * @throws DocumentException
	 *             when eurofxref-daily could not be reached
	 * @throws CurrencyNotFoundException
	 *             when currency rates are not provided by eurofex-daily
	 */
	@WebMethod(operationName = "from-euro")
	@WebResult(name = "result")
	public double fromEUR(@WebParam(name = "to") String to,
			@WebParam(name = "value") Double value)
			throws MalformedURLException, DocumentException,
			CurrencyNotFoundException {
		return convert("EUR", to, value);
	}

	/**
	 * Convert from a currency to EUR shortcut method for convert(from, "EUR",
	 * value)
	 * 
	 * @param from
	 *            the currency code
	 * @param to
	 *            the currency code to which convert
	 * @param value
	 *            the amount to convert
	 * @return the converted values
	 * @throws MalformedURLException
	 *             when eurofxref-daily could not be reached
	 * @throws DocumentException
	 *             when eurofxref-daily could not be reached
	 * @throws CurrencyNotFoundException
	 *             when currency rates are not provided by eurofex-daily
	 */
	@WebMethod(operationName = "to-euro")
	@WebResult(name = "result")
	public double toEUR(@WebParam(name = "from") String from,
			@WebParam(name = "value") Double value)
			throws MalformedURLException, DocumentException,
			CurrencyNotFoundException {
		return convert(from, "EUR", value);
	}

	/**
	 * converts from any currency to EUR according to eurofex-daily, also
	 * calculates crossrates throws exceptions when eurofxref-daily could not be
	 * reached if calculation could not be processe
	 * 
	 * @param from
	 *            the currency code
	 * @param to
	 *            the currency code to which convert
	 * @param value
	 *            the amount to convert
	 * @return the converted values
	 * @throws MalformedURLException
	 *             when eurofxref-daily could not be reached
	 * @throws DocumentException
	 *             when eurofxref-daily could not be reached
	 * @throws CurrencyNotFoundException
	 *             when currency rates are not provided by eurofex-daily
	 */
	@WebMethod(operationName = "convert")
	@WebResult(name = "result")
	public double convert(@WebParam(name = "from") String from,
			@WebParam(name = "to") String to,
			@WebParam(name = "value") Double value)
			throws MalformedURLException, DocumentException,
			CurrencyNotFoundException {
		double result = 0;
		HashMap<String, Double> currencies = retrieveCurrencies();
		if (value <= 0) {
			result = 0;
		} else if ("EUR".equals(from) && "EUR".equals(to)) {
			result = value;
		} else if ("EUR".equals(to) && currencies.get(from) != null) {
			result = value / currencies.get(from);
		} else if ("EUR".equals(from) && currencies.get(to) != null) {
			result = value * currencies.get(to);
		} else if (currencies.get(to) != null && currencies.get(from) != null) {
			result = (value / currencies.get(from)) * currencies.get(to);
		} else {
			throw new CurrencyNotFoundException("Rates for either \"" + from
					+ "\" or \"" + to + "\" are not provided by eurofex-daily");
		}
		return result;
	}

	private HashMap<String, Double> retrieveCurrencies()
			throws DocumentException, MalformedURLException {
		HashMap<String, Double> currencies = new HashMap<String, Double>();

		URL url = new URL(ECB_CURRENCIES_URL);
		SAXReader reader = new SAXReader();
		Document document = reader.read(url);
		List<Element> nodes = null;
		try {
			nodes = document.selectNodes("//*[@currency]");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		for (Iterator<Element> i = nodes.iterator(); i.hasNext();) {
			Element currency = (Element) i.next();
			currencies.put(currency.attribute("currency").getValue(),
					Double.parseDouble(currency.attribute("rate").getValue()));
		}
		return currencies;
	}
}

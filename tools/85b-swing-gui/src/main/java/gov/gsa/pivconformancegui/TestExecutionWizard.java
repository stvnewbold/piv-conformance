package gov.gsa.pivconformancegui;

import java.awt.Panel;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;



import gov.gsa.pivconformance.card.client.ApplicationAID;
import gov.gsa.pivconformance.card.client.ApplicationProperties;
import gov.gsa.pivconformance.card.client.CardHandle;
import gov.gsa.pivconformance.card.client.ConnectionDescription;
import gov.gsa.pivconformance.card.client.DefaultPIVApplication;
import gov.gsa.pivconformance.card.client.MiddlewareStatus;
import gov.gsa.pivconformance.card.client.PIVAuthenticators;
import gov.gsa.pivconformance.card.client.PIVMiddleware;
import gov.gsa.pivconformance.utils.PCSCUtils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestExecutionWizard extends Panel {
	private static final long serialVersionUID = 1L;
	
	JButton m_nextButton;
	JButton m_runButton;
	JComboBox m_readerCombo;
	JPasswordField m_cardPP;
	
	JTextField m_dbPath;
	
	CardTerminal m_terminal;
	static Logger m_logger = LoggerFactory.getLogger(TestExecutionWizard.class);
	private int m_lastLoginResult = 0;
	
	// This lets a user with a non-standard PCSC configuration control it with a properties file
	// in their home directory
	public void initLibrary()
	{
		PCSCUtils.ConfigureUserProperties();
	}
	
	// Support placing the readers on a dropdown in the GUI
	public List<String> getReaderNames() {
		List<String> readers = PCSCUtils.GetConnectedReaders();
		if(readers.size() == 0) {
			readers.add("Unable to communicate with reader");
		}
		return readers;
	}
	
	// Called when the GUI selection gets changed
	public boolean setReader(String name) {
		// if we get a null name, it's because the dropdown is being rebuilt. ignore it.
		if(name == null) return true;
		m_terminal = PCSCUtils.TerminalForReaderName(name);
		return m_terminal == null;
	}
	
	// get the last reset counter from a login, in the event login was called with an invalid PIN
	public int getLastLoginResult() {
		return m_lastLoginResult;
	}
	
	// log into the card using the PIN from the GUI. This currently assumes the application PIN
	// but will likely get a checkbox and a flag to use global
	public boolean performLogin(char[] pin, boolean useAppPin)
	{
		if(m_terminal == null) return false;
		try {
			if(!m_terminal.isCardPresent()) {
				m_logger.error("No card present in {}", m_terminal.getName());
				return false;
			}
		} catch (CardException e) {
			m_logger.error("Exception when querying terminal for card", e);
			return false;
		}
		PIVAuthenticators pivAuthenticators = new PIVAuthenticators();
		if (useAppPin) {
			pivAuthenticators.addApplicationPin(new String (pin));
		} else {
			pivAuthenticators.addGlobalPin(new String(pin));
		}

		ConnectionDescription cd = ConnectionDescription.createFromTerminal(m_terminal);
		CardHandle ch = new CardHandle();
		MiddlewareStatus result = PIVMiddleware.pivConnect(false, cd, ch);
		if(result != MiddlewareStatus.PIV_OK) return false;
		DefaultPIVApplication piv = new DefaultPIVApplication();
        ApplicationProperties cardAppProperties = new ApplicationProperties();
		ApplicationAID aid = new ApplicationAID();
		result = piv.pivSelectCardApplication(ch, aid, cardAppProperties);
		if(result != MiddlewareStatus.PIV_OK) return false;
		result = piv.pivLogIntoCardApplication(ch, pivAuthenticators.getBytes());
		if(result == MiddlewareStatus.PIV_AUTHENTICATION_FAILURE) {
			int tries = PCSCUtils.StatusWordsToRetries(piv.getLastResponseAPDUBytes());
			m_logger.info("Login failed. Application PIN: {} retries remain", tries);
			m_lastLoginResult = tries;
			return false;
		}
		m_lastLoginResult = 0;
		m_logger.info("Login successful.");
		return true;
	}
	

	
	public byte[] getATR()
	{
		if(m_terminal == null) {
			m_logger.error("No card reader found.");
			return null;
		}
		try {
			if(!m_terminal.isCardPresent()) {
				m_logger.error("No card is available in the selected reader");
				return null;
			}
		} catch (CardException e) {
			m_logger.error("Unable to communicate with card.", e);
			return null;
		}
		ConnectionDescription cd = ConnectionDescription.createFromTerminal(m_terminal);
		CardHandle ch = new CardHandle();
		MiddlewareStatus result = PIVMiddleware.pivConnect(false, cd, ch);
		if(result != MiddlewareStatus.PIV_OK) {
			m_logger.error("PIV connection error");
			return null;
		}
		return ch.getCard().getATR().getBytes();
	}
	

	public int getEncodedRetries()
	{
		int rv = -1;
		
		if(m_terminal == null) return -1;
		try {
			if(!m_terminal.isCardPresent()) {
				m_logger.error("No card present in {}", m_terminal.getName());
				return -1;
			}
		} catch (CardException e) {
			m_logger.error("Exception when querying terminal for card", e);
			return -1;
		}
		
		ConnectionDescription cd = ConnectionDescription.createFromTerminal(m_terminal);
		CardHandle ch = new CardHandle();
		MiddlewareStatus result = PIVMiddleware.pivConnect(false, cd, ch);

		try {
			if(result != MiddlewareStatus.PIV_OK)
				return -1;
			
			try {
				// Make sure we're logged out, as this call doesn't work if logged in
				ch.getCard().disconnect(true);
				result = PIVMiddleware.pivConnect(false, cd, ch);
			} catch (CardException e) {
				m_logger.debug("Attempt at card reset failed. Trying to proceed.");
			}
		
			DefaultPIVApplication piv = new DefaultPIVApplication();
	        ApplicationProperties cardAppProperties = new ApplicationProperties();
			ApplicationAID aid = new ApplicationAID();
			result = piv.pivSelectCardApplication(ch, aid, cardAppProperties);
			if(result != MiddlewareStatus.PIV_OK)
				return -1;
			
			PIVAuthenticators pivAuthenticators = new PIVAuthenticators();
			
			pivAuthenticators.addApplicationPin("");
			result = piv.pivLogIntoCardApplication(ch, pivAuthenticators.getBytes());
			if(result == MiddlewareStatus.PIV_AUTHENTICATION_FAILURE) {
				rv = (PCSCUtils.StatusWordsToRetries(piv.getLastResponseAPDUBytes()) & 0xf) << 4;
			}

			pivAuthenticators = new PIVAuthenticators();
			
			pivAuthenticators.addGlobalPin("");
			result = piv.pivLogIntoCardApplication(ch, pivAuthenticators.getBytes());
			if(result == MiddlewareStatus.PIV_AUTHENTICATION_FAILURE) {
				rv |= PCSCUtils.StatusWordsToRetries(piv.getLastResponseAPDUBytes()) & 0xf;
				m_logger.info("Global PIN: {} retries remain", rv);
			}
			return rv;
		} catch (Exception ex) {
			m_logger.error("Error: {}", ex.getLocalizedMessage());
		}
		return rv;
	}


}

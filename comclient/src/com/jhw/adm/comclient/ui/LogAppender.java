package com.jhw.adm.comclient.ui;

import javax.swing.SwingUtilities;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

/**
 * 
 * @author xiongbo
 * 
 */
public class LogAppender extends AppenderSkeleton {
	// private LogPanel logPanel;
	// private LogPanel logPanel2;

	// public LogAppender() {
	// System.out.println("m_objContext3:" + m_objContext + " " + logPanel);
	// }

	@Override
	protected void append(final LoggingEvent event) {
		// System.out.println("***:" + event.getMessage() + ":" +
		// event.getLevel()
		// + ":" + event.getClass());
		// LogPanel.addLog("***:" + event.getMessage() + ":" + event.getLevel()
		// + ":" + event.getClass());
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				LogPanel.addLog(event);
			}
		});
		// System.out.println("m_objContext:" + logPanel + " " + logPanel + " "
		// + logPanel2);
	}

	@Override
	public void close() {

	}

	@Override
	public boolean requiresLayout() {

		return false;
	}

	// public LogPanel getLogPanel() {
	// return logPanel;
	// }
	//
	// public void setLogPanel(LogPanel logPanel) {
	// this.logPanel = logPanel;
	// }

	// @Override
	// public void setApplicationContext(ApplicationContext arg0)
	// throws BeansException {
	// m_objContext = arg0;
	// }
	//
	// protected ApplicationContext m_objContext;
	//
	// @Override
	// public void afterPropertiesSet() throws Exception {
	// System.out.println("m_objContext2:" + m_objContext + " " + logPanel);
	// logPanel2 = logPanel;
	// System.out.println("m_objContext22:" + m_objContext + " " + logPanel2);
	//
	// }

}

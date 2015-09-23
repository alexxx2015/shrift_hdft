package edu.tum.uc.tomcat;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class TomcatServletListener implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		System.out.println("TOMCATSERVLETLISTENER contextDestroyed: "+arg0.getServletContext().getServletContextName());
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		System.out.println("TOMCATSERVLETLISTENER contextInitialized: "+arg0.getServletContext().getServletContextName());
	}

}

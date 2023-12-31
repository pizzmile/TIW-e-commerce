package it.polimi.tiw.project.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.project.beans.User;
import it.polimi.tiw.project.utils.ConnectionHandler;

@WebServlet("/home")
public class HomeController extends HttpServlet {
	
	private static final long serialVersionUID = 1L;	// session id
	private TemplateEngine templateEngine;				// engine to display page
	private Connection connection = null;				// connection to DB

	public HomeController() {
		super();
	}

	public void init() throws ServletException {
		ServletContext servletContext = getServletContext();													// get servlet context
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);	// get template resolver
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();																// get template engine
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
		templateResolver.setCharacterEncoding("UTF-8");
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// set request encoding to match the project character encoding (utf-8)
		request.setCharacterEncoding("UTF-8");
		
		// If the user is not logged in (not present in session) redirect to the login
		String loginpath = getServletContext().getContextPath() + "/index.html";
		HttpSession session = request.getSession();
		if (session.isNew() || session.getAttribute("user") == null) {
			response.sendRedirect(loginpath);
			return;
		}
		
		User user = (User) session.getAttribute("user");

		// Redirect to the Home page and add missions to the parameters
		String path = "/WEB-INF/Home.html";
		ServletContext servletContext = getServletContext();
		final WebContext context = new WebContext(request, response, servletContext, request.getLocale());
		context.setVariable("user", user);
		System.out.println(user.getName());
		System.out.println(user.getSurname());
		templateEngine.process(path, context, response.getWriter());
	}


	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}

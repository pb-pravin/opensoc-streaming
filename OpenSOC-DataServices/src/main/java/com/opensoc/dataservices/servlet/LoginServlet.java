package com.opensoc.dataservices.servlet;

import java.io.IOException;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.opensoc.dataservices.auth.AuthToken;

public class LoginServlet extends HttpServlet 
{
	private static final Logger logger = LoggerFactory.getLogger( LoginServlet.class );
	
	private static final long serialVersionUID = 1L;

	@Inject
	private Properties configProps;
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException 
	{
		doPost( req, resp );
	}
	
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException 
	{	
		String username = req.getParameter("username" );
		String password = req.getParameter("password" );
		UsernamePasswordToken token = new UsernamePasswordToken(username, password);
	
		logger.info( "Doing login for user: " + username );
		
		Subject currentUser = SecurityUtils.getSubject();

		try 
		{
		    currentUser.login(token);
		} 
		catch ( UnknownAccountException uae ) 
		{
			logger.warn( "Failing login with 405:", uae );
			resp.sendError(405);
			return;
		} 
		catch ( IncorrectCredentialsException ice ) 
		{
			logger.warn( "Failing login with 405:", ice );
			resp.sendError(405);
			return;
		} 
		catch ( LockedAccountException lae ) 
		{
			logger.warn( "Failing login with 405:", lae ); 
			resp.sendError(405);
			return;
		} 
		catch ( ExcessiveAttemptsException eae ) 
		{
			logger.warn( "Failing login with 405:", eae );
			resp.sendError(405);
			return;
		}  
		catch ( AuthenticationException ae ) 
		{
			logger.warn( "Failing login with 405:", ae );
			resp.sendError(405);
			return;
		}
		
		try
		{
		
			Cookie authTokenCookie = new Cookie("authToken", AuthToken.generateToken(configProps));
			resp.addCookie(authTokenCookie);
		}
		catch( Exception e )
		{
			logger.error( "Failed creating authToken cookie.", e );
			resp.sendError( 500 );
			return;
		}
		
		// resp.setStatus(HttpServletResponse.SC_OK);
		resp.sendRedirect( "/withsocket.jsp" );
	}	
}
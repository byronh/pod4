/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.IOException;
import javax.faces.application.ResourceHandler;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author mduppes
 * 
 * This filter class was made to handle redirections based on session status.
 * For example, if the user already had a valid session cookie (logged in previously)
 * The user will skip the log-in page and go directly to the schedule view.
 * 
 * doFilter() is evaluated every time that the user traverses to a /faces/* page.
 */
@WebFilter("/faces/*")
public class LoginFilter implements Filter {
    
    
    public LoginFilter() {
    }    
    
    

    /**
     *
     * @param request The servlet request we are processing
     * @param response The servlet response we are creating
     * @param chain The filter chain we are processing
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet error occurs
     */
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String currentURL = httpRequest.getRequestURI().toString();
        System.out.println("LoginFilter::doFilter - Accessing URL: " + currentURL);
            
        if (currentURL.contains(ResourceHandler.RESOURCE_IDENTIFIER) || httpRequest.getUserPrincipal() == null) {
            // We want to normally display when we are loading a resource or there is no user logged in
            System.out.println("LoginFilter::doFilter - Resource or null");
            chain.doFilter(request, response);
        } else if (httpRequest.getUserPrincipal() != null && currentURL.contains("login.xhtml")) {
            // Does redirecting on client side so the URL changes, instead of a server forward which doesn't change URL.
            System.out.println("LoginFilter::doFilter - Log in page + session exists: " + httpRequest.getSession().getId());
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/faces/facelets/ScheduleView.xhtml");
            //httpRequest.getRequestDispatcher("/faces/facelets/ScheduleView.xhtml").forward(request, response);
        } else {
            System.out.println("Filter: Displaying normally");
            chain.doFilter(request, response);
        }
}


    /**
     * Destroy method for this filter
     */
    public void destroy() {        
    }

    /**
     * Init method for this filter
     */
    public void init(FilterConfig filterConfig) {        

    }
    
}

/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.uvms.reporting.rest.filter;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;





import eu.europa.ec.fisheries.uvms.reporting.rest.constants.RestConstants;

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
 **/
public class CrossOriginFilter implements Filter {

    final static Logger LOG = LoggerFactory.getLogger(CrossOriginFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        LOG.info("Requstfilter starting up!");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse res, FilterChain chain) throws IOException, ServletException {
         HttpServletResponse response = (HttpServletResponse) res;
        response.setHeader(RestConstants.ACCESS_CONTROL_ALLOW_ORIGIN, RestConstants.ACCESS_CONTROL_ALLOW_METHODS_ALL);
        response.setHeader(RestConstants.ACCESS_CONTROL_ALLOW_METHODS, RestConstants.ACCESS_CONTROL_ALLOWED_METHODS);
        response.setHeader(RestConstants.ACCESS_CONTROL_ALLOW_HEADERS, ((HttpServletRequest)request).getHeader("Access-Control-Request-Headers"));
        chain.doFilter(request, res);
    }

    @Override
    public void destroy() {
        LOG.info("Requstfilter shuting down!");
    }

}
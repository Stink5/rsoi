package stink5.oauth2.lab2;

import static java.nio.charset.StandardCharsets.*;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;

public class ApplicationInitializer implements WebApplicationInitializer {

    private static final String CONFIG_LOCATION = "classpath:/spring/app-context.xml";
    private static final String MAPPING_ANY_URL = "/*";

    @Override
    public void onStartup(final ServletContext servletContext) throws ServletException {
        final WebApplicationContext context = getContext();

        servletContext.addListener(new ContextLoaderListener(context));

        final ServletRegistration.Dynamic dispatcher =
            servletContext.addServlet("DispatcherServlet", new DispatcherServlet(context)
        );
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping(MAPPING_ANY_URL);

        final CharacterEncodingFilter encodingFilter = new CharacterEncodingFilter();
        encodingFilter.setEncoding(UTF_8.name());
        encodingFilter.setForceEncoding(true);

        final FilterRegistration.Dynamic encodingFilterDynamic = (
            servletContext.addFilter("charEncodingFilter", encodingFilter)
        );
        encodingFilterDynamic.addMappingForUrlPatterns(
            EnumSet.allOf(DispatcherType.class), true, MAPPING_ANY_URL
        );

        final FilterRegistration.Dynamic securityFilter = (
            servletContext.addFilter(
                AbstractSecurityWebApplicationInitializer.DEFAULT_FILTER_NAME,
                DelegatingFilterProxy.class
            )
        );
        securityFilter.addMappingForUrlPatterns(
            EnumSet.allOf(DispatcherType.class), false, MAPPING_ANY_URL
        );
    }

    private WebApplicationContext getContext() {
        final XmlWebApplicationContext context = new XmlWebApplicationContext();
        context.setConfigLocation(CONFIG_LOCATION);
        return context;
    }

}
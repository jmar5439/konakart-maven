package com;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.struts2.tiles.StrutsTilesContainerFactory;
import org.apache.tiles.TilesApplicationContext;
import org.apache.tiles.TilesContainer;
import org.apache.tiles.context.TilesRequestContextFactory;
import org.apache.tiles.definition.DefinitionsFactoryException;
import org.apache.tiles.evaluator.AttributeEvaluatorFactory;
import org.apache.tiles.factory.AbstractTilesContainerFactory;
import org.apache.tiles.factory.BasicTilesContainerFactory;
import org.apache.tiles.freemarker.renderer.FreeMarkerAttributeRenderer;
import org.apache.tiles.renderer.impl.BasicRendererFactory;

public class CustomTilesContainerFactory extends BasicTilesContainerFactory {
	
	 private static final Map<String, String> INIT;

	    static {
	        INIT = new HashMap<String, String>();
	        INIT.put(AbstractTilesContainerFactory.CONTAINER_FACTORY_INIT_PARAM,
	                 StrutsTilesContainerFactory.class.getName());
	    }

	
	@Override
	protected List<URL> getSourceURLs(TilesApplicationContext applicationContext,
	        TilesRequestContextFactory contextFactory) {
		 List<URL> urls = new ArrayList<URL>();
	        try {
	            urls.add(applicationContext.getResource("/WEB-INF/classes/tiles.xml"));
	        } catch (IOException e) {
	            throw new DefinitionsFactoryException(
	                    "Cannot load definition URLs", e);
	        }
	        return urls;
	}
	
	@Override
	protected void registerAttributeRenderers(
			BasicRendererFactory rendererFactory,
			TilesApplicationContext applicationContext,
			TilesRequestContextFactory contextFactory,
			TilesContainer container,
			AttributeEvaluatorFactory attributeEvaluatorFactory) {
		// TODO Auto-generated method stub
		super.registerAttributeRenderers(rendererFactory, applicationContext,
				contextFactory, container, attributeEvaluatorFactory);
		 FreeMarkerAttributeRenderer freemarkerRenderer = new FreeMarkerAttributeRenderer();
		    freemarkerRenderer.setApplicationContext(applicationContext);
		  
		    freemarkerRenderer.setRequestContextFactory(contextFactory);
		    freemarkerRenderer.setParameter("TemplatePath", "/");
		    freemarkerRenderer.setParameter("NoCache", "true");
		    freemarkerRenderer.setParameter("ContentType", "text/html");
		    freemarkerRenderer.setParameter("template_update_delay", "0");
		    freemarkerRenderer.setParameter("default_encoding", "ISO-8859-1");
		    freemarkerRenderer.setParameter("number_format", "0.##########");
		    freemarkerRenderer.setParameter("ObjectWrapper", "beans");
		   
		    freemarkerRenderer.commit();
		    rendererFactory.registerRenderer("freemarker", freemarkerRenderer);
		
		
	}
	
}

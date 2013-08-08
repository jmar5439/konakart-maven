package com;

import org.apache.tiles.startup.TilesInitializer;
import org.apache.tiles.web.startup.AbstractTilesListener;

public class CustomTilesListener extends AbstractTilesListener {

	@Override
	protected TilesInitializer createTilesInitializer() {
		// TODO Auto-generated method stub
		return new  CustomTilesInitialitzer();
	}
	  
	     

}

package furgl.improvedHoes;

import furgl.improvedHoes.config.Config;
import net.fabricmc.api.ModInitializer;

public class ImprovedHoes implements ModInitializer {
	
	/*** Changelog
	 * 
	 */

	public static final String MODNAME = "Improved Hoes";
	public static final String MODID = "improvedhoes";

	@Override
	public void onInitialize() {
		Config.init();
		EventHandler.init();
	}
	
}
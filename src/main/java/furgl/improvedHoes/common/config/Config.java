package furgl.improvedHoes.common.config;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import furgl.improvedHoes.common.ImprovedHoes;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Config {
	public static Configuration config;
	public static ArrayList<String> radius0Hoes;
	public static ArrayList<String> radius1Hoes;
	public static ArrayList<String> radius2Hoes;
	public static ArrayList<String> radius3Hoes;
	private static String[] default0Radius = {"Wooden Hoe"};
	private static String[] default1Radius = {"Stone Hoe"};
	private static String[] default2Radius = {"Iron Hoe", "Golden Hoe"};
	private static String[] default3Radius = {"Diamond Hoe"};

	public static void init(final File file) {
		Config.config = new Configuration(file);
		Config.config.load();
		Config.syncConfig();
		Config.config.save();
	}
	
	public static void syncConfig() {
		Property radius0HoesProp = Config.config.get(Configuration.CATEGORY_GENERAL, "1x1 Hoes", default0Radius, "Hoes with a 1x1 block range.");
		Config.radius0Hoes = new ArrayList<String>(Arrays.asList(radius0HoesProp.getStringList()));
		Property radius1HoesProp = Config.config.get(Configuration.CATEGORY_GENERAL, "3x3 Hoes", default1Radius, "Hoes with a 3x3 block range.");
		Config.radius1Hoes = new ArrayList<String>(Arrays.asList(radius1HoesProp.getStringList()));
		Property radius2HoesProp = Config.config.get(Configuration.CATEGORY_GENERAL, "5x5 Hoes", default2Radius, "Hoes with a 5x5 block range.");
		Config.radius2Hoes = new ArrayList<String>(Arrays.asList(radius2HoesProp.getStringList()));
		Property radius3HoesProp = Config.config.get(Configuration.CATEGORY_GENERAL, "7x7 Hoes", default3Radius, "Hoes with a 7x7 block range.");
		Config.radius3Hoes = new ArrayList<String>(Arrays.asList(radius3HoesProp.getStringList()));
		Config.config.save();
	}
	
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onConfigChanged(final ConfigChangedEvent.OnConfigChangedEvent event) {
		if (event.getModID().equals(ImprovedHoes.MODID)) 
			Config.syncConfig();
	}
}
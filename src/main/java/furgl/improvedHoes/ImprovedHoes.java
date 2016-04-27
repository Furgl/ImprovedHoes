package furgl.improvedHoes;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid = ImprovedHoes.MODID, name = ImprovedHoes.MODNAME, version = ImprovedHoes.VERSION, guiFactory = "furgl.improvedHoes.ImprovedHoesGuiFactory")
public class ImprovedHoes
{
	public static final String MODID = "improvedHoes";
	public static final String MODNAME = "ImprovedHoes";
	public static final String VERSION = "1.0";

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		Config.init(event.getSuggestedConfigurationFile());	
		FMLCommonHandler.instance().bus().register(new ConfigChangedEvents());
		MinecraftForge.EVENT_BUS.register(new UseHoeEvents()); 
		MinecraftForge.EVENT_BUS.register(new PlayerInteractEvents()); 
		MinecraftForge.EVENT_BUS.register(new HarvestCheckEvents());
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		
	}

	public static boolean isRegisteredHoe(ItemStack stack)
	{
		if (Config.radius0Hoes.contains(stack.getDisplayName()) || 
				Config.radius1Hoes.contains(stack.getDisplayName()) ||
				Config.radius2Hoes.contains(stack.getDisplayName()) ||
				Config.radius3Hoes.contains(stack.getDisplayName()))
			return true;
		return false;
	}

	public static int calculateRadius(ItemStack stack) 
	{
		int radius = 0;
		if (Config.radius0Hoes.contains(stack.getDisplayName()))
			radius = 0;
		else if (Config.radius1Hoes.contains(stack.getDisplayName()))
			radius = 1;
		else if (Config.radius2Hoes.contains(stack.getDisplayName()))
			radius = 2;
		else if (Config.radius3Hoes.contains(stack.getDisplayName()))
			radius = 3;
		return radius;
	}
}


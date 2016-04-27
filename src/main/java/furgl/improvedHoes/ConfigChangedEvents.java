package furgl.improvedHoes;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class ConfigChangedEvents 
{
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onConfigChanged(final ConfigChangedEvent.OnConfigChangedEvent event) 
	{
		if (event.modID.equals(ImprovedHoes.MODID)) 
			Config.syncConfig();
	}
}

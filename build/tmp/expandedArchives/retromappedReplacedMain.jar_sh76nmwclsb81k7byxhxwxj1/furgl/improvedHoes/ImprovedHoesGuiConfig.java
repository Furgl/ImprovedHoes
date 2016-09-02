package furgl.improvedHoes;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

public class ImprovedHoesGuiConfig extends GuiConfig 
{
	public ImprovedHoesGuiConfig(GuiScreen parent) 
	{
		super(parent, getConfigElements(), ImprovedHoes.MODID, false, false, "Improved Hoes Configuration");
	}

	private static List<IConfigElement> getConfigElements() {
        final List<IConfigElement> list = new ArrayList<IConfigElement>();
        list.addAll(new ConfigElement(Config.config.getCategory(Configuration.CATEGORY_GENERAL)).getChildElements());
        return list;
    }
}
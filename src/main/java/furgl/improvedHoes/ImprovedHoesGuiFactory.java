package furgl.improvedHoes;

import java.util.Set;

import cpw.mods.fml.client.IModGuiFactory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

public class ImprovedHoesGuiFactory implements IModGuiFactory 
{
    @Override
	public void initialize(Minecraft minecraftInstance) 
    {
 
    }
 
    @Override
	public Class<? extends GuiScreen> mainConfigGuiClass() 
    {
        return ImprovedHoesGuiConfig.class;
    }
 
    @Override
	public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() 
    {
        return null;
    }
 
    @Override
	public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement element) 
    {
        return null;
    }
}
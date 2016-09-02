package furgl.improvedHoes;

import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;

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
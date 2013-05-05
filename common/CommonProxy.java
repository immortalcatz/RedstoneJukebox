package sidben.redstonejukebox.common;

import sidben.redstonejukebox.ModRedstoneJukebox;
import sidben.redstonejukebox.client.GuiRecordTrading;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.*;
import net.minecraft.src.*;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;


public class CommonProxy implements IGuiHandler {

	public static String textureSheet = 		"/resources/redstonejukebox.png";
	public static String redstoneJukeboxGui = 	"/resources/redstonejukebox-gui.png";
	public static String recordTradeGui = 		"/resources/recordtrading-gui.png";
	
	
	
	/*-------------------------------------------------------------------
		Server Logic
	-------------------------------------------------------------------*/

	
	// returns an instance of the Container
	@Override
	public Object getServerGuiElement(int guiID, EntityPlayer player, World world, int x, int y, int z)
	{
		if (guiID ==  ModRedstoneJukebox.redstoneJukeboxGuiID)
		{
			TileEntityRedstoneJukebox teJukebox = (TileEntityRedstoneJukebox)world.getBlockTileEntity(x, y, z);
			return new ContainerRedstoneJukebox(player.inventory, teJukebox);
		}

		else if (guiID ==  ModRedstoneJukebox.recordTradingGuiID)
		{
			// OBS: The X value is the EntityID - facepalm courtesy of http://www.minecraftforge.net/forum/index.php?topic=1671.0
			// OBS 2: Not all villagers can trade records, so there is an extra condition.
			Entity villager = world.getEntityByID(x);
			if (villager instanceof EntityVillager && CustomRecordHelper.canTradeRecords(x))
			{
				CustomRecordHelper.validateOffers(CustomRecordHelper.getStoreID(x));				// revalidates the offers before opening the GUI
				return new ContainerRecordTrading(player.inventory, (EntityVillager)villager, world);
			}
		}

		return null;
	}
	
	
	
	
	/*-------------------------------------------------------------------
		Client Logic
	-------------------------------------------------------------------*/
	public void registerRenderers() {
	}

	
	// returns an instance of the Gui 
    @Override
	public Object getClientGuiElement(int guiID, EntityPlayer player, World world, int x, int y, int z)
	{
		return null;
	}

	
}
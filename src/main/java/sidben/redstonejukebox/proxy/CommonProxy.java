package sidben.redstonejukebox.proxy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import sidben.redstonejukebox.ModRedstoneJukebox;
import sidben.redstonejukebox.handler.SoundEventHandler;
import sidben.redstonejukebox.init.MyBlocks;
import sidben.redstonejukebox.init.MyItems;
import sidben.redstonejukebox.init.MyRecipes;
import sidben.redstonejukebox.inventory.ContainerRedstoneJukebox;
import sidben.redstonejukebox.network.JukeboxGUIUpdatedMessage;
import sidben.redstonejukebox.network.NetworkHelper;
import sidben.redstonejukebox.reference.Reference;
import sidben.redstonejukebox.tileentity.TileEntityRedstoneJukebox;


/*
 * Base proxy class, here I initialize everything that must happen on both, server and client.
 * 
 */
public abstract class CommonProxy implements IProxy
{

    @Override
    public World getClientWorld() {
        return null;
    }
    

    @Override
    public void pre_initialize()
    {
        // Register network messages
        ModRedstoneJukebox.NetworkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.ModChannel);
        ModRedstoneJukebox.NetworkWrapper.registerMessage(NetworkHelper.JukeboxGUIHandler.class, JukeboxGUIUpdatedMessage.class, 0, Side.SERVER);
        

        // Register items
        MyItems.register();

        
        // Register blocks
        MyBlocks.register();
    }


    @Override
    public void initialize()
    {
        // Recipes
        MyRecipes.register();

        
        // Achievements

        
        // Event Handlers
        MinecraftForge.EVENT_BUS.register(new SoundEventHandler());

    }


    @Override
    public void post_initialize()
    {
    }


    /*
     * NOTE:
     * I tried overriding [getServerGuiElement] on my ServerProxy class, but it wasn't being called at all.
     * 
     * Server side only calls this one (tested on singleplayer), so I added the container logic here.
     * From what I can see, no one uses a proxy just for server, all codes I check have a "common" proxy
     * with the server stuff and a "client" proxy that inherits it, just for client stuff (textures, icons).
     * 
     */
   
    // returns an instance of the Container
    @Override
    public Object getServerGuiElement(int guiID, EntityPlayer player, World world, int x, int y, int z)
    {
        if (guiID == ClientProxy.redstoneJukeboxGuiID) {
            TileEntityRedstoneJukebox teJukebox = (TileEntityRedstoneJukebox) world.getTileEntity(x, y, z);
            return new ContainerRedstoneJukebox(player.inventory, teJukebox);
        }

        return null;
    }

    
    @Override
    public Object getClientGuiElement(int guiID, EntityPlayer player, World world, int x, int y, int z)
    {
        return null;
    }


}

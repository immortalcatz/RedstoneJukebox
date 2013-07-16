package sidben.redstonejukebox.common;

import java.util.Iterator;
import java.util.List;
import sidben.redstonejukebox.ModRedstoneJukebox;
import sidben.redstonejukebox.helper.CustomRecordHelper;
import sidben.redstonejukebox.helper.CustomRecordObject;
import sidben.redstonejukebox.helper.PacketHelper;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockJukeBox;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;


public class ItemCustomRecord extends ItemRecord
{

    @SideOnly(Side.CLIENT)
    private Icon[] iconArray;
    
    
	
	public ItemCustomRecord(int id, String name) {
		super(id, name);
		setMaxStackSize(1);
		setUnlocalizedName(name); 
        setCreativeTab(CreativeTabs.tabMisc);

		this.setMaxDamage(0);
        this.setHasSubtypes(true);
	}



	@SideOnly(Side.CLIENT)
	@Override
	/**
     * When this method is called, your block should register all the icons it needs with the given IconRegister. This
     * is the only chance you get to register icons.
     */
    public void registerIcons(IconRegister iconRegister)
    {
		this.iconArray = new Icon[ModRedstoneJukebox.maxCustomRecordIcon];
		
        for (int i = 0; i < this.iconArray.length; ++i)
        {
        	this.iconArray[i] = iconRegister.registerIcon(ModRedstoneJukebox.customRecordIconArray + String.format("%03d", i));
        }
    }

    
	
	@SideOnly(Side.CLIENT)
	@Override
	public Icon getIconFromDamage(int damage)
	{
		// OBS: DamageValue is used to set the custom record icon.
		return this.iconArray[damage];
	}

	
	
    /**
     * Callback for item usage. If the item does something special on right clicking, he will have one of those. Return
     * True if something happen and false if it don't. This is for ITEMS, not BLOCKS
     */
	@Override
	public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int x, int y, int z, int par7, float par8, float par9, float par10)
    {
    	String songID = "";

    	
		ModRedstoneJukebox.logDebugInfo("itemCustomRecord.onItemUse");
		ModRedstoneJukebox.logDebugInfo("    Side:   " + FMLCommonHandler.instance().getEffectiveSide());
		ModRedstoneJukebox.logDebugInfo("    Remote: " + par3World.isRemote);
		

    	// Make this compatible with regular jukeboxes
    	if (par3World.getBlockId(x, y, z) == Block.jukebox.blockID && par3World.getBlockMetadata(x, y, z) == 0)
        {
            if (par3World.isRemote)
            {
                return true;
            }
            else
            {
        		songID = getSongID(par1ItemStack);
        		PacketHelper.sendPlayRecordPacket(songID, x, y, z, true, 0, par3World.provider.dimensionId);

            	((BlockJukeBox)Block.jukebox).insertRecord(par3World, x, y, z, par1ItemStack);
            	--par1ItemStack.stackSize;
                return true;
            }
        }
        else
        {
    		ModRedstoneJukebox.logDebugInfo("---------------------------------------------");
        	ModRedstoneJukebox.logDebugInfo("Loaded Tile Entities");
        	ModRedstoneJukebox.logDebugInfo("    Size: " + par3World.loadedTileEntityList.size());

        	@SuppressWarnings("rawtypes")
			Iterator iterator = par3World.loadedTileEntityList.iterator();
            while (iterator.hasNext())
            {
                TileEntity tileentity = (TileEntity)iterator.next();
                ModRedstoneJukebox.logDebugInfo("        Item: " + tileentity.toString());
            }
            
            ModRedstoneJukebox.logDebugInfo("---------------------------------------------");
        	
        	
            return false;
        }

    }
	
    
    
    // allows items to add custom lines of information to the mouseover description
    @SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean debugActive)
   {
	   String songTitle = "";
	   String songID = getSongID(par1ItemStack);
	   
	   
	   if (songID != "") songTitle = CustomRecordHelper.getSongTitle(songID); 
	   
	   if (songTitle != "")
	   {
		   par3List.add(songTitle);
	   }
	   else
	   {
		   par3List.add("No song");
	   }
	   
	   par3List.add("\u00a7oCustom Record\u00a7r");
	   
	   /* 
	    *  "DebugActive" is when the player press F3+H to see extra info, like durability of tools.
	    *  Here I show the song ID.
	    */
	   if (debugActive)
	   {
		   if (songID == "") songID = "null";
		   par3List.add("\u00a78Music ID: " + songID + "\u00a7r");		   
	   }
   }
   
   
   

   /**
    * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
    */
   @SuppressWarnings({ "unchecked", "rawtypes" })
   public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List)
   {
	   // returns only records being used, with the needed NBT data. This info is added on the Creative Menu.
		for (CustomRecordObject record: CustomRecordHelper.getRecordList())
		{
			par3List.add(CustomRecordHelper.getCustomRecord(record));
		}

   }

   
   
   
   public String getSongID(ItemStack par1ItemStack)
   {
	   String songID = "";
	   if (par1ItemStack.stackTagCompound != null && par1ItemStack.stackTagCompound.hasKey("Song")) { songID = par1ItemStack.stackTagCompound.getString("Song"); }
	   return songID;
   }
   
   
   
}

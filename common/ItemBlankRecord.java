package sidben.redstonejukebox.common;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.src.*;


public class ItemBlankRecord extends Item {

	public ItemBlankRecord(int id, CreativeTabs tab, String name) {
		super(id);
		setMaxStackSize(16);
		setCreativeTab(tab);
		//setIconIndex(48);
		setUnlocalizedName(name); 
	}

	
	/*
	@Override
	public String getTextureFile () {
		return CommonProxy.textureSheet;
	}
	*/


	@SideOnly(Side.CLIENT)
    /**
     * When this method is called, your block should register all the icons it needs with the given IconRegister. This
     * is the only chance you get to register icons.
     */
    public void registerIcons(IconRegister iconRegister)
    {
		this.itemIcon = iconRegister.registerIcon("redstonejukebox:blankRecord");
    }
	
	
	

	// allows items to add custom lines of information to the mouseover description
	public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean debugActive)
	{
		if (debugActive)
		{
			par3List.add("Trade with a villager!");
		}
	}
	
}
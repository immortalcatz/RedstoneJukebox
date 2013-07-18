package sidben.redstonejukebox.common;

import java.util.Random;
import sidben.redstonejukebox.ModRedstoneJukebox;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.block.*;
import net.minecraft.block.material.*;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.*;
import net.minecraft.util.Icon;
import net.minecraft.world.*;


public class BlockRedstoneJukebox extends BlockContainer {


	/*--------------------------------------------------------------------
		Constants and Variables
	--------------------------------------------------------------------*/

    // True if this is an active jukebox, false if idle 
    private final boolean isActive;
    
    /**
     * This flag is used to prevent the jukebox inventory to be dropped upon block removal, is used internally when the
     * jukebox block changes from idle to active and vice-versa.
     */
    private static boolean keepMyInventory = false;



    
    
	
    /*--------------------------------------------------------------------
		Constructors
	--------------------------------------------------------------------*/

    public BlockRedstoneJukebox(int blockID, boolean active) {
    	super(blockID, Material.wood);
        this.isActive = active;
	}

	@Override
	public TileEntity createNewTileEntity(World var1) {
		return new TileEntityRedstoneJukebox();
	}
	

    
    
    
	/*--------------------------------------------------------------------
		Parameters
	--------------------------------------------------------------------*/

    /**
     * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
     * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
     */
    public boolean isOpaqueCube()
    {
    	// FALSE turns the block light-transparent
        return false;
    }

    /**
     * Returns the ID of the items to drop on destruction.
     */
    public int idDropped(int par1, Random par2Random, int par3)
    {
        return ModRedstoneJukebox.redstoneJukeboxIdleID;
    }

    /**
     * only called by clickMiddleMouseButton , and passed to inventory.setCurrentItem (along with isCreative)
     */
    public int idPicked(World par1World, int par2, int par3, int par4)
    {
        return ModRedstoneJukebox.redstoneJukeboxIdleID;
    }


    
    
    
    
    
	/*--------------------------------------------------------------------
		Textures and Rendering
	--------------------------------------------------------------------*/
    @SideOnly(Side.CLIENT)
    private Icon discIcon;
    
    @SideOnly(Side.CLIENT)
    private Icon topIcon;

    @SideOnly(Side.CLIENT)
    private Icon bottomIcon;

    @SideOnly(Side.CLIENT)
    private Icon sideOnIcon;

    @SideOnly(Side.CLIENT)
    private Icon sideOffIcon;

    
    
    @SideOnly(Side.CLIENT)
    /**
     * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
     */
    public Icon getIcon(int side, int metadata)
    {
    	switch(side)
    	{
    	case 0:
			//--- bottom
            return this.bottomIcon;

    	case 1:
			//--- top
			return this.topIcon;

    	case 7:
			//--- Extra texture (disc)
			return this.discIcon;

    	default:
	        //--- sides
			if (this.isActive) { return this.sideOnIcon; }
			return this.sideOffIcon;
		}    	
    }

    @SideOnly(Side.CLIENT)
    /**
     * When this method is called, your block should register all the icons it needs with the given IconRegister. This
     * is the only chance you get to register icons.
     */
    public void registerIcons(IconRegister iconRegister)
    {
    	this.discIcon = iconRegister.registerIcon(ModRedstoneJukebox.jukeboxDiscIcon);
    	this.topIcon = iconRegister.registerIcon(ModRedstoneJukebox.jukeboxTopIcon);
    	this.bottomIcon = iconRegister.registerIcon(ModRedstoneJukebox.jukeboxBottomIcon);
    	this.sideOnIcon = iconRegister.registerIcon(ModRedstoneJukebox.jukeboxSideOnIcon);
    	this.sideOffIcon = iconRegister.registerIcon(ModRedstoneJukebox.jukeboxSideOffIcon);
    }

    
    @SideOnly(Side.CLIENT)
    /**
     * Returns true if the given side of this block type should be rendered, if the adjacent block is at the given
     * coordinates.  Args: blockAccess, x, y, z, side
     */
    public boolean shouldSideBeRendered(IBlockAccess par1IBlockAccess, int x, int y, int z, int side)
    {
    	return true;
    }

    
    
    /**
     * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
     */
    public boolean renderAsNormalBlock()
    {
        return false;
    }
    
    
    /**
     * The type of render function that is called for this block
     */
    public int getRenderType()
    {
        return ModRedstoneJukebox.redstoneJukeboxModelID;
    }

    
    
    /**
     * Returns which pass should this block be rendered on. 0 for solids and 1 for alpha
     */
    public int getRenderBlockPass()
    {
        return 0;
    }

	
	
	
	
    /*--------------------------------------------------------------------
		World Events
	--------------------------------------------------------------------*/

    /**
     * Called whenever the block is added into the world. Args: world, x, y, z
     */
    public void onBlockAdded(World par1World, int x, int y, int z)
    {
		super.onBlockAdded(par1World, x, y, z);
	}


    /**
     * Called upon block activation (right click on the block.)
     */
    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int i, float a, float b, float c)
    {
    	// Avoids opening the GUI if sneaking
    	TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
        if (tileEntity == null || player.isSneaking()) { return false; }

        
    	player.openGui(ModRedstoneJukebox.instance, ModRedstoneJukebox.redstoneJukeboxGuiID, world, x, y, z);
    	return true;
    }

    
    /**
     * ejects contained items into the world, and notifies neighbors of an update, as appropriate
     */
    public void breakBlock(World par1World, int x, int y, int z, int par5, int par6)
    {
    	ModRedstoneJukebox.logDebugInfo("BlockRedstoneJukebox.breakBlock()");
		ModRedstoneJukebox.logDebugInfo("    Side:          " + FMLCommonHandler.instance().getEffectiveSide());
		ModRedstoneJukebox.logDebugInfo("    keepInventory: " + keepMyInventory);
		ModRedstoneJukebox.logDebugInfo("    TE Size 1: " + par1World.loadedTileEntityList.size());

		
    	if (!keepMyInventory)
		{
            TileEntityRedstoneJukebox teJukebox = (TileEntityRedstoneJukebox)par1World.getBlockTileEntity(x, y, z);

            if (teJukebox != null)
            {
            	teJukebox.ejectAllAndStopPlaying(par1World, x, y, z);
            }
		}

        super.breakBlock(par1World, x, y, z, par5, par6);
        ModRedstoneJukebox.logDebugInfo("    TE Size 2: " + par1World.loadedTileEntityList.size());
    }    
    

    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor blockID
     */
    public void onNeighborBlockChange(World world, int x, int y, int z, int blockID)
    {
    	ModRedstoneJukebox.logDebugInfo("BlockRedstoneJukebox.onNeighborBlockChange()");
		ModRedstoneJukebox.logDebugInfo("    Side:         " + FMLCommonHandler.instance().getEffectiveSide());
		ModRedstoneJukebox.logDebugInfo("    TE Size 1: " + world.loadedTileEntityList.size());

		// Forces the Tile Entity to update it's state (inspired by BuildCraft)
		TileEntityRedstoneJukebox teJukebox = (TileEntityRedstoneJukebox)world.getBlockTileEntity(x, y, z);
		if (teJukebox != null) { teJukebox.checkRedstonePower(); }
    }


    
    
    
    
    
    /*--------------------------------------------------------------------
		Custom World Events
	--------------------------------------------------------------------*/
	
    /**
     * Update which block ID the jukebox is using depending on whether or not it is playing.
     * 
     * Triggered by the Tile Entity when it detects changes.
     */
    public static void updateJukeboxBlockState(boolean active, World world, int x, int y, int z)
    {
		ModRedstoneJukebox.logDebugInfo("BlockRedstoneJukebox.updateJukeboxBlockState");
		ModRedstoneJukebox.logDebugInfo("    Side:    " + FMLCommonHandler.instance().getEffectiveSide());
		ModRedstoneJukebox.logDebugInfo("    Active:  " + active);
		ModRedstoneJukebox.logDebugInfo("    TE Size 1: " + world.loadedTileEntityList.size());
		
		
		int targetBlockId = (active ? ModRedstoneJukebox.redstoneJukeboxActiveID : ModRedstoneJukebox.redstoneJukeboxIdleID);
		int currentBlockId = world.getBlockId(x, y, z);

	
		ModRedstoneJukebox.logDebugInfo("    World block ID = " + currentBlockId);
		ModRedstoneJukebox.logDebugInfo("    Target block ID = " + targetBlockId);
        ModRedstoneJukebox.logDebugInfo("    TE Size 2: " + world.loadedTileEntityList.size());


        
        if (currentBlockId != targetBlockId)
        {
        	// get the TileEntity so it won't be reset
    		TileEntity teJukebox = world.getBlockTileEntity(x, y, z);
            keepMyInventory = true;
    		ModRedstoneJukebox.logDebugInfo("    TE Size 3: " + world.loadedTileEntityList.size());
            ModRedstoneJukebox.logDebugInfo("    Setting block");
            
            // change de block id (at this point, Tile Entity was reset)
	        world.setBlock(x, y, z, targetBlockId);
        	
	        keepMyInventory = false;

	        ModRedstoneJukebox.logDebugInfo("    TE Size 4: " + world.loadedTileEntityList.size());

	        
	        ModRedstoneJukebox.logDebugInfo("    Block setted");
	        
	        // Don't know what this does for sure. I think the flag "2" sends update to client 
	        world.setBlockMetadataWithNotify(x, y, z, 0, 2);
	        
	        
	        ModRedstoneJukebox.logDebugInfo("    Meta data setted");
	        ModRedstoneJukebox.logDebugInfo("    TE Size 5: " + world.loadedTileEntityList.size());

	
			// Recover the Tile Entity
	        if (teJukebox != null)
	        {
	            teJukebox.validate();
	            world.setBlockTileEntity(x, y, z, teJukebox);
	            ModRedstoneJukebox.logDebugInfo("    TE Size 6: " + world.loadedTileEntityList.size());
	        }
			/*
	        */
			
			//ModRedstoneJukebox.logDebugInfo("    TE for coords= " + (world.getBlockTileEntity(x, y, z).toString()));
			
        }

    }
    
    /**
     * Return the amount of extra range the jukebox will receive from near noteblocks.
     * 
     * Each note block increases the range by 8. 
     */
    public static int getAmplifierPower(World world, int x, int y, int z)
    {
    	int amp = 0;
    	
    	
    	// check an area of 5x5x3 around the block looking for note blocks
        for (int i = x - 2; i <= x + 2; ++i)
        {
            for (int k = z - 2; k <= z + 2; ++k)
            {
            	for (int j = y - 1; j <= y + 1; ++j)
            	{
            		
            		if (i!=0 || k != 0 || j != 0)
            		{
                    	// look for noteblocks
	                    if (world.getBlockId(i, j, k) == Block.music.blockID)
	                    {
	                    	amp += 8;
	                    	if (amp >= ModRedstoneJukebox.maxExtraVolume) { return ModRedstoneJukebox.maxExtraVolume; }
	                    }
                    }

            	} // for j
            } // for k
        } // for i

        
        return amp;
    }
    

    

    
    

	/*--------------------------------------------------------------------
		Visual Effects
	--------------------------------------------------------------------*/

    /**
     * A randomly called display update to be able to add particles or other items for display
     */
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World par1World, int x, int y, int z, Random par5Random)
    {
        if (this.isActive)
        {
			// redstone ore sparkles
        	this.showSparkles(par1World, x, y, z);


        	
            // not always shows particles
            if (par5Random.nextInt(2) == 0)		
            {

				// notes on note blocks
	        	// check an area of 5x5 around the block looking for note block
	            for (int i = x - 2; i <= x + 2; ++i)
	            {
	                for (int k = z - 2; k <= z + 2; ++k)
	                {
	                	for (int j = y - 1; j <= y + 1; ++j)
	                	{
	                		
	                		// do not check the jukebox and below it
	                		if (i!=0 || k != 0 || j == 1)
	                		{
		                    	// look for note blocks with space on the top
			                    if (par1World.getBlockId(i, j, k) == Block.music.blockID && !par1World.isBlockOpaqueCube(i, j+1, k))
			                    {
			                    	this.showNote(par1World, i, j, k);
			                    }
		                    }
		
	                	} // for j
	                } // for k
	            } // for i
            
            }
        }
    }
    
    
    /**
     * Displays redstone sparkles on the block sides.
     */
    private void showSparkles(World world, int x, int y, int z)
    {
		Random var5 = world.rand;
		double var6 = 0.0625D;

		for (int var8 = 2; var8 < 6; ++var8)		// only shows particles on the sides
		{
			double var9 = (double)((float)x + var5.nextFloat());
			double var11 = (double)((float)y + var5.nextFloat());
			double var13 = (double)((float)z + var5.nextFloat());


			if (var8 == 2 && !world.isBlockOpaqueCube(x, y, z + 1))
			{
				var13 = (double)(z + 1) + var6;
			}

			if (var8 == 3 && !world.isBlockOpaqueCube(x, y, z - 1))
			{
				var13 = (double)(z + 0) - var6;
			}

			if (var8 == 4 && !world.isBlockOpaqueCube(x + 1, y, z))
			{
				var9 = (double)(x + 1) + var6;
			}

			if (var8 == 5 && !world.isBlockOpaqueCube(x - 1, y, z))
			{
				var9 = (double)(x + 0) - var6;
			}

			if (var9 < (double)x || var9 > (double)(x + 1) || var11 < 0.0D || var11 > (double)(y + 1) || var13 < (double)z || var13 > (double)(z + 1))
			{
				world.spawnParticle("reddust", var9, var11, var13, 0.0D, 0.0D, 0.0D);
			}
		}    	
    }

    

    /**
     * Displays a random music note on the block.
     */
    private void showNote(World world, int x, int y, int z)
    {
    	int color = world.rand.nextInt(16);
    	world.spawnParticle("note", (double)x + 0.5D, (double)y + 1.2D, (double)z + 0.5D, (double)color / 16.0D, 0.0D, 0.0D);
    }







	/*--------------------------------------------------------------------
		Redstone logic
	--------------------------------------------------------------------*/

    /**
     * Can this block provide power. Only wire currently seems to have this change based on its state.
     */
    public boolean canProvidePower()
    {
    	return false;
    }

    
    /**
     * If this returns true, then comparators facing away from this block will use the value from
     * getComparatorInputOverride instead of the actual redstone signal strength.
     */
    public boolean hasComparatorInputOverride()
    {
        return true;
    }

    /**
     * If hasComparatorInputOverride returns true, the return value from this is used instead of the redstone signal
     * strength when this block inputs to a comparator.
     */
    public int getComparatorInputOverride(World par1World, int par2, int par3, int par4, int par5)
    {
    	TileEntityRedstoneJukebox teJukebox = (TileEntityRedstoneJukebox)par1World.getBlockTileEntity(par2, par3, par4);
    	return teJukebox == null ? 0 : teJukebox.isActive() ? teJukebox.getCurrentJukeboxPlaySlot() + 1 : 0;
    }







    
}

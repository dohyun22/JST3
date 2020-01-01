package dohyun22.jst3.blocks;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class JSTBlocks {
	public static BlockBase block1;
	public static BlockBase block2;
	public static BlockBase blockOre;
	public static BlockBase blockNO;
	public static BlockBase blockEO;
	public static BlockTileEntity blockTile;
	public static Block blockOHW;
	public static Item tileItem;

	public static void init() {
		GameRegistry.registerTileEntity(TileEntityMeta.class, new ResourceLocation(JustServerTweak.MODID, "mte"));
		
		block1 = (BlockBase) registerBlock(new BlockJST1(), ItemBlockBase.class, false, "metablocks/");
		block2 = (BlockBase) registerBlock(new BlockJST2(), ItemBlockBase.class, false, "metablocks/");
		
		blockOre = (BlockBase) registerBlock(new BlockOre(), ItemBlockBase.class, false, "metablocks/");
		blockNO = (BlockBase) registerBlock(new BlockNetherOre(), ItemBlockBase.class, false, "metablocks/");
		blockEO = (BlockBase) registerBlock(new BlockEndOre(), ItemBlockBase.class, false, "metablocks/");
		
		blockTile = new BlockTileEntity();
		ForgeRegistries.BLOCKS.register(blockTile);
		tileItem = new ItemBlockTE(blockTile).setRegistryName(blockTile.getRegistryName());
		ForgeRegistries.ITEMS.register(tileItem);

		blockOHW = registerSimpleBlock(new BlockOHW());

		if (JSTUtils.isClient()) loadMTEModel();
	}

	@SideOnly(Side.CLIENT)
	private static void loadMTEModel() {
		ModelResourceLocation loc = new ModelResourceLocation(JustServerTweak.MODID + ":blockte#inventory");
		ModelLoader.setCustomMeshDefinition(tileItem, stack -> loc);
	}
	
    public static Block registerSimpleBlock(Block bl) {
        return registerBlock(bl, ItemBlock.class, true, null);
    }
    
    public static Block registerBlock( Block bl, Class<? extends ItemBlock> cl, boolean b) {
        return registerBlock(bl, cl, true, "");
    }
    
    public static Block registerBlock(Block bl, Class<? extends ItemBlock> cl, boolean ris, String loc) {
    	if (bl == null || bl == Blocks.AIR) return null;
        ForgeRegistries.BLOCKS.register(bl);
        if (cl != null) {
            try {
                ItemBlock ib = cl.getDeclaredConstructor(Block.class).newInstance(bl);
                ForgeRegistries.ITEMS.register(ib.setRegistryName(bl.getRegistryName()));
                if (FMLCommonHandler.instance().getSide().isClient()) {
                	if (ris)
                		ModelLoader.setCustomModelResourceLocation(ib, 0, new ModelResourceLocation(bl.getRegistryName(), "inventory"));
	                if (bl instanceof BlockBase && loc != null) {
	                    BlockBase bb = (BlockBase)bl;
	                    ResourceLocation l = bl.getRegistryName();
	                    for (Integer m : bb.allowedMetas)
	                        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(bb), m, new ModelResourceLocation(l.getResourceDomain() + ":" + loc + l.getResourcePath() + "_" + m, "inventory"));
	                }
                }
            } catch (Throwable t) {
                System.err.println("Error occurred while registering block");
                t.printStackTrace();
            }
        }
        return bl;
    }
}

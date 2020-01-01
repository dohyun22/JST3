package dohyun22.jst3.items.behaviours.tool;

import dohyun22.jst3.items.behaviours.ItemBehaviour;
import dohyun22.jst3.loader.JSTCfg;
import dohyun22.jst3.utils.JSTUtils;
import ic2.api.crops.CropCard;
import ic2.api.crops.Crops;
import ic2.api.crops.ICropTile;
import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

public class IB_CropTool extends ItemBehaviour {

	@Override
	public EnumActionResult onUseFirst(ItemStack st, EntityPlayer ep, World w, BlockPos p, EnumFacing s, float hx, float hy, float hz, EnumHand h) {
		if (w.isRemote) return EnumActionResult.PASS;
		if (JSTCfg.ic2Loaded) {
			try {
				TileEntity te = w.getTileEntity(p);
				if (te instanceof ICropTile) {
					ICropTile cr = ((ICropTile)te);
					CropCard cc = cr.getCrop();
					if (cc != null) {
						ep.sendMessage(new TextComponentTranslation("jst.msg.scan.ic2.crop.desc"));
						ep.sendMessage(new TextComponentTranslation("jst.msg.scan.ic2.crop.info", new TextComponentTranslation(cc.getUnlocalizedName()), cc.getDiscoveredBy()));
						ep.sendMessage(new TextComponentTranslation("jst.msg.scan.ic2.crop.info2", cc.getProperties().getTier(), cr.getCurrentSize(), cc.getMaxSize(), cr.getGrowthPoints(), cc.getGrowthDuration(cr)));
						if (cc == Crops.weed)
							ep.sendMessage(new TextComponentTranslation("jst.msg.scan.ic2.crop.weed"));
						return EnumActionResult.SUCCESS;
					}
				}
			} catch (Throwable t) {
				t.printStackTrace();
				return EnumActionResult.SUCCESS;
			}
		}
		
		IBlockState bs = w.getBlockState(p);
		Block b = bs.getBlock();
		if (b instanceof IGrowable) {
			for (IProperty prop : bs.getPropertyKeys()) {
				if (prop.getName().equals("age") && prop instanceof PropertyInteger) {
					try {
						ep.sendMessage(new TextComponentString(bs.getValue((PropertyInteger)prop) + " / " + (((PropertyInteger)prop).getAllowedValues().size() - 1)));
					} catch (Exception e) {}
					return EnumActionResult.SUCCESS;
				}
			}
		}
		
		return EnumActionResult.PASS;
	}
	
	@Override
	public int getItemStackLimit(ItemStack st) {
		return 1;
	}
}

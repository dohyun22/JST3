package dohyun22.jst3.items.behaviours;

import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import dohyun22.jst3.loader.JSTCfg;
import dohyun22.jst3.utils.JSTUtils;
import ic2.api.crops.Crops;
import ic2.core.crop.TileEntityCrop;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class IB_Trowel extends IB_Damageable {
	
	public IB_Trowel() {
		super(250);
	}

	@Override
	public EnumActionResult onUseFirst(ItemStack st, EntityPlayer ep, World w, BlockPos p, EnumFacing s, float hx, float hy, float hz, EnumHand h) {
		if (w.isRemote) return EnumActionResult.PASS;
		if (JSTCfg.ic2Loaded) {
			try {
				TileEntity te = w.getTileEntity(p);
				if (te instanceof TileEntityCrop) {
					TileEntityCrop cr = ((TileEntityCrop)te);
					if (cr.getCrop() != null) {
						if (cr.getCrop() == Crops.weed) {
							w.playEvent(2001, p, Block.getStateId(Blocks.TALLGRASS.getDefaultState().withProperty(BlockTallGrass.TYPE, BlockTallGrass.EnumType.GRASS)));
						} else {
							w.playSound(null, p, SoundEvents.BLOCK_GRASS_BREAK, SoundCategory.PLAYERS, 1.0F, 0.9F + w.rand.nextFloat() * 0.2F);
							JSTUtils.dropEntityItemInPos(w, p, cr.getCrop().getSeeds(cr));
						}
						cr.reset();
						this.doDamage(st, 1, ep);
						return EnumActionResult.SUCCESS;
					}
				}
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
		return EnumActionResult.PASS;
	}
	
	@Override
    public EnumActionResult onRightClickBlock(ItemStack st, EntityPlayer pl, World w, BlockPos p, EnumHand h, EnumFacing f, float hx, float hy, float hz) {
    	IBlockState bs = null;
		if (!pl.canPlayerEdit(p.offset(f), f, st)) {
        	return EnumActionResult.PASS;
        } else {
            int hook = net.minecraftforge.event.ForgeEventFactory.onHoeUse(st, pl, w, p);
            if (hook != 0) return hook > 0 ? EnumActionResult.SUCCESS : EnumActionResult.FAIL;
            IBlockState iblockstate = w.getBlockState(p);
            Block block = iblockstate.getBlock();
            if (f != EnumFacing.DOWN && w.isAirBlock(p.up())) {
                if (block == Blocks.GRASS || block == Blocks.GRASS_PATH)
                	bs = Blocks.FARMLAND.getDefaultState();

                if (bs == null && block == Blocks.DIRT) {
                    switch ((BlockDirt.DirtType)iblockstate.getValue(BlockDirt.VARIANT)) {
                        case DIRT:
                        	bs = Blocks.FARMLAND.getDefaultState();
                        	break;
                        case COARSE_DIRT:
                            bs = Blocks.DIRT.getDefaultState().withProperty(BlockDirt.VARIANT, BlockDirt.DirtType.DIRT);
                        	break;
                        default:
                    }
                }
            }
        }
    	
		if (bs != null) {
	        w.playSound(pl, p, SoundEvents.ITEM_HOE_TILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
	        if (!w.isRemote) {
	            w.setBlockState(p, bs, 11);
	            this.doDamage(st, pl);
	            return EnumActionResult.SUCCESS;
	        }
    	}
		return EnumActionResult.PASS;
	}
	
	@Override
	public boolean onBlockDestroyed(ItemStack st, World w, IBlockState bs, BlockPos p, EntityLivingBase el) {
		if (bs == null) return false;
		if (bs.getBlockHardness(w, p) != 0.0F) this.doDamage(st, 1, el);
		return true;
	}
	
	@Override
	public float getDigSpeed(ItemStack st, IBlockState bs) {
		if (bs == null || st == null || st.isEmpty()) return 1.0F;
		return canHarvestBlock(bs, st) ? 5.0F : 1.0F;
	}
	
	@Override
	public boolean canHarvestBlock(IBlockState bs, ItemStack st) {
		return bs == null ? true : bs.getMaterial() == Material.SNOW || "shovel".equals(bs.getBlock().getHarvestTool(bs));
	}
	
	@Override
	public void addToolClasses(ItemStack st, Set<String> list) {
		list.add("shovel");
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	@Nullable
	public List<String> getInformation(ItemStack st, World w, ITooltipFlag adv) {
		List<String> ret = super.getInformation(st, w, adv);
		if (JSTCfg.ic2Loaded) ret.addAll(JSTUtils.getListFromTranslation("jst.tooltip.trowel.ic2"));
		return ret;
	}
	
	@Override
	public int getItemStackLimit(ItemStack st) {
		return 1;
	}
}

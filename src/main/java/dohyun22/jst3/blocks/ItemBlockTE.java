package dohyun22.jst3.blocks;

import java.util.List;

import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.TileEntityMeta;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemBlockTE extends ItemBlock {

	public ItemBlockTE(Block b) {
		super(b);
		setHasSubtypes(true);
	}

	@Override
	public boolean placeBlockAt(ItemStack st, EntityPlayer pl, World w, BlockPos p, EnumFacing sd, float hx, float hy, float hz, IBlockState bs) {
		if (!w.setBlockState(p, bs, 3)) return false;
		if (w.isRemote) return true;
		TileEntityMeta tem = (TileEntityMeta) w.getTileEntity(p);
		if (tem != null) {
			tem.createNewMetatileEntity(st.getItemDamage());
			if (tem.hasValidMTE()) {
				tem.mte.onPlaced(p, bs, pl, st);
				if (!tem.mte.canUpdate()) {
					try {
						w.tickableTileEntities.remove(tem);
					} catch (Exception e) {}
				}
				if (!tem.mte.isOpaque())
					BlockTileEntity.setState(w, p, false, 2);
			}
		}
		return true;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack st, World w, List ls, ITooltipFlag adv) {
		MetaTileBase mte = MetaTileBase.getTE(st.getItemDamage());
		if (mte == null)
			ls.add(I18n.format("jst.tooltip.tile.com.error"));
		else
			mte.getInformation(st, w, ls, adv);
	}
	
	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return super.getUnlocalizedName(stack) + "." + stack.getItemDamage();
	}
	
    @Override
    public boolean showDurabilityBar(ItemStack st) {
    	MetaTileBase mte = MetaTileBase.getTE(st.getItemDamage());
    	if (mte != null) return mte.showDurability(st);
        return false;
    }
    
    @Override
	public double getDurabilityForDisplay(ItemStack st) {
    	MetaTileBase mte = MetaTileBase.getTE(st.getItemDamage());
    	if (mte != null) return mte.getDurability(st);
        return 1.0D;
	}
    
	@Override
	public int getRGBDurabilityForDisplay(ItemStack st) {
    	MetaTileBase mte = MetaTileBase.getTE(st.getItemDamage());
    	if (mte != null) return mte.getRGBDurability(st);
        return super.getRGBDurabilityForDisplay(st);
	}
}

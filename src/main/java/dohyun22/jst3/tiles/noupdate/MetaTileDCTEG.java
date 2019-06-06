package dohyun22.jst3.tiles.noupdate;

import java.util.List;

import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.tiles.interfaces.IDCGenerator;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MetaTileDCTEG extends MetaTileBase implements IDCGenerator {

	@Override
	public double getPower(World w, BlockPos p) {
		int hot = -1;
		int cold = -1;
		for (EnumFacing f : EnumFacing.VALUES) {
			IBlockState bs = getWorld().getBlockState(getPos().offset(f));
			int temp = getBlockTemp(bs);
			if (temp == Integer.MIN_VALUE) continue;
			if (temp < 300) {
				hot += 300 - temp;
			} else if (temp > 300) {
				cold += temp - 300;
			} else if (temp == 300) {
				hot = hot < 0 ? 0 : hot;
				cold = cold < 0 ? 0 : cold;
			}
		}
		if (hot < 0 || cold < 0) return 0.0D;
        return Math.max(0, hot + cold) * 0.000125D;
	}

	@Override
	public MetaTileBase newMetaEntity(TileEntityMeta tem) {
		return new MetaTileDCTEG();
	}

	@Override
	public boolean canUpdate() {
		return false;
	}

	private int getBlockTemp(IBlockState bs) {
		int ret = Integer.MIN_VALUE;
		Block b = bs.getBlock();
		if (b == Blocks.SNOW || b == Blocks.SNOW_LAYER)
			ret = 260;
		else if (b == Blocks.ICE || b == Blocks.PACKED_ICE)
			ret = 250;
		else if (b == Blocks.MAGMA)
			ret = 1200;
		if (ret != Integer.MIN_VALUE || b.getMetaFromState(bs) != 0) return ret;
		Fluid f = FluidRegistry.lookupFluidForBlock(b);
		if (f != null) {
			ret = Integer.valueOf(f.getTemperature());
		}
		return ret;
	}
	

	@Override
	@SideOnly(value = Side.CLIENT)
	public String getModelKey() {
		return "jst_dcteg";
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getDefaultTexture() {
		TextureAtlasSprite s = getTETex("basic_side");
		TextureAtlasSprite t = getTETex("basic_teg");
		return new TextureAtlasSprite[] {s, s, t, t, t, t};
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getInformation(ItemStack st, World w, List<String> ls, ITooltipFlag adv) {
		ls.addAll(JSTUtils.getListFromTranslation("jst.tooltip.tile.dcgen.desc"));
		ls.add(I18n.format("jst.tooltip.tile.dcgen.teg"));
	}
}

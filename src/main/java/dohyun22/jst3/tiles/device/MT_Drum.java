package dohyun22.jst3.tiles.device;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MT_Drum extends MetaTileBase {
	private static final AxisAlignedBB DRUM_AABB = new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 1.0D, 0.875D);
	private final byte tier;
	private FluidTank tank;

	public MT_Drum(int t) {
		tier = (byte) t;
	}

	@Override
	public MetaTileBase newMetaEntity(TileEntityMeta tem) {
		MT_Drum r = new MT_Drum(tier);
		r.tank = new FluidTank(r.capacity());
		return r;
	}

	@Override
	public <T> T getCapability(Capability<T> c, @Nullable EnumFacing f) {
		super.getCapability(c, f);
		if (c == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
			return (T) tank;
		return null;
	}

	@Override
	public void onPostTick() {
		if (!isClient() && baseTile.isActive() && tank.getFluidAmount() > 0) {
			int amt = JSTUtils.fillTank(getWorld(), getPos(), EnumFacing.DOWN, tank.getFluid().copy());
			if (amt > 0) tank.drainInternal(amt, true);
		}
	}

	@Override
	public boolean canRightclickIfSneaking(EntityPlayer pl, ItemStack st, EnumFacing s, float hX, float hY, float hZ) {
		return true;
	}

	@Override
	public boolean onRightclick(EntityPlayer pl, ItemStack st, EnumFacing s, float hX, float hY, float hZ) {
		if (!isClient() && !FluidUtil.interactWithFluidHandler(pl, EnumHand.MAIN_HAND, tank)) {
			if (pl.isSneaking()) {
				baseTile.setActive(!baseTile.isActive());
				getWorld().playSound(null, getPos(), SoundEvents.UI_BUTTON_CLICK, SoundCategory.PLAYERS, 1.0F, 1.25F);
			} else if (FluidUtil.getFluidHandler(st) == null) {
				FluidStack fl = tank.getFluid();
				JSTUtils.sendChatTrsl(pl, fl == null ? "jst.msg.com.nofluid" : fl.getUnlocalizedName());
				JSTUtils.sendChatTrsl(pl, "jst.msg.com.tanksimple", tank.getFluidAmount(), tank.getCapacity());
			}
		}
		return true;
	}

	@Override
	public void onPlaced(BlockPos p, IBlockState bs, EntityLivingBase elb, ItemStack st) {
		if (baseTile == null) return;
		if (st.hasTagCompound())
			tank.readFromNBT(st.getTagCompound());
		super.onPlaced(p, bs, elb, st);
	}

	@Override
	public void getDrops(ArrayList<ItemStack> ls) {
		if (baseTile == null) return;
		super.getDrops(ls);
	    if (!isClient() && tank.getFluid() != null) {
	    	NBTTagCompound t = new NBTTagCompound();
	    	tank.writeToNBT(t);
	    	ls.get(0).setTagCompound(t);
	    }
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		tank.readFromNBT(tag);
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		tank.writeToNBT(tag);
	}

	@Override
	public boolean isSideSolid(EnumFacing s) {
		return false;
	}
	
	@Override
	public boolean isSideOpaque(EnumFacing s) {
		return false;
	}
	
	@Override
	public boolean isOpaque() {
		return false;
	}

	@Override
	public List<AxisAlignedBB> getBox() {
		return Arrays.asList(new AxisAlignedBB[] { DRUM_AABB });
	}

	@Override
	public AxisAlignedBB getBoundingBox() {
		return DRUM_AABB;
	}

	@Override
	public boolean showDurability(ItemStack st) {
		return st.getCount() == 1 && st.hasTagCompound() && st.getTagCompound().hasKey("FluidName");
	}

	@Override
	public double getDurability(ItemStack st) {
		if (!st.hasTagCompound()) return 1.0D;
		return 1.0D - (double)st.getTagCompound().getLong("Amount") / capacity();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getInformation(ItemStack st, World w, List<String> ls, boolean adv) {
		ls.addAll(JSTUtils.getListFromTranslation("jst.tooltip.tile.drum"));
		FluidStack fs = FluidStack.loadFluidStackFromNBT(st.getTagCompound());
		ls.add(fs == null ? I18n.format("jst.msg.com.nofluid") : fs.getLocalizedName());
		ls.add(I18n.format("jst.msg.com.tanksimple", fs == null ? 0 : fs.amount, capacity()));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getModelKey() {
		return "jst_drum" + tier + baseTile.isActive();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public List<BakedQuad> getModel(boolean isItem) {
		return JSTUtils.makeCubeAABB(isItem ? getDefaultTexture() : getTexture(), DRUM_AABB);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getTexture() {
		TextureAtlasSprite[] r = getDefaultTexture();
		if (baseTile.isActive()) r[0] = getTETex("fl_out");
		return r;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getDefaultTexture() {
		TextureAtlasSprite s = getTieredTex(tier);
		return new TextureAtlasSprite[] {s, getTETex("drum"), s, s, s, s};
	}

	private int capacity() {
		return (int) Math.min(JSTUtils.getVoltFromTier(tier) * 1000L, Integer.MAX_VALUE);
	}
}

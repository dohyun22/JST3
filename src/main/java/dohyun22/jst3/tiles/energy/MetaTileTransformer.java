package dohyun22.jst3.tiles.energy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.MetaTileEnergyInput;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.tiles.interfaces.IScrewDriver;
import dohyun22.jst3.utils.JSTSounds;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MetaTileTransformer extends MetaTileEnergyInput implements IScrewDriver {
	private final byte tier;
	/** 0: RS=Step-up, 1: Step-up, 2: Step-down */
	protected byte mode;

	public MetaTileTransformer(int tier) {
		this.tier = (byte) tier;
	}

	@Override
	public MetaTileBase newMetaEntity(TileEntityMeta tem) {
		return new MetaTileTransformer(this.tier);
	}

	@Override
	public boolean canProvideEnergy() {
		return true;
	}
	
	@Override
	public long getMaxEnergy() {
		return JSTUtils.getVoltFromTier(tier + 1) * 2;
	}
	
	@Override
	public int maxEUTransfer() {
		return JSTUtils.getVoltFromTier(tier + 1);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		mode = tag.getByte("mode");
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		tag.setByte("mode", mode);
	}
	
	@Override
	public void onPostTick() {
		super.onPostTick();
		if (getWorld().isRemote)
			return;
		
		if (baseTile.isActive()) {
			if (baseTile.energy >= JSTUtils.getVoltFromTier(tier + 1))
				injectEnergyToSide(baseTile.facing, JSTUtils.getVoltFromTier(tier + 1));
		} else if (baseTile.energy >= JSTUtils.getVoltFromTier(tier) * 5) {
			for (EnumFacing f : EnumFacing.VALUES) {
				if (baseTile.facing == f) continue;
				injectEnergyToSide(f, JSTUtils.getVoltFromTier(tier));
			}
		}
	}
	
	@Override
	public boolean isEnergyInput(EnumFacing side) {
		if (side == null) return true;
		if (this.baseTile != null) {
			boolean ret = side == this.baseTile.facing;
			return this.baseTile.isActive() ? !ret : ret;
		}
		return false;
	}

	@Override
	public boolean isEnergyOutput(EnumFacing side) {
		if (side == null) return true;
		return !isEnergyInput(side);
	}
	
	@Override
	public void onPlaced(BlockPos p, IBlockState bs, EntityLivingBase elb, ItemStack st) {
		super.onPlaced(p, bs, elb, st);
		if (this.baseTile == null) return;
		baseTile.facing = JSTUtils.getClosestSide(p, elb, false);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getDefaultTexture() {
		TextureAtlasSprite t = this.getTieredTex(tier);
		return new TextureAtlasSprite[] {t, t, t, t, t, getTETex("e3din")};
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getTexture() {
		TextureAtlasSprite[] ret = new TextureAtlasSprite[6];
		for (byte n = 0; n < ret.length; n++) {
			if (this.baseTile.facing == JSTUtils.getFacingFromNum(n))
				ret[n] = getTETex("e3d" + (this.baseTile.isActive() ? "out" : "in"));
			else
				ret[n] = this.getTieredTex(tier);
		}
		return ret;
	}
	
	@Override
	public void onBlockUpdate() {
		super.onBlockUpdate();
		if (!isClient() && (mode == 0 || mode == 1) && baseTile.setActive(mode == 0 ? isRSPowered() : !isRSPowered()))
			getWorld().playSound(null, getPos(), JSTSounds.SWITCH, SoundCategory.BLOCKS, 0.75F, getWorld().rand.nextFloat() * 0.2F + 0.9F);
	}
	
	@Override
	public void getInformation(ItemStack st, World w, List<String> ls, ITooltipFlag adv) {
		ls.addAll(JSTUtils.getListFromTranslation("jst.tooltip.tile.transformer.desc"));
		ls.add(I18n.format("jst.tooltip.tile.com.sd.rs"));
	}
	
	@Override
	public boolean setFacing(EnumFacing f, EntityPlayer pl) {
		return doSetFacing(f, false);
	}
	
	@Override
	public boolean canConnectRedstone(EnumFacing f) {
		return true;
	}
	
	@Override
	public boolean onScrewDriver(EntityPlayer pl, EnumFacing f, double px, double py, double pz) {
		mode++;
		if (mode < 0 || mode > 3)
			mode = 0;
		JSTUtils.sendMessage(pl, "jst.msg.trans." + mode);
		baseTile.setActive(mode == 0 ? isRSPowered() : mode == 1 ? !isRSPowered() : mode == 2 ? true : false);
		return true;
	}
}

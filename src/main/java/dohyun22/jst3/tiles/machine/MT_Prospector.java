package dohyun22.jst3.tiles.machine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.utils.JSTChunkData;
import dohyun22.jst3.utils.JSTSounds;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MT_Prospector extends MT_Machine {
	private boolean activated;
	private boolean worked;

	public MT_Prospector() {
		super(1);
	}

	@Override
	public MetaTileBase newMetaEntity(TileEntityMeta tem) {
		return new MT_Prospector();
	}

	@Override
	protected boolean checkCanWork() {
		if (activated && !worked) {
			energyUse = 32;
			mxprogress = 200;
			return true;
		}
		return false;
	}

	@Override
	protected void finishWork() {
		JSTChunkData.getOrCreateFluid(getWorld(), new ChunkPos(getPos()), true);
		worked = true;
	}
	
	@Override
	public void onPostTick() {
		super.onPostTick();
		if (isClient() && baseTile.isActive()) {
			World w = getWorld();
			if (w.rand.nextInt(8) == 0) {
				BlockPos p = getPos();
				for (int i = 0; i < 5; i++) {
					double x = p.getX() + 0.5D + w.rand.nextFloat() * 0.6D - 0.3D;
					double y = p.getY() + 1 + w.rand.nextFloat() * 0.2D;
					double z = p.getZ() + 0.5D + w.rand.nextFloat() * 0.6D - 0.3D;
					w.spawnParticle(w.rand.nextBoolean() ? EnumParticleTypes.SMOKE_LARGE : EnumParticleTypes.SMOKE_NORMAL, x, y, z, 0.0D, 0.0D, 0.0D);
				}
			}
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getDefaultTexture() {
		TextureAtlasSprite t = getTieredTex(1);
		return new TextureAtlasSprite[] {t, getTETex("vent"), t, t, t, t};
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		activated = tag.getBoolean("clicked");
		worked = tag.getBoolean("worked");
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		tag.setBoolean("clicked", activated);
		tag.setBoolean("worked", worked);
	}
	
	@Override
	public long getMaxEnergy() {
		return 7000;
	}
	
	@Override
	public boolean onRightclick(EntityPlayer pl, ItemStack st, EnumFacing f, float hitX, float hitY, float hitZ) {
		if (isClient())
			return true;
		
		long eu = JSTUtils.dischargeItem(st, getMaxEnergy() - baseTile.energy, tier, true, false);
		if (eu > 0) {
			getWorld().playSound(null, getPos(), JSTSounds.SHOCK, SoundCategory.BLOCKS, 1.0F, 3.0F);
			baseTile.energy += eu;
			return true;
		}
		
		if (worked) {
			getWorld().playSound(null, getPos(), JSTSounds.SCAN, SoundCategory.BLOCKS, 1.0F, 1.0F);
			BlockPos p = getPos();
			FluidStack[] fs = JSTChunkData.getOrCreateFluid(getWorld(), new ChunkPos(p), false);
			boolean flag = fs == null || fs[0] == null;
			if (!flag) JSTUtils.clearAdvancement(pl, JustServerTweak.MODID, "main/prospect");
			if (st.getItem() == Items.WRITABLE_BOOK) {
				String str = "\u00A79[X:" + p.getX() + " Z:"+ p.getZ() + "]\u00A70\n";
				str += flag ? "None, 0mB" : fs[0].getFluid().getLocalizedName(fs[0]) + " " + fs[0].amount + " mB";
				JSTUtils.printToBook(st, str);
			} else {
				if (flag) {
					pl.sendMessage(new TextComponentTranslation("jst.msg.com.nofluid"));
				} else {
					Fluid fl = fs[0].getFluid();
					pl.sendMessage(new TextComponentTranslation(JSTUtils.getUnlocalizedFluidName(fl)));
					pl.sendMessage(new TextComponentString(fs[0].amount + " mB"));
				}
			}
			return true;
		}
		if (baseTile.energy > 0)
			activated = true;
		if (baseTile.isActive()) {
			getWorld().playSound(null, getPos(), JSTSounds.AIE, SoundCategory.BLOCKS, 1.0F, 1.0F);
			pl.sendMessage(new TextComponentTranslation("jst.msg.com.wait"));
		}
		return true;
	}
	
	@Override
	protected void onStartWork() {
		getWorld().playSound(null, getPos(), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 1.0F, 2.0F);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getInformation(ItemStack st, World w, List<String> ls, ITooltipFlag adv) {
		ls.addAll(JSTUtils.getListFromTranslation("jst.tooltip.tile.prospect"));
	}
}

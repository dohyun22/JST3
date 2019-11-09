package dohyun22.jst3.tiles.device;

import java.util.ArrayList;

import javax.annotation.Nullable;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.blocks.JSTBlocks;
import dohyun22.jst3.client.gui.GUISuperCompressor;
import dohyun22.jst3.container.ContainerSuperCompressor;
import dohyun22.jst3.items.JSTItems;
import dohyun22.jst3.recipes.MRecipes;
import dohyun22.jst3.tiles.MTETank;
import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.MetaTileEnergyInput;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.utils.JSTSounds;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MT_SuperCompressor extends MetaTileEnergyInput {
	public int comp;
	private int uuBoost;
	private byte counter;
	private MTETank tank = new MTETank(64000, false, true, null, -1, false, "ic2uu_matter", "uu_matter");
	private static final int energyUse = 4;
	public static final int itemNeededPerNeutronium = 25000000;

	@Override
	public MetaTileBase newMetaEntity(TileEntityMeta tem) {
		return new MT_SuperCompressor();
	}

	@Override
	public int getInvSize() {
		return 11;
	}

	@Override
	public boolean isItemValidForSlot(int sl, ItemStack st) {
		return sl >= 0 && sl < 10;
	}

	@Override
	public boolean canExtractItem(int sl, ItemStack st, EnumFacing f) {
		return sl == 10;
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
		super.onPostTick();
		if (isClient()) return;

		if (counter > 0) counter--;

		boolean worked = false;
		if (tank.getFluid() != null && uuBoost < 64000) {
			FluidStack uu = tank.drainInternal(Integer.MAX_VALUE, true);
			if (uu != null) uuBoost += uu.amount * 64;
		}
		for (int n = 0; n < 10; n++) {
			ItemStack st = inv.get(n);
			if (st.isEmpty()) continue;
			int v = MRecipes.getValueInMap(st, MRecipes.CompressorValue);
			if (v == 0) v = 1;
			int e = st.getCount() * energyUse;
			if (v < 0 || baseTile.energy < e) continue;
			baseTile.energy -= e;
			comp += st.getCount() * v;
			if (uuBoost > 0) {
				int min = Math.min(uuBoost, st.getCount());
				uuBoost -= min;
				comp += min * v;
			}
			inv.set(n, ItemStack.EMPTY);
			worked = true;
		}

		if (worked && counter <= 0) {
			getWorld().playSound(null, getPos(), SoundEvents.BLOCK_PISTON_EXTEND, SoundCategory.BLOCKS, 0.5F, 2.5F);
			counter = 20;
		}

		if (comp >= itemNeededPerNeutronium) {
			if (inv.get(10).isEmpty())
				inv.set(10, new ItemStack(JSTItems.item1, 1, 24));
			else
				inv.get(10).grow(1);
			comp -= itemNeededPerNeutronium;
			getWorld().playSound(null, getPos(), JSTSounds.SWITCH, SoundCategory.BLOCKS, 1.0F, 1.0F);
		}
	}

	@Override
	public long getMaxEnergy() {
		return 81920;
	}

	@Override
	public int maxEUTransfer() {
		return 8192;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getDefaultTexture() {
		TextureAtlasSprite t = getTETex("comp");
		return new TextureAtlasSprite[] { getTieredTex(5), getTieredTex(5), t, t, t, t };
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		comp = tag.getInteger("comp");
		uuBoost = tag.getInteger("bst");
		tank.readFromNBT(tag);
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setInteger("comp", comp);
		tag.setInteger("bst", uuBoost);
		tank.writeToNBT(tag);
	}

	@Override
	public boolean onRightclick(EntityPlayer pl, ItemStack st, EnumFacing f, float hX, float hY, float hZ) {
		if (!isClient()) pl.openGui(JustServerTweak.INSTANCE, 1, getWorld(), getPos().getX(), getPos().getY(), getPos().getZ());
		return true;
	}

	@Override
	public void onPlaced(BlockPos p, IBlockState bs, EntityLivingBase elb, ItemStack st) {
		if (baseTile == null) return;
		super.onPlaced(p, bs, elb, st);
		comp = st.hasTagCompound() ? st.getTagCompound().getInteger("comp") : 0;
	}

	@Override
	public void getDrops(ArrayList<ItemStack> ls) {
		if (baseTile == null) return;
		ItemStack st = new ItemStack(JSTBlocks.blockTile, 1, baseTile.getID());
		if (!isClient() && comp > 0) {
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setInteger("comp", comp);
			st.setTagCompound(nbt);
		}
		ls.add(st);
	}

	@Override
	public Object getServerGUI(int id, InventoryPlayer inv, TileEntityMeta te) {
		return new ContainerSuperCompressor(inv, te);
	}

	@Override
	public Object getClientGUI(int id, InventoryPlayer inv, TileEntityMeta te) {
		return new GUISuperCompressor(new ContainerSuperCompressor(inv, te));
	}
}

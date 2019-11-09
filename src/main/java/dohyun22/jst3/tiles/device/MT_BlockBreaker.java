package dohyun22.jst3.tiles.device;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import dohyun22.jst3.blocks.JSTBlocks;
import dohyun22.jst3.compat.CompatBWM;
import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.utils.JSTSounds;
import dohyun22.jst3.utils.JSTUtils;
import dohyun22.jst3.utils.ReflectionUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityShulkerBox;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MT_BlockBreaker extends MetaTileBase {
	private boolean silkTouch;

	@Override
	public MetaTileBase newMetaEntity(TileEntityMeta tem) {
		return new MT_BlockBreaker();
	}
	
	@Override
	public void onPostTick() {
		World w = getWorld();
		if (w.isRemote) return;
		EnumFacing f = this.getFacing();
		BlockPos p = JSTUtils.getOffset(this.getPos(), JSTUtils.getOppositeFacing(f), 1);
		TileEntity te = this.getWorld().getTileEntity(p);
		if (te instanceof IInventory) {
			IInventory iinv = ((IInventory) te);
			if (!JSTUtils.checkInventoryFull(iinv, f)) {
				for (int n = 0; n < inv.size(); n++) {
					if (!this.getStackInSlot(n).isEmpty()) {
						ItemStack st = JSTUtils.sendStackToInv(iinv, this.decrStackSize(n, 64), f);
						if (st.isEmpty()) {
							iinv.markDirty();
						} else {
							this.setInventorySlotContents(n, st);
						}
					}
				}
			}
		} else if (f != null && !w.getBlockState(p).isSideSolid(w, p, f)) {
			for (int n = 0; n < this.inv.size(); n++) {
				ItemStack st = this.inv.get(n);
				if (!st.isEmpty()) {
					JSTUtils.dropEntityItemInPos(w, p, st);
					this.inv.set(n, ItemStack.EMPTY);
					this.markDirty();
				}
			}
		}
	}
	
	@Override
	public void onBlockUpdate() {
		if (isClient()) return;
		boolean rs = isRSPowered();
		if (baseTile.setActive(rs) && rs && this.baseTile.isEmpty()) {
			EnumFacing f = getFacing();
			if (f == null) return;
			BlockPos p = JSTUtils.getOffset(getPos(), f, 1);
			World w = getWorld();
			IBlockState bs = w.getBlockState(p);
			Block b = bs.getBlock();
			if (!b.isAir(bs, w, p) && bs.getBlockHardness(w, p) >= 0.0F) {
				if (dontCollectDrop(w, p)) {
					w.destroyBlock(p, true);
				} else {
					if (silkTouch && b.canSilkHarvest(w, p, bs, null)) {
						inv.set(0, b.getItem(w, p, bs));
					} else {
						List<ItemStack> drops = JSTUtils.getBlockDrops(w, p, bs, 0, 1, false, null, true);
						for (int n = 0; n < Math.min(getInvSize(), drops.size()); n++) inv.set(n, drops.get(n).copy());
					}
					w.destroyBlock(p, false);
				}
			}
		}
	}
	
	private static boolean dontCollectDrop(World w, BlockPos p) {
		if (w.getBlockState(p).getBlock() instanceof BlockDoor) return true;
		TileEntity te = w.getTileEntity(p);
		if (te == null) return false;
		return te.getClass().getSimpleName().contains("ShulkerBox");
	}

	@Override
	public int getInvSize() {
		return 27;
	}
	
	@Override
	public void onPlaced(BlockPos p, IBlockState bs, EntityLivingBase elb, ItemStack st) {
		if (this.baseTile == null) return;
		this.baseTile.facing = JSTUtils.getClosestSide(p, elb, false);
		this.onBlockUpdate();
		this.silkTouch = EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, st) > 0;
	}
	
	@Override
	public void getDrops(ArrayList ls) {
		if (baseTile == null) return;
	    ItemStack st = new ItemStack(JSTBlocks.blockTile, 1, baseTile.getID());
	    if (silkTouch) JSTUtils.setEnchant(Enchantments.SILK_TOUCH, 1, st);
	    ls.add(st);
	}
	
	@Override
	public void getSubBlocks(int id, NonNullList<ItemStack> list) {
		ItemStack st = new ItemStack(JSTBlocks.blockTile, 1, id);
		list.add(st);
		st = st.copy();
		JSTUtils.setEnchant(Enchantments.SILK_TOUCH, 1, st);
		list.add(st);
	}
	
	@Override
	public boolean setFacing(EnumFacing f, EntityPlayer pl) {
		return doSetFacing(f, false);
	}
	
	@Override
	public boolean canConnectRedstone(EnumFacing f) {
		return f != JSTUtils.getOppositeFacing(getFacing());
	}
	
	@Override
	public void readSyncableDataFromNBT(NBTTagCompound tag) {
		this.silkTouch = tag.getBoolean("st");
	}

	@Override
	public void writeSyncableDataToNBT(NBTTagCompound tag) {
		tag.setBoolean("st", this.silkTouch);
	}
	
	@Override
	public boolean canExtractItem(int sl, ItemStack st, EnumFacing dir) {
		EnumFacing f = getFacing();
		return f != null && dir == JSTUtils.getOppositeFacing(f) && super.canExtractItem(sl, st, dir);
	}
	
	@Override
	public boolean isItemValidForSlot(int sl, ItemStack st) {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getDefaultTexture() {
		TextureAtlasSprite s = getTETex("basic_side");
		return new TextureAtlasSprite[] {getTETex("st_out") , getTETex("block_breaker_off"), s, s, s, s};
	}

	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getTexture() {
		TextureAtlasSprite[] ret = new TextureAtlasSprite[6];
		for (byte n = 0; n < ret.length; n++) {
			EnumFacing f = JSTUtils.getFacingFromNum(n);
			if (this.baseTile.facing == f) {
				ret[n] = getTETex("block_breaker" + (this.baseTile.isActive() ? "" : "_off"));
			} else if (JSTUtils.getOppositeFacing(getFacing()) == f) {
				ret[n] = getTETex("st_out");
			} else {
				ret[n] = getTETex(this.silkTouch ? "t2_side" : "basic_side");
			}
		}
		return ret;
	}
}

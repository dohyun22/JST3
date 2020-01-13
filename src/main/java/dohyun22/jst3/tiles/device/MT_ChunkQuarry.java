package dohyun22.jst3.tiles.device;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.mojang.authlib.GameProfile;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.client.gui.GUIGeneric;
import dohyun22.jst3.container.BatterySlot;
import dohyun22.jst3.container.ContainerGeneric;
import dohyun22.jst3.network.JSTPacketHandler;
import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.MetaTileEnergyInput;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.tiles.interfaces.IGenericGUIMTE;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MT_ChunkQuarry extends MetaTileEnergyInput implements IGenericGUIMTE {
	private static final GameProfile BREAKER = new GameProfile(UUID.fromString("88092c88-13ed-11e9-ab14-d663bd873d94"), "[JSTQuarry]");
	private short wx, wy = -1, wz, rng, cnt, hlt;
	private int cx, cz;
	private boolean silk, work = true, liq, init;

	@Override
	public MetaTileBase newMetaEntity(TileEntityMeta tem) {
		return new MT_ChunkQuarry();
	}

	@Override
	public int getInvSize() {
		return 97;
	}

	@Override
	public long getMaxEnergy() {
		return 50000;
	}
	
	@Override
	public int maxEUTransfer() {
		return 8192;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		wx = tag.getShort("wx");
		wy = tag.getShort("wy");
		wz = tag.getShort("wz");
		rng = (short) MathHelper.clamp(tag.getShort("rng"), 0, 2);
		cnt = tag.getShort("cnt");
		silk = tag.getBoolean("silk");
		work = tag.getBoolean("work");
		liq = tag.getBoolean("liq");
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		tag.setShort("wx", wx);
		tag.setShort("wy", wy);
		tag.setShort("wz", wz);
		tag.setShort("rng", rng);
		tag.setShort("cnt", cnt);
		tag.setBoolean("silk", silk);
		tag.setBoolean("work", work);
		tag.setBoolean("liq", liq);
	}

	@Override
	public void onPostTick() {
		if (isClient()) return;

		injectEnergy(null, JSTUtils.dischargeItem(inv.get(0), Math.min(getMaxEnergy() - baseTile.energy, maxEUTransfer()), 5, false, false), false);
		if (hlt > 0) {
			hlt--;
			if (hlt % 5 == 0) {
				BlockPos p = getPos();
				for (int x = -rng; x <= rng; x++) for (int z = -rng; z <= rng; z++)
					JSTPacketHandler.playCustomEffect(getWorld(), p.add(x * 16, 0, z * 16), 3, 0);
			}
		}

		if (!init) {
			BlockPos p = getPos();
			cx = (p.getX() >> 4) - rng; cz = (p.getZ() >> 4) - rng;
			init = true;
		}

		if (work && baseTile.energy >= 1200) {
			boolean stop = true;
			for (int n = 1; n < inv.size(); n++) {
				if (inv.get(n).isEmpty()) {
					stop = false;
					break;
				}
			}
			if (stop) return;
			int dur = 32 - MathHelper.clamp((int)baseTile.energy / 1000, 0, 30);
			if (cnt++ >= dur) {
				cnt = 0;
				byte b = breakBlock();
				if (b < 0) {
					work = false;
					return;
				}
				if (b == 0) return;
				if (b == 1) baseTile.energy -= 32;
				if (b == 2) baseTile.energy -= 256;
				int rad = (rng * 2 + 1) * 16;
				wx++;
				if (wx >= rad) {
					wx = 0;
					wz++;
				}
				if (wz >= rad) {
					wz = 0;
					wy--;
				}
			}
		}
	}

	@Override
	public void onPlaced(BlockPos p, IBlockState bs, EntityLivingBase elb, ItemStack st) {
		super.onPlaced(p, bs, elb, st);
		hlt = 100;
	}

	@Override
	public boolean onRightclick(EntityPlayer pl, ItemStack st, EnumFacing f, float hitX, float hitY, float hitZ) {
		if (!isClient())
			pl.openGui(JustServerTweak.INSTANCE, 1, getWorld(), getPos().getX(), getPos().getY(), getPos().getZ());
		return true;
	}

	@Override
	public Object getServerGUI(int id, InventoryPlayer inv, TileEntityMeta te) {
		ContainerGeneric r = new ContainerGeneric(te);
		if (id == 1) {
			r.addSlot(new BatterySlot(te, 0, 8, 35, false, true));
			r.addPlayerSlots(inv, 8, 112);
		} else {
			for (int y = 0; y < 8; y++)
				for (int x = 0; x < 12; x++)
					r.addSlot(new Slot(te, 1 + x + y * 12, 8 + x * 18, 8 + y * 18));
			r.addPlayerSlots(inv, 35, 156);
		}
		return r;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Object getClientGUI(int id, InventoryPlayer inv, TileEntityMeta te) {
		ContainerGeneric sg = (ContainerGeneric)getServerGUI(id, inv, te);
		GUIGeneric r;
		if (id == 1) {
			r = new GUIGeneric(sg, 176, 194);
			r.addSlot(8, 35, 2);
			r.addPwr(13, 15);
			r.addButton(38, 7, 100, 20, 0, "jst.msg.com.startstop", true);
			r.addButton(38, 27, 100, 20, 1, 0);
			r.addButton(38, 47, 100, 20, 2, 1);
			r.addButton(38, 67, 100, 20, 3, "jst.msg.com.highlight", true);
			r.addButton(38, 87, 100, 20, 6, 2);
			r.addButton(149, 7, 20, 20, 4, "inv", false);
			r.addHoverText(149, 7, 20, 20, "jst.msg.com.openinv");
			r.addInv(8, 112);
		} else {
			r = new GUIGeneric(sg, 230, 238);
			for (int x = 0; x < 12; x++)
				for (int y = 0; y < 8; y++)
					r.addSlot(8 + x * 18, 8 + y * 18, 0);
			r.addInv(35, 156);
			r.addButton(7, 155, 20, 20, 5, "\u2190", false);
		}
		return r;
	}

	@Override
	public void handleBtn(int id, EntityPlayer pl) {
		if (id == 0) work = !work;
		if (id == 1) silk = !silk;
		if (id == 2) {
			rng++;
			if (rng >= 3) rng = 0;
			BlockPos p = getPos();
			cx = (p.getX() >> 4) - rng; cz = (p.getZ() >> 4) - rng;
			wx = 0; wy = -1; wz = 0; cnt = 0; work = false;
			hlt = 200;
		}
		if (id == 3) hlt = 200;
		if (id == 4 || id == 5) pl.openGui(JustServerTweak.INSTANCE, id == 4 ? 2 : 1, getWorld(), getPos().getX(), getPos().getY(), getPos().getZ());
		if (id == 6) liq = !liq;
	}

	@Override
	public int[] getGuiData() {
		return new int[] {silk ? 1 : 0, rng, liq ? 1 : 0};
	}

	@Override
	public String guiDataToStr(int id, int dat) {
		if (id == 0) return I18n.format("enchantment.untouching") + ": " + I18n.format(dat == 0 ? "gui.no" : "gui.yes");
		if (id == 1) return I18n.format("jst.msg.miner.area", dat);
		if (id == 2) return I18n.format("jst.msg.miner.liq", I18n.format(dat == 0 ? "gui.no" : "gui.yes"));
		return "";
	}

	@Override
	public boolean isItemValidForSlot(int sl, ItemStack st) {
		return sl != 0;
	}

	@Override
	public boolean canExtractItem(int sl, ItemStack st, EnumFacing f) {
		return super.canExtractItem(sl, st, f) && sl != 0;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getDefaultTexture() {
		TextureAtlasSprite t = getTETex("miner");
		return new TextureAtlasSprite[] {getTETex("block_breaker_off"), getTieredTex(4), t, t, t, t};
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getModelKey() {
		return "jst_quarry";
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getInformation(ItemStack st, World w, List<String> ls, boolean adv) {
		ls.addAll(JSTUtils.getListFromTranslation("jst.tooltip.tile.miner"));
	}

	private byte breakBlock() {
		World w = getWorld();
		if (w instanceof WorldServer) {
			BlockPos p, p2 = getPos();
			p = new BlockPos(cx << 4, p2.getY(), cz << 4).add(wx, wy, wz);
			if (p.getY() < 0 || p.equals(p2)) return -1;
			JSTPacketHandler.playCustomEffect(w, p, 1, 0);
			IBlockState bs = w.getBlockState(p);
			Block b = bs.getBlock();
			Material mat = bs.getMaterial();
			boolean l = mat == Material.LAVA || mat == Material.WATER || b instanceof BlockLiquid || b instanceof IFluidBlock;
			if (!b.isAir(bs, w, p) && bs.getBlockHardness(w, p) >= 0.0F && (liq || !l) && canBreakTE(w.getTileEntity(p)) && JSTUtils.canPlayerBreakThatBlock(FakePlayerFactory.get((WorldServer)w, BREAKER), p)) {
				boolean flag = false;
				if (liq && l) {
					flag = true;
				} else if (MT_BlockBreaker.dontCollectDrop(w, p)) {
					flag = true;
				} else {
					List<ItemStack> drops = new ArrayList();
					if (silk && b.canSilkHarvest(w, p, bs, null))
						drops.add(b.getItem(w, p, bs));
					else
						drops.addAll(JSTUtils.getBlockDrops(w, p, bs, 0, 1, false, null, true));
					flag = true;
					for (ItemStack st : drops) {
						if (st.isEmpty()) continue;
						byte empty = -1;
						for (byte n = 1; n < inv.size(); n++) {
							ItemStack st2 = inv.get(n);
							if (st2.isEmpty()) {
								if (empty < 0) empty = n;
							} else if (JSTUtils.canCombine(st, st2)) {
								int t = st.getCount() + st2.getCount();
								int c = Math.min(t, st2.getItem().getItemStackLimit(st2));
								st2.setCount(c);
								st.setCount(t - c);
							}
						}
						if (!st.isEmpty()) {
							if (empty >= 0)
								inv.set(empty, st);
							else
								JSTUtils.dropEntityItemInPos(w, p2.up(), st);
						}
					}
				}
				if (flag) {
					w.setBlockState(p, Blocks.AIR.getDefaultState(), 2);
					w.playEvent(2001, p, Block.getStateId(bs));
				}
				return 2;
			}
			return 1;
		}
		return -1;
	}

	private boolean canBreakTE(TileEntity te) {
		return !(te instanceof IInventory) && !(te instanceof TileEntityMobSpawner);
	}
}

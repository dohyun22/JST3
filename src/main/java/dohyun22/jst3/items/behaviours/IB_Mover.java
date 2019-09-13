package dohyun22.jst3.items.behaviours;

import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import dohyun22.jst3.utils.JSTSounds;
import dohyun22.jst3.utils.JSTUtils;
import dohyun22.jst3.utils.ReflectionUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.ForgeEventFactory;

public class IB_Mover extends ItemBehaviour {
	private static Method write = ReflectionUtils.getMethodObf(NBTTagCompound.class, new String[] {"func_74734_a", "write", "a"}, DataOutput.class);
	private static HashMap<Class, Movable> classes = new HashMap();

	static {
		addTEClass("cpw.mods.ironchest.TileEntityIronChest", "facing", true);
		addTEClass("cpw.mods.ironchest.common.tileentity.chest.TileEntityIronChest", "facing", true);
		addTEClass("forestry.apiculture.tiles.TileBeeHousingBase", "", true);
		addTEClass("forestry.core.tiles.TileNaturalistChest", "", true);
		addTEClass("thaumcraft.common.tiles.devices.TileHungryChest", "", true);
		addTEClass("t145.metalchests.tiles.TileMetalChest", null, false);
		addTEClass("cubex2.mods.multipagechest.MultiPageChest", "", true);
		addTEClass(TileEntityChest.class, "", true);
		addTEClass(TileEntityEnderChest.class, "", true);
		addTEClass(TileEntityFurnace.class, "", true);
		addTEClass(TileEntityMobSpawner.class, null, false);
		addTEClass(TileEntityDispenser.class, "", false);
		addTEClass(TileEntityHopper.class, "", false);
	}

	@Override
	public EnumActionResult onUseFirst(ItemStack st, EntityPlayer pl, World w, BlockPos p, EnumFacing s, float hx, float hy, float hz, EnumHand h) {
		if (!w.isRemote) {
			NBTTagCompound stt = st.getTagCompound();
			boolean hasBlock = stt != null && stt.hasKey("Block");
			if (hasBlock) {
				if (s != null && !w.getBlockState(p).getBlock().isReplaceable(w, p)) p = p.offset(s);
				if (w.getBlockState(p).getBlock().isReplaceable(w, p)) {
					Block b = JSTUtils.getModBlock(stt.getString("Block"));
					if (b != Blocks.AIR && canPlace(b, w, p)) {
						BlockSnapshot bss = BlockSnapshot.getBlockSnapshot(w, p);
						IBlockState bs = b.getStateFromMeta(stt.getByte("Meta"));
						w.setBlockState(p, bs);
						if (ForgeEventFactory.onPlayerBlockPlace(pl, bss, s, h).isCanceled()) {
							bss.restore(true, false);
						} else {
							TileEntity te = w.getTileEntity(p);
							if (te != null) {
								NBTTagCompound dt = stt.getCompoundTag("Data");
								Movable dir = getDir(te);
								if (dir != null) dir.changeDir(pl, w, p, dt);
								dt.setInteger("x", p.getX());
								dt.setInteger("y", p.getY());
								dt.setInteger("z", p.getZ());
								te = w.getTileEntity(p);
								if (te != null) te.readFromNBT(dt);
							}
							stt.removeTag("Block");
							stt.removeTag("Meta");
							stt.removeTag("Data");
							if (stt.hasNoTags()) st.setTagCompound(null);
							w.playSound(null, p, JSTSounds.SWITCH2, SoundCategory.PLAYERS, 1.0F, 0.75F);
						}
					}
				}
				return EnumActionResult.SUCCESS;
			} else {
				if (stt == null) {
					stt = new NBTTagCompound();
					st.setTagCompound(stt);
				}
				IBlockState bs = w.getBlockState(p);
				TileEntity te = w.getTileEntity(p);
				if (te != null && hasInstance(te) && bs.getPlayerRelativeBlockHardness(pl, w, p) >= 0.0F && bs.getBlockHardness(w, p) >= 0.0F && JSTUtils.canPlayerBreakThatBlock(pl, p)) {
					NBTTagCompound tag = te.writeToNBT(new NBTTagCompound());
					int n = checkSize(tag);
					if (n == 0) {
						tag.removeTag("x");
						tag.removeTag("y");
						tag.removeTag("z");
						stt.setString("Block", JSTUtils.getRegName(bs));
						stt.setByte("Meta", (byte)bs.getBlock().getMetaFromState(bs));
						stt.setTag("Data", tag);
						w.removeTileEntity(p);
						w.setBlockToAir(p);
						w.playSound(null, p, JSTSounds.SWITCH2, SoundCategory.PLAYERS, 1.0F, 0.75F);
					} else if (n == 1) {
						JSTUtils.sendChatTrsl(pl, "jst.msg.mover.toolarge");
					} else if (n == 2) {
						JSTUtils.sendChatTrsl(pl, "jst.msg.com.error");
					}
					return EnumActionResult.SUCCESS;
				}
			}
		}
		return EnumActionResult.PASS;
	}

	@Override
	public boolean canBeStoredInToolbox(ItemStack st) {
		return st.hasTagCompound() ? !st.getTagCompound().hasKey("Block") : true;
	}

	@Override
	public int getItemStackLimit(ItemStack st) {
		return 1;
	}

	@Override
	public void getInformation(ItemStack st, World w, List<String> ls, boolean adv) {
		ls.addAll(JSTUtils.getListFromTranslation("jst.tooltip.mover"));
		NBTTagCompound t = st.getTagCompound();
		if (t != null)
			ls.add(I18n.format("jst.tooltip.mover.filled", JSTUtils.getModBlock(t.getString("Block")).getLocalizedName()));
	}

	public static void addTEClass(Object o, String tag, boolean side) {
		Class c = ReflectionUtils.getClassObj(o);
		if (c != null) classes.put(c, tag == null ? null : new Movable(tag, side));
	}

	private static int checkSize(NBTTagCompound tag) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			DataOutputStream ds = new DataOutputStream(bos);
			write.invoke(tag, ds);
			ds.close();
			if (bos.toByteArray().length > 524288)
				return 1;
		} catch (Throwable t) {
			t.printStackTrace();
			return 2;
		}
		return 0;
	}

	private static boolean canPlace(Block b, World w, BlockPos p) {
		if (b instanceof BlockChest) {
			int cnt = 0;
			for (EnumFacing f : EnumFacing.HORIZONTALS) {
				BlockPos p2 = p.offset(f);
				if (w.getBlockState(p2).getBlock() == b) {
					cnt++;
					for (EnumFacing g : EnumFacing.HORIZONTALS)
						if (g != f.getOpposite() && w.getBlockState(p2.offset(g)).getBlock() == b) cnt++;
				}
			}
			return cnt < 2;
		}
		return true;
	}

	private static boolean hasInstance(TileEntity te) {
		for (Class c : classes.keySet()) if (c.isInstance(te)) return true;
		return false;
	}

	private static Movable getDir(TileEntity te) {
		for (Entry<Class, Movable> c : classes.entrySet()) if (c.getKey().isInstance(te)) return c.getValue();
		return null;
	}

	public static class Movable {
		final String str;
		final boolean side;

		public Movable(String s, boolean b) {
			str = s;
			side = b;
		}

		public void changeDir(EntityPlayer pl, World w, BlockPos p, NBTTagCompound nbt) {
			IBlockState bs = w.getBlockState(p);
			if (str.isEmpty()) {
				for (IProperty prop : bs.getPropertyKeys()) {
					if (prop.getName().equals("facing")) {
						try {
							EnumFacing f = JSTUtils.getClosestSide(p, pl, side);
							if (prop.getAllowedValues().contains(f))
								w.setBlockState(p, bs.withProperty(prop, f));
						} catch (Throwable t) {}
						break;
					}
				}
			} else {
				nbt.setByte(str, (byte) JSTUtils.getClosestSide(p, pl, side).ordinal());
			}
		}
	}
}

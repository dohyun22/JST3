package dohyun22.jst3.tiles.machine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;

import javax.annotation.Nullable;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.api.recipe.RecipeList;
import dohyun22.jst3.client.gui.GUICircuitResearch;
import dohyun22.jst3.client.gui.GUIGeneric;
import dohyun22.jst3.container.ContainerCircuitResearch;
import dohyun22.jst3.items.ItemMetaBase;
import dohyun22.jst3.items.JSTItems;
import dohyun22.jst3.items.behaviours.IB_BluePrint;
import dohyun22.jst3.items.behaviours.ItemBehaviour;
import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.MetaTileEnergyInput;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

public class MT_CircuitResearchMachine extends MetaTileEnergyInput {
	public static final int ROW = 12, COLUMN = 9, SOLDER_PER_WIRE = 20;
	public byte[] listOfGame = new byte[ROW * COLUMN];
	private final int tier;
	public byte solder, gameTier;
	public boolean lvlLoaded;
	private static final HashMap<Integer, ArrayList<byte[][]>> LEVELS = new HashMap();
	private static final HashMap<Integer, Object> BOARDS = new HashMap();

	static {
		for (int n = 1; n <= 10; n++)
			new Wire(n);
		int[][] c = new int[][] { { 0, 1 }, { 1, 0 }, { 0, 2 }, { 2, 0 }, { 0, 3 }, { 3, 0 }, { 1, 2 }, { 2, 1 },
				{ 1, 3 }, { 3, 1 }, { 2, 3 }, { 3, 2 } };
		for (int d = 0; d < 6; d++)
			for (int m = 0; m < c.length; m++)
				new IC(d + m * 6 + 11, c[m][0], c[m][1], d);

		addLvl(1, new byte[][] { { 38, 11 }, { 16, 40 }, { 21, 75 }, { 55, 54 } });
		addLvl(2, new byte[][] { { 14, 16 }, { 20, 24 }, { 53, 17 }, { 56, 71 }, { 62, 11 }, { 90, 60 } });
		addLvl(2, new byte[][] { { 14, 11 }, { 29, 24 }, { 47, 71 }, { 50, 17 }, { 75, 21 }, { 80, 64 } });
		addLvl(3, new byte[][] { { 22, 21 }, { 26, 76 }, { 37, 26 }, { 52, 80 }, { 55, 29 }, { 56, 65 }, { 65, 22 },
				{ 99, 68 } });
		addLvl(3, new byte[][] { { 27, 77 }, { 32, 69 }, { 42, 24 }, { 52, 20 }, { 57, 65 }, { 62, 23 }, { 80, 79 },
				{ 88, 12 } });
		addLvl(3, new byte[][] { { 6, 69 }, { 15, 19 }, { 38, 77 }, { 41, 34 }, { 59, 11 }, { 65, 24 }, { 69, 81 },
				{ 87, 68 } });
		addLvl(4, new byte[][] { { 11, 21 }, { 13, 22 }, { 27, 18 }, { 42, 12 }, { 53, 24 }, { 74, 66 }, { 81, 23 },
				{ 87, 75 }, { 89, 78 }, { 105, 66 } });
		addLvl(4, new byte[][] { { 13, 14 }, { 22, 15 }, { 41, 33 }, { 47, 11 }, { 56, 65 }, { 62, 73 }, { 63, 28 },
				{ 78, 72 }, { 87, 12 }, { 106, 61 } });
		addLvl(4, new byte[][] { { 8, 21 }, { 13, 16 }, { 22, 81 }, { 40, 64 }, { 54, 28 }, { 57, 72 }, { 75, 31 },
				{ 86, 21 }, { 90, 13 }, { 93, 65 } });

		BOARDS.put(1, new ItemStack(JSTItems.item1, 1, 190));
		BOARDS.put(2, new ItemStack(JSTItems.item1, 1, 191));
		BOARDS.put(3, new ItemStack(JSTItems.item1, 1, 192));
		BOARDS.put(4, new ItemStack(JSTItems.item1, 1, 193));
	}

	public MT_CircuitResearchMachine(int t) {
		tier = t;
	}

	private static void addLvl(int lvl, byte[][] data) {
		ArrayList<byte[][]> l = LEVELS.get(Integer.valueOf(lvl));
		if (l == null) {
			l = new ArrayList();
			LEVELS.put(Integer.valueOf(lvl), l);
		}
		l.add(data);
	}

	private boolean loadLvl(int l) {
		if (tier < 3 && tier < l) return false;
		try {
			ArrayList<byte[][]> lvl = LEVELS.get(l);
			if (lvl != null && !lvl.isEmpty()) {
				listOfGame = new byte[ROW * COLUMN];
				byte[][] dat = lvl.get(new Random().nextInt(lvl.size()));
				for (int n = 0; n < dat.length; n++)
					listOfGame[dat[n][0]] = dat[n][1];
				gameTier = (byte)l;
				return true;
			}
		} catch (Exception e) {}
		return false;
	}

	public static int getBoardTier(ItemStack board) {
		if (!board.isEmpty()) {
			for (Entry<Integer, Object> e : BOARDS.entrySet()) {
				Object o = e.getValue();
				if (o instanceof ItemStack && OreDictionary.itemMatches((ItemStack)o, board, false))
					return e.getKey();
				if (o instanceof String && JSTUtils.oreMatches(board, (String)o))
					return e.getKey();
			}
		}
		return -1;
	}

	@Override
	public boolean isItemValidForSlot(int sl, ItemStack st) {
		return super.isItemValidForSlot(sl, st) && !inv.get(sl).isEmpty();
	}

	@Override
	public void onPostTick() {
		super.onPostTick();
		if (isClient())
			return;
		if (tier != 1)
			baseTile.energy -= JSTUtils.chargeItem(getStackInSlot(3), Math.min(baseTile.energy, maxEUTransfer()), tier, false, false);
		if (!lvlLoaded && baseTile.getTimer() % 10 == 0)
			checkAndLoad();
	}

	public void checkAndLoad() {
		int bt = getBoardTier(getStackInSlot(0));
		if (bt >= 0 && canRun()) {
			if (!lvlLoaded && loadLvl(bt))
				lvlLoaded = true;
		} else {
			clearLvl();
		}
	}

	private boolean canRun() {
		if (!JSTUtils.oreMatches(getStackInSlot(1), "paper") || (!lvlLoaded && !JSTUtils.oreMatches(getStackInSlot(2), "wireSolder") && solder <= 0))
			return false;
		if (!OreDictionary.itemMatches(new ItemStack(JSTItems.item1, 1, 10050), getStackInSlot(3), false) || (!lvlLoaded && JSTUtils.getEUInItem(getStackInSlot(3)) < 100))
			return false;
		return true;
	}

	@Override
	public int getInvSize() {
		return 5;
	}

	@Override
	public MetaTileBase newMetaEntity(TileEntityMeta tem) {
		return new MT_CircuitResearchMachine(tier);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getDefaultTexture() {
		TextureAtlasSprite t = getTieredTex((tier - 1) * 2);
		return new TextureAtlasSprite[] { t, getTETex("circuit_research"), t, t, t, t };
	}

	@Override
	public boolean onRightclick(EntityPlayer pl, ItemStack heldItem, EnumFacing side, float hitX, float hitY,
			float hitZ) {
		if (baseTile != null && !getWorld().isRemote)
			pl.openGui(JustServerTweak.INSTANCE, 1, this.getWorld(), this.getPos().getX(), this.getPos().getY(),
					this.getPos().getZ());
		return true;
	}

	@Override
	public Object getClientGUI(int id, InventoryPlayer inv, TileEntityMeta te) {
		return new GUICircuitResearch(new ContainerCircuitResearch(inv, te));
	}

	@Override
	public Object getServerGUI(int id, InventoryPlayer inv, TileEntityMeta te) {
		return new ContainerCircuitResearch(inv, te);
	}

	@Override
	public boolean canAcceptEnergy() {
		return tier != 1;
	}

	@Override
	public boolean isEnergyInput(EnumFacing side) {
		return tier != 1;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		listOfGame = tag.getByteArray("ListOfMiniGame");
		solder = tag.getByte("Solder");
		lvlLoaded = tag.getBoolean("LvlLoaded");
		gameTier = tag.getByte("Tier");
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		tag.setByteArray("ListOfMiniGame", listOfGame);
		tag.setByte("Solder", solder);
		tag.setBoolean("LvlLoaded", lvlLoaded);
		tag.setByte("Tier", gameTier);
	}

	@Override
	public int maxEUTransfer() {
		return tier == 1 ? 0 : JSTUtils.getVoltFromTier((tier - 1) * 2);
	}

	@Override
	public long getMaxEnergy() {
		return maxEUTransfer() * 100;
	}

	public BlockPos intToPos(int i) {
		return new BlockPos(i % ROW, 0, i / ROW);
	}

	public int posToInt(BlockPos i) {
		return MathHelper.clamp(i.getZ() * ROW + i.getX(), 0, listOfGame.length - 1);
	}

	public MiniGameTile getTileAt(BlockPos i) {
		return MiniGameTile.getTile(listOfGame[posToInt(i)]);
	}

	public boolean isValid(BlockPos p) {
		return p.getX() >= 0 && p.getX() <= ROW && p.getZ() >= 0 && p.getZ() <= COLUMN;
	}

	public void checkClear() {
		if (isClient())
			return;
		boolean clear = true;
		for (int n = 0; n < listOfGame.length; n++) {
			MiniGameTile t = MiniGameTile.getTile(listOfGame[n]);
			if (t instanceof IC) {
				int v = 0;
				for (EnumFacing f : EnumFacing.HORIZONTALS) {
					Colors c = ((IC) t).getColor(f);
					if (c != null) {
						BlockPos p = intToPos(n).offset(f);
						if (isConnected(p, c, f.getOpposite()))
							v++;
					}
				}
				if (v < 2) {
					clear = false;
					break;
				}
			}
		}
		if (clear) {
			int consume = 0;
			for (byte b : listOfGame) {
				MiniGameTile t = MiniGameTile.getTile(b);
				if (t instanceof Wire)
					consume++;
			}
			getStackInSlot(0).shrink(1);
			getStackInSlot(1).shrink(1);
			clearLvl();
			makeBlueprint(consume, gameTier);
		}
	}

	private boolean isConnected(BlockPos p, Colors c, EnumFacing from) {
		MiniGameTile t = getTileAt(p);
		if (t instanceof Wire)
			for (EnumFacing f : EnumFacing.HORIZONTALS) {
				if (f == from || !((Wire) t).isConnected(f))
					continue;
				EnumFacing o = f.getOpposite();
				p = p.offset(f);
				if (!isValid(p))
					continue;
				t = getTileAt(p);
				if (t instanceof IC)
					return ((IC) t).getColor(o) == c;
				else if (t instanceof Wire && ((Wire) t).isConnected(o))
					return isConnected(p, c, o);
			}
		return false;
	}

	private void makeBlueprint(int consume, int tier) {
		ItemStack itemStack = new ItemStack(JSTItems.item1, 1, 10051);
		IB_BluePrint.setSizeOfConsumedLeadAndTier(itemStack, consume, tier);
		this.setInventorySlotContents(4, itemStack);
	}

	private void clearLvl() {
		listOfGame = new byte[ROW * COLUMN];
		lvlLoaded = false;
	}

	private void reloadMiniGame() {
	}

	public static abstract class MiniGameTile {
		static final MiniGameTile[] tile = new MiniGameTile[128];
		public final byte id;

		public MiniGameTile(int i) {
			id = (byte) i;
			if (i > 0 && i < tile.length)
				tile[i] = this;
		}

		public abstract void draw(Gui g, int x, int y);

		@Nullable
		public static MiniGameTile getTile(int i) {
			if (i >= 0 && i < tile.length)
				return tile[i];
			return null;
		}
	}

	public static class Wire extends MiniGameTile {
		public Wire(int i) {
			super(i);
		}

		@Override
		public void draw(Gui g, int x, int y) {
			int u = 0, v = 0, w = 13, h = 13;
			switch (id) {
			case 1:
				u += 5;
				w = 3;
				h = 8;
				break;// u
			case 2:
				u += 5;
				v += 5;
				w = 3;
				h = 8;
				break;// d
			case 3:
				v += 5;
				w = 8;
				h = 3;
				break;// l
			case 4:
				u += 5;
				v += 5;
				w = 8;
				h = 3;
				break;// r
			case 5:
				u += 5;
				w = 3;
				break;// ud
			case 6:
				v += 5;
				h = 3;
				break;// lr
			case 7:
				w = 8;
				h = 8;
				break;// ul
			case 8:
				u += 5;
				w = 8;
				h = 8;
				break;// ur
			case 9:
				v += 5;
				w = 8;
				h = 8;
				break;// dl
			case 10:
				u += 5;
				v += 5;
				w = 8;
				h = 8;
				break;// dr
			default:
				u += 5;
				v += 5;
				w = 3;
				h = 3;
			}
			g.drawTexturedModalRect(x + u, y + v, 218 + u, 208 + v, w, h);
		}

		public boolean isConnected(EnumFacing f) {
			int o = 0;
			switch (f) {
			case NORTH:
				o = 1;
				break;
			case SOUTH:
				o = 2;
				break;
			case WEST:
				o = 3;
				break;
			case EAST:
				o = 4;
				break;
			default:
			}
			switch (id) {
			case 1:
				return o == 1;
			case 2:
				return o == 2;
			case 3:
				return o == 3;
			case 4:
				return o == 4;
			case 5:
				return o == 1 || o == 2;
			case 6:
				return o == 3 || o == 4;
			case 7:
				return o == 1 || o == 3;
			case 8:
				return o == 1 || o == 4;
			case 9:
				return o == 2 || o == 3;
			case 10:
				return o == 2 || o == 4;
			}
			return false;
		}
	}

	public static class IC extends MiniGameTile {
		private final Colors c1, c2;
		private final byte dir;// 0=ud 1=lr 2=ul 3=ur 4=dl, 5=dr

		public IC(int i, int o1, int o2, int d) {
			super(i);
			Colors[] c = Colors.values();
			c1 = c[o1];
			c2 = c[o2];
			dir = (byte) d;
		}

		@Override
		public void draw(Gui g, int x, int y) {
			Colors u = null, d = null, l = null, r = null;
			switch (dir) {
			case 0:
				u = c1;
				d = c2;
				g.drawTexturedModalRect(x + 3, y + 4, 217, 1, 7, 5);
				break;
			case 1:
				l = c1;
				r = c2;
				g.drawTexturedModalRect(x + 4, y + 3, 217, 7, 5, 7);
				break;
			case 2:
				u = c1;
				l = c2;
				g.drawTexturedModalRect(x + 4, y + 4, 217, 15, 6, 6);
				break;
			case 3:
				u = c1;
				r = c2;
				g.drawTexturedModalRect(x + 3, y + 4, 217, 22, 6, 6);
				break;
			case 4:
				d = c1;
				l = c2;
				g.drawTexturedModalRect(x + 4, y + 3, 217, 29, 6, 6);
				break;
			case 5:
				d = c1;
				r = c2;
				g.drawTexturedModalRect(x + 3, y + 3, 217, 36, 6, 6);
				break;
			}

			int o;
			if (u != null) {
				o = u.ordinal() * 3;
				g.drawTexturedModalRect(x + 3, y + 2, 234, 17 + o, 7, 2);
			}
			if (d != null) {
				o = d.ordinal() * 3;
				g.drawTexturedModalRect(x + 3, y + 9, 226, 17 + o, 7, 2);
			}
			if (l != null) {
				o = l.ordinal() * 3;
				g.drawTexturedModalRect(x + 2, y + 3, 226 + o, 9, 2, 7);
			}
			if (r != null) {
				o = r.ordinal() * 3;
				g.drawTexturedModalRect(x + 9, y + 3, 226 + o, 1, 2, 7);
			}
		}

		public Colors getColor(EnumFacing f) {
			switch (dir) {
			case 0:
				if (f == EnumFacing.NORTH)
					return c1;
				if (f == EnumFacing.SOUTH)
					return c2;
				break;
			case 1:
				if (f == EnumFacing.WEST)
					return c1;
				if (f == EnumFacing.EAST)
					return c2;
				break;
			case 2:
				if (f == EnumFacing.NORTH)
					return c1;
				if (f == EnumFacing.WEST)
					return c2;
				break;
			case 3:
				if (f == EnumFacing.NORTH)
					return c1;
				if (f == EnumFacing.EAST)
					return c2;
				break;
			case 4:
				if (f == EnumFacing.SOUTH)
					return c1;
				if (f == EnumFacing.WEST)
					return c2;
				break;
			case 5:
				if (f == EnumFacing.SOUTH)
					return c1;
				if (f == EnumFacing.EAST)
					return c2;
				break;
			}
			return null;
		}
	}

	public static enum Colors {
		RED, YELLOW, BLUE, GREEN
	}
}

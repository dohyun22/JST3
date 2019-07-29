package dohyun22.jst3.tiles.test;

import java.util.Random;

import javax.annotation.Nullable;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.api.recipe.RecipeList;
import dohyun22.jst3.client.gui.GUIAssembler;
import dohyun22.jst3.client.gui.GUICircuitResearch;
import dohyun22.jst3.client.gui.GUIGeneric;
import dohyun22.jst3.container.ContainerCircuitResearch;
import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.MetaTileEnergyInput;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.tiles.machine.MT_MachineGeneric;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MT_CircuitResearchMachine extends MetaTileEnergyInput {
	public static final int row = 12, column = 9, solderPerWire = 20;
	public byte[] listOfGame = new byte[row * column];
	private final int tier;
	private byte solder;

	static {
		for (int n = 1; n <= 10; n++) new Wire(n);
		int[][] c = new int[][] {{0,1}, {1,0}, {0,2}, {2,0}, {0,3}, {3,0}, {1,2}, {2,1}, {1,3}, {3,1}, {2,3}, {3,2}};
		for (int d = 0; d < 6; d++) for (int m = 0; m < c.length; m++) new IC(d + m * 6 + 11, c[m][0], c[m][1], d);
	}

	public MT_CircuitResearchMachine(int tier) {
		// Removed broken korean characters.
		// 9*6(tier 0~2)->12*9(tier 3)
		this.tier = tier;

		for (int n = 1; n < MiniGameTile.tile.length; n++) {
			MiniGameTile t = MiniGameTile.tile[n];
			if (t != null) listOfGame[n] = t.id;
		}
	}

	@Override
	public boolean isItemValidForSlot(int sl, ItemStack st) {
		return super.isItemValidForSlot(sl, st) && !inv.get(sl).isEmpty();
	}

	@Override
	public void onPostTick() {
		super.onPostTick();
		if (isClient()) return;

		/*
		 * ItemStack stEnd = inv.get(inputNum - 1); if(!stEnd.isEmpty()) return;
		 * 
		 * for (int n = 0; n < inputNum - 1; n++) { ItemStack st = inv.get(n); if
		 * (st.isEmpty()) return; switch(n) { case 0: break;
		 * 
		 * default: break; } }
		 */

		/*Random r = new Random();
		for (int i = 0; i < listOfGame.length; i++) {
			byte value = (byte) r.nextInt(3);
			listOfGame[i] = (byte) value;
		}*/
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
		return new TextureAtlasSprite[] {t, getTETex("circuit_research"), t, t, t, t};
	}

	@Override
	public boolean onRightclick(EntityPlayer pl, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (baseTile == null || getWorld().isRemote)
			return true;
		pl.openGui(JustServerTweak.INSTANCE, 1, this.getWorld(), this.getPos().getX(), this.getPos().getY(), this.getPos().getZ());
		return true;
	}

	@Override
	public Object getClientGUI(int id, InventoryPlayer inv, TileEntityMeta te) {
		return new GUICircuitResearch(new ContainerCircuitResearch(inv, te));
	}

	@Override
	public Object getServerGUI(int id, InventoryPlayer inv, TileEntityMeta te) {
		if (id == 1)
			return new ContainerCircuitResearch(inv, te);
		return null;
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
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		tag.setByteArray("ListOfMiniGame", listOfGame);
		tag.setByte("Solder", solder);
	}

	@Override
	public int maxEUTransfer() {
		return tier == 1 ? 0 : JSTUtils.getTierFromVolt((tier - 1) * 2);
	}
	
	@Override
	public long getMaxEnergy() {
		return maxEUTransfer() * 100;
	}

	protected void saveGameToBlueprint(ItemStack bp) {
	}

	private BlockPos intToPos(int i) {
		return new BlockPos(i % row, i / row, 0);
	}

	private int posToInt(BlockPos i) {
		return MathHelper.clamp(i.getY() * row + i.getX(), 0, listOfGame.length - 1);
	}

	private MiniGameTile getTileAt(BlockPos i) {
		return MiniGameTile.getTile(listOfGame[posToInt(i)]);
	}

	private Colors getColor(BlockPos xy) {
		MiniGameTile t = getTileAt(xy);
		if (t instanceof Wire) {
			for (EnumFacing f : EnumFacing.HORIZONTALS) {
				
			}
		}
		return null;
	}

	public void placeWire(BlockPos xy, EnumFacing f) {
		byte i = listOfGame[posToInt(xy)];
	}

	public static abstract class MiniGameTile {
		static final MiniGameTile[] tile = new MiniGameTile[128];
		public final byte id;

		public MiniGameTile(int i) {
			id = (byte) i;
			if (i > 0 && i < tile.length) tile[i] = this;
		}

		public abstract void draw(Gui g, int x, int y);
		public void onNotify(byte[] map, EnumFacing f, BlockPos xy) {}
		public void onPlace(byte[] map, BlockPos xy) {}

		@Nullable
		public static MiniGameTile getTile(int i) {
			if (i >= 0 && i < tile.length) return tile[i];
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
			case 1: u += 5; w = 3; h = 8; break;//u
			case 2: u += 5; v += 5; w = 3; h = 8; break;//d
			case 3: v += 5; w = 8; h = 3; break;//l
			case 4: u += 5; v += 5; w = 8; h = 3; break;//r
			case 5: u += 5; w = 3; break;//ud
			case 6: v += 5; h = 3; break;//lr
			case 7: w = 8; h = 8; break;//ul
			case 8: u += 5; w = 8; h = 8; break;//ur
			case 9: v += 5; w = 8; h = 8; break;//dl
			case 10: u += 5; v += 5; w = 8; h = 8; break;//dr
			default: u += 5; v += 5; w = 3; h = 3;
			}
			g.drawTexturedModalRect(x + u, y + v, 218 + u, 208 + v, w, h);
		}
	}

	public static class IC extends MiniGameTile {
		private final Colors c1, c2;
		private final byte dir;//0=ud 1=lr 2=ul 3=ur 4=dl, 5=dr

		public IC(int i, int o1, int o2, int d) {
			super(i);
			Colors[] c = Colors.values();
			c1 = c[o1]; c2 = c[o2]; dir = (byte) d;
		}

		@Override
		public void draw(Gui g, int x, int y) {
			Colors u = null, d = null, l = null, r = null;
			switch (dir) {
			case 0: u = c1; d = c2; g.drawTexturedModalRect(x + 3, y + 4, 217, 1, 7, 5); break;
			case 1: l = c1; r = c2; g.drawTexturedModalRect(x + 4, y + 3, 217, 7, 5, 7); break;
			case 2: u = c1; l = c2; g.drawTexturedModalRect(x + 4, y + 4, 217, 15, 6, 6); break;
			case 3: u = c1; r = c2; g.drawTexturedModalRect(x + 3, y + 4, 217, 22, 6, 6); break;
			case 4: d = c1; l = c2; g.drawTexturedModalRect(x + 4, y + 3, 217, 29, 6, 6); break;
			case 5: d = c1; r = c2; g.drawTexturedModalRect(x + 3, y + 3, 217, 36, 6, 6); break;
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
			case 0: if (f == EnumFacing.NORTH) return c1; if (f == EnumFacing.SOUTH) return c2; break;
			case 1: if (f == EnumFacing.WEST) return c1; if (f == EnumFacing.EAST) return c2; break;
			case 2: if (f == EnumFacing.NORTH) return c1; if (f == EnumFacing.WEST) return c2; break;
			case 3: if (f == EnumFacing.NORTH) return c1; if (f == EnumFacing.EAST) return c2; break;
			case 4: if (f == EnumFacing.SOUTH) return c1; if (f == EnumFacing.WEST) return c2; break;
			case 5: if (f == EnumFacing.SOUTH) return c1; if (f == EnumFacing.EAST) return c2; break;
			}
			return null;
		}
	}

	public static enum Colors {
		RED, YELLOW, BLUE, GREEN
	}
}

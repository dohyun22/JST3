package dohyun22.jst3.tiles.multiblock;

import java.util.List;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.blocks.JSTBlocks;
import dohyun22.jst3.client.gui.GUIMulti;
import dohyun22.jst3.container.ContainerMulti;
import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.tiles.noupdate.MetaTileDCWind;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MT_Dam extends MT_Multiblock {
	private int turbines, water1, water2, surface, flow;
	private boolean damNear;

	@Override
	public MetaTileBase newMetaEntity(TileEntityMeta tem) {
		return new MT_Dam();
	}

	@Override
	public void onPlaced(BlockPos p, IBlockState bs, EntityLivingBase elb, ItemStack st) {
		super.onPlaced(p, bs, elb, st);
		searchArea();
	}

	@Override
	public void onPostTick() {
		super.onPostTick();
		int cnt = water1 + water2 >= 1000 ? 1200 : 600;
		if (!isClient() && baseTile.getTimer() % cnt == 0) {
			setUpdateTimer();
			searchArea();
			flow = MetaTileDCWind.updateWind(flow, getWorld().rand);
		}
	}

	@Override
	protected void clearPorts() {
		super.clearPorts();
		turbines = 0;
		water1 = 0;
		water2 = 0;
		surface = 0;
	}

	@Override
	protected boolean checkStructure() {
		BlockPos p = getPos(), p2;
		Biome b = getWorld().getBiome(p);
		int w = 0;
		if (!BiomeDictionary.hasType(b, BiomeDictionary.Type.RIVER))
			return false;
		EnumFacing[] dir; boolean flag = true;
		if (check(p, true, true) == 1 && water1 + water2 > 0) {
			dir = new EnumFacing[] {EnumFacing.NORTH, EnumFacing.SOUTH};
		} else {
			surface = 0;
			if (check(p, true, false) == 1 && water1 + water2 > 0) {
				dir = new EnumFacing[] {EnumFacing.WEST, EnumFacing.EAST};
				flag = false;
			} else {
				return false;
			}
		}
		for (EnumFacing f : dir) {
			for (int n = 1; n <= 50; n++) {
				p2 = p.offset(f, n);
				int a = check(p2, false, flag);
				if (a == -1)
					return false;
				else if (a == 0)
					break;
				w++;
			}
		}
		if (w < 10 || water1 <= 0 || water2 <= 0 || water1 + water2 < surface / 4 || turbines <= 0 || energyOutput.size() != 1)
			return false;
		updateEnergyPort(16384, 32768, true);
		return true;
	}

	@Override
	protected boolean checkCanWork() {
		return true;
	}

	@Override
	protected void doWork() {
		if (energyOutput.isEmpty()) return;
		World w = getWorld();
		if (w.isBlockLoaded(energyOutput.get(0))) {
			MT_EnergyPort p = getEnergyPort(0, true);
			if (p != null && turbines > 0) {
				if (flow <= 0) flow = MetaTileDCWind.updateWind(flow, w.rand);
				double eu = (water1 + water2) * 1.5 * (flow / 10.0D);
				eu += Math.abs(water1 - water2);
				if (damNear) eu /= 10.0D;
				eu = Math.min(eu, turbines * 256);
				if (p.baseTile.energy + (int) eu <= p.getMaxEnergy())
					p.baseTile.energy += (int) eu;
			}
		}
	}

	@Override
	public boolean canAcceptEnergy() {
		return false;
	}
	
	@Override
	public boolean isEnergyInput(EnumFacing f) {
		return false;
	}

	@Override
	protected boolean needEnergy() {
		return false;
	}

	@Override
	public boolean onRightclick(EntityPlayer pl, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (!isClient())
			pl.openGui(JustServerTweak.INSTANCE, 1, getWorld(), getPos().getX(), getPos().getY(), getPos().getZ());
		return true;
	}
	
	@Override
	public Object getServerGUI(int id, InventoryPlayer inv, TileEntityMeta te) {
		return new ContainerMulti(inv, te);
	}

	@Override
	public Object getClientGUI(int id, InventoryPlayer inv, TileEntityMeta te) {
		byte[][][] data = {
				{{4},{4},{4},{4},{4},{4},{4}},
				{{4},{4},{4},{4},{4},{4},{4}},
				{{4},{4},{4},{4},{4},{4},{4}},
				{{3},{3},{3},{3},{3},{3},{3}},
				{{3},{3},{3},{1},{3},{3},{3}},
		};
		return new GUIMulti(new ContainerMulti(inv, te), data);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		turbines = tag.getInteger("tb");
		water1 = tag.getInteger("w1");
		water2 = tag.getInteger("w2");
		flow = tag.getInteger("fw");
		damNear = tag.getBoolean("dn");
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setInteger("tb", turbines);
		tag.setInteger("w1", water1);
		tag.setInteger("w2", water2);
		tag.setInteger("fw", flow);
		tag.setBoolean("dn", damNear);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getDefaultTexture() {
		TextureAtlasSprite a = getTETex("screen3"), b = getTex(JustServerTweak.MODID + ":blocks/metablocks/plated_block");
		return new TextureAtlasSprite[] {b, a, b, b, b, b};
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getModelKey() {
		return "jst_dam";
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInfo(ItemStack st, List<String> ls) {
		ls.addAll(JSTUtils.getListFromTranslation("jst.tooltip.tile.dam"));
	}

	private int check(BlockPos p, boolean cen, boolean ns) {
		World w = getWorld();
		MutableBlockPos m = new MutableBlockPos();
		boolean nothing = true;
		for (int n = 0; n < 32; n++) {
			if (cen && n == 0) continue;
			m.setPos(p.getX(), p.getY() - n, p.getZ());
			if (m.getY() <= 0) break;
			IBlockState bs = getWorld().getBlockState(m);
			int id = MetaTileBase.getMTEId(w, m);
			if ((bs.getBlock() == JSTBlocks.block1 && bs.getBlock().getMetaFromState(bs) == 1) || getAndAddPort(p, 2, null) || id == 5085) {
				nothing = false;
				surface++;
				int d = water1 + water2 + 1;
				if (ns) {
					m.move(EnumFacing.WEST);
					if (w.getBlockState(m).getMaterial() == Material.WATER) water1++;
					m.move(EnumFacing.EAST, 2);
					if (w.getBlockState(m).getMaterial() == Material.WATER) water2++;
				} else {
					m.move(EnumFacing.NORTH);
					if (w.getBlockState(m).getMaterial() == Material.WATER) water1++;
					m.move(EnumFacing.SOUTH, 2);
					if (w.getBlockState(m).getMaterial() == Material.WATER) water2++;
				}
				if (id == 5085 && n >= 2 && water1 + water2 > d) turbines++;
			} else if (id == 6049) {
				return -1;
			} else if (bs.isFullCube()) {
				break;
			} else {
				return -1;
			}
		}
		return nothing ? 0 : 1;
	}

	private void searchArea() {
		int cx = getPos().getX() >> 4, cz = getPos().getZ() >> 4;
		World w = getWorld();
		for (int x = -3; x <= 3; x++) {
			for (int z = -3; z <= 3; z++) {
				if (w.isChunkGeneratedAt(cx + x, cz + z)) {
					Chunk c = w.getChunkFromChunkCoords(cx + x, cz + z);
					if (c == null) continue;
					for (TileEntity te : c.getTileEntityMap().values())
						if (te instanceof TileEntityMeta && te != baseTile && ((TileEntityMeta)te).mte instanceof MT_Dam) {
							damNear = true;
							return;
						}
				}
			}
		}
		damNear = false;
	}
}

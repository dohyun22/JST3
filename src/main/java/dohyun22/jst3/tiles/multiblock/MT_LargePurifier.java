package dohyun22.jst3.tiles.multiblock;

import java.util.List;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.client.gui.GUIMulti;
import dohyun22.jst3.container.ContainerMulti;
import dohyun22.jst3.items.JSTItems;
import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.utils.JSTChunkData;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MT_LargePurifier extends MT_Multiblock {

	@Override
	public MetaTileBase newMetaEntity(TileEntityMeta tem) {
		return new MT_LargePurifier();
	}

	@Override
	protected boolean checkStructure() {
		MutableBlockPos p = new MutableBlockPos();
		BlockPos p2 = getPos();
		World w = getWorld();
		for (int y = -2; y <= 0; y++) {
			for (int x = -1; x <= 1; x++) {
				for (int z = -1; z <= 1; z++) {
					if ((x | y | z) == 0) continue;
					p.setPos(p2.getX() + x, p2.getY() + y, p2.getZ() + z);
					if (x == 0 && y == -1 && z == 0) {
						if (!w.isAirBlock(p)) return false;
					} else if (y <= -1) {
						if (MetaTileBase.getMTEId(w, p) != 5002 && !getAndAddPort(p, 25, "csg_b")) return false;
					} else {
						if (x == 0 && z == 0) continue;
						if (MetaTileBase.getMTEId(w, p) != 5084) return false;
					}
				}
			}
		}
		if (energyInput.size() != 1 || fluidInput.size() > 1 || itemOutput.size() > 1) return false;
		updateEnergyPort(128, 1024, false);
		return true;
	}

	@Override
	protected int getCheckTimer() {
		return 200;
	}

	@Override
	protected boolean checkCanWork() {
		BlockPos p = getPos();
		int cx = getPos().getX() >> 4, cz = getPos().getZ() >> 4;
    	for (int x = -1; x <= 1; x++) {
	    	for (int z = -1; z <= 1; z++) {
	    		ChunkPos cp = new ChunkPos(cx + x, cz + z);
	    		if (JSTChunkData.getFineDust(getWorld(), cp) > 0) {
	    			energyUse = 100;
	    			mxprogress = 100;
	    			return true;
	    		}
	    	}
    	}
		return false;
	}

	@Override
	protected void finishWork() {
		boolean flag = false;
		MT_FluidPort fp = getFluidPort(0, false);
		if (fp != null && fp.tank.getFluidAmount() >= 1000) {
			FluidStack fs = fp.tank.drainInternal(new FluidStack(FluidRegistry.WATER, 1000), true);
			if (fs != null) flag = true;
		}
		World w = getWorld();
		int totalDust = 0, cx = getPos().getX() >> 4, cz = getPos().getZ() >> 4, amt = 7500, d;
		if (flag) amt *= 2;
		for (int x = -2; x <= 2; x++) {
			for (int z = -2; z <= 2; z++) {
				ChunkPos cp = new ChunkPos(cx + x, cz + z);
				d = JSTChunkData.getFineDust(w, cp);
				if (d > 0) {
					totalDust += d;
					JSTChunkData.addFineDust(w, cp, amt, true);
				}
			}
		}
		if (totalDust > 0 && getWorld().rand.nextInt(Math.max(4, 64 - totalDust / 4000)) == 0)
			sendItem(new ItemStack(JSTItems.item1, 1, 109));
		super.finishWork();
	}

	@Override
	protected boolean isPortPowered() {
		return true;
	}

	@Override
	public boolean canAcceptEnergy() {
		return false;
	}

	@Override
	public boolean isEnergyInput(EnumFacing side) {
		return false;
	}

	@Override
	public boolean onRightclick(EntityPlayer pl, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (baseTile != null && !getWorld().isRemote)
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
				{{4,4,4},
				{4,4,4},
				{4,4,4}},
				{{4,4,4},
				{4,0,4},
				{4,4,4}},
				{{7,7,7},
				{7,1,7},
				{7,7,7}}
		};
		return new GUIMulti(new ContainerMulti(inv, te), data);
	}

	@Override
	protected void updateClient() {
		if (baseTile.isActive()) {
			World w = getWorld();
			if (w.rand.nextInt(8) == 0) {
				BlockPos p = getPos();
				for (int i = 0; i < 6; i++) {
					double x = 0.5D + w.rand.nextFloat() * 3.2D - 1.4D;
					double y = p.getY() + 1.8D + w.rand.nextFloat() * 0.4D;
					double z = 0.5D + w.rand.nextFloat() * 3.2D - 1.4D;
					w.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, p.getX() + x, y, p.getZ() + z, -x * 0.05D, -0.2D - w.rand.nextFloat() * 0.1D, -z * 0.05D);
				}
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getDefaultTexture() {
		TextureAtlasSprite t = getTETex("csg_b");
		return new TextureAtlasSprite[] {t, getTETex("filter"), t, t, t, t};
	}
	
	@SideOnly(value=Side.CLIENT)
	public String getModelKey() {
		return "jst_lpuri";
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void addInfo(ItemStack st, List<String> ls) {
		ls.addAll(JSTUtils.getListFromTranslation("jst.tooltip.tile.largepurifier"));
	}
}

package dohyun22.jst3.tiles.multiblock;

import java.util.List;
import java.util.Random;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.client.gui.GUIMulti;
import dohyun22.jst3.container.ContainerMulti;
import dohyun22.jst3.network.JSTPacketHandler;
import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.utils.JSTDamageSource;
import dohyun22.jst3.utils.JSTSounds;
import dohyun22.jst3.utils.JSTUtils;
import dohyun22.jst3.utils.JSTDamageSource.EnumHazard;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MT_LargeTesla extends MT_Multiblock {
	private static final int ENERGY_PER_SHOT = 2000;

	@Override
	protected boolean checkStructure() {
		for (int y = 1; y <= 4; y++)
			if (MetaTileBase.getMTEId(getWorld(), getPos().up(y)) != 5069)
				return false;

		for (int x = -1; x <= 1; x++)
			for (int z = -1; z <= 1; z++)
				if (MetaTileBase.getMTEId(getWorld(), getPos().add(x, 5, z)) != 2)
					return false;
				
		return true;
	}
	
	@Override
	public long getMaxEnergy() {
		return 20000;
	}
	
	@Override
	public int maxEUTransfer() {
		return 512;
	}

	@Override
	protected boolean checkCanWork() {
		return getWorld().isBlockPowered(getPos());
	}

	@Override
	public MetaTileBase newMetaEntity(TileEntityMeta tem) {
		return new MT_LargeTesla();
	}

	@Override
	protected void doWork() {
	    if (isClient() || baseTile.getTimer() % 10 != 0) return;
		
		int r = 16;
	    World w = getWorld();
	    BlockPos p = getPos().up(5);
	    if (baseTile.energy >= ENERGY_PER_SHOT) {
	    	boolean flag = true;
	    	if (baseTile.getTimer() % 30 != 0) {
				List<EntityLivingBase> ls = w.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(p.getX() - r, p.getY() - r, p.getZ() - r, p.getX() + r + 1, p.getY() + r + 1, p.getZ() + r + 1));
				if (!ls.isEmpty()) {
					r = 2;
					EntityLivingBase e = ls.get(w.rand.nextInt(ls.size()));
					ls = w.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(e.posX - r, e.posY - r, e.posZ - r, e.posX + r, e.posY + r, e.posZ + r));
					for (EntityLivingBase e2 : ls) {
						if (e2 instanceof EntityPlayer) continue;
						if (e2.attackEntityFrom(JSTDamageSource.getElectricDamage(), JSTDamageSource.hasFullHazmat(EnumHazard.ELECTRIC, e2) ? 0 : 20)) {
							baseTile.energy -= ENERGY_PER_SHOT;
							JSTPacketHandler.playCustomEffect(w, p, 1, 0);
							flag = false;
						}
					}
					if (!flag) {
						w.playSound(null, p, JSTSounds.SHOCK, SoundCategory.BLOCKS, 2.0F, 1.0F);
						JSTPacketHandler.playCustomEffect(w, p, 2, e.getPosition().toLong());
					}
				}
	    	}
			
			if (flag && w.rand.nextInt(5) == 0) {
				BlockPos p2 = p.add(w.rand.nextInt(r * 2 + 1) - r, w.rand.nextInt(r * 2 + 1) - r, w.rand.nextInt(r * 2 + 1) - r);
				if (p.getDistance(p2.getX(), p2.getY(), p2.getZ()) > 8) {
					w.playSound(null, p, JSTSounds.SHOCK, SoundCategory.BLOCKS, 2.0F, 1.0F);
					baseTile.energy -= ENERGY_PER_SHOT / 4;
					JSTPacketHandler.playCustomEffect(w, p, 2, p2.toLong());
				}
			}
	    }
		if (!checkCanWork())
			stopWorking(false);
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
				{{0,0,0},
				{0,1,0},
				{0,0,0}},
				{{0,0,0},
				{0,2,0},
				{0,0,0}},
				{{0,0,0},
				{0,2,0},
				{0,0,0}},
				{{0,0,0},
				{0,2,0},
				{0,0,0}},
				{{0,0,0},
				{0,2,0},
				{0,0,0}},
				{{4,4,4},
				{4,4,4},
				{4,4,4}}
		};
		return new GUIMulti(new ContainerMulti(inv, te), data);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getDefaultTexture() {
		TextureAtlasSprite t = getTETex("hvsign");
		return new TextureAtlasSprite[] {getTieredTex(2), getTETex("e1d"), t, t, t, t};
	}
	
	@SideOnly(value=Side.CLIENT)
	public String getModelKey() {
		return "jst_ltesla";
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInfo(ItemStack st, List<String> ls) {
		ls.addAll(JSTUtils.getListFromTranslation("jst.tooltip.tile.tesla"));
	}
}

package dohyun22.jst3.tiles.device;

import java.util.UUID;

import com.google.common.base.Predicate;
import com.mojang.authlib.GameProfile;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.client.gui.GUIGeneric;
import dohyun22.jst3.container.ContainerGeneric;
import dohyun22.jst3.container.JSTSlot;
import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.tiles.interfaces.IGenericGUIMTE;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MT_BlockPlacer extends MetaTileBase implements IGenericGUIMTE {
	private static final GameProfile PLACER = new GameProfile(UUID.fromString("88092c88-13ed-11e9-ab14-d663bd873d93"), "[JSTPlacer]");

	@Override
	public MetaTileBase newMetaEntity(TileEntityMeta tem) {
		return new MT_BlockPlacer();
	}
	
	@Override
	public boolean canUpdate() {
		return false;
	}
	
	@Override
	public void onBlockUpdate() {
		if (isClient()) return;
		boolean rs = isRSPowered();
		boolean ac = baseTile.setActive(rs);
		if (ac)
			doBlockUpdate();
		if (ac && rs) {
			EnumFacing f = baseTile.facing;
			if (f == null) return;
			BlockPos p = getPos();
			World w = getWorld();
			if (w instanceof WorldServer) {
				FakePlayer fp = FakePlayerFactory.get((WorldServer)w, PLACER);
			    double dx = p.getX() + 0.5D, dy = p.getY() - 1.1D, dz = p.getZ() + 0.5D;
			    float yaw, pitch;
			    switch (f) {
				case DOWN:
					pitch = -90.0F;yaw = 0.0F;dy += 0.51D; break;
				case UP:
					pitch = 90.0F;yaw = 0.0F;dy -= 0.51D; break;
				case NORTH:
					pitch = 0.0F;yaw = 0.0F;dz += 0.51D; break;
				case SOUTH:
					pitch = 0.0F;yaw = 180.0F;dz -= 0.51D; break;
				case WEST:
					pitch = 0.0F;yaw = 270.0F;dx += 0.51D; break;
				default:
					pitch = 0.0F;yaw = 90.0F;dx -= 0.51D; break;
			    }
			    fp.setPositionAndRotation(dx, dy, dz, yaw, pitch);

				for (int n = 0; n < inv.size(); n++) {
					ItemStack st = inv.get(n);
					if (!st.isEmpty()) {
						try {
							boolean seed = st.getItem() instanceof ItemSeeds;
							p = p.offset(f);
							if (seed)
								p = p.offset(EnumFacing.DOWN);
							fp.setHeldItem(EnumHand.MAIN_HAND, st);
							EnumActionResult res = ForgeHooks.onPlaceItemIntoWorld(st, fp, w, p, seed ? EnumFacing.UP : f.getOpposite(), 0.5F, 0.5F, 0.5F, EnumHand.MAIN_HAND);
							if (st.isEmpty())
								inv.set(n, ItemStack.EMPTY);
							if (res != EnumActionResult.FAIL)
								break;
						} catch (Throwable t) {
							break;
						}
					}
				}
			}
		}
	}
	
	@Override
	public int getInvSize() {
		return 9;
	}
	
	@Override
	public void onPlaced(BlockPos p, IBlockState bs, EntityLivingBase elb, ItemStack st) {
		if (baseTile == null) return;
		baseTile.facing = JSTUtils.getClosestSide(p, elb, false);
		onBlockUpdate();
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
	public boolean onRightclick(EntityPlayer pl, ItemStack st, EnumFacing f, float hX, float hY, float hZ) {
		if (baseTile != null && !isClient())
			pl.openGui(JustServerTweak.INSTANCE, 1, getWorld(), baseTile.getPos().getX(), baseTile.getPos().getY(), baseTile.getPos().getZ());
		return true;
	}

	@Override
	public boolean canInsertItem(int sl, ItemStack st, EnumFacing dir) {
		return super.canInsertItem(sl, st, dir) && dir != baseTile.facing && !st.isEmpty() && st.getItem() instanceof ItemBlock;
	}

	@Override
	public int[] getSlotsForFace(EnumFacing dir) {
		if (baseTile.facing != dir)
			return super.getSlotsForFace(dir);
		return EMPTY_LIST;
	}

	@Override
	public Object getServerGUI(int id, InventoryPlayer inv, TileEntityMeta te) {
		ContainerGeneric ret = new ContainerGeneric(inv, te);
		for (int y = 0; y < 3; ++y)
			for (int x = 0; x < 3; ++x)
				ret.addSlot(new JSTSlot(te, x + y * 3, 62 + x * 18, 17 + y * 18));
		ret.addPlayerSlots(inv);
		return ret;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Object getClientGUI(int id, InventoryPlayer inv, TileEntityMeta te) {
		GUIGeneric ret = new GUIGeneric((ContainerGeneric) getServerGUI(id, inv, te));
		for (int y = 0; y < 3; y++)
	    	for (int x = 0; x < 3; x++)
	    		ret.addSlot(62 + x * 18, 17 + y * 18, 0);
		return ret;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getDefaultTexture() {
		TextureAtlasSprite s = getTETex("basic_side");
		return new TextureAtlasSprite[] {s, getTETex("block_placer"), s, s, s, s};
	}

	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getTexture() {
		TextureAtlasSprite[] ret = new TextureAtlasSprite[6];
		for (byte n = 0; n < ret.length; n++) {
			EnumFacing f = JSTUtils.getFacingFromNum(n);
			if (baseTile.facing == f)
				ret[n] = getTETex("block_placer" + (baseTile.isActive() ? "_on" : ""));
			else
				ret[n] = getTETex("basic_side");
		}
		return ret;
	}
}

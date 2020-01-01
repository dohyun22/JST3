package dohyun22.jst3.tiles.energy;

import javax.annotation.Nullable;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.client.gui.GUIGeneric;
import dohyun22.jst3.container.ContainerGeneric;
import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.tiles.interfaces.IGenericGUIMTE;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.Explosion;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MT_CreativeGenerator extends MetaTileBase implements IGenericGUIMTE {
	private int out;

	@Override
	public MetaTileBase newMetaEntity(TileEntityMeta tem) {
		return new MT_CreativeGenerator();
	}

	@Override
	public void onPostTick() {
		super.onPostTick();
		if (out > 0)
			for (EnumFacing f : EnumFacing.VALUES)
				JSTUtils.sendEnergy(getWorld(), getPos().offset(f), f.getOpposite(), out, false);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getDefaultTexture() {
		return getSingleTETex("creativegen");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getModelKey() {
		return "jst_crgen";
	}
	
	@Override
	public boolean canProvideEnergy() {
		return true;
	}
	
	@Override
	public boolean isEnergyOutput(EnumFacing f) {
		return true;
	}

	@Override
	public boolean onRightclick(EntityPlayer pl, ItemStack st, EnumFacing f, float hX, float hY, float hZ) {
		if (baseTile != null && !isClient() && pl.isCreative())
			pl.openGui(JustServerTweak.INSTANCE, 1, getWorld(), getPos().getX(), getPos().getY(), getPos().getZ());
		return true;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		out = tag.getInteger("out");
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		tag.setInteger("out", out);
	}

	@Override
	public Object getServerGUI(int id, InventoryPlayer inv, TileEntityMeta te) {
		ContainerGeneric r = new ContainerGeneric(te);
		r.addPlayerSlots(inv, 31, 84);
		return r;
	}

	@Override
	public Object getClientGUI(int id, InventoryPlayer inv, TileEntityMeta te) {
		GUIGeneric r = new GUIGeneric((ContainerGeneric)getServerGUI(id, inv, te), 222, 166);
		r.addButton(7, 39, 26, 20, 0, "+1", false);
		r.addButton(33, 39, 26, 20, 1, "+10", false);
		r.addButton(59, 39, 26, 20, 2, "+100", false);
		r.addButton(85, 39, 26, 20, 3, "+1k", false);
		r.addButton(111, 39, 26, 20, 4, "+10k", false);
		r.addButton(137, 39, 26, 20, 5, "+100k", false);
		r.addButton(163, 39, 26, 20, 6, "+1M", false);
		r.addButton(189, 39, 26, 20, 7, "+10M", false);

		r.addButton(7, 59, 26, 20, 8, "-1", false);
		r.addButton(33, 59, 26, 20, 9, "-10", false);
		r.addButton(59, 59, 26, 20, 10, "-100", false);
		r.addButton(85, 59, 26, 20, 11, "-1k", false);
		r.addButton(111, 59, 26, 20, 12, "-10k", false);
		r.addButton(137, 59, 26, 20, 13, "-100k", false);
		r.addButton(163, 59, 26, 20, 14, "-1M", false);
		r.addButton(189, 59, 26, 20, 15, "-10M", false);

		r.addText(80, 16, 0);
		r.addInv(31, 84);
		return r;
	}

	@Override
	public int[] getGuiData() {
		return new int[] {out};
	}

	@Override
	public String guiDataToStr(int id, int dat) {
		return dat + " EU";
	}

	@Override
	public void handleBtn(int id, EntityPlayer pl) {
		if (id >= 0 && id <= 15) {
			long m = id % 8;
			m = (long)Math.pow(10, m);
			if (id >= 8) m *= -1;
			out = Math.max(0, (int)Math.min(Integer.MAX_VALUE, (out + m)));
		}
	}

	@Override
	public float getHardness() {
		return -1.0F;
	}
	
	@Override
	public float getResistance(Entity ee, Explosion ex) {
		return 10000000.0F;
	}
	
	@Override
	public boolean canEntityDestroy(Entity e) {
		return false;
	}
	
	@Override
	public boolean onBlockExploded(Explosion ex) {
		return false;
	}

	@Override
	public boolean isUsable(EntityPlayer pl) {
		return pl.isCreative();
	}

	@Override
	public boolean wrenchCanRemove(EntityPlayer pl) {
		return isUsable(pl);
	}
}

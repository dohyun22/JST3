package dohyun22.jst3.tiles.energy;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.client.gui.GUIGeneric;
import dohyun22.jst3.container.BatterySlot;
import dohyun22.jst3.container.ContainerGeneric;
import dohyun22.jst3.container.JSTSlot;
import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.tiles.interfaces.IGenericGUIMTE;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MT_BioGen extends MT_Generator implements IGenericGUIMTE {
	private double fuelLeft;
	private int fuelValue;

	public MT_BioGen(int t) {
		super(t, true);
	}

	@Override
	public MetaTileBase newMetaEntity(TileEntityMeta tem) {
		return new MT_BioGen(tier);
	}

	@Override
	protected void checkCanGenerate() {
		baseTile.setActive((long)fuelLeft > 0);
	}

	@Override
	protected void doGenerate() {
		double use = Math.min(fuelLeft, JSTUtils.getVoltFromTier(tier));
		baseTile.energy += use;
		fuelLeft -= use;
	}

	@Override
	public int getInvSize() {
		return 3;
	}

	@Override
	public int maxEUTransfer() {
		return JSTUtils.getVoltFromTier(tier);
	}

	public void onPreTick() {
		if (isClient()) return;
		ItemStack in = inv.get(0);
		if ((long)fuelLeft <= 0) {
			int fv = getFuelValue(in);
			if (fv > 0) {
				fuelLeft = fv;
				fuelValue = fv;
				Item it = in.getItem();
				in.shrink(1);
				if (in.isEmpty()) {
					ItemStack st2 = it.getContainerItem(in);
					inv.set(2, st2);
				}
			}
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		fuelLeft = tag.getDouble("fuel");
		fuelValue = tag.getInteger("fuelval");
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setDouble("fuel", fuelLeft);
		tag.setInteger("fuelval", fuelValue);
	}

	@Override
	public boolean onRightclick(EntityPlayer pl, ItemStack st, EnumFacing f, float hX, float hY, float hZ) {
		if (!isClient()) pl.openGui(JustServerTweak.INSTANCE, 1, getWorld(), getPos().getX(), getPos().getY(), getPos().getZ());
		return true;
	}

	@Override
	public boolean canProvideEnergy() {
		return true;
	}

	@Override
	public boolean isEnergyOutput(EnumFacing side) {
		return true;
	}

	@Override
	public boolean canInsertItem(int sl, ItemStack st, EnumFacing dir) {
		return sl == 0 && getFuelValue(st) > 0;
	}

	@Override
	public boolean canExtractItem(int sl, ItemStack st, EnumFacing dir) {
		return false;
	}

	@Override
	public int getFuel() {
		return (int)fuelLeft;
	}

	@Override
	public int getMxFuel() {
		return fuelValue;
	}

	@Override
	public Object getServerGUI(int id, InventoryPlayer inv, TileEntityMeta te) {
		ContainerGeneric r = new ContainerGeneric(te);
		r.addSlot(new Slot(te, 0, 56, 53));
		r.addSlot(new BatterySlot(te, 1, 56, 17, true, false));
		r.addSlot(JSTSlot.out(te, 2, 120, 35));
		r.addPlayerSlots(inv);
		return r;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Object getClientGUI(int id, InventoryPlayer inv, TileEntityMeta te) {
		GUIGeneric r = new GUIGeneric((ContainerGeneric) getServerGUI(id, inv, te));
		r.addSlot(56, 53, 0);
		r.addSlot(56, 17, 2);
		r.addSlot(120, 35, 0);
		r.addPwr2(80, 34);
		r.addFuel(56, 36);
		r.addJEI(29, 34, JustServerTweak.MODID + ".foodfuel");
		return r;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getDefaultTexture() {
		TextureAtlasSprite t = getTieredTex(tier), f = getTETex("biogen");
		return new TextureAtlasSprite[] {t, t, f, f, f, f};
	}

	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getTexture() {
		TextureAtlasSprite[] r = getDefaultTexture();
		if (!baseTile.isActive()) {
			TextureAtlasSprite f = getTETex("biogen_off");
			for (int n = 2; n <= 5; n++)
				r[n] = f;
		}
		return r;
	}

	public static int getFuelValue(ItemStack in) {
		if (in != null && !in.isEmpty() && in.getItem() instanceof ItemFood) {
			ItemFood f = (ItemFood)in.getItem();
			return (int) (f.getHealAmount(in) * f.getSaturationModifier(in) * 2500.0F);
		}
		return 0;
	}
}

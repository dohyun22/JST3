package dohyun22.jst3.tiles.device;

import java.util.ArrayList;

import javax.annotation.Nullable;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.client.gui.GUIRPlanner;
import dohyun22.jst3.container.ContainerRPlanner;
import dohyun22.jst3.items.JSTItems;
import dohyun22.jst3.items.behaviours.misc.IB_Memory;
import dohyun22.jst3.loader.JSTCfg;
import dohyun22.jst3.recipes.MRecipes;
import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.tiles.energy.MetaTileFluidGen;
import dohyun22.jst3.utils.JSTSounds;
import dohyun22.jst3.utils.JSTUtils;
import ic2.api.reactor.IReactor;
import ic2.api.reactor.IReactorChamber;
import ic2.api.reactor.IReactorComponent;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fml.common.Optional.Method;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@net.minecraftforge.fml.common.Optional.Interface(iface="ic2.api.reactor.IReactor", modid="ic2")
public class MT_ReactorPlanner extends MetaTileBase implements IReactor {
	private float hem;
	public int boomPowerX10;
	public float output;
	public float maxOut;
	public int heat;
	private int prevHeat;
	public int maxHeat = 10000;
	public int timer;
	public long powerGenerated;
	private byte simspeed = 10;
	private int sItem;
	private boolean changed;
	@Nullable
	public NonNullList<ItemStack> prevData;

	@Override
	public MetaTileBase newMetaEntity(TileEntityMeta tem) {
		return new MT_ReactorPlanner();
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		boomPowerX10 = tag.getInteger("bp");
		heat = tag.getInteger("heat");
		prevHeat = tag.getInteger("phet");
		maxHeat = tag.getInteger("maxh");
		timer = tag.getInteger("timer");
		sItem = tag.getInteger("item");
		powerGenerated = tag.getLong("pwrg");
		simspeed = tag.getByte("speed");
		hem = tag.getFloat("hem");
		output = tag.getFloat("outp");
		maxOut = tag.getFloat("mout");
		NBTTagCompound tg = tag.getCompoundTag("pItems");
		if (!tg.hasNoTags()) {
			this.prevData = NonNullList.withSize(54, ItemStack.EMPTY);
			ItemStackHelper.loadAllItems(tg, prevData);
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		tag.setInteger("bp", boomPowerX10);
		tag.setInteger("heat", heat);
		tag.setInteger("phet", prevHeat);
		tag.setInteger("maxh", maxHeat);
		tag.setInteger("timer", timer);
		tag.setInteger("item", sItem);
		tag.setLong("pwrg", powerGenerated);
		tag.setByte("speed", simspeed);
		tag.setFloat("hem", hem);
		tag.setFloat("outp", output);
		tag.setFloat("mout", maxOut);
		if (this.prevData != null) {
			NBTTagCompound tg2 = new NBTTagCompound();
			ItemStackHelper.saveAllItems(tg2, this.prevData);
			tag.setTag("pItems", tg2);
		}
	}

	@Override
	public void onPostTick() {
		if (isClient() || !this.baseTile.isActive() || !JSTCfg.ic2Loaded) return;
		try {
			for (int n = 0; n < simspeed; n++) {
				timer++;
				simulate();
				if (!this.baseTile.isActive()) break;
			}
		} catch (Throwable t) {
			this.baseTile.setActive(false);
		}
	}
	
	private void simulate() {
		float prev = this.output;
		this.output = 0.0F;
		this.maxHeat = 10000;
		this.hem = 1.0F;
		for (int pass = 0; pass < 2; pass++) {
			for (int y = 0; y < 6; y++) {
				for (int x = 0; x < 9; x++) {
					ItemStack st = inv.get(1 + x + y * 9);
					if (st != null && st.getItem() instanceof IReactorComponent) {
						((IReactorComponent) st.getItem()).processChamber(st, this, x, y, pass == 0);
					}
				}
			}
		}
		this.maxOut = Math.max(prev, this.output) * 5.0F;
		powerGenerated += (long)getReactorEUEnergyOutput() * 20;
		if (changed) markDirty();
		
		if (heat >= maxHeat) {
			explode();
			return;
		} else if (getReactorEnergyOutput() <= 0) {
			this.baseTile.setActive(false);
		}
	}

	@Override
	public int getInvSize() {
		//1 ItemSlot + 54 ReactorSlot + 1 dataSlot
		return 56;
	}
	
	@Override
	public boolean isItemValidForSlot(int sl, ItemStack st) {
		return false;
	}
	
	@Override
	public boolean canInsertItem(int sl, ItemStack st, EnumFacing dir) {
		return false;
	}
	
	@Override
	public boolean canExtractItem(int sl, ItemStack st, EnumFacing dir) {
		return false;
	}
	
	@Override
	public boolean canSlotDrop(int sl) {
		return sl == 55;
	}
	
	@Override
	public ItemStack getStackInSlot(int sl) {
		if (sl == 0) {
			if (sItem > 0) {
				try {
					return MRecipes.NuclearItems.get(sItem - 1);
				} catch (Exception e) {}
			}
			return ItemStack.EMPTY;
		}
		return super.getStackInSlot(sl);
	}
	
	@Override
	public boolean onRightclick(EntityPlayer pl, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (this.isClient()) {
			return true;
		}
		pl.openGui(JustServerTweak.INSTANCE, 1, this.getWorld(), this.getPos().getX(), this.getPos().getY(), this.getPos().getZ());
		return true;
	}
	
	@Override
	public Object getServerGUI(int id, InventoryPlayer inv, TileEntityMeta te) {
		if (id == 1)
			return new ContainerRPlanner(inv, te);
		return null;
	}

	@Override
	public Object getClientGUI(int id, InventoryPlayer inv, TileEntityMeta te) {
		if (id == 1)
			return new GUIRPlanner(new ContainerRPlanner(inv, te));
		return null;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getDefaultTexture() {
		return this.getSingleTETex("radio");
	}
	
	public void toggleActive() {
		if (this.boomPowerX10 > 0) return;
		baseTile.setActive(!baseTile.isActive());
		if (prevData == null && baseTile.isActive()) {
			prevData = NonNullList.withSize(54, ItemStack.EMPTY);
			this.prevHeat = this.heat;
			for (int n = 1; n < 55; n++)
				prevData.set(n - 1, this.inv.get(n).copy());
		}
	}
	
	@SideOnly(value=Side.CLIENT)
	public String getModelKey() {
		return "jst_rplanner";
	}
	
	public void toggleItem(int mode) {
		if (mode == 0) {
			sItem = 0;
		} else if (mode == 1) {
			if (sItem >= MRecipes.NuclearItems.size() || sItem < 0)
				sItem = 0;
			else
				sItem++;
		} else if (mode == 2) {
			if (sItem > MRecipes.NuclearItems.size() || sItem <= 0)
				sItem = MRecipes.NuclearItems.size();
			else
				sItem--;
		}
	}
	
	public void setItem(int val) {
		sItem = MathHelper.clamp(val, 0, MRecipes.NuclearItems.size());
	}
	
	public void reset(boolean clear) {
		if (isClient()) return;
		baseTile.setActive(false);
		hem = 0;
		boomPowerX10 = 0;
		output = 0;
		heat = 0;
		maxHeat = 10000;
		timer = 0;
		powerGenerated = 0;
		if (clear) {
			maxOut = 0;
			prevHeat = 0;
			for (int n = 1; n < 55; n++)
				inv.set(n, ItemStack.EMPTY);
		} else if (prevData != null) {
			heat = prevHeat;
			for (int n = 1; n < 55; n++)
				inv.set(n, prevData.get(n - 1));
		}
		prevData = null;
	}
	
	public byte getSimSpeed() {
		return simspeed;
	}
	
	public void changeSpeed(int amt) {
		simspeed = (byte) MathHelper.clamp(simspeed + amt, 1, 100);
	}
	
	public void saveToDSD() {
		ItemStack s = inv.get(55);
		if (s.getItem() != JSTItems.item1 || !(JSTItems.item1.getBehaviour(s) instanceof IB_Memory)) return;
		boolean empty = true;
		NonNullList<ItemStack> l;
		if (prevData == null) {
			l = NonNullList.withSize(54, ItemStack.EMPTY);
			for (int n = 1; n < 55; n++)
				if (!inv.get(n).isEmpty()) {
					empty = false;
					l.set(n - 1, inv.get(n));
				}
		} else {
			l = prevData;
		}
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("h", prevHeat);
		ItemStackHelper.saveAllItems(tag, l);
		IB_Memory.setData(s, tag, "<T>jst.data.nuclear", prevHeat + " Heat", maxOut + " EU/t");
		markDirty();
	}
	
	public void readFromDSD() {
		ItemStack s = inv.get(55);
		if (s.getItem() != JSTItems.item1 || !(JSTItems.item1.getBehaviour(s) instanceof IB_Memory)) return;
		NBTTagCompound t = IB_Memory.getData(s);
		if (t != null && t.hasKey("h")) {
			heat = t.getInteger("h");
			reset(true);
			NonNullList<ItemStack> l = NonNullList.withSize(54, ItemStack.EMPTY);
			ItemStackHelper.loadAllItems(t, l);
			for (int n = 0; n < 54; n++)
				inv.set(n + 1, l.get(n));
			markDirty();
		}
	}

	@Override
	@Method(modid = "ic2")
	public BlockPos getPosition() {
		return getPos();
	}

	@Override
	@Method(modid = "ic2")
	public World getWorldObj() {
		return getWorld();
	}

	@Override
	@Method(modid = "ic2")
	public TileEntity getCoreTe() {
		return null;
	}

	@Override
	@Method(modid = "ic2")
	public int getHeat() {
		return heat;
	}

	@Override
	@Method(modid = "ic2")
	public void setHeat(int h) {
		heat = h;
	}

	@Override
	@Method(modid = "ic2")
	public int addHeat(int a) {
	    heat += a;
	    return heat;
	}

	@Override
	@Method(modid = "ic2")
	public int getMaxHeat() {
		return maxHeat;
	}

	@Override
	@Method(modid = "ic2")
	public void setMaxHeat(int v) {
		this.maxHeat = v;
	}

	@Override
	@Method(modid = "ic2")
	public void addEmitHeat(int h) {
	}

	@Override
	@Method(modid = "ic2")
	public float getHeatEffectModifier() {
		return hem;
	}

	@Override
	@Method(modid = "ic2")
	public void setHeatEffectModifier(float v) {
		hem = v;
	}

	@Override
	@Method(modid = "ic2")
	public float getReactorEnergyOutput() {
		return output;
	}

	@Override
	@Method(modid = "ic2")
	public double getReactorEUEnergyOutput() {
		return getReactorEnergyOutput() * 5.0F;
	}

	@Override
	@Method(modid = "ic2")
	public float addOutput(float e) {
		return this.output += e;
	}

	@Override
	@Method(modid = "ic2")
	public ItemStack getItemAt(int x, int y) {
		if (x < 0 || x >= 9 || y < 0 || y >= 6) return null;
		return this.inv.get(1 + x + y * 9);
	}

	@Override
	@Method(modid = "ic2")
	public void setItemAt(int x, int y, ItemStack st) {
		if (st == null) st = ItemStack.EMPTY;
		inv.set(1 + x + y * 9, st);
		changed = true;
	}

	@Override
	@Method(modid = "ic2")
	public void explode() {
		if (this.baseTile.setActive(false)) {
			getWorld().playSound(null, getPos(), JSTSounds.BOOM, SoundCategory.BLOCKS, 1.0F, 1.0F);
			getWorld().playSound(null, getPos(), JSTSounds.GEIGER, SoundCategory.BLOCKS, 1.0F, 1.0F);
			float boomPower = 10.0F;
			float boomMod = 1.0F;
			for (ItemStack st : inv) {
				if (!st.isEmpty() && st.getItem() instanceof IReactorComponent) {
					float f = ((IReactorComponent) st.getItem()).influenceExplosion(st, this);
					if (f > 0.0F && f < 1.0F) {
						boomMod *= f;
					} else {
						boomPower += f;
					}
				}
			}
			boomPower *= this.hem * boomMod * 10.0F;
			this.boomPowerX10 = (int)boomPower;
		}
	}

	@Override
	@Method(modid = "ic2")
	public int getTickRate() {
		return 20;
	}

	@Override
	@Method(modid = "ic2")
	public boolean produceEnergy() {
		return this.baseTile.isActive();
	}

	@Override
	@Method(modid = "ic2")
	public boolean isFluidCooled() {
		return false;
	}
}

package dohyun22.jst3.items.behaviours;

import java.math.BigInteger;
import java.util.List;

import javax.annotation.Nullable;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.client.gui.GUILaser;
import dohyun22.jst3.container.ContainerLaser;
import dohyun22.jst3.entity.EntityLaserBeam;
import dohyun22.jst3.loader.JSTCfg;
import dohyun22.jst3.utils.JSTSounds;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

public class IB_Laser extends ItemBehaviour {
	
	public IB_Laser() {
		maxEnergy = 1000000;
	}
	
	@Override
	public int getTier(ItemStack st) {
		return 3;
	}
	
	@Override
	public boolean canCharge(ItemStack st) {
		return true;
	}
	
	@Override
	public boolean canDischarge(ItemStack st) {
		return false;
	}
	
	@Override
	public long transferLimit(ItemStack st) {
		return 512;
	}
	
	@Override
	public int getItemStackLimit(ItemStack st) {
		return 1;
	}
	
	@Override
	public void getInformation(ItemStack st, World w, List<String> ls, boolean adv) {
		ls.add(I18n.format("jst.tooltip.laser"));
		addEnergyTip(st, ls);
	}
	
	@Override
	public void getSubItems(ItemStack st, List<ItemStack> sub) {
		sub.add(st.copy());
		setEnergy(st, maxEnergy);
		sub.add(st);
	}
	
	@Override
	public ActionResult<ItemStack> onRightClick(ItemStack st, World w, EntityPlayer pl, EnumHand h) {
		if (JSTUtils.isClient()) return new ActionResult(EnumActionResult.PASS, st);
		
		if (h == EnumHand.MAIN_HAND && pl.isSneaking()) {
			pl.openGui(JustServerTweak.INSTANCE, 1000, w, MathHelper.floor(pl.posX), MathHelper.floor(pl.posY), MathHelper.floor(pl.posZ));
			return new ActionResult(EnumActionResult.SUCCESS, st);
		}
		
		NBTTagCompound tag = JSTUtils.getOrCreateNBT(st);
		byte r = (byte) MathHelper.clamp(tag.getByte("range"), 0, 5);
		boolean br = tag.getBoolean("break");
		boolean sm = tag.getBoolean("smelt");
		int ec = Math.max(r * 400, 100) * (sm ? 2 : 1);
		if (!br) ec = (int)(ec * 0.75F);
		long en = getEnergy(st);
		if (ec > en)
			return new ActionResult(EnumActionResult.PASS, st);
		if (!pl.capabilities.isCreativeMode)
			setEnergy(st, en - ec);
		pl.world.playSound(null, pl.posX, pl.posY, pl.posZ, JSTSounds.LASER, SoundCategory.PLAYERS, 1.0F, 1.0F);
		shootLaser(w, pl, (r + 1) * 10, (r + 1) * 5, r == 0 ? 1 : r * 4, br ? sm ? 3 : 0 : 2, pl.rotationYaw, pl.rotationPitch, pl.posY + 1.610F);
		return new ActionResult(EnumActionResult.PASS, st);
	}

	/**
	 * @param w world
	 * @param e entity
	 * @param rn range
	 * @param pw power
	 * @param bb blockBreaks
	 * @param m mode
	 * @param yd yawDeg
	 * @param pd pitchDeg
	 * @param y y
	 * */
	public static void shootLaser (World w, EntityLivingBase e, float rn, float pw, int bb, int m, double yd, double pd, double y) {
		w.spawnEntity(new EntityLaserBeam(w, e, rn, m, yd, pd, y, pw, bb));
	}
	
	@Override
	public boolean canBeStoredInToolbox(ItemStack st) {
		return true;
	}

	@Override
	public Object getServerGUI(int id, EntityPlayer pl, World w, BlockPos p) {
		return new ContainerLaser(pl.inventory);
	}

	@Override
	public Object getClientGUI(int id, EntityPlayer pl, World w, BlockPos p) {
		return new GUILaser(new ContainerLaser(pl.inventory));
	}
}

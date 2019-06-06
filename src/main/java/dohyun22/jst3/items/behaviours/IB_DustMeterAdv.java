package dohyun22.jst3.items.behaviours;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.client.gui.GUIDust;
import dohyun22.jst3.container.ContainerDust;
import dohyun22.jst3.loader.JSTCfg;
import dohyun22.jst3.utils.JSTChunkData;
import dohyun22.jst3.utils.JSTSounds;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class IB_DustMeterAdv extends ItemBehaviour {

	public IB_DustMeterAdv() {
		maxEnergy = 150000;
	}

	@Override
	public int getTier(ItemStack st) {
		return 2;
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
		return 128L;
	}

	@Override
	public ActionResult<ItemStack> onRightClick(ItemStack st, World w, EntityPlayer pl, EnumHand h) {
		if (!JSTUtils.isClient() && getEnergy(st) > 2500) {
			if (!pl.capabilities.isCreativeMode)
				setEnergy(st, getEnergy(st) - 2500);
			w.playSound(null, pl.getPosition(), JSTSounds.SCAN, SoundCategory.PLAYERS, 1.0F, 1.0F);
			pl.openGui(JustServerTweak.INSTANCE, 1000, w, MathHelper.floor(pl.posX), MathHelper.floor(pl.posY), MathHelper.floor(pl.posZ));
			return new ActionResult(EnumActionResult.SUCCESS, st);
		}
		return new ActionResult(EnumActionResult.PASS, st);
	}

	@Override
	public void getSubItems(ItemStack st, List<ItemStack> sub) {
		sub.add(st.copy());
		setEnergy(st, this.maxEnergy);
		sub.add(st);
	}

	@Override
	@Nullable
	public Object getServerGUI(int id, EntityPlayer pl, World w, BlockPos p) {
		return new ContainerDust(pl.inventory);
	}

	@Override
	@Nullable
	public Object getClientGUI(int id, EntityPlayer pl, World w, BlockPos p) {
		return new GUIDust(new ContainerDust(pl.inventory));
	}

	@Override
	@SideOnly(Side.CLIENT)
	@Nullable
	public List<String> getInformation(ItemStack st, World w, ITooltipFlag adv) {
		long e = getEnergy(st);
		List<String> ret = new ArrayList();
		ret.add(I18n.format("jst.tooltip.energy.eu", e, maxEnergy));
		ret.add(I18n.format("jst.tooltip.energy.rf", e * JSTCfg.RFPerEU, maxEnergy * JSTCfg.RFPerEU));
		return ret;
	}
}

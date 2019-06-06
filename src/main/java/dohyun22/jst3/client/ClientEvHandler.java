package dohyun22.jst3.client;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.client.model.ModelMTE;
import dohyun22.jst3.compat.tic.CompatTiC;
import dohyun22.jst3.evhandler.EvHandler;
import dohyun22.jst3.items.JSTItems;
import dohyun22.jst3.items.behaviours.ItemBehaviour;
import dohyun22.jst3.loader.JSTCfg;
import dohyun22.jst3.loader.MTELoader;
import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.utils.JSTFluids;
import dohyun22.jst3.utils.JSTPotions;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.IRegistry;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientEvHandler implements IResourceManagerReloadListener {
	
	@Override
	public void onResourceManagerReload(IResourceManager rm) {
		ModelMTE.clearCache();
	}
	
	@SubscribeEvent
	public void textureStich(TextureStitchEvent.Pre ev){
		TextureLoader.initTex(ev.getMap());
	}
	
    @SubscribeEvent
    public void onModelBake(ModelBakeEvent ev) {
        IRegistry<ModelResourceLocation, IBakedModel> reg = ev.getModelRegistry();
        String pf = JustServerTweak.MODID + ":";
        registerModel(reg, pf + "BlockTE#normal", ModelMTE.INSTANCE);
        registerModel(reg, pf + "BlockTE#inventory", ModelMTE.INSTANCE);
    }
    
	@SubscribeEvent
	public void onToolTip(ItemTooltipEvent ev) {
		try {
			ItemStack st = ev.getItemStack();
			if (st.isEmpty()) return;
			String name = Item.REGISTRY.getNameForObject(st.getItem()).toString();
			List<String> tl = ev.getToolTip();
			if (JSTCfg.RIC2C && "ic2:cable".equals(name)) {
				String stn = tl.isEmpty() ? null : tl.get(0);
				tl.clear();
				if (stn != null) tl.add(stn);
				int id = EvHandler.getJSTMeta((byte) st.getMetadata(), st.hasTagCompound() ? st.getTagCompound().getByte("insulation") > 0 : false);
				MetaTileBase mtb = MetaTileBase.getTE(id);
				if (mtb == null) return;
				List<String> str = new ArrayList();
				mtb.getInformation(st, ev.getEntityPlayer().world, str, ev.getFlags());
				for (String s : str) if (s != null) tl.add(s);
			} else if (st.hasTagCompound() && CompatTiC.isTiCTool(st)) {
				NBTTagCompound tag = st.getTagCompound();
				if (tag.hasKey("JST_EU")) {
					long e = tag.getLong("JST_EU"), m = tag.getLong("JST_EU_MAX");
					tl.add("\u00A7a" + e + " / " + m + " EU");
					tl.add("\u00A7a" + (e * JSTCfg.RFPerEU) + " / " + (m * JSTCfg.RFPerEU) + " RF");
					int tier = tag.getInteger("JST_EU_LVL");
					tl.add("\u00A7a" + I18n.format("jst.tooltip.energy.tier") + " " + tier + " (" + JSTUtils.getTierName(tier) + ")");
				}
			}
		} catch (Throwable e) {}
	}

	@SubscribeEvent(priority=EventPriority.LOWEST)
	public void modFogColor(EntityViewRenderEvent.FogColors ev) {
		Entity e = ev.getEntity();
		if (e instanceof EntityLivingBase) {
			PotionEffect ef = ((EntityLivingBase)e).getActivePotionEffect(JSTPotions.finedust);
			if (ef != null) {
				int a = ef.getAmplifier();
				float r = Math.min(a * 0.15F + 0.5F, 1.0F);
				float r2 = 1.0F - r;
				int eR = (int)(ev.getRed() * 255.0F), eG = (int)(ev.getGreen() * 255.0F), eB = (int)(ev.getBlue() * 255.0F);
				if (a > 3) {
					eR = Math.max(0, eR - (a - 3) * 10);
					eG = Math.max(0, eG - (a - 3) * 10);
					eB = Math.max(0, eB - (a - 3) * 10);
				}
				ev.setRed(MathHelper.clamp((eR * r2 + 96 * r) / 255.0F, 0.0F, 1.0F));
				ev.setGreen(MathHelper.clamp((eG * r2 + 80 * r) / 255.0F, 0.0F, 1.0F));
				ev.setBlue(MathHelper.clamp((eB * r2 + 40 * r) / 255.0F, 0.0F, 1.0F));
			}
		}
	}

	/*@SubscribeEvent
	public void onBlockHighlight(DrawBlockHighlightEvent ev) {
		ItemStack st = ev.getPlayer().getHeldItem(EnumHand.MAIN_HAND);
		if (st != null && st.getItem() == JSTItems.item1) {
			ItemBehaviour ib = JSTItems.item1.getBehaviour(st);
			if (ib != null && ib.isWrench()) {
				
			}
		}
	}*/
    
    private static void registerModel(final IRegistry<ModelResourceLocation, IBakedModel> modelRegistry, String reg, IBakedModel val) {
        modelRegistry.putObject(new ModelResourceLocation(reg), val);
    }
}

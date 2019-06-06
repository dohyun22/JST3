package dohyun22.jst3.proxy;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;

import java.util.Map;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.client.ClientEvHandler;
import dohyun22.jst3.client.entity.LayerItem;
import dohyun22.jst3.client.entity.RenderCar;
import dohyun22.jst3.client.entity.RenderLaserBeam;
import dohyun22.jst3.client.entity.RenderPoweredCart;
import dohyun22.jst3.client.entity.RenderPrimedOre;
import dohyun22.jst3.entity.EntityCar;
import dohyun22.jst3.entity.EntityLaserBeam;
import dohyun22.jst3.entity.EntityPoweredCart;
import dohyun22.jst3.entity.EntityPrimedOre;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {
	
	@Override
	public void preinit() {
		ClientEvHandler h = new ClientEvHandler();
		MinecraftForge.EVENT_BUS.register(h);
		((IReloadableResourceManager)Minecraft.getMinecraft().getResourceManager()).registerReloadListener(h);
		RenderingRegistry.registerEntityRenderingHandler(EntityPrimedOre.class, RenderPrimedOre::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityLaserBeam.class, RenderLaserBeam::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityPoweredCart.class, RenderPoweredCart::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityCar.class, RenderCar::new);
	}
	
	@Override
	public void init() {
		LayerItem layer = new LayerItem();
		if (Loader.isModLoaded("mobends")) {
			try {
		    	Object obj = Class.forName("net.gobbob.mobends.animatedentity.AnimatedEntity").getField("skinMap").get(null);
		    	if (obj instanceof Map) {
		    		Map map = ((Map)obj);
		    		if (!map.isEmpty() && map.containsKey("default") && map.containsKey("slim")) {
		    			((RenderPlayer)map.get("default")).addLayer(layer);
		    			((RenderPlayer)map.get("slim")).addLayer(layer);
		    		}
		    	}
			} catch (Throwable t) {
				System.err.println("Failed to add MoBends Compat.");
				t.printStackTrace();
			}
		}
		try {
		    Map<String, RenderPlayer> map = Minecraft.getMinecraft().getRenderManager().getSkinMap();
			map.get("default").addLayer(layer);
			map.get("slim").addLayer(layer);
		} catch (Throwable t) {}
	}
}

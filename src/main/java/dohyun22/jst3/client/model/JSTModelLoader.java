package dohyun22.jst3.client.model;

import java.io.IOException;
import java.util.HashMap;

import dohyun22.jst3.JustServerTweak;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class JSTModelLoader implements ICustomModelLoader {
	public void register(String path, IModel model) {
		register(new ResourceLocation(JustServerTweak.MODID, path), model);
	}

	public void register(ResourceLocation location, IModel model) {
		models.put(location, model);
	}

	@Override
	public void onResourceManagerReload(IResourceManager resourceManager) {
		ModelMTE.clearCache();
	}

	@Override
	public boolean accepts(ResourceLocation modelLocation) {
		return models.containsKey(modelLocation);
	}

	@Override
	public IModel loadModel(ResourceLocation modelLocation) throws IOException {
		return (IModel) models.get(modelLocation);
	}

	private static final HashMap<ResourceLocation, IModel> models = new HashMap();
}
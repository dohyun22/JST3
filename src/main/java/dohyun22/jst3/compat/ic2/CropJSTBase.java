package dohyun22.jst3.compat.ic2;

import java.util.ArrayList;
import java.util.List;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.utils.JSTUtils;
import ic2.api.crops.CropCard;
import net.minecraft.util.ResourceLocation;

public abstract class CropJSTBase extends CropCard {
	public static List<ResourceLocation> tex;
	
	protected void loadTex() {
		if (JSTUtils.isClient()) {
			List<ResourceLocation> rl = getTexturesLocation();
			if (rl != null && !rl.isEmpty())
				CropJSTBase.tex.addAll(rl);
		}
	}
	
	@Override
	public String getOwner() {
		return JustServerTweak.MODID;
	}
	
	@Override
	public String getUnlocalizedName() {
		return "jst.compat.crop." + getId();
	}
	
	@Override
	public String getDiscoveredBy() {
		return "JustServer Team";
	}

	@Override
	public List<ResourceLocation> getTexturesLocation() {
		List<ResourceLocation> ret = new ArrayList(getMaxSize());
		for (int size = 1; size <= getMaxSize(); size++)
			ret.add(new ResourceLocation(JustServerTweak.MODID, "blocks/crop/" + getId() + size));
		return ret;
	}
}

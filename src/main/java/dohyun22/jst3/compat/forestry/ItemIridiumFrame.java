package dohyun22.jst3.compat.forestry;

import java.util.List;

import dohyun22.jst3.JustServerTweak;
import forestry.api.apiculture.DefaultBeeModifier;
import forestry.api.apiculture.IBee;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeeModifier;
import forestry.api.apiculture.IHiveFrame;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemIridiumFrame extends Item implements IHiveFrame {
	
	public ItemIridiumFrame() {
		setRegistryName(JustServerTweak.MODID, "IridiumFrame");
		setUnlocalizedName("iridiumframe");
		setCreativeTab(JustServerTweak.JSTTab);
	}

	@Override
	public ItemStack frameUsed(IBeeHousing housing, ItemStack frame, IBee queen, int wear) {
		return frame;
	}

	@Override
	public IBeeModifier getBeeModifier() {
		return CustomBeeModifier.INSTANCE;
	}
	
	@Override
    public EnumRarity getRarity(ItemStack st) {
        return EnumRarity.RARE;
    }
	
    @Override
    public int getItemStackLimit(ItemStack st) {
    	return 1;
    }

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack st, World w, List<String> ls, ITooltipFlag adv) {
		super.addInformation(st, w, ls, adv);
		ls.add(I18n.format("jst.compat.for.iridiumframe.desc"));
		ls.add(I18n.format("item.for.bee.modifier.production", CustomBeeModifier.production));
		ls.add(I18n.format("item.for.bee.modifier.genetic.decay", CustomBeeModifier.geneticDecay));
	}
	
	private static class CustomBeeModifier extends DefaultBeeModifier {
		private static final CustomBeeModifier INSTANCE = new CustomBeeModifier();
		private static final float production = 2.0F;
		private static final float geneticDecay = 0.3F;

		@Override
		public float getProductionModifier(IBeeGenome gen, float cMod) {
			return production;
		}

		@Override
		public float getGeneticDecay(IBeeGenome gen, float cMod) {
			return geneticDecay;
		}
	}
}

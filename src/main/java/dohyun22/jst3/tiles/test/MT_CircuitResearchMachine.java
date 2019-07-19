package dohyun22.jst3.tiles.test;

import java.util.Random;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.api.recipe.RecipeList;
import dohyun22.jst3.client.gui.GUIAssembler;
import dohyun22.jst3.client.gui.GUICircuitResearch;
import dohyun22.jst3.client.gui.GUIGeneric;
import dohyun22.jst3.container.ContainerCircuitResearch;
import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.tiles.machine.MT_MachineGeneric;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MT_CircuitResearchMachine extends MT_MachineGeneric {
	public final int row = 12;
	public final int column = 9;
	public byte[] listOfGame = new byte[row * column];

	public MT_CircuitResearchMachine(int tier) {
		// 4���� ��ǲ�� ������� ȸ�α���,����,�����ݼ�,�εα��̴�
		// 1���� �ƿ�ǲ�� ���� û�����̴�
		// 9*6(tier 0~2)->12*9(tier 3)
		super(tier, 4, 1, 0, 0, 0, null, false, false);
	}

	@Override
	public boolean isItemValidForSlot(int sl, ItemStack st) {
		return super.isItemValidForSlot(sl, st) && !inv.get(sl).isEmpty();
	}

	@Override
	public void onPostTick() {
		super.onPostTick();
		if (isClient())
			return;

		/*
		 * ItemStack stEnd = inv.get(inputNum - 1); if(!stEnd.isEmpty()) return;
		 * 
		 * for (int n = 0; n < inputNum - 1; n++) { ItemStack st = inv.get(n); if
		 * (st.isEmpty()) return; switch(n) { case 0: break;
		 * 
		 * default: break; } }
		 */

		Random r = new Random();
		for (int i = 0; i < listOfGame.length; i++) {
			byte value = (byte) r.nextInt(3);
			listOfGame[i] = (byte) value;
		}
	}

	@Override
	public MetaTileBase newMetaEntity(TileEntityMeta tem) {
		return new MT_CircuitResearchMachine(tier);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite[] getDefaultTexture() {
		TextureAtlasSprite t = getTieredTex(tier);
		return new TextureAtlasSprite[] { t, t, t, t, t, getTETex("circuit_research") };
	}

	@Override
	public boolean onRightclick(EntityPlayer pl, ItemStack heldItem, EnumFacing side, float hitX, float hitY,
			float hitZ) {
		if (this.baseTile == null || getWorld().isRemote)
			return true;
		pl.openGui(JustServerTweak.INSTANCE, 1, this.getWorld(), this.getPos().getX(), this.getPos().getY(),
				this.getPos().getZ());
		return true;
	}

	@Override
	public Object getClientGUI(int id, InventoryPlayer inv, TileEntityMeta te) {
		return new GUICircuitResearch(new ContainerCircuitResearch(inv, te));
	}

	@Override
	public Object getServerGUI(int id, InventoryPlayer inv, TileEntityMeta te) {
		if (id == 1)
			return new ContainerCircuitResearch(inv, te);
		return null;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		listOfGame = tag.getByteArray("ListOfMiniGame");
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setByteArray("ListOfMiniGame", listOfGame);
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void addComp(GUIGeneric gg) {
		gg.addSlot(185, 25, 0);
		gg.addSlot(185, 44, 0);
		gg.addSlot(185, 62, 0);
		gg.addSlot(185, 80, 0);
		gg.addSlot(185, 98, 0);
	}

	protected void saveGameToBlueprint(ItemStack bp) {
	}
}

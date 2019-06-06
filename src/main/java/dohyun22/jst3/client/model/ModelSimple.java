package dohyun22.jst3.client.model;

import com.google.common.collect.ImmutableList;
import java.util.List;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ItemTransformVec3f;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import org.lwjgl.util.vector.Vector3f;

public class ModelSimple implements IBakedModel {
	public static final ItemCameraTransforms TRANSFORM_DEFAULT = ItemCameraTransforms.DEFAULT;
	public static final ItemCameraTransforms TRANSFORM_BLOCK;
	public static final ItemCameraTransforms TRANSFORM_PLUG_AS_ITEM;
	public static final ItemCameraTransforms TRANSFORM_PLUG_AS_ITEM_BIGGER;
	public static final ItemCameraTransforms TRANSFORM_PLUG_AS_BLOCK;
	private final List<BakedQuad> quads;
	private final TextureAtlasSprite particle;
	private final ItemCameraTransforms transforms;

	static {
		ItemTransformVec3f thirdperson_left = def(75.0D, 45.0D, 0.0D, 0.0D, 2.5D, 0.0D, 0.375D);
		ItemTransformVec3f thirdperson_right = def(75.0D, 225.0D, 0.0D, 0.0D, 2.5D, 0.0D, 0.375D);
		ItemTransformVec3f firstperson_left = def(0.0D, 45.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.4D);
		ItemTransformVec3f firstperson_right = def(0.0D, 225.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.4D);
		ItemTransformVec3f head = def(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F);
		ItemTransformVec3f gui = def(30.0D, 225.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.625D);
		ItemTransformVec3f ground = def(0.0D, 0.0D, 0.0D, 0.0D, 3.0D, 0.0D, 0.25D);
		ItemTransformVec3f fixed = def(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.5D);
		TRANSFORM_BLOCK = new ItemCameraTransforms(thirdperson_left, thirdperson_right, firstperson_left,
				firstperson_right, head, gui, ground, fixed);

		ItemTransformVec3f item_head = def(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F);
		ItemTransformVec3f item_gui = def(0.0F, 90.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F);
		ItemTransformVec3f item_ground = def(0.0D, 0.0D, 0.0D, 0.0D, 3.0D, 0.0D, 0.5D);
		ItemTransformVec3f item_fixed = def(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.85D);
		TRANSFORM_PLUG_AS_ITEM = new ItemCameraTransforms(thirdperson_left, thirdperson_right, firstperson_left,
				firstperson_right, item_head, item_gui, item_ground, item_fixed);
		TRANSFORM_PLUG_AS_ITEM_BIGGER = scale(TRANSFORM_PLUG_AS_ITEM, 1.8D);

		thirdperson_left = def(75.0D, 45.0D, 0.0D, 0.0D, 2.5D, 0.0D, 0.375D);
		thirdperson_right = def(75.0D, 225.0D, 0.0D, 0.0D, 2.5D, 0.0D, 0.375D);
		firstperson_left = def(0.0D, 45.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.4D);
		firstperson_right = def(0.0D, 225.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.4D);
		gui = def(30.0D, 135.0D, 0.0D, -3.0D, 1.5D, 0.0D, 0.625D);
		TRANSFORM_PLUG_AS_BLOCK = new ItemCameraTransforms(thirdperson_left, thirdperson_right, firstperson_left,
				firstperson_right, head, gui, ground, fixed);
	}

	private static ItemCameraTransforms scale(ItemCameraTransforms from, double by) {
		ItemTransformVec3f thirdperson_left = scale(from.thirdperson_left, by);
		ItemTransformVec3f thirdperson_right = scale(from.thirdperson_right, by);
		ItemTransformVec3f firstperson_left = scale(from.firstperson_left, by);
		ItemTransformVec3f firstperson_right = scale(from.firstperson_right, by);
		ItemTransformVec3f head = scale(from.head, by);
		ItemTransformVec3f gui = scale(from.gui, by);
		ItemTransformVec3f ground = scale(from.ground, by);
		ItemTransformVec3f fixed = scale(from.fixed, by);
		return new ItemCameraTransforms(thirdperson_left, thirdperson_right, firstperson_left, firstperson_right, head,
				gui, ground, fixed);
	}

	private static ItemTransformVec3f scale(ItemTransformVec3f from, double by) {
		float scale = (float) by;
		Vector3f nScale = new Vector3f(from.scale);
		nScale.scale(scale);

		return new ItemTransformVec3f(from.rotation, from.translation, nScale);
	}

	private static ItemTransformVec3f translate(ItemTransformVec3f from, double dx, double dy, double dz) {
		Vector3f nTranslation = new Vector3f(from.translation);
		nTranslation.translate((float) dx, (float) dy, (float) dz);
		return new ItemTransformVec3f(from.rotation, nTranslation, from.scale);
	}

	private static ItemTransformVec3f def(double rx, double ry, double rz, double tx, double ty, double tz,
			double scale) {
		return def((float) rx, (float) ry, (float) rz, (float) tx, (float) ty, (float) tz, (float) scale);
	}

	private static ItemTransformVec3f def(float rx, float ry, float rz, float tx, float ty, float tz, float scale) {
		return new ItemTransformVec3f(new Vector3f(rx, ry, rz), new Vector3f(tx / 16.0F, ty / 16.0F, tz / 16.0F),
				new Vector3f(scale, scale, scale));
	}

	public ModelSimple(List<BakedQuad> quads, ItemCameraTransforms transforms) {
		this.quads = quads;
		if (quads.isEmpty()) {
			this.particle = null;
		} else {
			this.particle = ((BakedQuad) quads.get(0)).getSprite();
		}
		this.transforms = transforms;
	}

	public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
		if (side == null)
			return this.quads;
		return ImmutableList.of();
	}

	public boolean isAmbientOcclusion() {
		return false;
	}

	public boolean isGui3d() {
		return false;
	}

	public boolean isBuiltInRenderer() {
		return false;
	}

	public TextureAtlasSprite getParticleTexture() {
		return this.particle;
	}

	public ItemCameraTransforms getItemCameraTransforms() {
		return this.transforms;
	}

	public ItemOverrideList getOverrides() {
		return ItemOverrideList.NONE;
	}
}

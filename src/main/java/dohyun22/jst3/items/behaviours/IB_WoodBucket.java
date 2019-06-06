package dohyun22.jst3.items.behaviours;

public class IB_WoodBucket extends ItemBehaviour {
	public static final int sID = 9900;
	
	public static enum WoodBucketType {
		NONE(null, 0), WATR("water", 1), OIL("oil", 2), FUEL("fuel", 3);
		
		public final String fname;
		public final int id;
		
		private WoodBucketType(String fluid, int id) {
			this.fname = fluid;
			this.id = id;
		}
	}
}

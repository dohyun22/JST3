package dohyun22.jst3.utils;

import net.minecraftforge.common.property.IUnlistedProperty;

public class UnlistedProperty<V> implements IUnlistedProperty<V> {
	public final String name;

	public UnlistedProperty(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public boolean isValid(V value) {
		return value != null;
	}

	public Class getType() {
		return Object.class;
	}

	public String valueToString(V value) {
		return value.toString();
	}
}

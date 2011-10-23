package org.diyefi.openlogviewer.decoder;

public class LogField {
	public enum types {
		UINT8  (1),
		UINT16 (2),
		UINT32 (4),
		BITS8  (1),
		BITS16 (2),
		BITS32 (4),
		SINT8  (1),
		SINT16 (2),
		SINT32 (4);

		public final int width;

		private types(int width) {
			this.width = width;
		}
	}

	static final double divideByOne = 1.0;
	static final double addToZero = 0.0;

	String ID;
	types type;
	String[] bitFieldNames;

	// Optional
	String description;
	String unit;

	// This does not match MTX:
	double divBy = divideByOne; // divide first (mtx multiplies, and the result is uglier config IMO
	double addTo = addToZero;   // add second

	public LogField(String ID) {
		this(ID, types.UINT16, divideByOne, addToZero);
	}
	public LogField(String ID, types type) {
		this(ID, type, divideByOne, addToZero);
	}
	public LogField(String ID, types type, double divBy) {
		this(ID, type , divBy, addToZero);
	}

	public LogField(String ID, double divBy) {
		this(ID, types.UINT16, divBy, addToZero);
	}
	public LogField(String ID, double divBy, double addTo) {
		this(ID, types.UINT16, divBy, addTo);
	}

	public LogField(String ID, types type, double divBy, double addTo) {
		this.ID = ID;
		this.type = type;
		this.divBy = divBy;
		this.addTo = addTo;

		if (ID == null) {
			throw new RuntimeException("ID is null, needs to be a valid string!");
		} else if (type == null) {
			throw new RuntimeException("Type must be specified!");
		} else if ((this.type == types.BITS8) || (this.type == types.BITS16) || (this.type == types.BITS32)) {
			throw new RuntimeException("This constructor must NOT be used with flag variables");
		} else if (this.divBy == addToZero) {
			throw new RuntimeException("Divide by zero not possible, don't be silly!");
		}
	}

	public LogField(String ID, types type, String[] bitFieldNames) {
		this.ID = ID;
		this.type = type;
		this.bitFieldNames = bitFieldNames;

		if (ID == null) {
			throw new RuntimeException("ID is null, needs to be a valid string!");
		} else if (this.type != types.BITS8 && this.type != types.BITS16 && this.type != types.BITS32) {
			throw new RuntimeException("This constructor can only be used with flag variables!");
		}

		if (this.bitFieldNames == null) {
			throw new IllegalArgumentException("bitFieldNames is null!");
		} else if ((this.type == types.BITS8) && (bitFieldNames.length != 8)) {
			throw new IllegalArgumentException("BITS8 requires 8 flag names!");
		} else if ((this.type == types.BITS16) && (bitFieldNames.length != 16)) {
			throw new IllegalArgumentException("BITS16 requires 16 flag names!");
		} else if ((this.type == types.BITS32) && (bitFieldNames.length != 32)) {
			throw new IllegalArgumentException("BITS16 requires 32 flag names!");
		}
	}

	public final String getID() {
		return ID;
	}
	public final types getType() {
		return type;
	}
	public final String[] getBitFieldNames() {
		return bitFieldNames;
	}
	public final double getDivBy() {
		return divBy;
	}
	public final double getAddTo() {
		return addTo;
	}
	public final void setID(final String iD) {
		ID = iD;
	}
	public final void setType(final types type) {
		this.type = type;
	}
	public final void setBitFieldNames(final String[] bitFieldNames) {
		this.bitFieldNames = bitFieldNames;
	}
	public final void setDivBy(final double divBy) {
		this.divBy = divBy;
	}
	public final void setAddTo(final double addTo) {
		this.addTo = addTo;
	}
	public final String getDescription() {
		return description;
	}
	public final String getUnit() {
		return unit;
	}
	public final void setDescription(String description) {
		this.description = description;
	}
	public final void setUnit(final String unit) {
		this.unit = unit;
	}
}

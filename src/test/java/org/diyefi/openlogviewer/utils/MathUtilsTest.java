package org.diyefi.openlogviewer.utils;

import java.util.Locale;

import junit.framework.TestCase;

public class MathUtilsTest extends TestCase {

	public MathUtilsTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		Locale.setDefault(Locale.US); // Ensure tests pass in Europe! :-)
	}

	public void testRoundDecimalPlaces(){
		int decimalPlaces = -1;
		assertEquals("0", MathUtils.roundDecimalPlaces(0D, decimalPlaces));
		assertEquals("0", MathUtils.roundDecimalPlaces(1D/3D, decimalPlaces));
		assertEquals("1", MathUtils.roundDecimalPlaces(0.5D, decimalPlaces));
		assertEquals("1", MathUtils.roundDecimalPlaces(2D/3D, decimalPlaces));
		assertEquals("1", MathUtils.roundDecimalPlaces(1.3333333D, decimalPlaces));
		assertEquals("2", MathUtils.roundDecimalPlaces(1.5D, decimalPlaces));
		assertEquals("2", MathUtils.roundDecimalPlaces(1.6666666D, decimalPlaces));
		assertEquals("22", MathUtils.roundDecimalPlaces(22.3333333D, decimalPlaces));
		assertEquals("23", MathUtils.roundDecimalPlaces(22.6666666D, decimalPlaces));
		assertEquals("1", MathUtils.roundDecimalPlaces(0.99D, decimalPlaces));
		assertEquals("1", MathUtils.roundDecimalPlaces(0.999D, decimalPlaces));
		assertEquals("1", MathUtils.roundDecimalPlaces(0.9999D, decimalPlaces));
		assertEquals("12345678912345678", MathUtils.roundDecimalPlaces(12345678912345678D, decimalPlaces));
		assertEquals("0", MathUtils.roundDecimalPlaces(0.0001234D, decimalPlaces));
		assertEquals("0", MathUtils.roundDecimalPlaces(0.0006789D, decimalPlaces));

		assertEquals("0", MathUtils.roundDecimalPlaces(-0D, decimalPlaces));
		assertEquals("0", MathUtils.roundDecimalPlaces(-1D/3D, decimalPlaces));
		assertEquals("0", MathUtils.roundDecimalPlaces(-0.5D, decimalPlaces));
		assertEquals("-1", MathUtils.roundDecimalPlaces(-2D/3D, decimalPlaces));
		assertEquals("-1", MathUtils.roundDecimalPlaces(-1.3333333D, decimalPlaces));
		assertEquals("-1", MathUtils.roundDecimalPlaces(-1.5D, decimalPlaces));
		assertEquals("-2", MathUtils.roundDecimalPlaces(-1.6666666D, decimalPlaces));
		assertEquals("-22", MathUtils.roundDecimalPlaces(-22.3333333D, decimalPlaces));
		assertEquals("-23", MathUtils.roundDecimalPlaces(-22.6666666D, decimalPlaces));
		assertEquals("-1", MathUtils.roundDecimalPlaces(-0.99D, decimalPlaces));
		assertEquals("-1", MathUtils.roundDecimalPlaces(-0.999D, decimalPlaces));
		assertEquals("-1", MathUtils.roundDecimalPlaces(-0.9999D, decimalPlaces));
		assertEquals("-12345678912345678", MathUtils.roundDecimalPlaces(-12345678912345678D, decimalPlaces));
		assertEquals("0", MathUtils.roundDecimalPlaces(-0.0001234D, decimalPlaces));
		assertEquals("0", MathUtils.roundDecimalPlaces(-0.0006789D, decimalPlaces));

		decimalPlaces = 0;
		assertEquals("0", MathUtils.roundDecimalPlaces(0D, decimalPlaces));
		assertEquals("0", MathUtils.roundDecimalPlaces(1D/3D, decimalPlaces));
		assertEquals("1", MathUtils.roundDecimalPlaces(0.5D, decimalPlaces));
		assertEquals("1", MathUtils.roundDecimalPlaces(2D/3D, decimalPlaces));
		assertEquals("1", MathUtils.roundDecimalPlaces(1.3333333D, decimalPlaces));
		assertEquals("2", MathUtils.roundDecimalPlaces(1.5D, decimalPlaces));
		assertEquals("2", MathUtils.roundDecimalPlaces(1.6666666D, decimalPlaces));
		assertEquals("22", MathUtils.roundDecimalPlaces(22.3333333D, decimalPlaces));
		assertEquals("23", MathUtils.roundDecimalPlaces(22.6666666D, decimalPlaces));
		assertEquals("1", MathUtils.roundDecimalPlaces(0.99D, decimalPlaces));
		assertEquals("1", MathUtils.roundDecimalPlaces(0.999D, decimalPlaces));
		assertEquals("1", MathUtils.roundDecimalPlaces(0.9999D, decimalPlaces));
		assertEquals("12345678912345678", MathUtils.roundDecimalPlaces(12345678912345678D, decimalPlaces));
		assertEquals("0", MathUtils.roundDecimalPlaces(0.0001234D, decimalPlaces));
		assertEquals("0", MathUtils.roundDecimalPlaces(0.0006789D, decimalPlaces));

		assertEquals("0", MathUtils.roundDecimalPlaces(-0D, decimalPlaces));
		assertEquals("0", MathUtils.roundDecimalPlaces(-1D/3D, decimalPlaces));
		assertEquals("0", MathUtils.roundDecimalPlaces(-0.5D, decimalPlaces));
		assertEquals("-1", MathUtils.roundDecimalPlaces(-2D/3D, decimalPlaces));
		assertEquals("-1", MathUtils.roundDecimalPlaces(-1.3333333D, decimalPlaces));
		assertEquals("-1", MathUtils.roundDecimalPlaces(-1.5D, decimalPlaces));
		assertEquals("-2", MathUtils.roundDecimalPlaces(-1.6666666D, decimalPlaces));
		assertEquals("-22", MathUtils.roundDecimalPlaces(-22.3333333D, decimalPlaces));
		assertEquals("-23", MathUtils.roundDecimalPlaces(-22.6666666D, decimalPlaces));
		assertEquals("-1", MathUtils.roundDecimalPlaces(-0.99D, decimalPlaces));
		assertEquals("-1", MathUtils.roundDecimalPlaces(-0.999D, decimalPlaces));
		assertEquals("-1", MathUtils.roundDecimalPlaces(-0.9999D, decimalPlaces));
		assertEquals("-12345678912345678", MathUtils.roundDecimalPlaces(-12345678912345678D, decimalPlaces));
		assertEquals("0", MathUtils.roundDecimalPlaces(-0.0001234D, decimalPlaces));
		assertEquals("0", MathUtils.roundDecimalPlaces(-0.0006789D, decimalPlaces));

		decimalPlaces = 1;
		assertEquals("0.0", MathUtils.roundDecimalPlaces(0D, decimalPlaces));
		assertEquals("0.3", MathUtils.roundDecimalPlaces(1D/3D, decimalPlaces));
		assertEquals("0.5", MathUtils.roundDecimalPlaces(0.5D, decimalPlaces));
		assertEquals("0.7", MathUtils.roundDecimalPlaces(2D/3D, decimalPlaces));
		assertEquals("1.3", MathUtils.roundDecimalPlaces(1.3333333D, decimalPlaces));
		assertEquals("1.5", MathUtils.roundDecimalPlaces(1.5D, decimalPlaces));
		assertEquals("1.7", MathUtils.roundDecimalPlaces(1.6666666D, decimalPlaces));
		assertEquals("22.3", MathUtils.roundDecimalPlaces(22.3333333D, decimalPlaces));
		assertEquals("22.7", MathUtils.roundDecimalPlaces(22.6666666D, decimalPlaces));
		assertEquals("1.0", MathUtils.roundDecimalPlaces(0.99D, decimalPlaces));
		assertEquals("1.0", MathUtils.roundDecimalPlaces(0.999D, decimalPlaces));
		assertEquals("1.0", MathUtils.roundDecimalPlaces(0.9999D, decimalPlaces));
		assertEquals("12345678912345678.0", MathUtils.roundDecimalPlaces(12345678912345678D, decimalPlaces));
		assertEquals("0.0", MathUtils.roundDecimalPlaces(0.0001234D, decimalPlaces));
		assertEquals("0.0", MathUtils.roundDecimalPlaces(0.0006789D, decimalPlaces));

		assertEquals("0.0", MathUtils.roundDecimalPlaces(-0D, decimalPlaces));
		assertEquals("-0.3", MathUtils.roundDecimalPlaces(-1D/3D, decimalPlaces));
		assertEquals("-0.5", MathUtils.roundDecimalPlaces(-0.5D, decimalPlaces));
		assertEquals("-0.7", MathUtils.roundDecimalPlaces(-2D/3D, decimalPlaces));
		assertEquals("-1.3", MathUtils.roundDecimalPlaces(-1.3333333D, decimalPlaces));
		assertEquals("-1.5", MathUtils.roundDecimalPlaces(-1.5D, decimalPlaces));
		assertEquals("-1.7", MathUtils.roundDecimalPlaces(-1.6666666D, decimalPlaces));
		assertEquals("-22.3", MathUtils.roundDecimalPlaces(-22.3333333D, decimalPlaces));
		assertEquals("-22.7", MathUtils.roundDecimalPlaces(-22.6666666D, decimalPlaces));
		assertEquals("-1.0", MathUtils.roundDecimalPlaces(-0.99D, decimalPlaces));
		assertEquals("-1.0", MathUtils.roundDecimalPlaces(-0.999D, decimalPlaces));
		assertEquals("-1.0", MathUtils.roundDecimalPlaces(-0.9999D, decimalPlaces));
		assertEquals("-12345678912345678.0", MathUtils.roundDecimalPlaces(-12345678912345678D, decimalPlaces));
		assertEquals("0.0", MathUtils.roundDecimalPlaces(-0.0001234D, decimalPlaces));
		assertEquals("0.0", MathUtils.roundDecimalPlaces(-0.0006789D, decimalPlaces));


		decimalPlaces = 2;
		assertEquals("0.00", MathUtils.roundDecimalPlaces(0D, decimalPlaces));
		assertEquals("0.33", MathUtils.roundDecimalPlaces(1D/3D, decimalPlaces));
		assertEquals("0.50", MathUtils.roundDecimalPlaces(0.5D, decimalPlaces));
		assertEquals("0.67", MathUtils.roundDecimalPlaces(2D/3D, decimalPlaces));
		assertEquals("1.33", MathUtils.roundDecimalPlaces(1.3333333D, decimalPlaces));
		assertEquals("1.50", MathUtils.roundDecimalPlaces(1.5D, decimalPlaces));
		assertEquals("1.67", MathUtils.roundDecimalPlaces(1.6666666D, decimalPlaces));
		assertEquals("22.33", MathUtils.roundDecimalPlaces(22.3333333D, decimalPlaces));
		assertEquals("22.67", MathUtils.roundDecimalPlaces(22.6666666D, decimalPlaces));
		assertEquals("0.99", MathUtils.roundDecimalPlaces(0.99D, decimalPlaces));
		assertEquals("1.00", MathUtils.roundDecimalPlaces(0.999D, decimalPlaces));
		assertEquals("1.00", MathUtils.roundDecimalPlaces(0.9999D, decimalPlaces));
		assertEquals("12345678912345678.00", MathUtils.roundDecimalPlaces(12345678912345678D, decimalPlaces));
		assertEquals("0.00", MathUtils.roundDecimalPlaces(0.0001234D, decimalPlaces));
		assertEquals("0.00", MathUtils.roundDecimalPlaces(0.0006789D, decimalPlaces));

		assertEquals("0.00", MathUtils.roundDecimalPlaces(-0D, decimalPlaces));
		assertEquals("-0.33", MathUtils.roundDecimalPlaces(-1D/3D, decimalPlaces));
		assertEquals("-0.50", MathUtils.roundDecimalPlaces(-0.5D, decimalPlaces));
		assertEquals("-0.67", MathUtils.roundDecimalPlaces(-2D/3D, decimalPlaces));
		assertEquals("-1.33", MathUtils.roundDecimalPlaces(-1.3333333D, decimalPlaces));
		assertEquals("-1.50", MathUtils.roundDecimalPlaces(-1.5D, decimalPlaces));
		assertEquals("-1.67", MathUtils.roundDecimalPlaces(-1.6666666D, decimalPlaces));
		assertEquals("-22.33", MathUtils.roundDecimalPlaces(-22.3333333D, decimalPlaces));
		assertEquals("-22.67", MathUtils.roundDecimalPlaces(-22.6666666D, decimalPlaces));
		assertEquals("-0.99", MathUtils.roundDecimalPlaces(-0.99D, decimalPlaces));
		assertEquals("-1.00", MathUtils.roundDecimalPlaces(-0.999D, decimalPlaces));
		assertEquals("-1.00", MathUtils.roundDecimalPlaces(-0.9999D, decimalPlaces));
		assertEquals("-12345678912345678.00", MathUtils.roundDecimalPlaces(-12345678912345678D, decimalPlaces));
		assertEquals("0.00", MathUtils.roundDecimalPlaces(-0.0001234D, decimalPlaces));
		assertEquals("0.00", MathUtils.roundDecimalPlaces(-0.0006789D, decimalPlaces));


		decimalPlaces = 6;
		assertEquals("0.000000", MathUtils.roundDecimalPlaces(0D, decimalPlaces));
		assertEquals("0.333333", MathUtils.roundDecimalPlaces(1D/3D, decimalPlaces));
		assertEquals("0.500000", MathUtils.roundDecimalPlaces(0.5D, decimalPlaces));
		assertEquals("0.666667", MathUtils.roundDecimalPlaces(2D/3D, decimalPlaces));
		assertEquals("1.333333", MathUtils.roundDecimalPlaces(1.3333333D, decimalPlaces));
		assertEquals("1.500000", MathUtils.roundDecimalPlaces(1.5D, decimalPlaces));
		assertEquals("1.666667", MathUtils.roundDecimalPlaces(1.6666666D, decimalPlaces));
		assertEquals("22.333333", MathUtils.roundDecimalPlaces(22.3333333D, decimalPlaces));
		assertEquals("22.666667", MathUtils.roundDecimalPlaces(22.6666666D, decimalPlaces));
		assertEquals("0.990000", MathUtils.roundDecimalPlaces(0.99D, decimalPlaces));
		assertEquals("0.999000", MathUtils.roundDecimalPlaces(0.999D, decimalPlaces));
		assertEquals("0.999900", MathUtils.roundDecimalPlaces(0.9999D, decimalPlaces));
		assertEquals("12345678912345678.000000", MathUtils.roundDecimalPlaces(12345678912345678D, decimalPlaces));
		assertEquals("0.000123", MathUtils.roundDecimalPlaces(0.0001234D, decimalPlaces));
		assertEquals("0.000679", MathUtils.roundDecimalPlaces(0.0006789D, decimalPlaces));

		assertEquals("0.000000", MathUtils.roundDecimalPlaces(-0D, decimalPlaces));
		assertEquals("-0.333333", MathUtils.roundDecimalPlaces(-1D/3D, decimalPlaces));
		assertEquals("-0.500000", MathUtils.roundDecimalPlaces(-0.5D, decimalPlaces));
		assertEquals("-0.666667", MathUtils.roundDecimalPlaces(-2D/3D, decimalPlaces));
		assertEquals("-1.333333", MathUtils.roundDecimalPlaces(-1.3333333D, decimalPlaces));
		assertEquals("-1.500000", MathUtils.roundDecimalPlaces(-1.5D, decimalPlaces));
		assertEquals("-1.666667", MathUtils.roundDecimalPlaces(-1.6666666D, decimalPlaces));
		assertEquals("-22.333333", MathUtils.roundDecimalPlaces(-22.3333333D, decimalPlaces));
		assertEquals("-22.666667", MathUtils.roundDecimalPlaces(-22.6666666D, decimalPlaces));
		assertEquals("-0.990000", MathUtils.roundDecimalPlaces(-0.99D, decimalPlaces));
		assertEquals("-0.999000", MathUtils.roundDecimalPlaces(-0.999D, decimalPlaces));
		assertEquals("-0.999900", MathUtils.roundDecimalPlaces(-0.9999D, decimalPlaces));
		assertEquals("-12345678912345678.000000", MathUtils.roundDecimalPlaces(-12345678912345678D, decimalPlaces));
		assertEquals("-0.000123", MathUtils.roundDecimalPlaces(-0.0001234D, decimalPlaces));
		assertEquals("-0.000679", MathUtils.roundDecimalPlaces(-0.0006789D, decimalPlaces));
	}

	public void testRoundDecimalPlacesSpeed(){
		long thePast = System.currentTimeMillis();
		int iterations = 0;

		for (int i = 0; i < iterations; i++){
			testRoundDecimalPlaces();
		}
		long elapsedTime = System.currentTimeMillis() - thePast;
		System.out.println("Time to complete " + iterations + " iterations of testRoundDecimalPlaces(): " + elapsedTime + "ms");
	}
}

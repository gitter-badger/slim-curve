/*
 * #%L
 * Test SLIM Curve Java layer library.
 * %%
 * Copyright (C) 2013 - 2014 Board of Regents of the University of
 * Wisconsin-Madison.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

package loci.slim;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * TODO
 *
 * @author Aivar Grislis
 */
public class SingleRLDTest {
	private static final int POISSON_DATA = 2;
	private static final int POISSON_FIT = 3;
	private static final int GAUSSIAN_FIT = 4;
	private static final int NOISE_MLE = 5;
	
	private static final double A_VALUE = 32.37078094482422;
	private static final double T_VALUE = 2.20745849609375;
	private static final double Z_VALUE = 1.527251958847046;
	private static final double X2_VALUE = 1.167478592000543;
	private static final double X2_VALUE_2 = 1.4605735738009693;
	
	private static final double TOLERANCE = 0.01; // within 1%
	
	private SLIMCurve slimCurve;
	
	public SingleRLDTest() {
		slimCurve = new SLIMCurve();
	}
	
	private double[] getTransient1() {
		return new double[] { 17.0, 35.0, 37.0, 32.0, 33.0, 28.0, 39.0, 36.0, 29.0, 32.0, 37.0, 38.0, 27.0, 31.0, 30.0, 32.0,
							  26.0, 29.0, 25.0, 25.0, 25.0, 21.0, 35.0, 23.0, 13.0, 15.0, 21.0, 18.0,  8.0, 16.0, 14.0, 20.0,
							  12.0, 18.0, 17.0, 17.0, 13.0, 15.0, 14.0, 16.0, 12.0, 18.0, 14.0, 10.0,  8.0, 10.0, 18.0,  7.0,
							  10.0,  8.0, 11.0, 11.0, 12.0, 10.0, 13.0,  7.0, 15.0,  8.0,  6.0, 10.0,  8.0,  7.0,  9.0, 11.0,
							  15.0,  6.0,  6.0, 10.0,  3.0,  8.0,  5.0,  7.0,  9.0,  7.0,  5.0,  3.0,  5.0,  4.0,  6.0,  5.0,
							   6.0,  7.0,  5.0,  8.0,  3.0, 11.0,  5.0,  5.0,  7.0, 10.0,  3.0,  6.0, 11.0,  5.0, 10.0,  3.0,
							   5.0,  4.0,  7.0,  2.0,  3.0,  3.0,  4.0,  4.0,  4.0,  5.0,  9.0,  8.0,  5.0,  7.0,  5.0,  4.0,
							   2.0,  9.0,  5.0,  2.0,  3.0,  7.0,  5.0,  4.0,  4.0,  0.0,  3.0,  5.0,  6.0,  7.0,  2.0,  2.0,
							   0.0,  5.0,  6.0,  1.0,  7.0,  5.0,  5.0,  1.0,  8.0,  4.0,  3.0,  7.0,  3.0,  1.0,  3.0,  2.0,
							   0.0,  2.0,  9.0,  3.0,  3.0,  3.0,  3.0,  0.0,  3.0,  2.0,  3.0,  4.0,  5.0,  2.0,  1.0,  1.0,
							   1.0,  2.0,  3.0,  4.0,  2.0,  1.0,  4.0,  2.0,  3.0,  2.0,  4.0,  1.0,  1.0,  6.0,  1.0,  3.0,
							   0.0,  2.0,  2.0,  3.0,  1.0,  0.0,  1.0,  2.0,  1.0,  1.0,  2.0,  2.0,  3.0,  3.0,  4.0,  0.0,
							   2.0,  2.0,  1.0,  0.0,  0.0,  3.0 };
	}
	
	private int[] getStartStop1() {
		return new int[] { 8, 198 };
	}
	
	private double getXInc1() {
		return 0.048828125;
	}
	
	private double[] getInitZ1() {
		return new double[] { 0.0 };
	}
	
	private double[] getInitA1() {
		return new double[] { 1000.0 };
	}
	
	private double[] getInitTau1() {
		return new double[] { 2.0 };
	}
	
	private double getChiSquareTarget() {
		return 1.25;
	}
	
	private boolean withinTolerance(double value, double expectedValue, double tolerance) {
		double factor = Math.abs(value / expectedValue);
        if (factor < 1.0) {
			factor = 1.0 - factor;
		}
		else {
			factor = factor - 1.0;
		}
		return factor < tolerance;
	}

	@Test
	public void doFit1a() {
		double xInc = getXInc1();
		int[] startStop = getStartStop1();
		int fitStart = startStop[0];
		int fitStop = startStop[1];
		double[] trans = getTransient1();
		
		int noise = POISSON_DATA;
		
		double[] fitted = new double[trans.length];
		double[] chiSquare = new double[1];
		double chiSquareTarget = getChiSquareTarget();
		double chiSquareAdjust = fitStop - fitStart - 3;
		
		double[] a = getInitA1(); // using arrays here to pass double by reference
		double[] tau = getInitTau1();
		double[] z = getInitZ1();
		
		double[] prompt = null;
		int promptLength = 0;
		double[] sig = null;
		
		int returnCode = slimCurve.fitRLD(
				xInc,
				trans,
				fitStart,
				fitStop,
				prompt,
				promptLength,
				noise,
				sig,
				z,
				a,
				tau,
				fitted,
				chiSquare,
				chiSquareTarget * chiSquareAdjust);
		
		assertEquals("Poisson data RLD A", A_VALUE, a[0], TOLERANCE);
		assertEquals("Poisson data RLD T", T_VALUE, tau[0], TOLERANCE);
		assertEquals("Poisson data RLD Z", Z_VALUE, z[0], TOLERANCE);
		assertEquals("Poisson data RLD Chisquare", X2_VALUE_2, (chiSquare[0] / chiSquareAdjust), TOLERANCE); //TODO ARG note this noise value gives a different chisquare!
	}
	
	@Test
	public void doFit1b() {
		double xInc = getXInc1();
		int[] startStop = getStartStop1();
		int fitStart = startStop[0];
		int fitStop = startStop[1];
		double[] trans = getTransient1();
		
		int noise = POISSON_FIT;
		
		double[] fitted = new double[trans.length];
		double[] chiSquare = new double[1];
		double chiSquareTarget = getChiSquareTarget();
		double chiSquareAdjust = fitStop - fitStart - 3;
		
		double[] a = getInitA1();
		double[] tau = getInitTau1();
		double[] z = getInitZ1();
		
		System.out.println("about to call slimCurve.fitRLD");
		
		double[] prompt = null;
		int promptLength = 0;
		double[] sig = null;
		
		int returnCode = slimCurve.fitRLD(
				xInc,
				trans,
				fitStart,
				fitStop,
				prompt,
				promptLength,
				noise,
				sig,
				z,
				a,
				tau,
				fitted,
				chiSquare,
				chiSquareTarget * chiSquareAdjust);
		
		assertEquals("Poisson fit RLD A", A_VALUE, a[0], TOLERANCE);
		assertEquals("Poisson fit RLD T", T_VALUE, tau[0], TOLERANCE);
		assertEquals("Poisson fit RLD Z", Z_VALUE, z[0], TOLERANCE);
		assertEquals("Poisson fit RLD Chisquare", X2_VALUE, (chiSquare[0] / chiSquareAdjust), TOLERANCE);

	}
	
	@Test
	public void doFit1c() {
		double xInc = getXInc1();
		int[] startStop = getStartStop1();
		int fitStart = startStop[0];
		int fitStop = startStop[1];
		double[] trans = getTransient1();
		
		int noise = GAUSSIAN_FIT;
		
		double[] fitted = new double[trans.length];
		double[] chiSquare = new double[1];
		double chiSquareTarget = getChiSquareTarget();
		double chiSquareAdjust = fitStop - fitStart - 3;
		
		double[] a = getInitA1();
		double[] tau = getInitTau1();
		double[] z = getInitZ1();
		
		System.out.println("about to call slimCurve.fitRLD");
		
		double[] prompt = null;
		int promptLength = 0;
		double[] sig = null;
		
		int returnCode = slimCurve.fitRLD(
				xInc,
				trans,
				fitStart,
				fitStop,
				prompt,
				promptLength,
				noise,
				sig,
				z,
				a,
				tau,
				fitted,
				chiSquare,
				chiSquareTarget * chiSquareAdjust);
		
		assertEquals("Gaussian fit RLD A", A_VALUE, a[0], TOLERANCE);
		assertEquals("Gaussian fit RLD T", T_VALUE, tau[0], TOLERANCE);
		assertEquals("Gaussian fit RLD Z", Z_VALUE, z[0], TOLERANCE);
		assertEquals("Gaussian fit RLD Chisquare", X2_VALUE, (chiSquare[0] / chiSquareAdjust), TOLERANCE);

	}
	
	@Test
	public void doFit1d() {
		double xInc = getXInc1();
		int[] startStop = getStartStop1();
		int fitStart = startStop[0];
		int fitStop = startStop[1];
		double[] trans = getTransient1();
		
		int noise = NOISE_MLE;
		
		double[] fitted = new double[trans.length];
		double[] chiSquare = new double[1];
		double chiSquareTarget = getChiSquareTarget();
		double chiSquareAdjust = fitStop - fitStart - 3;
		
		double[] a = getInitA1();
		double[] tau = getInitTau1();
		double[] z = getInitZ1();
		
		System.out.println("about to call slimCurve.fitRLD");
		
		double[] prompt = null;
		int promptLength = 0;
		double[] sig = null;
		
		int returnCode = slimCurve.fitRLD(
				xInc,
				trans,
				fitStart,
				fitStop,
				prompt,
				promptLength,
				noise,
				sig,
				z,
				a,
				tau,
				fitted,
				chiSquare,
				chiSquareTarget * chiSquareAdjust);
		
		assertEquals("Maximum Likelihood RLD A", A_VALUE, a[0], TOLERANCE);
		assertEquals("Maximum Likelihood RLD T", T_VALUE, tau[0], TOLERANCE);
		assertEquals("Maximum Likelihood RLD Z", Z_VALUE, z[0], TOLERANCE);
		assertEquals("Maximum Likelihood RLD Chisquare", X2_VALUE, (chiSquare[0] / chiSquareAdjust), TOLERANCE);
	}
}

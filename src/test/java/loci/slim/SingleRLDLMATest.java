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
public class SingleRLDLMATest {
	private static final int POISSON_DATA = 2;
	private static final int POISSON_FIT = 3;
	private static final int GAUSSIAN_FIT = 4;
	private static final int NOISE_MLE = 5;
	
	private static final int CHISQ_INDEX = 0;
	private static final int Z_INDEX = 1;
	private static final int A_INDEX = 2;
	private static final int T_INDEX = 3;
	
	private static final double A_VALUE_A = 38.053829193115234;
	private static final double T_VALUE_A = 1.567094087600708;
	private static final double Z_VALUE_A = 2.672193765640259;
	private static final double X2_VALUE_A = 1.4840443657043783;
	
	private static final double A_VALUE_B = 39.83982849121094;
	private static final double T_VALUE_B = 1.5328590869903564;
	private static final double Z_VALUE_B = 2.7071805000305176;
	private static final double X2_VALUE_B = 1.163433707334141;
	
	private static final double A_VALUE_C = 32.37078094482422;
	private static final double T_VALUE_C = 2.20745849609375;
	private static final double Z_VALUE_C = 1.527251958847046;
	private static final double X2_VALUE_C = 1.167478592000543;
	
	private static final double A_VALUE_D = 36.17356491088867;
	private static final double T_VALUE_D = 1.8257659673690796;
	private static final double Z_VALUE_D = 2.100743055343628;
	private static final double X2_VALUE_D = 1.2066651206603025;
	
	private static final double TOLERANCE = 0.01; // within 1%
	
	private SLIMCurve slimCurve;
	
	public SingleRLDLMATest() {
		slimCurve = new SLIMCurve();
	}
	
	private double[] getTransient1() {
		// this is pixel 144, 230 of 100611-YG-256.sdt
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
		
		double[] a = getInitA1();
		double[] tau = getInitTau1();
		double[] z = getInitZ1();
		
		double[] prompt = null;
		int promptLength = 0;
		double[] sig = null;

		// do the RLD estimate
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
		
		// single exponential fit
		double[] params = new double[4];
		params[0] = 0.0;
		params[1] = z[0];
		params[2] = a[0];
		params[3] = tau[0];

		// no fixed parameters
		int numParams = 3;
		int[] free = new int[] { 1, 1, 1 };
		
		chiSquareAdjust = fitStop - fitStart - numParams;
		double chiSquareDelta = 1.0;
                
		returnCode = slimCurve.fitLMA(
						xInc,
						trans,
						fitStart,
						fitStop,
						prompt,
						promptLength,
						noise,
						sig,
						params,
						free,
						numParams,
						fitted,
						chiSquare,
						(double) chiSquareTarget * chiSquareAdjust,
						chiSquareDelta
						);
		
		assertEquals("Poisson data RLD+LMA A", A_VALUE_A, params[A_INDEX], TOLERANCE);
		assertEquals("Poisson data RLD+LMA T", T_VALUE_A, params[T_INDEX], TOLERANCE);
		assertEquals("Poisson data RLD+LMA Z", Z_VALUE_A, params[Z_INDEX], TOLERANCE);
		assertEquals("Poisson data RLD+LMA Chisquare", X2_VALUE_A, (params[CHISQ_INDEX] / chiSquareAdjust), TOLERANCE);
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
		
		// single exponential fit
		double[] params = new double[4];
		params[0] = 0.0;
		params[1] = z[0];
		params[2] = a[0];
		params[3] = tau[0];

		// no fixed parameters
		int numParams = 3;
		int[] free = new int[] { 1, 1, 1 };
		
		chiSquareAdjust = fitStop - fitStart - numParams;
		double chiSquareDelta = 1.0;
                
		returnCode = slimCurve.fitLMA(
						xInc,
						trans,
						fitStart,
						fitStop,
						prompt,
						promptLength,
						noise,
						sig,
						params,
						free,
						numParams,
						fitted,
						chiSquare,
						(double) chiSquareTarget * chiSquareAdjust,
						chiSquareDelta
						);
		
		assertEquals("Poisson fit RLD+LMA A", A_VALUE_B, params[A_INDEX], TOLERANCE);
		assertEquals("Poisson fit RLD+LMA T", T_VALUE_B, params[T_INDEX], TOLERANCE);
		assertEquals("Poisson fit RLD+LMA Z", Z_VALUE_B, params[Z_INDEX], TOLERANCE);
		assertEquals("Poisson fit RLD+LMA Chisquare", X2_VALUE_B, (params[CHISQ_INDEX] / chiSquareAdjust), TOLERANCE);
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
		
		// single exponential fit
		double[] params = new double[4];
		params[0] = 0.0;
		params[1] = z[0];
		params[2] = a[0];
		params[3] = tau[0];

		// no fixed parameters
		int numParams = 3;
		int[] free = new int[] { 1, 1, 1 };
		
		chiSquareAdjust = fitStop - fitStart - numParams;
		double chiSquareDelta = 1.0;
                
		returnCode = slimCurve.fitLMA(
						xInc,
						trans,
						fitStart,
						fitStop,
						prompt,
						promptLength,
						noise,
						sig,
						params,
						free,
						numParams,
						fitted,
						chiSquare,
						(double) chiSquareTarget * chiSquareAdjust,
						chiSquareDelta
						);
		
		assertEquals("Poisson fit RLD+LMA A", A_VALUE_C, params[A_INDEX], TOLERANCE);
		assertEquals("Poisson fit RLD+LMA T", T_VALUE_C, params[T_INDEX], TOLERANCE);
		assertEquals("Poisson fit RLD+LMA Z", Z_VALUE_C, params[Z_INDEX], TOLERANCE);
		assertEquals("Poisson fit RLD+LMA Chisquare", X2_VALUE_C, (params[CHISQ_INDEX] / chiSquareAdjust), TOLERANCE);
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
		
		// single exponential fit
		double[] params = new double[4];
		params[0] = 0.0;
		params[1] = z[0];
		params[2] = a[0];
		params[3] = tau[0];

		// no fixed parameters
		int numParams = 3;
		int[] free = new int[] { 1, 1, 1 };
		
		chiSquareAdjust = fitStop - fitStart - numParams;
		double chiSquareDelta = 1.0;
                
		returnCode = slimCurve.fitLMA(
						xInc,
						trans,
						fitStart,
						fitStop,
						prompt,
						promptLength,
						noise,
						sig,
						params,
						free,
						numParams,
						fitted,
						chiSquare,
						(double) chiSquareTarget * chiSquareAdjust,
						chiSquareDelta
						);
		
		assertEquals("Poisson fit RLD+LMA A", A_VALUE_D, params[A_INDEX], TOLERANCE);
		assertEquals("Poisson fit RLD+LMA T", T_VALUE_D, params[T_INDEX], TOLERANCE);
		assertEquals("Poisson fit RLD+LMA Z", Z_VALUE_D, params[Z_INDEX], TOLERANCE);
		assertEquals("Poisson fit RLD+LMA Chisquare", X2_VALUE_D, (params[CHISQ_INDEX] / chiSquareAdjust), TOLERANCE);
	}
}

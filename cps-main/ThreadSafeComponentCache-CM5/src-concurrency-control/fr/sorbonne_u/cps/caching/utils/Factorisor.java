package fr.sorbonne_u.cps.caching.utils;

// Copyright Jacques Malenfant, Sorbonne Universite.
// Jacques.Malenfant@lip6.fr
//
// This software is a computer program whose purpose is to provide a
// basic component programming model to program with components
// distributed applications in the Java programming language.
//
// This software is governed by the CeCILL-C license under French law and
// abiding by the rules of distribution of free software.  You can use,
// modify and/ or redistribute the software under the terms of the
// CeCILL-C license as circulated by CEA, CNRS and INRIA at the following
// URL "http://www.cecill.info".
//
// As a counterpart to the access to the source code and  rights to copy,
// modify and redistribute granted by the license, users are provided only
// with a limited warranty  and the software's author,  the holder of the
// economic rights,  and the successive licensors  have only  limited
// liability. 
//
// In this respect, the user's attention is drawn to the risks associated
// with loading,  using,  modifying and/or developing or reproducing the
// software by the user in light of its specific status of free software,
// that may mean  that it is complicated to manipulate,  and  that  also
// therefore means  that it is reserved for developers  and  experienced
// professionals having in-depth computer knowledge. Users are therefore
// encouraged to load and test the software's suitability as regards their
// requirements in conditions enabling the security of their systems and/or 
// data to be ensured and,  more generally, to use and operate it in the 
// same conditions as regards security. 
//
// The fact that you are presently reading this means that you have had
// knowledge of the CeCILL-C license and that you accept its terms.

import java.util.ArrayList;

// -----------------------------------------------------------------------------
/**
 * The class <code>Factorisor</code> implements a very simple version of the
 * prime factor factorisation of positive integers.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 * 
 * <p>Created on : 2020-03-10</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			Factorisor
{
	/**
	 * Highly simplistic factorisation in prime factors.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code n > 1}
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param n	integer to be factored.
	 * @return	prime factors of <code>n</code>.
	 */
	public static Integer[]	factor(int n)
	{
		ArrayList<Integer> temp = new ArrayList<>() ;
		int current = n ;
		for (int i = 2 ; i <= current ; i++) {
			if (current % i == 0) {
				temp.add(i) ;
				current = current / i ;
			}
		}
		Integer[] result = new Integer[temp.size()] ;
		for (int i = 0 ; i < result.length ; i++) {
			result[i] = temp.get(i) ;
		}
		return result ;
	}

	/**
	 * test for the method <code>factor</code>.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code args == null || args.length == 0}
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param args	command line arguments.
	 */
	public static void main(String[] args)
	{
		assert	args == null || args.length == 0 ;
		int value = 155 ;
		Integer[] factors = factor(value) ;
		System.out.print("facteurs de " + value + " = [") ;
		for (int i = 0 ; i < factors.length ; i++) {
			System.out.print(factors[i]) ;
			if (i < factors.length - 1) {
				System.out.print(", ") ;
			}
		}
		System.out.println("]") ;
	}
}
// -----------------------------------------------------------------------------

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import geometry.Vector2D;

/**
 *
 * @author Gregoire
 */


public class QuickSort {
	public static final int DESCENDING = -1, ASCENDING = 1;

	private static void sort(double[] T, int bottom, int top, int order) {
		if (bottom < top) {
			int pivot = pick_pivot(T, bottom, top);
			pivot = partition(T, bottom, top, pivot, order);
			sort(T, bottom, pivot - 1, order);
			sort(T, pivot + 1, top, order);
		}
	}

	private static int pick_pivot(double[] tab, int l, int h) {
		return (l + h) / 2;

	}

	private static int partition(double[] T, int bottom, int top, int pivot,
			int order) {
		swap(T, pivot, top);
		int j = bottom;
		for (int i = bottom; i <= (top - 1); i++) {
			if (T[i]<(T[top]) * order ) {
				swap(T, i, j);
				j = j + 1;
			}
		}
		swap(T, top, j);
		return j;
	}

	private static void swap(double[] tab, int i, int j) {
		double temp = tab[i];
		tab[i] = tab[j];
		tab[j] = temp;
	}
        public static void sortTab(double[] T,int order){
            sort(T, 0, T.length-1, order);
            
            
        }
        
        
    
}


package br.com.alvesfred.interviews;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * How to modify the zeros to the right side of word, see the rules below:
 *	Input : 109090,2,0,5,0,0,10,8,0,-2000,0,4,0
 *	Output : 199, 2, 5, 1, 8, -2, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
 * 
 * @author fred
 *
 */
public class Interview1 {

	public static void main(String[] args) {
		String[] input = new String[] {
				"109090","2","0","5","0","0","10","8","0","-2000","0","4","0"
		};

		final AtomicInteger countNonZero = new AtomicInteger(0);
		final int countZero              = countZeros(input, countNonZero);
		final String[] output            = handleZeroInput(input, countZero, countNonZero.get());

		print(output);
	}

	private static final void print(final String[] output) {
		// just printing...
		for (int i = 0; i < output.length; i++) {
			System.out.println(output[i]);
		}
	}

	private static final String[] handleZeroInput(String[] input, int countZero, int countNonZero) {
		// output ALWAYS will be >= input length...
		String output[] = new String[countZero + countNonZero];
		int i = 0;
		int j = 0;
		while (j < output.length) {
			if (i < input.length && input[i].length() > 0) {
				output[j++] = input[i];
			} else if (i >= input.length) {
				output[j++] = "0";
			}

			i++;
		}

		return output;
	}

	private static final int countZeros(String[] input, AtomicInteger countNonZero) {
		int countZero    = 0;
		int count        = 0;

		for (int i = 0; i < input.length; i++) {
			count    = input[i].length();
			input[i] = removeZeroRecursive(input[i], 0);

			if (input[i].length() > 0) {
				countNonZero.set(countNonZero.get() + 1);
			}

			countZero += (count != input[i].length()) ? count - input[i].length() : 0;
		}

		return countZero;
	}

	private static final String removeZeroRecursive(String valor, int index) {
		String valorTmp = "";

		if (valor.length() > index) {
			if (valor.charAt(index) == '0') {
				valorTmp = removeZeroRecursive(valor, ++index);
			} else if (valor.charAt(index) != ' ') {
				valorTmp = valor.charAt(index) + removeZeroRecursive(valor, ++index);
			}
		}
		
		return valorTmp;
	}
}

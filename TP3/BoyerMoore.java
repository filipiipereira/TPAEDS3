import java.util.HashMap;

public class BoyerMoore {

    public static HashMap<Character, Integer> createBadCharTable(String padrao) {
        HashMap<Character, Integer> tabela = new HashMap<>();
        for (int i = 0; i < padrao.length(); i++) {
            tabela.put(padrao.charAt(i), i); // posição mais à direita
        }
        return tabela;
    }

    public static int[] createGoodSuffixTable(String padrao) {
        int m = padrao.length();
        int[] shift = new int[m + 1];
        int[] border = new int[m + 1];

        int i = m;
        int j = m + 1;
        border[i] = j;

        while (i > 0) {
            while (j <= m && padrao.charAt(i - 1) != padrao.charAt(j - 1)) {
                if (shift[j] == 0) {
                    shift[j] = j - i;
                }
                j = border[j];
            }
            i--;
            j--;
            border[i] = j;
        }

        for (i = 0; i <= m; i++) {
            if (shift[i] == 0) {
                shift[i] = j;
            }
            if (i == j) {
                j = border[j];
            }
        }

        // remove a posição extra (0 não é usada)
        int[] finalShift = new int[m];
        System.arraycopy(shift, 1, finalShift, 0, m);
        return finalShift;
    }

    public static void searchPattern(String texto, String padrao) {
        int n = texto.length();
        int m = padrao.length();
        int quantidadePadrao = 0;

        if (m == 0) return;

        HashMap<Character, Integer> badChar = createBadCharTable(padrao);
        int[] goodSuffix = createGoodSuffixTable(padrao);

        int i = 0;

        while (i <= n - m) {
            int j = m - 1;

            while (j >= 0 && padrao.charAt(j) == texto.charAt(i + j)) {
                j--;
            }

            if (j < 0) {
                System.out.println("Padrão encontrado na posição: " + i);
                quantidadePadrao++;
                i += goodSuffix[0];  // continuar busca
            } else {
                char c = texto.charAt(i + j);
                int badCharShift = j - badChar.getOrDefault(c, -1);
                int goodSuffixShift = goodSuffix[j];
                i += Math.max(badCharShift, goodSuffixShift);
            }
        }
        System.out.println("Quantidade de padrões encontrados no arquivo: " + quantidadePadrao);
    }
}

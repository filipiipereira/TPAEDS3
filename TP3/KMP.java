
public class KMP {

    public static void procuraPadrao(String texto, String padrao) {
        int[] vetorFalha = construirVetorFalha(padrao);
        int i = 0; 
        int j = 0; 
        int quantidadePadrao = 0;
        while (i < texto.length()) {
            if (padrao.charAt(j) == texto.charAt(i)) {
                i++;
                j++;
            }
            StringBuilder sb = new StringBuilder();
            if (j == padrao.length()) {
                System.out.println("Padrão encontrado na posição: " + (i - j));
                quantidadePadrao++;
                j = vetorFalha[j - 1];
            } else if (i < texto.length() && padrao.charAt(j) != texto.charAt(i)) {
                if (j != 0) {
                    j = vetorFalha[j - 1];
                } else {
                    i++;
                }
            }
        }
        System.out.println("Quantidade de padrões encontrados no arquivo: " + quantidadePadrao);
    }

    public static int[] construirVetorFalha(String padrao) {
        int[] vetorFalha = new int[padrao.length()];
        int len = 0;
        int i = 1;
        while (i < padrao.length()) {
            if (padrao.charAt(i) == padrao.charAt(len)) {
                len++;
                vetorFalha[i] = len;
                i++;
            } else {
                if (len != 0) {
                    len = vetorFalha[len - 1];
                } else {
                    vetorFalha[i] = 0;
                    i++;
                }
            }
        }
        return vetorFalha;
    }

}

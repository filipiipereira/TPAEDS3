import java.util.HashMap;
public class BoyerMoore{
    public static HashMap<Character, Integer> createHash(String padrao){
        HashMap<Character, Integer> hashMap = new HashMap<>();
        for(int i = padrao.length() - 2; i >= 0; i--){
            char caractere = padrao.charAt(i);
            if(!hashMap.containsKey(caractere)){
                hashMap.put(caractere, i);
            }
        }
        return hashMap;
    }

    public static int[] createArray(String padrao) {
        int m = padrao.length();
        int[] sufixoBom = new int[m];
        int[] borda = new int[m + 1];

        // Inicializa as variáveis
        int i = m;
        int j = m + 1;
        borda[i] = j;

        // Etapa 1: Construir a tabela de borda
        while (i > 0) {
            while (j <= m && padrao.charAt(i - 1) != padrao.charAt(j - 1)) {
                if (sufixoBom[j] == 0) {
                    sufixoBom[j] = j - i;
                }
                j = borda[j];
            }
            i--;
            j--;
            borda[i] = j;
        }

        // Etapa 2: Preencher os deslocamentos finais
        j = borda[0];
        for (i = 0; i <= m; i++) {
            if (sufixoBom[i] == 0) {
                sufixoBom[i] = j;
            }
            if (i == j) {
                j = borda[j];
            }
        }

        // Ajustar vetor para ter apenas m posições (descartar posição extra)
        int[] resultado = new int[m];
        System.arraycopy(sufixoBom, 1, resultado, 0, m);

        return resultado;
    }
    public static void searchPattern(String texto, String padrao){
        HashMap<Character, Integer> hash_cr = createHash(padrao);
        int array_sb[] = createArray(padrao);
        int m = padrao.length();
        int n = texto.length();
        int i = m - 1;

        while(i < n){
            int k = i;
            int j = m - 1;

            // Comparar da direita pra esquerda
            while(j >= 0 && texto.charAt(k) == padrao.charAt(j)){
                k--;
                j--;
            }

            if(j < 0){
                System.out.println("Padrão encontrado na posição: " + (k + 1));
                i += array_sb[0]; // usar o deslocamento do sufixo bom da posição 0
            } else {
                char caracterRuim = texto.charAt(k);
                int pos = hash_cr.containsKey(caracterRuim) ? hash_cr.get(caracterRuim) : -1;
                int deslocamentoCR = Math.max(1, j - pos); // deslocamento do caractere ruim
                int deslocamentoSB = array_sb[j];          // deslocamento do sufixo bom
                i += Math.max(deslocamentoCR, deslocamentoSB);
            }
        }
    }

}
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
        int[] sb = new int[m];
        int[] z = new int[m];
        
        // Computa o Z-array do padrão reverso
        String padraoReverso = new StringBuilder(padrao).reverse().toString();
        computeZArray(padraoReverso, z);
        
        for (int j = 0; j < m; j++) {
            sb[j] = m; // valor padrão
        }

        for (int i = m - 1; i >= 0; i--) {
            int j = m - z[i];
            if (j < m) {
                sb[j] = i;
            }
        }

        return sb;
    }

    // Função auxiliar para calcular o Z-array (usada no sufixo bom)
    private static void computeZArray(String s, int[] z) {
        int n = s.length();
        int l = 0, r = 0;
        z[0] = n;
        for (int i = 1; i < n; i++) {
            if (i > r) {
                l = r = i;
                while (r < n && s.charAt(r - l) == s.charAt(r)) {
                    r++;
                }
                z[i] = r - l;
                r--;
            } else {
                int k = i - l;
                if (z[k] < r - i + 1) {
                    z[i] = z[k];
                } else {
                    l = i;
                    while (r < n && s.charAt(r - l) == s.charAt(r)) {
                        r++;
                    }
                    z[i] = r - l;
                    r--;
                }
            }
        }
    }

    public static void searchPattern(String texto, String padrao){
        HashMap<Character, Integer> hash_cr = createHash(padrao);
        int[] array_sb = createArray(padrao);
        int m = padrao.length();
        int n = texto.length();
        int i = m - 1;

        while(i < n){
            int j = m - 1;
            int k = i;

            while(j >= 0 && texto.charAt(k) == padrao.charAt(j)){
                k--;
                j--;
            }

            if(j < 0){
                System.out.println("Posição: " + (k + 1));
                // Move para a próxima possível ocorrência
                i += m; // ou i += 1 se quiser achar todas
            } else {
                char c = texto.charAt(k);
                int crShift = hash_cr.containsKey(c) ? m - 1 - hash_cr.get(c) : m;
                int sbShift = array_sb[j];
                int shift = Math.max(crShift, sbShift);
                if (shift <= 0) shift = 1; // segurança contra loop infinito
                i += shift;
            }
        }
    }

}
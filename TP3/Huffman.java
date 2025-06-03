import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.PriorityQueue;

class HuffmanNode implements Comparable<HuffmanNode> {
    Byte b; // Pode ser null para nós internos
    int frequencia;
    HuffmanNode esquerdo, direito;

    public HuffmanNode(Byte b, int frequencia) {
        this.b = b;
        this.frequencia = frequencia;
    }

    @Override
    public int compareTo(HuffmanNode outro) {
        return Integer.compare(this.frequencia, outro.frequencia);
    }
}


public class Huffman {

    public static HashMap<Byte, String> geraCodigos(byte[] sequencia) {
        HashMap<Byte, Integer> mapaDeFrequencias = new HashMap<>();
        for (byte c : sequencia) {
            mapaDeFrequencias.put(c, mapaDeFrequencias.getOrDefault(c, 0) + 1);
        }

        PriorityQueue<HuffmanNode> pq = new PriorityQueue<>();
        for (Byte b : mapaDeFrequencias.keySet()) {
            pq.add(new HuffmanNode(b, mapaDeFrequencias.get(b)));
        }

        while (pq.size() > 1) {
            HuffmanNode esquerdo = pq.poll();
            HuffmanNode direito = pq.poll();

            HuffmanNode pai = new HuffmanNode(null, esquerdo.frequencia + direito.frequencia);
            pai.esquerdo = esquerdo;
            pai.direito = direito;

            pq.add(pai);
        }

        HuffmanNode raiz = pq.poll();
        HashMap<Byte, String> codigos = new HashMap<>();
        constroiCodigos(raiz, "", codigos);
        return codigos;
    }

    private static void constroiCodigos(HuffmanNode no, String codigo, HashMap<Byte, String> codigos) {
        if (no == null) return;

        if (no.esquerdo == null && no.direito == null && no.b != null) {
            codigos.put(no.b, codigo);
        }

        constroiCodigos(no.esquerdo, codigo + "0", codigos);
        constroiCodigos(no.direito, codigo + "1", codigos);
    }

    public static byte[] codifica(byte[] sequencia, HashMap<Byte, String> codigos) {
        VetorDeBits sequenciaCodificada = new VetorDeBits();
        int i = 0;
        for (byte b : sequencia) {
            String codigo = codigos.get(b);
            if (codigo == null) {
                throw new RuntimeException("Código não encontrado para byte: " + b);
            }
            for (char c : codigo.toCharArray()) {
                if (c == '0') {
                    sequenciaCodificada.clear(i++);
                } else {
                    sequenciaCodificada.set(i++);
                }
            }
        }
        return sequenciaCodificada.toByteArray();
    }

      // Versão buscando na tabela de códigos.
    public static byte[] decodifica(String sequenciaCodificada, HashMap<Byte, String> codigos) {
        ByteArrayOutputStream sequenciaDecodificada = new ByteArrayOutputStream();
        StringBuilder codigoAtual = new StringBuilder();

        for (int i = 0; i < sequenciaCodificada.length(); i++) {
            codigoAtual.append(sequenciaCodificada.charAt(i));
            for (byte b : codigos.keySet()) {
                if (codigos.get(b).equals(codigoAtual.toString())) {
                    sequenciaDecodificada.write(b);
                    codigoAtual = new StringBuilder();
                    break;
                }
            }
        }
        return sequenciaDecodificada.toByteArray();
    }

    public static void main(String[] args) {
        String frase = "O sabiá não sabia que o sábio sabia que o sabiá não sabia assobiar.";
        System.out.println("Frase original: " + frase);

        HashMap<Byte, String> codigos = geraCodigos(frase.getBytes());
        System.out.println("Códigos: " + codigos);

        // Codificação
        VetorDeBits sequenciaCodificada = new VetorDeBits();
        int i = 0;
        for (byte b : frase.getBytes()) {
            String codigo = codigos.get(b);
            for(char c : codigo.toCharArray()) {
                if(c=='0')
                    sequenciaCodificada.clear(i++);
                else
                    sequenciaCodificada.set(i++);            }
        }
        byte[] vb = sequenciaCodificada.toByteArray();
        System.out.println("\n"+sequenciaCodificada);
        System.out.println("Tamanho original: "+frase.getBytes().length+" bytes");
        System.out.println("Tamanho compactado: "+vb.length+" bytes");

        // Decodificação
        VetorDeBits sequenciaCodificada2 = new VetorDeBits(vb);
      //  System.out.println("\nFrase decodificada: " + (new String(decodifica(sequenciaCodificada2.toString(), codigos))));
    }
}
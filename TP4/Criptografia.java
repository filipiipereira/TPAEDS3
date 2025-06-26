import java.io.File; 
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * Classe responsável por realizar criptografia e descriptografia
 * de arquivos utilizando Cifra de César e DES.
 */
public class Criptografia {
    /** Valor de deslocamento usado na Cifra de César */
    private static int deslocamento;

    /** Algoritmo utilizado para criptografia DES */
    private static final String ALGORITHM = "DES";

    /** Transformação usada no Cipher para DES */
    private static final String TRANSFORMATION = "DES";

    /** Chave fixa de 8 bytes para uso no algoritmo DES */
    private static final byte[] keyBytes = "12345678".getBytes();

    /** Caminho do arquivo principal */
    private static String FILE_NAME = "SequentialFile.dat";

    /** Caminho do arquivo temporário */
    private static String FILE_TEMP = "Temp.dat";

    /**
     * Define o valor do deslocamento para uso na Cifra de César.
     * 
     * @param deslocamento valor inteiro de deslocamento
     */
    public static void setDeslocamento(int deslocamento) {
        Criptografia.deslocamento = deslocamento;
    }

    /**
     * Realiza a criptografia do conteúdo do arquivo temporário (Temp.dat)
     * utilizando a Cifra de César e grava o resultado em SequentialFile.dat.
     * Após a criptografia, o arquivo temporário é excluído.
     */
    public static void ciframentoCesarCriptografar() {
        try {
            FileInputStream fis = new FileInputStream(FILE_TEMP);
            int tamanho = fis.available();
            byte[] dadosOriginais = new byte[tamanho];
            fis.read(dadosOriginais);
            fis.close();

            byte[] dadosCriptografados = new byte[tamanho];
            for (int i = 0; i < tamanho; i++) {
                int byteOriginal = dadosOriginais[i] & 0xFF;
                dadosCriptografados[i] = (byte) ((byteOriginal + deslocamento) % 256);
            }

            FileOutputStream fos = new FileOutputStream(FILE_NAME);
            fos.write(dadosCriptografados);
            fos.close();

            File arquivoTemp = new File(FILE_TEMP);
            if (arquivoTemp.exists()) {
                boolean excluido = arquivoTemp.delete();
                if (!excluido) {
                    System.out.println("Não foi possível excluir o arquivo temporário: " + FILE_TEMP);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Realiza a descriptografia do conteúdo de SequentialFile.dat
     * utilizando a Cifra de César e grava o resultado no arquivo temporário (Temp.dat).
     */
    public static void ciframentoCesarDescriptografar() {
        try {
            FileInputStream fis = new FileInputStream(FILE_NAME);
            int tamanho = fis.available();
            byte[] dadosOriginais = new byte[tamanho];
            fis.read(dadosOriginais);
            fis.close();

            byte[] dadosDescriptografados = new byte[tamanho];
            for (int i = 0; i < tamanho; i++) {
                int byteOriginal = dadosOriginais[i] & 0xFF;
                dadosDescriptografados[i] = (byte) ((byteOriginal - deslocamento + 256) % 256);
            }

            FileOutputStream fos = new FileOutputStream(FILE_TEMP);
            fos.write(dadosDescriptografados);
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Realiza a criptografia do conteúdo do arquivo temporário (Temp.dat)
     * utilizando o algoritmo DES e grava o resultado em SequentialFile.dat.
     * Após a criptografia, o arquivo temporário é excluído.
     */
    
    public static void criptografarDES() {
        try {
            DESKeySpec keySpec = new DESKeySpec(keyBytes);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
            SecretKey secretKey = keyFactory.generateSecret(keySpec);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new SecureRandom());

            // Ler arquivo
            byte[] dadosOriginais = Files.readAllBytes(Paths.get(FILE_TEMP));

            // Criptografar
            byte[] dadosCriptografados = cipher.doFinal(dadosOriginais);

            // Escrever resultado
            try (FileOutputStream fos = new FileOutputStream(FILE_NAME)) {
                fos.write(dadosCriptografados);
            }

            File arquivoTemp = new File(FILE_TEMP);
            if (arquivoTemp.exists()) {
                boolean excluido = arquivoTemp.delete();
                if (!excluido) {
                    System.out.println("Não foi possível excluir o arquivo temporário: " + FILE_TEMP);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Realiza a descriptografia do conteúdo de SequentialFile.dat
     * utilizando o algoritmo DES e grava o resultado no arquivo temporário (Temp.dat).
     */
    public static void descriptografarDES() {
        try {
            DESKeySpec keySpec = new DESKeySpec(keyBytes);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
            SecretKey secretKey = keyFactory.generateSecret(keySpec);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new SecureRandom());

            // Ler arquivo criptografado
            byte[] dadosCriptografados = Files.readAllBytes(Paths.get(FILE_NAME));

            // Descriptografar
            byte[] dadosDescriptografados = cipher.doFinal(dadosCriptografados);

            // Escrever resultado
            try (FileOutputStream fos = new FileOutputStream(FILE_TEMP)) {
                fos.write(dadosDescriptografados);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

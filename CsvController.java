import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import static java.time.ZoneOffset.UTC;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Classe responsável por carregar dados de um arquivo CSV e armazená-los como objetos do tipo Movie.
 */
public class CsvController {
    
    /**
     * Nome do arquivo CSV a ser carregado.
     */
    private static String CSV_NAME = "moviesDataSet.csv";
    private static String FILE_NAME = "SequentialFile.dat";
    
    /**
     * Método para carregar os dados do arquivo CSV e inseri-los em um arquivo sequencial.
     * 
     * O método faz a leitura linha por linha do arquivo, processa os dados, converte tipos quando necessário
     * e cria objetos da classe Movie, os quais são inseridos em um arquivo sequencial.
     */
     public static void LoadFromCsv() {
        String linha;
        SequentialFile sequentialFile = new SequentialFile();
        System.out.println("Loading...");
        try (RandomAccessFile file = new RandomAccessFile(FILE_NAME, "rw")){
            try (BufferedReader br = new BufferedReader(new FileReader(CSV_NAME))) {
                br.readLine(); // Ignora o cabeçalho
                linha = br.readLine();
                
                while (linha != null) {
                    // Divide a linha respeitando valores entre aspas
                    String[] originalValues = linha.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                    String[] values = new String[6];
                    
                    // Copia os valores existentes
                    for (int i = 0; i < originalValues.length; i++) {
                        values[i] = originalValues[i];
                    }
                    
                    // Preenche os valores ausentes com strings vazias
                    if (originalValues.length < 6) {
                        for (int i = originalValues.length; i < 6; i++) {
                            values[i] = "";
                        }
                    }
                    
                    // Processa os valores extraídos
                    String name = values[0];
                    LocalDate date = null;
                    long epochDate = -1;
                    
                    if (!values[1].isEmpty()) {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                        date = LocalDate.parse(values[1], formatter);
                        epochDate = date.atStartOfDay(UTC).toEpochSecond();
                    }
                    
                    int budget = 0;
                    if (!values[2].isEmpty()) {
                        budget = Integer.parseInt(values[2]);
                    }
                    
                    float boxOffice = 0;
                    if (!values[3].isEmpty()) {
                        boxOffice = Float.parseFloat(values[3]);
                    }
                    
                    String genre = values[4];
                    List<String> financingCompanies = new ArrayList<>(Arrays.asList(values[5].split(",")));
                    
                    // Cria um objeto Movie e o insere no arquivo sequencial
                    Movie movie = new Movie(0, name, epochDate, budget, boxOffice, financingCompanies, genre);
                    sequentialFile.Insert(movie, file);
                    
                    // Lê a próxima linha
                    linha = br.readLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        System.out.println("Load completed");
    }
}

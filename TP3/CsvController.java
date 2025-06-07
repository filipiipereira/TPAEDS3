import java.io.BufferedReader;
import java.io.FileReader;
import java.io.RandomAccessFile;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Classe responsável por carregar dados de um arquivo CSV e armazená-los como objetos do tipo Movie.
 */
public class CsvController {
    
    /**
     * Nome do arquivo CSV a ser carregado.
     */
    private static final String CSV_NAME = "moviesDataSet.csv";
    private static final String FILE_NAME = "SequentialFile.dat";
    private static final String BTREE_NAME = "tree.dat";
    private static final String DIRECTORY_HASH = "hashDirectory.dat";
    private static final String BUCKET_HASH = "hashBuckets.dat";
    private static final String DICIONARYNAME_LIST_NAME = "dicionaryListName.dat";
    private static final String BLOCOSNAME_LIST_NAME = "blocosListName.dat";
    private static final String DICIONARYGENRE_LIST_NAME = "dicionaryGenre.dat";
    private static final String BLOCOSGENRE_LIST_NAME = "blocosListGenre.dat";

    /**
     * Método para carregar os dados do arquivo CSV e inseri-los em um arquivo sequencial.
     * 
     * O método faz a leitura linha por linha do arquivo, processa os dados, converte tipos quando necessário
     * e cria objetos da classe Movie, os quais são inseridos em um arquivo sequencial.
     */
    
    public static void LoadFromCsv() {
        String line;
        SequentialFile sequentialFile = new SequentialFile();
        System.out.println("Loading...");
        try (RandomAccessFile file = new RandomAccessFile(FILE_NAME, "rw")){
            ArvoreBMais bTree = new ArvoreBMais<>(ParIntLong.class.getConstructor(), 5, BTREE_NAME);
            HashExtensivel<ParIntLongHash> he = new HashExtensivel<>(ParIntLongHash.class.getConstructor(), 10, DIRECTORY_HASH,
            BUCKET_HASH);
            ListaInvertida listName = new ListaInvertida(4, DICIONARYNAME_LIST_NAME, BLOCOSNAME_LIST_NAME);
            ListaInvertida listGenre = new ListaInvertida(4, DICIONARYGENRE_LIST_NAME, BLOCOSGENRE_LIST_NAME);
            try (BufferedReader br = new BufferedReader(new FileReader(CSV_NAME))) {
                br.readLine(); // Ignora o cabeçalho
                line = br.readLine();
                while (line != null) {
                    Movie movie = readMovieFromCSV(line);
                    long pos = sequentialFile.InsertMovieFromCSV(movie, file);
                    IndexController.Create(movie, pos, bTree, he, listName, listGenre);
                    line = br.readLine();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Load completed");
    }

    public static Movie readMovieFromCSV(String line){
        // Divide a line respeitando valores entre aspas
        String[] originalValues = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
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
        
        if (!values[1].isEmpty()) {
            date = LocalDate.parse(values[1]);
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
        Movie movie = new Movie(0, name, date, budget, boxOffice, financingCompanies, genre);
        return movie;
    }
}

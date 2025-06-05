import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

/**
 * Classe Controller responsável por gerenciar a interação com o usuário e manipular objetos da classe Film.
 */
public class Controller {
    private static String FILE_NAME = "SequentialFile.dat";
    private static final String COMPRESSED_HUFFMAN_PREFIX = "SequentialFileHuffManCompress_v";
    private static final String COMPRESSED_SUFFIX = ".dat";
    private static final String COMPRESSED_LZW_PREFIX = "SequentialFileLZWCompress_v";
    private static final String BTREE_NAME = "tree.dat";
    private static final String DIRECTORY_HASH = "hashDirectory.dat";
    private static final String BUCKET_HASH = "hashBuckets.dat";
    private static final String DICIONARYNAME_LIST_NAME = "dicionaryListName.dat";
    private static final String BLOCOSNAME_LIST_NAME = "blocosListName.dat";
    private static final String DICIONARYGENRE_LIST_NAME = "dicionaryGenre.dat";
    private static final String BLOCOSGENRE_LIST_NAME = "blocosListGenre.dat";
    /**
     * Método responsável por capturar os dados de um filme a partir da entrada do usuário.
     * 
     * @param scanner Objeto Scanner para entrada do usuário.
     * @return Objeto Film preenchido com os dados informados pelo usuário.
     */

    public static Movie Form(Scanner scanner) {
        System.out.print("\nNew name: ");
        scanner.nextLine(); // Limpa o buffer
        String name = scanner.nextLine();

        LocalDate date = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        boolean controleDataValida = false;
        while (!controleDataValida) {
            try {
                System.out.print("New date (yyyy-MM-dd): ");
                String dateInput = scanner.nextLine();
                date = LocalDate.parse(dateInput);
                controleDataValida = true;
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format! Please enter the correct format (yyyy-MM-dd).");
            }
        }
        int budget = 0;
        boolean controleBudgetValido = false;

        while (!controleBudgetValido) {
        try {
            System.out.print("New budget: ");
            budget = scanner.nextInt();
            controleBudgetValido = true; 
        } catch (InputMismatchException e) {
            System.out.println("Invalid value! Please enter an integer number for the budget.");
            scanner.nextLine();
        }   
    }
        float boxOffice = 0.0f;
        boolean controleBoxOfficeValido = false;
        while (!controleBoxOfficeValido) {
            try {
                System.out.print("New global box-office: ");
                boxOffice = scanner.nextFloat();
                controleBoxOfficeValido = true; 
            } catch (InputMismatchException e) {
                System.out.println("Invalid value! Please enter a valid decimal number for the global box-office.");
                scanner.nextLine(); 
            }
        }
        System.out.print("New genre: ");
        scanner.nextLine(); // Limpa o buffer
        String genre = scanner.nextLine();

        System.out.print("New financing companies (type '0' to stop): ");
        List<String> financingCompanies = new ArrayList<>();
        String financingCompany = scanner.nextLine();

        while (!financingCompany.equals("0")) {
            financingCompanies.add(financingCompany);
            financingCompany = scanner.nextLine();
        }

        return new Movie(1, name, date, budget, boxOffice, financingCompanies, genre);
    }

    public static int MenuIndex(){
        Scanner scanner = new Scanner(System.in);
        int index;
        do { 
            System.out.println("\nMenu Index: ");
            System.out.println("1) Btree");
            System.out.println("2) Extended Hash");
            System.out.println("3) Inverted List");
            index = scanner.nextInt();
            if(index < 1 || index > 3) System.out.println("Opção Inválida!");
        } while (index < 1 || index > 3);
        return index;
    }
    public static int MenuLista(){
        Scanner scanner = new Scanner(System.in);
        int option;
        do { 
            System.out.println("\nMenu Lista ");
            System.out.println("1) Pesquisar por nome");
            System.out.println("2) Pesquisar por gênero");
            System.out.println("3) Pesquisar por nome e gênero");
            option = scanner.nextInt();
            if(option < 1 || option > 3) System.out.println("Opção Inválida!");
        } while (option < 1 || option > 3);
        return option;
    }


    /**
     * Método responsável por atualizar um filme existente com base no ID informado.
     * 
     * @param scanner Objeto Scanner para entrada do usuário.
     * @return true se a atualização foi bem-sucedida, false caso contrário.
     */

    public static boolean Update(Scanner scanner) {
        System.out.println("Which ID: ");
        int id = scanner.nextInt();
        SequentialFile.Get(id, 2).toStr();
        Movie movie = Form(scanner);
        movie.setId(id);
        return SequentialFile.Update(movie);
    }

    /**
     * Método responsável por deletar um filme com base no ID informado.
     * 
     * @param scanner Objeto Scanner para entrada do usuário.
     * @return true se a exclusão foi bem-sucedida, false caso contrário.
     */

    public static boolean Delete(Scanner scanner) {
        boolean flag = false;
       
        System.out.println("Which ID: ");
        int id = scanner.nextInt();
        flag = SequentialFile.Delete(id);
        return flag;
    }

    /**
     * Método responsável por buscar um filme com base no ID informado.
     * 
     * @param scanner Objeto Scanner para entrada do usuário.
     * @return Objeto Film correspondente ao ID informado.
     */

    public static void Get(Scanner scanner) {
        int index = MenuIndex();
        if(index == 1 | index == 2){
            System.out.println("Which ID: ");
            int id = scanner.nextInt();
            Movie movie = SequentialFile.Get(id, index);
            if(movie == null) System.out.println("Movie not found!");
            else movie.toStr();
        }
        else{
            Movie[] lista;
            int option = MenuLista();
            scanner.nextLine(); //cleaning buffer
            System.out.print("Digite a palavra: ");
            String palavra = scanner.nextLine();
            if(option == 3) {
                System.out.print("Digite a segunda palavra: ");
                String genre = scanner.nextLine();
                lista = SequentialFile.GetLista(palavra, genre, option);
            } else {
                lista = SequentialFile.GetLista(palavra, "",option);
            }
            
            System.out.println("Tamanho lista de filmes: " + lista.length);
            for(Movie m : lista){
                m.toStr();
            }
        }
    }

    public static void Compress() {
        long inicioHuffman = System.currentTimeMillis(); 
        String nomeArquivoHuff = SequentialFile.CompressHuffman();
        if(nomeArquivoHuff != null) System.out.println("Compressão Huffman OK");
        else System.out.println("Compressão Huffman ERRO");
        long finalHuffman = System.currentTimeMillis();
        long resultadoMilliHuff = finalHuffman - inicioHuffman;
        long inicioLZW = System.currentTimeMillis();
        String nomeArquivoLZW = SequentialFile.CompressLZW();
        if(nomeArquivoLZW != null) System.out.println("Compressão LZW OK");
        else System.out.println("Compressao LZW ERRO");
        System.out.println("");
        long finalLZW = System.currentTimeMillis();
        long resultadoLZWMilli = finalLZW - inicioLZW;
        SequentialFile.compararAlgoritmoCompressao(nomeArquivoHuff,nomeArquivoLZW,resultadoMilliHuff,resultadoLZWMilli);
    }

    public static void Decompress(Scanner scanner) {
        int versao;
            System.out.println("Por qual versão deseja descomprimir? Existem " + SequentialFile.contarVersoes(COMPRESSED_LZW_PREFIX, COMPRESSED_SUFFIX) + " versões: ");
            versao = scanner.nextInt();

                String nomeArquivoLZW = COMPRESSED_LZW_PREFIX + versao + COMPRESSED_SUFFIX;
                String nomeArquivoHuff = COMPRESSED_HUFFMAN_PREFIX + versao + COMPRESSED_SUFFIX;
                long inicioHuffman = System.currentTimeMillis(); 
                SequentialFile.DecompressHuffman(nomeArquivoHuff); 
                long finalHuffman = System.currentTimeMillis();
                long resultadoMilliHuff = finalHuffman - inicioHuffman;
                long inicioLZW = System.currentTimeMillis();
                SequentialFile.DecompressLZW(nomeArquivoLZW, versao);
                long finalLZW = System.currentTimeMillis();
                long resultadoLZWMilli = finalLZW - inicioLZW;
                SequentialFile.compararAlgoritmoDescompressao(resultadoMilliHuff, resultadoLZWMilli);
                
}
    

    public static void Match(Scanner scanner) {
        System.out.print("Digite o padrão a ser buscado: ");
        scanner.nextLine();
        String padrao = scanner.nextLine(); // remove espaços e quebras
        int escolha;
        boolean flag;
        do{
            System.out.print("\n1) KMP\n2)Boyer Moore\nDigite o algoritmo a ser executado: ");
            escolha = scanner.nextInt();
            flag = escolha != 1 && escolha != 2;
            if(flag) System.out.println("Opção inválida!");
        }while(flag);
        SequentialFile.Match(padrao, escolha);
    }
}

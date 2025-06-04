/**
 * Classe principal que executa o menu de interação com o usuário.
 */
import java.io.File;
import java.util.Scanner;

public class Main {
    /**
     * Método principal que exibe o menu e gerencia as opções do usuário.
     * 
     * @param args Argumentos da linha de comando (não utilizados).
     */
    private static final String FILE_NAME = "SequentialFile.dat";

    public static void main(String[] args) {
        int option;
        int index = 0;
        Scanner scanner = new Scanner(System.in);
        
        do {
            System.out.println("\nMenu: ");
            System.out.println("0) End program");
            System.out.println("1) Load movies from Csv --> Arquivos já carregados devido a demora para carregar Lista Invertida");
            System.out.println("2) Update movie");
            System.out.println("3) Delete movie");
            System.out.println("4) Show data from a movie");
            System.out.println("5) Compress");
            System.out.println("6) Decompress");
            System.out.println("7) Casamento de Padrões(KMP)");
            System.out.println("\nChoose an option: ");
            
            option = scanner.nextInt(); 
            
            switch(option) {
                case 0:
                    System.out.println("See you next time!");
                    break;
                case 1:
                    CsvController.LoadFromCsv();
                    break;
                case 2:
                    if (!isFileLoaded()) {
                        System.out.println("Error: File not loaded. Please load it first.");
                        break;
                    }
                    if(Controller.Update(scanner)) System.out.println("Updated successfully");
                    else System.out.println("Error in update");
                    break;
                case 3:
                    if (!isFileLoaded()) {
                        System.out.println("Error: File not loaded. Please load it first.");
                        break;
                    }
                    if (Controller.Delete(scanner)) 
                        System.out.println("Deleted successfully");
                    else 
                        System.out.println("Error in delete");
                    break;
                case 4:
                    if (!isFileLoaded()) {
                        System.out.println("Error: File not loaded. Please load it first.");
                        break;
                    }
                    Controller.Get(scanner);
                    break;
                case 5:
                    Controller.Compress();
                    break;
                case 6:
                    Controller.Decompress(scanner);
                    break;
                case 7:
                    scanner.nextLine(); //limpar buffer
                    Controller.KMP(scanner);
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
                    break;
            }
        } while(option != 0);
        
        scanner.close();
    }

    /**
     * Método para verificar se o arquivo foi carregado.
     * 
     * @return true se o arquivo existir, false caso contrário.
     */

    private static boolean isFileLoaded() {
        File file = new File(FILE_NAME);
        return file.exists();
    }
}
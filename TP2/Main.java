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
    
    public static void main(String[] args) {
        int option;
        int index = 0;
        Scanner scanner = new Scanner(System.in);
        
        do {
            System.out.println("\nMenu: ");
            System.out.println("0) End program");
            System.out.println("1) Load movies from Csv");
            System.out.println("2) Update movie");
            System.out.println("3) Delete movie");
            System.out.println("4) Show data from a movie");
            System.out.println("5) Sort File");
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
                    System.out.println("\nMenu UPDATE: ");
                    System.out.println("0) Back to main menu");
                    System.out.println("1) Btree");
                    System.out.println("2) Extended Hash");
                    System.out.println("3) Inverted List");
                    index = scanner.nextInt();
                        if(index == 0) {
                            break;
                        }
                        else if(index >=1 && index <=3) {
                            if(Controller.Update(scanner, index))
                            System.out.println("Updated successfully");
                            else 
                            System.out.println("Error in update");
                        } 
                        else{
                            System.out.println("Opção Inválida!!");
                        }
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
                    System.out.println("\nMenu GET: ");
                    System.out.println("0) Back to main menu");
                    System.out.println("1) Btree");
                    System.out.println("2) Extended Hash");
                    System.out.println("3) Inverted List");
                    index = scanner.nextInt();
                    if(index == 0) {
                        break;
                    }
                    else if(index >=1 && index <=3) {
                        Movie movie = Controller.Get(scanner, index);
                            if (movie == null) 
                                System.out.println("Movie not found");
                            else 
                                movie.toStr();
                            }
                    else {
                        System.out.println("Opção Inválida!");
                    }
                    break;
                case 5:
                    if (!isFileLoaded()) {
                        System.out.println("Error: File not loaded. Please load it first.");
                        break;
                    }
                    Controller.Sort(scanner);
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
                    break;
            }
        } while(option != 0);
        
        scanner.close();
    }

    private static final String FILE_NAME = "SequentialFile.dat";

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
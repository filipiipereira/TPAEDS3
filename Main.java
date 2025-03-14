/**
 * Classe principal que executa o menu de interação com o usuário.
 */
import java.util.Scanner;

public class Main {
    /**
     * Método principal que exibe o menu e gerencia as opções do usuário.
     * 
     * @param args Argumentos da linha de comando (não utilizados).
     */
    public static void main(String[] args) {
        int option;
        Scanner scanner = new Scanner(System.in);
        
        do {
            System.out.println("\nMenu: ");
            System.out.println("0) End program");
            System.out.println("1) Load movies from Csv");
            System.out.println("2) Insert new movie");
            System.out.println("3) Update movie");
            System.out.println("4) Delete movie");
            System.out.println("5) Show data from a movie");
            System.out.println("6) Sort File");
            System.out.println("\nChoose an option: ");
            
            option = scanner.nextInt(); 
            
            switch(option) {
                case 0:
                    CsvController.LoadToCsv();
                    System.out.println("See you next time!");
                    break;
                case 1:
                    CsvController.LoadFromCsv();
                    break;
                case 2:
                    if(Controller.Insert(scanner)) 
                        System.out.println("Inserted successfully");
                    else 
                        System.out.println("Error in insert");
                    break;
                case 3: 
                    if(Controller.Update(scanner)) 
                        System.out.println("Updated successfully");
                    else 
                        System.out.println("Error in update");
                    break;
                case 4: 
                    if(Controller.Delete(scanner)) 
                        System.out.println("Deleted successfully");
                    else 
                        System.out.println("Error in delete");
                    break;
                case 5: 
                    Movie movie = Controller.Get(scanner);
                    if(movie == null) 
                        System.out.println("movie not found");
                    else 
                        movie.toStr();
                    break;
                case 6: 
                    Controller.Sort(scanner);
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        } while(option != 0);
        
        scanner.close();
    }
}
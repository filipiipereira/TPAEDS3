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

        System.out.println("");
        System.out.println("FILIPAO VOCE É O CARAAAAA, VOCE É FODAAAAAAA S2 SALVE DO JULIADA");
        
        do {
            System.out.println("\nMenu: ");
            System.out.println("0) End program");
            System.out.println("1) Load films from Csv");
            System.out.println("2) Insert new film");
            System.out.println("3) Update film");
            System.out.println("4) Delete film");
            System.out.println("5) Show data from a movie");
            System.out.println("6) Sort");
            System.out.println("\nChoose an option: ");
            
            option = scanner.nextInt(); 
            
            switch(option) {
                case 0:
                    System.out.println("See you next time!");
                    break;
                case 1:
                    LoadCsv.LoadFromCsv();
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
                    Film film = Controller.Get(scanner);
                    if(film == null) 
                        System.out.println("Film not found");
                    else 
                        film.toStr();
                    break;
                case 6: 
                    SequentialFile.ExternalSort(200, 5);
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        } while(option != 0);
        
        scanner.close();
    }
}
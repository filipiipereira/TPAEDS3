
import java.util.Scanner;


public class Main {
    public static void main(String[] args) {
        int option;
        Scanner scanner = new Scanner(System.in);
        do{
            System.out.println("\nMenu: ");
            System.out.println("0) End program");
            System.out.println("1) Load films from Csv");
            System.out.println("2) Insert new film");
            System.out.println("3) Update film");
            System.out.println("4) Delete film");
            System.out.println("5) Show data from a movie");
            do { 
                System.out.println("\nChoose an option: ");
                option = scanner.nextInt(); 
            } while (!(option <= 0 || option >= 5));
            switch(option){
                case 0:
                    System.out.println("Saindo...");
                    break;
                case 1:
                    LoadCsv.LoadFromCsv();
                    break;
                case 2:
                    if(Controller.Insert(scanner)) System.out.println("Inserted successfully");
                    else System.out.println("error in insert");
                    break;
                case 3: 
                    if(Controller.Update(scanner)) System.out.println("Uptaded successfully");
                    else System.out.println("error in update");
                    break;
                case 4: 
                    if(Controller.Delete(scanner)) System.out.println("Deleted successfully");
                    else System.out.println("error in delete");
                    break;
                case 5: 
                    Film film = Controller.Get(scanner);
                    if(film == null) System.out.println("Film not found");
                    else film.toStr();
                    break;
                default:
                    System.out.println("Opção Inválida!");
            }
        }while(option != 0);
        scanner.close();
    }
}

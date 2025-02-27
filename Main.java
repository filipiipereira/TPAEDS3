
import java.util.Scanner;


public class Main {
    public static void main(String[] args) {
        int option;
        Scanner scanner = new Scanner(System.in);
        Controller controller = new Controller();
        do{
            System.out.println("Menu: ");
            System.out.println("0) End program");
            System.out.println("1) Load films from Csv");
            System.out.println("2) Insert new film");
            System.out.println("3) Update film");
            System.out.println("4) Delete film");
            System.out.println("5) Show data from a movie");
            do { 
                System.out.println("Choose an option: ");
                option = scanner.nextInt(); 
            } while (!(option == 0 || option == 1 || option == 2 || option == 3 || option == 4 || option == 5));
            switch(option){
                case 1:
                    LoadCsv lc = new LoadCsv();
                    lc.LoadFromCsv();
                    break;
                case 2:
                    if(controller.Insert(scanner)) System.out.println("Inserted successfully");
                    else System.out.println("error in insert");
                    break;
                case 3: 
                    if(controller.Update(scanner)) System.out.println("Uptaded successfully");
                    else System.out.println("error in update");
                    break;
                case 4: 
                    if(controller.Delete(scanner)) System.out.println("Deleted successfully");
                    else System.out.println("error in delete");
                    break;
                case 5: 
                    controller.Get(scanner);
            }
        }while(option != 0);
        scanner.close();
    }
}

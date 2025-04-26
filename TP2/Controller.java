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
        int index = MenuIndex();
        boolean flag = false;
        if(index == 1 | index == 2){
            System.out.println("Which ID: ");
            int id = scanner.nextInt();
            SequentialFile.Get(id, index).toStr();
            Movie movie = Form(scanner);
            movie.setId(id);
            flag = SequentialFile.Update(movie, index);
        }
        else{
            int option = MenuLista();
            System.out.print("Digite a palavra: ");
            String palavra = scanner.nextLine();
            SequentialFile.GetLista(palavra, option);
        }
        return flag;
    }

    /**
     * Método responsável por deletar um filme com base no ID informado.
     * 
     * @param scanner Objeto Scanner para entrada do usuário.
     * @return true se a exclusão foi bem-sucedida, false caso contrário.
     */

    public static boolean Delete(Scanner scanner) {
        int index = MenuIndex();
        System.out.println("Which ID: ");
        int id = scanner.nextInt();
        return SequentialFile.Delete(id, index);
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
            int option = MenuLista();
            scanner.nextLine(); //cleaning buffer
            System.out.print("Digite a palavra: ");
            String palavra = scanner.nextLine();
            Movie[] lista = SequentialFile.GetLista(palavra, option);
            System.out.println("Tamanho lista de filmes: " + lista.length);
            for(Movie m : lista){
                m.toStr();
            }
        }
    }
}
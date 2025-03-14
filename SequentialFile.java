/**
 * Classe que implementa operações de armazenamento sequencial para objetos do tipo Movie.
 * As operações incluem inserção, recuperação, atualização e exclusão.
 */
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
public class SequentialFile {
    protected static String FILE_NAME = "SequentialFile.dat";

    protected static int numberOfMovies = 0;

    public static void WriteMovie(RandomAccessFile file, Movie Movie){
        try {
            file.writeByte(0); // flag
            file.writeInt(Movie.registerByteSize());
            file.writeInt(Movie.getId());
            file.writeUTF(Movie.getName());
            file.writeLong(Movie.getDate());
            file.writeInt(Movie.getBudget());
            file.writeFloat(Movie.getBoxOffice());
            
            // Escreve o gênero como string de tamanho fixo
            String genre = Movie.getGenre();
            for (int i = 0; i < 10; i++) {
                file.writeByte(i < genre.length() ? genre.charAt(i) : ' ');
            }
            
            file.writeInt(Movie.getFinancingCompanies().size());
            for (String company : Movie.getFinancingCompanies()) {
                file.writeUTF(company);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static Movie ReadMovie(RandomAccessFile file){
        Movie Movie = null;
        try {
            int registerId = file.readInt();
            String registerName = file.readUTF();
            long registerDate = file.readLong();
            int registerBudget = file.readInt();
            float registerBoxOffice = file.readFloat();
            byte[] genreBytes = new byte[10];
            file.readFully(genreBytes);
            String registerGenre = new String(genreBytes).trim();
            int numCompanies = file.readInt();
            List<String> registerFinancingCompanies = new ArrayList<>();
            for (int i = 0; i < numCompanies; i++) {
                registerFinancingCompanies.add(file.readUTF());
            }
            Movie = new Movie(registerId, registerName, registerDate, registerBudget, registerBoxOffice, registerFinancingCompanies, registerGenre);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Movie;
    }

    /**
     * Insere um novo Moviee no arquivo sequencial.
     * @param Movie Objeto Movie a ser inserido.
     * @return true se a inserção for bem-sucedida, false caso contrário.
     */
    public static boolean Insert(Movie Movie) {
        boolean response = false;
        try (RandomAccessFile file = new RandomAccessFile(FILE_NAME, "rw")) {
            int objectId;
            if (file.length() != 0) {
                file.seek(0);
                int lastId = file.readInt();
                objectId = lastId + 1;
            } else {
                objectId = 1;
            }
            Movie.setId(objectId);
            file.seek(0);
            file.writeInt(Movie.getId());
            file.seek(file.length());
            WriteMovie(file, Movie);
            numberOfMovies++;
            response = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    /**
     * Recupera um Moviee do arquivo sequencial pelo ID.
     * @param id Identificador do Moviee.
     * @return Objeto Movie correspondente ou null se não encontrado.
     */
    public static Movie Get(int id) {
        Movie movie = null;
        boolean find = false;
        try (RandomAccessFile file = new RandomAccessFile(FILE_NAME, "r")) {
            file.seek(4); // Pula o último ID salvo
            while (file.getFilePointer() < file.length() && !find) {
                Byte flag = file.readByte();
                int registerLength = file.readInt();
                long pos = file.getFilePointer();
                if (flag == '*') {
                    file.seek(pos + registerLength);
                } else {
                    Movie recordMovie = ReadMovie(file);
                    if (recordMovie.getId() == id) {
                        movie = recordMovie;
                        find = true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return movie;
    }

    /**
     * Atualiza um Moviee existente no arquivo sequencial.
     * @param newMovie Novo objeto Movie atualizado.
     * @return true se a atualização for bem-sucedida, false caso contrário.
     */
    public static boolean Update(Movie newMovie) {
        boolean response = false;
        try (RandomAccessFile file = new RandomAccessFile(FILE_NAME, "rw")) {
            file.seek(4);
            while (file.getFilePointer() < file.length() && !response) {
                long pos = file.getFilePointer();
                byte flag = file.readByte();
                int registerSize = file.readInt();
                if (flag != '*') {
                    int id = file.readInt();
                    if (id == newMovie.getId()) {
                        int newSize = newMovie.registerByteSize();
                        if (newSize <= registerSize) {
                            file.seek(pos);
                            WriteMovie(file, newMovie);
                        } else {
                            file.seek(pos);
                            file.writeByte('*'); // Marcar como excluído
                            file.seek(file.length());
                            WriteMovie(file, newMovie);
                        }
                        response = true;
                    } else {
                        file.seek(pos + 5 + registerSize); //register size does not count flag byte and registter size bytes(+5)
                    }
                } else {
                    file.seek(pos + 5 + registerSize); //register size does not count flag byte and registter size bytes(+5)
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    /**
     * Exclui um Moviee do arquivo sequencial pelo ID.
     * @param id Identificador do Moviee a ser excluído.
     * @return true se a exclusão for bem-sucedida, false caso contrário.
     */
    public static boolean Delete(int id) {
        boolean response = false;
        try (RandomAccessFile file = new RandomAccessFile(FILE_NAME, "rw")) {
            file.seek(4);
            while (file.getFilePointer() < file.length() && !response) {
                long position = file.getFilePointer();
                byte flag = file.readByte();
                int registerSize = file.readInt();
                if (flag == 0) {
                    int readID = file.readInt();
                    if (readID == id) {
                        file.seek(position);
                        file.writeByte('*');
                        numberOfMovies--;
                        response = true;
                    } else {
                        file.seek(position + 1 + 4 + registerSize);
                    }
                } else {
                    file.seek(position + 1 + 4 + registerSize);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }
}
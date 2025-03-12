
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class SequentialFile{
    private static String FILE_NAME = "SequentialFile.dat";
    public static boolean Insert(Film film){
        boolean response = false;
        try (RandomAccessFile file = new RandomAccessFile(FILE_NAME, "rw")) {
            int objectId;
            if(file.length() != 0){
                file.seek(0);
                int lastId = file.readInt();
                objectId = lastId + 1;
                film.setId(objectId);
            }
            else{
                objectId = 1;
            }
            file.seek(0);
            file.writeInt(objectId);
            file.seek(file.length());
            file.writeByte(0);//flag
            file.writeInt(film.registerByteSize());
            file.writeInt(film.getId());
            file.writeUTF(film.getName());
            file.writeLong(film.getDate());
            file.writeInt(film.getBudget());
            file.writeFloat(film.getBoxOffice());
            //write fixed-size string
            String genre = film.getGenre();
            for (int i = 0; i < 10; i++) {
                file.writeByte(i < genre.length() ? genre.charAt(i) : ' ');
            }
            file.writeInt(film.getFinancingCompanies().size());
            for (String company : film.getFinancingCompanies()) {
                file.writeUTF(company);
            }
            response = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    public static Film Get(int id){
        Film film = null;
        boolean find = false;
        try(RandomAccessFile file = new RandomAccessFile(FILE_NAME, "r")){
            file.seek(4);//jumping over the last id
            while(file.getFilePointer() < file.length() && !find){
                Byte flag = file.readByte();
                int registerLength = file.readInt(); // Lê o tamanho do registro
                long pos = file.getFilePointer();
                if(flag == '*'){
                    file.seek(pos + registerLength);
                }
                else{ 
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
                    for(String s : registerFinancingCompanies){
                    }
                    if(registerId == id){
                        film = new Film(registerId, registerName, registerDate, registerBudget, registerBoxOffice, registerFinancingCompanies, registerGenre);
                        find = true;
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return film;
    }

    public static boolean Update(Film newFilm) {
        boolean response = false;
        try (RandomAccessFile file = new RandomAccessFile(FILE_NAME, "rw")) {
            file.seek(4);
            while (file.getFilePointer() < file.length() && !response) {
                byte flag = file.readByte();
                int registerSize = file.readInt();
                long pos = file.getFilePointer();
                if (flag != '*') {
                    int id = file.readInt();
                    if (id == newFilm.getId()) {
                        int newSize = newFilm.registerByteSize();
                        if (newSize <= registerSize) {
                            file.seek(pos);
                            file.writeByte(0); // flag
                            file.writeInt(newSize);
                            file.writeInt(newFilm.getId());
                            file.writeUTF(newFilm.getName());
                            file.writeLong(newFilm.getDate());
                            file.writeInt(newFilm.getBudget());
                            file.writeFloat(newFilm.getBoxOffice());
    
                            String gender = newFilm.getGenre();
                            for (int i = 0; i < 10; i++) {
                                file.writeByte(i < gender.length() ? gender.charAt(i) : ' ');
                            }
    
                            file.writeInt(newFilm.getFinancingCompanies().size());
                            for (String company : newFilm.getFinancingCompanies()) {
                                file.writeUTF(company);
                            }
                        } else {
                            file.seek(pos);
                            file.writeByte('*'); // Marcar como excluído
                            file.seek(file.length());
                            file.writeByte(0);
                            file.writeInt(newSize);
                            file.writeInt(newFilm.getId());
                            file.writeUTF(newFilm.getName());
                            file.writeLong(newFilm.getDate());
                            file.writeInt(newFilm.getBudget());
                            file.writeFloat(newFilm.getBoxOffice());
    
                            String gender = newFilm.getGenre();
                            for (int i = 0; i < 10; i++) {
                                file.writeByte(i < gender.length() ? gender.charAt(i) : ' ');
                            }
    
                            file.writeInt(newFilm.getFinancingCompanies().size());
                            for (String company : newFilm.getFinancingCompanies()) {
                                file.writeUTF(company);
                            }
                        }
                        response = true;
                    } else {
                        file.seek(pos + registerSize);
                    }
                } else {
                    file.seek(pos + registerSize);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }
    
    
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

    public static void Sort(int b, int m){
        try (RandomAccessFile file = new RandomAccessFile(FILE_NAME, "r")) {
            int quantityRecords = 0;
            int quantityPaths = 0;
            file.seek(4); //jumping lastId record
            while(quantityPaths < m){
                Film blockFilm[] = new Film[b];
                while(quantityRecords < b){
                    Byte flag = file.readByte();
                    int registerLength = file.readInt(); 
                    long pos = file.getFilePointer();
                    if(flag == '*'){
                        file.seek(pos + registerLength);
                    }
                    else{ 
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
                        for(String s : registerFinancingCompanies){
                        }
                        Film film = new Film(registerId, registerName, registerDate, registerBudget, registerBoxOffice, registerFinancingCompanies, registerGenre);
                        blockFilm[quantityRecords] = film;
                        quantityRecords++;
                    }
                }
                quantityPaths++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

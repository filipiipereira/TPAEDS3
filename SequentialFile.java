
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class SequentialFile{
    private String name;
    public SequentialFile(){
        this.name = "SequentialFile.dat";
    }
    public boolean Insert(Film film){
        boolean response = false;
        try (RandomAccessFile file = new RandomAccessFile(this.name, "rw")) {
            int objectId;
            if(file.length() != 0){
                file.seek(0);
                int lastId = file.readInt();
                objectId = lastId + 1;
                film.setId(lastId);
            }
            else{
                objectId = 1;
            }
            file.seek(0);
            file.writeInt(objectId);
            file.seek(file.length());
            file.writeByte(0);//flag
            file.writeInt(film.byteSize());
            file.writeInt(film.getId());
            file.writeUTF(film.getName());
            file.writeLong(film.getDate());
            file.writeInt(film.getBudget());
            file.writeFloat(film.getBoxOffice());
            //write fixed-size string
            String fixedString = film.getGenre();
            int fixedStringSize = fixedString.length();
            if(fixedStringSize <= 10){
                file.writeBytes(fixedString);
                for (int i = fixedStringSize; i < 10; i++) {
                    file.writeByte(' ');
                }
            }
            else{
                for (int i = 0; i < 10; i++) {
                    file.writeByte(fixedString.charAt(i));
                }
            }
            file.writeInt(film.getFinancingCompanies().size());
            for(int i = 0; i < film.getFinancingCompanies().size(); i++){
                file.writeUTF(film.getFinancingCompanies().get(i));
            }
            response = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }
    public Film get(int id){
        Film film = null;
        boolean find = false;
        try(RandomAccessFile file = new RandomAccessFile(this.name, "r")){
            file.seek(4);//jumping over the last id
            while(file.getFilePointer() < file.length() && !find){
                if(file.readByte() != '*'){
                    int registerLength = file.readInt(); 
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
}
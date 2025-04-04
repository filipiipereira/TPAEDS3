

public class Btree{
    private static final String INDEXFILE_NAME = "IndexFile.dat";
    private Page root;
    private int order;
    public Btree(int order){
        this.order = order;
        this.root = null;
    }
    public long Find(int index){
        long position = -1;
        boolean find = false;
        Page page = root;
        int i = 0;
        while(page != null && !find){
            if(i == page.GetNumberOfIndexes()){
                page = page.GetPages()[i];
                i = 0;
            }
            if(index == page.GetIndexes()[i])find = true;
            else if(index < page.GetIndexes()[i]){
                page = page.GetPages()[i];
                i = 0;
            }
            else if(i < order-1) i++;
            else{
                page = page.GetPages()[i+1];
                i = 0;
            }
        }
        return position; //if !find position = -1
    }
    public void Insert(int index, long position){
        boolean inserted = false;
        if(root.GetNumberOfIndexes() == 0){
            root.SetIndexes(0, index);
            root.SetPositions(0, position);
            root.SetNumberOfIndexes(root.GetNumberOfIndexes()+1);
            return;
        }
        Page page = root;
        int i = 0;
        while(!page.isLeaf()){
            if(index < page.GetIndexes()[i]){
                page = page.GetPages()[i];
                i = 0;
            }
            else if(i < order-1) i++;
            else{
                page = page.GetPages()[i+1];
                i = 0;
            }
        }

    }
}
class Page{
    private int order;
    private int[] indexes;
    public long[] positions;
    private Page[] pages;
    private int numberOfIndexes;
    public Page(int order){
        this.order = order;
        //lembrar de inicializar os elementos e pages
        indexes = new int[order-1];
        positions = new long[order-1];
        pages = new Page[order];
        numberOfIndexes = 0;
    }
    public void SetIndexes(int arrayI, int index){
        this.indexes[arrayI] = index;
    }
    public void SetPositions(int arrayI, long position){
        this.positions[arrayI] = position;
    }
    public int[] GetIndexes(){
        return indexes;
    }
    public Page[] GetPages(){
        return pages;
    }
    public void SetNumberOfIndexes(int numberOfIndexes){
        this.numberOfIndexes = numberOfIndexes;
    }
    public int GetNumberOfIndexes(){
        return numberOfIndexes;
    }
    public boolean isLeaf(){
        boolean isLeaf = true;
        for(Page p : this.GetPages()){
            if(p != null){
                isLeaf = false;
                break;
            }
        }
        return isLeaf;
    }
}
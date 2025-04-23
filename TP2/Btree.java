
import javax.swing.RootPaneContainer;




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
            if(i == page.currentNumberOfIndexes){
                page = page.pages[i];
                i = 0;
            }
            if(index == page.indexes[i])find = true;
            else if(index < page.indexes[i]){
                page = page.pages[i];
                i = 0;
            }
            else if(i < page.numberOfIndexes) i++;
            else{
                page = page.pages[i+1];
                i = 0;
            }
        }
        return position; //if !find position = -1
    }
    public void Insert(int index, long position){
        Pair pair = new Pair(index, position);
        boolean inserted = false;
        if(root.currentNumberOfIndexes == 0){
            root = new Page(order);
            root.indexes[0] = pair.index;
            root.positions[0] = pair.position;
            root.currentNumberOfIndexes = 1;
            return;
        }
        Pair climbingIndex = InsertRecursive(root, pair);
        if(climbingIndex.index != -1){
            Page newPage = new Page(order);
            newPage.indexes[0] = climbingIndex.index;
            newPage.positions[0] = climbingIndex.position;
            newPage.currentNumberOfIndexes++;
            Page childOne = new Page(order);
            for(int i = 0; i < root.numberOfIndexes/2; i++){
                childOne.indexes[i] = root.indexes[i];
                childOne.positions[i] = root.indexes[i];
                childOne.pages[i] = root.pages[i]; 
            }
            Page
            for(int i = root.numberOfIndexes/2; i < root.numberOfIndexes; i++){

            }
            root = newPage;
        }
    }
    public Split InsertRecursive(Page page, Pair pair){
        Split split = new Split();
        if(!page.isLeaf()){
            int i = 0;
            Page childPage = new Page(order);
            if(pair.index < page.indexes[i]){
                childPage = page.pages[i];
            }
            else if(i < order-1) i++;
            else{
                childPage = page.pages[i+1];
            }
            split = InsertRecursive(childPage, pair);
            //after this its the returning part
            if(split.climbingIndex.index != -1){
                if(page.currentNumberOfIndexes < (page.numberOfIndexes)){//recebe split e inseri
                    for(int j = page.numberOfIndexes; j > i; j--){
                        page.indexes[j] = page.indexes[j-1];
                    }
                    page.indexes[i] = split.climbingIndex.index;
                    page.positions[i] = split.climbingIndex.position;
                    page.currentNumberOfIndexes++;
                    page.pages[i] = split.pageOne;
                    page.pages[i+1] = split.pageTwo;
                    split.climbingIndex.index = -1;//inseriu
                }
                else{//recebe split e split
                    Split currentSplit = split;
                    split.pageOne = new Page(order);
                    split.pageTwo = new Page(order);
                    if(i == page.numberOfIndexes/2){
                        for(int j = 0; j < page.numberOfIndexes/2; j++){
                            split.pageOne.pages[i] = page.pages[i];
                            split.pageTwo.pages[i] = page.pages[i + page.numberOfIndexes/2];
                        }
                        split.pageOne.pages[page.numberOfIndexes/2+1] = currentSplit.pageOne;
                        split.pageTwo.pages[page.numberOfIndexes/2+1] = currentSplit.pageTwo;
                    }
                    if(i < page.numberOfIndexes/2){
                        split.climbingIndex.index = page.indexes[page.numberOfIndexes/2 - 1];
                        split.climbingIndex.position = page.positions[page.numberOfIndexes/2 - 1];
                        for(int j = (page.numberOfIndexes/2-1); j > i; j--){
                            page.indexes[j] = page.indexes[j-1];
                        }
                        page.indexes[i] = currentSplit.climbingIndex.index;
                        page.positions[i] = currentSplit.climbingIndex.position;
                    }

                    //acabar de preparar o proximo split
                    else if(i > page.numberOfIndexes/2){
                        split.climbingIndex.index = page.indexes[page.numberOfIndexes/2];
                        split.climbingIndex.position = page.positions[page.numberOfIndexes/2];
                        for(int j = page.numberOfIndexes/2; j < i; i++){
                            page.indexes[j] = page.indexes[j+1];
                        }
                        page.indexes[i-1] = currentSplit.climbingIndex.index;
                        page.positions[i-1] = currentSplit.climbingIndex.position;

                    }   
                    for(int j = 0; j < page.numberOfIndexes/2; j++){
                        split.pageOne.indexes[i] = page.indexes[i];
                        split.pageOne.positions[i] = page.positions[i];
                        split.pageTwo.indexes[i] = page.indexes[i + page.numberOfIndexes/2];
                        split.pageTwo.positions[i] = page.positions[i + page.numberOfIndexes/2];
                    }
                }
            }
        }
        else if(page.currentNumberOfIndexes < (page.numberOfIndexes)){ //inserir na folha
            int i = 0;
            while(i < page.currentNumberOfIndexes && pair.index > page.indexes[i]) i++;
            for(int j = page.numberOfIndexes; j > i; j--){
                page.indexes[j] = page.indexes[j-1];
            }
            page.indexes[i] = pair.index;
            page.positions[i] = pair.position;
            page.currentNumberOfIndexes++;
            split.climbingIndex.index = -1;//inseriu
        }
        else{ //sobe index e split folha
            int i = 0;
            while(i < page.currentNumberOfIndexes && pair.index > page.indexes[i]) i++;
            if(i == page.numberOfIndexes/2){
                split.climbingIndex.index = pair.index;
                split.climbingIndex.position = pair.position;
            }
            else if(i < page.numberOfIndexes/2){
                split.climbingIndex.index = page.indexes[page.numberOfIndexes/2 - 1];
                split.climbingIndex.position = page.positions[page.numberOfIndexes/2 - 1];
                for(int j = (page.numberOfIndexes/2-1); j > i; j--){
                    page.indexes[j] = page.indexes[j-1];
                }
                page.indexes[i] = pair.index;
                page.positions[i] = pair.position;
            }
            else{
                split.climbingIndex.index = page.indexes[page.numberOfIndexes/2];
                split.climbingIndex.position = page.positions[page.numberOfIndexes/2];
                for(int j = page.numberOfIndexes/2; j < i; i++){
                    page.indexes[j] = page.indexes[j+1];
                }
                page.indexes[i-1] = pair.index;
                page.positions[i-1] = pair.position;
            }
            split.pageOne = new Page(order);
            for(int j = 0; j < page.numberOfIndexes/2; j++){
                split.pageOne.indexes[i] = page.indexes[i];
                split.pageOne.positions[i] = page.positions[i];
            }
            split.pageTwo = new Page(order);
            for(int j = 0; j < page.numberOfIndexes/2; j++){
                split.pageOne.indexes[i] = page.indexes[i + page.numberOfIndexes/2];
                split.pageOne.positions[i] = page.positions[i + page.numberOfIndexes/2];
            }
        }
        return split;
    }

    class Page{
        private int numberOfIndexes;
        private int numberOfPages;
        private int[] indexes;
        public long[] positions;
        private Page[] pages;
        private int currentNumberOfIndexes;
        public Page(int order){
            this.numberOfIndexes = order-1;
            this.numberOfPages = order;
            //lembrar de inicializar os elementos e pages
            indexes = new int[numberOfIndexes];
            positions = new long[numberOfIndexes];
            pages = new Page[numberOfPages];
            currentNumberOfIndexes = 0;
        }
        public boolean isLeaf(){
            boolean isLeaf = true;
            for(Page p : this.pages){
                if(p != null){
                    isLeaf = false;
                    break;
                }
            }
            return isLeaf;
        }
    }
    class Pair{
        private int index;
        private long position;
        public Pair(int index, long position){
            this.index = index;
            this.position = position;
        }
        public Pair(){
            this.index = -1;
            this.position = -1;
        }
    }
    class Split{
        private Page pageOne;
        private Page pageTwo;
        private Pair climbingIndex;
        public Split(){
            pageOne = null;
            pageTwo = null;
            climbingIndex = new Pair();
        }
    }
}

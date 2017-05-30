package data;

/**
 * Created by matanghao1 on 29/5/17.
 */
public class Author {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    String name;

    public Author(String name) {
        this.name = name;
    }
}

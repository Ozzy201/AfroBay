package reader.softech.com.zcommerce.Model;

public class Comments {

    private String comments;
    private String publisher;


    public Comments(String comments, String publisher) {
        this.comments = comments;
        this.publisher = publisher;
    }

    public Comments() {
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
}

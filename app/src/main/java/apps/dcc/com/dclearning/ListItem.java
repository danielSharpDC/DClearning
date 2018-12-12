package apps.dcc.com.dclearning;

public class ListItem {
    private int color;
    private String pseudo;
    private String text;
    private String pdfFile;

    public ListItem(int color, String pseudo, String text, String pdfFile) {
        this.color = color;
        this.pseudo = pseudo;
        this.text = text;
        this.pdfFile = pdfFile;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getPseudo() {
        return pseudo;
    }
    public String getPdfFile() {
        return pdfFile;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
    public void setPdfFile(String pdfFile) {
        this.pdfFile = pdfFile;
    }
}
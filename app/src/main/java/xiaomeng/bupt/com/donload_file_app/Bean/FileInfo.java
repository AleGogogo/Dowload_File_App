package xiaomeng.bupt.com.donload_file_app.Bean;

/**
 * Created by rain on 2016/8/30.
 */
public class FileInfo {
    private int id;
    private String name;
    private String url;
    private int start;
    private int end;
    private int finished;

    public FileInfo(){}

    public FileInfo(int id, String name, String url, int start, int end, int finished) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.start = start;
        this.end = end;
        this.finished = finished;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public int getFinished() {
        return finished;
    }

    public void setFinished(int finished) {
        this.finished = finished;
    }
}

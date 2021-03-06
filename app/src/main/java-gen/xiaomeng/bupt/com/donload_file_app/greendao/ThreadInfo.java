package xiaomeng.bupt.com.donload_file_app.greendao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table THREAD_INFO.
 */
public class ThreadInfo {

    private Long id;
    private Integer start;
    private Integer end;
    private Integer finished;
    private String url;

    public ThreadInfo() {
    }

    public ThreadInfo(Long id) {
        this.id = id;
    }

    public ThreadInfo(Long id, Integer start, Integer end, Integer finished, String url) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.finished = finished;
        this.url = url;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getStart() {
        return start;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

    public Integer getEnd() {
        return end;
    }

    public void setEnd(Integer end) {
        this.end = end;
    }

    public Integer getFinished() {
        return finished;
    }

    public void setFinished(Integer finished) {
        this.finished = finished;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}

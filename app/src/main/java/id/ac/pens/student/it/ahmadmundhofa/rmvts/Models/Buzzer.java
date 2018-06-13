package id.ac.pens.student.it.ahmadmundhofa.rmvts.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Buzzer {
    @SerializedName("created")
    @Expose
    private String created;
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("detail")
    @Expose
    private String detail;

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}

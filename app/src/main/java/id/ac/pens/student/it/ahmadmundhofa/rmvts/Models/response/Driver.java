package id.ac.pens.student.it.ahmadmundhofa.rmvts.Models.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Driver {
    @SerializedName("images")
    @Expose
    private String images;
    @SerializedName("link_images")
    @Expose
    private String linkImages;
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("created")
    @Expose
    private String created;

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public String getLinkImages() {
        return linkImages;
    }

    public void setLinkImages(String linkImages) {
        this.linkImages = linkImages;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }
}

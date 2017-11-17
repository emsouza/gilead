package net.sf.gilead.test.domain.misc;

import java.util.ArrayList;
import java.util.List;

import net.sf.gilead.pojo.java5.LightEntity;

/**
 * Page Domain class. Used to test list ordering
 */
public class Page extends LightEntity {

    private static final long serialVersionUID = 1058354709157710766L;

    private Integer id;
    private Integer version;

    private String name;

    private List<Photo> photoList;

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the photoList
     */
    public List<Photo> getPhotoList() {
        return photoList;
    }

    /**
     * @param photoList the photo list to set
     */
    public void setPhotoList(List<Photo> photoList) {
        this.photoList = photoList;
    }

    public void addPhoto(Photo photo) {
        photo.setPage(this);

        // Create message list if needed
        if (photoList == null) {
            photoList = new ArrayList<>();
        }
        photoList.add(photo);
    }

    public void removePhoto(Photo photo) {
        photoList.remove(photo);
    }
}

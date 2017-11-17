package net.sf.gilead.test.domain.misc;

import net.sf.gilead.pojo.java5.LightEntity;

/**
 * Photo Domain class. Used for testing list order
 * 
 * @author bruno.marchesson
 */
public class Photo extends LightEntity {

    private static final long serialVersionUID = -1067096371173906324L;

    private Integer id;
    private Integer version;

    private String url;

    private Page page;

    public final Integer getId() {
        return id;
    }

    public final void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return the version
     */
    public Integer getVersion() {
        return version;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return the pageIndex
     */
    public int getPageIndex() {
        return this.getPage().getPhotoList().indexOf(this);
    }

    /**
     * @param pageIndex the pageIndex to set
     */
    public void setPageIndex(int pageIndex) {}

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }
}
